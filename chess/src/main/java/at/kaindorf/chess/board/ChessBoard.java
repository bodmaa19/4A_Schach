package at.kaindorf.chess.board;

import at.kaindorf.chess.pojos.moves.EnPassantMove;
import at.kaindorf.chess.pojos.moves.Move;
import at.kaindorf.chess.pojos.moves.PromotionMove;
import at.kaindorf.chess.pojos.piece.ChessPiece;
import at.kaindorf.chess.pojos.piece.Piece;
import at.kaindorf.chess.pojos.piece.PieceType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChessBoard {
    private ChessPiece[] board = new ChessPiece[64];
    private boolean whiteTurn = false;
    private String moveHistoryStr = "";
    private Move lastMove;

    public ChessBoard() {
        setBoardWithFenString("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
        whiteTurn = true;
    }

    public ChessBoard(ChessBoard chessBoard) {
        for (int i = 0; i < chessBoard.board.length; i++) {
            this.board[i] = new ChessPiece(chessBoard.getBoard()[i].getPiece(), chessBoard.getBoard()[i].getNumberOfMoves());
        }
        moveHistoryStr = String.valueOf(chessBoard.moveHistoryStr);
        this.whiteTurn = !chessBoard.isWhiteTurn();
    }

    public ChessBoard(ChessBoard chessBoard, boolean isTrue) {
        for (int i = 0; i < chessBoard.board.length; i++) {
            this.board[i] = new ChessPiece(chessBoard.getBoard()[i].getPiece(), chessBoard.getBoard()[i].getNumberOfMoves());
        }
        this.whiteTurn = chessBoard.isWhiteTurn();
        moveHistoryStr = String.valueOf(chessBoard.moveHistoryStr);
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

    public String generateFenString() {
        String fen = "";
        int num = 0;
        int row = 0;

        for (int i = 0; i < board.length; i++) {
            if (!board[i].getPiece().equals(Piece.NO)) {
                Piece p = board[i].getPiece();
                fen += (num == 0) ? p.getFen() : num + p.getFen();
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
        // Gets all moves including the ones that end up in a check for the king
            List<Move> moves = new ArrayList<>();

            for (int i = 0; i < board.length; i++) {
                Piece p = board[i].getPiece();

                List<Move> newMoves = null;
                if ((whiteTurn && p.isWhitePiece()) || (!whiteTurn && !p.isWhitePiece())) {
                    newMoves = switch (p.getPieceType()) {
                        case King -> Move.getValidKingMove(board[i], i, board);
                        case Queen -> Move.getValidQueenMove(board[i], i, board);
                        case Rook -> Move.getValidRookMove(board[i], i, board);
                        case Knight -> Move.getValidKnightMove(board[i], i, board);
                        case Bishop -> Move.getValidBishopMove(board[i], i, board);
                        case Pawn -> Move.getValidPawnMove(board[i], i, this);
                        case NoPiece -> null;
                    };
                }
                if (newMoves != null) {
                    moves.addAll(newMoves);
                }
            }

            // Checks if castling is possible
                Piece king = (whiteTurn) ? Piece.WK : Piece.BK;
                int castling = Move.isCastling(this);
                int kingIdx = findPiece(king);
                if (castling == 1 || castling == 3) {
                    moves.add(new Move(kingIdx, kingIdx - 2));
                }
                if (castling == 2 || castling == 3) {
                    moves.add(new Move(kingIdx, kingIdx + 2));
                }
            //
        //

        // Check for invalid moves that end up in a check (if the boolean check flag is set)
            if (check) {
                List<Move> removeList = new ArrayList<>();

                for (Move move : moves) {
                    ChessBoard nextMoveChessBoard = new ChessBoard(this);
                    nextMoveChessBoard.makeMove(move, false);
                    List<Move> nextMoves = nextMoveChessBoard.getAllValidMoves(false);

                    if (nextMoveChessBoard.canKingBeCaptured(nextMoves, king)) {
                        removeList.add(move);
                    }
                }
                moves.removeAll(removeList);
            }
        //

        return moves;
    }

    public void makeMove(Move move, boolean realMove) {
        movePiece(move);

        if(realMove) {
            board[move.getTargetPos()].increaseMoveCounter();
            lastMove = move;
            updateMoveHistoryString(move);
        }
    }

    private void movePiece(Move move) {
        int startPos = move.getStartPos();
        int targetPos = move.getTargetPos();

        Piece movedPiece = board[startPos].getPiece();

        board[targetPos] = new ChessPiece(board[startPos].getPiece(), board[startPos].getNumberOfMoves());
        board[startPos] = new ChessPiece(Piece.NO, 0);

        // En Passante
            if(move instanceof EnPassantMove) {
                board[((EnPassantMove)move).getEnPassantCaptureIdx()] = new ChessPiece(Piece.NO, 0);
            }
        //

        // Promotion
            if(move instanceof PromotionMove) {
                board[targetPos] = new ChessPiece(Piece.create(PieceType.Queen, movedPiece.getPieceColor()), 0);
            }

        // Rochade
            if (movedPiece.equals((whiteTurn) ? Piece.WK : Piece.BK)) {
                int kingDiff = targetPos - startPos;
                if (Math.abs(kingDiff) == 2) {
                    if (kingDiff < 0) {
                        board[targetPos + 1] = board[startPos - 4];
                        board[startPos - 4] = new ChessPiece(Piece.NO, 0);
                    } else {
                        board[targetPos - 1] = board[startPos + 3];
                        board[startPos + 3] = new ChessPiece(Piece.NO, 0);
                    }
                }
            }
        //
    }

    private void updateMoveHistoryString(Move move) {
        int targetPos = move.getTargetPos();
        int numberOfMoves = getTotalNumberOfMoves();

        //Move Prefix
            if (isWhiteTurn()) {
                moveHistoryStr += (numberOfMoves / 2) + 1 + ".";
            }
        //

        Piece p = board[targetPos].getPiece();
        if (!(p.getPieceType() == PieceType.Pawn)) {
            moveHistoryStr += p.getFen().toUpperCase();
        }

        moveHistoryStr += (char)(targetPos % 8 + 'a') + "" + Math.abs(8-(targetPos / 8)) + " ";
    }

    public boolean canKingBeCaptured(List<Move> moves, Piece king) {
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

    public int getTotalNumberOfMoves() {
        return Arrays.stream(board).mapToInt(ChessPiece::getNumberOfMoves).sum();
    }

    public String getMoveHistoryStr() {
        return moveHistoryStr;
    }

    public Move getLastMove() {
        return lastMove;
    }

    public Move getPreviousMove() {
        return lastMove;
    }

    public static void main(String[] args) {
        ChessBoard board = new ChessBoard();
        board.getAllValidMoves(true);
    }
}