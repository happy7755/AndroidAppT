package com.hciexample.diyapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/*PianoActivity, Key, MidiFileCreator, PianoView*/

public class PianoActivity  extends Activity {
    public static final String TAG = "ANDROID INSTRUMENTS"; //태그
    public static final int KEY_MARGIN = 16; // 건반간 간격 픽셀
    public static final int KEY_LENGTH = 7;//7; // C Major 스케일 음계의 수
    public static final int OCTAVE_COUNT = 1; // 옥타브 수
    public static final String KEYS = "cdefgab"; // C Major 스케일 음계
    public static final String[] PITCHES = new String[]{"c", "cs", "d", "ds", "e", "f", "fs", "g", "gs", "a", "as", "b"}; // Chromatic 스케일 음계

    // General MIDI Programs
    public static final String[] GM_PROGRAMS = new String[] {
            "Acoustic Grand Piano", "Bright Acoustic Piano",
            "Electric Grand Piano", "Honky-tonk Piano", "Electric Piano 1",
            "Electric Piano 2", "Harpsichord", "Clavinet", "Celesta",
            "Glockenspiel", "Music Box", "Vibraphone", "Marimba", "Xylophone",
            "Tubular Bells", "Dulcimer", "Drawbar Organ", "Percussive Organ",
            "Rock Organ", "Church Organ", "Reed Organ", "Accordion",
            "Harmonica", "Tango Accordion", "Acoustic Guitar (nylon)",
            "Acoustic Guitar (steel)", "Electric Guitar (jazz)",
            "Electric Guitar (clean)", "Electric Guitar (muted)",
            "Overdriven Guitar", "Distortion Guitar", "Guitar harmonics",
            "Acoustic Bass", "Electric Bass (finger)", "Electric Bass (pick)",
            "Fretless Bass", "Slap Bass 1", "Slap Bass 2", "Synth Bass 1",
            "Synth Bass 2", "Violin", "Viola", "Cello", "Contrabass",
            "Tremolo Strings", "Pizzicato Strings", "Orchestral Harp",
            "Timpani", "String Ensemble 1", "String Ensemble 2",
            "Synth Strings 1", "Synth Strings 2", "Choir Aahs", "Voice Oohs",
            "Synth Choir", "Orchestra Hit", "Trumpet", "Trombone", "Tuba",
            "Muted Trumpet", "French Horn", "Brass Section", "Synth Brass 1",
            "Synth Brass 2", "Soprano Sax", "Alto Sax", "Tenor Sax",
            "Baritone Sax", "Oboe", "English Horn", "Bassoon", "Clarinet",
            "Piccolo", "Flute", "Recorder", "Pan Flute", "Blown Bottle",
            "Shakuhachi", "Whistle", "Ocarina", "Lead 1 (square)",
            "Lead 2 (sawtooth)", "Lead 3 (calliope)", "Lead 4 (chiff)",
            "Lead 5 (charang)", "Lead 6 (voice)", "Lead 7 (fifths)",
            "Lead 8 (bass + lead)", "Pad 1 (new age)", "Pad 2 (warm)",
            "Pad 3 (polysynth)", "Pad 4 (choir)", "Pad 5 (bowed)",
            "Pad 6 (metallic)", "Pad 7 (halo)", "Pad 8 (sweep)", "FX 1 (rain)",
            "FX 2 (soundtrack)", "FX 3 (crystal)", "FX 4 (atmosphere)",
            "FX 5 (brightness)", "FX 6 (goblins)", "FX 7 (echoes)",
            "FX 8 (sci-fi)", "Sitar", "Banjo", "Shamisen", "Koto", "Kalimba",
            "Bag pipe", "Fiddle", "Shanai", "Tinkle Bell", "Agogo",
            "Steel Drums", "Woodblock", "Taiko Drum", "Melodic Tom",
            "Synth Drum", "Reverse Cymbal", "Guitar Fret Noise",
            "Breath Noise", "Seashore", "Bird Tweet", "Telephone Ring",
            "Helicopter", "Applause", "Gunshot" };

