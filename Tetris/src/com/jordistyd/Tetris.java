package com.jordistyd;

import javax.swing.*;
import java.awt.*;

/**
 * A class to represent the
 * classic tetris game using java.
 *@author Yarden Galon
 *@version 08-07-2021
 */
public class Tetris extends JFrame {
    private JLabel statusBar;

    /**
     * Empty constructor, simply class
     * the startGame method to start
     * the game
     */
    public Tetris(){
        startGame();
    }
    /**
     * Method to start the game.
     */
    private void startGame(){
        // set up the status bar
        statusBar = new JLabel(" 0");
        add(statusBar, BorderLayout.SOUTH);

        // set up game board
        Board board = new Board(this);
        add(board);
        board.start();

        setTitle("Tetris");
        setSize(200, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    /**
     * Get method for the status bar
     * @return status bar, a JLabel object
     */
    public JLabel getStatusBar(){
        return statusBar;
    }
    /**
     * Main method
     */
    public static void main(String[] args){
        EventQueue.invokeLater(() ->{
            Tetris game = new Tetris();
            game.setVisible(true);
        });
    }



}
