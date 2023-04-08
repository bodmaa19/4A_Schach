package at.kaindorf.chess.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChessPiece {
    Piece piece;
    int numberOfMoves = 0;

    public void move(){
        numberOfMoves++;
    }
}
