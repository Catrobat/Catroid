package at.tugraz.ist.catroid.stage;

import java.util.ArrayList;
import java.util.HashMap;

import android.util.Pair;

/**
 * 
 * @author Thomas Holzmann
 *
 */
public class Script extends Thread {
	
	private ArrayList<HashMap<String, String>> mScriptData;
	//TODO private DrawObject mDrawObject;
	private SoundManager mSoundManager;
	//private Ticker ticker;
	
	//TODO Script(DrawObject draw, 
	public Script(ArrayList<HashMap<String, String>> scriptData){
		super();
		mScriptData = scriptData;
		//TODO mDrawObject = drawObject;
		mSoundManager = SoundManager.getInstance();
		
		
	}
}
