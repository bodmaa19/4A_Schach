package at.kaindorf.chess.pojos.moves;

public class EnPassantMove extends Move {

    private int enPassantCaptureIdx;
    public EnPassantMove(int startPos, int targetPos, int captureIdx) {
        super(startPos, targetPos);
        enPassantCaptureIdx = captureIdx;
    }

    public int getEnPassantCaptureIdx() {
        return enPassantCaptureIdx;
    }
}
