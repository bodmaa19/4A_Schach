package at.kaindorf.chess.core.pojos.moves;
/**
 * project name: Schach_4A
 * author: Maxi Maurer
 * date: 28.05.2023
 */
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
