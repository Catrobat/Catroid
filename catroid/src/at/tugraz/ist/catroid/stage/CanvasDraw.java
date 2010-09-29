package at.tugraz.ist.catroid.stage;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


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
	private Paint mWhitePaint;
	
	//TODO destroy surface somewhere!
	
	public CanvasDraw(){
		super();
		mSurfaceView = StageActivity.mStage;
		mSurfaceView.getHolder().addCallback(this);
		mWhitePaint = new Paint();
		mWhitePaint.setStyle(Paint.Style.FILL);
		mWhitePaint.setColor(Color.WHITE);
		
	}

	public synchronized void draw(DrawObject drawObject) {
//		if (drawObject.getBitmap() == null)
//			Log.i("CanvasDraw", "draw: no bitmap!");
//		if (mCanvas == null)
//			Log.i("CanvasDraw", "draw: no canvas!");
		if (mCanvas != null && drawObject.getBitmap() != null){
			mCanvas.drawBitmap(drawObject.getBitmap(), drawObject.getPosition().first, drawObject.getPosition().second, null); //TODO change to getter
		}	
	}

	public synchronized void clear() {
		if (mCanvas != null){
			mCanvas.drawRect(new Rect(0, 0, mCanvas.getWidth(), mCanvas.getHeight()),
					mWhitePaint);
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	public synchronized void surfaceCreated(SurfaceHolder holder) {
		mCanvas = holder.lockCanvas();
		
		if (mCanvas != null)
			// we want to start with a white rectangle
			mCanvas.drawRect(new Rect(0, 0, mCanvas.getWidth(), mCanvas.getHeight()),
				mWhitePaint);
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

}
