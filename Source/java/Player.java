// the helicopter app

package com.example.abhayrajmalhotra.myfirstgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;


/**
 * Created by Abhay Raj Malhotra on 01-07-2016.
 */
public class Player extends GameObj {

    private Bitmap spriteSheet;
    private int score;

    private boolean playing;
    private boolean up;
    private Animation animation = new Animation();

    private long startTime;
    public Player (Bitmap res, int w, int h, int numFrames)
    {
        // starting position of helicopter
        x = 100;
        y = 240; // height /2
        // amount of shift
        dy = 0;
        score = 0;
        width = w;
        height = h;
        Bitmap[] image = new Bitmap[numFrames];
        spriteSheet = res;

        for(int i = 0; i<image.length; i++)
        {
            //assigning the image array to bitmap image
            image[i]= Bitmap.createBitmap(spriteSheet, i*width,0, width, height);
        }

        animation.setFrames(image);
        animation.setDelay(10);

        startTime = System.nanoTime();
    }

    // to get in work with the onTouchFunction
    public void setUp(boolean b)
    {
    up =b;
    }

    public void update()
    {
        // getting update only at a gap of 0.1s
        long elapsed =(System.nanoTime() - startTime)/1000000;
        if(elapsed>100)
        {
            score++; // score added ever 100ms
            startTime=System.nanoTime();
        }

        animation.update();

        // since Y coordinate lies above
        if(up) { dy -=1;} // going up

        //going down
        else { dy +=1;} // going down

       // controlling the drop of game
        if(dy>10) dy=10;

        // controlling rise of game
        if(dy<-10) dy=-10;


        y+=dy; // updating only height

        // not letting go off the screen
        if(y>460)
            y = 460;

        //pushing down
        else if(y<1)
        {
            y=2;
            dy=0;
        }
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(animation.getImage(), x, y , null);

    }

    // the score function
    public int getScore() { return score;}

    // primary function for Game Pane class
    public boolean getPlaying() { return playing; }

    // indicator for updating and drawing in game pane class
    public void setPlaying(boolean b) { playing = b; }

    // after every game
    public void resetDy() { dy =0; }

    public void resetScore() { score=0; }

    public void setScore(int value)
    {
        score = value;
    }
}
