package com.origintech.talkingwatch.event;

import com.origintech.talkingwatch.exception.EventSourceException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by shuaibincheng on 15/9/22.
 */
public class EventSourceManager
{
    private static final EventSourceManager instance = new EventSourceManager();
    public static EventSourceManager getInstance()
    {
        return instance;
    }

    List<EventSource> sources = (List<EventSource>)
                                Collections.synchronizedList(new ArrayList<EventSource>());
    public void addEventSource(EventSource es)
    {
        sources.add(es);
    }

    public void removeEventSource(EventSource es)
    {
        sources.remove(es);
    }

    public void startListenAll() throws EventSourceException
    {
        for(EventSource es : sources)
        {
            es.startListen();
        }
    }
    public void stopListenAll()
    {
        for(EventSource es : sources)
        {
            es.stopListen();
        }
    }
}
