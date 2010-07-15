package com.tugraz.android.app;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.TooManyListenersException;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener  {
    /** Called when the activity is first created. */
	
	static final int TOOLBOX_DIALOG = 0;
	
	protected ListView mMainListView;
	//TODO change public list and adapter
	public ArrayList<HashMap<String, String>> mList = new ArrayList<HashMap<String,String>>(); 
	public MainListViewAdapter adapter = new MainListViewAdapter(this, mList);
	
	private Button mToolboxButton;
	private ToolboxDialog mToolboxDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        
        
        //Bsp.: List; Testdaten
               
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "1");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
        map.put(BrickDefine.BRICK_NAME, "Test1");
        map.put(BrickDefine.BRICK_VALUE, "bla");
        mList.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "2");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
        map.put(BrickDefine.BRICK_NAME, "Test2");
        map.put(BrickDefine.BRICK_VALUE, "blabla1");
        mList.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "3");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
        map.put(BrickDefine.BRICK_NAME, "Test3");
        map.put(BrickDefine.BRICK_VALUE, "blabla2");
        mList.add(map);
              
        mMainListView = (ListView) findViewById(R.id.MainListView);
        mMainListView.setAdapter(adapter);
        this.registerForContextMenu(mMainListView);
        
        mToolboxButton = (Button) this.findViewById(R.id.toolbar_button);
		mToolboxButton.setOnClickListener(this);
      
    }

    
    protected Dialog onCreateDialog(int id){
        switch(id) { //TODO kommt er hier nur einmal her oder bei jedem aufruf?
        case TOOLBOX_DIALOG:
        	mToolboxDialog = new ToolboxDialog(this, true, null, 0); //TODO passen argumente so?  
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
        adapter.getItemId(info.position);
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.maincontextmenu, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterView.AdapterContextMenuInfo info;
    	info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    	mList.remove(info.position);
        adapter.notifyDataSetChanged();
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
        	mList.clear();
        	adapter.notifyDataSetChanged();
            return true;
   
        default:
            return super.onOptionsItemSelected(item);
        }
    }

	@Override
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
 }