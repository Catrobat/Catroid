package at.tugraz.ist.catroid.stage;

public class SoundManager {
	private static SoundManager mSoundManager = null;
	
	private SoundManager(){
		super();
	}
	
	public static SoundManager getInstance(){
		if (mSoundManager == null){
			mSoundManager = new SoundManager();
		}
		return mSoundManager;
	}
	
	public void play(String filePath){
		//TODO implement
	}

}
