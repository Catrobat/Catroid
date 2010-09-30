package at.tugraz.ist.catroid.stage;

import java.util.*;

import at.tugraz.ist.catroid.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.MotionEvent;
import at.tugraz.ist.catroid.constructionSite.content.ContentManager;

public class StageManager {
	private String mProjectFile;
	private ContentManager mContentManager;
	private Context mContext;
	protected ArrayList<Sprite> mSpritesList;
	private Boolean mSpritesChanged;
	private IDraw mDraw;

	private Handler mHandler = new Handler();
	private Runnable mRunnable = new Runnable() {
		public void run() {
			for (int i = 0; i < mSpritesList.size(); i++) {
				if (mSpritesList.get(i).mDrawObject.getToDraw()) {
					mSpritesChanged = true;
					mSpritesList.get(i).mDrawObject.setToDraw(false);
				}
			}
			if (mSpritesChanged) {
				drawSprites();
			}

			mHandler.postDelayed(this, 33);
		}

	};

	public StageManager(Context context, String projectFile) {
		mProjectFile = projectFile;
		mContext = context;

		mContentManager = new ContentManager(mContext);
		mContentManager.loadContent(mProjectFile);

		mSpritesList = new ArrayList<Sprite>();
		for (int i = 0; i < mContentManager.getAllContentArrayList().size(); i++) {
			mSpritesList.add(new Sprite(mContentManager
					.getAllContentArrayList().get(i)));
		}
		sortSpriteList();
		mSpritesChanged = true;

		mDraw = new CanvasDraw(mSpritesList);

		for (int i = 0; i < mSpritesList.size(); i++) {
			mSpritesList.get(i).start();
		}
	}

	public void sortSpriteList() {
		Collections.sort(mSpritesList);
	}

	public void drawSprites() {
		mDraw.draw();
	}

	public void processTouchEvent(MotionEvent event) {
		//
	}

	public void pause(boolean drawScreen) {
		for (int i = 0; i < mSpritesList.size(); i++) {
			mSpritesList.get(i).pause();
		}

		if (drawScreen) {
			Bitmap pauseBitmap = BitmapFactory.decodeResource(mContext
					.getResources(), R.drawable.paused_cat);
			mDraw.drawPauseScreen(pauseBitmap);
			mHandler.removeCallbacks(mRunnable);
			mSpritesChanged = true;
		}
	}

	public void unPause() {
		for (int i = 0; i < mSpritesList.size(); i++) {
			mSpritesList.get(i).unPause();
		}
		mRunnable.run();
	}

	public void start() {
		mRunnable.run();
	}
}
