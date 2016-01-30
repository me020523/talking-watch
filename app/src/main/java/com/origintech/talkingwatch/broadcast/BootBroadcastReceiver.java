package com.origintech.talkingwatch.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.origintech.talkingwatch.TalkingWatchService;

import java.util.logging.Logger;

/**
 * Created by shuaibincheng on 16/1/30.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    Logger logger = Logger.getLogger(this.getClass().toString());
    @Override
    public void onReceive(Context context, Intent intent) {
        logger.info("device rebooted, so let's start the service");
        Intent service = new Intent(context, TalkingWatchService.class);
        context.startService(service);
    }
}
