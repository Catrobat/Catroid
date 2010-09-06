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
import android.util.Pair;
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
	
	private ArrayList<HashMap<String, String>> mCurrentSpriteList;
	private ArrayList<String> mContentGalleryList;
	
	private ArrayList<Pair<String, ArrayList<HashMap<String, String>>>> mAllContentArrayList;
	
	private FileSystem mFilesystem;
	private Parser mParser;
	private Context mCtx;
	private static final String mTempFile = "defaultSaveFile.spf";
	private int mCurrentSprite;
	private int mIdCounter;
	private static String STAGE;

	
	public ArrayList<HashMap<String, String>> getCurrentSpriteList(){
		return mCurrentSpriteList;
	}
	
	public ArrayList<String> getContentGalleryList(){
		return mContentGalleryList;
	}
	
	public void resetContent(){
		mCurrentSpriteList = null;
		mAllContentArrayList.clear();
		mCurrentSprite = 0;
		mIdCounter = 0;
		mContentGalleryList.clear();
		}
	
	public void addSprite(Pair<String, ArrayList<HashMap<String, String>>> sprite)
	{
		mAllContentArrayList.add(sprite);
		mCurrentSprite = mAllContentArrayList.size()-1;
		
		switchSprite(mCurrentSprite);
	}
	
	public void switchSprite(int position){
		mCurrentSpriteList = mAllContentArrayList.get(position).second;
		mCurrentSprite = position;
		loadContentGalleryList();
		setChanged();
		notifyObservers();
	}
	
	private void loadContentGalleryList(){
		mContentGalleryList.clear();
		for(int i = 0; i < mCurrentSpriteList.size(); i++){
			String type = mCurrentSpriteList.get(i).get(BrickDefine.BRICK_TYPE);
			
			if( type.equals(String.valueOf(BrickDefine.SET_BACKGROUND)) 
					|| type.equals(String.valueOf(BrickDefine.SET_COSTUME))){
			
				mContentGalleryList.add(mCurrentSpriteList.get(i).get(BrickDefine.BRICK_VALUE_1));
			}
		}
	}
	

	public void removeBrick(int position){
		String type = mCurrentSpriteList.get(position).get(BrickDefine.BRICK_TYPE);
		if( type.equals(String.valueOf(BrickDefine.SET_BACKGROUND)) 
				|| type.equals(String.valueOf(BrickDefine.SET_COSTUME))){
			
			mContentGalleryList.remove(mCurrentSpriteList.get(position).get(BrickDefine.BRICK_VALUE_1));
		}
		mCurrentSpriteList.remove(position);
		setChanged();
		notifyObservers();
	}
	
	public void addBrick(HashMap<String, String> map){
		map.put(BrickDefine.BRICK_ID, ((Integer)mIdCounter).toString());
		mIdCounter++;
		mCurrentSpriteList.add(map);
		
		setChanged();
		notifyObservers();
	}
	
	public ContentManager(Context context){
		mCtx = context;
		STAGE = mCtx.getString(R.string.stage);
		mCurrentSpriteList = null;
		mAllContentArrayList = new ArrayList<Pair<String, ArrayList<HashMap<String, String>>>>();
		
		mFilesystem = new FileSystem();
		mParser = new Parser();
		
		mIdCounter = 0;	
		mContentGalleryList = new ArrayList<String>();
		
		resetContent();
		setDefaultStage();
	}
	
	
	public void setDefaultStage(){ //TODO komischer name!
		mCurrentSpriteList = new ArrayList<HashMap<String,String>>();
		mCurrentSprite = 0;

		mAllContentArrayList.add(new Pair<String, ArrayList<HashMap<String, String>>>(mCtx.getString(R.string.stage), mCurrentSpriteList));
	}

	private void setmAllContentArrayListAndmAllContentNameArrayList(
			ArrayList<Pair<String, ArrayList<HashMap<String, String>>>> treeMap) {
	}
	
	public String getCurrentSpriteName(){
		return mAllContentArrayList.get(mCurrentSprite).first;
	}
	
	public Integer getCurrentSpritePosition(){
		return mCurrentSprite;
	}
	
	public void setObserver(Observer observer)
	{
		addObserver(observer);
	}
	
    public ArrayList<String> getAllContentNameList(){
    	ArrayList<String> spritesNameList = new ArrayList<String>();
    	for (int i=0; i<mAllContentArrayList.size(); i++)
    		spritesNameList.add(mAllContentArrayList.get(i).first);
    	return spritesNameList;
    }
    public ArrayList<Pair<String, ArrayList<HashMap<String, String>>>> getAllContentList(){
    	return mAllContentArrayList;
    }
    
	/**
	 * test method
	 */
    public int getIdCounter()
    {
    	return mIdCounter;
    }
    
    public boolean moveBrickUpInList(int position){
    	if(position > 0 && position < mCurrentSpriteList.size()){
    		HashMap<String, String> map = mCurrentSpriteList.get(position);
    		mCurrentSpriteList.remove(position);
    		mCurrentSpriteList.add(position-1, map);
    		setChanged();
    		notifyObservers();
    		return true;
    	}
    	return false;
    }
    
    public boolean moveBrickDownInList(int position){
    	if(position < mCurrentSpriteList.size()-1 && position >= 0){
    		HashMap<String, String> map = mCurrentSpriteList.get(position);
    		mCurrentSpriteList.remove(position);
    		mCurrentSpriteList.add(position+1, map);
    		setChanged();
    		notifyObservers();
    		return true;
    	}
    	return false;
    }
    
    
	///////////////////////////////
	/**
	 * load content into data structure
	 */
	public void loadContent(){
		loadContent(mTempFile);
	}
	/**
	 * load content into data structure
	 */
	public void loadContent(String fileName){
		
		resetContent();
		
		FileInputStream scratch = mFilesystem.createOrOpenFileInput
			(Utils.concatPaths(ConstructionSiteActivity.ROOT, fileName), mCtx);
			
		try {
			if(scratch != null && scratch.available() > 0){
				//TODO: TreeMap -> Array<Pair>
				setmAllContentArrayListAndmAllContentNameArrayList(mParser.parse(scratch, mCtx));
				
				
				mCurrentSpriteList = mAllContentArrayList.get(0).second;
				loadContentGalleryList();
			    mIdCounter = getHighestId();
			    mCurrentSprite = 0;
	
			    scratch.close();
			}

		} catch (IOException e) {
		}
		if(mAllContentArrayList.size() == 0)
		{
			setDefaultStage();
		}
	    setChanged();
	    notifyObservers();
	}
	


	private int getHighestId() {
		ArrayList<ArrayList<HashMap<String, String>>> SpriteMap = new ArrayList<ArrayList<HashMap<String, String>>>();
        SpriteMap.addAll((ArrayList<ArrayList<HashMap<String, String>>>)mAllContentArrayList.clone());
        int highestId = 0;
		for(int i=0; i<mAllContentArrayList.size(); i++){
			ArrayList<HashMap<String, String>> sprite = SpriteMap.get(i);
			for(int j=0; j<sprite.size(); j++){
				HashMap<String, String> brickList = sprite.get(j);
				String stringId =  brickList.get(BrickDefine.BRICK_ID);
				if(brickList.size()>0 && !(brickList.get(BrickDefine.BRICK_ID).equals(""))){
					int tempId = Integer.valueOf(brickList.get(BrickDefine.BRICK_ID).toString()).intValue();
					boolean test = (highestId<tempId);
					if(test){
						highestId = tempId;
					}		
				}
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
		//((Activity)mCtx).setTitle(title.replace(ConstructionSiteActivity.DEFAULT_FILE_ENDING, "").replace("/", ""));
		//TODO: setTitle-> ClassCastException Testing
		ArrayList< Pair<String, ArrayList<HashMap<String, String>>>> spriteBrickList = new ArrayList< Pair<String, ArrayList<HashMap<String, String>>>>();
		for(int i=0; i<mAllContentArrayList.size(); i++){
			spriteBrickList.add(mAllContentArrayList.get(i));
		}
		
		FileOutputStream fd = mFilesystem.createOrOpenFileOutput(Utils.concatPaths(ConstructionSiteActivity.ROOT, file), mCtx);
		DataOutputStream ps = new DataOutputStream(fd);
		
		String xml = mParser.toXml(spriteBrickList);
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
	
    

}