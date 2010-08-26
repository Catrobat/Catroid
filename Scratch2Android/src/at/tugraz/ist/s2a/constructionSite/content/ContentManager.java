package at.tugraz.ist.s2a.constructionSite.content;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import android.R.bool;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import at.tugraz.ist.s2a.ConstructionSiteActivity;
import at.tugraz.ist.s2a.R;
import at.tugraz.ist.s2a.utils.ImageContainer;
import at.tugraz.ist.s2a.utils.Utils;
import at.tugraz.ist.s2a.utils.filesystem.FileSystem;
import at.tugraz.ist.s2a.utils.parser.Parser;
import at.tugraz.ist.s2a.constructionSite.gui.dialogs.SpritesDialog;

/**
 * provides content
 * @author alex, niko
 *
 */
public class ContentManager extends Observable{
	
	private ArrayList<HashMap<String, String>> mContentArrayList;
	
	private ArrayList<ArrayList<HashMap<String, String>>> mAllContentArrayList;
	private ArrayList<String> mAllContentNameArrayList;
	
	private FileSystem mFilesystem;
	private Parser mParser;
	private Context mCtx;
	private static final String mTempFile = "defaultSaveFile.spf";
	private int mCurrentSprite;
	private int mIdCounter;
	private static String STAGE;

	
	public ArrayList<HashMap<String, String>> getContentArrayList(){
		return mContentArrayList;
	}
	
	public void resetContent(){
		mContentArrayList = null;
		mAllContentArrayList.clear();
		mAllContentNameArrayList.clear();
		
		mContentArrayList = new ArrayList<HashMap<String,String>>();
		mCurrentSprite = 0;
		
		HashMap<String, String> stage = new HashMap<String, String>();
		mContentArrayList.add(stage);
		mAllContentArrayList.add(mContentArrayList);
		mAllContentNameArrayList.add(mCtx.getString(R.string.stage));
		
		mIdCounter = 0;
		//Fill Dummy Stage
        setChanged();
		notifyObservers();
	}
	
	public void addSprite(String name, ArrayList<HashMap<String, String>> sprite)
	{
		mAllContentArrayList.add(sprite);
		mAllContentNameArrayList.add(name);
		mCurrentSprite = mAllContentNameArrayList.size()-1;
		
		switchSprite(mCurrentSprite);
	}
	
	public void switchSprite(int position){
		mContentArrayList = mAllContentArrayList.get(position);
		mCurrentSprite = position;
		setChanged();
		notifyObservers();
	}
	

	public void removeBrick(int position){
		mContentArrayList.remove(position);
		setChanged();
		notifyObservers();
	}
	
	public void addBrick(HashMap<String, String> map){
		map.put(BrickDefine.BRICK_ID, ((Integer)mIdCounter).toString());
		mIdCounter++;
		mContentArrayList.add(map);
		setChanged();
		notifyObservers();
	}
	
	public ContentManager(Context context){
		mCtx = context;
		STAGE = mCtx.getString(R.string.stage);
		mContentArrayList = new ArrayList<HashMap<String, String>>();
		mAllContentArrayList = new ArrayList<ArrayList<HashMap<String,String>>>();
		mAllContentNameArrayList = new ArrayList<String>();
		
		mFilesystem = new FileSystem();
		mParser = new Parser();
		
		mIdCounter = 0;	
	}
	
