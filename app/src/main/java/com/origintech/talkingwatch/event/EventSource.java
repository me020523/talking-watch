package com.origintech.talkingwatch.event;

import com.origintech.talkingwatch.exception.ShakeNotSupportedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by shuaibincheng on 15/9/21.
 */
public class EventSource
{
    public interface OnEventHandler
    {
        void handle(Event e);
    }

    public EventSource(){}
    private String name = null; //the name of this event source
    public EventSource(String name)
    {
        this.name = name;
    }

    private List<OnEventHandler> listener = (List<OnEventHandler>)
                                Collections.synchronizedList(new ArrayList<OnEventHandler>());
    public void startListen() throws ShakeNotSupportedException
    {

    }
    public void registerListener(OnEventHandler handler)
    {
        listener.add(handler);
    }
    public void unregisterListener(OnEventHandler handler)
    {
        listener.remove(handler);
    }
    public void clearListener()
    {
        listener.clear();
    }
    public void stopListen()
    {

    }

    protected void onShake(Event e)
    {
        for(OnEventHandler handler : listener)
        {
            handler.handle(e);
        }
    }
}
