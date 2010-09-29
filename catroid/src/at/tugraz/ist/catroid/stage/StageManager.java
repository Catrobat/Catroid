package at.tugraz.ist.catroid.stage;

import java.util.*;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import at.tugraz.ist.catroid.constructionSite.content.ContentManager;

public class StageManager {
	private String mRootImages;
	private String mRootSounds;
	private String mRoot;
	private String mProjectFile;
	private ContentManager mContentManager;
	private Context mContext;
	private ArrayList<Sprite> mSpritesList;
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

	public StageManager(Context context, String imageRoot, String soundRoot,
			String root, String projectFile) {
		mRootImages = imageRoot;
		mRootSounds = soundRoot;
		mRoot = root;
		mProjectFile = projectFile;
		mContext = context;
		
		mDraw = new CanvasDraw();

		mContentManager = new ContentManager(mContext);
		mContentManager.loadContent(projectFile);

		mSpritesList = new ArrayList<Sprite>();
		for (int i = 0; i < mContentManager.getAllContentArrayList().size(); i++) {
			mSpritesList.add(new Sprite(mContentManager
					.getAllContentArrayList().get(i)));
		}
		sortSpriteList();
		mSpritesChanged = true;
		
		for (int i = 0; i < mSpritesList.size(); i++) {
			mSpritesList.get(i).start();
		}
	}

	public void sortSpriteList() {
		Collections.sort(mSpritesList);
	}

	public void drawSprites() {
		mDraw.clear();
		for (int i = 0; i < mSpritesList.size(); i++) {
			mDraw.draw(mSpritesList.get(i).mDrawObject);
		}
	}

	public void processTouchEvent(MotionEvent event) {
		//
	}

	public void pause() {
		for (int i = 0; i < mSpritesList.size(); i++) {
			mSpritesList.get(i).pause();
		}
		mHandler.removeCallbacks(mRunnable);

	}

	public void unPause() {
		for (int i = 0; i < mSpritesList.size(); i++) {
			mSpritesList.get(i).pause();
		}
		mRunnable.run();
	}

	public void start() {
		mRunnable.run();
	}
}
