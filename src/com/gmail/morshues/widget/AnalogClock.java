package com.gmail.morshues.widget;

import com.gamil.morshues.widget.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RemoteViews.RemoteView;

@RemoteView
public class AnalogClock extends View {
    private Activity activity;

    private Time mCalendar = new Time();

    private float mHour;
    private float mMinutes;
	private float mSeconds;
    private boolean mChanged;
    
    private static final int DIAL_COLOR_DEFAULT = Color.parseColor("#E0FFFFFF");
    private static final int HOUR_HAND_COLOR_DEFAULT = Color.parseColor("#E0FFFFFF");
    private static final int MINUTE_HAND_COLOR_DEFAULT = Color.parseColor("#E0FFFFFF");
    private static final int SECOND_HAND_COLOR_DEFAULT = Color.parseColor("#E0FFFFFF");
    private static final int SHADOW_COLOR_DEFAULT = Color.parseColor("#A0000000");
	
    private Paint dialPaint = new Paint();
	private Paint hourPaint = new Paint();
	private Paint minutePaint = new Paint();
	private Paint secondPaint = new Paint();
	private int shadowColor;

	private Path dialPath;
	private Path hourPath;
	private Path minutePath;
	private Path secondPath;

	private float radius;
	private int centerX;
	private int centerY;
	
    public AnalogClock(Context context) {
		super(context);
		activity = (Activity) context;
	}
    
    public AnalogClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("NewApi")
	public AnalogClock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
		activity = (Activity) context;
		
