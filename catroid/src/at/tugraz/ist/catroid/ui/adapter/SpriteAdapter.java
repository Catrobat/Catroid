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
package at.tugraz.ist.catroid.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.utils.ImageEditing;

public class SpriteAdapter extends ArrayAdapter<Sprite> {

	private LayoutInflater inflater;

	public SpriteAdapter(Context context, int resource, int textViewResourceId, List<Sprite> objects,
			LayoutInflater inflater) {
		super(context, resource, textViewResourceId, objects);
		this.inflater = inflater;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		TextView spriteTitle = null;
		ImageView imageView = null;
		Sprite sprite = getItem(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		spriteTitle = holder.gettitle();
		spriteTitle.setText(sprite.getName());
		String imagepath = null;
		//this will change after the refactoring of the scriptactivity
		for (Script script : sprite.getScriptList()) {
			for (Brick brick : script.getBrickList()) {
				if (brick instanceof SetCostumeBrick) {
					imagepath = ((SetCostumeBrick) brick).getImagePath();
					break;
				}
			}
			if (imagepath != null) {
				break;
			}
		}
		imageView = holder.getImage();
		if (imagepath == null) {
			imageView.setImageResource(R.drawable.sadfrog);
		} else {
			imageView.setImageBitmap(ImageEditing.getScaledBitmap(imagepath, Consts.THUMBNAIL_HEIGHT,
					Consts.THUMBNAIL_WIDTH));
		}
		return convertView;
	}

	private class ViewHolder {
		private View row;
		private TextView title = null;
		private ImageView i11 = null;

		public ViewHolder(View row) {
			this.row = row;
		}

		public TextView gettitle() {
			if (null == title) {
				title = (TextView) row.findViewById(R.id.title);
			}
			return title;
		}

		public ImageView getImage() {
			if (null == i11) {
				i11 = (ImageView) row.findViewById(R.id.img);
			}
			return i11;
		}
	}
}