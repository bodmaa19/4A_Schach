package at.kaindorf.chess.core.pojos.moves;

//Max ToDo: Make all getValidMoves to
// - Include Board vs idxBoard
// - Rename idx -> startingIdx
// - Rename p --> specific piece

import at.kaindorf.chess.core.board.ChessBoard;
import at.kaindorf.chess.core.pojos.piece.ChessPiece;
import at.kaindorf.chess.core.pojos.piece.Piece;
import at.kaindorf.chess.core.pojos.piece.PieceType;

import java.util.*;
import java.util.stream.Collectors;

public class Move {
    private int startPos;
    private int targetPos;
    private int aiScore = 0;


    public static List<Move> getValidQueenMove(ChessPiece p, int idx, ChessPiece[] board) {
        List<Move> moves = new ArrayList<>();
        moves.addAll(getValidBishopMove(p, idx, board));
        moves.addAll(getValidRookMove(p, idx, board));
        return moves;
    }

    public static List<Move> getValidBishopMove(ChessPiece piece, int idx, ChessPiece[] board) {
        Piece p = piece.getPiece();
        int[] moveArr = p.getMoves();
        if (moveArr.length > 4) {
            moveArr = Arrays.copyOfRange(moveArr, 4, moveArr.length);
        }
        List<Move> moves = new ArrayList<>();
        int anz = 0;
        char currentColor = getColorOfField(idx);

        for (int i = 0; i < moveArr.length; i++) {
            if (idx + moveArr[i] < board.length && idx + moveArr[i] >= 0) {
                int targetIdx = idx + moveArr[i];
                Piece target = board[targetIdx].getPiece();
                char targetColor = getColorOfField(targetIdx);
                anz = 0;

                while ((isDifferentColor(p.getColor(), target.getColor()) || target.getColor() == Piece.NO.getColor())
                        && anz < 7 && currentColor == targetColor) {
                    try {
                        anz++;
                        moves.add(new Move(idx, targetIdx));
                        if (isDifferentColor(p.getColor(), target.getColor())) {
                            break;
                        }
                        targetIdx = targetIdx + moveArr[i];
                        target = board[targetIdx].getPiece();
                        targetColor = getColorOfField(targetIdx);
                    } catch (IndexOutOfBoundsException ex) {
                        break;
                    }
                    if (p.equals(Piece.BK) || p.equals(Piece.WK)) {
                        break;
                    }
                }
            }
        }
        return moves;
    }

    public static List<Move> getValidRookMove(ChessPiece piece, int idx, ChessPiece[] board) {
        Piece p = piece.getPiece();
        int[] moveArr = p.getMoves();
        if (moveArr.length > 4) {
            moveArr = Arrays.copyOfRange(moveArr, 0, 4);
        }
        List<Move> moves = new ArrayList<>();
        int anz = 0;
        char currentColor;

        for (int i = 0; i < moveArr.length; i++) {
            if (idx + moveArr[i] < board.length && idx + moveArr[i] >= 0) {
                currentColor = getColorOfField(idx);
                int targetIdx = idx + moveArr[i];
                Piece target = board[targetIdx].getPiece();
                char targetColor = getColorOfField(targetIdx);
                anz = 0;

                while ((isDifferentColor(p.getColor(), target.getColor()) || target.getColor() == Piece.NO.getColor())
                        && anz < 7 && currentColor != targetColor) {
                    try {
                        anz++;
                        moves.add(new Move(idx, targetIdx));
                        if (isDifferentColor(p.getColor(), target.getColor())) {
                            break;
                        }
                        targetIdx = targetIdx + moveArr[i];
                        target = board[targetIdx].getPiece();
                        currentColor = targetColor;
                        targetColor = getColorOfField(targetIdx);
                    } catch (IndexOutOfBoundsException ex) {
                        break;
                    }
                    if (p.equals(Piece.BK) || p.equals(Piece.WK)) {
                        break;
                    }
                }
            }
        }
        return moves;
    }

