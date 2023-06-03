package at.kaindorf.chess.core.pojos;

import at.kaindorf.chess.core.pojos.moves.Move;
import at.kaindorf.chess.userManagement.pojos.Player;
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
