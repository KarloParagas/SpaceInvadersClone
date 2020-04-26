package com.example.spaceinvadersclone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

import java.util.Random;

public class Enemy {
    //Enemy object which acts as the hit box for the enemy
    RectF enemyHitBox;

    //Declare and initialize a random field for generating random numbers
    Random random = new Random();

    //Variable to hold the enemy bitmap/image
    private Bitmap enemyBitmap1;
    private Bitmap enemyBitmap2;

    //Enemy hit box dimensions
    private float hitBoxLength;
    private float hitBoxHeight;

    //X coordinate of the enemy object
    private float x;

    //Y coordinate of the enemy object
    private float y;

    //Keep track of the enemy speed, measured in pixels per second
    private float enemySpeed;

    //Directions the enemy could go
    public final int LEFT = 1;
    public final int RIGHT = 2;

    //Direction the enemy is moving
    private int enemyMovement = RIGHT;

    //Keep track is the enemy is still alive and visible on screen
    boolean isEnemyAlive;

    /**
     * This method creates the enemy when "new Enemy()" is called
     * @param context Current game state
     * @param row The row in which the enemy will be in the group
     * @param column The column in which the enemy will be in the group
     * @param screenX comes from the GameEngine class' when an enemy object is created
     * @param screenY comes from the GameEngine class' when an enemy object is created
     */
    public Enemy(Context context, int row, int column, int screenX, int screenY) {
        //Initialize the enemy object/hit box
        enemyHitBox = new RectF();

        //Initialize the enemy hit box dimensions
        hitBoxLength = screenX / 20;
        hitBoxHeight = screenY / 20;

        //Set enemy status to alive
        isEnemyAlive = true;

        int padding = screenX / 50;

        //Initialize the x and y coordinates of the enemy hit box
        x = column * (hitBoxLength + padding);
        y = row * (hitBoxHeight + padding);

        //Initialize the bitmap/image
        enemyBitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy1);
        enemyBitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy2);

        //Scale the enemy images appropriate for screen resolution
        enemyBitmap1 = Bitmap.createScaledBitmap(
            enemyBitmap1,
            (int) hitBoxLength,
            (int) hitBoxHeight,
            false
        );

        enemyBitmap2 = Bitmap.createScaledBitmap(
                enemyBitmap2,
                (int) hitBoxLength,
                (int) hitBoxHeight,
                false
        );

        //Set the enemy speed
        enemySpeed = 60;
    }

    public RectF getEnemyHitBox() {
        return enemyHitBox;
    }

    public void destroyEnemy() {
        isEnemyAlive = false;
    }

    public boolean getEnemyLifeStatus() {
        return isEnemyAlive;
    }

    public Bitmap getEnemyBitmap1() {
        return enemyBitmap1;
    }

    public Bitmap getEnemyBitmap2() {
        return enemyBitmap2;
    }

    public float getEnemyX() {
        return x;
    }

    public float getEnemyY() {
        return y;
    }

    public float getHitBoxLength() {
        return hitBoxLength;
    }

    public void update(long fps) {
        if (enemyMovement == LEFT) {
            x -= enemySpeed / fps;
        }
        if (enemyMovement == RIGHT) {
            x += enemySpeed / fps;
        }

        //Update the enemy object with it's new coordinates from it's movement
        //so it can be used to determine if the enemy has collided with another object
        enemyHitBox.top = y;
        enemyHitBox.bottom = y + hitBoxHeight;
        enemyHitBox.left = x;
        enemyHitBox.right = x + hitBoxLength;
    }

    /**
     * This method is used to change the enemy's movement direction.
     * The enemy will move down and move to it's opposite direction.
     */
    public void changeDirection() {
        if (enemyMovement == LEFT) {
            enemyMovement = RIGHT;
        }
        else {
            enemyMovement = LEFT;
        }
        y += hitBoxHeight;
        enemySpeed *= 1f;
    }

    /**
     * This method will be used to decide if an enemy will shoot the player
     * @param playerXPosition The player's current x coordinate
     * @param playerHitBoxLength The player's hit box length
     * @return Return true or false so the fire method can know whether to shoot or not
     *         which will be in a separate method.
     */
    public boolean shootPlayer(float playerXPosition, float playerHitBoxLength) {
        //Initialize a random integer number
        int randomNum = -1;

        //If an enemy is near the player, increase the enemy's chances to shoot at the player
        //Note: This if statement detects whether or not the enemy is within the player's length
        if (playerXPosition + playerHitBoxLength > x &&
            playerXPosition + playerHitBoxLength < x + hitBoxLength ||
            (playerXPosition > x && playerXPosition < x + playerHitBoxLength)) {

            //Enemy will have a more likely of a change to shoot
            randomNum = random.nextInt(101);
            if (randomNum == 0) {
                return true;
            }
        }

        //If an enemy is not near the player, shoot randomly
        randomNum = random.nextInt(1001);
        if (randomNum == 0) {
            return true;
        }

        return false;
    }
}
