package at.tugraz.ist.catroid.constructionSite.gui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import at.tugraz.ist.catroid.content.script.Script;

public class ProgrammAdapter extends BaseAdapter {

	private Context context;
	private Script script;

	public ProgrammAdapter(Context context, Script script) {
		this.script = script;
		this.context = context;
	}
	
	public void setContent(Script script) {
		this.script = script;
		notifyDataSetChanged();
	}
	
	@Override
	public boolean hasStableIds() {
		return false;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		//if(convertView != null)
		//	return convertView;
		return script.getBrickList().get(position).getView(context, position, this);
	}

	public int getCount() {
		return 1;
//		return script.getBrickList().size();
	}
	
	public Object getItem(int arg0) {
		return script.getBrickList().get(arg0);
	}

	public long getItemId(int position) {
		return position;
	}

}
