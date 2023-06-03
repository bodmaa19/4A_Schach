package at.kaindorf.chess.core.pojos;

import at.kaindorf.chess.core.pojos.moves.Move;
import at.kaindorf.chess.userManagement.pojos.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChessReturn {
    private String fenString;
    private List<Move> moves;
    private Move aiMove;
    private boolean white;
    private boolean isWhiteTurn;
    private User opponent;
}