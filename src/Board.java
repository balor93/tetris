
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
    private NextShape nextShape;

    private int currentCol;
    private int currentRow;

    private Timer timer;

    private MyKeyAdapter myKeyAdepter;

    public ScoreBoard scoreBoard;

    public final int INIT_ROW = -2;

    public Board() {
        super();

        matrix = new Tetrominoes[NUM_ROWS][NUM_COLS];
        myKeyAdepter = new MyKeyAdapter();
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

        

    }

    public void initGame() {
        initValues();
        timer.start();
        scoreBoard.reset();
        currentShape = Shape.getRandomShape(); 
       
        removeKeyListener(myKeyAdepter);
        addKeyListener(myKeyAdepter);

    }
    
    public boolean incrementLevels(){
        if(scoreBoard.getScore()==5 ||scoreBoard.getScore()==10 || scoreBoard.getScore()==15 ){
            
            return true;
        }
        return false;        
    }
    
    public void setNextShape(NextShape sh){
       nextShape=sh;
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
                currentShape = nextShape.getNextShape();
                nextShape.generateNextShape();
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
        WindowEnd d = new WindowEnd((JFrame) getParent().getParent().getParent().getParent(), true, scoreBoard);
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
                scoreBoard.increment(1);
                if(incrementLevels()){
                    deltaTime=deltaTime-75;
                    timer.setDelay(deltaTime);
                }
            }
        }
        
    }

    private void cleanRow(int numRow) {
        for (int i = numRow; i > 0; i--) {
            for (int j = 0; j < NUM_COLS; j++) {
                matrix[i][j] = matrix[i - 1][j];
            }
        }
        
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
            currentShape.draw(g, currentRow, currentCol, squareWidth(), squareHeight());
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

                Util.drawSquare(g, row, col, matrix[row][col], squareWidth(), squareHeight());
            }
        }
    }

   

    private int squareWidth() {
        return getWidth() / NUM_COLS;
    }

    private int squareHeight() {
        return getHeight() / NUM_ROWS;
    }

    
    
   

}
