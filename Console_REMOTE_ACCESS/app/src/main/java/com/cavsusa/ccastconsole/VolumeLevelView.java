package com.cavsusa.ccastconsole;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


public class VolumeLevelView extends View {
	private Paint mPaint;
	Context thisContext;

	private int	mLevel;		// 0 ~ 100

	public VolumeLevelView(Context context) {
		super(context);
		thisContext = context;

		init();
	}

	public VolumeLevelView(Context context, AttributeSet attr) {
		super(context, attr);
		thisContext = context;

		init();
	}

	private void init() {
		mLevel = 0;
		mPaint = new Paint();
	}

	public void setLevel(int level) {
		mLevel = level;

		if (mLevel > 100)
			mLevel = 100;
		if (mLevel < 0)
			mLevel = 0;

		postInvalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int h, w, ty;

		h = canvas.getHeight();
		w = canvas.getWidth();

//		Log.d("ccastLevel", "onDraw wid " + w + " ht " + h + " level " + mLevel);

	// erase back
		mPaint.setColor(Color.GRAY);
		canvas.drawRect(0, 0, (float)w, (float)h, mPaint);

		ty = h * (100 - mLevel) / 100;

		mPaint.setColor(Color.BLUE);
//		mPaint.setTextSize(12);
//		canvas.drawText("Blah blah", 0, 100, mPaint);
		canvas.drawRect(0, ty, (float)w, (float)h, mPaint);
	}

}