    private ViewGroup viewKeys, keysContainer, swipeArea;
    // private ImageView imgKey;

    private SoundPool sPool; // 사운드 풀
    private Map<String, Integer> sMap;
    private AudioManager mAudioManager;

    private int imgKeyWidth, programNo, octaveShift;
    private SharedPreferences pref;

    @SuppressWarnings("unused")
    private int sKey0, sKey1, sKey2;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = getSharedPreferences("net.hyeongkyu.android.androInstruments", Activity.MODE_PRIVATE);

        setContentView(R.layout.activity_piano);

        viewKeys = (ViewGroup) findViewById(R.id.view_key);
        viewKeys.setOnTouchListener(keysTouchListener);

        keysContainer = (ViewGroup) findViewById(R.id.keys_container);
        keysContainer.setOnTouchListener(keysContainerTouchListener);
        //int viewKeysScrollX = pref.getInt("viewKeysScrollX", 0);
        //if(viewKeysScrollX>0)((HorizontalScrollView)viewKeys).smoothScrollTo(viewKeysScrollX, 0);

        //imgKey = (ImageView) findViewById(R.id.img_key);
        //imgKeyWidth = pref.getInt("imgKeyWidth", 4000);
        //imgKey.setLayoutParams(new LinearLayout.LayoutParams(imgKeyWidth, LayoutParams.FILL_PARENT));

        //swipeArea = (ViewGroup) findViewById(R.id.swipe_area);

