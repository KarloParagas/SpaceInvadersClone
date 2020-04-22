package com.example.spaceinvadersclone;

import android.graphics.Rect;
import android.graphics.RectF;

public class Bullet {
    //Bullet object
    private RectF bullet;

    //Represents the horizontal and vertical position of the bullet
    private float x;
    private float y;

    //Direction of the bullet
    public final int UP = 0;
    public final int DOWN = 1;

    //Trajectory of the bullet
    int trajectory = -1;

    //Bullet speed
    float speed = 350;

    //Dimensions of the bullet
    private int width = 1;
    private int height;

    //Keep track if the bullet has been shot/destroyed
    private boolean isBulletActive;

    public Bullet(int screenY) {
        //The bullet's height will be 1/20th of the length of the screen's y
        height = screenY / 20;

        //Initialize the boolean to false because no bullets will be shot
        //just yet when a bullet object is created
        isBulletActive = false;

        bullet = new RectF();
    }

    /**
     * This method will return the current bullet object to be used for
     * collision detection, etc in another class.
     */
    public RectF getBullet() {
        return bullet;
    }

    /**
     * Returns the current state/status of the bullet.
     */
    public boolean getBulletState() {
        return isBulletActive;
    }

    /**
     * Sets the bullet object to off once it has collided, or goes off screen.
     */
    public void setBulletInactive() {
        isBulletActive = false;
    }

    /**
     * This method will get the bullet's point of impact.
     * If the bullet is heading down the y axis, the point of impact needs to be at the bottom of the bullet.
     * If the bullet is heading up the y axis, the point of impact needs to be at the top of the bullet.
     */
    public float getBulletImpactY() {
        if (trajectory == DOWN) {
            return y + height;
        }
        else {
            return y;
        }
    }

    /**
     * This method will inform the class where the bullet is going to start from.
     * @param startX X coordinate of the player or enemy shooting the bullet.
     * @param startY Y coordinate of the player or enemy shooting the bullet.
     * @param direction If the bullet is coming from the player object, it goes up.
     *                  If the bullet is coming from the enemy object, it goes down.
     * @return True if the bullet has been successfully fired by the object,
     *         and false if the bullet is active.
     */
    public boolean shoot(float startX, float startY, int direction) {
        //If the bullet isn't active
        if (!isBulletActive) {
            x = startX;
            y = startY;
            trajectory = direction;
            isBulletActive = true;
            return true;
        }
        return false; //This means the bullet is active
    }

    /**
     * This updates the bullet's frame rate per second
     */
    public void update(long fps) {
        if (trajectory == UP) {
            y -= speed / fps;
        }
        else {
            y += speed / fps;
        }

        //Update the bullet object with it's new coordinates from it's movement
        //so it can be used to determine if has collided with another object
        bullet.left = x;
        bullet.right = x + width;
        bullet.top = y;
        bullet.bottom = y + height;
    }
}
