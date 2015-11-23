package com.origintech.talkingwatch.event;

import android.content.Context;
import android.os.Handler;

import com.origintech.talkingwatch.exception.ShakeNotSupportedException;

import java.util.Date;

/**
 * Created by shuaibincheng on 15/9/23.
 */
public class TimeEventSource extends EventSource
{
    public enum TimeType
    {
        HOUR,
        HALF_OF_HOUR
    }

    private TimeType type = TimeType.HOUR;
    private Context context = null;
    public TimeEventSource(Context context,TimeType type, String name)
    {
        super(name);
        this.type = type;
        this.context = context;
    }

    private Handler handler = new Handler();
    private Runnable delayJob = new Runnable()
    {
        @Override
        public void run()
        {
            Date current = new Date();
            int minute = current.getMinutes();
            int second = current.getSeconds();
            if(type == TimeType.HALF_OF_HOUR && minute == 30 && second == 0)
                onShake(new Event());
            else if(type == TimeType.HOUR && minute == 0 && second == 0)
                onShake(new Event());

            handler.postDelayed(delayJob, 1000);
        }
    };
    @Override
    public void startListen() throws ShakeNotSupportedException
    {
        handler.postDelayed(delayJob,1000);
    }

    @Override
    public void stopListen() {
        handler.removeCallbacks(delayJob);
    }
}
