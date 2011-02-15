package at.tugraz.ist.catroid.constructionSite.gui.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.entities.SoundInfo;
import at.tugraz.ist.catroid.io.StorageHandler;

public class SoundBrickAdapter extends BaseAdapter{
	private Context context;
    private ArrayList<SoundInfo> soundList;
  
	public SoundBrickAdapter(Context context, ArrayList<SoundInfo> soundList) {
		this.context = context;
		this.soundList = soundList;
		
	}

	public int getCount() {
		return soundList.size();
	}

	public Object getItem(int arg0) {
		return soundList.get(arg0);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(
	    Context.LAYOUT_INFLATER_SERVICE);
		
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.adapter_sound, null);
		TextView text = (TextView)view.findViewById(R.id.tvSound);
		text.setText(soundList.get(position).getTitle());
		return view;
	}
	

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}
	
	
	
}
