package at.kaindorf.chess.core.controller;

import at.kaindorf.chess.core.ai.ChessAi;
import at.kaindorf.chess.core.board.ChessBoard;
import at.kaindorf.chess.core.pojos.GameAlreadyEndedException;
import at.kaindorf.chess.userManagement.database.UserMockDatabase;
import at.kaindorf.chess.core.pojos.ChessReturn;
import at.kaindorf.chess.userManagement.pojos.Player;
import at.kaindorf.chess.userManagement.pojos.User;
import at.kaindorf.chess.core.pojos.moves.Move;
import at.kaindorf.chess.core.pojos.moves.PromotionMove;
import at.kaindorf.chess.core.pojos.piece.PieceType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
/**
 * project name: Schach_4A
 * author: Roman Lanschützer
 * date: 21.05.2023
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class AngularChessController {
    private Map<List<Player>, ChessBoard> games = new HashMap<>();
    private List<Player> waitingList = new CopyOnWriteArrayList<>();
    private List<Player> allPlayersList = new ArrayList<>();

    @RequestMapping(value = "/chess/angular/login", method = RequestMethod.POST)
    public Player login(@RequestBody String idStr) {
        int id = Integer.parseInt(idStr);
        int nextId = 1;
        if (!allPlayersList.isEmpty()) {
            nextId = allPlayersList.stream().mapToInt(Player::getPlayerId).max().getAsInt() + 1;
        }
        // von Manuel
        List<User> userList = UserMockDatabase.getInstance().getUserList();
        User user = userList.stream().filter(u -> u.getUserId() == id).findFirst().get();
        // von Manuel ende
        Player player = new Player(nextId, user);
        allPlayersList.add(player);
        return player;
    }

    @RequestMapping(value = "/chess/angular/start", method = RequestMethod.GET)
    public synchronized List<Move> startGame(@RequestParam boolean isSinglePlayer, @RequestParam int playerId) {
        Player player = allPlayersList.stream().filter(p -> p.getPlayerId() == playerId).findFirst().get();
        if (isSinglePlayer) {
            List<Player> players = new ArrayList<>();
            players.add(player);
            ChessBoard chessBoard = new ChessBoard();
            List<Move> validMoves = chessBoard.getAllValidMoves(true);
            games.put(players, chessBoard);
            return validMoves;
        } else {
            waitingList.add(player);
            if (waitingList.size() >= 2) {
                List<Player> newMatch = waitingList.subList(0, 2);
                List<Player> syncMatch = new CopyOnWriteArrayList<>();
                syncMatch.add(newMatch.get(0));
                syncMatch.add(newMatch.get(1));
                ChessBoard board = new ChessBoard();
                games.put(syncMatch, board);
                waitingList.removeAll(newMatch);
            }
        }
        return null;
    }

    @RequestMapping(value = "/chess/angular/wait", method = RequestMethod.GET)
    public synchronized ChessReturn waitingForPlayers(@RequestParam int playerId) {
        Player player = allPlayersList.stream().filter(p -> p.getPlayerId() == playerId).findFirst().get();
        Optional<List<Player>> optionalPlayers = games.keySet().stream().filter(l -> l.contains(player)).findFirst();

        if (optionalPlayers.isPresent()) {
            List<Player> players = optionalPlayers.get();
            int idx = players.indexOf(player);
            ChessBoard board = games.get(players);
            try {
                Player opponent = (idx == 0) ? players.get(1) : players.get(0);
                ChessReturn chessReturn = new ChessReturn(board.generateFenString(), board.getAllValidMoves(true),
                        new Move(1, 1), (idx == 0) ? true : false, board.isWhiteTurn(), opponent.getUser(), board.getGameStatus());
                return chessReturn;
            } catch (GameAlreadyEndedException ex) {
                return new ChessReturn(board.getGameStatus());
            }
        }

        return null;
    }

    // Endpoint for the waiting-client (not active player) to check if there is a new move
    @RequestMapping(value = "/chess/angular/multi/checkBoard", method = RequestMethod.GET)
    public synchronized ChessReturn checkIfBoardChanged(@RequestParam boolean turn, @RequestParam int playerId) {
        Player player = allPlayersList.stream().filter(p -> p.getPlayerId() == playerId).findFirst().get();
        Optional<List<Player>> optionalPlayers = games.keySet().stream().filter(l -> l.contains(player)).findFirst();
        if (optionalPlayers.isPresent()) {
            List<Player> players = optionalPlayers.get();
            int idx = players.indexOf(player);
            Player opponent = (idx == 0) ? players.get(1) : players.get(0);
            ChessBoard board = games.get(players);
            try {
                if (turn != board.isWhiteTurn()) {
                    ChessReturn chessReturn = new ChessReturn(board.generateFenString(), board.getAllValidMoves(true),
                            board.getLastMove(), (idx == 0) ? true : false, board.isWhiteTurn(), opponent.getUser(), board.getGameStatus());
                    return chessReturn;
                }
            } catch (GameAlreadyEndedException ex) {
                return new ChessReturn(board.getGameStatus());
            }

        }
        return null;
    }

    @RequestMapping(value = "/chess/angular/move/single", method = RequestMethod.GET)
    public synchronized ChessReturn getAllValidMovesSingleAngular(@RequestParam int startPos, @RequestParam int targetPos, @RequestParam int playerId) {
        Player player = allPlayersList.stream().filter(p -> p.getPlayerId() == playerId).findFirst().get();
        Optional<List<Player>> optionalPlayers = games.keySet().stream().filter(l -> l.contains(player)).findFirst();
        if (optionalPlayers.isPresent()) {
            List<Player> players = optionalPlayers.get();
            ChessBoard board = games.get(players);
            try {
                board.makeMove(new Move(startPos, targetPos), true);
                board.changeTurn();
                Move aiMove = ChessAi.calculateBestMove(new ChessBoard(board, false));
                board.makeMove(aiMove, true);
                board.changeTurn();
                return new ChessReturn(board.generateFenString(), board.getAllValidMoves(true), aiMove, true, board.isWhiteTurn(), null, board.getGameStatus());
            } catch (GameAlreadyEndedException ex) {
                return new ChessReturn(board.getGameStatus());
            }

        }
        return null;
    }

    // Endpoint for playing-client (active player) to make a valid move (move is 100% valid)
    @RequestMapping(value = "/chess/angular/move/multi", method = RequestMethod.GET)
    public synchronized ResponseEntity<ChessReturn> makeValidMoveMultiAngular(@RequestParam int startPos, @RequestParam int targetPos, @RequestParam(required = false) PieceType desiredPiece, @RequestParam int playerId) {
        Player player = allPlayersList.stream().filter(p -> p.getPlayerId() == playerId).findFirst().get();
        Optional<List<Player>> optionalPlayers = games.keySet().stream().filter(l -> l.contains(player)).findFirst();
        if (optionalPlayers.isPresent()) {
            List<Player> twoPlayerLobby = optionalPlayers.get();
            ChessBoard board = games.get(twoPlayerLobby);
            try {
                boolean isSendingPlayerWhite = twoPlayerLobby.indexOf(player) == 0;

                List<Move> validMoves = board.getAllValidMoves(true);
                Move move = validMoves.stream().filter(m -> m.getStartPos() == startPos && m.getTargetPos() == targetPos).findFirst().get();
                // von Maxi
                if (move instanceof PromotionMove && desiredPiece != null) {
                    ((PromotionMove) move).setDesiredType(desiredPiece);
                }
                // von Maxi ende
                board.makeMove(move, true);
                board.changeTurn();
                System.out.println("STATUS: " + board.getGameStatus());
                ChessReturn chessReturn = new ChessReturn(board.generateFenString(), board.getAllValidMoves(true),
                        move, isSendingPlayerWhite, board.isWhiteTurn(), null, board.getGameStatus());
                return ResponseEntity.ok(chessReturn);
            } catch (GameAlreadyEndedException ex) {
                return ResponseEntity.ok(new ChessReturn(board.getGameStatus()));
            }

        }
        return null;
    }
}
