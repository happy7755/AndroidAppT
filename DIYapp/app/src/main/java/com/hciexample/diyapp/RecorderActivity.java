package com.hciexample.diyapp;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/*RecorderActivity , Complex1D, RealDoubleFFT, RealDoubleFFT_Mixed*/

public class RecorderActivity extends AppCompatActivity  {
    //implements Button.OnTouchListener
//audiorecord 객체에서 주파수는 8kHz 오디오채널은 하나, 샘플은 16비트
    int frequency=8000;
    int channelConfiguration= AudioFormat.CHANNEL_CONFIGURATION_MONO;
    int audioEncoding=AudioFormat.ENCODING_PCM_16BIT;

    //우리의 FFT객체는 트랜스포머이고 이 객체를 통해 audiorecord객체에서 256가지 샘플 다룬다
    //즉 샘플 수는 FFT객체를 통해 실행하고 가져올 주파수의 수와 같다.

    private RealDoubleFFT transformer;
    int blockSize=256;
    //Button button;
    boolean started=true;

    boolean sound_use=false;

    RecordAudio recordTask;


    int id[] = new int[8];
    int x[] = new int[8];
    int y[] = new int[8];
    TextView ttt;
    String result;
    ImageButton b1, b2, b3, b4, b5, b6, b7;

    MediaPlayer m;

    boolean p1,p2,p3,p4,p5,p6,p7;
    //도레미~ 자리에 터치되어있으면 true

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ttt=(TextView)findViewById(R.id.textView4);

        b1=(ImageButton)findViewById(R.id.imageButton1);
        b2=(ImageButton)findViewById(R.id.imageButton2);
        b3=(ImageButton)findViewById(R.id.imageButton3);
        b4=(ImageButton)findViewById(R.id.imageButton4);
        b5=(ImageButton)findViewById(R.id.imageButton5);
        b6=(ImageButton)findViewById(R.id.imageButton6);
        b7=(ImageButton)findViewById(R.id.imageButton7);


        transformer=new RealDoubleFFT(blockSize);

