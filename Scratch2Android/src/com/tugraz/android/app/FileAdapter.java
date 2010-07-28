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

public class FileAdapter extends BaseAdapter{
    private Context mCtx;
    public ArrayList<String> mList;
    private ContentManager mContentManager;
    private MediaFileLoader mMediaFileLoader;
    
	public FileAdapter(Context context,
			ArrayList<String> data) {
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
			return (position);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		String type = mList.get(position);
		//Inflater to build the views
		LayoutInflater inflater = (LayoutInflater)mCtx.getSystemService(
	      Context.LAYOUT_INFLATER_SERVICE);
		
		//Check the type
					LinearLayout view = (LinearLayout) inflater.inflate(R.layout.mlve_two_labels, null);
			//text1.setTextColor(Color.BLUE);
			TextView text = (TextView)view.getChildAt(0);
			text.setText("Setze Hintergrund:");
			//text2.setTextColor(Color.BLUE);
			view.setBackgroundColor(Color.argb(255, 139, 0, 139));
			return view;
  }


	public void setContentManager(ContentManager contentManager) {
		mContentManager = contentManager;
		
	}

	

}
