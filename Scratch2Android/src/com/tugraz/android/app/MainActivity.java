package com.tugraz.android.app;


import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */

	
	private ListView mMainListView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Bsp.: List; Testdaten
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>(); 
        
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "1");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
        map.put(BrickDefine.BRICK_NAME, "Test1");
        map.put(BrickDefine.BRICK_VALUE, "bla");
        list.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "2");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
        map.put(BrickDefine.BRICK_NAME, "Test2");
        map.put(BrickDefine.BRICK_VALUE, "blabla1");
        list.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "3");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
        map.put(BrickDefine.BRICK_NAME, "Test3");
        map.put(BrickDefine.BRICK_VALUE, "blabla2");
        list.add(map);

         
        MainListViewAdapter adapter = new MainListViewAdapter(this, list);
         
        mMainListView = (ListView) findViewById(R.id.MainListView);
        mMainListView.setAdapter(adapter);
    }
    
    
    
}