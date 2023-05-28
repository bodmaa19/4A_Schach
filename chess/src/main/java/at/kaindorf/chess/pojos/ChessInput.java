package at.kaindorf.chess.pojos;

import at.kaindorf.chess.pojos.moves.Move;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChessInput {
    Move move;
    List<Player> players;
}
