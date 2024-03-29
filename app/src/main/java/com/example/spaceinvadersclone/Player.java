package com.example.spaceinvadersclone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

public class Player {
    //Player object which acts as the hit box
    RectF playerHitBox;

    //The player avatar will be represented by a Bitmap
    //https://developer.android.com/reference/android/graphics/Bitmap.html
    private Bitmap bitmap;

    //Dimensions of the player hit box
    private float hitBoxLength;
    private float hitBoxHeight;

    //The horizontal and vertical coordinate of the player object/hit box
    private float x;
    private float y;

    //The player avatar's speed in pixels per second
    private float playerSpeed;

    //Directions the player can move
    public final int LEFT = 1;
    public final int RIGHT = 2;
    public final int STOP = 0;

    //Keep track of the player's movement
    private int playerMovement = STOP;

    /**
     * This constructor is created when "new Player()" is called.
     * @param context current state of the application/object
     * @param screenX comes from the GameEngine class' when a player object is created
     * @param screenY comes from the GameEngine class' when a player object is created
     */
    public Player(Context context, int screenX, int screenY) {
        //Initialize the player hit box
        playerHitBox = new RectF();

        hitBoxLength = screenX / 10;
        hitBoxHeight = screenY / 10;

        //Start the player in the center of the screen
        x = screenX / 2;
        y = screenY - hitBoxHeight;

        //Initialize the bitmap
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.playeravatar);

        //Adjust the bitmap size appropriate for the screen resolution
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) hitBoxLength, (int) hitBoxHeight, false);

        //Initialize the player's speed
        playerSpeed = 350;
    }

    /**
     * Getter method so other classes can use the player object
     * @return a copy of the player object
     */
    public RectF getPlayerHitBox() {
        return playerHitBox;
    }

    /**
     * Getter method so other classes can use the player avatar bitmap
     * @return a copy of the bitmap
     */
    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * Getter method to access the current value stored in the x field
     */
    public float getX() {
        return x;
    }

    /**
     * Getter method to access the current value stored in the height field
     */
    public float getHitBoxHeight() {
        return hitBoxHeight;
    }

    /**
     * Getter method to access the current value stored in the length field
     */
    public float getHitBoxLength() {
        return hitBoxLength;
    }

    /**
     * This method is used to change or set the player's movement
     */
    public void setPlayerMovement(int direction) {
        playerMovement = direction;
    }

    /**
     * This updates the player frame rate per second
     */
    public void update(long fps) {
        if (playerMovement == LEFT) {
            x -= playerSpeed / fps; //Divided with fps to make sure the movement is consistent and smooth
        }
        if (playerMovement == RIGHT) {
            x += playerSpeed / fps; //Divided with fps to make sure the movement is consistent and smooth
        }

        //Update the player object with it's new coordinates from it's movement
        //so it can be used to determine if the player has collided with another object
        playerHitBox.top = y;
        playerHitBox.bottom = y + hitBoxHeight;
        playerHitBox.left = x;
        playerHitBox.right = x + hitBoxLength;
    }
}
