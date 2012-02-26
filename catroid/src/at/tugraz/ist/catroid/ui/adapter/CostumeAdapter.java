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

import java.io.File;
import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.ui.CostumeActivity;
import at.tugraz.ist.catroid.utils.UtilFile;

public class CostumeAdapter extends ArrayAdapter<CostumeData> {
	protected ArrayList<CostumeData> costumeDataItems;
	protected CostumeActivity activity;

	public CostumeAdapter(final CostumeActivity activity, int textViewResourceId, ArrayList<CostumeData> items) {
		super(activity, textViewResourceId, items);
		this.activity = activity;
		costumeDataItems = items;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = View.inflate(activity, R.layout.activity_costume_costumelist_item, null);
		}

		convertView.findViewById(R.id.btn_costume_copy).setTag(position);
		convertView.findViewById(R.id.btn_costume_delete).setTag(position);
		convertView.findViewById(R.id.btn_costume_edit).setTag(position);
		convertView.findViewById(R.id.costume_name).setTag(position);
		convertView.findViewById(R.id.costume_image).setTag(position);

		CostumeData costumeData = costumeDataItems.get(position);

		if (costumeData != null) {
			ImageView costumeImage = (ImageView) convertView.findViewById(R.id.costume_image);
			TextView costumeNameTextField = (TextView) convertView.findViewById(R.id.costume_name);
			TextView costumeResolution = (TextView) convertView.findViewById(R.id.costume_res);
			TextView costumeSize = (TextView) convertView.findViewById(R.id.costume_size);

			costumeImage.setImageBitmap(costumeData.getThumbnailBitmap());
			costumeNameTextField.setText(costumeData.getCostumeName());

			//setting resolution and costume size:
			{
				int[] resolution = costumeData.getResolution();
				costumeResolution.setText(resolution[0] + " x " + resolution[1]);

				//setting size
				costumeSize.setText(UtilFile.getSizeAsString(new File(costumeData.getAbsolutePath())));
			}
		}
		return convertView;
	}

}
