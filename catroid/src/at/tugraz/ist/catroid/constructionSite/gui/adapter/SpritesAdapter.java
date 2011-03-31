/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.constructionSite.gui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;

public class SpritesAdapter extends BaseAdapter{
    private Context mCtx;
    public ArrayList<String> mList;
  
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
		return view;
		}
	
	
	
}
