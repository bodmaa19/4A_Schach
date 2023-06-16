package at.kaindorf.chess.core.pojos.piece;
/**
 * project name: Schach_4A
 * author: Roman Lanschützer
 * date: 24.03.2023
 */
public enum Piece {
    WK("K", new int[]{1, -1, 8, -8, 9, -9, 7, -7}, 'w', -4000, new int[]{
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            1, 1, 3, 1, 1, 1, 3, 1
    }),
    WQ("Q", new int[]{1, -1, 8, -8, 9, -9, 7, -7}, 'w', -900, new int[]{
            1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 2, 2, 2, 2, 1, 1,
            1, 1, 2, 3, 3, 2, 1, 1,
            1, 1, 2, 3, 3, 2, 1, 1,
            1, 1, 2, 2, 2, 2, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1
    }),
    WR("R", new int[]{1, -1, 8, -8}, 'w', -500, new int[]{
            1, 1, 2, 2, 2, 2, 1, 1,
            0, 0, 1, 2, 2, 1, 0, 0,
            0, 0, 1, 2, 2, 1, 0, 0,
            0, 0, 1, 2, 2, 1, 0, 0,
            0, 0, 1, 2, 2, 1, 0, 0,
            0, 0, 1, 2, 2, 1, 0, 0,
            0, 0, 1, 2, 2, 1, 0, 0,
            1, 1, 2, 2, 2, 2, 1, 1
    }),
    WN("N", new int[]{-10, 10, 6, -6, 17, -17, 15, -15}, 'w', -300, new int[]{
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 1, 1, 1, 1, 1, 1, 0,
            0, 1, 2, 2, 2, 2, 1, 0,
            0, 1, 2, 3, 3, 2, 1, 0,
            0, 1, 2, 3, 3, 2, 1, 0,
            0, 1, 2, 2, 2, 2, 1, 0,
            0, 1, 1, 1, 1, 1, 1, 0,
            0, 0, 0, 0, 0, 0, 0, 0
    }),
    WB("B", new int[]{9, -9, 7, -7}, 'w', -300, new int[]{
            2, 2, 0, 2, 2, 0, 2, 2,
            1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1,
            2, 1, 1, 3, 3, 1, 1, 2,
            2, 1, 1, 3, 3, 1, 1, 2,
            1, 1, 1, 0, 0, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1,
            2, 2, 0, 2, 2, 0, 2, 2
    }),
    WP("P", new int[]{-8, -16, -7, -9}, 'w', -100, new int[]{
            4, 4, 4, 4, 4, 4, 4, 4,
            3, 3, 3, 3, 3, 3, 3, 3,
            2, 2, 2, 3, 3, 2, 2, 2,
            2, 2, 2, 3, 3, 2, 2, 2,
            1, 1, 1, 2, 2, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0
    }),
    BK("k", new int[]{1, -1, 8, -8, 9, -9, 7, -7}, 'b', 4000, new int[]{
            1, 1, 3, 1, 1, 1, 3, 1,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0
    }),
    BQ("q", new int[]{1, -1, 8, -8, 9, -9, 7, -7}, 'b', 900, new int[]{
            1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 2, 2, 2, 2, 1, 1,
            1, 1, 2, 3, 3, 2, 1, 1,
            1, 1, 2, 3, 3, 2, 1, 1,
            1, 1, 2, 2, 2, 2, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1
    }),
    BR("r", new int[]{1, -1, 8, -8}, 'b', 500, new int[]{
            1, 1, 2, 2, 2, 2, 1, 1,
            1, 1, 3, 2, 2, 3, 1, 1,
            0, 0, 1, 2, 2, 1, 0, 0,
            0, 0, 1, 2, 2, 1, 0, 0,
            0, 0, 1, 2, 2, 1, 0, 0,
            0, 0, 1, 2, 2, 1, 0, 0,
            0, 0, 1, 2, 2, 1, 0, 0,
            1, 1, 2, 2, 2, 2, 1, 1
    }),
    BN("n", new int[]{-10, 10, 6, -6, 17, -17, 15, -15}, 'b', 300, new int[]{
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 1, 1, 1, 1, 1, 1, 0,
            0, 1, 2, 2, 2, 2, 1, 0,
            0, 1, 2, 3, 3, 2, 1, 0,
            0, 1, 2, 3, 3, 2, 1, 0,
            0, 1, 2, 2, 2, 2, 1, 0,
            0, 1, 1, 1, 1, 1, 1, 0,
            0, 0, 0, 0, 0, 0, 0, 0
    }),
    BB("b", new int[]{9, -9, 7, -7}, 'b', 300, new int[]{
            2, 2, 0, 2, 2, 0, 2, 2,
            1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1,
            2, 1, 1, 3, 3, 1, 1, 2,
            2, 1, 1, 3, 3, 1, 1, 2,
            1, 1, 1, 0, 0, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1,
            2, 2, 0, 2, 2, 0, 2, 2
    }),
    BP("p", new int[]{8, 16, 9, 7}, 'b', 100, new int[]{
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 2, 2, 1, 1, 1,
            2, 2, 2, 3, 3, 2, 2, 2,
            2, 2, 2, 2, 2, 2, 2, 2,
            3, 3, 3, 3, 3, 3, 3, 3,
            4, 4, 4, 4, 4, 4, 4, 4
    }),
    NO("", new int[]{}, ' ', 0, new int[]{});

    String fen;
    int[] moves;
    char color;
    int aiValue;
    int[] heatMap;

    public String getFen() {
        return fen;
    }

    public int[] getMoves() {
        return moves;
    }

    public char getColor() {
        return color;
    }

    public int getAiValue() {
        return aiValue;
    }

    public int[] getHeatMap() {
        return heatMap;
    }

    public PieceType getPieceType() {
        switch(fen.toLowerCase()) {
            case "p": return PieceType.Pawn;
            case "r": return PieceType.Rook;
            case "n": return PieceType.Knight;
            case "b": return PieceType.Bishop;
            case "q": return PieceType.Queen;
            case "k": return PieceType.King;
            default: return PieceType.NoPiece;
        }
    }

    public boolean isPieceType(PieceType type) {
        return getPieceType() == type;
    }

    public PieceColor getPieceColor() {
        return color == 'w' ? PieceColor.White : PieceColor.Black;
    }

    public boolean isWhitePiece() {
        return color == 'w';
    }

    public static Piece create(PieceType type, PieceColor color) {
        switch(type) {
            case Pawn: return color == PieceColor.White ? Piece.WP : Piece.BP;
            case Rook: return color == PieceColor.White ? Piece.WR : Piece.BR;
            case Knight: return color == PieceColor.White ? Piece.WN : Piece.BN;
            case Bishop: return color == PieceColor.White ? Piece.WB : Piece.BB;
            case King: return color == PieceColor.White ? Piece.WK : Piece.BK;
            case Queen: return color == PieceColor.White ? Piece.WQ : Piece.BQ;
            default: return Piece.NO;
        }
    }

    Piece(String fen, int[] moves, char color, int aiValue, int[] heatMap) {
        this.fen = fen;
        this.moves = moves;
        this.color = color;
        this.aiValue = aiValue;
        this.heatMap = heatMap;
    }
}