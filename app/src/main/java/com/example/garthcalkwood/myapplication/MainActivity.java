package com.example.garthcalkwood.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.util.Random;


/**
 * Main activity for the Minesweeper app
 *
 * @author Garth Calkwood
 * @version 1.0
 * @since 2019-01-01
 */
public class MainActivity extends AppCompatActivity {

    private Block[][] gameBoard = generateGrid(8);
    private boolean flag = false;
    private int numMines = 8;
    private int remainingBlocks = 64 - numMines;

    /**
     * Initializes layout and flag button
     * <p>
     * Turns on checked change listener for the flag button
     *
     * @param savedInstanceState bundle object containing the previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToggleButton flagButton = findViewById(R.id.toggleButton);
        flagButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    flag = true;
                } else {
                    flag = false;
                }
            }
        });

    }

    /**
     * Determines which button was clicked and executes an action accordingly
     * <p>
     * First gets the corresponding x and y coordinates of the clicked button, then checks if the
     * player is in flag or click mode. If a mine is clicked the gameOver() function is called. If
     * an unclicked button is clicked, the number of nearby mines is revealed, if there are no nearby
     * mines, the recursiveReveal() function is called, which reveals the surrounding buttons up to
     * the buttons that are next to mines. After a button is revealed, if it's the last non-mine
     * button, the victory function is called. If a block is clicked in flag mode, it gets flagged and
     * becomes unclickable. If an already flagged button is flaged, it gets unflagged and becomes
     * clickable again.
     *
     * @param view the view element that was clicked
     */
    public void onClick(View view){

        int id = view.getId();

        Button clickedButton = findViewById(id);
        int coordinates = getButtonCoordinates(id);
        int x = coordinates / 10;
        int y = coordinates % 10;

        Block clickedBlock = getBlockFromId(id);

        if (!flag) {

            // if block is a mine
            if (clickedBlock.getState() == 2 && !clickedBlock.getIsFlagged() ){
                clickedButton.setBackgroundColor(Color.RED);
                gameOver();
            }

            // if block is unclicked
            if (clickedBlock.getState() == 0 && !clickedBlock.getIsFlagged() ) {

                int nearbyMines = countNearbyMines(x, y);

                if (nearbyMines == 0) {
                    recursiveReveal(coordinates, clickedBlock, clickedButton);
                } else {

                    int stringId = getStringId(nearbyMines);

                    clickedBlock.setState(1);
                    remainingBlocks--;

                    clickedButton.setText(stringId);

                    if ((x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1))
                        clickedButton.setBackgroundColor(Color.WHITE);
                    else
                        clickedButton.setBackgroundColor(Color.LTGRAY);

                    if (remainingBlocks == 0){
                        victory();
                    }
                }
            }
        } else if (numMines >= 0) {
            TextView minesDisplay = findViewById(R.id.minesDisplay);

            if (!clickedBlock.getIsFlagged() && numMines > 0) {
                numMines--;
                minesDisplay.setText(getStringId(numMines));
                clickedBlock.setIsFlagged(true);
                clickedButton.setText(R.string.F);
            } else if (clickedBlock.getIsFlagged()){
                numMines++;
                minesDisplay.setText(getStringId(numMines));
                clickedBlock.setIsFlagged(false);
                clickedButton.setText("");
            }
        }
    }

    /**
     * ends the main activity and sends player to the victory screen
     */
    public void victory(){
        startActivity(new Intent(MainActivity.this, Victory.class ));
        MainActivity.this.finish();
    }

    /**
     * ends the main activity and sends player to the game over screen
     */
    public void gameOver(){
        startActivity(new Intent(MainActivity.this, GameOver.class ));
        MainActivity.this.finish();
    }

    /**
     * Reveals surrounding non-mine buttons
     * <p>
     * First reveals the button. Then returns if the button has nearby mines, otherwise the
     * recursiveReveal funcion is called for every surrounding button starting with the top left.
     *
     * @param coordinates the x and y coordinates of a button as a 2 digit number, 1st digit is x, 2nd is y.
     * @param clickedBlock the Block variable of the button that was clicked
     * @param clickedButton the Button variable of the button that was clicked
     */
    public void recursiveReveal(int coordinates, Block clickedBlock, Button clickedButton){
        int x = coordinates / 10;
        int y = coordinates % 10;
        int nearbyMines = countNearbyMines(x, y);

        if ( (x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1) )
            clickedButton.setBackgroundColor(Color.WHITE);
        else
            clickedButton.setBackgroundColor(Color.LTGRAY);

        clickedBlock.setState(1);
        remainingBlocks--;

        if (remainingBlocks == 0){
            victory();
        }

        if (nearbyMines > 0){
            int stringId = getStringId(nearbyMines);
            clickedButton.setText(stringId);
            return;
        } else {
            for (int i = Math.max(x-1, 0); i <= Math.min(x+1, 7); i++){
                for (int j = Math.max(y-1, 0); j <= Math.min(y+1, 7); j++){
                    if (gameBoard[i][j].getState() == 0 && !gameBoard[i][j].getIsFlagged()) {
                        int newCoordinates = i * 10 + j;
                        int id = getButtonId(newCoordinates);
                        recursiveReveal( getButtonCoordinates(id), getBlockFromId(id), (Button) findViewById(id) );
                    }
                }
            }
        }
    }



