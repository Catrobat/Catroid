package at.tugraz.ist.s2a;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import at.tugraz.ist.s2a.constructionSite.content.BrickDefine;
import at.tugraz.ist.s2a.constructionSite.content.ContentManager;
import at.tugraz.ist.s2a.constructionSite.gui.adapter.FileAdapter;
import at.tugraz.ist.s2a.constructionSite.gui.adapter.MainListViewAdapter;
import at.tugraz.ist.s2a.constructionSite.gui.dialogs.ToolboxBackgroundDialog;
import at.tugraz.ist.s2a.constructionSite.gui.dialogs.ToolboxSpriteDialog;
import at.tugraz.ist.s2a.constructionSite.gui.dialogs.ToolboxSpritesDialog;
import at.tugraz.ist.s2a.stage.StageActivity;
import at.tugraz.ist.s2a.utils.filesystem.MediaFileLoader;

public class ConstructionSiteActivity extends Activity implements Observer, OnClickListener{

    /** Called when the activity is first created. */
	
	//TODO remove objects in object list after "l�sche baustelle"
	
	//TODO clean up the adapter, 3 of them do the same -> multiple code (is it necessary to distinguish between a stage and a sprite!?)
	//TODO rename some classes buttons etc they are often not significant
	//TODO make more packages
	//TODO style your gui elements either with java code or xml but no mixture
	
	//TODO IDs manage brick id 
	
	static final int TOOLBOX_DIALOG_SPRITE = 0;
	static final int TOOLBOX_DIALOG_BACKGROUND = 1;
	static final int SPRITETOOLBOX_DIALOG = 2;
	static final int SAVE_DIALOG = 3;
	static final int LOAD_DIALOG = 4;
	
	private Button mToolboxButton;
	private Dialog mToolboxDialog;
	private Dialog mSaveDialog;
	private Dialog mLoadDialog;
	
	protected ListView mMainListView;
	private MainListViewAdapter mAdapter;
	private ContentManager mContentManager;

	private Button mSpritesToolboxButton;
    private ToolboxSpritesDialog mSpritesToolboxDialog;
    private ArrayList<String> mFilelist;
    
    private View mPictureView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        mContentManager = new ContentManager();
        mContentManager.setObserver(this);
        mContentManager.setContext(this);
        mMainListView = (ListView) findViewById(R.id.MainListView);
        mAdapter = new MainListViewAdapter(this, mContentManager.getContentArrayList(), mMainListView);
        
        mMainListView.setAdapter(mAdapter);
            
        //Testing
        //mContentManager.testSet();
        //mContentManager.saveContent();
        mContentManager.loadContent();
        
        this.registerForContextMenu(mMainListView);
        
        mToolboxButton = (Button) this.findViewById(R.id.toolbar_button);
		mToolboxButton.setOnClickListener(this);
		
