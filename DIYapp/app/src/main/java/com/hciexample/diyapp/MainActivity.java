package com.hciexample.diyapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;



public class MainActivity extends AppCompatActivity implements Button.OnClickListener{
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TextView tv = (TextView) findViewById(R.id.textView);
        Button recorder = (Button) findViewById(R.id.button);
        Button piano = (Button) findViewById(R.id.button8);
        Button ocarina = (Button) findViewById(R.id.button2);
        recorder.setOnClickListener(this);
        piano.setOnClickListener(this);
        ocarina.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO
                );
            }
        }



    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.button:
                Intent intent = new Intent(this, RecorderActivity.class);
                //다른 클래스로 전환하는 코드이다.
                startActivity(intent);
                break;

            case R.id.button8:
                Intent intent1 = new Intent(this, PianoActivity.class);
                startActivity(intent1);
                break;

            case R.id.button2:
                Intent intent2 = new Intent(this, OcarinaActivity.class);
                startActivity(intent2);
                break;


        }
    }

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    //권한 승인

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //승인 거부
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

*/
}
