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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.CostumeActivity;

/**
 * @author ainulhusna
 * 
 */
public class CostumeAdapter extends ArrayAdapter<CostumeData> {

	private ArrayList<CostumeData> items;
	private CostumeActivity activity;
	String filemanagerstring, selectedImagePath, imagePath, costumeName, absolutePath, costume, costumeFormat,
			costumeDisplayName;
	private Sprite sprite;

	/**
	 * @param activity2
	 * @param activityCostumelist
	 * @param costumeData
	 * @param sprite2
	 */
	public CostumeAdapter(CostumeActivity activity, int activityCostumelist, ArrayList<CostumeData> items, Sprite sprite) {
		super(activity, activityCostumelist, items);
		this.sprite = sprite;
		this.items = items;
		this.activity = activity;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = activity.getLayoutInflater();
			v = vi.inflate(R.layout.activity_costumelist, null);
		}

		final CostumeData costumeData = items.get(position);
		if (costumeData != null) {
			TextView costumeNameTextView = (TextView) v.findViewById(R.id.costume_edit_name);
			costumeNameTextView.setText(costumeData.getCostumeName());

			Button copyCostume = (Button) v.findViewById(R.id.copy_costume);
			copyCostume.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					int costumeId = 0;
					absolutePath = costumeData.getAbsolutePath();
					String costumeName = costumeData.getCostumeName();
					String costumeFormat = costumeData.getFileExtension();

					for (int i = 0; i < sprite.getCostumeDataList().size(); i++) {
						if (costumeName.equals(sprite.getCostumeDataList().get(i).getCostumeName())) {
							if (costumeId <= sprite.getCostumeDataList().get(i).getCostumeId()) {
								costumeId = sprite.getCostumeDataList().get(i).getCostumeId();
							}
						}
					}

					costumeId = costumeId + 1;

					String costumeDisplayName = costumeName.concat("_" + costumeId);
					try {
						File copyFile = StorageHandler.getInstance().copyImage(
								ProjectManager.getInstance().getCurrentProject().getName(), absolutePath,
								costumeDisplayName + costumeFormat);

						if (copyFile != null) {
							absolutePath = copyFile.getName();
						}

						CostumeData costumeData = new CostumeData();
						costumeData.setCostumeFilename(copyFile.getName());
						costumeData.setCostumeName(costumeDisplayName);
						costumeData.setCostumeDataId(costumeId);
						items.add(costumeData);
						notifyDataSetChanged();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			});

			Button deleteCostume = (Button) v.findViewById(R.id.delete_button);
			deleteCostume.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					if (items.size() > 1) {
						items.remove(costumeData);
						//sprite.getCostumeDataList().remove(costumeData);
						notifyDataSetChanged();
					}
				}
			});

			Button renameCostume = (Button) v.findViewById(R.id.rename_button);
			renameCostume.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					activity.selectedCostumeInfo = costumeData;
					activity.showDialog(Consts.DIALOG_RENAME_COSTUME);
				}
			});

			ImageView costumeImage = (ImageView) v.findViewById(R.id.costume_image);
			if (costumeImage != null) {
				costumeImage.setImageBitmap(costumeData.getThumbnailBitmap());
			}
		}
		return v;
	}

}
