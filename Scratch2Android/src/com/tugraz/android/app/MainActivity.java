package com.tugraz.android.app;


import java.util.Observable;
import java.util.Observer;
import java.util.TooManyListenersException;


import android.app.Activity;
import android.content.Context;
import android.app.Dialog;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements Observer, OnClickListener{

    /** Called when the activity is first created. */
	
	static final int TOOLBOX_DIALOG = 0;
	
	protected ListView mMainListView;
	private MainListViewAdapter mAdapter;
	private ContentManager mContentManager;
	
	
	private Button mToolboxButton;
	private ToolboxDialog mToolboxDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContentManager = new ContentManager();
        mContentManager.setObserver(this);
        mContentManager.setContext(this);
        mAdapter = new MainListViewAdapter(this, mContentManager.mContentArrayList);
        
        mMainListView = (ListView) findViewById(R.id.MainListView);
        mMainListView.setAdapter(mAdapter);
        
        //Testing
        mContentManager.testSet();
        //mContentManager.saveContent();
        //Standard save at end load at begining
        //mContentManager.loadContent();
        
        this.registerForContextMenu(mMainListView);
        
        mToolboxButton = (Button) this.findViewById(R.id.toolbar_button);
		mToolboxButton.setOnClickListener(this);
		
    }

    
    protected Dialog onCreateDialog(int id){
        switch(id) { //TODO kommt er hier nur einmal her oder bei jedem aufruf?
        case TOOLBOX_DIALOG:
        	mToolboxDialog = new ToolboxDialog(this, true, null, 0); //TODO passen argumente so? 
        	mToolboxDialog.setContentManager(mContentManager);
            break;
        default:
            mToolboxDialog = null;
        }
        return mToolboxDialog;

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
   
        default:
            return super.onOptionsItemSelected(item);
        }
    }

	
	public void update(Observable observable, Object data) {
		Log.d("View1", mAdapter.mList.toString());
		Log.d("View1", mMainListView.toString());

		mAdapter.notifyDataSetChanged();
		Log.d("View2", mAdapter.mList.toString());
		Log.d("View2", mContentManager.getContentArrayList().toString());
	}
	
	//automatic save
	public void onStop()
	{
		mContentManager.saveContent();
		super.onStop();
	}
	
	
	public void onClick(View v) {
		if (v.getId() == R.id.toolbar_button) {
			openToolbox();
		}
		
	}
		
	private void openToolbox(){
		showDialog(TOOLBOX_DIALOG);
	}
	
	public ToolboxDialog getToolboxDialog(){
		return mToolboxDialog;
	}
	public BaseAdapter getAdapter(){
		return mAdapter;
	}
 }