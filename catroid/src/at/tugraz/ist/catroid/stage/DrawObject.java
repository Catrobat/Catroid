package at.tugraz.ist.catroid.stage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;
import at.tugraz.ist.catroid.utils.ImageEditing;

public class DrawObject {

	private Pair<Integer, Integer> mPosition;
	private Bitmap mBitmap;
	private int mZOrder;
	private Pair<Integer, Integer> mSize;
	private Boolean mToDraw;
	private Boolean mHidden;
	private String mPath;
	private float mScaleFactor;

	public DrawObject() {
		mToDraw = false;
		mHidden = false;
		mPosition = new Pair<Integer, Integer>(0, 0);
		mZOrder = 0;
		mSize = new Pair<Integer, Integer>(0, 0); // width , height
		mScaleFactor = 1;
		mBitmap = null;
		mPath = null;
	}

	public synchronized void scaleBitmap(int scaleFactor) {
		mScaleFactor = (float) scaleFactor / 100f;

		if (mBitmap == null) {
			return;
		}
		if (scaleFactor < 1) {
			mBitmap = ImageEditing.scaleBitmap(mBitmap, (float) scaleFactor);
			mSize = new Pair<Integer, Integer>(mBitmap.getWidth(), mBitmap
					.getHeight());
			mScaleFactor = 1;
		}
		if (scaleFactor > 1) {
			Bitmap fullSizeBitmap = BitmapFactory.decodeFile(mPath);
			mScaleFactor = (mSize.first * mScaleFactor)
					/ fullSizeBitmap.getWidth();
			mBitmap = ImageEditing.scaleBitmap(fullSizeBitmap, scaleFactor);
			fullSizeBitmap.recycle();
			mScaleFactor = 1;
		}
		mToDraw = true;

	}

	public synchronized void setBitmap(String path) throws Exception {
		mBitmap = BitmapFactory.decodeFile(path);
		mBitmap = ImageEditing.scaleBitmap(mBitmap, mScaleFactor);
		mPath = path;
		mSize = new Pair<Integer, Integer>(mBitmap.getWidth(), mBitmap
				.getHeight());
		mToDraw = true;
	}

	public synchronized Bitmap getBitmap() {
		return mBitmap;
	}

	public synchronized boolean getToDraw() {
		return mToDraw;
	}

	public synchronized Pair<Integer, Integer> getPosition() {
		return mPosition;
	}

	public synchronized void setmPosition(Pair<Integer, Integer> position) {
		mPosition = position;
	}

	public synchronized Boolean getHidden() {
		return mHidden;
	}

	public synchronized void setHidden(Boolean hidden) {
		mHidden = hidden;
	}

	public synchronized Pair<Integer, Integer> getSize() {
		return mSize;
	}

	public int getZOrder() {
		return mZOrder;
	}

	public void setZOrder(int zOrder) {
		mZOrder = zOrder;
	}
	
}
