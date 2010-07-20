package com.tugraz.android.app;

import java.util.ArrayList;
import java.util.HashMap;

import com.tugraz.android.app.filesystem.MediaFileLoader;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class MainListViewAdapter extends BaseAdapter{
    private Context mCtx;
    private MediaFileLoader mMediaFileLoader;
    
    public ArrayList<HashMap<String, String>> mList;

    
	public MainListViewAdapter(Context context,
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
		//Testfall schreiben
		String type = mList.get(position).get(BrickDefine.BRICK_ID);
		if(type == null)
			return 0;
		else
			return Integer.valueOf(type).intValue();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		//Log.d("View", mList.toString());
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
			text1.setText(mList.get(position).get(BrickDefine.BRICK_NAME));
			TextView text2 = (TextView)view.getChildAt(1);
			text2.setText("TODO set data");
			
			Spinner spinner = (Spinner)view.getChildAt(2);
			
			
			//set adapter		
			final SimpleAdapter adapter = new SimpleAdapter(mCtx, mMediaFileLoader.getPictureContent(), R.layout.picture_spinner,
					new String[] {MediaFileLoader.PICTURE_THUMB, MediaFileLoader.PICTURE_NAME},
	                new int[] {R.id.PictureSpinnerImageView, R.id.PictureSpinnerTextView});
			spinner.setAdapter(adapter);
			return view;
		}
		case (BrickDefine.PLAY_SOUND): 
		{
		    LinearLayout view = (LinearLayout) inflater.inflate(R.layout.mlve_two_labels, null);
			TextView text1 = (TextView)view.getChildAt(0);
			text1.setText(mList.get(position).get(BrickDefine.BRICK_NAME));
			TextView text2 = (TextView)view.getChildAt(1);
			text2.setText("TODO set data");
			
			return view;
		}
		case (BrickDefine.WAIT): 
		{
			LinearLayout view =  (LinearLayout)inflater.inflate(R.layout.mlve_two_labels_edit, null);
			TextView text1 = (TextView)view.getChildAt(0);
			text1.setText(mList.get(position).get(BrickDefine.BRICK_NAME));
			LinearLayout view2 = (LinearLayout)view.getChildAt(1);
			  TextView text2 = (TextView) view2.getChildAt(0);
			  text2.setText("TODO set data");
	          EditText etext = (EditText) view2.getChildAt(1);
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

	

}
