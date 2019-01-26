package com.example.garthcalkwood.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * Victory activity for the Minesweeper app
 *
 * @author Garth Calkwood
 * @version 1.0
 * @since 2019-01-01
 */
public class Victory extends AppCompatActivity {

    /**
     * Initializes layout
     *
     * @param savedInstanceState bundle object containing the previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victory_screen);
    }

    /**
     * Ends the victory activity and restarts the game if the retry button is clicked
     *
     * @param view the view element that was clicked
     */
    public void onClick(View view){
        int id = view.getId();

        if (id == R.id.retryButton){
            startActivity(new Intent(Victory.this, MainActivity.class ));
            Victory.this.finish();
        }

    }
}