    public static char getColorOfField(int idx) {
        return (((idx / 8) + ((idx % 8) % 2)) % 2 == 0) ? 'w' : 'b';
    }

    private static boolean isColorOfFieldDifferent(int idx1, int idx2) {
        return isDifferentColor(getColorOfField(idx1), getColorOfField(idx2));
    }

    public static List<Move> getValidKnightMove(ChessPiece piece, int idx, ChessPiece[] board) {
        Piece p = piece.getPiece();
        int[] moveArr = p.getMoves();
        List<Move> moves = new ArrayList<>();
        char pieceField = getColorOfField(idx);
        for (int i = 0; i < moveArr.length; i++) {
            if (idx + moveArr[i] < board.length && idx + moveArr[i] >= 0) {
                Piece target = board[idx + moveArr[i]].getPiece();
                int targetField = getColorOfField(idx + moveArr[i]);
                if ((isDifferentColor(p.getColor(), target.getColor()) || target.equals(Piece.NO)) && pieceField != targetField) {
                    moves.add(new Move(idx, idx + moveArr[i]));
                }
            }
        }
        return moves;
    }

    public static boolean isDifferentColor(char c1, char c2) {
        return c1 != c2 && c2 != ' ';
    }

    public static boolean isDifferentColor(Piece p1, Piece p2) { return p1.getColor() != p2.getColor(); }

    public static List<Move> getValidPawnMove(ChessPiece piece, int startPos, ChessBoard board) {
        ChessPiece[] idxBoard = board.getBoard();
        Piece pawn = piece.getPiece();
        int[] moveArr = pawn.getMoves();

        int targetPos;
        List<Move> moves = new ArrayList<>();
        // Normal pawn move
            targetPos = startPos + moveArr[0];
            if (targetPos >= 0 && targetPos < idxBoard.length && idxBoard[targetPos].getPiece() == Piece.NO) {
                moves.add(getNormaleOrPromotingPawnMove(startPos, targetPos));
            }
        //

        // Fist "double" pawn move
        targetPos = startPos + moveArr[1];
        if (targetPos >= 0 && targetPos < idxBoard.length) {
            if (idxBoard[targetPos].getPiece().equals(Piece.NO) &&
                idxBoard[startPos + moveArr[0]].getPiece().equals(Piece.NO) &&
                piece.getNumberOfMoves() == 0) {
                moves.add(new Move(startPos, targetPos));
            }
        }

        // Capturing a piece
            // normal
                int pRow = startPos / 8;
                for (int i = 2; i < moveArr.length; i++) {
                    if (startPos + moveArr[i] >= 0 && startPos + moveArr[i] < idxBoard.length) {
                        if (isDifferentColor(pawn.getColor(), idxBoard[startPos + moveArr[i]].getPiece().getColor())
                                && (startPos + moveArr[i]) / 8 != pRow + (pawn.isWhitePiece() ? -2 : 2) &&
                                getColorOfField(startPos) == getColorOfField(startPos + moveArr[i])) {
                            moves.add(getNormaleOrPromotingPawnMove(startPos, startPos + moveArr[i]));
                        }
                    }
                }
            //

            // Capturing "en passante":
                Move previousMove = board.getPreviousMove();
                boolean enPassentPossible = false;

                if(previousMove != null) {
                    ChessPiece chessPiece = idxBoard[previousMove.targetPos];
                    boolean lastMovePawn = chessPiece.getPiece().isPieceType(PieceType.Pawn);
                    boolean firstMove = chessPiece.getNumberOfMoves() == 1;
                    boolean forthRow = previousMove.targetPos >= 24 && previousMove.targetPos <= 39;
                    boolean edgeCase = isColorOfFieldDifferent(previousMove.targetPos, startPos);
                    enPassentPossible = lastMovePawn && firstMove && forthRow && edgeCase;
                }

                if(enPassentPossible)
                {
                    if(previousMove.targetPos == startPos + 1) {
                        int diffToTarget = pawn.isWhitePiece() ? -7 : 9;
                        int diffToCapture = pawn.isWhitePiece() ? 8 : -8;
                        moves.add(new EnPassantMove(startPos, startPos + diffToTarget, startPos + diffToTarget + diffToCapture));
                    }

                    if(previousMove.targetPos == startPos - 1) {
                        int diffToTarget = pawn.isWhitePiece() ? -9 : 7;
                        int diffToCapture = pawn.isWhitePiece() ? 8 : -8;
                        moves.add(new EnPassantMove(startPos, startPos + diffToTarget, startPos + diffToTarget + diffToCapture));
                    }
                }
            //
        //

        return moves;
    }

