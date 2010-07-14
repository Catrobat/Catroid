package com.tugraz.android.app;


import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends Activity implements Observer{
    /** Called when the activity is first created. */

	
	protected ListView mMainListView;
	private MainListViewAdapter mAdapter;
	private ContentManager mContentManager;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContentManager = new ContentManager();
        mContentManager.loadContent();
        MainListViewAdapter mAdapter = new MainListViewAdapter(this, mContentManager.getContentArrayList());
              
        mMainListView = (ListView) findViewById(R.id.MainListView);
        mMainListView.setAdapter(mAdapter);
        this.registerForContextMenu(mMainListView);
      
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
        	mAdapter.notifyDataSetChanged();
            return true;
   
        default:
            return super.onOptionsItemSelected(item);
        }
    }

	public void update(Observable observable, Object data) {
		mAdapter.notifyDataSetChanged();
	}
 }