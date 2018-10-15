package com.hciexample.diyapp;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.ImageView;


public class TestActivity extends AppCompatActivity{

    int id[] = new int[8];
    int x[] = new int[8];
    int y[] = new int[8];
    int end[] = new int[8];
    int before[] = new int[8];

    MediaPlayer m;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        ImageView key = (ImageView)findViewById(R.id.imageView11);

    }


    public boolean onTouchEvent(MotionEvent event){
        int pointer_cnt = event.getPointerCount();//발생한 포인터 개수
        if(pointer_cnt > 8) pointer_cnt = 8;

        switch(event.getAction() & MotionEvent.ACTION_MASK){

            case MotionEvent.ACTION_DOWN:

                id[0] = event.getPointerId(0);
                x[0] = (int) (event.getX());
                y[0] = (int) (event.getY());

                if(y[0]>200 && y[0]<450) {
                    m = MediaPlayer.create(getBaseContext(), R.raw.sound1);
                }
                else if(y[0]>450 && y[0]<700) {
                    m = MediaPlayer.create(getBaseContext(), R.raw.sound2);
                }
                else if(y[0]>700 && y[0]<900) {
                    m = MediaPlayer.create(getBaseContext(), R.raw.sound3);
                }
                else if(y[0]>900 && y[0]<1100) {
                    m = MediaPlayer.create(getBaseContext(), R.raw.sound4);
                }
                else if(y[0]>1100 && y[0]<1350) {
                    m = MediaPlayer.create(getBaseContext(), R.raw.sound5);
                }
                else if(y[0]>1350 && y[0]<1550) {
                    m = MediaPlayer.create(getBaseContext(), R.raw.sound6);
                }
                else if(y[0]>1550 &&y[0]<1780) {
                    m = MediaPlayer.create(getBaseContext(), R.raw.sound7);
                }
                else {
                    m = MediaPlayer.create(getBaseContext(), R.raw.sound8);
                }

                m.start();

                m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.stop();
                        mp.release();
                    }
                });

                break;

            case MotionEvent.ACTION_POINTER_DOWN:

                for(int i=0;i<pointer_cnt;i++){
                    id[i] = event.getPointerId(i);
                    x[i] = (int) (event.getX(i));
                    y[i] = (int) (event.getY(i));


                    if(y[i]>20 && y[i]<450) {
                        m = MediaPlayer.create(getBaseContext(), R.raw.sound1);
                        end[i]=1;

                    }
                    else if(y[i]>450 && y[i]<700) {
                        m = MediaPlayer.create(getBaseContext(), R.raw.sound2);
                        end[i]=2;

                    }
                    else if(y[i]>700 && y[i]<900) {
                        m = MediaPlayer.create(getBaseContext(), R.raw.sound3);
                        end[i]=3;
                    }
                    else if(y[i]>900 && y[i]<1100) {
                        m = MediaPlayer.create(getBaseContext(), R.raw.sound4);
                        end[i]=4;
                    }
                    else if(y[i]>1100 && y[i]<1350) {
                        m = MediaPlayer.create(getBaseContext(), R.raw.sound5);
                        end[i]=5;
                    }
                    else if(y[i]>1350 && y[i]<1550) {
                        m = MediaPlayer.create(getBaseContext(), R.raw.sound6);
                        end[i]=6;
                    }
                    else if(y[i]>1550 && y[i]<1780) {
                        m = MediaPlayer.create(getBaseContext(), R.raw.sound7);
                        end[i]=7;
                    }
                }
/*
                if(before[pointer_cnt-1]==end[pointer_cnt-1]) {
                    if (before[pointer_cnt-2] != end[pointer_cnt - 2]) {
                        switch (end[pointer_cnt - 2]) {
                            case 1:
                                m = MediaPlayer.create(getBaseContext(), R.raw.sound1);
                                break;
                            case 2:
                                m = MediaPlayer.create(getBaseContext(), R.raw.sound2);
                                break;
                            case 3:
                                m = MediaPlayer.create(getBaseContext(), R.raw.sound3);
                                break;
                            case 4:
                                m = MediaPlayer.create(getBaseContext(), R.raw.sound4);
                                break;
                            case 5:
                                m = MediaPlayer.create(getBaseContext(), R.raw.sound5);
                                break;
                            case 6:
                                m = MediaPlayer.create(getBaseContext(), R.raw.sound6);
                                break;
                            case 7:
                                m = MediaPlayer.create(getBaseContext(), R.raw.sound7);
                                break;
                        }
                    }

                }

                for(int j=0;j<pointer_cnt;j++){
                    before[j]=end[j];
                }*/

                m.start();

                m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.stop();
                        mp.release();
                    }
                });

        }


        return super.onTouchEvent(event);
    }




}
