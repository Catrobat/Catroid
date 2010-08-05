package com.tugraz.android.app;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.TooManyListenersException;

import com.tugraz.android.app.filesystem.MediaFileLoader;


import android.app.Activity;
import android.content.Context;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.BaseTypes;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity implements Observer, OnClickListener{

    /** Called when the activity is first created. */
	
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
    }

    private static int LAST_SELECTED_ELEMENT_POSITION = 0;
    
    public void rememberLastSelectedElement(int position){
    	LAST_SELECTED_ELEMENT_POSITION = position;
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if((requestCode == MediaFileLoader.GALLERY_INTENT_CODE) && (data != null)){
			HashMap<String, String> content = mContentManager.getContentArrayList().get(LAST_SELECTED_ELEMENT_POSITION);
			content.put(BrickDefine.BRICK_VALUE, data.getDataString());
			
			Log.d("TEST", data.getDataString());
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
				dismissDialog(SAVE_DIALOG);
				}
			});
        	
        	return  mSaveDialog;
        case LOAD_DIALOG:
        	mLoadDialog = new Dialog(this);
        	mLoadDialog.setContentView(R.layout.loaddialoglayout);
        	ListView view = (ListView)mLoadDialog.findViewById(R.id.loadfilelist);
        	ArrayList<String> list = new ArrayList<String>();
        	for(int i=0; i<this.fileList().length; i++)
        	{
        		if(fileList()[i].contains(".spf"))
        		list.add(fileList()[i]);
        	}
        	FileAdapter adapter = new FileAdapter(this, list);
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
        	Intent intent = new Intent(MainActivity.this, StageActivity.class);
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