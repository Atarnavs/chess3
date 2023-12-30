import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class Board extends JFrame{
    private static final int EMPTY = 0;
    private static final int KING = 1;
    private static final int QUEEN = 2;
    private static final int ROOK = 3;
    private static final int BISHOP = 4;
    private static final int KNIGHT = 5;
    private static final int PAWN = 6;
    private static final int WHITE = 16;
    private static final int BLACK = 8;
    boolean whiteSide = true;
    GameConnector position;
    private int clickCounter = 0;
    private int pawn = -1;
    private Move move = new Move();
    private char[][] board = {
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}
    };
    private String FEN;
    private static Container chessBoard;
    private JButton[][] squares = new JButton[8][8];
    private JButton[] promotionChoices = new JButton[4];
    private JButton[] promotionChoicesBlack = new JButton[4];
    private JDialog promotionDialogue = new JDialog();
    private JDialog promotionDialogueBlack = new JDialog();
    private JPanel buttonPanel = new JPanel();
    private JButton[] bottomButtons = new JButton[4];
    //private Engine engine = new Engine();
    private Color colour = Color.DARK_GRAY;
    public static boolean ENGINE_ENABLED = true;
    public static boolean ENGINE_DISABLED = false;
    private boolean engineEnabled;
    private int AUTOQUEEN = 0;
    public Move getMove() {
        return move;
    }

    //Scanner scanner = new Scanner();
    public Board(boolean engineEnabled) {
        super("chess board");
        this.engineEnabled = engineEnabled;
        position = new GameConnector();
        FEN = position.getPieces();
        setLayout(new BorderLayout());
        chessBoard = new JPanel();
        chessBoard.setLayout(new GridLayout(8, 8));
        if (engineEnabled) {
            position.engine = true;
        }
        //promotionDialogue.setLayout(new GridLayout(4,1));
        //setLayout(new GridLayout(8, 8));
        ActionListener1 buttonHandler = new ActionListener1();
        for (int i = 7; i > -1; i--) {
            for (int j = 0; j < 8; j++) {
                squares[i][j] = new JButton();
                if ( (i + j) % 2 == 0 ) {
                    squares[i][j].setBackground(colour);
                }
                else {
                    squares[i][j].setBackground(Color.WHITE);
                }
                squares[i][j].setOpaque(true);
                squares[i][j].setBorderPainted(false);
                chessBoard.add(squares[i][j]);
                //add(squares[i][j]);
                squares[i][j].addActionListener(buttonHandler);

            }
        }
        for (int i = 0; i < 4; i++) {
            bottomButtons[i] = new JButton();
            bottomButtons[i].addActionListener(new bottomButtonsHandler());
            buttonPanel.add(bottomButtons[i]);
        }
        //bottomButtons[0].setIcon(Icons.backArrow);
        for (int i = 0; i < 4; i++) {
            promotionChoices[i] = new JButton();
            promotionChoices[i].addActionListener(new PromotionButtonHandler());
            promotionDialogue.add(promotionChoices[i]);
        }
        for (int i = 0; i < 4; i++) {
            promotionChoicesBlack[i] = new JButton();
            promotionChoicesBlack[i].addActionListener(new PromotionButtonHandler());
            promotionDialogueBlack.add(promotionChoicesBlack[i]);
        }

        promotionChoices[0].setIcon(Icons.whiteQueen);
        promotionChoices[1].setIcon(Icons.whiteRook);
        promotionChoices[2].setIcon(Icons.whiteBishop);
        promotionChoices[3].setIcon(Icons.whiteKnight);
        promotionChoicesBlack[0].setIcon(Icons.blackQueen);
        promotionChoicesBlack[1].setIcon(Icons.blackRook);
        promotionChoicesBlack[2].setIcon(Icons.blackBishop);
        promotionChoicesBlack[3].setIcon(Icons.blackKnight);

        //promotionDialogue.setSize(100, 400);
        //promotionDialogue.setName("Choose piece");

        add(chessBoard, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        chessBoard.setSize(800, 800);
        setSize(800, 820);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPieces(FEN);
        setVisible(true);
        while (this.engineEnabled) { // position.legalMoves().size() != 0
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
            System.out.println(this.engineEnabled);
            if (position.isEngineMove()) {
                Move move = position.returnEngineMove();
                System.out.println(move.starting_square.x + " " + move.starting_square.y + " " + move.ending_square.x + " " + move.ending_square.y + " " + move.piece_type);
                makeMove(move);
                position.makeMove(move);
            }
        }
    }
    private void resetPieces() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squares[i][j].setIcon(Icons.emptySquare);
            }
        }
        FEN = position.getPieces();
        setPieces(FEN);
    }
    public void makeMove (Move move) {
        System.out.println(move.starting_square.x + " " + move.starting_square.y + " " + move.ending_square.x + " " + move.ending_square.y + " ");
        String moveLegality = position.ruleCheck(move);
        if (Objects.equals(moveLegality, "PASS")) {
            //System.out.println("piece type is: " + move.piece_type);
            //System.out.println("piece type & 7 is: " + (move.piece_type & 7));
            if (((move.piece_type & 7) == 6) && (move.ending_square.y == 0 || move.ending_square.y == 7)) {
                if (position.isSideMove()) {
                    JOptionPane.showOptionDialog(chessBoard, "", "Chose a piece", JOptionPane.DEFAULT_OPTION, -1, Icons.whiteKing, promotionChoices, null);
                }
                else {
                    JOptionPane.showOptionDialog(chessBoard, "", "Chose a piece", JOptionPane.DEFAULT_OPTION, -1, Icons.blackKing, promotionChoicesBlack, null);
                }

            }
            if (engineEnabled) {
                if (!position.isEngineMove()) {
                    position.makeMove(move);
                }
            }
            position.makeMove(move);
            //position.updatePieceList();
            resetPieces();
        }
        else {
            System.out.println("Illegal move");
            //position.voidNewList();
        }
    }
    private class ActionListener1 implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e == null) {
                return;
            }
            Object source = e.getSource();
            for (int i = 7; i > -1; i--) {
                for (int j = 0; j < 8; j++) {
                    if (source == squares[i][j]) {
                        System.out.print("x = " + j + " y = " + i + "\n");
                        clickCounter = (clickCounter + 1) % 2;
                        System.out.println("clickCounter = " + clickCounter);
                        if (whiteSide) {
                            if (clickCounter == 1) {
                                move.starting_square.x = j;
                                move.starting_square.y = i;
                                if (board[i][j] == ' ') {
                                    clickCounter = 0;
                                }
                            } else {
                                move.ending_square.x = j;
                                move.ending_square.y = i;
                                move.piece_type = determineType(move.starting_square.x, move.starting_square.y);
                                makeMove(move);
                                position.debugPrint();

                                return;
                            }
                        }
                        else {
                            if (clickCounter == 1) {
                                move.starting_square.x = 7 - j;
                                move.starting_square.y = 7 - i;
                                if (board[7 - i][7 - j] == ' ') {
                                    clickCounter = 0;
                                }
                            } else {
                                move.ending_square.x = 7 - j;
                                move.ending_square.y = 7 - i;
                                move.piece_type = determineType(move.starting_square.x, move.starting_square.y);
                                makeMove(move);
                                position.debugPrint();

                                return;
                            }
                        }
                    }
                }
            }
        }
    }
    private int determineType(int x, int y) {
        char piece = board[y][x];
        int value = EMPTY;
        if (piece == ' ') {return value;}
        if (piece == 'K' || piece == 'k') {value += KING;}
        else if (piece == 'Q' || piece == 'q') {value += QUEEN;}
        else if (piece == 'R' || piece == 'r') {value += ROOK;}
        else if (piece == 'B' || piece == 'b') {value += BISHOP;}
        else if (piece == 'N' || piece == 'n') {value += KNIGHT;}
        else if (piece == 'P' || piece == 'p') {value += PAWN;}
        if (piece > 96 && piece < 123) {
            value += BLACK;
        }
        else {
            value += WHITE;
        }
        return value;
    }
    private class PromotionButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e == null) {
                return;
            }
            Object source = e.getSource();
            for (int i = 0; i < 4; i++) {
                if (source == promotionChoices[i]) {
                    pawn = i;
                    //promotionDialogue.
                }
                if (source == promotionChoicesBlack[i]) {
                    pawn = i;
                }
            }

        }
    }
    private class bottomButtonsHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e == null) {
                return;
            }
            Object source = e.getSource();
            if (source == bottomButtons[0]) {
                position.unmakeMove();
                resetPieces();
            }
            if (source == bottomButtons[2]) {
                if (whiteSide) {
                    setPiecesInReverse(FEN);
                    whiteSide = false;
                }
                else {
                    whiteSide = true;
                    setPieces(FEN);
                }
            }
            if (source == bottomButtons[3]) {
                engineEnabled = false;
            }
        }
    }
    private void setPieces(String FEN) {
        if (!whiteSide) {
            setPiecesInReverse(FEN);
            return;
        }
        for (var i = 0; i < 8; i++) {
            for (var j = 0; j < 8; j++) {
                squares[i][j].setIcon(Icons.emptySquare);
            }
        }
        System.out.println(FEN);
        setBoard(FEN);
        printBoard();
        char piece;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                piece = board[y][x];
                if(piece == 'r') {squares[y][x].setIcon(Icons.blackRook);}
                else if(piece == 'n') {squares[y][x].setIcon(Icons.blackKnight);}
                else if(piece == 'b') {squares[y][x].setIcon(Icons.blackBishop);}
                else if(piece == 'q') {squares[y][x].setIcon(Icons.blackQueen);}
                else if(piece == 'k') {squares[y][x].setIcon(Icons.blackKing);}
                else if(piece == 'p') {squares[y][x].setIcon(Icons.blackPawn);}
                else if(piece == 'P') {squares[y][x].setIcon(Icons.whitePawn);}
                else if(piece == 'K') {squares[y][x].setIcon(Icons.whiteKing);}
                else if(piece == 'Q') {squares[y][x].setIcon(Icons.whiteQueen);}
                else if(piece == 'B') {squares[y][x].setIcon(Icons.whiteBishop);}
                else if(piece == 'N') {squares[y][x].setIcon(Icons.whiteKnight);}
                else if(piece == 'R') {squares[y][x].setIcon(Icons.whiteRook);}


            }
        }
    }
    private void setPiecesInReverse(String FEN) {
        for (var i = 0; i < 8; i++) {
            for (var j = 0; j < 8; j++) {
                squares[i][j].setIcon(Icons.emptySquare);
            }
        }
        System.out.println(FEN);
        setBoard(FEN);
        printBoard();
        char piece;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                piece = board[7 - y][7 - x];
                if(piece == 'r') {squares[y][x].setIcon(Icons.blackRook);}
                else if(piece == 'n') {squares[y][x].setIcon(Icons.blackKnight);}
                else if(piece == 'b') {squares[y][x].setIcon(Icons.blackBishop);}
                else if(piece == 'q') {squares[y][x].setIcon(Icons.blackQueen);}
                else if(piece == 'k') {squares[y][x].setIcon(Icons.blackKing);}
                else if(piece == 'p') {squares[y][x].setIcon(Icons.blackPawn);}
                else if(piece == 'P') {squares[y][x].setIcon(Icons.whitePawn);}
                else if(piece == 'K') {squares[y][x].setIcon(Icons.whiteKing);}
                else if(piece == 'Q') {squares[y][x].setIcon(Icons.whiteQueen);}
                else if(piece == 'B') {squares[y][x].setIcon(Icons.whiteBishop);}
                else if(piece == 'N') {squares[y][x].setIcon(Icons.whiteKnight);}
                else if(piece == 'R') {squares[y][x].setIcon(Icons.whiteRook);}


            }
        }
    }
    private void setBoard(String FEN) {
        setEmpty();
        int x = 0;
        int y = 7;
        for (char i: FEN.toCharArray()) {
            if (i == '/') {x--; y--;}
            else if (i == ' ') {return;}
            else if (i < 57 && i > 47) {x += Integer.parseInt(String.valueOf(i)) - 1;}
            else {
                board[y][x] = i;
            }
            x = (x + 1)%8;
        }
    }
    private void printBoard() {
        for (int i = 7; i > -1; i--) {
            for (char a: board[i]) {
                System.out.print(a + "," + " ");
            }
            System.out.println();
        }
    }
    public static void main(String[] args) {
        Board chessBoard = new Board(ENGINE_DISABLED); //"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"


    }
    private void setEmpty() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = ' ';
            }
        }
    }
}
