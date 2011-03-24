package at.tugraz.ist.catroid.constructionSite.gui.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import at.tugraz.ist.catroid.content.brick.Brick;

public class ToolBoxAdapter extends BaseAdapter {

	private Context context;
	private List<Brick> brickList;

	public ToolBoxAdapter(Context context, List<Brick> brickList) {
		this.context = context;
		this.brickList = brickList;
		//mViewContainer = new HashMap<String, View>();
	}
	
	public int getCount() {
		return brickList.size();
	}

	public Brick getItem(int position) {
		return brickList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Brick brick = brickList.get(position);
		return brick.getView(context, position, null);
	}
}