		mSpritesToolboxButton = (Button) this.findViewById(R.id.sprites_button);
		mSpritesToolboxButton.setOnClickListener(this);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mFilelist = new ArrayList<String>();
    }

    private static int LAST_SELECTED_ELEMENT_POSITION = 0;
    
    
    public void rememberLastSelectedElementAndView(int position, View pictureView){
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
		                String column0Value = c.getString(0);
		                String column1Value = c.getString(1);
		                
		                content.put(BrickDefine.BRICK_VALUE, c.getString(0));
		                content.put(BrickDefine.BRICK_NAME, c.getString(1));
		                Log.d("Data",column0Value);
		                Log.d("Display name",column1Value);
		        }
			
			mAdapter.setImage(mPictureView);
	
		}
			
			
		super.onActivityResult(requestCode, resultCode, data);
	}
	
    protected Dialog onCreateDialog(int id){
        switch(id) {
        case TOOLBOX_DIALOG_SPRITE:
        	mToolboxDialog = new ToolboxSpriteDialog(this, true, null, 0);  
        	((ToolboxSpriteDialog)mToolboxDialog).setContentManager(mContentManager);
        	return mToolboxDialog;
        case TOOLBOX_DIALOG_BACKGROUND:
        	mToolboxDialog = new ToolboxBackgroundDialog(this, true, null, 0); 
        	((ToolboxBackgroundDialog)mToolboxDialog).setContentManager(mContentManager);
        	return mToolboxDialog;
        case SPRITETOOLBOX_DIALOG:
        	mSpritesToolboxDialog = new ToolboxSpritesDialog(this, true, null, 0);
        	mSpritesToolboxDialog.setContentManager(mContentManager);
        	return mSpritesToolboxDialog;
        case SAVE_DIALOG:
        	mSaveDialog = new Dialog(this);
        	mSaveDialog.setContentView(R.layout.savedialoglayout);
        	mSaveDialog.setTitle(R.string.save_file_main);
        	EditText file = (EditText) mSaveDialog.findViewById(R.id.saveFilename);
        	file.setTextColor(Color.BLACK);
        	file.setText("filename");
        	Button saveButton = (Button) mSaveDialog.findViewById(R.id.saveButton);
        	saveButton.setText("Speichern");
        	saveButton.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
				EditText file = (EditText) mSaveDialog.findViewById(R.id.saveFilename);
				//Anmerkung speichert nur im Application Context
				File tfile = new File(file.getText().toString()+".spf");
				mContentManager.saveContent(tfile.toString());
				mFilelist.add(tfile.toString());
				dismissDialog(SAVE_DIALOG);
				}
			});
        	
        	return  mSaveDialog;
        case LOAD_DIALOG:
        	mLoadDialog = new Dialog(this);
        	mLoadDialog.setContentView(R.layout.loaddialoglayout);
        	ListView view = (ListView)mLoadDialog.findViewById(R.id.loadfilelist);
        	
        	File sdFile = new File("/sdcard/");
        	String[] sdFileList = sdFile.list();
        	
        	for(int i=0; i<sdFileList.length; i++)
        	{
        		if(sdFileList[i].contains(".spf"))
        		  mFilelist.add(sdFileList[i]);
        	}
        	//SimpleAdapter adapter = new SimpleAdapter(this, null, R.layout.spritetoolbox, new String[] {"NAME"}, new int[] { R.layout.spritetoolbox});
        	FileAdapter adapter = new FileAdapter(this, mFilelist);
        	view.setAdapter(adapter);
        	adapter.setDialog(mLoadDialog);
        	adapter.setContentManager(mContentManager);
        	return mLoadDialog;
        default:
            mToolboxDialog = null;
            return mToolboxDialog;
        }
       
    }
	
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.constructionsitemenu, menu);
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
        getMenuInflater().inflate(R.menu.maincontextmenu, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterView.AdapterContextMenuInfo info;
    	info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    	mContentManager.remove(info.position);
    	return true;
    };

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.play:
        	Intent intent = new Intent(ConstructionSiteActivity.this, StageActivity.class);
            startActivity(intent);
            return true;
            
        case R.id.reset:
        	mContentManager.clearSprites();
            return true;
            
        case R.id.load:
        	showDialog(LOAD_DIALOG);
        	return true;
            
        case R.id.save:
        	showDialog(SAVE_DIALOG);
        	return true;
   
        default:
            return super.onOptionsItemSelected(item);
        }
    }

	
	public void update(Observable observable, Object data) {
		
		mAdapter.notifyDataSetChanged();
		this.setTitle(mContentManager.getCurrentSprite());
	
	}
	
	public void onStop()
	{
		mContentManager.saveContent();
		super.onStop();
	}
	
	public void onPause()
	{
		mContentManager.saveContent();
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
		
	private void openToolbox(){
		if(mContentManager.getCurrentSprite().equals("stage"))
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
	
	public Dialog getToolboxDialog(){
		return mToolboxDialog;
	}
 }