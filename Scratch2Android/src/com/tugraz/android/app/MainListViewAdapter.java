package com.tugraz.android.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.tugraz.android.app.filesystem.MediaFileLoader;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class MainListViewAdapter extends BaseAdapter{
    protected static final float THUMBNAIL_WIDTH = 80;
	protected static final float THUMBNAIL_HEIGHT = 80;
	private Context mCtx;
    private MediaFileLoader mMediaFileLoader;
    private ListView mMainListView;
    public ArrayList<HashMap<String, String>> mList;

    
	public MainListViewAdapter(Context context,
			ArrayList<HashMap<String, String>> data, ListView listview) {
		mCtx = context;
		mList = data;	
		mMainListView = listview;
		mMediaFileLoader = new MediaFileLoader(mCtx);
		mMediaFileLoader.loadSoundContent();
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
		final HashMap<String, String> brick = mList.get(position);
		//TODO Reuse Views
		final String type = mList.get(position).get(BrickDefine.BRICK_TYPE);
		final String value = mList.get(position).get(BrickDefine.BRICK_VALUE);

		LayoutInflater inflater = (LayoutInflater)mCtx.getSystemService(
	      Context.LAYOUT_INFLATER_SERVICE); 
		switch(Integer.valueOf(type).intValue()){
		case (BrickDefine.SET_BACKGROUND): 
		{
			RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.brick_set_background, null);
			TextView text = (TextView)view.getChildAt(1);
			text.setText(R.string.set_background_main_adapter);

			ImageView imageView = (ImageView)view.getChildAt(0);
			imageView.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					    Log.d("Position", ((Integer)mMainListView.getPositionForView(v)).toString());
						mMediaFileLoader.openPictureGallery(mMainListView.getPositionForView(v), v);
				}		
				});
		    if(value != ""){
			Bitmap bm = null;	
			Log.d("Content", value);
		    bm = BitmapFactory.decodeFile(value); 
		    imageView.setBackgroundResource(0);
			Matrix matrix = new Matrix();
			float scaleWidth = (((float)THUMBNAIL_WIDTH)/bm.getWidth());
			float scaleHeight = (((float)THUMBNAIL_HEIGHT)/bm.getHeight());
		    matrix.postScale(scaleWidth, scaleHeight);
			Bitmap newbm = Bitmap.createBitmap(bm, 0, 0,bm.getWidth() ,bm.getHeight() , matrix, true);
			imageView.setImageBitmap(newbm);
		    }
