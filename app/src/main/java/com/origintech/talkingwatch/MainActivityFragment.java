package com.origintech.talkingwatch;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.support.v7.widget.AppCompatImageView;

import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.baidu.mobads.IconsAd;
import com.baidu.mobads.RecommendAd;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements MainActivity.ServiceListener,
        SettingDialogFragment.SettingEventHandler{

    Logger logger = Logger.getLogger(this.getClass().toString());

    private AppCompatImageView talkingBtn = null;
    private ImageButton talkingToggle = null;
    private ImageButton settingBtn = null;

    private SettingDialogFragment mDialog = null;

    private FloatingActionButton interstitialAd = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        talkingBtn = (AppCompatImageView)v.findViewById(R.id.talking);
        talkingBtn.setOnClickListener(onTalkingListener);

        talkingToggle = (ImageButton)v.findViewById(R.id.btn_talking_toggle);
        talkingToggle.setOnClickListener(onTalkingToggled);

        settingBtn = (ImageButton)v.findViewById(R.id.btn_setting);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //快速双击会导致出现Fragment already added: SettingDialogFragment
                //所以添加之前需要判断
                if (!mDialog.isAdded())
                    mDialog.show(MainActivityFragment.this.getFragmentManager(), "setting");
            }
        });

        LinearLayout bannerContainer = (LinearLayout)v.findViewById(R.id.banner);

        GregorianCalendar future = new GregorianCalendar(2016, 1, 26, 12, 0);
        GregorianCalendar now = new GregorianCalendar();
        if(now.getTime().after(future.getTime())) {
            this.addBaiduBanner(bannerContainer);
        }
//        this.addBaiduBanner(bannerContainer);


        interstitialAd = (FloatingActionButton)v.findViewById(R.id.fab);
        interstitialAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        //baiduIconsAd();
        //baiduAdPromotionWall(interstitialAd);
        mDialog = new SettingDialogFragment();
        mDialog.setEventHandler(this);

        return v;
    }

    //百度推广墙
    private RecommendAd baiduRecommendAd = null;
    private void baiduAdPromotionWall(ImageView icon){
        Log.i("initialized-ad", "onAdInitialized");
        String adPlaceId = "2383838"; //重要:请填上您的广告位ID
        RecommendAd.Builder builder = new RecommendAd.Builder(icon, adPlaceId);
        builder.setEventListener(new RecommendAd.RecmdEventListener(){
            @Override
            public void onIconBindFailed(String reason) {
                Log.i("RecommendAd-DEMO ", "onIconBindFailed: " + reason); }
            @Override
            public void onIconShow() {
                Log.i("RecommendAd-DEMO ", "onIconShow"); }

            @Override
            public void onIconClick() {

            }
        });
        baiduRecommendAd = builder.build();
        baiduRecommendAd.load(mContext);
    }

    //百度轮播广告
    private IconsAd iconsAd = null;
    private void baiduIconsAd(){
        iconsAd = new IconsAd((MainActivity)mContext,"2384583",new int[]{
            R.drawable.ic_stars_white_48dp, R.drawable.ic_stars_white_48dp
        });
        iconsAd.loadAd((MainActivity)mContext);
    }

    //百度Banner广告
    private void addBaiduBanner(ViewGroup container){
        AdView adView = new AdView(this.getContext(),"2413796");
        adView.setListener(new AdViewListener() {
            @Override
            public void onAdReady(AdView adView) {

            }

            @Override
            public void onAdShow(JSONObject jsonObject) {
                MobclickAgent.onEvent(mContext,"banner_ad_show");
            }

            @Override
            public void onAdClick(JSONObject jsonObject) {
                MobclickAgent.onEvent(mContext,"banner_ad_click");
            }

            @Override
            public void onAdFailed(String s) {

            }

            @Override
            public void onAdSwitch() {

            }
        });
        container.addView(adView);
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

    public boolean isServiceConnected(){
        if(mContext != null && ((MainActivity)mContext).getTalkingService() != null)
            return true;
        else
            return false;
    }
    @Override
    public void onServiceDisConnected(TalkingWatchService service) {

    }

    @Override
    public void onHalfTimeToggled(boolean enable) {
        if(!isServiceConnected())
            return;
        ((MainActivity)mContext).getTalkingService().toggleHalfTime(enable);
    }

    @Override
    public void onShapeTimeToggled(boolean enable) {
        if(!isServiceConnected())
            return;
        ((MainActivity)mContext).getTalkingService().toggleShapeTime(enable);
    }

    @Override
    public void onSensitivityChanged(int value) {
        if(!isServiceConnected())
            return;
        ((MainActivity)mContext).getTalkingService().changeSensitity(value);
    }

    @Override
    public void onServiceRestart() {
        if(!isServiceConnected())
            return;

    }
}
