package com.origintech.talkingwatch.event;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.origintech.talkingwatch.exception.ShakeNotSupportedException;

import java.util.logging.Logger;

/**
 * Created by shuaibincheng on 15/9/21.
 */
public class ShakeEventSource extends EventSource
{
    Logger logger = Logger.getLogger(this.getClass().toString());

    private Context context = null;
    public ShakeEventSource(Context context, String name)
    {
        super(name);
        this.context = context;
    }

    private SensorManager sm = null;
    private Sensor sensor = null;


    public float getShakeThreshold() {
        return shakeThreshold;
    }

    private float shakeThreshold = 15;
    public void setThreshold(float value){
        shakeThreshold = value;
        logger.info("the threshold has changed to " + value);
    }
    private SensorEventListener listener = new SensorEventListener()
    {
        @Override
        public void onSensorChanged(SensorEvent event)
        {
            float dx = event.values[0];
            float dy = event.values[1];
            float dz = event.values[2];

            if(Math.abs(dx) > shakeThreshold || Math.abs(dy) > shakeThreshold
                    || Math.abs(dz) > shakeThreshold)
            {
                onShake(new Event());
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {

        }
    };
    @Override
    public void startListen() throws ShakeNotSupportedException
    {
        logger.info(">>>>start to listen the shaking event");
        sm = (SensorManager)this.context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(sensor == null)
        {
            throw new ShakeNotSupportedException("the sensor is not available");
        }

        sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        super.startListen();
    }

    @Override
    public void stopListen()
    {
        logger.info(">>>>stop to listen the shaking event");
        if(sm == null || sensor == null)
            return;
        sm.unregisterListener(listener);
        super.stopListen();
    }
}