//		    LayoutParams params = (LayoutParams) view.getLayoutParams();
//		    params.addRule(RelativeLayout.ALIGN_BOTTOM, parent.getChildAt(size-1).getId());
//		    view.setLayoutParams(params);

			return view;
		}
		case (BrickDefine.PLAY_SOUND): 
		{
			RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.brick_play_sound, null);
			TextView text = (TextView)view.getChildAt(0);
			text.setText(R.string.play_sound_main_adapter);
			
			Spinner spinner = (Spinner)view.getChildAt(1);
         
			final SimpleAdapter adapter = new SimpleAdapter(mCtx, mMediaFileLoader.getSoundContent(), R.layout.sound_spinner,
					new String[] {MediaFileLoader.SOUND_NAME},
	                new int[] {R.id.SoundSpinnerTextView});
			spinner.setAdapter(adapter);
			if (adapter.getCount() != 0) {
				spinner.setSelection(0);
				OnItemSelectedListener listener = new OnItemSelectedListener(){
	
					
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
							brick.put(BrickDefine.BRICK_VALUE, ((HashMap<String, String>)adapter.getItem(arg2)).get(MediaFileLoader.SOUND_PATH));
					}
	
					
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
						
					}
					
				};
			    spinner.setOnItemSelectedListener(listener);
				//view.setBackgroundColor(Color.BLUE);
				spinner.setSelection(getIndexFromElementSound(adapter, brick.get(BrickDefine.BRICK_VALUE)));
			}
			
			return view;
		}
		case (BrickDefine.WAIT): 
		{
			RelativeLayout view =  (RelativeLayout)inflater.inflate(R.layout.brick_wait, null);
			  TextView text = (TextView) view.getChildAt(0);
			  text.setText(R.string.wait_main_adapter);
			  //text.setTextColor(Color.BLUE);
	          EditText etext = (EditText) view.getChildAt(1);
	          etext.setText("1");
	          
	          etext.setText(brick.get(BrickDefine.BRICK_VALUE));
	          
	          etext.addTextChangedListener(new TextWatcher()
	          {

				
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					
				}

				
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub
					
				}

				
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					brick.remove(BrickDefine.BRICK_VALUE);
					brick.put(BrickDefine.BRICK_VALUE, s.toString());	
				}  
	          });
	         // view.setBackgroundColor(Color.argb(255, 255, 215, 0));
			return view;
			
		}
		case (BrickDefine.HIDE):
		{
			LinearLayout view =  (LinearLayout)inflater.inflate(R.layout.brick_simple_text_view, null);
			TextView text = (TextView) view.getChildAt(0);
			text.setText(R.string.hide_main_adapter);
			//view.setBackgroundColor(Color.argb(255, 255, 215, 100));
			return view;
		}
		case (BrickDefine.SHOW):
		{
			LinearLayout view =  (LinearLayout)inflater.inflate(R.layout.brick_simple_text_view, null);
			TextView text = (TextView) view.getChildAt(0);
			text.setText(R.string.show_main_adapter);
			//view.setBackgroundColor(Color.argb(255, 255, 215, 200));
			return view;
		}
		 case (BrickDefine.GO_TO):
		  {
		   RelativeLayout view =  (RelativeLayout)inflater.inflate(R.layout.brick_goto, null);
		   
		   TextView text = (TextView) view.getChildAt(0);
		   text.setText(R.string.goto_main_adapter);
		   
		   LinearLayout layout = (LinearLayout) view.getChildAt(1);
		   
		   EditText textX = (EditText) layout.getChildAt(0);
		   textX.setText(brick.get(BrickDefine.BRICK_VALUE));
		   textX.addTextChangedListener(new TextWatcher()
		           {
		    
		    public void afterTextChanged(Editable s) {
		     // TODO Auto-generated method stub 
		    }
		    
		    public void beforeTextChanged(CharSequence s, int start,
		      int count, int after) {
		     // TODO Auto-generated method stub     
		    }
		    
		    public void onTextChanged(CharSequence s, int start,
		      int before, int count) {
		     brick.remove(BrickDefine.BRICK_VALUE);
		     brick.put(BrickDefine.BRICK_VALUE, s.toString());     
		    } 
		           });
		   
		   EditText textY = (EditText) layout.getChildAt(1);
		   textY.setText(brick.get(BrickDefine.BRICK_VALUE_1));
		   textY.addTextChangedListener(new TextWatcher()
		           {
		    
		    public void afterTextChanged(Editable s) {
		     // TODO Auto-generated method stub 
		    }
		    
		    public void beforeTextChanged(CharSequence s, int start,
		      int count, int after) {
		     // TODO Auto-generated method stub 
		    }
		    
		    public void onTextChanged(CharSequence s, int start,
		      int before, int count) {
		     brick.remove(BrickDefine.BRICK_VALUE_1);
		     brick.put(BrickDefine.BRICK_VALUE_1, s.toString()); 
		    } 
		           });
		   //view.setBackgroundColor(Color.argb(255, 255, 215, 255));
		   return view;
		  }
	
		case (BrickDefine.SET_COSTUME): 
		{
			RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.brick_set_costume, null);
			//text1.setTextColor(Color.BLUE);
			TextView text = (TextView)view.getChildAt(1);
			text.setText(R.string.costume_main_adapter);
			//text2.setTextColor(Color.BLUE);
			//view.setBackgroundColor(Color.argb(255, 139, 0, 50));

			ImageView imageView = (ImageView)view.getChildAt(0);
			imageView.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
				  mMediaFileLoader.openPictureGallery(mMainListView.getPositionForView(v), v);
				}
			});
			if(value != ""){
				Bitmap bm = null;	
				Log.d("Content", value);
			    bm = BitmapFactory.decodeFile(value); 
			    imageView.setBackgroundResource(0);
				Matrix matrix = new Matrix();
				float scaleWidth = (((float)THUMBNAIL_WIDTH)/bm.getWidth());
				float scaleHeight = (((float)THUMBNAIL_HEIGHT)/bm.getHeight());
			    matrix.postScale(scaleWidth, scaleHeight);
				Bitmap newbm = Bitmap.createBitmap(bm, 0, 0,bm.getWidth() ,bm.getHeight() , matrix, true);
				imageView.setImageBitmap(newbm);
			    }
			
			return view;
		}
		
		
		
		
		case (BrickDefine.NOT_DEFINED):
		{
			return null;
		}
		default: 
		{
			// Not defined Error
			return null;
	    }
		
		}
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


public void setImage(View v){
	String value = mList.get(mMainListView.getPositionForView(v)).get(BrickDefine.BRICK_VALUE);
	Bitmap bm = null;		
    bm = BitmapFactory.decodeFile(value); 
    v.setBackgroundResource(0);
	Matrix matrix = new Matrix();
	float scaleWidth = (((float)THUMBNAIL_WIDTH)/bm.getWidth());
	float scaleHeight = (((float)THUMBNAIL_HEIGHT)/bm.getHeight());
    matrix.postScale(scaleWidth, scaleHeight);
	Bitmap newbm = Bitmap.createBitmap(bm, 0, 0,bm.getWidth() ,bm.getHeight() , matrix, true);
	((ImageView)v).setImageBitmap(newbm);
	
}
	
}
