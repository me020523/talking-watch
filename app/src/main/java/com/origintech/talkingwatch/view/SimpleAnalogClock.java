package com.origintech.talkingwatch.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;

import com.origintech.talkingwatch.R;

import java.util.TimeZone;

/**
 * Created by shuaibincheng on 15/11/1.
 */
public class SimpleAnalogClock extends View
{
    boolean mAttached = false;

    private Drawable mHourHand = null;
    private Drawable mMinHand = null;
    private Drawable mDial = null;

    private float mHourHandOffset = 0.0f;
    private float mMinHandOffset = 0.0f;

    private int mDialHeight = 0;
    private int mDialWidth = 0;

    private Time calendar = null;

    public SimpleAnalogClock(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        Resources res = context.getResources();
        TypedArray ta = res.obtainAttributes(attrs, R.styleable.SimpleAnalogClock);

        mHourHand = ta.getDrawable(R.styleable.SimpleAnalogClock_hour_hand);
        mMinHand = ta.getDrawable(R.styleable.SimpleAnalogClock_min_hand);
        mDial = ta.getDrawable(R.styleable.SimpleAnalogClock_dial);

        if(mHourHand == null || mMinHand == null || mDial == null)
            throw new RuntimeException("lack of essential resources");

        mHourHandOffset = ta.getDimension(R.styleable.SimpleAnalogClock_hour_hand_offset, 0.0f);
        mMinHandOffset = ta.getDimension(R.styleable.SimpleAnalogClock_min_hand_offset,0.0f);

        mDialWidth = mDial.getIntrinsicWidth();
        mDialHeight = mDial.getIntrinsicHeight();
    }

    

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        float hScale = 1.0f, vScale = 1.0f;

        if(widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth){
            hScale = (float)widthSize / (float)mDialWidth;
        }
        if(heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight){
            vScale = (float)heightSize / (float)mDialHeight;
        }

        float scale = Math.min(hScale,vScale);

        int measuredWidth = resolveSizeAndState((int) (scale * mDialWidth), widthMode, 0);
        int measuredHeight = resolveSizeAndState((int) (scale * mDialHeight), heightMode, 0);
        setMeasuredDimension(widthSize,heightSize);
    }

    public int resolveSizeAndState_My(int size, int measureSpec, int childMeasuredState) {
        final int specMode = MeasureSpec.getMode(measureSpec);
        final int specSize = MeasureSpec.getSize(measureSpec);
        final int result;
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                if (specSize < size) {
                    result = specSize | MEASURED_STATE_TOO_SMALL;
                } else {
                    result = size;
                }
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
            default:
                result = size;
        }
        return result | (childMeasuredState & MEASURED_STATE_MASK);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mChanged = true;
    }

    private final Handler mHandler = new Handler();
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)){
                String tz = intent.getStringExtra("time-zone");
                calendar = new Time(TimeZone.getTimeZone(tz).getID());
            }
            onTimeChanged();

            invalidate();
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if(!mAttached){
            mAttached = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

            getContext().registerReceiver(mIntentReceiver, filter);
        }

        calendar = new Time();

        onTimeChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mAttached){
            mAttached = false;
            getContext().unregisterReceiver(mIntentReceiver);
        }
    }

    private float mHour = 0.0f;
    private float mMinute = 0.0f;
    private boolean mChanged = false;
    protected void onTimeChanged(){
        calendar.setToNow();

        mHour = calendar.hour;
        mMinute = calendar.minute;

        mChanged = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boolean changed = false;
        if(mChanged){
            changed = mChanged;
            mChanged = false;
        }

        int availableWidth = getRight() - getLeft();
        int availableHeight = getBottom() - getTop();

        int x = availableWidth >> 1;
        int y = availableHeight >> 1;

        final Drawable dial = mDial;
        int w = dial.getIntrinsicWidth();
        int h = dial.getIntrinsicHeight();

        boolean scaled = false;
        float scale = 1.0f;

        if(availableWidth < w || availableHeight < h){
            scaled = true;
            scale = Math.min((float)availableWidth / (float)w,
                    (float)availableHeight / (float)h);
            canvas.save();
            canvas.scale(scale,scale, x, y);
        }

        if(changed){
            dial.setBounds(x - (w >> 1), y - (h >> 1), x + (w >> 1), y + (w >> 1));
        }
        dial.draw(canvas);
        canvas.save();

        canvas.rotate(mHour / 12.0f * 360.0f, x, y);
        final Drawable hourHand = mHourHand;
        final float hourHandOffset = mHourHandOffset;
        if(changed){
            w = hourHand.getIntrinsicWidth();
            h = hourHand.getIntrinsicHeight();
            hourHand.setBounds(x - (w >> 1),y - (h - (int)hourHandOffset),
                    x + (w >> 1), y + (int)hourHandOffset);
        }
        hourHand.draw(canvas);
        canvas.restore();
        canvas.save();

        canvas.rotate(mMinute / 60.0f * 360.0f, x, y);
        final Drawable minHand = mMinHand;
        final float minHandOffset = mMinHandOffset;
        if(changed){
            w = minHand.getIntrinsicWidth();
            h = minHand.getIntrinsicHeight();
            minHand.setBounds(x - (w >> 1), y - (h - (int)minHandOffset),
                    x + (w >> 1), y + (int)minHandOffset);
        }
        minHand.draw(canvas);
        canvas.restore();

        if(scaled)
            canvas.restore();

        canvas.save();
    }
}