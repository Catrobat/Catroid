package com.tugraz.android.app;


import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
	private static final String BRICK_ID = "brick_id";
	private static final String BRICK_NAME = "brick_name";
	private static final String BRICK_VALUE = "brick_value";
	
	private ListView mMainListView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Bsp.: List; Testdaten
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>(); 
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(BRICK_ID, "1");
        map.put(BRICK_NAME, "Test1");
        map.put(BRICK_VALUE, "bla");
        list.add(map);
        map = new HashMap<String, String>();
        map.put(BRICK_ID, "2");
        map.put(BRICK_NAME, "Test2");
        map.put(BRICK_VALUE, "blabla");
        list.add(map);
        
        HashMap<Integer, Integer> viewElementMap = new HashMap<Integer, Integer>();
        viewElementMap.put(BrickDefine.SET_BACKGROUND, R.layout.mlve_two_labels);
        viewElementMap.put(BrickDefine.PLAY_SOUND, R.layout.mlve_two_labels_edit);
        
        HashMap<Integer, String[]> from = new HashMap<Integer, String[]>();
        HashMap<Integer, int[]> to = new HashMap<Integer, int[]>();
        //TODO fill the hashes
        
        //end bsp data
        MainListViewAdapter adapter = new MainListViewAdapter(this, list, viewElementMap, 
        from,to);
         
        mMainListView = (ListView) findViewById(R.id.MainListView);
        mMainListView.setAdapter(adapter);
    }
    
    
    
}