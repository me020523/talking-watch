package com.origintech.talkingwatch.exception;

/**
 * Created by shuaibincheng on 15/9/21.
 */
public class ShakeNotSupportedException extends EventSourceException
{
    public ShakeNotSupportedException(String detailMessage)
    {
        super(detailMessage);
    }
}
