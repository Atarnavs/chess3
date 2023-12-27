import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Board extends JFrame{
    GameConnector position;
    private int clickCounter = 0;
    private int xStart = 0;
    private int yStart = 0;
    private int xEnd = 0;
    private int yEnd = 0;
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
    public int[] getMove() {
        return new int[] {xStart, yStart, xEnd, yEnd};
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
                int[] move = position.returnEngineMove();
                System.out.println(move[0] + " " + move[1] + " " + move[2] + " " + move[3] + " " + move[4]);
                makeMove(move[0], move[1], move[2], move[3], move[4]);
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
    public void makeMove (int xStart, int yStart, int xEnd, int yEnd, int pawn) {
        boolean[] moveLegality = position.ruleCheck(xStart, yStart, xEnd, yEnd);

        if (moveLegality[0]) {

            if (moveLegality[1] && pawn == -1) {
                if (position.isSideMove()) {
                    JOptionPane.showOptionDialog(chessBoard, "", "Chose pieces", JOptionPane.DEFAULT_OPTION, -1, Icons.whiteKing, promotionChoices, null);
                }
                else {
                    JOptionPane.showOptionDialog(chessBoard, "", "Chose pieces", JOptionPane.DEFAULT_OPTION, -1, Icons.blackKing, promotionChoicesBlack, null);
                }

            }
            else {
                this.pawn = pawn;
            }
            if (engineEnabled) {
                if (!position.isEngineMove()) {
                    position.makeMove(xStart, yStart, xEnd, yEnd, pawn);
                }
            }
            position.makeMove(xStart, yStart, xEnd, yEnd, this.pawn);
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
                        if (clickCounter == 1) {
                            xStart = j;
                            move.starting_square.x = j;
                            yStart = i;
                            move.starting_square.y = i;
                            if (board[i][j] == ' ') {clickCounter = 0;}
                        } else {
                            xEnd = j;
                            move.ending_square.x = j;
                            yEnd = i;
                            move.ending_square.y = i;
                            makeMove(xStart, yStart, xEnd, yEnd, -1);
                            position.debugPrint();

                            return;
                        }
                    }
                }
            }
        }
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
            if (source == bottomButtons[3]) {
                engineEnabled = false;
            }
        }
    }
    private void setPieces(String FEN) {
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
    private void setBoard(String FEN) {
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
}