    private static Move getNormaleOrPromotingPawnMove(int startingPos, int targetPos) {
        Move move;

        if(targetPos < 8 || targetPos > 55) {
            move = new PromotionMove(startingPos, targetPos, PieceType.Queen);
        } else {
            move = new Move(startingPos, targetPos);
        }

        return move;
    }

    public static int isCastlingPossible(ChessBoard board) {
        boolean turn = board.isWhiteTurn();
        Optional<ChessPiece> optionalChessPiece = Arrays.stream(board.getBoard()).filter(p -> p.getPiece().equals((turn) ? Piece.WK : Piece.BK)).findFirst();
        ChessPiece king = null;
        if (optionalChessPiece.isEmpty()) {
            return 0;
        } else {
            king = optionalChessPiece.get();
        }

        int kingIdx = board.findPiece(king.getPiece());
        List<ChessPiece> rooks = Arrays.stream(board.getBoard()).filter(p -> p.getPiece().equals((turn) ? Piece.WR : Piece.BR)).collect(Collectors.toList());
        int returnValue = 0;
        if (rooks.size() == 2 && king.getNumberOfMoves() == 0) {
            boolean isEmptyLeft = true;
            boolean isEmptyRight = true;
            for (int i = kingIdx - 3; i < kingIdx + 3; i++) {
                if (i < kingIdx) {
                    if (!board.getBoard()[i].getPiece().equals(Piece.NO)) {
                        isEmptyLeft = false;
                    }
                } else if (i > kingIdx) {
                    if (!board.getBoard()[i].getPiece().equals(Piece.NO)) {
                        isEmptyRight = false;
                    }
                }
            }

            if (isEmptyLeft && rooks.get(0).getNumberOfMoves() == 0) {
                returnValue += 1;
            }
            if (isEmptyRight && rooks.get(1).getNumberOfMoves() == 0) {
                returnValue += 2;
            }
        }
        return returnValue;
    }

    public static List<Move> getValidKingMove(ChessPiece p, int idx, ChessPiece[] board) {
        List<Move> moves = new ArrayList<>();
        moves.addAll(getValidBishopMove(p, idx, board));
        moves.addAll(getValidRookMove(p, idx, board));
        return moves;
    }

    public static List<Move> getValidMoves(Piece p, int idx, Piece[] board) {
        List<Move> moves = new ArrayList<>();

        return moves;
    }

    public Move(int startPos, int targetPos) {
        this.startPos = startPos;
        this.targetPos = targetPos;
    }

    public void setAiScore(int aiScore) {
        this.aiScore = aiScore;
    }

    public int getStartPos() {
        return startPos;
    }

    public int getTargetPos() {
        return targetPos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return startPos == move.startPos && targetPos == move.targetPos && aiScore == move.aiScore;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPos, targetPos, aiScore);
    }

    public int getAiScore() {
        return aiScore;
    }

    @Override
    public String toString() {
        return "Move{" +
                "startPos=" + startPos +
                ", targetPos=" + targetPos +
                '}';
    }

    public String toStringAi() {
        return "Move{aiScore=" + aiScore + "}";
    }
}