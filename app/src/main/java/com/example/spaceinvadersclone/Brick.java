package com.example.spaceinvadersclone;

import android.graphics.RectF;

public class Brick {
    //Brick hit box object
    private RectF BrickHitBox;

    //Keeps track if the brick is on screen
    private boolean isBrickOnScreen;

    /**
     * This constructor is executed when a class calles "new Brick()" which creates a brick
     * @param row Represents the row number within an individual defensive cover
     * @param col Represents the column number within an individual defensive cover
     * @param coverNumber Determines which cover group it belongs to (Group 1, 2, 3 or 4)
     * @param screenX Device width
     * @param screenY Device height
     */
    public Brick(int row, int col, int coverNumber, int screenX, int screenY) {
        //Set the dimensions of a single brick
        int width = screenX / 110;
        int height = screenY / 50;
        //Note: These numbers are arbitrary, adjust to preference

        //Initialize the brick
        isBrickOnScreen = true;

        //Padding between bricks
        int brickPadding = 3;

        //Number of defensive covers
        int defensePadding = screenX / 9; //Gap between each defensive brick group
        int startHeight = screenY - (screenY / 8 * 2); //Starting height of a defensive brick group
        //Note: These numbers are arbitrary, adjust to preference

        BrickHitBox = new RectF(
                col * width + brickPadding + (defensePadding * coverNumber) + defensePadding + defensePadding * coverNumber, //left horizontal position the brick
                row * height + brickPadding + startHeight, //Starting vertical position of the defensive brick
                col * width + width - brickPadding + (defensePadding * coverNumber) + defensePadding + defensePadding * coverNumber, //width of the defensive brick
                row * height + height - brickPadding + startHeight //Vertical bottom position of the defensive brick
        );
    }

    /**
     * This getter method returns a copy of the brick
     */
    public RectF getBrickHitBox() {
        return this.BrickHitBox;
    }

    /**
     * This getter method reports the brick's visibility to whoever calls it
     */
    public boolean getBrickVisibility() {
        return isBrickOnScreen;
    }

    /**
     * This setter method takes the brick off of the screen
     */
    public void setBrickToInvisible() {
        isBrickOnScreen = false;
    }
}
