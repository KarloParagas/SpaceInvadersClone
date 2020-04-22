package com.example.spaceinvadersclone;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameEngine extends SurfaceView implements Runnable {
    //Represents the context of current state of the application/object.
    //It lets newly-created objects understand what has been going on.
    Context context;

    //Game thread
    //https://examples.javacodegeeks.com/android/core/thread/android-thread-example/
    private Thread gameThread = null;

    //Field for the surface before something is drawn
    private SurfaceHolder surfaceHolder;

    //Screen size
    private int screenX;
    private int screenY;

    //Objects for canvas and paint
    private Canvas canvas;
    private Paint paint;

    //Keep track of the game's frame rate
    private long fps;
    private long endFrame;

    //Keep track if the game is running or not
    private volatile boolean playing;

    //Keep the game paused when the app is first ran
    private boolean isPaused = true;

    //Keep track of the score
    int score = 0;

    //Keep track of player lives
    private int playerLives = 3;

    //The player object
    private Player player;

    //Bricks that the player can use for defensive cover
    private Brick[] bricks = new Brick[100];
    private int numBricks;

    //Max amount of enemies
    Enemy[] enemies = new Enemy[60];
    int numEnemies = 0;

    //The enemy bullets
    private Bullet[] enemyBullets = new Bullet[500];
    private int nextBullet;
    private int maxEnemyBullets = 10;

    /**
     * This constructor is called when the game view (MainActivity.java) initializes it
     * @param context current state of the application/object
     * @param x comes from MainActivity class' onCreate method when it initialized gameEngine
     * @param y comes from MainActivity class' onCreate method when it initialized gameEngine
     */
    public GameEngine(Context context, int x, int y) {
        //Sets up the objects contained in this class by the SurfaceView
        super(context);

        //Assign the context field so it can be used in other methods
        this.context = context;

        //Initialize the surfaceHolder field
        surfaceHolder = getHolder();

        //Initialize the paint field
        paint = new Paint();

        //Initialize the screenX and screenY screen size fields (passed from MainActivity/GameView)
        screenX = x;
        screenY = y;

        //Start the level
        startLevel();
    }

    /**
     * This method will initialize all of the game objects declared from the fields above
     */
    private void startLevel() {
        //Create a player

        //Create the player's bullet

        //Initialize the enemyBullets array

        //Create an army of enemies

        //Create the defensive covers for the player
    }

    /**
     * This method is called when the Thread is started
     */
    @Override
    public void run() {
        while (playing) {
            //Set up a starting point to calculate the frame rate
            long startFrame = System.currentTimeMillis();

            //Update the frame
            if (!isPaused) {
                update();
            }

            //Draw the frame
            draw();

            //Keep track of the total length in time from the start
            //of this method to this current point
            endFrame = System.currentTimeMillis() - startFrame;
            if (endFrame >= 1) {
                fps = 1000 / endFrame;
            }
        }
    }

    /**
     * This method is called by the run() method, which updates once per second
     */
    private void update() {
        //Check if the enemy has collided with the sides of the screen
        boolean isCollided = false;

        //Keep track if the player has lost
        boolean didLose = false;

        //Move the player

        //Update the enemies if they're visible

        //Update all of the enemy's bullets if they're active

        //Has an enemy collided with the sides of the screen

        //If the player has 0 lives left
        if (didLose) {
            //Restart the game
            startLevel();
        }

        //Update the player's bullet

        //Keep track if the player's bullet have collided with the top of the screen

        //Keep track if the enemy's bullet have collided with the bottom of the screen

        //Keep track if the player's bullet have collided with an enemy

        //Keep track if an enemy's bullet have collided with a brick cover

        //Keep track if an player's bullet have collided with a brick cover

        //Keep track if the enemy's bullet have collided with the player
    }

    /**
     * This method will draw all of the game's objects
     */
    private void draw() {
        //Makes sure that the drawing surface if valid, if not, the game crashes
        if (surfaceHolder.getSurface().isValid()) {
            //Prepare the canvas for drawing
            canvas = surfaceHolder.lockCanvas();

            //Draw the background color
            canvas.drawColor(Color.argb(255, 55, 55, 55));

            //Change the brush color
            paint.setColor(Color.argb(255, 0, 255, 0));

            //Draw the player

            //Draw the enemy

            //Draw the bricks if they're visible

            //Draw the player's bullets, if they're active

            //Draw the enemy's bullets, if they're active

            //Draw the remaining lives

            //Draw the remaining lives of the player

            //Change the brush color and set the text size
            paint.setColor(Color.argb(255, 249, 129, 0));
            paint.setTextSize(50);

            //Draw the scoreboard
            canvas.drawText("Score: " + score + "  Lives: " + playerLives, 10, 50, paint);

            //Once everything has been set up above, draw/display everything on the screen
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * This method is called whenever the player touches the screen.
     * Note: The SurfaceView class implements onTouchListener so this onTouchEvent can
     * be overridden be used to detect screen touches.
     */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            //If the player touches the screen
            case MotionEvent.ACTION_DOWN:
                break;

            //If the player takes their finger off of the screen
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    /**
     * This method is executed if the user has paused/stopped the game,
     * which will shutdown the Thread
     */
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        }
        catch (InterruptedException e) {
            Log.e("Error: ", "Joining thread");
        }
    }

    /**
     * This method executes when the user has booted up the game again,
     * which will start the Thread
     */
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}
