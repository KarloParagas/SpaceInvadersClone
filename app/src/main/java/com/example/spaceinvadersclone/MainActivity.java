package com.example.spaceinvadersclone;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class MainActivity extends AppCompatActivity {

    //This field is the game view that will contain all of the logic
    GameEngine gameEngine;

    /**
     * This method is called when the application starts
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get the device's screen details
        Display display = getWindowManager().getDefaultDisplay();

        //Load the resolution into a point object
        Point size = new Point();
        display.getSize(size);

        //Initialize the game view and set it as the view
        gameEngine = new GameEngine(this, size.x, size.y); //Parameters get a hold of the device's screen data
        setContentView(gameEngine);
    }

    /**
     * This method is executed when the player starts the game
     */
    @Override
    protected void onResume() {
        super.onResume();

        //Execute the game view's resume method
        gameEngine.resume();
    }

    /**
     * This method is executed when the player quits the game
     */
    @Override
    protected void onPause() {
        super.onPause();

        //Execute the game view's pause method
        gameEngine.pause();
    }
}
