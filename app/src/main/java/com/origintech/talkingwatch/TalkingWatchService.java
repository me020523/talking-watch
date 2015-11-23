package com.origintech.talkingwatch;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;

import com.origintech.talkingwatch.event.Event;
import com.origintech.talkingwatch.event.EventSource;
import com.origintech.talkingwatch.event.EventSourceManager;
import com.origintech.talkingwatch.event.ShakeEventSource;
import com.origintech.talkingwatch.event.TimeEventSource;
import com.origintech.talkingwatch.exception.EventSourceException;
import com.origintech.talkingwatch.speaker.MusicWatchSpeaker;
import com.origintech.talkingwatch.speaker.WatchSpeaker;

import java.util.Date;

public class TalkingWatchService extends Service
{

    public static String SERVICE_KEY = "talking_watch_service";

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
                /*Toast.makeText(TalkingWatchService.this.getApplicationContext(),
                        "the watch is talking", Toast.LENGTH_SHORT).show();*/
                Date date = new Date();

                speaker.speak(date);
            }
        };

        EventSource shake = new ShakeEventSource(this.getApplicationContext(), "shakeEvent");
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
        try
        {
            EventSourceManager.getInstance().startListenAll();
        }
        catch (EventSourceException e)
        {
            e.printStackTrace();
        }

        SharedPreferences sp = this.getSharedPreferences(SERVICE_KEY, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(SERVICE_KEY, true);
        editor.commit();

        return START_STICKY;
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
}
