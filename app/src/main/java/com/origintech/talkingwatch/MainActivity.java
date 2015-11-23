package com.origintech.talkingwatch;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class MainActivity extends AppCompatActivity
{
    private TalkingWatchService mService = null;
    public TalkingWatchService getTalkingService(){
        return mService;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private boolean isServiceRunning()
    {
        SharedPreferences sp = this.getSharedPreferences(TalkingWatchService.SERVICE_KEY
                                                            , Activity.MODE_PRIVATE);
        return sp.getBoolean(TalkingWatchService.SERVICE_KEY,false);
    }

    private ServiceConnection sc = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Toast.makeText(MainActivity.this.getApplicationContext(),
            //        "the service started", Toast.LENGTH_SHORT).show();
            mService = ((TalkingWatchService.ServiceBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };
    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this.getApplicationContext(), TalkingWatchService.class);
        if(!isServiceRunning())
        {
            //the service hasn't started, so we need to start it first
           this.startService(intent);
        }
        //now let's bind the service
        this.bindService(intent, sc, Context.BIND_AUTO_CREATE);
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
        super.onStop();
        this.unbindService(sc);
    }
}
