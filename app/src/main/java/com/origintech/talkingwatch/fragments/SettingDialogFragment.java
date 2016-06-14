package com.origintech.talkingwatch.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.origintech.talkingwatch.R;
import com.origintech.talkingwatch.event.EventSource;
import com.origintech.talkingwatch.event.EventSourceManager;
import com.origintech.talkingwatch.event.ShakeEventSource;

/**
 * Created by shuaibincheng on 15/12/12.
 */
public class SettingDialogFragment extends DialogFragment {

    public interface SettingEventHandler {
        void onHalfTimeToggled(boolean enable);
        void onShapeTimeToggled(boolean enable);
        void onSensitivityChanged(int value);
        void onServiceRestart();
    }

    private CheckBox mShapeTimeCheck = null;
    private CheckBox mHalfTimeCheck = null;
    private SeekBar mSensativeSetting = null;
    private Button mServiceRestart = null;

    private SettingEventHandler mHandler = null;
    public void setEventHandler(SettingEventHandler handler){
        mHandler = handler;
    }

    public SettingDialogFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.setting_dialog, container);

        mShapeTimeCheck = (CheckBox)v.findViewById(R.id.sharp_time_enable);
        mHalfTimeCheck = (CheckBox)v.findViewById(R.id.half_time_enable);
        mSensativeSetting = (SeekBar)v.findViewById(R.id.seekBar);
        mServiceRestart = (Button)v.findViewById(R.id.restart_service);

        return v;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d =  super.onCreateDialog(savedInstanceState);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return d;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventSource es = EventSourceManager.getInstance().getEventSourceByName("hour");
        if(es != null && es.isEnable()){
            mShapeTimeCheck.setChecked(true);
        }
        else{
            mShapeTimeCheck.setChecked(false);
        }

        es = EventSourceManager.getInstance().getEventSourceByName("halfhour");
        if(es != null && es.isEnable()){
            mHalfTimeCheck.setChecked(true);
        }else{
            mHalfTimeCheck.setChecked(false);
        }

        es = EventSourceManager.getInstance().getEventSourceByName("shakeEvent");
        if(es != null){
            mSensativeSetting.setProgress((int)((ShakeEventSource)es).getShakeThreshold());
        }

        mHalfTimeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mHandler != null){
                    mHandler.onHalfTimeToggled(isChecked);
                }
            }
        });

        mShapeTimeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mHandler != null){
                    mHandler.onShapeTimeToggled(isChecked);
                }
            }
        });

        mSensativeSetting.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 10)
                    progress = 10;
                if (mHandler != null)
                    mHandler.onSensitivityChanged(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSensativeSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mHandler != null)
                    mHandler.onServiceRestart();
            }
        });
    }
}
