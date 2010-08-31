package at.tugraz.ist.s2a;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import at.tugraz.ist.s2a.constructionSite.content.BrickDefine;
import at.tugraz.ist.s2a.constructionSite.content.ContentManager;
import at.tugraz.ist.s2a.constructionSite.gui.adapter.ConstructionSiteListViewAdapter;
import at.tugraz.ist.s2a.constructionSite.gui.dialogs.ChangeProgramNameDialog;
import at.tugraz.ist.s2a.constructionSite.gui.dialogs.LoadProgramDialog;
import at.tugraz.ist.s2a.constructionSite.gui.dialogs.NewProjectDialog;
import at.tugraz.ist.s2a.constructionSite.gui.dialogs.ToolBoxDialog;
import at.tugraz.ist.s2a.constructionSite.gui.dialogs.SpritesDialog;
import at.tugraz.ist.s2a.stage.StageActivity;
import at.tugraz.ist.s2a.utils.ImageContainer;
import at.tugraz.ist.s2a.utils.Utils;
import at.tugraz.ist.s2a.utils.filesystem.MediaFileLoader;

public class ConstructionSiteActivity extends Activity implements Observer, OnClickListener{

    /** Called when the activity is first created. */
	
	static final int TOOLBOX_DIALOG_SPRITE = 0;
	static final int TOOLBOX_DIALOG_BACKGROUND = 1;
	static final int SPRITETOOLBOX_DIALOG = 2;
	static final int NEW_PROJECT_DIALOG = 3;
	static final int LOAD_DIALOG = 4;
	static final int CHANGE_PROJECT_NAME_DIALOG = 6;
	
	static final String PREF_ROOT = "pref_root";
	static final String PREF_FILE_SPF = "pref_path";
	
	
	public static final String DEFAULT_ROOT = "/sdcard/scratch2android/defaultProject";
	public static final String DEFAULT_PROJECT = "/sdcard/scratch2android";
	public static final String DEFAULT_FILE = "defaultSaveFile.spf";
	public static final String DEFAULT_FILE_ENDING = ".spf";
	
	public static String ROOT_IMAGES;
	public static String ROOT_SOUNDS;
	public static String ROOT;
	public static String SPF_FILE;
	
	public SharedPreferences mPreferences;
	
	private Button mToolboxButton;
	private ToolBoxDialog mToolboxObjectDialog;
	private ToolBoxDialog mToolboxStageDialog;
	private Dialog mNewProjectDialog;
	private Dialog mChangeProgramNameDialog;
	private Dialog mLoadDialog;
	private ImageContainer mImageContainer;
	
	
	protected ListView mMainListView;
	private ConstructionSiteListViewAdapter mAdapter;
	private ContentManager mContentManager;

