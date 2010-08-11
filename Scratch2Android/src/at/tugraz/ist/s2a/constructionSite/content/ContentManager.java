package at.tugraz.ist.s2a.constructionSite.content;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;
import android.content.Context;
import at.tugraz.ist.s2a.utils.filesystem.FileSystem;
import at.tugraz.ist.s2a.constructionSite.gui.dialogs.SpritesDialog;
import at.tugraz.ist.s2a.utils.parser.Parser;

/**
 * provides content
 * @author alex, niko
 *
 */
public class ContentManager extends Observable{
	
	private ArrayList<HashMap<String, String>> mContentArrayList;
	private TreeMap<String, ArrayList<HashMap<String, String>>> mSpritesAndBackgroundList;
	
	private FileSystem mFilesystem;
	private Parser mParser;
	private Context mCtx;
	private static final String mTempFile = "defaultSaveFile.spf";
	private String mCurrentSprite;
	private SpritesDialog mSpritebox;
	private ArrayList<String> mSpritelist = new ArrayList<String>();
	
	public ArrayList<HashMap<String, String>> getContentArrayList(){
		return mContentArrayList;
	}
	
	public TreeMap<String, ArrayList<HashMap<String, String>>> getSpritesAndBackground(){
		return mSpritesAndBackgroundList;
	}
	
	public void removeSprite(String name){
		if(mSpritesAndBackgroundList.containsKey(name))
		{
			mSpritesAndBackgroundList.remove(name);
			refreshSpritelist();
		}
		if(mCurrentSprite.equals(name))		
		{
			mContentArrayList = (ArrayList<HashMap<String,String>>)mSpritesAndBackgroundList.get(name).clone();
			setChanged();
			notifyObservers();
		}
		if(mSpritesAndBackgroundList.size() == 0)
		{
			//Fill Dummy Stage
			mSpritesAndBackgroundList.put("stage", new ArrayList<HashMap<String,String>>());
			setChanged();
			notifyObservers();
		}
	}
	
	public void clearSprites(){
		mSpritesAndBackgroundList.clear();
		mContentArrayList.clear();
		mCurrentSprite = "stage";
		saveContent();
		mSpritesAndBackgroundList.put("stage", (ArrayList<HashMap<String,String>>)mContentArrayList.clone());
		//Fill Dummy Stage
		refreshSpritelist();//TODO Check this (SpritesAdapter)
        setChanged();
		notifyObservers();
	}
	
	public void addSprite(String name, ArrayList<HashMap<String, String>> sprite)
	{
		if(mSpritesAndBackgroundList.containsKey(name))
			{/*do nothing Sprite already exists*/}
		else
			{mSpritesAndBackgroundList.put(name, sprite);}
		
		switchSprite(name);
		mCurrentSprite = name;
		refreshSpritelist();
	}
	
	public void remove(int position){
		mContentArrayList.remove(position);
		setChanged();
		notifyObservers();
	}
	
	public void clear(){
		mContentArrayList.clear();
        setChanged();
		notifyObservers();
	}
	
	public void add(HashMap<String, String> map){
		mContentArrayList.add(map);
		setChanged();
		notifyObservers();
	}
	
	public ContentManager(){
		mSpritesAndBackgroundList= new TreeMap<String, ArrayList<HashMap<String, String>>>();
		mContentArrayList = new ArrayList<HashMap<String, String>>();
		mFilesystem = new FileSystem();
		mParser = new Parser();
		mSpritelist = new ArrayList<String>();
		mSpritesAndBackgroundList.put("stage", (ArrayList<HashMap<String,String>>)mContentArrayList.clone());
		refreshSpritelist();
		mCurrentSprite = "stage";
		
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
		
		FileInputStream scratch = mFilesystem.createOrOpenFileInput("/sdcard/"+file, mCtx);
        
		if(scratch != null){
	        
			mSpritesAndBackgroundList.clear();
			mContentArrayList.clear();
			
			mSpritesAndBackgroundList.putAll(mParser.parse(scratch));
	        mContentArrayList.addAll((ArrayList<HashMap<String,String>>)mSpritesAndBackgroundList.get("stage").clone());
            mCurrentSprite ="stage";
	        try {
				scratch.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(mSpritesAndBackgroundList.size() == 0)
			{
				//Fill Dummy Stage
				mSpritesAndBackgroundList.put("stage", new ArrayList<HashMap<String,String>>());
			}
			refreshSpritelist();
	        setChanged();
	        notifyObservers();
		} 

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
		mSpritesAndBackgroundList.put(mCurrentSprite,(ArrayList<HashMap<String,String>>) mContentArrayList.clone());
		FileOutputStream fd = mFilesystem.createOrOpenFileOutput("/sdcard/"+file, mCtx);
		DataOutputStream ps = new DataOutputStream(fd);

		String xml = mParser.toXml(mSpritesAndBackgroundList);
		
		try {
			ps.write(xml.getBytes());
			ps.close();
			fd.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
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
			mSpritesAndBackgroundList.put("stage", new ArrayList<HashMap<String,String>>());
		refreshSpritelist();
		setChanged();
		notifyObservers();
	}
	
	public void switchSprite(String nameNewSprite){
		mSpritesAndBackgroundList.put(mCurrentSprite, (ArrayList<HashMap<String,String>>)mContentArrayList.clone());
		saveContent();
		mContentArrayList.clear();
		mContentArrayList.addAll(mSpritesAndBackgroundList.get(nameNewSprite));
		mCurrentSprite = nameNewSprite;
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
	public void setContext(Context context)
	{
		 mCtx = context;
	}
	public void setSpriteBox(SpritesDialog spritebox)
	{
		mSpritebox = spritebox;
	}
	
    public ArrayList<String> getSpritelist(){
    	return mSpritelist;
    }
    
    private void refreshSpritelist(){
        mSpritelist.clear();
        mSpritelist.addAll(mSpritesAndBackgroundList.keySet());
//        mSpritelist.clear();
//        TreeMap<String, ArrayList<HashMap<String, String>>> map = new TreeMap<String, ArrayList<HashMap<String,String>>>();
//        map.putAll(mSpritesAndBackgroundList);
//        for(int i=0; i<mSpritesAndBackgroundList.size(); i++){
//        	mSpritelist.add(map.firstKey());
//        	map.remove(map.firstKey());
//        }
        }

}