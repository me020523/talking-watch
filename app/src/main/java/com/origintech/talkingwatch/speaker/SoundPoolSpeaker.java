package com.origintech.talkingwatch.speaker;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.origintech.talkingwatch.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Created by shuaibincheng on 16/5/3.
 */
public class SoundPoolSpeaker extends WatchSpeaker {
    private Logger logger = Logger.getLogger(this.getClass().toString());
    private final int[] HOUR_INDEX = {
            R.raw.t12,
            R.raw.t1, R.raw.t2, R.raw.t03, R.raw.t04,
            R.raw.t05, R.raw.t06, R.raw.t07, R.raw.t08,
            R.raw.t09, R.raw.t10, R.raw.t11
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

    private final int[] resId = {
            R.raw.am0, R.raw.am1, R.raw.em, R.raw.min, R.raw.pm,
            R.raw.point, R.raw.t00, R.raw.t1, R.raw.t2, R.raw.t02,
            R.raw.t03, R.raw.t04, R.raw.t05, R.raw.t06, R.raw.t07,
            R.raw.t08, R.raw.t09, R.raw.t10, R.raw.t11, R.raw.t12,
            R.raw.t20, R.raw.t30, R.raw.t40, R.raw.t50, R.raw.timenow,
            R.raw.toll, R.raw.t01
    };
    private SoundPool pool = null;
    private Context context = null;
    private HashMap<Integer, Integer> rawToResId = null;

    public SoundPoolSpeaker(Context context){
        pool = new SoundPool(resId.length, AudioManager.STREAM_MUSIC, 0);
        this.context = context;
        loadMusicRes();
    }

    protected void loadMusicRes(){
        if(pool == null)
            return;
        for(int i = 0; i < resId.length; i++){
            int id = pool.load(this.context, resId[i],1);
            rawToResId.put(resId[i], id);
        }
    }

    private List<Integer> playIDList = Collections.synchronizedList(new ArrayList<Integer>());
    private volatile AtomicInteger currentPlayId = new AtomicInteger(-1);
    @Override
    public void speak(Date date) {
        logger.info("now let's play the time");

        playIDList.clear();
        if(currentPlayId.get() != -1){
            pool.stop(currentPlayId.get());
        }

        int hour = date.getHours();
        int min = date.getMinutes();

        prepareMusicRes(hour, min);
    }
    private void prepareMusicRes(int hour, int min){
        playIDList.add(rawToResId.get(R.raw.timenow));
        int resId;
        if(hour >=0 && hour < 6 ){
            resId = R.raw.am0;
        }
        else if(hour >= 6 && hour < 12){
            resId = R.raw.am1;
        }
        else if(hour >= 12 && hour < 18){
            resId = R.raw.pm;
        }
        else {
            resId = R.raw.em;
        }
        playIDList.add(rawToResId.get(resId));
        resId = HOUR_INDEX[hour % 12];
        playIDList.add(rawToResId.get(resId));
        playIDList.add(rawToResId.get(R.raw.point));

        int minHigh = (min / 10)*10;
        int minLow = min % 10;
        if(minHigh == 0 && minLow != 0){
            resId = MIN_INDEX[minLow];
            playIDList.add(rawToResId.get(resId));
        }
        else{
            resId = MIN_INDEX[minHigh];
            playIDList.add(rawToResId.get(resId));
            if(minLow != 0){
                resId = MIN_INDEX[minLow];
                playIDList.add(rawToResId.get(resId));
            }
        }

        playIDList.add(rawToResId.get(R.raw.min));
    }
}