	/**
	 * load content into data structure
	 */
	public void loadContent(){
		loadContent(mTempFile);
	}
	/**
	 * load content into data structure
	 */
	public void loadContent(String file){
		((Activity)mCtx).setTitle(file.replace(ConstructionSiteActivity.DEFAULT_FILE_ENDING, ""));
		resetContent();
		
		FileInputStream scratch = mFilesystem.createOrOpenFileInput(Utils.concatPaths(ConstructionSiteActivity.ROOT, file), mCtx);
			
		try {
			if(scratch != null && scratch.available() > 0){
				    
				mSpritesAndBackgroundList.putAll(mParser.parse(scratch, mCtx));
				
				
				mContentArrayList.addAll((ArrayList<HashMap<String,String>>)mSpritesAndBackgroundList.get(mSpritesAndBackgroundList.firstKey()).clone());
				
			    mIdCounter = getHighestId();	        
			    mCurrentSprite =STAGE;
	
			    scratch.close();
			}
			
			if(mSpritesAndBackgroundList.size() == 0)
			{
				//Fill Dummy Stage
				mSpritesAndBackgroundList.put(STAGE, new ArrayList<HashMap<String,String>>());
			}
		    setChanged();
		    notifyObservers();
		    
			

		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	private int getHighestId() {
		TreeMap<String, ArrayList<HashMap<String, String>>> SpriteMap = new TreeMap<String, ArrayList<HashMap<String,String>>>();
        SpriteMap.putAll((TreeMap<String, ArrayList<HashMap<String, String>>>)mSpritesAndBackgroundList.clone());
        int highestId = 0;
		for(int i=0; i<mSpritesAndBackgroundList.size(); i++){
			ArrayList<HashMap<String, String>> sprite = SpriteMap.get(SpriteMap.firstKey());
			if(sprite.size()>0){
				int tempId = Integer.parseInt(sprite.get(sprite.size()-1).get(BrickDefine.BRICK_ID));
				boolean test = (highestId<tempId);
				if(test){
					highestId = tempId;
				}
					
			}
			//TODO: geht nur solange letzter Stein höchste Id falls sie höher wird müssen alle Steine durchschaut werden auskommentierter Code!!!
			for(int j=0; j<sprite.size(); j++){
				int tempId = Integer.parseInt(sprite.get(j).get(BrickDefine.BRICK_ID));
				if(tempId > highestId)
					tempId= highestId;
			}
			

		}
		return (highestId+1); // ID immer aktuellste freie
	}

	/**
	 * save content
	 */
	public void saveContent(){
		saveContent(mTempFile);	
	}
	
	/**
	 * save content
	 */
	public void saveContent(String file){
		
		String title = new String(file);
		((Activity)mCtx).setTitle(title.replace(ConstructionSiteActivity.DEFAULT_FILE_ENDING, "").replace("/", ""));
		
		mSpritesAndBackgroundList.put(mCurrentSprite,(ArrayList<HashMap<String,String>>) mContentArrayList.clone());
		FileOutputStream fd = mFilesystem.createOrOpenFileOutput(Utils.concatPaths(ConstructionSiteActivity.ROOT, file), mCtx);
		DataOutputStream ps = new DataOutputStream(fd);
		String xml = mParser.toXml(mSpritesAndBackgroundList);
		
		try {
			ps.write(xml.getBytes());
			ps.close();
			fd.close();
		} catch (IOException e) {
			Log.e("Contentmanager", "ERROR saving file " + e.getMessage());
			e.printStackTrace();
		}	
		
		Log.d("Contentmanager", "Save file!");
   	}
	
	/**
	 * test method
	 *
	 */
	public void testSet(){
		mContentArrayList.clear();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "1");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
        map.put(BrickDefine.BRICK_NAME, "Test3");
        map.put(BrickDefine.BRICK_VALUE, "/mnt/sdcard/See You Again.mp3");
        mContentArrayList.add(map);
        }
	
	/**
	 * test method
	 */
	public void setContentArrayList(ArrayList<HashMap<String, String>> list){
		mContentArrayList = list;
		setChanged();
		notifyObservers();
	}
	
	public void setSpritesAndBackgroundList(TreeMap<String, ArrayList<HashMap<String, String>>> spritesAndBackground){
		mSpritesAndBackgroundList = spritesAndBackground;
		//Check for default stage Object
		if(mSpritesAndBackgroundList.size() == 0)
			mSpritesAndBackgroundList.put(STAGE, new ArrayList<HashMap<String,String>>());
		refreshSpritelist();
		setChanged();
		notifyObservers();
	}
	
	
	
	public String getCurrentSprite(){
		return mCurrentSprite;
	}
	
	public void setObserver(Observer observer)
	{
		addObserver(observer);
	}
	
    public ArrayList<String> getSpritelist(){
    	return mSpritelist;
    }
    

    
	/**
	 * test method
	 */
    public int getIdCounter()
    {
    	return mIdCounter;
    }
    

}