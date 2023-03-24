package at.kaindorf.chess.board;

import at.kaindorf.chess.pojos.Move;
import at.kaindorf.chess.pojos.Piece;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChessBoard {
    private Piece[] board = new Piece[64];
    private boolean whiteTurn = true;

    public ChessBoard() {
        resetBoard();
        setBoardWithFenString("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBKQBNR");
    }

    public ChessBoard(String fenString) {
        setBoardWithFenString(fenString);
    }

    public void resetBoard() {
        for (int i = 0; i < board.length; i++) {
            board[i] = Piece.NO;
        }
        whiteTurn = true;
    }

    private void setBoardWithFenString(String fenString) {
        resetBoard();
        String[] tokens = fenString.split("");
        int idx = 0;
        for (String s : tokens) {
            try {
                int num = Integer.parseInt(s);
                idx += num;
            } catch (NumberFormatException e) {
                if (!s.equals("/")) {
                    board[idx] = Arrays.stream(Piece.values()).filter(p -> p.getFen().equals(s)).findFirst().get();
                    idx++;
                }
            }
        }
    }

    @Override
    public String toString() {
        String boardStr = "";

        for (int i = 0; i < board.length; i++) {
            boardStr += board[i] + " ";
            if ((i + 1) % 8 == 0) {
                boardStr += "\n";
            }
        }
        return boardStr;
    }

    public List<Move> getAllValidMoves() {
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            Piece p = board[i];

            List<Move> newMoves = switch (p) {
                case WK -> null;
                case WQ -> null;
                case WR, BR -> getValidRookMove(p, i);
                case WN, BN -> getValidNightMove(p, i);
                case WB -> null;
                case BK -> null;
                case BQ -> null;
                case BB -> null;
                case BP, WP -> getValidPawnMove(p, i);
                case NO -> null;
            };
            if (newMoves != null) {
                moves.addAll(newMoves);
            }
        }

        return moves;
    }

    private List<Move> getValidRookMove(Piece p, int idx) {
        int[] moveArr = p.getMoves();
        List<Move> moves = new ArrayList<>();
        //int pieceField = ((idx / 8) + ((idx%8)%2))%2;
        for (int i = 0; i < moveArr.length; i++) {
            if (idx + moveArr[i] < board.length && idx + moveArr[i] >= 0) {
                int targetIdx = idx + moveArr[i];
                Piece target = board[targetIdx];
                //int targetField = (((idx + moveArr[i]) / 8) + (((idx + moveArr[i])%8)%2))%2;
                while (isDifferentColor(p.getColor(), target.getColor())) {
                    targetIdx = targetIdx + moveArr[i];
                    target = board[targetIdx + moveArr[i]];
                    if (isDifferentColor(p.getColor(), target.getColor())) {
                        moves.add(new Move(idx, idx + moveArr[i]));
                        break;
                    }

                }
            }
        }
        return moves;
    }

    private List<Move> getValidNightMove(Piece p, int idx) {
        int[] moveArr = p.getMoves();
        List<Move> moves = new ArrayList<>();
        int pieceField = ((idx / 8) + ((idx % 8) % 2)) % 2;
        System.out.println("Moves: " + ((pieceField == 0) ? "White" : "Black"));
        for (int i = 0; i < moveArr.length; i++) {
            if (idx + moveArr[i] < board.length && idx + moveArr[i] >= 0) {
                Piece target = board[idx + moveArr[i]];
                int targetField = (((idx + moveArr[i]) / 8) + (((idx + moveArr[i]) % 8) % 2)) % 2;
                System.out.println(pieceField + " " + targetField);
                if ((isDifferentColor(p.getColor(), target.getColor()) || target.equals(Piece.NO)) && pieceField != targetField) {
                    moves.add(new Move(idx, idx + moveArr[i]));
                }
            }
        }
        return moves;
    }

    private boolean isDifferentColor(char c1, char c2) {
        return c1 != c2 && c2 != ' ';
    }

    private List<Move> getValidPawnMove(Piece p, int idx) {
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

    public static void main(String[] args) {
        ChessBoard board = new ChessBoard();
        System.out.println(board);
        board.getAllValidMoves();
    }
}
