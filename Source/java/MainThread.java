// The whole game loop - referred from a game on libGDX project
// the never ending thread running

package com.example.abhayrajmalhotra.myfirstgame;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by Abhay Raj Malhotra on 28-06-2016.
 */
public class MainThread extends Thread {

    private int FPS=30; // minimum for resolution
    private double avgFPS;
    private SurfaceHolder surfaceHolder;
    private GamePane gamepane;
    private boolean running;

    public static Canvas canvas;
    public MainThread(SurfaceHolder surfaceHolder, GamePane gamepane)
    {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamepane = gamepane;
    }

    @Override
    public void run() {
        long startT = 0;
        int frameCt = 0;
        long targetT = 1000 / FPS;
        long timeM;
        long waitT;
        long totalT = 0;

        while (running) {
            startT = System.nanoTime();
            canvas = null;
            //lock canvas
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    this.gamepane.update();
                    this.gamepane.draw(canvas);
                }
            } catch (Exception e) {}

            finally {
                 if(canvas!=null)
                 {
                     try{
                         surfaceHolder.unlockCanvasAndPost(canvas);
                     }catch(Exception e){}
                 }
            }
            ;
            timeM = (System.nanoTime() - startT) / 1000000;
            waitT = targetT - timeM;

            try {
                this.sleep(waitT);

            } catch (Exception e) {
                System.out.println(e.getStackTrace());
            }

        totalT += System.nanoTime() - startT;
        frameCt++;

        if (frameCt == FPS) {
            avgFPS = 1000 / totalT / frameCt / 1000000;
            frameCt = 0;
            totalT = 0;
            System.out.println(avgFPS);
            }
        }
    }

        public void setRunning(boolean b)
    {
        running = b;
    }


}