    /**
     * Find the number of mines surrounding a button
     * <p>
     * Iterates through the buttons surrounding the button in question, then returns how many are mines
     *
     * @param x the x coordinate of the button
     * @param y the y coordinate of the button
     */
    public int countNearbyMines(int x, int y){
        int count = 0;

        for (int i = Math.max(x-1, 0); i <= Math.min(x+1, 7); i++){
            for (int j = Math.max(y-1, 0); j <= Math.min(y+1, 7); j++){
                if (gameBoard[i][j].getState() == 2)
                    count++;
            }
        }
        return count;
    }

    /**
     * Generates a grid with randomly placed mines
     * <p>
     * First creates a 2D array of blocks using the makeGrid() function. Then randomly generates
     * x and y coordinates of the blocks that are going to be mines and adjusts their states accordingly.
     *
     * @param numBombs the number of mines to be randomly placed on the grid
     */
    public Block[][] generateGrid(int numBombs){

        Random r = new Random();

        Block[][] grid = makeGrid();

        while (numBombs > 0){
            int x = r.nextInt(8);
            int y = r.nextInt(8);

            if (grid[x][y].getState() != 2) {
                grid[x][y].setState(2);
                numBombs--;
            }
        }
        return grid;
    }

    /**
     * Returns and empty 8x8 array of blocks.
     */
    public Block[][] makeGrid(){

        Block[][] grid = new Block[8][8];

        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                grid[i][j] = new Block();
            }
        }
        return grid;
    }

    /**
     * Returns the block corresponding to the button id.
     * @param id The id of the button
     */
    public Block getBlockFromId(int id){
        int coordinates = getButtonCoordinates(id);
        int x = coordinates / 10;
        int y = coordinates % 10;

        return gameBoard[x][y];
    }

    /**
     * Returns the string ID corresponding to count
     * <p>
     * Used for labeling clicked buttons with the number of nearby mines.
     *
     * @param count number of nearby mines
     */
    public int getStringId(int count){
        switch(count){
            case 0:
                return R.string.nearbyMines_0;
            case 1:
                return R.string.nearbyMines_1;
            case 2:
                return R.string.nearbyMines_2;
            case 3:
                return R.string.nearbyMines_3;
            case 4:
                return R.string.nearbyMines_4;
            case 5:
                return R.string.nearbyMines_5;
            case 6:
                return R.string.nearbyMines_6;
            case 7:
                return R.string.nearbyMines_7;
            case 8:
                return R.string.nearbyMines_8;
            default:
                throw new AssertionError("Too many nearby mines.");
        }
    }

    /**
     * Gets the button ID of a button based on its coordinates
     * <p>
     * A giant switch statement used to access a button's id through it's coordinates
     *
     * @param coordinates the x and y coordinates of a button as a 2 digit number, 1st digit is x, 2nd is y.
     */
    public int getButtonId(int coordinates){
        switch(coordinates) {
            case 0:
                return R.id.button0;
            case 1:
                return R.id.button1;
            case 2:
                return R.id.button2;
            case 3:
                return R.id.button3;
            case 4:
                return R.id.button4;
            case 5:
                return R.id.button5;
            case 6:
                return R.id.button6;
            case 7:
                return R.id.button7;
            case 10:
                return R.id.button10;
            case 11:
                return R.id.button11;
            case 12:
                return R.id.button12;
            case 13:
                return R.id.button13;
            case 14:
                return R.id.button14;
            case 15:
                return R.id.button15;
            case 16:
                return R.id.button16;
            case 17:
                return R.id.button17;
            case 20:
                return R.id.button20;
            case 21:
                return R.id.button21;
            case 22:
                return R.id.button22;
            case 23:
                return R.id.button23;
            case 24:
                return R.id.button24;
            case 25:
                return R.id.button25;
            case 26:
                return R.id.button26;
            case 27:
                return R.id.button27;
            case 30:
                return R.id.button30;
            case 31:
                return R.id.button31;
            case 32:
                return R.id.button32;
            case 33:
                return R.id.button33;
            case 34:
                return R.id.button34;
            case 35:
                return R.id.button35;
            case 36:
                return R.id.button36;
            case 37:
                return R.id.button37;
            case 40:
                return R.id.button40;
            case 41:
                return R.id.button41;
            case 42:
                return R.id.button42;
            case 43:
                return R.id.button43;
            case 44:
                return R.id.button44;
            case 45:
                return R.id.button45;
            case 46:
                return R.id.button46;
            case 47:
                return R.id.button47;
            case 50:
                return R.id.button50;
            case 51:
                return R.id.button51;
            case 52:
                return R.id.button52;
            case 53:
                return R.id.button53;
            case 54:
                return R.id.button54;
            case 55:
                return R.id.button55;
            case 56:
                return R.id.button56;
            case 57:
                return R.id.button57;
            case 60:
                return R.id.button60;
            case 61:
                return R.id.button61;
            case 62:
                return R.id.button62;
            case 63:
                return R.id.button63;
            case 64:
                return R.id.button64;
            case 65:
                return R.id.button65;
            case 66:
                return R.id.button66;
            case 67:
                return R.id.button67;
            case 70:
                return R.id.button70;
            case 71:
                return R.id.button71;
            case 72:
                return R.id.button72;
            case 73:
                return R.id.button73;
            case 74:
                return R.id.button74;
            case 75:
                return R.id.button75;
            case 76:
                return R.id.button76;
            case 77:
                return R.id.button77;
            default:
                throw new AssertionError("Coordinates do not exist.");
        }
    }

    /**
     * Gets the coordinates of a button based on its button ID
     * <p>
     * A giant switch statement used to access a button's coordinates through it's ID
     *
     * @param id the id of a button
     */
    public int getButtonCoordinates(int id){
        switch(id) {
            case R.id.button0:
                return 0;
            case R.id.button1:
                return 1;
            case R.id.button2:
                return 2;
            case R.id.button3:
                return 3;
            case R.id.button4:
                return 4;
            case R.id.button5:
                return 5;
            case R.id.button6:
                return 6;
            case R.id.button7:
                return 7;
            case R.id.button10:
                return 10;
            case R.id.button11:
                return 11;
            case R.id.button12:
                return 12;
            case R.id.button13:
                return 13;
            case R.id.button14:
                return 14;
            case R.id.button15:
                return 15;
            case R.id.button16:
                return 16;
            case R.id.button17:
                return 17;
            case R.id.button20:
                return 20;
            case R.id.button21:
                return 21;
            case R.id.button22:
                return 22;
            case R.id.button23:
                return 23;
            case R.id.button24:
                return 24;
            case R.id.button25:
                return 25;
            case R.id.button26:
                return 26;
            case R.id.button27:
                return 27;
            case R.id.button30:
                return 30;
            case R.id.button31:
                return 31;
            case R.id.button32:
                return 32;
            case R.id.button33:
                return 33;
            case R.id.button34:
                return 34;
            case R.id.button35:
                return 35;
            case R.id.button36:
                return 36;
            case R.id.button37:
                return 37;
            case R.id.button40:
                return 40;
            case R.id.button41:
                return 41;
            case R.id.button42:
                return 42;
            case R.id.button43:
                return 43;
            case R.id.button44:
                return 44;
            case R.id.button45:
                return 45;
            case R.id.button46:
                return 46;
            case R.id.button47:
                return 47;
            case R.id.button50:
                return 50;
            case R.id.button51:
                return 51;
            case R.id.button52:
                return 52;
            case R.id.button53:
                return 53;
            case R.id.button54:
                return 54;
            case R.id.button55:
                return 55;
            case R.id.button56:
                return 56;
            case R.id.button57:
                return 57;
            case R.id.button60:
                return 60;
            case R.id.button61:
                return 61;
            case R.id.button62:
                return 62;
            case R.id.button63:
                return 63;
            case R.id.button64:
                return 64;
            case R.id.button65:
                return 65;
            case R.id.button66:
                return 66;
            case R.id.button67:
                return 67;
            case R.id.button70:
                return 70;
            case R.id.button71:
                return 71;
            case R.id.button72:
                return 72;
            case R.id.button73:
                return 73;
            case R.id.button74:
                return 74;
            case R.id.button75:
                return 75;
            case R.id.button76:
                return 76;
            case R.id.button77:
                return 77;
            default:
                throw new AssertionError("Not a real button.");
        }

    }
}