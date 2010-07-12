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
	

	public StageViewThread(SurfaceHolder holder, Context context,
			Handler handler) {
		mSurfaceHolder = holder;
		this.context = context;
		this.setName("StageViewThread");
	}

	public void setRunning(boolean b) {
		mRun = b;
	}
	
	public boolean isRunning(){
		return mRun;
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

	
	/**
	 * Draws the stage.
	 */
	protected void doDraw(Canvas canvas) {
		Paint paint = new Paint();

		Resources res = context.getResources();
		BitmapDrawable myImage = (BitmapDrawable) res
				.getDrawable(R.drawable.icon);  //TODO umaendern in richtiges bild
		
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		// canvas.drawRect(new Rect(mX + 0, mY + 0, mX + 40, mY + 40), paint);
		canvas.drawRect(new Rect(0, 0, canvas.getWidth(), canvas.getHeight()),
				paint);
		canvas.drawBitmap(myImage.getBitmap(), mX, mY, null);

	}

}
