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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class ToolboxSpritesAdapter extends BaseAdapter{
    private Context mCtx;
    public ArrayList<String> mList;
    private ContentManager mContentManager;
  
	public ToolboxSpritesAdapter(Context context,
			ArrayList<String> data) {
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
		String type = mList.get(position);
		if(type == null)
			return 0;
		else
			return 0;//TODO Check this Integer.valueOf(type).intValue();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mCtx.getSystemService(
	    Context.LAYOUT_INFLATER_SERVICE);
		
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.spritetoolbox, null);
		view.setTag(mList.get(position));
		TextView text = (TextView)view.getChildAt(0);
		text.setText(mList.get(position));
		text.setTextColor(Color.BLUE);
		//view.setBackgroundColor(Color.argb(255, 255, 255, 139));		
	    view.setOnClickListener(new View.OnClickListener() {				
				
				public void onClick(View v) {
					mContentManager.switchSprite(((LinearLayout)v).getTag().toString());
				}
			});
		return view;
		}
	
	
	public void setContentManager(ContentManager contentManager) {
		mContentManager = contentManager;
		
	}


}
