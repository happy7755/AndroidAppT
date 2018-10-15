package com.hciexample.diyapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ToggleButton;

/**
 * Created by csculley on 10/28/17.
 */

public class Ocarina12HoleFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        return inflater.inflate(R.layout.ocarina_12_hole, container, false);
    }
    public void onStart() {
        super.onStart();
        ImageButton b1 = (ImageButton) getActivity().findViewById(R.id.button1);
        ImageButton b2 = (ImageButton) getActivity().findViewById(R.id.button2);
        ImageButton b3 = (ImageButton) getActivity().findViewById(R.id.button3);
        ImageButton b4 = (ImageButton) getActivity().findViewById(R.id.button4);
        ImageButton b5 = (ImageButton) getActivity().findViewById(R.id.button5);
        ImageButton b6 = (ImageButton) getActivity().findViewById(R.id.button6);
        ImageButton b7 = (ImageButton) getActivity().findViewById(R.id.button7);
        ImageButton b8 = (ImageButton) getActivity().findViewById(R.id.button8);
        //Buttons 9 and 10 are volume buttons
        ImageButton b11 = (ImageButton) getActivity().findViewById(R.id.button11);
        ImageButton b12 = (ImageButton) getActivity().findViewById(R.id.button12);

        ToggleButton volLock = (ToggleButton) getActivity().findViewById(R.id.vol_lock);

        volLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    OcarinaActivity.setVolumeLock(true);
                } else {
                    OcarinaActivity.setVolumeLock(false);
                }
            }
        });

        b1.setOnTouchListener(OcarinaActivity.getTouchListener());
        b2.setOnTouchListener(OcarinaActivity.getTouchListener());
        b3.setOnTouchListener(OcarinaActivity.getTouchListener());
        b4.setOnTouchListener(OcarinaActivity.getTouchListener());
        b5.setOnTouchListener(OcarinaActivity.getTouchListener());
        b6.setOnTouchListener(OcarinaActivity.getTouchListener());
        b7.setOnTouchListener(OcarinaActivity.getTouchListener());
        b8.setOnTouchListener(OcarinaActivity.getTouchListener());
        //Buttons 9 and 10 are volume buttons
        b11.setOnTouchListener(OcarinaActivity.getTouchListener());
        b12.setOnTouchListener(OcarinaActivity.getTouchListener());
    }
}