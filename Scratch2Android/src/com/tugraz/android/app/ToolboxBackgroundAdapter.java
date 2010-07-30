package com.tugraz.android.app;

import java.util.ArrayList;
import java.util.HashMap;

import com.tugraz.android.app.filesystem.MediaFileLoader;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class ToolboxBackgroundAdapter extends BaseAdapter{
    private Context mCtx;
    public ArrayList<HashMap<String, String>> mList;
    private ContentManager mContentManager;
    private MediaFileLoader mMediaFileLoader;
    
	public ToolboxBackgroundAdapter(Context context,
			ArrayList<HashMap<String, String>> data) {
		mCtx = context;
		mList = data;
		
		mMediaFileLoader = new MediaFileLoader(mCtx);
		mMediaFileLoader.loadPictureContent();
	}
	
	

	public int getCount() {
		return mList.size();
	}

	public Object getItem(int arg0) {
		
		return mList.get(arg0);
	}

	public long getItemId(int position) {
		String type = mList.get(position).get(BrickDefine.BRICK_ID);
		if(type == null)
			return 0;
		else
			return Integer.valueOf(type).intValue();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		//TODO Reuse Views
		String type = mList.get(position).get(BrickDefine.BRICK_TYPE);
		LayoutInflater inflater = (LayoutInflater)mCtx.getSystemService(
	      Context.LAYOUT_INFLATER_SERVICE);
		
		switch(Integer.valueOf(type).intValue()){
		case (BrickDefine.SET_BACKGROUND): 
		{
			LinearLayout view = (LinearLayout) inflater.inflate(R.layout.mlve_two_labels, null);
			TextView text = (TextView)view.getChildAt(0);
			text.setText(R.string.set_background_main_adapter);
			view.setBackgroundColor(Color.argb(255, 139, 0, 139));
			Spinner spinner = (Spinner)view.getChildAt(1);
			final SimpleAdapter adapter = new SimpleAdapter(mCtx, mMediaFileLoader.getPictureContent(), R.layout.picture_spinner,
					new String[] {MediaFileLoader.PICTURE_THUMB, MediaFileLoader.PICTURE_NAME},
	                new int[] {R.id.PictureSpinnerImageView, R.id.PictureSpinnerTextView});
			spinner.setAdapter(adapter);
			view.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					HashMap<String, String> map = new HashMap<String, String>();
			        map.put(BrickDefine.BRICK_ID, "1");
			        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
			        map.put(BrickDefine.BRICK_NAME, "SetBackground");
			        map.put(BrickDefine.BRICK_VALUE, "1");
					mContentManager.add(map);
				}
			});
			return view;
		}
		case (BrickDefine.PLAY_SOUND): 
		{
			LinearLayout view = (LinearLayout) inflater.inflate(R.layout.mlve_two_labels, null);
			TextView text = (TextView)view.getChildAt(0);
			text.setText(R.string.play_sound_main_adapter);
			
			view.setBackgroundColor(Color.BLUE);
            view.setOnClickListener(new View.OnClickListener() {
				
				@Override
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
			LinearLayout view =  (LinearLayout)inflater.inflate(R.layout.mlve_two_labels_edit, null);
			  TextView text = (TextView) view.getChildAt(0);
			  text.setText(R.string.wait_main_adapter);
			  text.setTextColor(Color.BLUE);
	          EditText etext = (EditText) view.getChildAt(1);
	          
	          view.setBackgroundColor(Color.argb(255, 255, 215, 0));
            view.setOnClickListener(new View.OnClickListener() {
				
				@Override
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
