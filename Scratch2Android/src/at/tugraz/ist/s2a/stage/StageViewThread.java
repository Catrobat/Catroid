package at.tugraz.ist.s2a.stage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ScaleDrawable;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;
import at.tugraz.ist.s2a.R;
import at.tugraz.ist.s2a.utils.ImageEditing;


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
	private Bitmap mBackground;
	private Map<String, Pair<Bitmap, Pair<Float, Float>>> mBitmapToPositionMap;
	private Map<String, Float> mScaleMap;

	public StageViewThread(SurfaceHolder holder, Context context,
			Handler handler) {
		mSurfaceHolder = holder;
		this.setName("StageViewThread");
		mBitmapToPositionMap = Collections
				.synchronizedMap(new HashMap<String, Pair<Bitmap, Pair<Float, Float>>>());
		mScaleMap = Collections
		.synchronizedMap(new HashMap<String, Float>());
	}

	public synchronized void setRunning(boolean b) {
		mRun = b;
	}
	
	public synchronized void setBackground(String path) {
		mIsDraw = false;
		Log.i("before-parse", path);

		mBackground = BitmapFactory.decodeFile(path);
	
		mIsDraw = true;
	}

	public void addBitmapToDraw(String spriteName, String path, float x, float y, float scaling) {
		
		Pair<Float, Float> coordinates = new Pair<Float, Float>(x, y);
		Pair<Bitmap, Pair<Float, Float>> bitmapPositionPair = null;
		
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		bitmapPositionPair = new Pair<Bitmap, Pair<Float, Float>>(bitmap, coordinates);
		
		mIsDraw = false; // TODO brauchen wir das ueberall??
		mBitmapToPositionMap.put(spriteName, bitmapPositionPair);
		mScaleMap.put(spriteName, scaling);
		mIsDraw = true;
	}

	public void removeBitmapToDraw(String spriteName) {
		mIsDraw = false;
		mBitmapToPositionMap.remove(spriteName);
		mScaleMap.remove(spriteName);
		mIsDraw = true;
	}

	public void changeBitmapPosition(String spriteName, float x, float y) {
		if (!mBitmapToPositionMap.containsKey(spriteName))
			return;
		Pair<Float, Float> newCoordinates = new Pair<Float, Float>(x, y);
		Bitmap bitmap = mBitmapToPositionMap.get(spriteName).first;
		Pair<Bitmap, Pair<Float, Float>> bitmapAndPosition = new Pair<Bitmap, Pair<Float, Float>>(
				bitmap, newCoordinates);
		mIsDraw = false;
		mBitmapToPositionMap.remove(spriteName);
		mBitmapToPositionMap.put(spriteName, bitmapAndPosition);
		mIsDraw = true;

	}
	
	public void changeScalingFactor(String spriteName, float factor){
		if (!mScaleMap.containsKey(spriteName))
			return;

		mIsDraw = false;
		mScaleMap.remove(spriteName);
		mScaleMap.put(spriteName, factor);
		mIsDraw = true;
	}

	public synchronized boolean isRunning() {
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
				}
			}

		}
	}

	protected synchronized void doDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		if (canvas != null) { // draw only if we already have a canvas
			canvas.drawRect(new Rect(0, 0, canvas.getWidth(), canvas.getHeight()),
					paint);
	
			if (mBackground != null)
				canvas.drawBitmap(mBackground, 0, 0, null);
			
			if(mBitmapToPositionMap.size() != mScaleMap.size()){
				Log.e("StageViewThread", "mBitmapToPositionMap and mScaleMap do not have the same size! Not drawing this frame.");
			}
	
			// TODO welcher sprite soll an oberster ebene gezeichnet werden??
			Iterator<String> keyIterator = mBitmapToPositionMap.keySet().iterator();
			for (int i = 0; i < mBitmapToPositionMap.size(); i++) {
				String key = keyIterator.next();
				Pair<Bitmap, Pair<Float, Float>> bitmapPair = mBitmapToPositionMap
						.get(key);
				float scalingFactor = mScaleMap.get(key);
				if (scalingFactor != 1) {
					// scale the bitmap if scaling factor is not 1
					Bitmap scaledBitmap = ImageEditing.scaleBitmap(bitmapPair.first, scalingFactor);
					bitmapPair = new Pair<Bitmap, Pair<Float, Float>> (scaledBitmap, bitmapPair.second);
				}
				if (bitmapPair.first != null)
					canvas.drawBitmap(bitmapPair.first, bitmapPair.second.first,
						bitmapPair.second.second, null);
			}
			mIsDraw = false;
		}

	}


}
