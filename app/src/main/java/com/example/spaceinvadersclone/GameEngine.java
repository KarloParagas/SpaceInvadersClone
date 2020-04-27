package com.example.spaceinvadersclone;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
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

    //The bullet object
    private Bullet bullet;

    //Bricks that the player can use for defensive cover
    private Brick[] bricks = new Brick[500];
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
        player = new Player(context, screenX, screenY);

        //Create the player's bullet
        bullet = new Bullet(screenY);

        //Create the enemyBullets array
        for (int i = 0; i < enemyBullets.length; i++) {
            enemyBullets[i] = new Bullet(screenY);
        }

        //Create an army of enemies
        numEnemies = 0;
        int maxRow = 5;
        int maxColumn = 6;
        for (int row = 0; row < maxRow; row++) {
            for (int col = 0; col < maxColumn; col++) {
                enemies[numEnemies] = new Enemy(context, row, col, screenX, screenY);
                numEnemies++;
            }
        }

        //Create the defensive covers for the player
        numBricks = 0;
        int maxGroupNumber = 4;
        int maxBrickRow = 5;
        int maxBrickColumn = 10;
        for (int groupNumber = 0; groupNumber < maxGroupNumber; groupNumber++) {
            for (int row = 0; row < maxBrickRow; row++) {
                for (int col = 0; col < maxBrickColumn; col++) {
                    bricks[numBricks] = new Brick(row, col, groupNumber, screenX, screenY);
                    numBricks++;
                }
            }
        }
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
        boolean hasEnemyCollidedWithScreen = false;

        //Keep track if the player has lost
        boolean didLose = false;

        //Move the player
        player.update(fps);

        //Update the enemies if they're visible
        for (int i = 0; i < numEnemies; i++) {
            //If the enemies are alive
            if (enemies[i].getEnemyLifeStatus()) {
                //Move the enemy
                enemies[i].update(fps);

                //If the enemy wants to shoot the player
                if (enemies[i].shootPlayer(player.getX(), player.getHitBoxLength())) {
                    //If the enemy shoots
                    if (enemyBullets[nextBullet].shoot(enemies[i].getEnemyX() + enemies[i].getHitBoxLength() / 2, enemies[i].getEnemyY(), bullet.DOWN)) {
                        //Increment bullets fired
                        nextBullet++;

                        //If the max number of enemy bullets have been reached
                        if (nextBullet == maxEnemyBullets) {
                            //Reset back to 0
                            //This stops the spamming of bullets until one completes it's trajectory
                            //Note: If bullet 0 is active, the shoot() method returns false
                            nextBullet = 0;
                        }
                    }
                }

                //If the enemy collides with the screen
                if (enemies[i].getEnemyX() > screenX - enemies[i].getHitBoxLength() || enemies[i].getEnemyX() < 0) {
                    hasEnemyCollidedWithScreen = true;
                }
            }
        }

        //Update the player's bullet
        if (bullet.getBulletState()) { //If the player has fired a bullet
            bullet.update(fps);
        }

        //Update all of the enemy's bullets if they're active
        for (int i = 0; i < enemyBullets.length; i++) {
            if (enemyBullets[i].getBulletState()) {
                enemyBullets[i].update(fps);
            }
        }

        //If an enemy collided with the sides of the screen
        if (hasEnemyCollidedWithScreen) {
            for (int i = 0; i < numEnemies; i++) {
                enemies[i].changeDirection();

                //Detect if any enemies have entered the bottom 10th of the screen, the player lost
                if (enemies[i].getEnemyY() > screenY - screenY / 10) {
                    didLose = true;
                }
            }
        }

        //If the player collides with the sides of the screen
        //If the player collides with the screen
        if (player.getPlayerHitBox().right > screenX || player.getPlayerHitBox().left < 0) {
            player.setPlayerMovement(0);
        }


        //If the player has 0 lives left
        if (didLose) {
            //Restart the game
            startLevel();
        }

        /////Check for collision between objects\\\\\
        //Keep track if the player's bullet have collided with the top of the screen
        if (bullet.getBulletImpactY() < 0) { //If the top of the bullet is less than 0, then it's left the screen
            bullet.setBulletInactive(); //Set the bullet to inactive
        }

        //Keep track if the enemy's bullet have collided with the bottom of the screen
        for (int i = 0; i < enemyBullets.length; i++) { //Loop through each individual enemy bullet
            if (enemyBullets[i].getBulletImpactY() > screenY) { //If the bullet has gone beyond the device's screen y, then it's left the screen
                enemyBullets[i].setBulletInactive(); //Set the bullet to inactive
            }
        }

        //Keep track if the player's bullet have collided with an enemy
        if (bullet.getBulletState()) { //Get the bullet's status
            for (int i = 0; i < numEnemies; i++) { //Loop through each enemies in the array
                if (enemies[i].getEnemyLifeStatus()) { //If the enemy is alive
                    if (RectF.intersects(bullet.getBullet(), enemies[i].getEnemyHitBox())) { //If the bullet's hit box collides with an enemy's hit box
                        enemies[i].destroyEnemy(); //Destroy that enemy
                        bullet.setBulletInactive(); //Set the bullet to inactive
                        score += 10; //Player get's 10 points

                        //Check if the player has won, restart the game
                        if (score == numEnemies * 10) {
                            isPaused = true;
                            score = 0;
                            playerLives = 3;
                            startLevel();
                        }
                    }
                }
            }
        }

        //Keep track if an enemy's bullet have collided with a brick cover
        for (int i = 0; i < enemyBullets.length; i++) { //Loop through each of the enemy bullets array
            if (enemyBullets[i].getBulletState()) { //If the bullet is active
                for (int j = 0; j < numBricks; j++) { //Loop through each bullet
                    if (bricks[j].getBrickVisibility()) { //If that brick in the array is visible
                        if (RectF.intersects(enemyBullets[i].getBullet(), bricks[j].getBrickHitBox())) { //If the enemy bullet collides with a brick
                            enemyBullets[i].setBulletInactive(); //Set that enemy bullet to inactive
                            bricks[j].setBrickToInvisible(); //Set the brick to invisible, destroy the brick
                        }
                    }
                }
            }
        }

        //Keep track if an player's bullet have collided with a brick cover
        if (bullet.getBulletState()) { //If the bullet is active
            for (int i = 0; i < numBricks; i++) { //Loop through each of the bricks in the array
                if (bricks[i].getBrickVisibility()) { //If the brick is visible
                    if (RectF.intersects(bullet.getBullet(), bricks[i].getBrickHitBox())) { //If the bullet collides with a brick
                        bullet.setBulletInactive(); //Set the bullet to inactive
                        bricks[i].setBrickToInvisible(); //Set the brick to invisible
                    }
                }
            }
        }

        //Keep track if the enemy's bullet have collided with the player
        for (int i = 0; i < enemyBullets.length; i++) { //Loop through each of the enemy's bullets in the array
            if (enemyBullets[i].getBulletState()) { //If the enemy bullet is active
                if (RectF.intersects(player.getPlayerHitBox(), enemyBullets[i].getBullet())) { //If the enemy's bullet collides with the player
                    enemyBullets[i].setBulletInactive(); //Set the bullet to inactive
                    playerLives--; //Decrement the player's lives

                    //If the player has 0 lives left, the game is over
                    if (playerLives == 0) {
                        isPaused = true;
                        playerLives = 3;
                        score = 0;
                        startLevel();
                    }
                }
            }
        }
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
            canvas.drawColor(Color.argb(255, 35, 35, 35));

            //Change the brush color
            paint.setColor(Color.argb(255, 0, 255, 0));

            //Draw the player
            //Note: drawBitmap(bitmap from the player object, x start position from the player object,
            //                 y start position from the player object, paint the player object)
            canvas.drawBitmap(player.getBitmap(), player.getX(), screenY - player.getHitBoxHeight(), paint);

            //Draw the enemy
            for (int i = 0; i < numEnemies; i++) {
                //if the enemy is alive
                if (enemies[i].isEnemyAlive) {
                    canvas.drawBitmap(enemies[i].getEnemyBitmap1(), enemies[i].getEnemyX(), enemies[i].getEnemyY(), paint);
                }
            }

            //Draw the bricks if they're visible
            for (int i = 0; i < numBricks; i++) {
                if (bricks[i].getBrickVisibility()) {
                    canvas.drawRect(bricks[i].getBrickHitBox(), paint);
                }
            }

            //Change the brush color
            paint.setColor(Color.argb(255, 255, 187, 0));

            //Draw the player's bullets, if they're active
            if (bullet.getBulletState()) {
                canvas.drawRect(bullet.getBullet(), paint);
            }

            //Draw the enemy's bullets, if they're active
            for (int i = 0; i < enemyBullets.length; i++) {
                if (enemyBullets[i].getBulletState()) {
                    canvas.drawRect(enemyBullets[i].getBullet(), paint);
                }
            }

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
                isPaused = false;

                //If the player touches the lower 8th of the screen
                //That's when the player will moving left or right
                if (motionEvent.getY() > screenY - screenY /8) {
                    //If the player touches the "positive" half side of the screen,
                    //that means the player wants to go right
                    if (motionEvent.getX() > screenX / 2) {
                        player.setPlayerMovement(player.RIGHT);
                    }
                    else { //Else, the player must be touching the "negative" half side of the screen
                        player.setPlayerMovement(player.LEFT);
                    }
                }

                //If the player touches ABOVE the lower 8th of the screen, shoot a bullet
                if (motionEvent.getY() < screenY - screenY / 8) {
                    //Call bullet's shoot method
                    bullet.shoot(
                            //Player object's x coordinate/position + the center of the player object (getLength() / 2) which will cause the bullet to come out from the center
                            player.getX() + player.getHitBoxLength() / 2,
                            screenY, //Player object's current Y position
                            bullet.UP //Trajectory of the bullet
                    );
                }
                break;

            //If the player takes their finger off of the screen
            case MotionEvent.ACTION_UP:
                //If the player has removed their finger off from the area of the screen at the bottom 10th
                if (motionEvent.getY() > screenY - screenY / 10) {
                    player.setPlayerMovement(player.STOP);
                }
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
