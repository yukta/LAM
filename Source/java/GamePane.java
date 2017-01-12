// the whole backbone of the game

package com.example.abhayrajmalhotra.myfirstgame;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import  android.view.View.OnClickListener ;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.Random;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.AudioManager;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Abhay Raj Malhotra on 29-06-2016.
 */
public class GamePane extends SurfaceView implements SurfaceHolder.Callback {

    int posX = 200,posY;
    private int WIDTH, HEIGHT; // of the device
    private MainThread thread; // the thread running - pause not implemented
    private Background bg,bg2; // the background image class object
    private Player player; // player class object
    private ArrayList<Dhua> dhua; // smoke object
    private long dhuaStart; // starting time of smoke
    private long missileStart;// of missile
    private ArrayList<Missile> missile; // for constantly updating
    private Random rand = new Random(); // for position of the missile
    private Bomb bomb; // bomb class object
    private long startReset;
    // the keys for handling drawing and update functions
    private boolean reset; // in case of a reset
    private boolean disappear; // waiting to disappear the helicopter
    private boolean started = false; // once game starts
    private boolean newGameCreated; // true if new game called
    private int best, ct1=0; // best score and the number of times played in a session
    MediaPlayer mp; // helicopter voice looping
    SharedPreferences settings; // for best score
    private SoundPool sounds; // for sound effect of explosion
    private int blast,gp,sonic,sonicClash; // id for that sound
    long resetElapsed; // if time elapses then start the game
    private boolean shield = false;
    private long shieldStart;
    private int bonus = 100;
    private int shieldTime;

    // constructor
    public GamePane(Context context)
    {
        //calling for context from main activity
        super(context);


        // of the background image to which we will viewport
        WIDTH =856;
        HEIGHT = 480;

        //add call back to surface holder to intercept
        getHolder().addCallback(this);

        setFocusable(true);
    }

