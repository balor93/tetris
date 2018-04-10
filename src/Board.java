
import javax.swing.JPanel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alu20909379x
 */
public class Board extends JPanel{
    
    public static final int NUM_ROWS =22;
    public static final int NUM_COLS=10;
    
    private Tetrominoes [][] matrix;
    private int deltaTime;
    private Shape currentShape;
    private int currentRow;
    private int currentCol;
    
    public Board(){
        super();
        matrix= new Tetrominoes[NUM_ROWS][NUM_COLS];
        for(int row =0; row<NUM_ROWS;row++){
            for(int col =0;col<NUM_COLS;col++){
                matrix[row][col]=Tetrominoes.NoShape;
            }
        }
        deltaTime=500;
        currentShape= new Shape();
        currentRow=0;
        currentCol=NUM_COLS/2;
    }
    
    
    
}
