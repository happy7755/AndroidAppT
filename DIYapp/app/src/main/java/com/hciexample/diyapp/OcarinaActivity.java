package com.hciexample.diyapp;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;

/*OcarinaActivity, Note, PlayAudio, Ocarina4hole, Ocarina12hole, OcarinaTouchListener*/

public class OcarinaActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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

    static boolean sound_use=false;

    RecordAudio2 recordTask;


    protected static OcarinaTouchListener touchListener;
    protected static boolean volumeLockEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocarina);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setFragment(new Ocarina4HoleFragment());
        touchListener = new OcarinaTouchListener("4Hole");
        PlayAudio.start();

        transformer=new RealDoubleFFT(blockSize);

        recordTask = new RecordAudio2();
        recordTask.execute();

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (volumeLockEnabled) {
            int action = event.getAction();
            int keyCode = event.getKeyCode();

            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    if (action == KeyEvent.ACTION_DOWN) {
                        touchListener.setButtons(9, true);
                        dispatchTouchEvent(MotionEvent.obtain((long) SystemClock.uptimeMillis(),(long) SystemClock.uptimeMillis() + 10,MotionEvent.ACTION_DOWN, 10000.0f, 0.0f, 0));
                    } else if (action == KeyEvent.ACTION_UP) {
                        touchListener.setButtons(9, false);
                        dispatchTouchEvent(MotionEvent.obtain((long) SystemClock.uptimeMillis(),(long) SystemClock.uptimeMillis() + 10,MotionEvent.ACTION_UP, 10000.0f, 0.0f, 0));
                    }
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if (action == KeyEvent.ACTION_DOWN) {
                        touchListener.setButtons(10, true);
                        dispatchTouchEvent(MotionEvent.obtain((long) SystemClock.uptimeMillis(),(long) SystemClock.uptimeMillis() + 10,MotionEvent.ACTION_DOWN, 10000.0f, 0.0f, 0));
                    } else if (action == KeyEvent.ACTION_UP) {
                        touchListener.setButtons(10, false);
                        dispatchTouchEvent(MotionEvent.obtain((long) SystemClock.uptimeMillis(),(long) SystemClock.uptimeMillis() + 10,MotionEvent.ACTION_UP, 10000.0f, 0.0f, 0));
                    }
                    return true;
                default:
                    return super.dispatchKeyEvent(event);
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_item_ocarina_12_hole) {
            setFragment(new Ocarina12HoleFragment());
            touchListener = new OcarinaTouchListener("12Hole");
        } else if (id == R.id.nav_item_ocarina_4_hole) {
            setFragment(new Ocarina4HoleFragment());
            touchListener = new OcarinaTouchListener("4Hole");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void setFragment(Fragment fragment) {
        android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.content_frame, fragment);
        t.commit();
    }

    static public void setVolumeLock(boolean b) {
        volumeLockEnabled = b;
    }

    static public boolean getVolumeLock() {
        return volumeLockEnabled;
    }

    static OcarinaTouchListener getTouchListener() {
        return touchListener;
    }


    private class RecordAudio2 extends AsyncTask<Void, double[], Void> {

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


}