package com.example.please;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class MyImageView extends SurfaceView {

	Paint paint = new Paint();
	int height = this.getHeight();
	int width = this.getWidth();
	public float angle = 0;
	
	public MyImageView(Context context) {
		super(context);
		//Log.i("my image view", "init");
		//paint = new Paint();
		//paint.setColor(Color.BLACK);
		//setWillNotDraw(false);
		// TODO Auto-generated constructor stub
	}
	
	public MyImageView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    //Log.i("my image view", "init other");
	    setWillNotDraw(false);
	}

	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// TODO Auto-generated method stub
		width = canvas.getWidth();
		height = canvas.getHeight();
		//Log.i("my image view", Integer.toString((int)(angle*255/360)));
		paint.setColor(Color.WHITE);
		canvas.drawPaint(paint);
		paint.setColor(Color.LTGRAY);
		paint.setStrokeWidth(1);
		//canvas.drawLine(0, 0, width, height, paint);
		canvas.drawOval(new RectF(0, 0, width, height), paint);
		
		paint.setColor(Color.RED);
		paint.setAlpha((int)(angle*255/360));
		canvas.drawArc(new RectF(0, 0, width, height), -90, angle, true, paint);
		
		paint.setColor(Color.GREEN);
		paint.setAlpha((int)((360-angle)*255/900));
		canvas.drawArc(new RectF(0, 0, width, height), -90, angle, true, paint);
		
	}

	

}
