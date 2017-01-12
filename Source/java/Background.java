// the continously moving background image

package com.example.abhayrajmalhotra.myfirstgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

/**
 * Created by Abhay Raj Malhotra on 29-07-2016.
 */
public class Background {

    private Bitmap image, upperScroll; // loads background image
    private int x,y,dx=-10; // -10 means 10 to west


    //assigning the image
    Background(Bitmap res,Bitmap res2)
    {
        image = res;  // minimum of screen speed + division of 50 with score
        upperScroll = res2;
    }


    public void update(int score)
    {

        //goes left with speed of 10pixels and the relative speed of score/100
        dx = -10 - (score/100);

        x+=dx;
        //if reaches end of the whole screen start again

        if(x<-856)
            x=0;
    }
    public void draw(Canvas canvas)
    {
        //drawing image
       canvas.drawBitmap(image,x,y,null);
       canvas.drawBitmap(upperScroll, x, -40, null);

        // to cover up the forward lag of the image for continuity
        if(x<0)
        {
            //draw ahead of place
            canvas.drawBitmap(image,x+856,y,null);
            canvas.drawBitmap(upperScroll,x+856,-40,null);
        }
    }

}
