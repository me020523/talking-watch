package com.origintech.talkingwatch.speaker;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.origintech.talkingwatch.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by shuaibincheng on 15/11/22.
 */
public class MusicWatchSpeaker extends WatchSpeaker {

    MediaPlayer mPlayer = null;
    Context mContext = null;

    private final int[] HOUR_INDEX = {
            R.raw.t00,
            R.raw.t1, R.raw.t2, R.raw.t03, R.raw.t04,
            R.raw.t05, R.raw.t06, R.raw.t07, R.raw.t08,
            R.raw.t09, R.raw.t10, R.raw.t11, R.raw.t12
    };
    private final int[] MIN_INDEX = {
            R.raw.t00,
            R.raw.t01, R.raw.t02, R.raw.t03, R.raw.t04, R.raw.t05,
            R.raw.t06, R.raw.t07, R.raw.t08, R.raw.t09, R.raw.t10,
            0,0,0,0,0,0,0,0,0,R.raw.t20,
            0,0,0,0,0,0,0,0,0,R.raw.t30,
            0,0,0,0,0,0,0,0,0,R.raw.t40,
            0,0,0,0,0,0,0,0,0,R.raw.t50,
    };
    public MusicWatchSpeaker(Context context){
        mContext = context;
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(!mWavFiles.isEmpty()){
                    AssetFileDescriptor afs = mWavFiles.remove(0);
                    try {
                        mPlayer.reset();
                        mPlayer.setDataSource(afs.getFileDescriptor(),afs.getStartOffset(),afs.getLength());
                        mPlayer.prepare();
                        mPlayer.start();
                        afs.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    mPlayer.stop();
                    mPlayer.reset();
                }

            }
        });
    }


    @Override
    public void speak(Date date) {
        int hour = date.getHours();
        int minute = date.getMinutes();
        mWavFiles.clear();

        if(mPlayer.isPlaying()){
            mPlayer.stop();
            mPlayer.reset();
        }

        loadWavRes(hour, minute);

        AssetFileDescriptor afs = null;
        afs = mContext.getResources().openRawResourceFd(R.raw.timenow);
        try {
            mPlayer.reset();
            mPlayer.setDataSource(afs.getFileDescriptor(),afs.getStartOffset(),afs.getLength());
            mPlayer.prepare();
            mPlayer.start();
            afs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private List<AssetFileDescriptor> mWavFiles = Collections.synchronizedList(
            new ArrayList<AssetFileDescriptor>());
    public void loadWavRes(int hour, int minute){
        AssetFileDescriptor afs = null;

        if(hour >=0 && hour < 6 ){
            afs = mContext.getResources().openRawResourceFd(R.raw.am0);
        }
        else if(hour >= 6 && hour < 12){
            afs = mContext.getResources().openRawResourceFd(R.raw.am1);
        }
        else if(hour >= 12 && hour < 18){
            afs = mContext.getResources().openRawResourceFd(R.raw.pm);
        }
        else {
            afs = mContext.getResources().openRawResourceFd(R.raw.em);
        }
        mWavFiles.add(afs);

        afs = mContext.getResources().openRawResourceFd(HOUR_INDEX[hour % 12]);
        mWavFiles.add(afs);
        afs = mContext.getResources().openRawResourceFd(R.raw.point);
        mWavFiles.add(afs);

        int minHigh = (minute / 10)*10;
        int minLow = minute % 10;
        if(minHigh == 0 && minLow == 0){
            afs = mContext.getResources().openRawResourceFd(MIN_INDEX[0]);
            mWavFiles.add(afs);
        }
        else if(minHigh == 0 && minLow != 0){
            afs = mContext.getResources().openRawResourceFd(MIN_INDEX[minLow]);
            mWavFiles.add(afs);
        }
        else{
            afs = mContext.getResources().openRawResourceFd(MIN_INDEX[minHigh]);
            mWavFiles.add(afs);
            afs = mContext.getResources().openRawResourceFd(MIN_INDEX[minLow]);
            mWavFiles.add(afs);
        }

        afs = mContext.getResources().openRawResourceFd(R.raw.min);
        mWavFiles.add(afs);
    }
}
