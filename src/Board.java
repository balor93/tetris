
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author alu20482156n
 */
public class Board extends JPanel implements ActionListener {

    class MyKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (canMoveTo(currentShape, currentRow, currentCol - 1)) {
                        currentCol--;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (canMoveTo(currentShape, currentRow, currentCol + 1)) {
                        currentCol++;
                    }
                    break;
                case KeyEvent.VK_UP:
                    Shape rotShape = currentShape.rotateRight();
                    if (canMoveTo(rotShape, currentRow, currentCol)) {
                        currentShape = rotShape;
                    }

                    break;
                case KeyEvent.VK_DOWN:
                    if (canMoveTo(currentShape, currentRow + 1, currentCol)) {
                        currentRow++;
                    }
                    break;
                case KeyEvent.VK_P:
                    if (!timer.isRunning()) {
                        scoreBoard.printScore();
                        timer.start();
                    } else {
                        timer.stop();
                        scoreBoard.pause();
                    }
                    break;
                default:
                   
                    break;
            }
           
            repaint();
        }

    }

    public static final int NUM_ROWS = 22;
    public static final int NUM_COLS = 10;

    private Tetrominoes[][] matrix;
    private int deltaTime;

    private Shape currentShape;

    private int currentCol;
    private int currentRow;

    private Timer timer;

    private MyKeyAdapter myKeyAdepter;

    public ScoreBoard scoreBoard;

    public final int INIT_ROW = -2;

    public Board() {
        super();

        matrix = new Tetrominoes[NUM_ROWS][NUM_COLS];

        initValues();
        timer = new Timer(deltaTime, this);
    }

    private void initValues() {
        setFocusable(true);

        cleanBoard();

        deltaTime = 500;
        currentShape = null;

        currentRow = INIT_ROW;
        currentCol = NUM_COLS / 2;

        myKeyAdepter = new MyKeyAdapter();

    }

    public void initGame() {
        initValues();
        timer.start();

        currentShape = new Shape(); // = Shape.getRandomShape()

        addKeyListener(myKeyAdepter);

    }

    public void setScoreBoard(ScoreBoard scoreBoard) {
        this.scoreBoard = scoreBoard;
    }

    private void cleanBoard() {
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {

                matrix[row][col] = Tetrominoes.NoShape;
            }
        }
    }

    private boolean canMoveTo(Shape shape, int newRow, int newCol) {
        if ((newCol + shape.getXmin() < 0)
                || (newCol + shape.getXmax() >= NUM_COLS)
                || (newRow + shape.getYmax() >= NUM_ROWS)
                || hitWithMatrix(shape, newRow, newCol)) {
            return false;
        }
        return true;
    }

    private boolean hitWithMatrix(Shape shape, int newRow, int newCol) {
        int[][] squaresArray = shape.getCoordinates();

        for (int point = 0; point <= 3; point++) {
            int row = newRow + squaresArray[point][1];
            int col = newCol + squaresArray[point][0];
            if (row >= 0) { //para que no se salga del indice de matrix (INIT_ROW = -2)
                if (matrix[row][col] != Tetrominoes.NoShape) {
                    return true;
                }
            }
        }
        return false;
    }

   

    //Main Game loop
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (canMoveTo(currentShape, currentRow + 1, currentCol)) {
            currentRow++;

        } else {
            if (isGameOver()) {
                gameOver();
            } else {
                moveCurrentShapeToMatrix();
                checkRow();
                currentShape = new Shape();
                currentRow = INIT_ROW;
                currentCol = NUM_COLS / 2;
                checkRow();
            }

        }
        repaint();
    }

    public boolean isGameOver() {
        int[][] squaresArray = currentShape.getCoordinates();
        for (int point = 0; point <= 3; point++) {
            int row = currentRow + squaresArray[point][1];
            if (row <= 0) {
                return true;
                
            }
        }
        return false;
    }

    public void gameOver() {
        scoreBoard.setText("game over"); 
        removeKeyListener(myKeyAdepter);
        timer.stop();
        WindowEnd d = new WindowEnd((JFrame) getParent().getParent().getParent().getParent(), true);
        d.setVisible(true);
        

    }

    private void checkRow() {
        boolean lineNoWhite = true;

        for (int i = 0; i < NUM_ROWS; i++) {
            lineNoWhite = true;
            for (int j = 0; j < NUM_COLS; j++) {
                if (matrix[i][j] == Tetrominoes.NoShape) {
                    lineNoWhite = false;
                }
            }
            if (lineNoWhite) {
                cleanRow(i);
            }
        }
    }

    private void cleanRow(int numRow) {
        for (int i = numRow; i > 0; i--) {
            for (int j = 0; j < NUM_COLS; j++) {
                matrix[i][j] = matrix[i - 1][j];
            }
        }
        scoreBoard.increment(1);
    }

    private void moveCurrentShapeToMatrix() {
        int[][] squaresArray = currentShape.getCoordinates();
        for (int point = 0; point <= 3; point++) {
            int row = currentRow + squaresArray[point][1];
            int col = currentCol + squaresArray[point][0];
            matrix[row][col] = currentShape.getShape();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        if (currentShape != null) {
            drawCurrentShape(g);
        }
        drawBorder(g);

    }

    public void drawBorder(Graphics g) {
        g.setColor(Color.red);
        g.drawRect(0, 0, NUM_COLS * squareWidth(), NUM_ROWS * squareHeight());
    }

    public void drawBoard(Graphics g) {
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {

                drawSquare(g, row, col, matrix[row][col]);
            }
        }
    }

    private void drawSquare(Graphics g, int row, int col, Tetrominoes shape) {
        Color colors[] = {new Color(0, 0, 0), new Color(204, 102, 102),
            new Color(102, 204, 102), new Color(102, 102, 204),
            new Color(204, 204, 102), new Color(204, 102, 204),
            new Color(102, 204, 204), new Color(218, 170, 0)
        };

        int x = col * squareWidth();
        int y = row * squareHeight();
        Color color = colors[shape.ordinal()];
        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);
        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);
        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
    }

    private int squareWidth() {
        return getWidth() / NUM_COLS;
    }

    private int squareHeight() {
        return getHeight() / NUM_ROWS;
    }

    private void drawCurrentShape(Graphics g) {
        int[][] squaresArray = currentShape.getCoordinates();

        for (int point = 0; point <= 3; point++) {
            drawSquare(g, currentRow + squaresArray[point][1], currentCol + squaresArray[point][0], currentShape.getShape());
        }
    }

}
