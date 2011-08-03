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

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
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
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.CostumeActivity;
import at.tugraz.ist.catroid.utils.Utils;

public class CostumeAdapter extends ArrayAdapter<CostumeData> {
	protected ArrayList<CostumeData> costumeDataItems;
	protected CostumeActivity activity;

	public CostumeAdapter(final CostumeActivity activity, int textViewResourceId, ArrayList<CostumeData> items) {
		super(activity, textViewResourceId, items);
		this.activity = activity;
		costumeDataItems = items;
	}

	@Override
	public View getView(final int position, View convView, ViewGroup parent) {

		View convertView = convView;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.activity_costume_costumelist_item, null);
		}

		final CostumeData costumeData = costumeDataItems.get(position);

		if (costumeData != null) {
			final ImageView costumeImage = (ImageView) convertView.findViewById(R.id.costume_image);
			final TextView costumeNameTextField = (TextView) convertView.findViewById(R.id.costume_name);
			final Button paintroidButton = (Button) convertView.findViewById(R.id.btn_costume_edit);
			final Button renameCostumeButton = (Button) convertView.findViewById(R.id.btn_costume_rename);
			final Button copyCostumeButton = (Button) convertView.findViewById(R.id.btn_costume_copy);
			Button deleteCostumeButton = (Button) convertView.findViewById(R.id.btn_costume_delete);
			TextView costumeResolution = (TextView) convertView.findViewById(R.id.costume_res);
			TextView costumeSize = (TextView) convertView.findViewById(R.id.costume_size);

			copyCostumeButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_copy, 0, 0);
			paintroidButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_menu_edit, 0, 0);
			deleteCostumeButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_trash, 0, 0);
			renameCostumeButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_menu_edit, 0, 0);

			costumeImage.setImageBitmap(costumeData.getThumbnailBitmap());
			costumeNameTextField.setText(costumeData.getCostumeName());

			//setting duration and costume size:
			{
				int[] resolution = costumeData.getResolution();
				costumeResolution.setText(resolution[0] + " x " + resolution[1]);

				//setting size
				float fileSizeInKB = costumeData.getSizeInKb();
				String fileSizeString;
				if (fileSizeInKB > 1024) {
					DecimalFormat decimalFormat = new DecimalFormat("#.00");
					fileSizeString = decimalFormat.format(fileSizeInKB / 1024) + " MB";
				} else {
					fileSizeString = Long.toString((long) fileSizeInKB) + " KB";
				}
				costumeSize.setText(fileSizeString);
			}

			copyCostumeButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {

					try {
						String projectName = ProjectManager.getInstance().getCurrentProject().getName();
						StorageHandler.getInstance().copyImage(projectName, costumeData.getAbsolutePath(), null);
						String imageName = costumeData.getCostumeName() + "_"
								+ activity.getString(R.string.copy_costume_addition);
						String imageFileName = costumeData.getCostumeFileName();
						activity.updateCostumeAdapter(imageName, imageFileName);
					} catch (IOException e) {
						Utils.displayErrorMessage(activity, activity.getString(R.string.error_load_image));
						e.printStackTrace();
					}

					//if we need a seperate file (I think we don't need this yet)
					//						try {
					//							String path = costumeData.getAbsolutePath();
					//							if (path.equalsIgnoreCase("")) {
					//								throw new IOException();
					//							}
					//							String projectName = ProjectManager.getInstance().getCurrentProject().getName();
					//							String timeStamp = Utils.getTimestamp();
					//							String newName = costumeData.getChecksum() + "_" + costumeData.getCostumeName() + "_"
					//									+ timeStamp + "." + costumeData.getFileExtension();
					//							File imageFile = StorageHandler.getInstance().copyImage(projectName, path, newName);
					//							String imageName = costumeData.getCostumeName() + "_copy";
					//							String imageFileName = imageFile.getName();
					//							updateCostumeAdapter(imageName, imageFileName);
					//						} catch (IOException e) {
					//							Utils.displayErrorMessage(activity, activity.getString(R.string.error_load_image));
					//						}
					//						notifyDataSetChanged();
				}
			});

			paintroidButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					activity.selectedCostumeData = costumeData;
					//TODO call paintroid
				}
			});

			renameCostumeButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					activity.selectedCostumeData = costumeData;
					activity.showDialog(Consts.DIALOG_RENAME_COSTUME);
				}
			});

			deleteCostumeButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					costumeDataItems.remove(costumeData);
					StorageHandler.getInstance().deleteFile(costumeData.getAbsolutePath());
					notifyDataSetChanged();
				}
			});

		}
		return convertView;
	}
}
