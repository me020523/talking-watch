package com.origintech.talkingwatch;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity
{
    Logger logger = Logger.getLogger(this.getClass().toString());

    public interface ServiceListener{
        void onServiceConnected(TalkingWatchService service);
        void onServiceDisConnected(TalkingWatchService service);
    }
    private TalkingWatchService mService = null;
    public TalkingWatchService getTalkingService(){
        return mService;
    }

    private List<ServiceListener> mServiceConnListener = new ArrayList<>();

    public void registerServiceConnListener(ServiceListener sc){
        mServiceConnListener.add(sc);
    }
    public void unregisterServiceConnListener(ServiceListener sc){
        mServiceConnListener.remove(sc);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private boolean isServiceRunning()
    {
        return TalkingWatchService.isRunning();
    }

    private ServiceConnection sc = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mBinded = true;

            mService = ((TalkingWatchService.ServiceBinder)service).getService();

            for(ServiceListener sl : mServiceConnListener){
                sl.onServiceConnected(mService);
            }
            logger.info("connected to talking service");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            for(ServiceListener sl : mServiceConnListener){
                sl.onServiceDisConnected(mService);
            }
            mService = null;

            mBinded = false;

            logger.info("disconnected with talking service");
        }
    };

    private Handler mHandler = new Handler();
    private boolean mBinded = false;
    @Override
    protected void onStart() {
        super.onStart();

        logger.info(">>>MainActivity start...");
        final Intent intent = new Intent(this.getApplicationContext(), TalkingWatchService.class);
        if(!isServiceRunning())
        {
            //the service hasn't started, so we need to start it first
            this.startService(intent);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isServiceRunning()){
                        //now let's bind the service
                        logger.info("the service has been started, so we can bind it");
                        MainActivity.this.bindService(intent, sc, Context.BIND_AUTO_CREATE);
                    }
                    else
                    {
                        logger.info("the service has not been started, so we still need to wait");
                        mHandler.postDelayed(this,500);
                    }
                }
            }, 500);
        }
        else{
            this.bindService(intent, sc, Context.BIND_ABOVE_CLIENT);
        }

        AnalyticsConfig.enableEncrypt(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop()
    {
        if(mBinded){
            this.unbindService(sc);
        }
        super.onStop();
    }

    @Override
    protected void onPause() {

        super.onPause();

        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
}
