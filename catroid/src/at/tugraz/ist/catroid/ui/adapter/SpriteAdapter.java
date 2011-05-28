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

	private Context context;
	private static LayoutInflater inflater = null;

	public SpriteAdapter(Context context2, int resource, int textViewResourceId, List<Sprite> objects) {
		super(context2, resource, textViewResourceId, objects);
		this.context = context2;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public static class ViewHolder {
		public TextView text;
		public ImageView image;
		public TextView detail;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View spriteView = convertView;
		ViewHolder holder;
		if (convertView == null) {
			spriteView = inflater.inflate(R.layout.sprite_list, null);
			holder = new ViewHolder();
			holder.text = (TextView) spriteView.findViewById(R.id.sprite_title);
			holder.detail = (TextView) spriteView.findViewById(R.id.sprite_detail);
			holder.image = (ImageView) spriteView.findViewById(R.id.sprite_img);
			spriteView.setTag(holder);
		} else {
			holder = (ViewHolder) spriteView.getTag();
		}

		//------------------------------------------------------------
		//this will change after the refactoring of the scriptactivity
		Sprite sprite = getItem(position);
		String imagepath = null;
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
		//------------------------------------------------------------

		holder.text.setText(sprite.getName());
		holder.detail.setText("details");
		if (imagepath == null) {
			holder.image.setImageResource(R.drawable.sadfrog);
		} else { //it would be more efficient to use the thumb from setCostumeBrick - but this will change in the near future so I didn't implement it
			//TODO make this more efficient after the refact of ScriptActivity
			holder.image.setImageBitmap(ImageEditing.getScaledBitmap(imagepath, Consts.THUMBNAIL_HEIGHT,
					Consts.THUMBNAIL_WIDTH));
		}
		return spriteView;
	}
}