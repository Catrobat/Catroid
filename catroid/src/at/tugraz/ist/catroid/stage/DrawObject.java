package at.tugraz.ist.catroid.stage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;
import at.tugraz.ist.catroid.utils.ImageEditing;

public class DrawObject {

	public Pair<Integer, Integer> mPosition;
	public Bitmap mBitmap;
	public int mZOrder;
	public Pair<Integer, Integer> mSize;
	public Boolean mToDraw;
	public Boolean mHidden;
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

	public void setBitmap(String path) throws Exception {
		mBitmap = BitmapFactory.decodeFile(path);
		mBitmap = ImageEditing.scaleBitmap(mBitmap, mScaleFactor);
		mPath = path;
		mSize = new Pair<Integer, Integer>(mBitmap.getWidth(), mBitmap
				.getHeight());
		mToDraw = true;
	}

	public void scaleBitmap(int scaleFactor){
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

	}
}
