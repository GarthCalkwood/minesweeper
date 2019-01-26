package com.example.garthcalkwood.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;

/**
 * StartingScreen activity for the Minesweeper app
 *
 * @author Garth Calkwood
 * @version 1.0
 * @since 2019-01-01
 */
public class StartingScreen extends AppCompatActivity {

    /**
     * Initializes layout
     *
     * @param savedInstanceState bundle object containing the previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_screen);
    }

    /**
     * Ends the starting screen activity and starts the game if the start button is clicked
     *
     * @param view the view element that was clicked
     */
    public void onClick(View view){
        int id = view.getId();

        /////// make this restart the game.
        if (id == R.id.startButton){

            startActivity(new Intent(StartingScreen.this, MainActivity.class ));
            StartingScreen.this.finish();
        }

    }


}
