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

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.utils.UtilFile;

public class CostumeAdapter extends ArrayAdapter<CostumeData> {
	
	protected ArrayList<CostumeData> costumeDataItems;
	protected Context context;

	private OnCostumeEditListener onCostumeEditListener;
	
	public CostumeAdapter(final Context context, int textViewResourceId, ArrayList<CostumeData> items) {
		super(context, textViewResourceId, items);
		this.context = context;
		costumeDataItems = items;
	}
	
	public void setOnCostumeEditListener(OnCostumeEditListener listener) {
		onCostumeEditListener = listener;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = View.inflate(context, R.layout.activity_costume_costumelist_item, null);
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
			Button costumeEditButton = (Button) convertView.findViewById(R.id.btn_costume_edit);
			Button costumeCopyButton = (Button) convertView.findViewById(R.id.btn_costume_copy);
			Button costumeDeleteButton = (Button) convertView.findViewById(R.id.btn_costume_delete);

			costumeImage.setImageBitmap(costumeData.getThumbnailBitmap());
			costumeNameTextField.setText(costumeData.getCostumeName());

			//setting resolution and costume size:
			{
				int[] resolution = costumeData.getResolution();
				costumeResolution.setText(resolution[0] + " x " + resolution[1]);

				//setting size
				if (costumeData.getAbsolutePath() != null) {
					costumeSize.setText(UtilFile.getSizeAsString(new File(costumeData.getAbsolutePath())));
				}
			}
			
			costumeImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onCostumeEditListener != null) {
						onCostumeEditListener.onCostumeEdit(v);
					}
				}
			});
			
			costumeNameTextField.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onCostumeEditListener != null) {
						onCostumeEditListener.onCostumeRename(v);
					}
				}
			});
			
			costumeEditButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onCostumeEditListener != null) {
						onCostumeEditListener.onCostumeEdit(v);
					}
				}
			});
			
			costumeCopyButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onCostumeEditListener != null) {
						onCostumeEditListener.onCostumeCopy(v);
					}
				}
			});
			
			costumeDeleteButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onCostumeEditListener != null) {
						onCostumeEditListener.onCostumeDelete(v);
					}
				}
			});
		}
		
		return convertView;
	}
	
	public interface OnCostumeEditListener {
		
		public void onCostumeEdit(View v);
		public void onCostumeRename(View v);
		public void onCostumeDelete(View v);
		public void onCostumeCopy(View v);
	}
}