		// disable hardware acceleration to avoid invalid upon API level 14.
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AnalogClock, defStyle, 0);
		initColor(ta);
        ta.recycle();
		
        Thread colokThread = null;
        colokThread = new Thread(new CountDownRunner());
        colokThread.start();
    }

    private void initColor(TypedArray ta) {
        dialPaint.setColor(ta.getColor(R.styleable.AnalogClock_color_dail, DIAL_COLOR_DEFAULT));
        dialPaint.setStyle(Style.STROKE);
        dialPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        hourPaint.setColor(ta.getColor(R.styleable.AnalogClock_color_hand_hour, HOUR_HAND_COLOR_DEFAULT));
        hourPaint.setStyle(Style.FILL);
        hourPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        minutePaint.setColor(ta.getColor(R.styleable.AnalogClock_color_hand_minute, MINUTE_HAND_COLOR_DEFAULT));
        minutePaint.setStyle(Style.FILL);
        minutePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        secondPaint.setColor(ta.getColor(R.styleable.AnalogClock_color_hand_second, SECOND_HAND_COLOR_DEFAULT));
        secondPaint.setStyle(Style.FILL);
        secondPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        shadowColor = ta.getColor(R.styleable.AnalogClock_color_shadow, SHADOW_COLOR_DEFAULT);
    }
    
    private void updateClock(int w, int h) {
        float diameter = Math.min(w, h)*0.85f;
        radius = diameter/2;
        centerX = w / 2;
        centerY = h / 2;

        dialPaint.setStrokeWidth(radius/15);
        dialPaint.setShadowLayer(radius/15f, 0, radius/20, shadowColor);
        dialPath = new Path();
        dialPath.addCircle(centerX, centerY, radius*1.1f, Path.Direction.CW);
        for (int i = 0; i < 60; i++) {
        	if (i%15 == 0) {
            	dialPath.addArc(new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius), i*6-1,2);        		
        		dialPath.addArc(new RectF(centerX - radius*0.96f, centerY - radius*0.96f, centerX + radius*0.96f, centerY + radius*0.96f), i*6-1,2);        		        		
        		dialPath.addArc(new RectF(centerX - radius*0.93f, centerY - radius*0.93f, centerX + radius*0.93f, centerY + radius*0.93f), i*6-1,2);        		        		
        		dialPath.addArc(new RectF(centerX - radius*0.9f, centerY - radius*0.90f, centerX + radius*0.9f, centerY + radius*0.9f), i*6-1,2);        		        		
        	} else if (i%5 == 0) {
        		dialPath.addArc(new RectF(centerX - radius*0.96f, centerY - radius*0.96f, centerX + radius*0.96f, centerY + radius*0.96f), i*6-0.5f,1);        		        		
        		dialPath.addArc(new RectF(centerX - radius*0.93f, centerY - radius*0.93f, centerX + radius*0.93f, centerY + radius*0.93f), i*6-0.5f,1);        		        		
        		dialPath.addArc(new RectF(centerX - radius*0.9f, centerY - radius*0.90f, centerX + radius*0.9f, centerY + radius*0.9f), i*6-0.5f,1);        		        		
        	} 
        	dialPath.addArc(new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius), i*6-0.5f,1);        		
        }
        
        hourPath = new Path();
        hourPath.addRoundRect(new RectF(centerX - radius/30, centerY-radius/2, centerX + radius/30, centerY+radius/6), radius/50, radius/50, Path.Direction.CW);

        minutePath = new Path();
        minutePath.addRoundRect(new RectF(centerX - radius/50, centerY-radius*0.95f, centerX + radius/50, centerY+radius/4), radius/50, radius/50, Path.Direction.CW);

        secondPath = new Path();
        secondPath.addRoundRect(new RectF(centerX - radius/100, centerY - radius*1.00f, centerX + radius/100, centerY - radius*0.50f), radius/100f, radius/100f, Path.Direction.CW);
        secondPath.addCircle(centerX, centerY-radius*0.40f, radius/25f, Path.Direction.CW);
        secondPath.addRoundRect(new RectF(centerX - radius/50, centerY - radius*0.30f, centerX + radius/50, centerY - radius*0.20f), radius/100f, 2f, Path.Direction.CW);
        secondPath.addRoundRect(new RectF(centerX - radius/100, centerY - radius*0.15f, centerX + radius/100, centerY - radius*0.05f), radius/100f, radius/100f, Path.Direction.CW);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mChanged = true;
        
        updateClock(w, h);
    }
    
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boolean changed = mChanged;
        if (changed) {
            mChanged = false;
        }

        canvas.drawPath(dialPath, dialPaint);
        
        canvas.save();
        canvas.rotate(mHour / 12.0f * 360.0f, centerX, centerY);
        hourPaint.setShadowLayer(radius/30f, (float)Math.sin(mHour/6f*Math.PI)*radius/25, (float)Math.cos(mHour/6f*Math.PI)*radius/25, shadowColor);
        canvas.drawPath(hourPath, hourPaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(mMinutes / 60.0f * 360.0f, centerX, centerY);
        minutePaint.setShadowLayer(radius/30f, (float)Math.sin(mMinutes/30f*Math.PI)*radius/20, (float)Math.cos(mMinutes/30f*Math.PI)*radius/20, shadowColor);
        canvas.drawPath(minutePath, minutePaint);
        canvas.restore();
        
        canvas.save();
        canvas.rotate(mSeconds / 60.0f * 360.0f, centerX, centerY);
        secondPaint.setShadowLayer(radius/60f, (float)Math.sin(mSeconds/30f*Math.PI)*radius/18, (float)Math.cos(mSeconds/30f*Math.PI)*radius/18, shadowColor);
        canvas.drawPath(secondPath, secondPaint);
        canvas.restore();
    }

    private void onTimeChanged() {
        mCalendar.setToNow();

        int hour = mCalendar.hour;
        int minute = mCalendar.minute;
        int second = mCalendar.second;

        mSeconds = second;
        mMinutes = minute + second / 60.0f;
        mHour = hour + mMinutes / 60.0f;
        mChanged = true;
    }


    class CountDownRunner implements Runnable {
    	public void run() {
    		while (!Thread.currentThread().isInterrupted()) {
    			try {
    				activity.runOnUiThread(new Runnable() {
    					public void run() {
    						onTimeChanged();
    						invalidate();
    					}
    				});
    				Thread.sleep(1000);
    			} catch (InterruptedException e) {
    				Thread.currentThread().interrupt();
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
    	}
	}
}
