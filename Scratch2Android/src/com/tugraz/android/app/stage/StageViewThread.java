package com.tugraz.android.app.stage;

import com.tugraz.android.app.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.SurfaceHolder;

/**
 * 
 * The StageViewThread which executes the drawing of the stage.
 * 
 * @author Thomas Holzmann
 *
 */
public class StageViewThread extends Thread {
	private boolean mRun = false;
	private SurfaceHolder mSurfaceHolder;
	private Context context;
	private int mX = 0;
	private int mY = 0;
	private int mKeyCode = -1;

	public StageViewThread(SurfaceHolder holder, Context context,
			Handler handler) {
		mSurfaceHolder = holder;
		this.context = context;
	}

	public void setRunning(boolean b) {
		mRun = b;
	}

	public void run() {
		boolean isdraw = true;
		while (mRun) {
			Canvas c = null;
			if (isdraw) {
				try {
					c = mSurfaceHolder.lockCanvas(null);
					synchronized (mSurfaceHolder) {

						doDraw(c);
					}
				} finally {
					if (c != null)
						mSurfaceHolder.unlockCanvasAndPost(c);
				}
			}

		}
	}

    public void doKeyDown(int keyCode, KeyEvent msg) {  
         mKeyCode = keyCode;  
     }  
     public void doKeyUp(int keyCode, KeyEvent msg) { 
    	 if (((keyCode == KeyEvent.KEYCODE_DPAD_UP) || (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) && ((mKeyCode == KeyEvent.KEYCODE_DPAD_UP) || (mKeyCode == KeyEvent.KEYCODE_DPAD_DOWN)))
    		 mKeyCode = -1;  
    	 if (((keyCode == KeyEvent.KEYCODE_DPAD_LEFT) || (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) && ((mKeyCode == KeyEvent.KEYCODE_DPAD_LEFT) || (mKeyCode == KeyEvent.KEYCODE_DPAD_RIGHT)))
    		 mKeyCode = -1;  
     }  
	
	protected void doDraw(Canvas canvas) {
		Paint paint = new Paint();

		Resources res = context.getResources();
		BitmapDrawable myImage = (BitmapDrawable) res
				.getDrawable(R.drawable.icon);

		// for key events
		if (mKeyCode == KeyEvent.KEYCODE_DPAD_UP) mY--;
		if (mKeyCode == KeyEvent.KEYCODE_DPAD_DOWN) mY++;
		if (mKeyCode == KeyEvent.KEYCODE_DPAD_LEFT) mX--;
		if (mKeyCode == KeyEvent.KEYCODE_DPAD_RIGHT) mX++;
		
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		// canvas.drawRect(new Rect(mX + 0, mY + 0, mX + 40, mY + 40), paint);
		canvas.drawRect(new Rect(0, 0, canvas.getWidth(), canvas.getHeight()),
				paint);
		canvas.drawBitmap(myImage.getBitmap(), mX, mY, null);

		//mX++;
		//mY++;
		// String str = "";
		// if (mKeyCode == KeyEvent.KEYCODE_DPAD_UP) str = "DPAD_UP";
		// if (mKeyCode == KeyEvent.KEYCODE_DPAD_DOWN) str = "DPAD_DOWN";
		// if (mKeyCode == KeyEvent.KEYCODE_DPAD_LEFT) str = "DPAD_LEFT";
		// if (mKeyCode == KeyEvent.KEYCODE_DPAD_RIGHT) str = "DPAD_RIGHT";
		// canvas.drawText("keyCode>" + mKeyCode + " " + str, 0, 40, paint);
	}

}
