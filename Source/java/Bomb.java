// the bombing on collision

package com.example.abhayrajmalhotra.myfirstgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Abhay Raj Malhotra on 03-07-2016.
 */

public class Bomb {
    private int x;
    private int y;
    private int width;
    private int height;
    private int row;
    private Animation animation = new Animation(); // takes in account of animation class
    private Bitmap spritesheet; // the bombing sprite sheet
    Bitmap[] image ; // the image array being sent to the animation
    public Bomb(Bitmap res, int x, int y, int w, int h, int numFrames)
    {
        // taking in the place with the actual width and height of each frame
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;

        // the image rendering array
        image= new Bitmap[numFrames]; // initialising the array - java style

        spritesheet = res;

        for(int i = 0; i<image.length; i++)
        {
            //assigning the prescribed pixels accurately
            if(i%5==0&&i>0)row++;
            image[i] = Bitmap.createBitmap(spritesheet, (i-(5*row))*width, row*height, width, height);
        }

        animation.setFrames(image); // giving in to animation class
        animation.setDelay(10); // a delay of 10ms
    }

    public void draw(Canvas canvas)
    {
        // only if playedOnce became false then explode
        if(!animation.playedOnce())
        {
            canvas.drawBitmap(animation.getImage(),x,y,null);
        }

    }
    public void update()
    {
        // only if playedOnce became false then update
        if(!animation.playedOnce())
        {
            animation.update(); // updating the next frame
        }
    }

}