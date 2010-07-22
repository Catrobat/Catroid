package com.tugraz.android.app;

import java.util.ArrayList;
import java.util.HashMap;

import com.tugraz.android.app.filesystem.MediaFileLoader;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
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
		mMediaFileLoader.loadSoundContent();
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
		final HashMap<String, String> brick = mList.get(position);
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
			//text1.setTextColor(Color.BLUE);
			TextView text = (TextView)view.getChildAt(0);
			text.setText("Setze Hintergrund:");
			//text2.setTextColor(Color.BLUE);
			view.setBackgroundColor(Color.argb(255, 139, 0, 139));

			Spinner spinner = (Spinner)view.getChildAt(1);
			
			
			//set adapter		
			final SimpleAdapter adapter = new SimpleAdapter(mCtx, mMediaFileLoader.getPictureContent(), R.layout.picture_spinner,
					new String[] {MediaFileLoader.PICTURE_THUMB, MediaFileLoader.PICTURE_NAME},
	                new int[] {R.id.PictureSpinnerImageView, R.id.PictureSpinnerTextView});
			
			spinner.setAdapter(adapter);
			OnItemSelectedListener listener = new OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
						brick.put(BrickDefine.BRICK_VALUE, ((HashMap<String, String>)adapter.getItem(arg2)).get(MediaFileLoader.PICTURE_PATH));
				}
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}				
			};
		    spinner.setOnItemSelectedListener(listener);
		    spinner.setSelection(getIndexFromElementPicture(adapter, brick.get(BrickDefine.BRICK_VALUE)));
//		    LayoutParams params = (LayoutParams) view.getLayoutParams();
//		    params.addRule(RelativeLayout.ALIGN_BOTTOM, parent.getChildAt(size-1).getId());
//		    view.setLayoutParams(params);

			return view;
		}
		case (BrickDefine.PLAY_SOUND): 
		{
		    LinearLayout view = (LinearLayout) inflater.inflate(R.layout.mlve_two_labels, null);
			TextView text = (TextView)view.getChildAt(0);
			text.setText("Spiele Klang:");
			
			Spinner spinner = (Spinner)view.getChildAt(1);
         
			final SimpleAdapter adapter = new SimpleAdapter(mCtx, mMediaFileLoader.getSoundContent(), R.layout.picture_spinner,
					new String[] {MediaFileLoader.SOUND_THUMB, MediaFileLoader.SOUND_NAME},
	                new int[] {R.id.PictureSpinnerImageView, R.id.PictureSpinnerTextView});
			spinner.setAdapter(adapter);
			spinner.setSelection(0);
			OnItemSelectedListener listener = new OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
						brick.put(BrickDefine.BRICK_VALUE, ((HashMap<String, String>)adapter.getItem(arg2)).get(MediaFileLoader.SOUND_PATH));
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
				
			};
		    spinner.setOnItemSelectedListener(listener);
			view.setBackgroundColor(Color.BLUE);
			spinner.setSelection(getIndexFromElementSound(adapter, brick.get(BrickDefine.BRICK_VALUE)));
			return view;
		}
		case (BrickDefine.WAIT): 
		{
			LinearLayout view =  (LinearLayout)inflater.inflate(R.layout.mlve_two_labels_edit, null);
			  TextView text = (TextView) view.getChildAt(0);
			  text.setText("Warte ");
			  text.setTextColor(Color.BLUE);
	          EditText etext = (EditText) view.getChildAt(1);
	          etext.setText("1");
	          
	          etext.setText(brick.get(BrickDefine.BRICK_VALUE));
	          
	          etext.addTextChangedListener(new TextWatcher()
	          {

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					brick.remove(BrickDefine.BRICK_VALUE);
					brick.put(BrickDefine.BRICK_VALUE, s.toString());
					
				}
	        	  
	        	  
	          });
	          
	          view.setBackgroundColor(Color.argb(255, 255, 215, 0));
	        
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
  public int  getIndexFromElementPicture(SimpleAdapter adapter, String element) {
	  ArrayList<HashMap<String, String>> arrayList = mMediaFileLoader.getPictureContent();
    for(int i = 0; i < adapter.getCount(); i++) {
      String value = arrayList.get(i).get(MediaFileLoader.PICTURE_PATH);
	  if(value.equals((element))) {
	    return i;
	    }
	  }
	return 0;
	}
  public int  getIndexFromElementSound(SimpleAdapter adapter, String element) {
	  ArrayList<HashMap<String, String>> arrayList = mMediaFileLoader.getSoundContent();
    for(int i = 0; i < adapter.getCount(); i++) {
      String value = arrayList.get(i).get(MediaFileLoader.SOUND_PATH);
	  if(value.equals((element))) {
	    return i;
	    }
	  }
	return 0;
	}
	

}
