package at.kaindorf.chess.pojos;

public enum Piece {
    WK("K", new int[]{1, -1, 8, -8, 9, -9, 7, -7}, 'w'),
    WQ("Q", new int[]{1, -1, 8, -8, 9, -9, 7, -7}, 'w'),
    WR("R", new int[]{1, -1, 8, -8}, 'w'),
    WN("N", new int[]{-10, 10, 6, -6, 17, -17, 15, -15}, 'w'),
    WB("B", new int[]{9, -9, 7, -7}, 'w'),
    WP("P", new int[]{-8, -16, -7, -9}, 'w'),
    BK("k", new int[]{1, -1, 8, -8, 9, -9, 7, -7}, 'b'),
    BQ("q", new int[]{1, -1, 8, -8, 9, -9, 7, -7}, 'b'),
    BR("r", new int[]{1, -1, 8, -8}, 'b'),
    BN("n", new int[]{-10, 10, 6, -6, 17, -17, 15, -15}, 'b'),
    BB("b", new int[]{9, -9, 7, -7}, 'b'),
    BP("p", new int[]{8, 16, 9, 7}, 'b'),
    NO("", new int[]{}, ' ');

    String fen;
    int[] moves;
    int numberOfMoves = 0;
    char color;

    public String getFen() {
        return fen;
    }

    public int[] getMoves() {
        return moves;
    }

    public int getNumberOfMoves() {
        return numberOfMoves;
    }

    public void move(){
        numberOfMoves++;
    }

    public char getColor() {
        return color;
    }

    Piece(String fen, int[] moves, char color){
        this.fen = fen;
        this.moves = moves;
        this.color = color;
    }
}
