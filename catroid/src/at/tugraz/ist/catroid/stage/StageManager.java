package at.tugraz.ist.catroid.stage;

import java.util.*;

import at.tugraz.ist.catroid.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import at.tugraz.ist.catroid.constructionSite.content.ContentManager;

public class StageManager {
	private ContentManager mContentManager;
	private Context mContext;
	protected ArrayList<Sprite> mSpritesList;
	private Boolean mSpritesChanged;
	private IDraw mDraw;
	private int maxZValue = 0;
	private boolean isPaused;

	private Handler mHandler = new Handler();
	private Runnable mRunnable = new Runnable() {
		public void run() {
			Log.v("StageManager", "run");
			for (int i = 0; i < mSpritesList.size(); i++) {
				if (mSpritesList.get(i).mDrawObject.getToDraw() == true) {
					mSpritesChanged = true;
					mSpritesList.get(i).mDrawObject.setToDraw(false);
				}
			}
			if (mSpritesChanged) {
				mSpritesChanged = !drawSprites();
			}

			if (!isPaused)
				mHandler.postDelayed(this, 33);
		}

	};

	public int getMaxZValue() {
		Log.d("StageManager", "Max z value = " + maxZValue);
		return maxZValue;
	}

	public StageManager(Context context, String projectFile) {
		mContext = context;

		mContentManager = new ContentManager(mContext);
		mContentManager.loadContent(projectFile);

		mSpritesList = new ArrayList<Sprite>();
		for (int i = 0; i < mContentManager.getAllContentArrayList().size(); i++) {
			mSpritesList.add(new Sprite(mContentManager.getAllContentArrayList().get(i), this));
		}
		
		// set stage z coordinate to minimum
		mSpritesList.get(0).mDrawObject.setZOrder(Integer.MIN_VALUE);
		sortSpriteList();
		mSpritesChanged = true;

		mDraw = new CanvasDraw(mSpritesList);

		for (int i = 0; i < mSpritesList.size(); i++) {
			mSpritesList.get(i).start();
		}
	}

	public void sortSpriteList() {
		Collections.sort(mSpritesList);
		maxZValue = mSpritesList.get(mSpritesList.size() - 1).mDrawObject.getZOrder();
		Log.d("StageManager", "Sort:");
		for (Sprite s : mSpritesList) {
			Log.d("StageManager", s.mName + ": z = " + s.mDrawObject.getZOrder());
		}
	}

	public boolean drawSprites() {
		Log.v("StageManager", "drawSprites");
		return mDraw.draw();
	}

	public void processOnTouch(int coordX, int coordY) {
		for (int i = 0; i < mSpritesList.size(); i++) {
			mSpritesList.get(i).processOnTouch(coordX, coordY);
		}
	}

	public void pause(boolean drawScreen) {
		for (int i = 0; i < mSpritesList.size(); i++) {
			mSpritesList.get(i).pause();
		}

		if (drawScreen) {
			Bitmap pauseBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.paused_cat);
			mDraw.drawPauseScreen(pauseBitmap);
			mHandler.removeCallbacks(mRunnable);
			mSpritesChanged = true;
		}
		
		isPaused = true;
	}

	public void unPause() {
		for (int i = 0; i < mSpritesList.size(); i++) {
			mSpritesList.get(i).unPause();
		}
		isPaused = false;
		mRunnable.run();
	}

	public void start() {
		isPaused = false;
		mRunnable.run();
	}
}
