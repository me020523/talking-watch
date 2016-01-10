package com.origintech.talkingwatch;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.baidu.mobads.appoffers.OffersView;

public class OfferWallActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_wall);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        RelativeLayout container = (RelativeLayout)this.findViewById(R.id.offerwall);
        OffersView ov=new OffersView(OfferWallActivity.this, false);

        RelativeLayout.LayoutParams rllp=new RelativeLayout.LayoutParams(-1,-1);
        rllp.addRule(RelativeLayout.BELOW, 100);
        container.addView(ov, rllp);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
