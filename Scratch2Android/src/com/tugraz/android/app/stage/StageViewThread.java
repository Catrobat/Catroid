package com.tugraz.android.app.stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

/**
 * 
 * The StageViewThread which executes the drawing of the stage.
 * 
 * @author Thomas Holzmann
 * 
 */
public class StageViewThread extends Thread {
	public boolean mIsDraw = false;

	private boolean mSurfaceCreated = false;
	private boolean mRun = false;
	private SurfaceHolder mSurfaceHolder;
	private Context context;
	private Bitmap mBackground;
	private Map<String, Pair<Bitmap, Pair<Float, Float>>> mBitmapToPositionMap;

	public StageViewThread(SurfaceHolder holder, Context context,
			Handler handler) {
		mSurfaceHolder = holder;
		this.context = context;
		this.setName("StageViewThread");
		mBitmapToPositionMap = Collections
				.synchronizedMap(new HashMap<String, Pair<Bitmap, Pair<Float, Float>>>());
	}

	public synchronized void setRunning(boolean b) {
		mRun = b;
	}

	public synchronized void setBackground(String path) {
		mIsDraw = false;
		Log.i("before-parse", path);
		Uri uri = Uri.parse(path);
		Log.i("Image-Path", uri.getEncodedPath());

		ContentResolver resolver = context.getContentResolver();
		
		
		Log.i("Image-Path", uri.getEncodedPath());
		try {
			mBackground = MediaStore.Images.Media.getBitmap(resolver, uri);
		} catch (FileNotFoundException e) {
			Log.w("StageViewThread", "could not find background image!");
			e.printStackTrace();
		} catch (IOException e) {
			Log.w("StageViewThread", "io error at loading background image!");
			e.printStackTrace();
		}
		mIsDraw = true;
	}

	public void addBitmapToDraw(String spriteName, String path, float x, float y) {
		Uri uri = Uri.parse(path);
		Pair<Float, Float> coordinates = new Pair<Float, Float>(x, y);
		Pair<Bitmap, Pair<Float, Float>> bitmapPair = null;
		try {
			bitmapPair = new Pair<Bitmap, Pair<Float, Float>>(
					MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri), coordinates);
		} catch (FileNotFoundException e) {
			Log.w("StageViewThread", "could not find sprite image!");
			e.printStackTrace();
		} catch (IOException e) {
			Log.w("StageViewThread", "io error at loading sprite image!");
			e.printStackTrace();
		}
		mIsDraw = false; // TODO brauchen wir das ueberall??
		mBitmapToPositionMap.put(spriteName, bitmapPair);
		mIsDraw = true;
	}

	public void removeBitmapToDraw(String spriteName) {
		mIsDraw = false;
		mBitmapToPositionMap.remove(spriteName);
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
			if (canvas == null)
				Log.i("StageViewThread", "canvas is null");
			canvas.drawRect(new Rect(0, 0, canvas.getWidth(), canvas.getHeight()),
					paint);
	
			if (mBackground != null)
				canvas.drawBitmap(mBackground, 0, 0, null);
	
			// TODO welcher sprite soll an oberster ebene gezeichnet werden??
			Iterator<String> keyIterator = mBitmapToPositionMap.keySet().iterator();
			for (int i = 0; i < mBitmapToPositionMap.size(); i++) {
				Pair<Bitmap, Pair<Float, Float>> bitmapPair = mBitmapToPositionMap
						.get(keyIterator.next());
				if (bitmapPair != null)
					canvas.drawBitmap(bitmapPair.first, bitmapPair.second.first,
						bitmapPair.second.second, null);
			}
			mIsDraw = false;
		}

	}


}
