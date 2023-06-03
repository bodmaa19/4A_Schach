package at.kaindorf.chess.core.controller;

import at.kaindorf.chess.core.ai.ChessAi;
import at.kaindorf.chess.core.board.ChessBoard;
import at.kaindorf.chess.core.pojos.ChessReturn;
import at.kaindorf.chess.core.pojos.moves.Move;
import at.kaindorf.chess.core.pojos.piece.PieceType;
import at.kaindorf.chess.userManagement.pojos.Player;
import at.kaindorf.chess.core.pojos.moves.PromotionMove;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ChessController {
    private Map<List<Player>, ChessBoard> games = new HashMap<>();
    private List<Player> waitingList = new CopyOnWriteArrayList<>();
    private List<Player> allPlayersList = new ArrayList<>();

    @RequestMapping(value = "/chess/loginTest", method = RequestMethod.POST)
    public Player loginTest(@RequestBody String name) {
        int nextId = 1;
        if (!allPlayersList.isEmpty()) {
            nextId = allPlayersList.stream().mapToInt(Player::getPlayerId).max().getAsInt() + 1;
        }
        Player player = new Player(nextId, null);
        allPlayersList.add(player);
        return player;
    }


    // Game is created (and started on the server) and users are removed from the queue
    @RequestMapping(value = "/chess/start", method = RequestMethod.POST)
    public synchronized List<Move> startGame(@RequestParam boolean isSinglePlayer, @RequestBody Player player) {
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

    // Endpoint for the clients to check if the game has started on the server
    @RequestMapping(value = "/chess/wait", method = RequestMethod.POST)
    public synchronized ChessReturn waitingForPlayers(@RequestBody Player player) {
        Optional<List<Player>> optionalPlayers = games.keySet().stream().filter(l -> l.contains(player)).findFirst();

        if (optionalPlayers.isPresent()) {
            List<Player> players = optionalPlayers.get();
            int idx = players.indexOf(player);
            ChessBoard board = games.get(players);
            Player opponent = (idx == 0) ? players.get(1) : players.get(0);
            ChessReturn chessReturn = new ChessReturn(board.generateFenString(), board.getAllValidMoves(true),
                    new Move(1, 1), (idx == 0) ? true : false, board.isWhiteTurn(), opponent.getUser());
            return chessReturn;
        }

        return null;
    }



    // Endpoint for the waiting-client (not active player) to check if there is a new move
    @RequestMapping(value = "/chess/multi/checkBoard", method = RequestMethod.POST)
    public synchronized ChessReturn checkIfBoardChanged(@RequestParam boolean turn, @RequestBody Player player) {
        Optional<List<Player>> optionalPlayers = games.keySet().stream().filter(l -> l.contains(player)).findFirst();
        if (optionalPlayers.isPresent()) {
            List<Player> players = optionalPlayers.get();
            int idx = players.indexOf(player);
            Player opponent = (idx == 0) ? players.get(1) : players.get(0);
            ChessBoard board = games.get(players);
            if (turn != board.isWhiteTurn()) {
                ChessReturn chessReturn = new ChessReturn(board.generateFenString(), board.getAllValidMoves(true),
                        board.getLastMove(), (idx == 0) ? true : false, board.isWhiteTurn(), opponent.getUser());
                return chessReturn;
            }
        }

        return null;
    }

    // Endpoint for playing-client (active player) to make a valid move (move is 100% valid)
    @RequestMapping(value = "/chess/move/multi", method = RequestMethod.POST)
    public synchronized ResponseEntity<ChessReturn> makeValidMoveMulti(@RequestParam int startPos, @RequestParam int targetPos, @RequestParam(required = false) PieceType desiredPiece, @RequestBody List<Player> players) {
        Optional<List<Player>> optionalPlayers = games.keySet().stream().filter(l -> l.contains(players.get(0))).findFirst();
        if (optionalPlayers.isPresent()) {
            List<Player> twoPlayerLobby = optionalPlayers.get();
            ChessBoard board = games.get(twoPlayerLobby);
            boolean isSendingPlayerWhite = twoPlayerLobby.indexOf(players.get(0)) == 0;

            List<Move> validMoves = board.getAllValidMoves(true);
            Move move = validMoves.stream().filter(m -> m.getStartPos() == startPos && m.getTargetPos() == targetPos).findFirst().get();
            // von Maxi
            if (move instanceof PromotionMove && desiredPiece != null) {
                ((PromotionMove) move).setDesiredType(desiredPiece);
            }
            // von Maxi ende
            board.makeMove(move, true);
            board.changeTurn();
            ChessReturn chessReturn = new ChessReturn(board.generateFenString(), board.getAllValidMoves(true),
                    move, isSendingPlayerWhite, board.isWhiteTurn(), null);
            return ResponseEntity.ok(chessReturn);
        }
        return null;
    }

    @RequestMapping(value = "/chess/move/single", method = RequestMethod.POST)
    public synchronized ChessReturn getAllValidMovesSingle(@RequestParam int startPos, @RequestParam int targetPos, @RequestBody List<Player> players) {
        ChessBoard board = games.get(players);
        board.makeMove(new Move(startPos, targetPos), true);
        board.changeTurn();
        Move aiMove = ChessAi.calculateBestMove(new ChessBoard(board, false));
        board.makeMove(aiMove, true);
        board.changeTurn();

        return new ChessReturn(board.generateFenString(), board.getAllValidMoves(true), aiMove, true, board.isWhiteTurn(), null);
    }
}