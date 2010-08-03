package com.tugraz.android.app;

import java.util.ArrayList;
import java.util.HashMap;

import com.tugraz.android.app.filesystem.MediaFileLoader;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ToolboxSpriteAdapter extends BaseAdapter{
    private Context mCtx;
    public ArrayList<HashMap<String, String>> mList;
    private ContentManager mContentManager;
    private MediaFileLoader mMediaFileLoader;
    
	public ToolboxSpriteAdapter(Context context,
			ArrayList<HashMap<String, String>> data) {
		mCtx = context;
		mList = data;
		
		mMediaFileLoader = new MediaFileLoader(mCtx);
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
		
		String type = mList.get(position).get(BrickDefine.BRICK_TYPE);

		LayoutInflater inflater = (LayoutInflater)mCtx.getSystemService(
	      Context.LAYOUT_INFLATER_SERVICE);
		
		switch(Integer.valueOf(type).intValue()){
			case (BrickDefine.PLAY_SOUND): 
		{
				RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.brick_play_sound, null);
			TextView text = (TextView)view.getChildAt(0);
			text.setText("Spiele Klang:");
			
		//	view.setBackgroundColor(Color.BLUE);
            view.setOnClickListener(new View.OnClickListener() {
				
				
				public void onClick(View v) {
					HashMap<String, String> map = new HashMap<String, String>();
			        map.put(BrickDefine.BRICK_ID, "2");
			        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
			        map.put(BrickDefine.BRICK_NAME, "PlaySound");
			        map.put(BrickDefine.BRICK_VALUE, "1");
					mContentManager.add(map);
					
				}
			});
			return view;
		}
		case (BrickDefine.WAIT): 
		{
			RelativeLayout view =  (RelativeLayout)inflater.inflate(R.layout.brick_wait, null);
			  TextView text = (TextView) view.getChildAt(0);
			  text.setText("Warte ");
			//  text.setTextColor(Color.BLUE);
	          EditText etext = (EditText) view.getChildAt(1);
	          
	         // view.setBackgroundColor(Color.argb(255, 255, 215, 0));
            view.setOnClickListener(new View.OnClickListener() {
				
				
				public void onClick(View v) {
					HashMap<String, String> map = new HashMap<String, String>();
			        map.put(BrickDefine.BRICK_ID, "3");
			        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
			        map.put(BrickDefine.BRICK_NAME, "Wait");
			        map.put(BrickDefine.BRICK_VALUE, "1");
					mContentManager.add(map);
					
				}
			});
			return view;
		}
		case (BrickDefine.HIDE): 
		{
			LinearLayout view =  (LinearLayout)inflater.inflate(R.layout.brick_simple_text_view, null);
		  TextView text = (TextView) view.getChildAt(0);
		  text.setText("Hide");
		  //text.setTextColor(Color.BLUE);
	      //view.setBackgroundColor(Color.argb(255, 255, 215, 100));
          view.setOnClickListener(new View.OnClickListener() {
				
				
				public void onClick(View v) {
					HashMap<String, String> map = new HashMap<String, String>();
			        map.put(BrickDefine.BRICK_ID, "4");
			        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.HIDE));
			        map.put(BrickDefine.BRICK_NAME, "Hide");
			        map.put(BrickDefine.BRICK_VALUE, "1");
					mContentManager.add(map);
					
				}
			});
          
			return view;
		}
		case (BrickDefine.SHOW): 
		{
			LinearLayout view =  (LinearLayout)inflater.inflate(R.layout.brick_simple_text_view, null);
		  TextView text = (TextView) view.getChildAt(0);
		  text.setText("Show");
		 // text.setTextColor(Color.BLUE);
	      //view.setBackgroundColor(Color.argb(255, 255, 215, 200));
          view.setOnClickListener(new View.OnClickListener() {
				
				
				public void onClick(View v) {
					HashMap<String, String> map = new HashMap<String, String>();
			        map.put(BrickDefine.BRICK_ID, "5");
			        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SHOW));
			        map.put(BrickDefine.BRICK_NAME, "Show");
			        map.put(BrickDefine.BRICK_VALUE, "1");
					mContentManager.add(map);
					
				}
			});
          
			return view;
		}
		case (BrickDefine.GO_TO): 
		{
			RelativeLayout view =  (RelativeLayout)inflater.inflate(R.layout.brick_goto, null);
		  TextView text = (TextView) view.getChildAt(0);
		  text.setText("GO-TO-XY");
		  text.setTextColor(Color.BLUE);
	     // view.setBackgroundColor(Color.argb(255, 255, 215, 255));
          view.setOnClickListener(new View.OnClickListener() {
				
				
				public void onClick(View v) {
					HashMap<String, String> map = new HashMap<String, String>();
			        map.put(BrickDefine.BRICK_ID, "6");
			        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.GO_TO));
			        map.put(BrickDefine.BRICK_NAME, "GO-TO-XY");
			        map.put(BrickDefine.BRICK_VALUE, "1");
			        map.put(BrickDefine.BRICK_VALUE_1, "1");
					mContentManager.add(map);
					
				}
			});
          
			return view;
		}
		case (BrickDefine.SET_COSTUME): 
		{
			RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.brick_set_costume, null);
			//text1.setTextColor(Color.BLUE);
			TextView text = (TextView)view.getChildAt(1);
			text.setText("Setze Kost�m:");
			//text2.setTextColor(Color.BLUE);
		//	view.setBackgroundColor(Color.argb(255, 139, 0, 50));

			ImageView imageView = (ImageView)view.getChildAt(0);
			
			imageView.setEnabled(false);
			view.setOnClickListener(new View.OnClickListener() {				
				
				public void onClick(View v) {
					HashMap<String, String> map = new HashMap<String, String>();
			        map.put(BrickDefine.BRICK_ID, "7");
			        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_COSTUME));
			        map.put(BrickDefine.BRICK_NAME, "SetCostume");
			        map.put(BrickDefine.BRICK_VALUE, "1");
					mContentManager.add(map);
				}
			});
			return view;
		}
		case (BrickDefine.NOT_DEFINED):
		{
			return null;
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
