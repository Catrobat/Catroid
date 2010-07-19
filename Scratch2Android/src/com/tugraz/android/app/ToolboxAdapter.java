package com.tugraz.android.app;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ToolboxAdapter extends BaseAdapter{
    private Context mCtx;
    public ArrayList<HashMap<String, String>> mList;
    private ContentManager mContentManager;
    
	public ToolboxAdapter(Context context,
			ArrayList<HashMap<String, String>> data) {
		mCtx = context;
		mList = data;	
	}
	
	

	public int getCount() {
		return mList.size();
	}

	public Object getItem(int arg0) {
		
		return mList.get(arg0);
	}

	public long getItemId(int position) {
		//Testfall schreiben
		String type = mList.get(position).get(BrickDefine.BRICK_ID);
		if(type == null)
			return 0;
		else
			return Integer.valueOf(type).intValue();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		//TODO check convertView
		//TODO Reuse Views
		//Type of the Brick
		String type = mList.get(position).get(BrickDefine.BRICK_TYPE);
		//Inflater to build the views
		LayoutInflater inflater = (LayoutInflater)mCtx.getSystemService(
	      Context.LAYOUT_INFLATER_SERVICE);
		
		//Check the type
		switch(Integer.valueOf(type).intValue()){
		case (BrickDefine.SET_BACKGROUND): 
		{
			LinearLayout view = (LinearLayout) inflater.inflate(R.layout.mlve_two_labels, null);
			TextView text1 = (TextView)view.getChildAt(0);
			text1.setText("SET_BACKGROUND");
			TextView text2 = (TextView)view.getChildAt(1);
			text2.setText("BausteinSetBackground");
			view.setClickable(true);
			view.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					HashMap<String, String> map = new HashMap<String, String>();
			        map.put(BrickDefine.BRICK_ID, "1");
			        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
			        map.put(BrickDefine.BRICK_NAME, "SetBackground");
			        map.put(BrickDefine.BRICK_VALUE, "bla");
					mContentManager.add(map);
				}
			});
			return view;
		}
		case (BrickDefine.PLAY_SOUND): 
		{
		    LinearLayout view = (LinearLayout) inflater.inflate(R.layout.mlve_two_labels, null);
			TextView text1 = (TextView)view.getChildAt(0);
			text1.setText("PLAY_SOUND");
			TextView text2 = (TextView)view.getChildAt(1);
			text2.setText("BausteinPlaySound");
			view.setClickable(true);
            view.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					HashMap<String, String> map = new HashMap<String, String>();
			        map.put(BrickDefine.BRICK_ID, "2");
			        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
			        map.put(BrickDefine.BRICK_NAME, "PlaySound");
			        map.put(BrickDefine.BRICK_VALUE, "bla");
					mContentManager.add(map);
					
				}
			});
			return view;
		}
		case (BrickDefine.WAIT): 
		{
			LinearLayout view =  (LinearLayout)inflater.inflate(R.layout.mlve_two_labels_edit, null);
			TextView text1 = (TextView)view.getChildAt(0);
			text1.setText("WAIT");
			LinearLayout view2 = (LinearLayout)view.getChildAt(1);
			TextView text2 = (TextView) view2.getChildAt(0);
	        text2.setText("BausteinWait");
	        view.setClickable(true);
            view.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					HashMap<String, String> map = new HashMap<String, String>();
			        map.put(BrickDefine.BRICK_ID, "3");
			        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
			        map.put(BrickDefine.BRICK_NAME, "Wait");
			        map.put(BrickDefine.BRICK_VALUE, "bla");
					mContentManager.add(map);
					
				}
			});
			return view;
		}
		case (BrickDefine.NOT_DEFINED):
		{
			return inflater.inflate(R.layout.mlve_two_labels, parent);
		}
		default: 
		{
			//TODO: Not defined Error
			return null;
	    }
		
		}
	}

	public void setContentManager(ContentManager contentManager) {
		mContentManager = contentManager;
		
	}

	

}
