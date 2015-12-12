package com.origintech.talkingwatch;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.renderscript.Sampler;

import com.origintech.talkingwatch.event.Event;
import com.origintech.talkingwatch.event.EventSource;
import com.origintech.talkingwatch.event.EventSourceManager;
import com.origintech.talkingwatch.event.ShakeEventSource;
import com.origintech.talkingwatch.event.TimeEventSource;
import com.origintech.talkingwatch.exception.EventSourceException;
import com.origintech.talkingwatch.speaker.MusicWatchSpeaker;
import com.origintech.talkingwatch.speaker.WatchSpeaker;

import java.util.Date;
import java.util.logging.Logger;

public class TalkingWatchService extends Service
{
    Logger logger = Logger.getLogger(TalkingWatchService.class.toString());

    private static boolean mRunning = false;
    public static boolean isRunning(){
        return mRunning;
    }

    public static String SERVICE_KEY = "talking_watch_service";
    public static String SERVICE_ENABLED_KEY = "talking_watch_service_enabled";
    public static String SHAKING_THRESHOLD_KEY = "shaking_threshold_key";
    public static String HALFTIME_ENABLED= "halftime_es_enabled";
    public static String SHARPTIME_ENABLED= "sharptime_es_enabled";

    private EventSource.OnEventHandler handler = null;

    private WatchSpeaker speaker = null;
    public WatchSpeaker getSpeaker(){
        return speaker;
    }
    @Override
    public void onCreate()
    {
        handler = new EventSource.OnEventHandler() {
            @Override
            public void handle(Event e) {
                //now the watch should talk
                Date date = new Date();
                if(mEnabled && speaker != null)
                    speaker.speak(date);
                else if(speaker == null)
                    logger.info("this speaker is null!!!");
                else if(!mEnabled)
                    logger.info("the talking sevice has been disabled");
            }
        };

        int threshold = 0;
        SharedPreferences sp = this.getSharedPreferences(SERVICE_KEY, Activity.MODE_PRIVATE);
        threshold = sp.getInt(SHAKING_THRESHOLD_KEY, 15);
        EventSource shake = new ShakeEventSource(this.getApplicationContext(), "shakeEvent");
        ((ShakeEventSource)shake).setThreshold(threshold);
        shake.registerListener(handler);
        EventSourceManager.getInstance().addEventSource(shake);

        shake = new TimeEventSource(this.getApplicationContext(),
                                        TimeEventSource.TimeType.HOUR, "hour");
        shake.registerListener(handler);
        EventSourceManager.getInstance().addEventSource(shake);

        shake = new TimeEventSource(this.getApplicationContext(),
                                        TimeEventSource.TimeType.HALF_OF_HOUR, "halfhour");
        shake.registerListener(handler);
        EventSourceManager.getInstance().addEventSource(shake);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        speaker = new MusicWatchSpeaker(this.getApplicationContext());

        SharedPreferences sp = this.getSharedPreferences(SERVICE_KEY, Activity.MODE_PRIVATE);
        mEnabled = sp.getBoolean(SERVICE_ENABLED_KEY, true);

        try{
            EventSourceManager.getInstance().startListenAll();

            boolean e = sp.getBoolean(HALFTIME_ENABLED, true);
            if(e)
                EventSourceManager.getInstance().getEventSourceByName("halfhour").startListen();
            else
                EventSourceManager.getInstance().getEventSourceByName("halfhour").stopListen();

            e = sp.getBoolean(SHARPTIME_ENABLED, true);
            if(e)
                EventSourceManager.getInstance().getEventSourceByName("hour").startListen();
            else
                EventSourceManager.getInstance().getEventSourceByName("hour").stopListen();

        }catch (Exception e) {
            e.printStackTrace();
        }

        mRunning = true;

        logger.info("the talking service started");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SharedPreferences sp = this.getSharedPreferences(SERVICE_KEY, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(SERVICE_ENABLED_KEY, mEnabled);
        editor.commit();

        mRunning = false;

        logger.info("talking service destroyed");
    }

    public class ServiceBinder extends Binder
    {
        public TalkingWatchService getService()
        {
            return TalkingWatchService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return new ServiceBinder();
    }

    private boolean mEnabled = true;

    public void setEnabled(boolean enabled){
        mEnabled = enabled;

        SharedPreferences sp = this.getSharedPreferences(SERVICE_KEY, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(SERVICE_ENABLED_KEY, enabled);
        editor.commit();

        onToggled(mEnabled);
    }
    public boolean getEnabled(){
        return mEnabled;
    }

    protected void onToggled(boolean enabled){
//        if(!enabled){
//            EventSourceManager.getInstance().stopListenAll();
//        }
//        else{
//            try {
//                EventSourceManager.getInstance().startListenAll();
//            } catch (EventSourceException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public void toggleHalfTime(boolean enable){
        EventSource es = EventSourceManager.getInstance().getEventSourceByName("halfhour");
        if(es == null)
            return;
        try{
            if(enable)
                es.startListen();
            else
                es.stopListen();
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            SharedPreferences sp = this.getSharedPreferences(SERVICE_KEY, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(HALFTIME_ENABLED, enable);
            editor.commit();
        }
    }
    public void toggleShapeTime(boolean enable){
        EventSource es = EventSourceManager.getInstance().getEventSourceByName("hour");
        if(es == null)
            return;
        try {
            if (enable)
                es.startListen();
            else
                es.stopListen();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            SharedPreferences sp = this.getSharedPreferences(SERVICE_KEY, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(SHARPTIME_ENABLED, enable);
            editor.commit();
        }
    }
    public void changeSensitity(int value){
        ShakeEventSource es = (ShakeEventSource) EventSourceManager.getInstance()
                .getEventSourceByName("shakeEvent");
        if(es == null)
            return;
        es.setThreshold(value);
        SharedPreferences sp = this.getSharedPreferences(SERVICE_KEY, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(SHAKING_THRESHOLD_KEY, value);
        editor.commit();
    }
}
