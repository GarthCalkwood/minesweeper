package com.example.garthcalkwood.myapplication;

/**
 * Block class for the Minesweeper app
 *
 * @author Garth Calkwood
 * @version 1.0
 * @since 2019-01-01
 */
public class Block {

    private int state; // 0 = unclicked 1 = clicked, 2 = mine
    private boolean isFlagged;


    public Block(){
        state = 0;
    }

    public int getState(){
        return state;
    }

    public void setState(int state){
        this.state = state;
    }

    public boolean getIsFlagged() { return isFlagged; }

    public void setIsFlagged(boolean b) { isFlagged = b; }

}
