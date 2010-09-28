package at.tugraz.ist.catroid.stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

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
		// TODO Auto-generated method stub
		
	}
	
	
}
