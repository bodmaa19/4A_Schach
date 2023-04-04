package at.kaindorf.chess.board;

import at.kaindorf.chess.pojos.Move;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChessController {
    private ChessBoard board = new ChessBoard();
    @RequestMapping(value = "/chess", method = RequestMethod.POST)
    public List<Move> getAllValidMoves(@RequestBody String fenString){
        //board.changeTurn();
        //System.out.println(board.isWhiteTurn());
        board.setBoardWithFenString(fenString);
        return board.getAllValidMoves(true);
    }
}