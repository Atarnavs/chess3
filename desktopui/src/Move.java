public class Move {
    public Square starting_square;
    public Square ending_square;
    Integer piece_type;
    public Move() {
        starting_square = new Square();
        ending_square = new Square();
    }
}
