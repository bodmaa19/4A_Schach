package at.kaindorf.chess.board;

import at.kaindorf.chess.pojos.Move;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChessController {
    @RequestMapping(value = "/chess", method = RequestMethod.POST)
    public List<Move> getAllValidMoves(@RequestBody String fenString){
        ChessBoard board = new ChessBoard(fenString);
        return board.getAllValidMoves();
    }
}
