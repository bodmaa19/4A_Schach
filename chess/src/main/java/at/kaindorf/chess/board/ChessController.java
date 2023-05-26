package at.kaindorf.chess.board;

import at.kaindorf.chess.ai.ChessAi;
import at.kaindorf.chess.pojos.ChessInput;
import at.kaindorf.chess.pojos.ChessReturn;
import at.kaindorf.chess.pojos.Move;
import at.kaindorf.chess.pojos.Player;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ChessController {
    private Map<List<Player>, ChessBoard> games = new HashMap<>();
    private List<Player> waitingList = new CopyOnWriteArrayList<>();
    private List<Player> playerList = new ArrayList<>();

    @RequestMapping(value = "/chess/login", method = RequestMethod.POST)
    public Player login(@RequestBody String name) {
        int nextId = 1;
        if (!playerList.isEmpty()) {
            nextId = playerList.stream().mapToInt(Player::getPlayerId).max().getAsInt() + 1;
        }

        Player player = new Player(nextId, name);
        playerList.add(player);
        return player;
    }

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

    @RequestMapping(value = "/chess/multi/checkBoard", method = RequestMethod.POST)
    public synchronized ChessReturn checkBoard(@RequestParam boolean turn, @RequestBody Player player) {
        Optional<List<Player>> optionalPlayers = games.keySet().stream().filter(l -> l.contains(player)).findFirst();
        if (optionalPlayers.isPresent()) {
            List<Player> players = optionalPlayers.get();
            int idx = players.indexOf(player);
            ChessBoard board = games.get(players);
            if (turn != board.isWhiteTurn()) {
                System.out.println(board.getLastMove());
                ChessReturn chessReturn = new ChessReturn(board.generateFenString(), board.getAllValidMoves(true),
                        board.getLastMove(), (idx == 0) ? true : false, board.isWhiteTurn());
                return chessReturn;
            }
        }
        return null;
    }

    @RequestMapping(value = "/chess/wait", method = RequestMethod.POST)
    public synchronized ChessReturn waitingForPlayers(@RequestBody Player player) {
        Optional<List<Player>> optionalPlayers = games.keySet().stream().filter(l -> l.contains(player)).findFirst();

        if (optionalPlayers.isPresent()) {
            List<Player> players = optionalPlayers.get();
            int idx = players.indexOf(player);
            ChessBoard board = games.get(players);
            ChessReturn chessReturn = new ChessReturn(board.generateFenString(), board.getAllValidMoves(true),
                    new Move(1, 1), (idx == 0) ? true : false, board.isWhiteTurn());
            return chessReturn;
        }

        return null;
    }

    @RequestMapping(value = "/chess/move/multi", method = RequestMethod.POST)
    public synchronized ChessReturn getAllValidMovesMulti(@RequestParam int startPos, @RequestParam int targetPos, @RequestBody List<Player> players) {
        Optional<List<Player>> optionalPlayers = games.keySet().stream().filter(l -> l.contains(players.get(0))).findFirst();
        if (optionalPlayers.isPresent()) {
            List<Player> playerList = optionalPlayers.get();
            int idx = playerList.indexOf(players.get(0));
            ChessBoard board = games.get(playerList);
            Move move = new Move(startPos, targetPos);
            board.makeMove(move, true);
            board.changeTurn();
            ChessReturn chessReturn = new ChessReturn(board.generateFenString(), board.getAllValidMoves(true),
                    move, (idx == 0) ? true : false, board.isWhiteTurn());
            return chessReturn;
        }
        return null;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value = "/chess/move/single", method = RequestMethod.POST)
    public synchronized ChessReturn getAllValidMoves(@RequestParam int startPos, @RequestParam int targetPos, @RequestBody List<Player> players) {
        ChessBoard board = games.get(players);
        board.makeMove(new Move(startPos, targetPos), true);
        board.changeTurn();
        Move aiMove = ChessAi.calculateBestMove(new ChessBoard(board, false));
        //System.out.println(board.generateFenString());
        board.makeMove(aiMove, true);
        board.changeTurn();
        //Move aiMove = new Move(-1 , -1);
        return new ChessReturn(board.generateFenString(), board.getAllValidMoves(true), aiMove, true, board.isWhiteTurn());
    }
}