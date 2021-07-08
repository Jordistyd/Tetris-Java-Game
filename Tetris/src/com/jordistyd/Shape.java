package com.jordistyd;
import java.util.Random;

/**
 * This class will represent the characteristics of
 * a shape in a basic tetris game.
 *
 * @author Yarden Galon
 * @version 27-06-2021
 */
public class Shape {

    protected enum Tetrominoe {      // Group shapes together
        noShape, zShape, sShape,
        lineShape, tShape, squareShape,
        lShape, mirrorLShape
    }

    private Tetrominoe unitShape;
    private int [][] coordinates;

    /**
     * Basic shape constructor, sets the coordinates of an empty
     * shape and creates it using setShape method
     */
    public Shape(){
        coordinates = new int[4][2];
        setShape(Tetrominoe.noShape);
    }

    /**
     * set method for tetris piece, sets a specific tetris piece on the board
     * @param shape the piece to be set
     */
    void setShape(Tetrominoe shape){
        int [][][] coordinateTable = new int[][][]{
                {{0,0}, {0,0}, {0,0}, {0,0}},
                {{0,-1}, {0,0}, {-1,0}, {-1,1}},
                {{0,-1}, {0,0}, {1,0}, {1,1}},
                {{0,-1}, {0,0}, {0,1}, {0,2}},
                {{-1,0}, {0,0}, {1,0}, {0,1}},
                {{0,0}, {1,0}, {0,1}, {1, 1}},
                {{-1,-1}, {0,-1}, {0,0}, {0, 1}},
                {{1,-1}, {0,-1}, {0,0}, {0,1}}
        };


        // tetris coordinates builder
        for (int i = 0; i < 4; i++){
            System.arraycopy(coordinateTable[shape.ordinal()], 0, coordinates, 0, 4);
        }
        unitShape = shape;
    }
    /**
     * set method for X coordinate of the tetris piece
     * @param index int representing current row
     * @param x the coordinate to be set
     */
    private void setX(int index, int x){
        coordinates[index][0] = x;
    }
    /**
     * set method for Y coordinate of the tetris piece
     * @param index int representing current row
     * @param y the coordinate to be set
     */
    private void setY(int index, int y){
        coordinates[index][1] = y;
    }
    /**
     * get method for x
     * @param index int for row to be checked
     * @return x the coordinate
     */
    public int getX(int index){
        return coordinates[index][0];
    }
    /**
     * get method for y
     * @param index int for the row to be checked
     * @return y the coordinate
     */
    public int getY(int index){
        return coordinates[index][1];
    }
    /**
     * get method to return full piece
     * @return the full coordinates of the shape
     */
    public Tetrominoe getShape(){
        return unitShape;
    }
    /**
     * method to create a random shape and set it
     */
    public void setRandomShape() {
        Random rand = new Random();
        int x = Math.abs(rand.nextInt()) % 7 + 1;

        Tetrominoe[] values = Tetrominoe.values();
        setShape(values[x]);
    }
    /**
     * method to retrieve the minimum X bounds
     * @return int the minimum Y coordinate
     */
    public int minX(){
        int min = coordinates[0][0];
        for (int i = 0; i < 4; i ++){
            min = Math.min(min, coordinates[i][0]);
        }
        return min;
    }
    /**
     * method to retrieve the minimum Y bounds
     * @return int the minimum Y coordinate
     */
    public int minY(){
        int min = coordinates[0][1];
        for (int i = 0; i < 4; i++){
            min = Math.min(min, coordinates[i][1]);
        }
        return min;
    }
    /**
     * Method to rotate the shape counter-clockwise
     */
    public Shape rotateLeft(){
        if (unitShape == Tetrominoe.squareShape) {       // square doesn't rotate
            return this;
        }
        Shape result = new Shape();
        result.unitShape = unitShape;
        for (int i = 0; i < 4; i++){
            result.setX(i, getY(i));
            result.setY(i, -getX(i));
        }
        return result;
    }
    /**
     * Method to rotate the shape clockwise
     */
    public Shape rotateRight(){
        if (unitShape == Tetrominoe.squareShape) {       // square doesn't rotate
            return this;
        }
        Shape result = new Shape();
        result.unitShape = unitShape;
        for(int i = 0; i < 4; i++){
            result.setX(i, -getY(i));
            result.setX(i, getX(i));
        }
        return result;
    }
}
