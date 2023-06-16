package at.kaindorf.chess.core.pojos.moves;

import at.kaindorf.chess.core.board.ChessBoard;
import at.kaindorf.chess.core.pojos.piece.ChessPiece;
import at.kaindorf.chess.core.pojos.piece.Piece;
import lombok.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
/**
 * project name: Schach_4A
 * author: Roman Lanschützer
 * date: 08.04.2023
 */
@Getter
@Setter
public class CastlingMove extends Move {
    private int rookStart;
    private int rookTarget;

    public CastlingMove(int startPos, int targetPos, int rookStart, int rookTarget) {
        super(startPos, targetPos);
        this.rookStart = rookStart;
        this.rookTarget = rookTarget;
    }

    public static int isCastling(ChessBoard board) {
        boolean turn = board.isWhiteTurn();
        ChessPiece king = Arrays.stream(board.getBoard()).filter(p -> p.getPiece().equals((turn) ? Piece.WK : Piece.BK)).findFirst().get();
        int kingIdx = board.findPiece(king.getPiece());
        List<ChessPiece> rooks = Arrays.stream(board.getBoard()).filter(p -> p.getPiece().equals((turn) ? Piece.WR : Piece.BR)).collect(Collectors.toList());
        int returnValue = 0;
        if (rooks.size() == 2 && king.getNumberOfMoves() == 0) {
            if (rooks.get(0).getNumberOfMoves() == 0 && rooks.get(1).getNumberOfMoves() == 0) {
                boolean isEmptyLeft = true;
                boolean isEmptyRight = true;
                for (int i = kingIdx - 3; i < kingIdx + 3; i++) {
                    if (i < kingIdx) {
                        if(!board.getBoard()[i].getPiece().equals(Piece.NO)){
                            isEmptyLeft = false;
                        }
                    }else if(i > kingIdx){
                        if(!board.getBoard()[i].getPiece().equals(Piece.NO)){
                            isEmptyRight = false;
                        }
                    }
                }

                if(isEmptyLeft){
                    returnValue += 1;
                }
                if(isEmptyRight){
                    returnValue += 2;
                }
            }
        }
        System.out.println(returnValue);
        return returnValue;
    }
}
