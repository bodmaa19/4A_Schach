package at.kaindorf.chess.core.pojos.piece;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * project name: Schach_4A
 * author: Roman Lanschützer
 * date: 08.04.2023
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChessPiece {
    Piece piece;
    int numberOfMoves = 0;

    public void increaseMoveCounter(){
        numberOfMoves++;
    }
}
