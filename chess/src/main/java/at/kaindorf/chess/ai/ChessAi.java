package at.kaindorf.chess.ai;

import at.kaindorf.chess.ai.book.BookIO;
import at.kaindorf.chess.board.ChessBoard;
import at.kaindorf.chess.pojos.piece.ChessPiece;
import at.kaindorf.chess.pojos.moves.Move;
import at.kaindorf.chess.pojos.piece.Piece;

import java.util.*;
import java.util.stream.Collectors;

public class ChessAi {
    private static Move bestMove;
    private static int wishedDepth = 4;


    public static Move calculateBestMove(ChessBoard board) {
        bestMove = null;
        if (board.getTotalNumberOfMoves() < 8) {
            try {
                String nextMoveStr = board.getMoveHistoryStr() + BookIO.getNextMove(board.getMoveHistoryStr()) + " ";
                System.out.println(nextMoveStr);
                for (Move move : board.getAllValidMoves(true)) {
                    ChessBoard nextMoveBoard = new ChessBoard(board, false);
                    nextMoveBoard.makeMove(move, true);
                    if (nextMoveBoard.getMoveHistoryStr().equals(nextMoveStr)) {
                        bestMove = move;
                    }
                }
            } catch (StringIndexOutOfBoundsException ex) {
                int move = miniMax(board, wishedDepth, Integer.MIN_VALUE + 100, Integer.MAX_VALUE - 100);
            }
        }
        if(bestMove==null){
            int move = miniMax(board, wishedDepth, Integer.MIN_VALUE + 100, Integer.MAX_VALUE - 100);
        }
        return bestMove;
    }

    private static int calculateCosts(ChessBoard board) {
        List<Piece> pieces = Arrays.stream(board.getBoard()).map(b -> b.getPiece()).collect(Collectors.toList());
        int costs = 0;
        double off = 1;
        for (int i = 0; i < pieces.size(); i++) {
            Piece p = pieces.get(i);
            if (!p.equals(Piece.NO)) {
                ChessPiece[] chess = board.getBoard();
                if (wishedDepth % 2 == 0) {
                    costs += p.getAiValue() + ((p.getHeatMap()[i] + 1) * off);
                } else {
                    costs -= p.getAiValue() + ((p.getHeatMap()[i] + 1) * off);
                }
            }
        }
        return (int) (costs);
    }

    private static int eval = 0;

    public static int miniMax(ChessBoard board, int depth, int alpha, int beta) {
        if (depth == 0) {
            eval++;
            return calculateCosts(board);
        }

        int maxValue = alpha;
        List<Move> moves = board.getAllValidMoves(true);
        if (moves.size() == 0) {
            return (board.isWhiteTurn()) ? -8000 : 8000;
        }
        moves = orderMoves(moves, board);
        if (depth == wishedDepth) {
            //System.out.println(moves);
        }
        for (Move move : moves) {
            ChessBoard nextMove = new ChessBoard(board);
            nextMove.makeMove(move, true);
            nextMove.changeTurn();
            int cost = -miniMax(new ChessBoard(nextMove), depth - 1, beta * -1, -1 * maxValue);

            if (cost > maxValue) {
                maxValue = cost;
                if (depth == wishedDepth) {
                    bestMove = move;
                }
                if (maxValue >= beta) {
                    break;
                }
            }
        }
        return maxValue;
    }


    private static int searchCaptures(ChessBoard board, int alpha, int beta) {
        int costs = calculateCosts(board);
        if (costs >= beta) {
            return beta;
        }

        List<Piece> pieces = Arrays.stream(board.getBoard()).map(p -> p.getPiece()).collect(Collectors.toList());
        alpha = Math.max(alpha, costs);
        List<Move> moves = board.getAllValidMoves(true).stream()
                .filter(m -> (pieces.get(m.getTargetPos()).getColor() == 'w')).collect(Collectors.toList());
        for (Move move : moves) {
            ChessBoard nextMove = new ChessBoard(board);
            nextMove.makeMove(move, false);

            costs = -searchCaptures(new ChessBoard(nextMove, false), -beta, -alpha);

            if (costs >= beta) {
                return beta;
            }
            alpha = Math.max(alpha, costs);
        }
        return alpha;
    }

    public static List<Move> orderMoves(List<Move> moves, ChessBoard board) {
        for (Move move : moves) {
            Piece start = board.getBoard()[move.getStartPos()].getPiece();
            Piece target = board.getBoard()[move.getTargetPos()].getPiece();
            int score = 0;
            if (!target.equals(Piece.NO)) {
                //System.out.println(Math.abs(target.getAiValue()) +" "+ Math.abs(start.getAiValue()));
                score += Math.abs(target.getAiValue()) - Math.abs(start.getAiValue());
                //System.out.println(move.toStringAi());
            }
            move.setAiScore(score);
        }
        if (board.isWhiteTurn()) {
            moves.sort((o1, o2) -> -1 * (o1.getAiScore() > o2.getAiScore() ? -1 : o1.getAiScore() < o2.getAiScore() ? 1 : 0));
        } else {
            moves.sort((o1, o2) -> (o1.getAiScore() > o2.getAiScore() ? -1 : o1.getAiScore() < o2.getAiScore() ? 1 : 0));
        }
        //System.out.println(moves);
        //System.out.println(moves.get(0).getAiScore() + " " + moves.get(moves.size()-1).getAiScore());
        return moves;
    }

    public static void main(String[] args) {
        List<Integer> test = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            int val;
            if (i % 2 == 0) {
                val = -i;
            } else {
                val = i;
            }
            test.add(val);
        }
        test.sort((o1, o2) -> o1 > o2 ? -1 : o1 < o2 ? 1 : 0);
        System.out.println(test);
    }
}