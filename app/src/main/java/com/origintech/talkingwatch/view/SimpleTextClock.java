package com.origintech.talkingwatch.view;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.origintech.talkingwatch.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by shuaibincheng on 15/11/22.
 */
public class SimpleTextClock extends View {

    private Drawable mBackground = null;
    private String mCustomFontName = null;
    private TextPaint mPaint = null;
    public SimpleTextClock(Context context, AttributeSet attrs) {
        super(context, attrs);

        Resources res = context.getResources();
        TypedArray ta = res.obtainAttributes(attrs, R.styleable.SimpleTextClock);

        mBackground = ta.getDrawable(R.styleable.SimpleTextClock_clock_bg);
        mCustomFontName = ta.getString(R.styleable.SimpleTextClock_custom_font);

        if(mBackground == null || mCustomFontName == null)
            throw new RuntimeException("lack of essential resource");

        Typeface font = Typeface.createFromAsset(getContext().getAssets(),"fonts/" + mCustomFontName);
        mPaint = new TextPaint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setTypeface(font);
    }

    private Calendar mCalendar = null;
    private boolean mAttached = false;
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)){
                String timezone = intent.getStringExtra("time-zone");
                mCalendar = new GregorianCalendar(TimeZone.getTimeZone(timezone));
            }


            onTimeChanged();
            invalidate();
        }
    };
    protected void onTimeChanged(){
        mCalendar.setTime(new Date());
        mChanged = true;
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(!mAttached){
            mAttached = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);

            getContext().registerReceiver(mIntentReceiver, filter);
        }

        mCalendar = new GregorianCalendar();
        onTimeChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mAttached){
            getContext().unregisterReceiver(mIntentReceiver);
            mAttached = false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(widthSize,heightSize);
    }

    boolean mChanged = false;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mChanged = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boolean changed = mChanged;
        if(changed){
            mChanged = false;
        }

        final Drawable bg = mBackground;

        int bgWidth = bg.getIntrinsicWidth();
        int bgHeight= bg.getIntrinsicHeight();

        int w = getRight() - getLeft();
        int h = getBottom() - getTop();
        int x = (w >> 1);
        int y = (h >> 1);

        boolean scaled = false;
        float scale = 1.0f;
        if(w < bgWidth || h < bgHeight){
            scaled = true;
            scale = Math.min((float)w / (float)bgWidth, (float)h / (float)bgHeight);

            canvas.save();
            canvas.scale(scale, scale, x, y);
        }

        if(changed){
            bg.setBounds(x - (bgWidth >> 1), y - (bgHeight >> 1),
                    x + (bgWidth >> 1), y + (bgHeight >> 1));
        }
        bg.draw(canvas);
        canvas.restore();
        canvas.save();

        final Calendar calendar = mCalendar;
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String time = df.format(calendar.getTime());
        if(scaled){
            mPaint.setTextSize(2 * h / 3);
        }
        StaticLayout staticLayout = new StaticLayout(time,mPaint,
                w, Layout.Alignment.ALIGN_NORMAL,1.0f,0,false);
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        float fontW = mPaint.measureText(time);
        canvas.translate(fontW / 2,
                (staticLayout.getLineBottom(0) - staticLayout.getLineTop(0))  / 2);
        staticLayout.draw(canvas);
        canvas.restore();
        canvas.save();
    }
}
