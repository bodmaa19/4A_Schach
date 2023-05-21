package at.kaindorf.chess.pojos;

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
}