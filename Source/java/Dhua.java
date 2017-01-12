// the trailing smoke puffs

package com.example.abhayrajmalhotra.myfirstgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Abhay Raj Malhotra on 01-07-2016.
 */
public class Dhua extends GameObj{

    public int r; // for radius
    public Dhua(int x, int y)
    {
        r =5;
        super.x= x;
        super.y = y;
    }

    // going back as per the speed of background
    public void update()
    {
        x-=10;
    }


    public void draw(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);

        // draw 3 at a time with their respective positions

        canvas.drawCircle(x - r, y - r, r, paint);
        canvas.drawCircle(x-r+4, y-r+1, r, paint);
        canvas.drawCircle(x-r+2, y-r-2, r, paint);

    }
}
