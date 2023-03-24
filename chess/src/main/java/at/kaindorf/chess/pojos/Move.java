package at.kaindorf.chess.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Move {
    private int startPos;
    private int targetPos;
}
