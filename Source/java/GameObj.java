// returns all key elements - getter setter class
// One speed parameter can be included more

package com.example.abhayrajmalhotra.myfirstgame;

import android.graphics.Rect;

/**
 * Created by Abhay Raj Malhotra on 29-07-2016.
 */
public abstract class GameObj {
    protected int x;
    protected  int y;
    protected  int dx;
    protected  int dy;
    protected int width;
    protected int height;

    public void setX(int x)
    {
        this.x=x;
    }
    public void setY(int y)
    {
        this.y=y;
    }
    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getHeight(int height)
    {
        return height;
    }

    public int getWidth(int width)
    {
        return width;
    }

    // for explosion purpose
    public Rect getRectangle()
    {
        return new Rect(x,y,x+width,y+height);
    }

}
