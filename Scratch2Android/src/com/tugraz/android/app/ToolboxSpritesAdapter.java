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

public class ToolboxSpritesAdapter extends BaseAdapter{
    private Context mCtx;
    public ArrayList<HashMap<String, String>> mList;
    private ContentManager mContentManager;
    
	public ToolboxSpritesAdapter(Context context,
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
		String type = mList.get(position).get(BrickDefine.BRICK_ID);
		if(type == null)
			return 0;
		else
			return Integer.valueOf(type).intValue();
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater)mCtx.getSystemService(
	    Context.LAYOUT_INFLATER_SERVICE);
		
		    //Check the type
	    LinearLayout view = (LinearLayout) inflater.inflate(R.layout.spritetoolbox, null);
		TextView text = (TextView)view.getChildAt(0);
		text.setText("Hier steht mein Name");
		view.setBackgroundColor(Color.argb(255, 139, 0, 139));		
	    view.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					//TODO ContentManager switch to new Sprite
				}
			});
			return view;
		}
	
	
	public void setContentManager(ContentManager contentManager) {
		mContentManager = contentManager;
		
	}

}
