package at.tugraz.ist.catroid.stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.content.sprite.Sprite;

public class StageManager {
    private Activity mActivity;
    protected ArrayList<Sprite> mSpritesList;
    private Boolean mSpritesChanged;
    private IDraw mDraw;
    private int maxZValue = 0;
    private boolean isPaused;

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        public void run() {
            // Log.v("StageManager", "run");
            for (int i = 0; i < mSpritesList.size(); i++) {
                if (mSpritesList.get(i).getToDraw() == true) {
                    mSpritesChanged = true;
                    mSpritesList.get(i).setToDraw(false);
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

    public StageManager(Activity activity, String projectFile) {
        StorageHandler storageHandler;
        try {
            storageHandler = StorageHandler.getInstance();
            Project project = storageHandler.loadProject(projectFile);
            mSpritesList = (ArrayList<Sprite>) project.getSpriteList();
            mActivity = activity;

            // set stage z coordinate to minimum
            sortSpriteList();
            mSpritesChanged = true;

            mDraw = new CanvasDraw(mSpritesList);

            for (int i = 0; i < mSpritesList.size(); i++) {
                mSpritesList.get(i).startScripts();
            }
        } catch (IOException e) {
            // TODO: Show error dialog
        }
    }

    public void sortSpriteList() {
        Collections.sort(mSpritesList);
        maxZValue = mSpritesList.get(mSpritesList.size() - 1).getZPosition();
        Log.d("StageManager", "Sort:");
        for (Sprite s : mSpritesList) {
            Log.d("StageManager", s.getName() + ": z = " + s.getZPosition());
        }
    }

    public boolean drawSprites() {
        Log.v("StageManager", "drawSprites");
        return mDraw.draw();
    }

    public void processOnTouch(int coordX, int coordY) {
        // for (int i = 0; i < mSpritesList.size(); i++) {
        // mSpritesList.get(i).processOnTouch(coordX, coordY);
        // }
    }

    public void pause(boolean drawScreen) {
        for (int i = 0; i < mSpritesList.size(); i++) {
            mSpritesList.get(i).pause();
        }

        if (drawScreen) {
            Bitmap pauseBitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.paused_cat);
            mDraw.drawPauseScreen(pauseBitmap);
            mHandler.removeCallbacks(mRunnable);
            mSpritesChanged = true;
        }

        isPaused = true;
    }

    public void unPause() {
        for (int i = 0; i < mSpritesList.size(); i++) {
            mSpritesList.get(i).resume();
        }
        isPaused = false;
        mSpritesChanged = true;
        mRunnable.run();
    }

    public void start() {
        isPaused = false;
        mRunnable.run();
    }
}
