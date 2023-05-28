package at.kaindorf.chess.pojos.moves;

import at.kaindorf.chess.pojos.piece.PieceType;

public class PromotionMove extends Move {

    private PieceType desiredType;

    public PromotionMove(int startPos, int targetPos, PieceType desiredPiece) {
        super(startPos, targetPos);
        desiredType = desiredPiece;
    }

    public PieceType getDesiredType() {
        return desiredType;
    }

    public void setDesiredType(PieceType desiredType) {
        this.desiredType = desiredType;
    }
}
