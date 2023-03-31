package at.kaindorf.chess.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Move {
    private int startPos;
    private int targetPos;

    public static List<Move> getValidQueenMove(Piece p, int idx, Piece[] board) {
        List<Move> moves = new ArrayList<>();
        moves.addAll(getValidBishopMove(p, idx, board));

        moves.addAll(getValidRookMove(p, idx, board));
        return moves;
    }

    public static List<Move> getValidBishopMove(Piece p, int idx, Piece[] board) {
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
                Piece target = board[targetIdx];
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
                        target = board[targetIdx];
                        targetColor = getColorOfField(targetIdx);
                    } catch (IndexOutOfBoundsException ex) {
                        break;
                    }
                    if(p.equals(Piece.BK) || p.equals(Piece.WK)){
                        break;
                    }
                }
            }
        }
        return moves;
    }

    public static List<Move> getValidRookMove(Piece p, int idx, Piece[] board) {
        int[] moveArr = p.getMoves();
        if (moveArr.length > 4) {
            moveArr = Arrays.copyOfRange(moveArr, 0, 4);
        }
        List<Move> moves = new ArrayList<>();
        int anz = 0;
        char currentColor = getColorOfField(idx);

        for (int i = 0; i < moveArr.length; i++) {
            if (idx + moveArr[i] < board.length && idx + moveArr[i] >= 0) {
                currentColor = getColorOfField(idx);
                int targetIdx = idx + moveArr[i];
                Piece target = board[targetIdx];
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
                        target = board[targetIdx];
                        currentColor = targetColor;
                        targetColor = getColorOfField(targetIdx);
                    } catch (IndexOutOfBoundsException ex) {
                        break;
                    }
                    if(p.equals(Piece.BK) || p.equals(Piece.WK)){
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

    public static List<Move> getValidNightMove(Piece p, int idx, Piece[] board) {
        int[] moveArr = p.getMoves();
        List<Move> moves = new ArrayList<>();
        char pieceField = getColorOfField(idx);
        for (int i = 0; i < moveArr.length; i++) {
            if (idx + moveArr[i] < board.length && idx + moveArr[i] >= 0) {
                Piece target = board[idx + moveArr[i]];
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

    public static List<Move> getValidPawnMove(Piece p, int idx, Piece[] board) {
        int[] moveArr = p.getMoves();
        List<Move> moves = new ArrayList<>();
        if (idx + moveArr[0] >= 0 && idx + moveArr[0] < board.length) {
            if (board[idx + moveArr[0]].equals(Piece.NO)) {
                moves.add(new Move(idx, idx + moveArr[0]));
            }
        }

        /*if(board[idx+moveArr[1]].equals(Piece.NO) && p.getNumberOfMoves() == 0){
            moves.add(new Move(idx, idx+moveArr[1]));
        }*/
        int pRow = idx / 8;
        for (int i = 2; i < moveArr.length; i++) {
            if (idx + moveArr[i] >= 0 && idx + moveArr[i] < board.length) {
                if (isDifferentColor(p.getColor(), board[idx + moveArr[i]].getColor())
                        && (idx + moveArr[i]) / 8 != pRow + ((p.getColor() == 'w') ? -2 : 2)) {
                    moves.add(new Move(idx, idx + moveArr[i]));
                }
            }
        }
        return moves;
    }

    public static void makeMove(){

    }

    public static List<Move> getValidKingMove(Piece p, int idx, Piece[] board) {
        List<Move> moves = new ArrayList<>();
        moves.addAll(getValidBishopMove(p, idx, board));

        moves.addAll(getValidRookMove(p, idx, board));
        return moves;
    }
}