        resetSoundPool();
        mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);

        boolean isFirstExec = pref.getBoolean("isFirstExec", true);
        if(isFirstExec){
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirstExec", false);
            editor.commit();
        }
    }

    private View.OnTouchListener keysTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            int action = event.getAction();
            int downPointerIndex = -1;
            int upPointerIndex = -1;

            if (action == MotionEvent.ACTION_DOWN) downPointerIndex = 0;
            else if(action == MotionEvent.ACTION_POINTER_DOWN) downPointerIndex = 0;
            else if(action == MotionEvent.ACTION_POINTER_1_DOWN) downPointerIndex = 0;
            else if(action == MotionEvent.ACTION_POINTER_2_DOWN) downPointerIndex = 1;
            else if(action == MotionEvent.ACTION_POINTER_3_DOWN) downPointerIndex = 2;
            else if(action == MotionEvent.ACTION_UP) upPointerIndex = 0;
            else if(action == MotionEvent.ACTION_POINTER_UP) upPointerIndex = 0;
            else if(action == MotionEvent.ACTION_POINTER_1_UP) upPointerIndex = 0;
            else if(action == MotionEvent.ACTION_POINTER_2_UP) upPointerIndex = 1;
            else if(action == MotionEvent.ACTION_POINTER_3_UP) upPointerIndex = 2;

            //int scrollAreaBottom = swipeArea.getBottom(); // 스크롤 영역의 하단
            // Toast.makeText(getApplicationContext(), String.valueOf(scrollAreaBottom) , Toast.LENGTH_SHORT).show();


            if(downPointerIndex>=0){
                int scrollWidth = keysContainer.getRight(); // 스크롤 폭
                int keyWhiteWidth = (int) (((float) scrollWidth) / (KEY_LENGTH * OCTAVE_COUNT)); // 건반 하나당 할당 폭
                int octaveWidth = (int)((float)scrollWidth/OCTAVE_COUNT); // 옥타브당 폭

                int scrollX = view.getScrollX(); // 스크롤 위치
                int bottom = view.getBottom();//-scrollAreaBottom; // 건반 높이

                float touchX = event.getX(downPointerIndex);
                float touchY = event.getY(downPointerIndex);//-scrollAreaBottom;
                if(touchY<0) return false;

                int touchKeyX = scrollX + (int) touchX; // 이미지 상의 터치 X 좌표
                int touchKeyPos = (touchKeyX / keyWhiteWidth); // 몇번째 흰 건반인가
                int touchYPosPercent = (int)((touchY/((float)bottom))*100); // Y좌표는 height 대비  몇 % 지점에 찍혔는가

                //옥타브 계산
                int octave = (touchKeyX/octaveWidth)+1;

                String key = ""+KEYS.charAt(touchKeyPos % (KEY_LENGTH));
                if(touchYPosPercent<55){
                    //전체 높이의 55% 이내에 찍혔으면, 검은 건반을 눌렀을 가능성이 있음.
                    //각 흰 건반의 경계로부터 30% 이내에 맞았을 경우, 건반의 경계를 파악하여, 그 자리에 검은 건반이 있는지 확인한다.
                    int nearLineX1 = ((touchKeyX/keyWhiteWidth)*keyWhiteWidth);
                    int nearLineX2 = (((touchKeyX/keyWhiteWidth)+1)*keyWhiteWidth);
                    if((touchKeyX-nearLineX1)<(nearLineX2-touchKeyX)){
                        //아랫쪽 건반에 가까울 경우
                        if(((touchKeyX-nearLineX1)/(float)keyWhiteWidth)<0.3f){
                            //검은 건반 유효(flat)
                            if("cf".indexOf(key)<0){
                                int keyCharPos = KEYS.indexOf(key)-1;
                                if(keyCharPos<0) keyCharPos = KEY_LENGTH;
                                key = ""+KEYS.charAt(keyCharPos);
                                key += "s";
                            }
                        }
                    }else{
                        //윗쪽 건반에 가까울 경우
                        if(((nearLineX2-touchKeyX)/(float)keyWhiteWidth)<0.3f){
                            //검은 건반 유효(sharp)
                            if("eb".indexOf(key)<0) key += "s";
                        }
                    }
                }
                key += octave;

                int soundKey = sMap.get(key);
                float streamCurrent = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                float streamMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                float streamVolume = streamCurrent / streamMax;
                //int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                int sKey = sPool.play(soundKey, streamVolume, streamVolume, 0, 0, 1);

                if(downPointerIndex==0) sKey0 = sKey;
                else if(downPointerIndex==1) sKey1 = sKey;
                else if(downPointerIndex==2) sKey2 = sKey;
            }
            else if(upPointerIndex>=0){
                // 사운드를 중지 시키면 소리가 너무 끊어진다.
				/*
				if(upPointerIndex==0){
					if(sKey0>0) sPool.stop(sKey0);
					sKey0 = 0;
				}else if(upPointerIndex==1){
					if(sKey1>0) sPool.stop(sKey1);
					sKey1 = 0;
				}else if(upPointerIndex==2){
					if(sKey2>0) sPool.stop(sKey2);
					sKey2 = 0;
				}
				*/
            }

            //터치 영역이 스크롤 영역인지 확인
            //float touchY = event.getY();
            //if(touchY>scrollAreaBottom) return true;
            return false;
        }
    };

    private View.OnTouchListener keysContainerTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    };


    /**
     * General Midi Program 목록을 띄운다.
     */
    private void showPrograms(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("general_midi_programs");
        alert.setItems(GM_PROGRAMS, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("programNo", which);
                editor.commit();

                resetSoundPool();
            }
        });
        alert.show();
    }

    /**
     * Sound Pool을 재설정합니다.
     */
    private void resetSoundPool(){
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("악기를 설정 중입니다.");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Integer> tmpMap = new HashMap<String, Integer>();
                SoundPool tmpPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);

                //미디 파일 생성
                try {
                    programNo = pref.getInt("programNo", 0);
                    octaveShift = pref.getInt("octaveShift", 2);
                    MidiFileCreator midiFileCreator = new MidiFileCreator(PianoActivity.this);
                    midiFileCreator.createMidiFiles(programNo, octaveShift);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String dir = getDir("", MODE_PRIVATE).getAbsolutePath();
                for(int i=1;i<=OCTAVE_COUNT;i++){
                    for (int j=0;j<PITCHES.length;j++){
                        String soundPath = dir+ File.separator+PITCHES[j]+i+".mid";
                        tmpMap.put(PITCHES[j]+i, tmpPool.load(soundPath, 1));
                    }
                }

                sMap = tmpMap;
                sPool = tmpPool;

                progress.dismiss();
            }
        });
        progress.show();
        thread.start();
    }


}