package com.origintech.talkingwatch;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ImageView talkingBtn = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        talkingBtn = (ImageView)v.findViewById(R.id.talking);
        talkingBtn.setOnClickListener(onTalkingListener);
        return v;
    }

    private Context mContext = null;
    private View.OnClickListener onTalkingListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TalkingWatchService service = ((MainActivity)mContext).getTalkingService();
            if(service != null && service.getSpeaker() != null){
                service.getSpeaker().speak(new Date());
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
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
        mContext = null;
        talkingBtn.setOnClickListener(null);
    }
}
