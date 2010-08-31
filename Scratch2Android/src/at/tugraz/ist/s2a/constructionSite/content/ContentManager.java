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
	
	private ArrayList<HashMap<String, String>> mContentArrayList;
	private ArrayList<String> mContentGalleryList;
	
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
	
	public ArrayList<String> getContentGalleryList(){
		return mContentGalleryList;
	}
	
	public void resetContent(){
		mContentArrayList = null;
		mAllContentArrayList.clear();
		mAllContentNameArrayList.clear();
		mCurrentSprite = 0;
		mIdCounter = 0;
		mContentGalleryList.clear();
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
		loadContentGalleryList();
		setChanged();
		notifyObservers();
	}
	
	private void loadContentGalleryList(){
		mContentGalleryList.clear();
		for(int i = 0; i < mContentArrayList.size(); i++){
			String type = mContentArrayList.get(i).get(BrickDefine.BRICK_TYPE);
			
			if( type.equals(String.valueOf(BrickDefine.SET_BACKGROUND)) 
					|| type.equals(String.valueOf(BrickDefine.SET_COSTUME))){
			
				mContentGalleryList.add(mContentArrayList.get(i).get(BrickDefine.BRICK_VALUE_1));
			}
		}
	}
	

	public void removeBrick(int position){
		String type = mContentArrayList.get(position).get(BrickDefine.BRICK_TYPE);
		if( type.equals(String.valueOf(BrickDefine.SET_BACKGROUND)) 
				|| type.equals(String.valueOf(BrickDefine.SET_COSTUME))){
			
			mContentGalleryList.remove(mContentArrayList.get(position).get(BrickDefine.BRICK_VALUE_1));
		}
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
		mContentArrayList = null;
		mAllContentArrayList = new ArrayList<ArrayList<HashMap<String,String>>>();
		mAllContentNameArrayList = new ArrayList<String>();
		
		mFilesystem = new FileSystem();
		mParser = new Parser();
		
		mIdCounter = 0;	
		mContentGalleryList = new ArrayList<String>();
		
		resetContent();
		setDefaultStage();
	}
	
	
	public void setDefaultStage(){
		mContentArrayList = new ArrayList<HashMap<String,String>>();
		mCurrentSprite = 0;

		mAllContentArrayList.add(mContentArrayList);
		mAllContentNameArrayList.add(mCtx.getString(R.string.stage));
	}

	private void setmAllContentArrayListAndmAllContentNameArrayList(
			TreeMap<String, ArrayList<HashMap<String, String>>> treeMap) {
		mAllContentArrayList.addAll(treeMap.values()); 
		mAllContentNameArrayList.addAll(treeMap.keySet());
	}
	
	public String getCurrentSpriteName(){
		return mAllContentNameArrayList.get(mCurrentSprite);
	}
	
	public Integer getCurrentSpritePosition(){
		return mCurrentSprite;
	}
	
	public void setObserver(Observer observer)
	{
		addObserver(observer);
	}
	
    public ArrayList<String> getAllContentNameList(){
    	return mAllContentNameArrayList;
    }
    public ArrayList<ArrayList<HashMap<String, String>>> getAllContentList(){
    	return mAllContentArrayList;
    }
    
	/**
	 * test method
	 */
    public int getIdCounter()
    {
    	return mIdCounter;
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
				
				
				mContentArrayList = mAllContentArrayList.get(0);
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
		for(int i=0; i<mAllContentNameArrayList.size(); i++){
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
		ArrayList< Pair<String, ArrayList<HashMap<String, String>>>> spriteNameBrickListTreeMap = new ArrayList< Pair<String, ArrayList<HashMap<String, String>>>>();
		for(int i=0; i<mAllContentArrayList.size(); i++){
			spriteNameBrickListTreeMap.add(new Pair(mAllContentNameArrayList.get(i), mAllContentArrayList.get(i)));
		}
		
		FileOutputStream fd = mFilesystem.createOrOpenFileOutput(Utils.concatPaths(ConstructionSiteActivity.ROOT, file), mCtx);
		DataOutputStream ps = new DataOutputStream(fd);
		
		String xml = mParser.toXml(spriteNameBrickListTreeMap);
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