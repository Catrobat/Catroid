package at.tugraz.ist.catroid.stage;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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
public class CanvasDraw implements IDraw {
	private Canvas mCanvas = null;
	private SurfaceView mSurfaceView;
	private Paint mWhitePaint;
	private SurfaceHolder mHolder;
	private ArrayList<Sprite> mSpritesList;

	// TODO destroy surface somewhere!

	public CanvasDraw(ArrayList<Sprite> spritesList) {
		super();
		mSurfaceView = StageActivity.mStage;
		mHolder = mSurfaceView.getHolder();
		mWhitePaint = new Paint();
		mWhitePaint.setStyle(Paint.Style.FILL);
		mWhitePaint.setColor(Color.WHITE);
		mSpritesList = spritesList;

	}

	public synchronized boolean draw() {
		mCanvas = mHolder.lockCanvas();
		try {
			if (mCanvas == null)
				throw new Exception();
			// we want to start with a white rectangle
			mCanvas.drawRect(
					new Rect(0, 0, mCanvas.getWidth(), mCanvas.getHeight()),
					mWhitePaint);
			for (int i = 0; i < mSpritesList.size(); i++) {
				DrawObject drawObject = mSpritesList.get(i).mDrawObject;
				if (drawObject.getBitmap() != null) {
					mCanvas.drawBitmap(drawObject.getBitmap(),
							drawObject.getPositionAbs().first,
							drawObject.getPositionAbs().second, null);
					drawObject.setToDraw(false);
				}
			}
			mHolder.unlockCanvasAndPost(mCanvas);
			return true;

		} catch (Exception e) {
			return false;
		}

	}

	public synchronized void drawPauseScreen(Bitmap pauseBitmap) {
		Paint greyPaint = new Paint();
		greyPaint.setStyle(Paint.Style.FILL);
		greyPaint.setColor(Color.DKGRAY);
		mCanvas = mHolder.lockCanvas();
		if (mCanvas != null) {
			mCanvas.drawRect(
					new Rect(0, 0, mCanvas.getWidth(), mCanvas.getHeight()),
					greyPaint);
			if (pauseBitmap != null) {
				Bitmap scaledPauseBitmap = ImageEditing.scaleBitmap(
						pauseBitmap, ((float) mCanvas.getWidth() / 2f)
								/ (float) pauseBitmap.getWidth());
				int posX = mCanvas.getWidth() / 2
						- scaledPauseBitmap.getWidth() / 2;
				int posY = mCanvas.getHeight() / 2
						- scaledPauseBitmap.getHeight() / 2;
				mCanvas.drawBitmap(scaledPauseBitmap, posX, posY, null);
			}
		}
		mHolder.unlockCanvasAndPost(mCanvas);

	}

}
