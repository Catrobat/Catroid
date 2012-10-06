/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui.adapter;

import java.util.List;

import org.catrobat.catroid.common.CostumeData;
import org.catrobat.catroid.content.Sprite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import org.catrobat.catroid.R;

public class SpriteAdapter extends ArrayAdapter<Sprite> {

	private static LayoutInflater inflater = null;
	private Context context;

	public SpriteAdapter(Context context, int resource, int textViewResourceId, List<Sprite> objects) {
		super(context, resource, textViewResourceId, objects);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
	}

	private static class ViewHolder {
		private TextView text;
		private ImageView image;
		private View divider;
		private TextView scripts;
		private TextView bricks;
		private TextView costumes;
		private TextView sounds;
		//private TextView detail;
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
			holder.divider = spriteView.findViewById(R.id.sprite_divider);
			holder.scripts = (TextView) spriteView.findViewById(R.id.textView_number_of_scripts);
			holder.bricks = (TextView) spriteView.findViewById(R.id.textView_number_of_bricks);
			holder.costumes = (TextView) spriteView.findViewById(R.id.textView_number_of_costumes);
			holder.sounds = (TextView) spriteView.findViewById(R.id.textView_number_of_sounds);
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

		holder.scripts.setText(context.getResources().getString(R.string.number_of_scripts) + " "
				+ sprite.getNumberOfScripts());

		holder.bricks.setText(context.getResources().getString(R.string.number_of_bricks) + " "
				+ (sprite.getNumberOfBricks() + sprite.getNumberOfScripts()));

		holder.costumes.setText(context.getResources().getString(R.string.number_of_costumes) + " "
				+ sprite.getCostumeDataList().size());

		holder.sounds.setText(context.getResources().getString(R.string.number_of_sounds) + " "
				+ sprite.getSoundList().size());

		if (position == 0) {
			holder.divider.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 4));
			// normally a color would be enough in this case(R.color.gray)
			// but when I tested the color value, I did not get the correct color - the gray was slightly different
			// should be #808080 for gray - but always was #848284
			// with a shape gradient, I get the correct color in the testcase
			holder.divider.setBackgroundResource(R.color.divider_background);
		} else {
			holder.divider.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 2));
			holder.divider.setBackgroundResource(R.color.divider);
		}
		return spriteView;
	}
}
