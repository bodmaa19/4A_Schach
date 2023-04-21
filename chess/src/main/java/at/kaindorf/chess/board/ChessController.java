package at.kaindorf.chess.board;

import at.kaindorf.chess.ai.ChessAi;
import at.kaindorf.chess.pojos.Castling;
import at.kaindorf.chess.pojos.ChessReturn;
import at.kaindorf.chess.pojos.Move;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
public class ChessController {
    private ChessBoard board;

    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value = "/chess/start", method = RequestMethod.POST)
    public List<Move> getAllValidMoves(@RequestBody String fenString){
        board = new ChessBoard();
        System.out.println(board.isWhiteTurn());
        System.out.println(1442);
        //board.setBoardWithFenString(fenString);
        //board.changeTurn();
        return board.getAllValidMoves(true);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value = "/chess/move", method = RequestMethod.POST)
    public ChessReturn getAllValidMoves(@RequestBody Move newMove){
        //System.out.println(board.isWhiteTurn());
        board.makeMove(newMove, true);
        board.changeTurn();
        Move aiMove = ChessAi.calculateBestMove(new ChessBoard(board, false));
        //System.out.println(board.generateFenString());
        board.makeMove(aiMove, true);
        board.changeTurn();
        //Move aiMove = new Move(-1 , -1);
        return new ChessReturn(board.generateFenString(), board.getAllValidMoves(true), aiMove);
    }
}