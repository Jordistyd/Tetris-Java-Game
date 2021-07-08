package com.jordistyd;

import com.jordistyd.Shape.Tetrominoe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


/**
 * Class to initiate board and game screen settings
 * Utilizes the swing java framework for building GUIs
 *@author Yarden Galon
 *@version 27-06-2021
 */
public class Board extends JPanel {

    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 22;
    private final int PERIOD_INTERVAL = 300;    // Determine constant falling speed

    // Set board status
    private Timer timer;
    private boolean isDoneFalling = false;
    private boolean isPaused = false;   //check game status
    private int numRemovedLines = 0;    //score updater
    private int currentX = 0;
    private int currentY = 0;
    private JLabel statusBar;
    private Shape currentPiece;
    private Tetrominoe[] board;

    /**
     * Board constructor, initializes board using an  initialize method
     * @param parent A Tetris object to initialize on the board
     */
    public Board(Tetris parent){
        initBoard(parent);
    }

    /**
     *Method to initialize the board.
     * @param parent A tetris object to initialize on the board
     */
    private void initBoard(Tetris parent){
        setFocusable(true);
        statusBar = parent.getStatusBar();
        addKeyListener(new TAdapter());
    }
    /**
     * Method to determine width of tetris piece in comparison to
     * the board size
     */
    private int unitWidth(){
        return (int) getSize().getWidth() / BOARD_WIDTH;
    }
    /**
     * Method to determine width of tetris piece in comparison to
     * the board size
     */
    private int unitHeight(){
        return (int) getSize().getHeight() / BOARD_HEIGHT;
    }
    /**
     * Method to get current unit at specified position
     * @param x the X coordinate
     * @param y the Y coordinate
     */
    private Tetrominoe shapeAt(int x, int y){
        return board[(y * BOARD_WIDTH) + x];
    }
    /**
     * Method to start new game
     */
    public void start(){
        currentPiece = new Shape();
        board = new Tetrominoe[BOARD_WIDTH * BOARD_HEIGHT];

        clearBoard();
        newUnit();

        timer = new Timer(PERIOD_INTERVAL, new GameCycle());
        timer.start();
    }
    /**
     * Method to pause game
     */
    private void pause(){
        isPaused = !isPaused;
        if(isPaused){
            statusBar.setText("Waiting");
        }
        else{
            statusBar.setText(String.valueOf(numRemovedLines));
        }
        repaint();
    }
    /**
     * Override method to paint components
     * @param g Graphics object to hold shape
     */
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        paintPiece(g);

    }
    /**
     * Method to draw piece on the table
     * @param g a graphic object to hold the shape
     */
    private void paintPiece(Graphics g){
        Dimension size = getSize();
        int boardCeiling = (int) size.getHeight() - BOARD_HEIGHT * unitHeight();
        for (int i = 0; i < BOARD_HEIGHT; i++){
            for (int j = 0; j < BOARD_WIDTH; j++){
                Tetrominoe piece = shapeAt(j, BOARD_HEIGHT - i - 1);

                if (piece != Tetrominoe.noShape){
                    drawSquare(g, j * unitWidth(), boardCeiling + i * unitHeight(), piece);
                }
            }
        }
        if (currentPiece.getShape() != Tetrominoe.noShape){
            for(int i = 0; i < 4; i++){
                int x = currentX + currentPiece.getX(i);
                int y = currentY - currentPiece.getY(i);
                drawSquare(g, x * unitWidth(), boardCeiling + (BOARD_HEIGHT - y - 1) * unitHeight(),
                        currentPiece.getShape());
            }
        }
    }
    /**
     * Method to Drop piece down
     */
    private void dropDown(){
        int updatedY = currentY;
        while (updatedY > 0){
            if(tryAction(currentPiece, currentX, updatedY - 1)){
                break;
            }
            updatedY--;
        }
        unitDropped();
    }

    /**
     * Method to drop piece one line
     */
    private void oneLineDown(){
        if (tryAction(currentPiece, currentX, currentY - 1)){
            unitDropped();
        }
    }
    /**
     * Method to clear board
     */
    private void clearBoard(){
        for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; i++){
            board[i] = Tetrominoe.noShape;
        }
    }
    /**
     * Method to put piece in board array
     */
    private void unitDropped(){
        for (int i = 0; i < 4; i++){
            int x = currentX + currentPiece.getX(i);
            int y = currentY - currentPiece.getY(i);
            board[(y * BOARD_WIDTH) + x] = currentPiece.getShape();
        }
        removeCompleteLines();
        if(!isDoneFalling){
            newUnit();
        }
    }
    /**
     * Method to create and draw new piece on the board
     */
    private void newUnit(){
        currentPiece.setRandomShape();
        currentX = BOARD_WIDTH / 2 + 1;
        currentY = BOARD_HEIGHT - 1 + currentPiece.minY();

        if (tryAction(currentPiece, currentX, currentY)){
            currentPiece.setShape(Tetrominoe.noShape);
            timer.stop();

            String msg = String.format("Game over :(. Score: %d", numRemovedLines);
            statusBar.setText(msg);
        }
    }
    /**
     * Method to try and move piece on board
     * @param updatedUnit Shape object to move
     * @param updatedX The X coordinate to move
     * @param updatedY The Y coordinate to move
     */
    private boolean tryAction(Shape updatedUnit, int updatedX, int updatedY) {
        for (int i = 0; i < 4; i++) {
            int x = updatedX + updatedUnit.getX(i);
            int y = updatedY - updatedUnit.getY(i);

            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {      // Move is out of bounds
                return true;
            }

            if (shapeAt(x, y) != Tetrominoe.noShape) {
                return true;
            }
        }

        // update new values
        currentPiece = updatedUnit;
        currentX = updatedX;
        currentY = updatedY;
        repaint();
        return false;
    }
    /**
     * Method to remove the full lines and update score
     */
    private void removeCompleteLines(){
        int numOfFullLines = 0;
        for(int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean lineFull = true;

            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (shapeAt(j, i) == Tetrominoe.noShape) {      // space is empty
                    lineFull = false;
                    break;
                }
            }
            if(lineFull){
                numOfFullLines++;
                for(int k = i; k < BOARD_HEIGHT - 1; k++){
                    for(int j = 0; j < BOARD_WIDTH; j++){
                        board[(k * BOARD_WIDTH) + j] = shapeAt(j, k +1);
                    }
                }
            }
        }
        if(numOfFullLines > 0) {
            // update score
            numRemovedLines += numOfFullLines;
            statusBar.setText(String.valueOf(numRemovedLines));

            // move to next shape
            isDoneFalling = true;
            currentPiece.setShape(Tetrominoe.noShape);
        }
    }
    /**
     * Method to draw shape,
     * each shape is built from a different colored square.
     * @param g Graphics object to represent square
     * @param x The x coordinate of object
     * @param y The y coordinate of object
     * @param unit Tetrominoe object to update
     */
    private void drawSquare(Graphics g, int x, int y, Tetrominoe unit){
        // create array with different colors
        Color[] colors = {new Color(0, 0, 0), new Color(204, 102, 102),
                new Color(102, 204, 102), new Color(102, 102, 204),
                new Color(204, 204, 80), new Color(204, 102, 20),
                new Color(102, 50, 204), new Color(255, 10,0)
        };
        Color color = colors[unit.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, unitWidth() - 2, unitHeight() -2);

        g.setColor(color.brighter());
        g.drawLine(x, y + unitHeight() - 1, x, y);
        g.drawLine(x, y, x + unitWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + unitHeight() -1,
                x + unitWidth() - 1, y + unitHeight() - 1);
        g.drawLine(x + unitWidth() - 1, y + unitHeight() -1,
                x + unitWidth() - 1, y + 1);
    }
    /**
     * Method to invoke the game cycle method
     */
    private class GameCycle implements ActionListener{
        // Must override for implementation
        @Override
        public void actionPerformed(ActionEvent event){
            playGameCycle();
        }
    }
    /**
     * Method to create a new game cycle
     */
    private void playGameCycle(){
        update();
        repaint();
    }
    /**
     * Method to update screen.
     * every new fall screen is updated
     */
    private void update(){
        if(isPaused){
            return;
        }
        if(isDoneFalling){
            isDoneFalling = false;
            newUnit();
        }
        else{
            oneLineDown();
        }
    }
    /**
     * Method to check for key events.
     * extends KeyAdapter class
     */
    public class TAdapter extends KeyAdapter {
        // must implement because of extension
        @Override
        public void keyPressed(KeyEvent key){
            if(currentPiece.getShape() == Tetrominoe.noShape){
                return;
            }
            int keyCode = key.getKeyCode();

            switch (keyCode){
                case KeyEvent.VK_P:
                    pause();
                    break;
                case KeyEvent.VK_LEFT:
                    tryAction(currentPiece, currentX - 1, currentY);
                    break;
                case KeyEvent.VK_RIGHT:
                    tryAction(currentPiece, currentX + 1, currentY);
                    break;
                case KeyEvent.VK_DOWN:
                    tryAction(currentPiece.rotateRight(), currentX, currentY);
                    break;
                case KeyEvent.VK_UP:
                    tryAction(currentPiece.rotateLeft(), currentX, currentY);
                    break;
                case KeyEvent.VK_SPACE:
                    dropDown();
                    break;
                case KeyEvent.VK_D:
                    oneLineDown();
                    break;
            }
        }
    }
}

