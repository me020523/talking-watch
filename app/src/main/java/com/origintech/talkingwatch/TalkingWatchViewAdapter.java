package com.origintech.talkingwatch;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.origintech.talkingwatch.fragments.AlarmFragment;
import com.origintech.talkingwatch.fragments.WatchFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shuaibincheng on 16/6/14.
 */
public class TalkingWatchViewAdapter  extends FragmentPagerAdapter{
    private final int FRAGMENT_COUNT = 2;

    public TalkingWatchViewAdapter(FragmentManager fm) {
        super(fm);
    }

    private List<Fragment> fragments = new ArrayList<>();
    @Override
    public Fragment getItem(int position) {
        Fragment f = null;
        try{
            f = fragments.get(position);
        }catch (IndexOutOfBoundsException e){
            switch (position){
                case 0:
                    f = new WatchFragment();
                    break;
                case 1:
                    f = new AlarmFragment();
                    break;
                default:
                    f = new WatchFragment();
            }
        }
        return f;
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }
}
