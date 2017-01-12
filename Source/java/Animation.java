// Helper  class for the missile, bomb and the player class

package com.example.abhayrajmalhotra.myfirstgame;

import android.graphics.Bitmap;

/**
 * Created by Abhay Raj Malhotra on 01-07-2016.
 */
public class Animation {

    private Bitmap[] frames; // the number of images in a sprite sheet array
    private int currentFrame; // present rendering frame

    //for taking in delay parameters
    private long startTime;
    private long delay;

    //checks if played only once
    private boolean playedOnce;

    public void setFrames(Bitmap[] frames)
    {
        this.frames = frames;
        currentFrame = 0;
        startTime = System.nanoTime(); // sets the particular frame
    }

    public void setDelay(long d)
    {
        delay=d;
    } // amount of delay = 10ms

    public void setFrame(int i)
    {
        currentFrame=i;
    } // current frame being rendered which changes at 10ms rate

    // takes in the amount of delay and updates accordingly
    public void update()
    {

        // looping the whole of the animation on the sprite sheet
        long elapsed = (System.nanoTime()-startTime)/1000000;

        if(elapsed>delay)
        {
            currentFrame++; // next frame if elapsed
            startTime = System.nanoTime(); //updating the timer again
        }

        if(currentFrame==frames.length)
        {
            currentFrame=0; // reset to 0
            playedOnce=true;
        }
    }

    public Bitmap getImage()
    {
        return frames[currentFrame];
    } // gives image to draw functions

    public int getFrame()
    {
        return currentFrame;
    } // checks for the current frame rendered

    public boolean playedOnce()
    {
        return playedOnce;
    } // if played once

}