        recordTask = new RecordAudio();
        recordTask.execute();

    }


    public void identouch(int i){
        if(y[i]<500) {
            p1=true;
            b1.setImageResource(R.drawable.btn_green);
        }
        else if(y[i]>500 && y[i]<800) {
            p2=true;
            b2.setImageResource(R.drawable.btn_green);
        }
        else if(y[i]>800 && y[i]<1000) {
            b3.setImageResource(R.drawable.btn_green);
            p3=true;
        }
        else if(y[i]>1000 && y[i]<1200) {
            b4.setImageResource(R.drawable.btn_green);
            p4=true;
        }
        else if(y[i]>1400 && y[i]<1600) {
            b5.setImageResource(R.drawable.btn_green);
            p5=true;
        }
        else if(y[i]>1600&& y[i]<1800) {
            b6.setImageResource(R.drawable.btn_green);
            p6=true;
        }
        else if(y[i]>1800) {
            b7.setImageResource(R.drawable.btn_green);
            p7=true;
        }

    }

    public void initp(){
        p1=false;
        p2=false;
        p3=false;
        p4=false;
        p5=false;
        p6=false;
        p7=false;
        b1.setImageResource(R.drawable.btn_black);
        b2.setImageResource(R.drawable.btn_black);
        b3.setImageResource(R.drawable.btn_black);
        b4.setImageResource(R.drawable.btn_black);
        b5.setImageResource(R.drawable.btn_black);
        b6.setImageResource(R.drawable.btn_black);
        b7.setImageResource(R.drawable.btn_black);


    }

    public boolean onTouchEvent(MotionEvent event){
        int pointer_cnt = event.getPointerCount();//발생한 포인터 개수
        if(pointer_cnt > 8) pointer_cnt = 8;



        switch(event.getAction() & MotionEvent.ACTION_MASK){

            case MotionEvent.ACTION_DOWN:

                id[0] = event.getPointerId(0);
                x[0] = (int) (event.getX());
                y[0] = (int) (event.getY());

                b1.setImageResource(R.drawable.btn_black);
                b2.setImageResource(R.drawable.btn_black);
                b3.setImageResource(R.drawable.btn_black);
                b4.setImageResource(R.drawable.btn_black);
                b5.setImageResource(R.drawable.btn_black);
                b6.setImageResource(R.drawable.btn_black);
                b7.setImageResource(R.drawable.btn_black);

                if(y[0]>1600 && y[0]<1800 && sound_use) {
                    result = "높은도\n";
                    b6.setImageResource(R.drawable.btn_green);
                    m = MediaPlayer.create(getBaseContext(), R.raw.sound8);
                    m.start();

                    m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.stop();
                            mp.release();
                        }
                    });

                }
                else if(y[0]>1800 && sound_use) {
                    result += "시\n";
                    b7.setImageResource(R.drawable.btn_green);
                    m = MediaPlayer.create(getBaseContext(), R.raw.sound7);
                    m.start();

                    m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.stop();
                            mp.release();
                        }
                    });

                }


                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                initp();
                for(int i=0;i<pointer_cnt;i++){
                    id[i] = event.getPointerId(i);
                    x[i] = (int) (event.getX(i));
                    y[i] = (int) (event.getY(i));

                    identouch(i);
/*
                    if(y[i]<500 && p2 && p3 && p4 && p5 && p6 && p7 && sound_use) {
                        result = "도d";

                        m = MediaPlayer.create(getBaseContext(), R.raw.sound1);
                    }
                    else if(!p1 && y[i]>500 && y[i]<800 && p3 && p4 && p5 && p6 && p7 && sound_use) {
                        result = "레d";

                        m = MediaPlayer.create(getBaseContext(), R.raw.sound2);
                    }

                    else if(!p1 && !p2 && y[i]>800 && y[i]<1000 && p4 && p5 && p6 && p7 && sound_use) {
                        result = "미d";
                        m = MediaPlayer.create(getBaseContext(), R.raw.sound3);
                    }
                    else if(!p1 && !p2 && !p3 && y[i]>1000 && y[i]<1200 && p5 && p6 && p7 && sound_use) {
                        result = "파d";
                        m = MediaPlayer.create(getBaseContext(), R.raw.sound4);
                    }
                    else if(!p1 && !p2 && !p3 && !p4 && y[i]>1400 && y[i]<1600 && p6 && p7 && sound_use) {
                        result = "솔d";
                        m = MediaPlayer.create(getBaseContext(), R.raw.sound5);
                    }
                    else if(!p1 && !p2 && !p3 && !p4 && !p5 && y[i]>1600 && y[i]<1800 && p7 && sound_use) {
                        result = "라d";
                        m = MediaPlayer.create(getBaseContext(), R.raw.sound6);
                    }*/
                }

                if(p1 && p2 && p3 && p4 && p5 && p6 && p7 && sound_use) {
                    result = "도d";

                    m = MediaPlayer.create(getBaseContext(), R.raw.sound1);
                    m.start();

                    m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.stop();
                            mp.release();
                        }
                    });
                }
                else if(!p1 && p2 && p3 && p4 && p5 && p6 && p7 && sound_use) {
                    result = "레d";

                    m = MediaPlayer.create(getBaseContext(), R.raw.sound2);
                    m.start();

                    m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.stop();
                            mp.release();
                        }
                    });
                }

                else if(!p1 && !p2 && p3 && p4 && p5 && p6 && p7 && sound_use) {
                    result = "미d";
                    m = MediaPlayer.create(getBaseContext(), R.raw.sound3);
                    m.start();

                    m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.stop();
                            mp.release();
                        }
                    });
                }
                else if(!p1 && !p2 && !p3 && p4 && p5 && p6 && p7 && sound_use) {
                    result = "파d";
                    m = MediaPlayer.create(getBaseContext(), R.raw.sound4);
                    m.start();

                    m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.stop();
                            mp.release();
                        }
                    });
                }
                else if(!p1 && !p2 && !p3 && !p4 && p5 && p6 && p7 && sound_use) {
                    result = "솔d";
                    m = MediaPlayer.create(getBaseContext(), R.raw.sound5);
                    m.start();

                    m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.stop();
                            mp.release();
                        }
                    });
                }
                else if(!p1 && !p2 && !p3 && !p4 && !p5 && p6 && p7 && sound_use) {
                    result = "라d";
                    m = MediaPlayer.create(getBaseContext(), R.raw.sound6);
                    m.start();

                    m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.stop();
                            mp.release();
                        }
                    });
                }



                break;


            case MotionEvent.ACTION_POINTER_UP:
                initp();
                for(int i=0;i<pointer_cnt;i++){
                    id[i] = event.getPointerId(i);
                    x[i] = (int) (event.getX(i));
                    y[i] = (int) (event.getY(i));

                    identouch(i);
                }
                if(p1 && p2 && p3 && p4 && p5 && p6 && p7 && sound_use){
                    result =  "레u";
                    m = MediaPlayer.create(getBaseContext(), R.raw.sound2);
                    m.start();

                    m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.stop();
                            mp.release();
                        }
                    });

                }

                else if(!p1 && p2 && p3 && p4 && p5 && p6 && p7 && sound_use){
                    result =  "미u";
                    m = MediaPlayer.create(getBaseContext(), R.raw.sound3);
                    m.start();

                    m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.stop();
                            mp.release();
                        }
                    });

                }

                else if (!p1 && !p2 && p3 && p4 && p5 && p6 && p7 && sound_use){
                    result =  "파u";
                    m = MediaPlayer.create(getBaseContext(), R.raw.sound4);
                    m.start();

                    m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.stop();
                            mp.release();
                        }
                    });

                }

                else if (!p1 && !p2 && !p3 && p4 && p5 && p6 && p7 && sound_use){
                    result = "솔u";
                    m = MediaPlayer.create(getBaseContext(), R.raw.sound5);
                    m.start();

                    m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.stop();
                            mp.release();
                        }
                    });

                }

                else if (!p1 && !p2 && !p3 && !p4 && p5 && p6 && p7 && sound_use){
                    result = "라u";
                    m = MediaPlayer.create(getBaseContext(), R.raw.sound6);
                    m.start();

                    m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.stop();
                            mp.release();
                        }
                    });

                }
                else if (!p1 && !p2 && !p3 && !p4 && !p5 && p6 && p7 && sound_use){
                    result = "시u";
                    m = MediaPlayer.create(getBaseContext(), R.raw.sound7);
                    m.start();

                    m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.stop();
                            mp.release();
                        }
                    });

                }



                break;



        }

        ttt.setText(result);

        return super.onTouchEvent(event);
    }

    private class RecordAudio extends AsyncTask<Void, double[], Void>{

        int value;
        @Override

        protected Void doInBackground(Void... params) {

            try{
                // AudioRecord를 설정하고 사용한다.
                int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
                AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, bufferSize);

                // short로 이뤄진 배열인 buffer는 원시 PCM 샘플을 AudioRecord 객체에서 받는다.
                // double로 이뤄진 배열인 toTransform은 같은 데이터를 담지만 double 타입인데, FFT 클래스에서는 double타입이 필요해서이다.

                short[] buffer = new short[blockSize];
                double[] toTransform = new double[blockSize];

                audioRecord.startRecording();

                while(started){
                    int bufferReadResult = audioRecord.read(buffer, 0, blockSize);
                    // AudioRecord 객체에서 데이터를 읽은 다음에는 short 타입의 변수들을 double 타입으로 바꾸는 루프를 처리한다.
                    // 직접 타입 변환(casting)으로 이 작업을 처리할 수 없다. 값들이 전체 범위가 아니라 -1.0에서 1.0 사이라서 그렇다
                    // short를 32,768.0(Short.MAX_VALUE) 으로 나누면 double로 타입이 바뀌는데, 이 값이 short의 최대값이기 때문이다.

                    for(int i = 0; i < blockSize && i < bufferReadResult; i++) {
                        toTransform[i] = (double) buffer[i] / Short.MAX_VALUE; // 부호 있는 16비트
                    }


                    // 이제 double값들의 배열을 FFT 객체로 넘겨준다. FFT 객체는 이 배열을 재사용하여 출력 값을 담는다. 포함된 데이터는 시간 도메인이 아니라
                    // 주파수 도메인에 존재한다. 이 말은 배열의 첫 번째 요소가 시간상으로 첫 번째 샘플이 아니라는 얘기다. 배열의 첫 번째 요소는 첫 번째 주파수 집합의 레벨을 나타낸다.

                    // 256가지 값(범위)을 사용하고 있고 샘플 비율이 8,000 이므로 배열의 각 요소가 대략 15.625Hz를 담당하게 된다. 15.625라는 숫자는 샘플 비율을 반으로 나누고(캡쳐할 수 있는
                    // 최대 주파수는 샘플 비율의 반이다. <- 누가 그랬는데...), 다시 256으로 나누어 나온 것이다. 따라서 배열의 첫 번째 요소로 나타난 데이터는 영(0)과 15.625Hz 사이에
                    // 해당하는 오디오 레벨을 의미한다.

                    transformer.ft(toTransform);

                    // publishProgress를 호출하면 onProgressUpdate가 호출된다.

                    publishProgress(toTransform);
                }



                audioRecord.stop();

            }catch(Throwable t){
                Log.e("AudioRecord", "Recording Failed");

            }



            return null;

        }



        // onProgressUpdate는 우리 엑티비티의 메인 스레드로 실행된다. 따라서 아무런 문제를 일으키지 않고 사용자 인터페이스와 상호작용할 수 있다.
        // 이번 구현에서는 onProgressUpdate가 FFT 객체를 통해 실행된 다음 데이터를 넘겨준다. 이 메소드는 최대 100픽셀의 높이로 일련의 세로선으로
        // 화면에 데이터를 그린다. 각 세로선은 배열의 요소 하나씩을 나타내므로 범위는 15.625Hz다. 첫 번째 행은 범위가 0에서 15.625Hz인 주파수를 나타내고,
        // 마지막 행은 3,984.375에서 4,000Hz인 주파수를 나타낸다.

        @Override

        protected void onProgressUpdate(double[]... toTransform) {
            //canvas.drawColor(Color.BLACK);
            for(int i = 0; i < toTransform[0].length; i++){
                int x = i;
                int downy = (int) (100 - (toTransform[0][i] * 10));
                int upy = 100;

                // canvas.drawLine(x, downy, x, upy, paint);
                value = (int)Math.abs(toTransform[0][i]*10);

                if(value>0){
                    sound_use=true;
                }
                else sound_use=false;

            }
            // imageView.invalidate();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //  getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

}
