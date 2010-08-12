package at.tugraz.ist.s2a.constructionSite.gui.adapter;

import java.util.ArrayList;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import at.tugraz.ist.s2a.R;
import at.tugraz.ist.s2a.R.layout;
import at.tugraz.ist.s2a.constructionSite.content.ContentManager;

public class SpritesAdapter extends BaseAdapter{
    private Context mCtx;
    public ArrayList<String> mList;
    private ContentManager mContentManager;
    private Dialog mDialog;
  
	public SpritesAdapter(Context context,
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
			return 0;//TODO wenn sprites ids haben hier zurueckgeben
	}

	public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mCtx.getSystemService(
	    Context.LAYOUT_INFLATER_SERVICE);
		
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.sprites_list_element, null);
		view.setTag(mList.get(position));
		TextView text = (TextView)view.getChildAt(0);
		text.setText(mList.get(position));
		//text.setTextColor(Color.BLUE);
		//view.setBackgroundColor(Color.argb(255, 255, 255, 139));		
	    view.setOnClickListener(new View.OnClickListener() {				
				
				public void onClick(View v) {
					mContentManager.switchSprite(((LinearLayout)v).getTag().toString());
					mDialog.dismiss();
				}
			});
		return view;
		}
	
	
	public void setContentManager(ContentManager contentManager) {
		mContentManager = contentManager;
		
	}
	
	public void setDialog(Dialog dialog){
		mDialog = dialog;	
	}


}
