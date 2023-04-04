package at.kaindorf.chess.board;

import at.kaindorf.chess.pojos.Move;
import at.kaindorf.chess.pojos.Piece;
import org.springframework.util.SerializationUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChessBoard {
    private Piece[] board = new Piece[64];
    private boolean whiteTurn = false;

    public ChessBoard() {
        resetBoard();
        setBoardWithFenString("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
    }

    public ChessBoard(ChessBoard chessBoard){
        for (int i = 0; i < chessBoard.board.length; i++) {
            this.board[i] = chessBoard.getBoard()[i];
        }
        this.whiteTurn = !chessBoard.isWhiteTurn();
    }

    public ChessBoard(String fenString) {
        setBoardWithFenString(fenString);
    }

    public void resetBoard() {
        for (int i = 0; i < board.length; i++) {
            board[i] = Piece.NO;
        }
    }

    public void setBoardWithFenString(String fenString) {
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

    public void setBoard(Piece[] board) {
        this.board = board;
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

    public List<Move> getAllValidMoves(boolean check) {
        List<Move> moves = new ArrayList<>();
        int wk = 0;
        for (int i = 0; i < board.length; i++) {
            Piece p = board[i];
            List<Move> newMoves = null;
            //if((whiteTurn && p.getColor() == 'w') || (!whiteTurn && p.getColor() == 'b')) {
            newMoves = switch (p) {
                case WK, BK -> Move.getValidKingMove(p, i, board);
                case WQ, BQ -> Move.getValidQueenMove(p, i, board);
                case WR, BR -> Move.getValidRookMove(p, i, board);
                case WN, BN -> Move.getValidNightMove(p, i, board);
                case WB, BB -> Move.getValidBishopMove(p, i, board);
                case BP, WP -> Move.getValidPawnMove(p, i, board);
                case NO -> null;
            };
            //}
            if (newMoves != null) {
                moves.addAll(newMoves);
            }
            if (Piece.WK.equals(p)) {
                wk = i;
            }
        }

        if(check) {
            int crt = 0;
            for (Move move : moves) {
                ChessBoard nextMoveChessBoard = new ChessBoard(this);
                nextMoveChessBoard.makeMove(move);
                List<Move> nextMoves = nextMoveChessBoard.getAllValidMoves(false);
                if (isCheck(nextMoves)) {
                    System.out.println("check");
                }
            }
        }
        return moves;
    }

    public void makeMove(Move move) {
        board[move.getTargetPos()] = board[move.getStartPos()];
        board[move.getStartPos()] = Piece.NO;
    }

    public boolean isCheck(List<Move> moves) {
        // implement turn
        Optional<Piece> wk = Arrays.stream(board).filter(p -> p.equals(Piece.WK)).findFirst();
        if(wk.isPresent()){
            System.out.println(wk.get());
            if(moves.stream().map(m -> m.getTargetPos()).collect(Collectors.toList()).contains(wk.get())){
                return true;
            }
        }

        return false;
    }

    public void changeTurn() {
        whiteTurn = !whiteTurn;
    }

    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    public void setWhiteTurn(boolean whiteTurn) {
        this.whiteTurn = whiteTurn;
    }

    public Piece[] getBoard() {
        return board;
    }

    public static void main(String[] args) {
        ChessBoard board = new ChessBoard();
        System.out.println(board);
        board.getAllValidMoves(true);
    }
}