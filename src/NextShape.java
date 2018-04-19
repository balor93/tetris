

import java.awt.Color;
import java.awt.Graphics;
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
public class NextShape extends JPanel {
    
    private Shape nextShape;
    
    public NextShape(){
        nextShape= new Shape();
    }
    
    public Shape getNextShape(){
        return nextShape;
    }
    
    public void generateNextShape(){
        nextShape= Shape.getRandomShape();
        repaint();
    }
    
   @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (nextShape != null) {
            nextShape.draw(g, 1, 1, squareWidth(), squareHeight());
        }
        

    }

    private int squareWidth() {
        return getWidth() / 4; //4 va a ser de 4*4
    }

    private int squareHeight() {
        return getHeight() / 4;
    }
    
    
    
    
    
}
