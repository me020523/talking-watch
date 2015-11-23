package com.origintech.talkingwatch.speaker;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;
import com.shoushuo.android.tts.ITts;

import java.util.Date;

/**
 * Created by shuaibincheng on 15/9/22.
 */
public class TTSWatchSpeaker extends WatchSpeaker
{
    private Context context = null;
    private ITts ttsService = null;
    private boolean ttsBound = false;

    private ServiceConnection connection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName className, IBinder iservice) {
            System.out.println("connected the tts service");
            ttsService = ITts.Stub.asInterface(iservice);
            ttsBound = true;
            try
            {
                ttsService.initialize();
            }
            catch (RemoteException e) { }
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
            ttsService = null;
            ttsBound = false;
        }
    };

    public TTSWatchSpeaker(Context context)
    {
        this.context = context;

        if(!ttsBound)
        {
            String actionName = "com.shoushuo.android.tts.intent.action.InvokeTts";
            Intent intent = new Intent(actionName);
            intent.setPackage("com.shoushuo.android.tts");
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
    }

    public void speak(Date date) {
        try
        {
            StringBuilder sb = new StringBuilder();
            sb.append("现在时间");
            sb.append(date.getHours());
            sb.append("点");
            sb.append(date.getMinutes());
            sb.append("分");
            String text = sb.toString();
            System.out.println(text);
            if(ttsBound)
                ttsService.speak(text, TextToSpeech.QUEUE_FLUSH);
        }
        catch (RemoteException e)
        {

        }
    }

    public void release()
    {
        if (ttsBound ) {
            ttsBound = false;
            context.unbindService(connection) ;
        }
    }
}
