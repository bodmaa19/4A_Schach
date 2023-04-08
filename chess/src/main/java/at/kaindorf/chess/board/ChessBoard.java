package at.kaindorf.chess.board;

import at.kaindorf.chess.pojos.ChessPiece;
import at.kaindorf.chess.pojos.Move;
import at.kaindorf.chess.pojos.Piece;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChessBoard {
    private ChessPiece[] board = new ChessPiece[64];
    private boolean whiteTurn = false;

    public ChessBoard() {
        setBoardWithFenString("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
        //setBoardWithFenString("r1bqkb1r/ppp2ppp/2n2n2/3pp3/3PP3/2N2N2/PPP2PPP/R1BQKB1R");
        //setBoardWithFenString("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R");
        whiteTurn = true;
    }

    public ChessBoard(ChessBoard chessBoard) {
        for (int i = 0; i < chessBoard.board.length; i++) {
            this.board[i] = new ChessPiece(chessBoard.getBoard()[i].getPiece(), chessBoard.getBoard()[i].getNumberOfMoves());
        }
        this.whiteTurn = !chessBoard.isWhiteTurn();
    }

    public ChessBoard(ChessBoard chessBoard, boolean isTrue) {
        for (int i = 0; i < chessBoard.board.length; i++) {
            this.board[i] = new ChessPiece(chessBoard.getBoard()[i].getPiece(), chessBoard.getBoard()[i].getNumberOfMoves());
        }
        this.whiteTurn = chessBoard.isWhiteTurn();
    }

    public ChessBoard(String fenString) {
        setBoardWithFenString(fenString);
    }

    public void resetBoard() {
        for (int i = 0; i < board.length; i++) {
            board[i] = new ChessPiece(Piece.NO, 0);
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
                    board[idx].setPiece(Arrays.stream(Piece.values()).filter(p -> p.getFen().equals(s)).findFirst().get());
                    idx++;
                }
            }
        }
    }

    public String generateFenString(){
        String fen = "";
        int num = 0;
        int row = 0;

        for (int i = 0; i < board.length; i++) {
            if (!board[i].getPiece().equals(Piece.NO)) {
                Piece p = board[i].getPiece();
                fen += (num == 0) ? p.getFen() : num +  p.getFen();
                num = 0;
            } else {
                if (num == 8) {
                    fen += num;
                    num = 0;
                } else {
                    num++;
                }
            }
            if ((i + 1) % 8 == 0 && i < board.length - 1) {
                fen += (num != 0) ? num + "/" : "/";
                num = 0;
            }
        }
        if (num != 0) {
            fen += num;
        }
        System.out.println(fen);
        return fen;
    }

    public void setBoard(ChessPiece[] board) {
        this.board = board;
    }

    @Override
    public String toString() {
        String boardStr = "";

        for (int i = 0; i < board.length; i++) {
            boardStr += board[i].getPiece() + " ";
            if ((i + 1) % 8 == 0) {
                boardStr += "\n";
            }
        }
        return boardStr;
    }

    public List<Move> getAllValidMoves(boolean check) {
        List<Move> moves = new ArrayList<>();

        for (int i = 0; i < board.length; i++) {
            Piece p = board[i].getPiece();
            List<Move> newMoves = null;
            if ((whiteTurn && p.getColor() == 'w') || (!whiteTurn && p.getColor() == 'b')) {
                newMoves = switch (p) {
                    case WK, BK -> Move.getValidKingMove(board[i], i, board);
                    case WQ, BQ -> Move.getValidQueenMove(board[i], i, board);
                    case WR, BR -> Move.getValidRookMove(board[i], i, board);
                    case WN, BN -> Move.getValidNightMove(board[i], i, board);
                    case WB, BB -> Move.getValidBishopMove(board[i], i, board);
                    case BP, WP -> Move.getValidPawnMove(board[i], i, board);
                    case NO -> null;
                };
            }
            if (newMoves != null) {
                moves.addAll(newMoves);
            }
        }


        List<Move> removeList = new ArrayList<>();
        Piece king = (whiteTurn) ? Piece.WK : Piece.BK;
        int castling = Move.isCastling(this);
        int kingIdx = findPiece(king);
        if(castling == 1 || castling == 3){
            moves.add(new Move(kingIdx, kingIdx-2));
        }
        if(castling == 2 || castling == 3){
            moves.add(new Move(kingIdx, kingIdx+2));
        }

        if (check) {
//            moves.stream().parallel().forEach(move -> {
//                ChessBoard nextMoveChessBoard = new ChessBoard(this);
//                nextMoveChessBoard.makeMove(move, false);
//                List<Move> nextMoves = nextMoveChessBoard.getAllValidMoves(false);
//                if (nextMoveChessBoard.isCheck(nextMoves, king)) {
//                    removeList.add(move);
//                }
//            });
            for (Move move : moves) {
                ChessBoard nextMoveChessBoard = new ChessBoard(this);
                nextMoveChessBoard.makeMove(move, false);
                List<Move> nextMoves = nextMoveChessBoard.getAllValidMoves(false);
                if (nextMoveChessBoard.isCheck(nextMoves, king)) {
                    removeList.add(move);
                }
            }
            moves.removeAll(removeList);
        }

        return moves;
    }

    public void makeMove(Move move, boolean realMove) {
        if (realMove) {
            board[move.getStartPos()].move();
        }
        board[move.getTargetPos()] = new ChessPiece(board[move.getStartPos()].getPiece(), board[move.getStartPos()].getNumberOfMoves());
        board[move.getStartPos()] = new ChessPiece(Piece.NO, 0);

        if(board[move.getTargetPos()].getPiece().equals((whiteTurn) ? Piece.WK : Piece.BK)){
            int kingDiff = move.getTargetPos() - move.getStartPos();
            System.out.println("King: " + kingDiff);
            if(Math.abs(kingDiff) == 2){
                if (kingDiff < 0){
                    board[move.getTargetPos()+1] = board[move.getStartPos()-4];
                    board[move.getStartPos()-4] = new ChessPiece(Piece.NO, 0);
                }else{
                    board[move.getTargetPos()-1] = board[move.getStartPos()+3];
                    board[move.getStartPos()+3] = new ChessPiece(Piece.NO, 0);
                }
            }
        }
    }


    public boolean isCheck(List<Move> moves, Piece king) {
        int kingIdx = findPiece(king);
        if (kingIdx != -1) {
            if (moves.stream().map(m -> m.getTargetPos()).collect(Collectors.toList()).contains(kingIdx)) {
                return true;
            }
        } else {
            return true;
        }

        return false;
    }

    public boolean isCheckMove(Move move, Piece king) {
        int kingIdx = findPiece(king);
        if (kingIdx != -1) {
            if (move.getTargetPos() == kingIdx) {
                return true;
            }
        } else {
            return true;
        }

        return false;
    }

    public int findPiece(Piece piece) {
        for (int i = 0; i < board.length; i++) {
            if (board[i].getPiece().equals(piece)) {
                return i;
            }
        }
        return -1;
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

    public ChessPiece[] getBoard() {
        return board;
    }

    public static void main(String[] args) {
        ChessBoard board = new ChessBoard();
        System.out.println(board);
        board.getAllValidMoves(true);
    }
}