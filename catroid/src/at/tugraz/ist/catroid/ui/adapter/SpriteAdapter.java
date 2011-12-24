/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
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
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Sprite;

public class SpriteAdapter extends ArrayAdapter<Sprite> {

	private static LayoutInflater inflater = null;
	boolean first = true;

	public SpriteAdapter(Context context, int resource, int textViewResourceId, List<Sprite> objects) {
		super(context, resource, textViewResourceId, objects);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public static class ViewHolder {
		public TextView text;
		public ImageView image;
		//public TextView detail;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View spriteView = convertView;
		ViewHolder holder;
		if (convertView == null) {
			spriteView = inflater.inflate(R.layout.activity_project_spritelist_item, null);
			holder = new ViewHolder();
			holder.text = (TextView) spriteView.findViewById(R.id.sprite_title);
			//holder.detail = (TextView) spriteView.findViewById(R.id.sprite_detail);
			holder.image = (ImageView) spriteView.findViewById(R.id.sprite_img);
			spriteView.setTag(holder);
		} else {
			holder = (ViewHolder) spriteView.getTag();
		}

		//------------------------------------------------------------
		Sprite sprite = getItem(position);
		CostumeData firstCostumeData = null;
		if (sprite.getCostumeDataList().size() > 0) {
			firstCostumeData = sprite.getCostumeDataList().get(0);
		}
		//------------------------------------------------------------

		holder.text.setText(sprite.getName());
		if (firstCostumeData == null) {
			holder.image.setImageBitmap(null);
		} else {
			holder.image.setImageBitmap(firstCostumeData.getThumbnailBitmap());
		}
		return spriteView;
	}
}
