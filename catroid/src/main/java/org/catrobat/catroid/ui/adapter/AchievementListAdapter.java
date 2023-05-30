/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.achievements.Achievement;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;



public class AchievementListAdapter extends ArrayAdapter<Achievement> {

	private Context adapterContext;
	private int adapterResource;
	static class ViewHolder{
		TextView Title;
		ImageView Image;
	}

	public AchievementListAdapter(@NonNull Context context, int resource, @NonNull List<Achievement> objects) {
		super(context, resource, objects);
		adapterContext = context;
		adapterResource = resource;
	}

	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		String Title = getItem(position).getTitle();
		int drawable = getItem(position).getDrawable();



		ViewHolder holder;
		if(convertView == null)
		{
			LayoutInflater inflater = LayoutInflater.from(adapterContext);
			convertView = inflater.inflate(adapterResource, parent, false);

			holder = new ViewHolder();

			holder.Title = (TextView) convertView.findViewById(R.id.achievementTitle);
			holder.Image = (ImageView) convertView.findViewById(R.id.achievementImage);


			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.Title.setText(Title);
		holder.Image.setImageResource(drawable);

		ColorMatrix colorMatrix = new ColorMatrix();
		colorMatrix.setSaturation(0);
		holder.Image.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
		if(getItem(position).isUnlocked())
		{
			holder.Image.clearColorFilter();
		}



		return convertView;
	}
}
