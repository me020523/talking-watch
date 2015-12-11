package com.origintech.talkingwatch;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Date;
import java.util.logging.Logger;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements MainActivity.ServiceListener{

    Logger logger = Logger.getLogger(this.getClass().toString());

    private ImageView talkingBtn = null;
    private ImageButton talkingToggle = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        talkingBtn = (ImageView)v.findViewById(R.id.talking);
        talkingBtn.setOnClickListener(onTalkingListener);

        talkingToggle = (ImageButton)v.findViewById(R.id.btn_setting);
        talkingToggle.setOnClickListener(onTalkingToggled);

        return v;
    }



    private Context mContext = null;
    private View.OnClickListener onTalkingListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TalkingWatchService service = ((MainActivity)mContext).getTalkingService();
            if(service == null){
                Toast.makeText(MainActivityFragment.this.getContext(),
                        "no talking sevice connected",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if(service.getSpeaker() == null){
                Toast.makeText(MainActivityFragment.this.getContext(),
                        "the talking service has no speaker",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if(service != null && service.getSpeaker() != null){
                logger.info("start to speak by clicking");
                service.getSpeaker().speak(new Date());
            }
        }
    };

    private View.OnClickListener onTalkingToggled = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TalkingWatchService service = ((MainActivity)mContext).getTalkingService();
            if(service == null){
                Toast.makeText(MainActivityFragment.this.getContext(),
                        "no talking sevice connected",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if(service.getEnabled()){
                service.setEnabled(false);
                talkingToggle.setSelected(false);
            }
            else{
                service.setEnabled(true);
                talkingToggle.setSelected(true);
            }

        }
    };



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        ((MainActivity)mContext).registerServiceConnListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        ((MainActivity)mContext).unregisterServiceConnListener(this);
        mContext = null;
        talkingBtn.setOnClickListener(null);
    }

    @Override
    public void onServiceConnected(TalkingWatchService service) {
        if(service.getEnabled()){
            talkingToggle.setSelected(true);
        }else{
            talkingToggle.setSelected(false);
        }
    }

    @Override
    public void onServiceDisConnected(TalkingWatchService service) {

    }
}
