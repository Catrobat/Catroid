package com.tugraz.android.app;


import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.TooManyListenersException;


import android.app.Activity;
import android.content.Context;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.BaseTypes;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import android.widget.TextView;

public class MainActivity extends Activity implements Observer, OnClickListener{

    /** Called when the activity is first created. */
	
	static final int TOOLBOX_DIALOG_SPRITE = 0;
	static final int TOOLBOX_DIALOG_BACKGROUND = 1;
	static final int SPRITETOOLBOX_DIALOG = 2;
	static final int SAVE_DIALOG = 3;
	static final int LOAD_DIALOG = 4;
	
	protected ListView mMainListView;
	private MainListViewAdapter mAdapter;
	private ContentManager mContentManager;
	
	
	private Button mToolboxButton;
	private Dialog mToolboxDialog;
	private Dialog mSaveDialog;
	private Dialog mLoadDialog;

	private Button mSpritesToolboxButton;
    //TODO Eigener ToolboxDialog und eigener ToolboxAdapter
	private ToolboxSpritesDialog mSpritesToolboxDialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContentManager = new ContentManager();
        mContentManager.setObserver(this);
        mContentManager.setContext(this);
        mAdapter = new MainListViewAdapter(this, mContentManager.getContentArrayList());
        mMainListView = (ListView) findViewById(R.id.MainListView);
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
    }

    
    protected Dialog onCreateDialog(int id){
        switch(id) { //TODO kommt er hier nur einmal her oder bei jedem aufruf?
        case TOOLBOX_DIALOG_SPRITE:
        	mToolboxDialog = new ToolboxSpriteDialog(this, true, null, 0); //TODO passen argumente so?  
        	((ToolboxSpriteDialog)mToolboxDialog).setContentManager(mContentManager);
        	return mToolboxDialog;
        case TOOLBOX_DIALOG_BACKGROUND:
        	mToolboxDialog = new ToolboxBackgroundDialog(this, true, null, 0); //TODO passen argumente so?  
        	((ToolboxBackgroundDialog)mToolboxDialog).setContentManager(mContentManager);
        	return mToolboxDialog;
        case SPRITETOOLBOX_DIALOG:
        	mSpritesToolboxDialog = new ToolboxSpritesDialog(this, true, null, 0);
        	mSpritesToolboxDialog.setContentManager(mContentManager);
        	return mSpritesToolboxDialog;
        case SAVE_DIALOG:
        	mSaveDialog = new Dialog(this);
        	mSaveDialog.setContentView(R.layout.savedialoglayout);
        	mSaveDialog.setTitle("Save File");
        	EditText file = (EditText) mSaveDialog.findViewById(R.id.saveFilename);
        	file.setTextColor(Color.BLACK);
        	file.setText("filename");
        	Button saveButton = (Button) mSaveDialog.findViewById(R.id.saveButton);
        	saveButton.setText("Speichern");
        	saveButton.setOnClickListener(new OnClickListener() {
				
				@Override
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
        	this.fileList();
        	Log.d("Content", this.fileList()[0]+this.fileList()[1]+this.fileList()[2]);

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
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.play:
        	//TODO: save state
        	Intent intent = new Intent(MainActivity.this, StageActivity.class);
            startActivity(intent);
            return true;
            
        case R.id.reset:
        	mContentManager.clear();
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
	
	//automatic save
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