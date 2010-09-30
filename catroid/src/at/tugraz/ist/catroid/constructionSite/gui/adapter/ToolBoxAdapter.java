package at.tugraz.ist.catroid.constructionSite.gui.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.BrickDefine;

public class ToolBoxAdapter extends BaseAdapter{

	private Context mCtx;
	private ArrayList<HashMap<String, String>> mContent;	
	private HashMap<String, View> mViewContainer;
	
	public ToolBoxAdapter(Context context,
			ArrayList<HashMap<String, String>> data) {
		mCtx = context;
		mContent = data;
		mViewContainer = new HashMap<String, View>();
	}
    
	public View getView(int position, View convertView, ViewGroup parent) {
		String type = mContent.get(position).get(BrickDefine.BRICK_TYPE);
		LayoutInflater inflater = (LayoutInflater)mCtx.getSystemService(
	      Context.LAYOUT_INFLATER_SERVICE);
		
		switch(Integer.valueOf(type).intValue()){
			
			case (BrickDefine.SET_BACKGROUND):
			{
				if(mViewContainer.containsKey(type))
					return mViewContainer.get(type);			
				RelativeLayout setBackgroundView = (RelativeLayout) 
					inflater.inflate(R.layout.toolbox_brick_set_background, null);			
				mViewContainer.put(type, setBackgroundView);
				return setBackgroundView;
			}
			
			case (BrickDefine.PLAY_SOUND): 
			{
				if(mViewContainer.containsKey(type))
					return mViewContainer.get(type);
			
				RelativeLayout playSoundView = (RelativeLayout) 
					inflater.inflate(R.layout.toolbox_brick_play_sound, null);			
	            mViewContainer.put(type, playSoundView);
				return playSoundView;
			}
			
			case (BrickDefine.WAIT): 
			{
				if(mViewContainer.containsKey(type))
					return mViewContainer.get(type);	
				RelativeLayout view =  (RelativeLayout)inflater.inflate(R.layout.toolbox_brick_wait, null);
	            mViewContainer.put(type, view);
	            return view;
			}
			
			case (BrickDefine.HIDE): 
			{
				if(mViewContainer.containsKey(type))
					return mViewContainer.get(type);	
				LinearLayout view =  (LinearLayout)inflater.inflate(R.layout.toolbox_brick_simple_text_view, null);
				TextView text = (TextView) view.getChildAt(0);
				text.setText(R.string.hide_main_adapter);
				mViewContainer.put(type, view);
				return view;
			}
			
			case (BrickDefine.SHOW): 
			{
				if(mViewContainer.containsKey(type))
					return mViewContainer.get(type);	
				LinearLayout view =  (LinearLayout)inflater.inflate(R.layout.toolbox_brick_simple_text_view, null);
				TextView text = (TextView) view.getChildAt(0);
				text.setText(R.string.show_main_adapter);
				mViewContainer.put(type, view);
				return view;
			}
			
			case (BrickDefine.GO_TO): 
			{
				if(mViewContainer.containsKey(type))
					return mViewContainer.get(type);	
				RelativeLayout view =  (RelativeLayout)inflater.inflate(R.layout.toolbox_brick_goto, null);
				mViewContainer.put(type, view);
				return view;
			}
			
			case (BrickDefine.SET_COSTUME): 
			{
				if(mViewContainer.containsKey(type))
					return mViewContainer.get(type);	
				RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.toolbox_brick_set_costume, null);
				mViewContainer.put(type, view);
				return view;
			}
			
			case (BrickDefine.SCALE_COSTUME): 
			{
				if(mViewContainer.containsKey(type))
					return mViewContainer.get(type);	
				RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.toolbox_brick_scale_costume, null);
				mViewContainer.put(type, view);
				return view;
			}
			
			case (BrickDefine.TOUCHED):
			{
				if(mViewContainer.containsKey(type))
					return mViewContainer.get(type);	
				LinearLayout view =  (LinearLayout)inflater.inflate(R.layout.toolbox_brick_touched, null);
				TextView text = (TextView) view.getChildAt(0);
				text.setText(R.string.touched_main_adapter);
				mViewContainer.put(type, view);
				return view;
			}
			
			default: return null;
			
		}
		
	}
	
	
	public int getCount() {
		return mContent.size();
	}

	
	public HashMap<String, String > getItem(int position) {
		return mContent.get(position);
	}

	
	public long getItemId(int position) {
		return Integer.parseInt(mContent.get(position).get(BrickDefine.BRICK_ID));
	}
}
