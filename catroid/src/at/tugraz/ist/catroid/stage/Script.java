package at.tugraz.ist.catroid.stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import android.util.Log;
import android.util.Pair;
import at.tugraz.ist.catroid.constructionSite.content.BrickDefine;
import at.tugraz.ist.catroid.utils.Utils;

/**
 * 
 * @author Thomas Holzmann
 *
 */
public class Script extends Thread implements Observer{
	
	private ArrayList<HashMap<String, String>> mScriptData;
	private DrawObject mDrawObject;
	private SoundManager mSoundManager;
	private BrickWait mBrickWait;
	private int mCommandCount = 0;
	
	public Script(DrawObject drawObject, ArrayList<HashMap<String, String>> scriptData){
		super();
		mScriptData = scriptData;
		mDrawObject = drawObject;
		mSoundManager = SoundManager.getInstance();
		mBrickWait = new BrickWait();
	}
	
	@Override
	public void run() {
		//doNextCommand();
	}

	public void update(Observable observable, Object data) {
		mCommandCount++;
		doNextCommand();
		
	}
	
	public synchronized void doNextCommand() {
		if ((mScriptData.size() <= mCommandCount) || (StageActivity.mDoNextCommands == false)){
			// abort if mCommandCount has run through all commands to execute
			return;
		}
		HashMap<String, String> map = mScriptData.get(mCommandCount);

		String imagePath;
		int type = Integer.parseInt(map.get(BrickDefine.BRICK_TYPE));
		switch (type) {
		case BrickDefine.SET_BACKGROUND: 
		case BrickDefine.SET_COSTUME:
			imagePath = map.get(Utils.concatPaths(StageActivity.ROOT_IMAGES, BrickDefine.BRICK_VALUE));
			try {
				mDrawObject.setBitmap(imagePath);
			} catch (Exception e) {
				Log.e("Script","Image "+imagePath+" does not exist!");
				e.printStackTrace();
			}
			mCommandCount++;
			doNextCommand();
			break;

		case BrickDefine.PLAY_SOUND:
			mSoundManager.play(Utils.concatPaths(StageActivity.ROOT_SOUNDS, map.get(BrickDefine.BRICK_VALUE)));
			mCommandCount++;
			doNextCommand();
			break;

		case BrickDefine.WAIT:
			mBrickWait.mWaitTime = (int)(Float.parseFloat(map.get(BrickDefine.BRICK_VALUE))*1000f);
			mBrickWait.addObserver(this);
			mBrickWait.start();
			break;

		case BrickDefine.GO_TO:
			mDrawObject.mPosition = new Pair<Integer,Integer>(Integer.parseInt(map.get(BrickDefine.BRICK_VALUE)), 
					Integer.parseInt(map.get(BrickDefine.BRICK_VALUE_1)));
			mCommandCount++;
			doNextCommand();
			break;

		case BrickDefine.HIDE:
			mDrawObject.mHidden = true;
			mCommandCount++;
			doNextCommand();
			break;

		case BrickDefine.SHOW:
			mDrawObject.mHidden = false;
			mCommandCount++;
			doNextCommand();
			break;
			
		case BrickDefine.SCALE_COSTUME:
			mDrawObject.scaleBitmap(Integer.parseInt(map.get(BrickDefine.BRICK_VALUE)));
			mCommandCount++;
			doNextCommand();
			break;
		}

	}
}
