package com.tugraz.android.app.stage;

import java.util.Collections;
import java.util.HashMap;

import com.tugraz.android.app.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.Pair;
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
	public boolean mIsDraw = false;
	
	private boolean mRun = false;
	private SurfaceHolder mSurfaceHolder;
	private Context context;
	private int mX = 0;
	private int mY = 0;
	private Bitmap mBackgroundBitmap;
	private HashMap<Bitmap, Pair<Float,Float>> mBitmapToPositionMap;
	

	public StageViewThread(SurfaceHolder holder, Context context,
			Handler handler) {
		mSurfaceHolder = holder;
		this.context = context;
		this.setName("StageViewThread");
		//mBackgroundBitmap = BitmapFactory.decodeResource(context.getResources(),
		//		   R.drawable.icon);
		mBitmapToPositionMap = (HashMap<Bitmap, Pair<Float, Float>>) Collections.synchronizedMap(new HashMap<Bitmap, Pair<Float,Float>>());
		//TODO funktioniert der cast da so? 
	}

	public synchronized void setRunning(boolean b) {
		mRun = b;
	}
	
	public void setBackgroundBitmap(String path){
		synchronized (mBackgroundBitmap){
			mBackgroundBitmap = BitmapFactory.decodeFile(path);
		}	
	}
	public void addBitmapToDraw(String path, float x, float y) {
		Pair<Float,Float> coordinates = new Pair<Float,Float>(x,y);
		mBitmapToPositionMap.put(BitmapFactory.decodeFile(path), coordinates);
	}
	
	public synchronized boolean isRunning(){
		return mRun;
	}

	public void run() {
		while (mRun) {
			Canvas c = null;
			if (mIsDraw) {
				try {
					c = mSurfaceHolder.lockCanvas(null);
					synchronized (mSurfaceHolder) {

						doDraw(c);
					}
				} finally {
					if (c != null)
						mSurfaceHolder.unlockCanvasAndPost(c);
					//throw new ThreadDeath();
				}
			}

		}
	}

	
	/**
	 * Draws the stage.
	 */
	protected synchronized void doDraw(Canvas canvas) {
		Paint paint = new Paint();

		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				   R.drawable.icon);
			
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		// canvas.drawRect(new Rect(mX + 0, mY + 0, mX + 40, mY + 40), paint);
		canvas.drawRect(new Rect(0, 0, canvas.getWidth(), canvas.getHeight()),
				paint);
		canvas.drawBitmap(mBackgroundBitmap, mX, mY, null);
		mIsDraw = false;

	}

}
