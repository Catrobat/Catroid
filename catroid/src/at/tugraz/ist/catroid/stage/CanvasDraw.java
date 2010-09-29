package at.tugraz.ist.catroid.stage;

import java.util.Iterator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import at.tugraz.ist.catroid.utils.ImageEditing;

/**
 * 
 * Draws DrawObjects into a canvas.
 * 
 * @author Thomas Holzmann
 *
 */
public class CanvasDraw implements IDraw, SurfaceHolder.Callback {
	private Canvas mCanvas=null;
	private SurfaceView mSurfaceView;
	
	public CanvasDraw(){
		super();
		mSurfaceView = StageActivity.mStage;
		mSurfaceView.getHolder().addCallback(this);
		
	}

	public synchronized void draw(DrawObject drawObject) {
		if (mCanvas != null && drawObject.mBitmap != null){
			mCanvas.drawBitmap(drawObject.mBitmap, drawObject.mPosition.first, drawObject.mPosition.second, null); //TODO change to getter
		}	
	}

	public synchronized void clear() {
		// TODO Auto-generated method stub

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		mCanvas = holder.lockCanvas();
		
		// we want to start with a white rectangle
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		mCanvas.drawRect(new Rect(0, 0, mCanvas.getWidth(), mCanvas.getHeight()),
				paint);
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

}
