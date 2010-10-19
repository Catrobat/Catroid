package at.tugraz.ist.catroid.stage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;
import at.tugraz.ist.catroid.utils.ImageEditing;

public class DrawObject {

	private Pair<Integer, Integer> mPositionAbs;
	private Pair<Integer, Integer> mPositionRel;
	private Bitmap mBitmap;
	private int mZOrder;
	private Pair<Integer, Integer> mSize;
	private boolean mToDraw;
	private boolean mHidden;
	private String mPath;
	private float mScaleFactor;
	private int mMaxRelCoordinates = 1000;
	private int mOrigHeight = 0;
	private int mOrigWidth = 0;

	public DrawObject() {
		mToDraw = false;
		mHidden = false;
		mPositionAbs = new Pair<Integer, Integer>(0, 0);
		mPositionRel = new Pair<Integer, Integer>(0, 0);
		setmPositionRel(new Pair<Integer, Integer>(0, 0));
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
		if (scaleFactor == 0) {
			return;
		}

		int newHeight = (int)((float) mOrigHeight * mScaleFactor);
		int newWidth = (int)((float) mOrigWidth * mScaleFactor);

		positionToSpriteTopLeft();

		if (newHeight > mSize.second || newWidth > mSize.first) {
			mBitmap.recycle();
			mBitmap = BitmapFactory.decodeFile(mPath);

		}

		mBitmap = ImageEditing.scaleBitmap(mBitmap, newWidth, newHeight);
		mSize = new Pair<Integer, Integer>(newWidth, newHeight);

		setPositionToSpriteCenter();
		mToDraw = true;

	}

	public synchronized void setBitmap(String path) throws Exception {
		positionToSpriteTopLeft();
		Bitmap tempBitmap = BitmapFactory.decodeFile(path);

		// dirty workaround for Stage Background
		// still on search for a better solution
		if (tempBitmap.getHeight() > StageActivity.SCREEN_HEIGHT) {
			double backgroundScaleFactor = ((double) StageActivity.SCREEN_HEIGHT + 2)
					/ (double) tempBitmap.getHeight(); // SCREEN_HEIGHT + 2
														// because of rounding
														// errors in set to
														// center
			tempBitmap = ImageEditing.scaleBitmap(tempBitmap,
					backgroundScaleFactor);
		}
		mBitmap = tempBitmap;

		mPath = path;
		mSize = new Pair<Integer, Integer>(mBitmap.getWidth(),
				mBitmap.getHeight());
		new Pair<Integer, Integer>(mBitmap.getWidth(), mBitmap.getHeight());
		mOrigHeight = mBitmap.getHeight();
		mOrigWidth = mBitmap.getWidth();
		setPositionToSpriteCenter();

		mToDraw = true;
	}

	public synchronized Bitmap getBitmap() {
		if (mHidden)
			return null;
		return mBitmap;
	}

	public synchronized boolean getToDraw() {
		return mToDraw;
	}

	public synchronized void setToDraw(boolean value) {
		mToDraw = value;
	}

	public synchronized Pair<Integer, Integer> getPositionRel() {
		return mPositionRel;
	}

	public synchronized void setmPositionRel(Pair<Integer, Integer> position) {
		mPositionRel = position;
		positionToSpriteTopLeft();
		// ToDo: checken ob zuerst in float rechnen und dann auf int casten
		// nötig ist
		int xAbs = Math
				.round(((StageActivity.SCREEN_WIDTH / (2f * mMaxRelCoordinates)) * position.first)
						+ StageActivity.SCREEN_WIDTH / 2f);
		int yAbs = Math
				.round((StageActivity.SCREEN_HEIGHT / (2f * mMaxRelCoordinates))
						* position.second + StageActivity.SCREEN_HEIGHT / 2f);
		mPositionAbs = new Pair<Integer, Integer>(xAbs, yAbs);
		setPositionToSpriteCenter();
		mToDraw = true;
	}

	public synchronized Pair<Integer, Integer> getPositionAbs() {
		return mPositionAbs;
	}

	public synchronized void setmPositionAbs(Pair<Integer, Integer> position) {
		mPositionAbs = position;
		mToDraw = true;
	}

	public synchronized Boolean getHidden() {
		return mHidden;
	}

	public synchronized void setHidden(Boolean hidden) {
		mHidden = hidden;
		mToDraw = true;
	}

	public synchronized Pair<Integer, Integer> getSize() {
		return mSize;
	}

	public int getZOrder() {
		return mZOrder;
	}

	public void setZOrder(int zOrder) {
		mZOrder = zOrder;
		mToDraw = true;
	}

	private void setPositionToSpriteCenter() {
		if (mBitmap == null) {
			return;
		}
		int xPos = mPositionAbs.first - mBitmap.getWidth() / 2;
		int yPos = mPositionAbs.second - mBitmap.getHeight() / 2;
		mPositionAbs = new Pair<Integer, Integer>(xPos, yPos);

	}

	private void positionToSpriteTopLeft() {
		if (mBitmap == null) {
			return;
		}
		int xPos = mPositionAbs.first + mBitmap.getWidth() / 2;
		int yPos = mPositionAbs.second + mBitmap.getHeight() / 2;
		mPositionAbs = new Pair<Integer, Integer>(xPos, yPos);
	}

	@SuppressWarnings("unused")
	private Pair<Integer, Integer> getBitmapSize() {
		int oldWidth = 0;
		int oldHeight = 0;
		if (mBitmap != null) {
			oldWidth = mBitmap.getWidth();
			oldHeight = mBitmap.getHeight();
		}
		return new Pair<Integer, Integer>(oldWidth, oldHeight);

	}

}
