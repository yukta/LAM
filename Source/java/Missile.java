// Missile Rendering class

package com.example.abhayrajmalhotra.myfirstgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

/**
 * Created by Abhay Raj Malhotra on 02-07-2016.
 */
public class Missile extends GameObj {

    private Bitmap spriteSheet;
    private int score;
    private int speed;
    private Animation animation = new Animation();

    public Missile(Bitmap res, int x, int y, int w, int h, int s, int numFrames)
    {
        super.x = x;
        super.y =y;
        width =w;
        height = h;
        score = s;
        speed = 10 + (score/100) ; // minimum of screen speed + division of 100 with score

        Bitmap[] image = new Bitmap[numFrames];
        spriteSheet = res;

        for(int i =0;i<image.length;i++)
        {
            // setting the frames in a linear vertical manner
            image[i]=Bitmap.createBitmap(spriteSheet, 0, i*height, width, height);
            animation.setFrames(image);
            animation.setDelay(10);
        }
    }

    public void update()
    {
        // going west at speed of - (speed)
        animation.setDelay(10); // the amount of rotation
        x-=speed;
        animation.update();
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(animation.getImage(), x,y,null);

    }
}