	private Button mSpritesToolboxButton;
    private SpritesDialog mSpritesToolboxDialog;
    
    
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.construction_site);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPreferences = this.getPreferences(Activity.MODE_PRIVATE);
        
        setRoot(mPreferences.getString(PREF_ROOT, DEFAULT_ROOT), mPreferences.getString(PREF_FILE_SPF, DEFAULT_FILE));
        
        mImageContainer = new ImageContainer(ROOT_IMAGES);
        mContentManager = new ContentManager(this);
        mContentManager.setObserver(this);
        
        
        mMainListView = (ListView) findViewById(R.id.MainListView);
        mAdapter = new ConstructionSiteListViewAdapter(this, 
        		mContentManager.getContentArrayList(), mMainListView, mImageContainer);
        mMainListView.setAdapter(mAdapter);
        
        mToolboxButton = (Button) this.findViewById(R.id.toolbar_button);
		mToolboxButton.setOnClickListener(this);
		
		mSpritesToolboxButton = (Button) this.findViewById(R.id.sprites_button);
		mSpritesToolboxButton.setOnClickListener(this);
		
		this.registerForContextMenu(mMainListView);
        //Testing
        //mContentManager.testSet();
        //mContentManager.saveContent();
        mContentManager.loadContent(SPF_FILE);
        setTitle(SPF_FILE);
    }

    private static int LAST_SELECTED_ELEMENT_POSITION = 0;
    private ImageView mPictureView;
    
    public void rememberLastSelectedElementAndView(int position, ImageView pictureView){
    	LAST_SELECTED_ELEMENT_POSITION = position;
    	mPictureView = pictureView;
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if((requestCode == MediaFileLoader.GALLERY_INTENT_CODE) && (data != null)){
			
			
			HashMap<String, String> content = mContentManager.getContentArrayList().get(LAST_SELECTED_ELEMENT_POSITION);
		      Uri u2 = Uri.parse(data.getDataString());
		      String[] projection = { MediaStore.Images.ImageColumns.DATA, 
		                        MediaStore.Images.ImageColumns.DISPLAY_NAME};
		        Cursor c = managedQuery(u2, projection, null, null, null);
		        if (c!=null && c.moveToFirst()) {
		        	 File image_full_path = new File(c.getString(0));
		        	 
		        	 String imageName = mImageContainer.saveImage(image_full_path.getAbsolutePath());
		        	 String imageThumbnailName = mImageContainer.saveThumbnail(image_full_path.getAbsolutePath());
		        	 
		        	 mImageContainer.deleteImage(content.get(BrickDefine.BRICK_VALUE));
		        	 mImageContainer.deleteImage(content.get(BrickDefine.BRICK_VALUE_1));
		        	 
		        	 content.put(BrickDefine.BRICK_VALUE, imageName);
		        	 content.put(BrickDefine.BRICK_VALUE_1, imageThumbnailName);
		             content.put(BrickDefine.BRICK_NAME, c.getString(1));
		             mAdapter.notifyDataSetChanged();
		             //debug
		             String column0Value = c.getString(0);
		             String column1Value = c.getString(1);

		             Log.d("Data",column0Value);
		             Log.d("Display name",column1Value);
		        }
		}
			
			
		super.onActivityResult(requestCode, resultCode, data);
	}
	
    protected Dialog onCreateDialog(int id){
        switch(id) {
        case TOOLBOX_DIALOG_SPRITE:
        	mToolboxObjectDialog = new ToolBoxDialog(this, mContentManager, BrickDefine.getToolBoxBrickContent(BrickDefine.OBJECT_CATEGORY));  
        	return mToolboxObjectDialog;
        case TOOLBOX_DIALOG_BACKGROUND:
        	mToolboxStageDialog = new ToolBoxDialog(this, mContentManager, BrickDefine.getToolBoxBrickContent(BrickDefine.STAGE_CATEGORY)); 
        	return mToolboxStageDialog;
        case SPRITETOOLBOX_DIALOG:
        	mSpritesToolboxDialog = new SpritesDialog(this, true, null, 0);
        	mSpritesToolboxDialog.setContentManager(mContentManager);
        	return mSpritesToolboxDialog;
        case NEW_PROJECT_DIALOG:
        	mNewProjectDialog = new NewProjectDialog(this, mContentManager);
        	return  mNewProjectDialog;
        case CHANGE_PROJECT_NAME_DIALOG:
        	mChangeProgramNameDialog = new ChangeProgramNameDialog(this, mContentManager);
        	return  mChangeProgramNameDialog;      	
        case LOAD_DIALOG:        	
        	mLoadDialog = new LoadProgramDialog(this, mContentManager);
        	return mLoadDialog;
    	
        default:
            return null;
        }
       
    }
	
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.construction_site_menu, menu);
		return true;
	}
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            return;
        }
        mAdapter.getItemId(info.position);
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.construction_site_context_menu, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterView.AdapterContextMenuInfo info;
    	info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    	mContentManager.removeBrick(info.position);
    	return true;
    };

    public static final String INTENT_EXTRA_ROOT = "intent_root";
    public static final String INTENT_EXTRA_ROOT_IMAGES = "intent_root_images";
    public static final String INTENT_EXTRA_ROOT_SOUNDS = "intent_root_sounds";
    public static final String INTENT_EXTRA_SPF_FILE_NAME = "intent_spf_file_name";
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.play:
        	Intent intent = new Intent(ConstructionSiteActivity.this, StageActivity.class);
        	intent.putExtra(INTENT_EXTRA_ROOT, ROOT);
        	intent.putExtra(INTENT_EXTRA_ROOT_IMAGES, ROOT_IMAGES);
        	intent.putExtra(INTENT_EXTRA_ROOT_SOUNDS, ROOT_SOUNDS);
        	intent.putExtra(INTENT_EXTRA_SPF_FILE_NAME, SPF_FILE);
        	
            startActivity(intent);
            return true;
            
        case R.id.reset:
        	Utils.deleteFolder(ROOT_IMAGES);
        	Utils.deleteFolder(ROOT_SOUNDS);
        	mContentManager.resetContent();
        	mContentManager.setDefaultStage();
        	updateViews();
            return true;
            
        case R.id.load:
        	mContentManager.saveContent(SPF_FILE);
        	showDialog(LOAD_DIALOG);
        	return true;
            
        case R.id.newProject:
        	mContentManager.saveContent(SPF_FILE);
        	showDialog(NEW_PROJECT_DIALOG);
        	return true;
        	
        case R.id.changeProject:
        	mContentManager.saveContent(SPF_FILE);
        	showDialog(CHANGE_PROJECT_NAME_DIALOG);
        	return true;
   
        default:
            return super.onOptionsItemSelected(item);
        }
    }

	public void update(Observable observable, Object data) {
		mAdapter.notifyDataSetChanged(mContentManager.getContentArrayList());	
		mMainListView.setSelection(mAdapter.getCount()-1);
	}

	public void onPause()
	{
		mContentManager.saveContent(SPF_FILE);
	    SharedPreferences.Editor editor = mPreferences.edit();
	    editor.putString(PREF_ROOT, ROOT);
	    editor.putString(PREF_FILE_SPF, SPF_FILE);
	    editor.commit();
		super.onPause();
	}
	
	public void onClick(View v) {
		if (v.getId() == R.id.toolbar_button) {
			openToolbox();
		}
		else if (v.getId() == R.id.sprites_button){
			openSpriteToolbox();
		}
	}
		
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_HOME){
			this.finish();
			return true;
		}
			
		return super.onKeyDown(keyCode, event);
	}

	private void openToolbox(){
		if(mContentManager.getCurrentSpriteName().equals(this.getString(R.string.stage)))
		{
			showDialog(TOOLBOX_DIALOG_BACKGROUND);
		}
		else
		{
			showDialog(TOOLBOX_DIALOG_SPRITE);
		}
	}
	
	private void openSpriteToolbox()
	{
		showDialog(SPRITETOOLBOX_DIALOG);
	}
	
	/**
	 * Test Method
	 * @return one of the Toolbox
	 */
	public Dialog getToolboxDialog(){
		return mToolboxStageDialog;
	}
	
	public void onBrickClickListener(View v) {
		//Log.d("TEST", "HALLO");
		if(mContentManager.getCurrentSpriteName().equals(this.getString(R.string.stage))){
			mContentManager.addBrick(mToolboxStageDialog.getBrickClone(v));
			if(mToolboxStageDialog.isShowing())
				mToolboxStageDialog.dismiss();
		}		
		else{
			mContentManager.addBrick(mToolboxObjectDialog.getBrickClone(v));
			if(mToolboxObjectDialog.isShowing())
				mToolboxObjectDialog.dismiss();
		}
			
			
	}
	public static void setRoot(String root, String file){
		File rootFile = new File(root);
		if(!rootFile.exists())
			rootFile.mkdirs();
		ConstructionSiteActivity.ROOT = rootFile.getPath();
		File rootImageFile = new File(Utils.concatPaths(root, "/images"));
		if(!rootImageFile.exists())
			rootImageFile.mkdirs();
		ConstructionSiteActivity.ROOT_IMAGES = rootImageFile.getPath();
		File rootSoundFile = new File(Utils.concatPaths(root, "/sounds"));
		if(!rootSoundFile.exists())
			rootSoundFile.mkdirs();
		ConstructionSiteActivity.ROOT_SOUNDS = rootSoundFile.getPath();
		
		SPF_FILE = file;
		File spfFile = new File(Utils.concatPaths(ROOT,SPF_FILE));
		if(!spfFile.exists())
			try {
				spfFile.createNewFile();
			} catch (IOException e) {
				Log.e("CONSTRUCTION_SITE_ACTIVITY", e.getMessage());
				e.printStackTrace();
			}
	}

 }