    // abstract method of surface view
    @Override
    public  void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public  void surfaceDestroyed(SurfaceHolder holder){
        boolean retry=true; // if screen was started again
        // releasing the memory with music
        mp.release();
        sounds.release();

        int counter = 0;
        //clearing the thread
        while(retry && counter<1000)
        {
            counter++;
            try{

                thread.setRunning(false);
                thread.join();
                retry= false;
                thread = null;
            }catch(InterruptedException e) {e.printStackTrace(); }

        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        //loading the helicopter sound in raw directory
        mp = MediaPlayer.create(super.getContext(), R.raw.heli);
        mp.setLooping(true); // always repeat

        // bestScore is the saved preference for best score
         settings = super.getContext().getSharedPreferences("bestScore", Context.MODE_PRIVATE);


        // loading the sound effect called blast
        sounds = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        blast = sounds.load(super.getContext(), R.raw.grenade, 1);
        sonic =sounds.load(super.getContext(),R.raw.sonic,2);
        sonicClash = sounds.load(super.getContext(),R.raw.clash,3);

        // calling constructor of player background class

        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.grassbg1), BitmapFactory.decodeResource(getResources(),R.drawable.brick));
      //  bg2 = new Background(BitmapFactory.decodeResource(getResources(),R.drawable.bg2));

        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.heli), 110, 42,5);
        // creating an array of objects for dhua and missile class
        dhua = new ArrayList<Dhua>();
        missile = new ArrayList<Missile>();

        //mapping the amount of time from start
        missileStart = System.nanoTime();
        dhuaStart = System.nanoTime();

        // calling the main thread now which continously calls update and draw method
        thread = new MainThread(getHolder(), this);

        //we can safely start the game loop
        thread.setRunning(true);
        thread.start();
    }


    // the onTouch method
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {

        int action = event.getAction() & MotionEvent.ACTION_MASK;

        // if screen was pressed i.e. going up
        if(action==MotionEvent.ACTION_DOWN)
        {


            int eX = (int)event.getX();
            int eY = (int)event.getY();

            int scale = (80*getWidth())/856;

            if(eX<scale && eY> (getHeight())-scale) {
                if(!shield && shieldTime>0) {
                    shield = true;
                    sounds.play(sonic, 1.0f, 1.0f, 0, 0, 1); // left right priority and rate
                    shieldStart = System.nanoTime();
                }

                else
                shield = false;
            }

            // if the player wasn't playing then setting up
                        if (!player.getPlaying() && newGameCreated && reset) {
                            player.setUp(true); // going up i.e dy = -1
                            player.setPlaying(true);
                        }

                        // if already playing
                        if (player.getPlaying()) {

                            if (!started) started = true;

                            reset = false;
                            player.setUp(true);
                    }

            return true;

        }

        if(action==MotionEvent.ACTION_UP)
        {

            player.setUp(false); //going down i.e. dy = +1
            return true;
        }


        return super.onTouchEvent(event);
    }


    public void update()
    {


        // update only if player playing
        if(player.getPlaying()) {

            // start music
            mp.start();
            // update background and player
            bg.update(player.getScore());
            player.update();

            // simultaneous update of best score if best < current score
            if(player.getScore()>best)
                best = player.getScore();

            if(player.getScore()>bonus) {
                shieldTime += 4;
                if(shieldTime>20)
                    shieldTime = 20;
                bonus+=100;
            }

            if(shieldTime<1)
                shield = false;

            //checking for border collision
            if(player.y>=410 || player.y<14) { player.setPlaying(false); }


            //delay for launch of missiles
            long missileElapsed = (System.nanoTime() - missileStart)/1000000;

            // launch only if greater than 2 sec + score / 100 -- a huge score haa
            if(missileElapsed >(2000 - player.getScore()/100)){

                //first missile always goes down the middle and first missile
                if(missile.size()==0 && gp ==0)
                {
                    // to the array list
                    /*
                    from extreme east + 10
                    with a width and height of 77 and 23 for one missile and 13 such key frames
                     */
                    gp++; //game play missile goes first time
                    missile.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.
                            missile),WIDTH + 10, HEIGHT/2, 77, 23, player.getScore(), 13));
                }
                else
                {
                    /*
                     upper region
                     from 0 to height /2
                      */

                    missile.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.missile),
                            WIDTH+10,25 + ((int)(rand.nextDouble()*(HEIGHT/2))),77,23, player.getScore(),13));

                    /*
                     lower region
                     any where between the point height/2 to point height - 20
                     */
                    missile.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.missile),
                            WIDTH+10, (HEIGHT/2) + (((int)(rand.nextDouble()*(HEIGHT)))-20),77,23, player.getScore(),13));
                }

                //reset timer for launch of next missile
                missileStart = System.nanoTime();
            }

            //now checking for collision with individual missile using rectangle collision
            for(int i=0; i<missile.size();i++)
            {
                // checking if it clashes uses collision function defined underneath
                missile.get(i).update();
                if(Collision(missile.get(i),player))
                {
                    if(!shield)
                    player.setPlaying(false);

                    else
                    sounds.play(sonicClash, 1.0f, 1.0f, 0, 0, 1); // left right priority and rate

                    missile.remove(i);
                    break;
                }

                // remove that missile obj if collided
                if(missile.get(i).getX()<-50) {
                    missile.remove(i);
                    break;
                }
            }

            // for dhua pumping out
            long elapsed = (System.nanoTime() - dhuaStart)/1000000;
            if(elapsed>120) {
                dhua.add(new Dhua(player.getX(), player.getY() + 10));
                dhuaStart = System.nanoTime();
            }

            for(int i =0; i<dhua.size(); i++)
            {
                dhua.get(i).update();

                // once it goes off the screen
                if(dhua.get(i).getX()<-10)
                {
                    dhua.remove(i);
                }

            }

        }

        else
        {
    // if player wasn't playing
            if(mp.isPlaying()) {
                mp.stop();
                sounds.play(blast, 0.5f, 0.5f, 0, 0, 1); // left right priority and rate
                mp.release();
                mp = MediaPlayer.create(super.getContext(),R.raw.heli); // loading the helicopter song again
                mp.setLooping(true);
            }

            player.resetDy(); // reset the acceleration
            if(!reset)
            {
                // no new game
                newGameCreated= false;
                // taking time to restart
                startReset = System.nanoTime();
                //game was now reset
                reset = true;
                //helicopter has to disappear
                disappear = true;
                // bomb has to be loaded with now
                // individual image component is of 140 X 140 and total 25 frames are their
                // little up the screen since helicopter actually creeps down
                bomb = new Bomb(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), player.getX(),player.getY()-35, 140,140, 25);
            }

            // update the bomb
            bomb.update();
            resetElapsed = (System.nanoTime()-startReset)/1000000;

            // after 5.5 second game loaded
            if(resetElapsed>5500 && !newGameCreated)
            {
                newGame();
            }


        }
    }

    public boolean Collision(GameObj a, GameObj b)
    {
        // checking for intersection amongst the two game objects
        if(Rect.intersects(a.getRectangle(), b.getRectangle()))
            return true;

        return false;
    }


    @Override
    public void draw(Canvas canvas)
    {
        /*
        Viewporting the whole screen to 856 and 480
        So this ensures the portability of games to different devices
         */
        final float scaleFactorX = (float)getWidth()/856;
        final float scaleFactorY = (float)getHeight()/480;

        if(canvas!=null)
        {

            // we save the original state of it will increase the size on each draw when it'll scales
            final int saveState = canvas.save();
            //scaling the screen
            canvas.scale(scaleFactorX, scaleFactorY);

            bg.draw(canvas); // draw background

            // the smoke and helicopter are drawn and the missiles too
            if(!disappear && newGameCreated && player.getPlaying()) {
                player.draw(canvas);
                for (Dhua sp : dhua) {
                    sp.draw(canvas);
                }

                if(shield == true)
                {
                    canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.shieldimg),player.getX()+67,player.getY()-20,null);
                    canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.shield),5,400,null);
                    long shieldEnd = (long)((System.nanoTime() - shieldStart)/1000000);
                    if(shieldEnd>1000) {
                        shieldStart=System.nanoTime();
                        shieldTime-=1;
                    }
                }
                else
                    canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.noshield),5,400,null);

                }

            for(Missile m:missile)
            {
                m.draw(canvas);
            }


            if(started )
            {
                bomb.draw(canvas);

            }

            // draw text function for score and best
            drawText(canvas);

            canvas.restoreToCount(saveState); // restore to original canvas
        }
    }



    public void newGame()
    {
        best = settings.getInt("key",0); // retrieving the saved value
        disappear = false;


        dhua.clear();
        missile.clear();

        player.resetDy();

        player.setY(HEIGHT/2);

        // checking and updating the high score
        if(player.getScore()>best)
        {
            best = player.getScore();
            SharedPreferences.Editor editor = settings.edit();
             editor.putInt("key", best);
             editor.commit();
        }

        bonus = 100;
        shieldTime = 0;
        player.resetScore();
        newGameCreated=true;

        ct1++;
    }


    public void drawText(Canvas canvas)
    {

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));

        //displaying the high score and running score
        if(player.getPlaying()) {
            canvas.drawText("Running " + player.getScore(), 600, 60, paint);
            canvas.drawText("High " + best, 50, 60, paint);
            canvas.drawText("Shield "+shieldTime, 320, 60, paint);
        }


        paint.setTextSize(50);

        // a false emulation of loading screen
        if(resetElapsed<2000 && resetElapsed>=1500)
            canvas.drawText("LOADING ." , 280, 260, paint);
        else if(resetElapsed<3000 && resetElapsed>=2000)
            canvas.drawText("LOADING . ." ,280 , 260, paint);
        else if(resetElapsed<4000 && resetElapsed>=3000)
            canvas.drawText("LOADING . . ." ,280 , 260, paint);
        else if(resetElapsed<5000 && resetElapsed>=4000)
            canvas.drawText("LOADING . . . ." ,280 , 260, paint);

        // in case of new game start
        if(!player.getPlaying() && newGameCreated && reset)
        {
            //calling a new paint with new attributes
            Bitmap firstImage = BitmapFactory.decodeResource(getResources(),R.drawable.big);
            canvas.drawBitmap(firstImage, 20, 130, null); // first image
            Paint paint1 = new Paint();
            paint1.setTextSize(50);
            paint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("Tap to start", (WIDTH / 2)+35 , HEIGHT / 2, paint1);

            paint1.setTextSize(30);
            canvas.drawText("High " + best, WIDTH - 200, 60, paint1);

            paint1.setTextSize(20);
            canvas.drawText("Hold and release for playing", (WIDTH / 2)+32 , (HEIGHT / 2)+50, paint1);
            canvas.drawText("Press the button on the bottom left to activate shield", (WIDTH / 2)-90 , (HEIGHT / 2)+100, paint1);

        }
    }

}
