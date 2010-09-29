package at.tugraz.ist.catroid.stage;

import java.util.*;

import android.content.Context;
import at.tugraz.ist.catroid.constructionSite.content.ContentManager;

public class StageManager {
	private String mRootImages;
	private String mRootSounds;
	private String mRoot;
	private String mProjectFile;
	private ContentManager mContentManager;
	private Context mContext;
	private ArrayList<Sprite> mSpritesList;

	public StageManager(Context context, String imageRoot, String soundRoot,
			String root, String projectFile) {
		mRootImages = imageRoot;
		mRootSounds = soundRoot;
		mRoot = root;
		mProjectFile = projectFile;

		mContentManager = new ContentManager(mContext);
		mContentManager.loadContent(projectFile);
		
		mSpritesList = new ArrayList<Sprite>();
		for (int i = 0; i < mContentManager.getAllContentArrayList().size(); i++) {
			mSpritesList.add(new Sprite(mContentManager
					.getAllContentArrayList().get(i)));
		}
		sortSpriteList();
	}
	
	public void sortSpriteList(){
		Collections.sort(mSpritesList);
	}

}
