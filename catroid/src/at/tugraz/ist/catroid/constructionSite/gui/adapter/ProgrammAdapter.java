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
