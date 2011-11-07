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
package at.tugraz.ist.catroid.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ListView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.adapter.CostumeAdapter;
import at.tugraz.ist.catroid.utils.ActivityHelper;
import at.tugraz.ist.catroid.utils.ImageEditing;
import at.tugraz.ist.catroid.utils.Utils;

public class CostumeActivity extends ListActivity {
	private ArrayList<CostumeData> costumeDataList;

	public static final int REQUEST_SELECT_IMAGE = 0;
	public static final int REQUEST_PAINTROID_EDIT_IMAGE = 1;
	public static final int REQUEST_PAINTROID_NEW_IMAGE = 2;
	public static final int REQUEST_CAM_IMAGE = 3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_costume);
		costumeDataList = ProjectManager.getInstance().getCurrentSprite().getCostumeDataList();

		setListAdapter(new CostumeAdapter(this, R.layout.activity_costume_costumelist_item, costumeDataList));
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!Utils.checkForSdCard(this)) {
			return;
		}

		reloadAdapter();

		//change actionbar:
		ScriptTabActivity scriptTabActivity = (ScriptTabActivity) getParent();
		ActivityHelper activityHelper = scriptTabActivity.activityHelper;
		if (activityHelper != null) {
			//set new functionality for actionbar add button:
			activityHelper.changeClickListener(R.id.btn_action_add_sprite, createAddCostumeClickListener());
			//set new icon for actionbar plus button:
			int addButtonIcon;
			Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
			if (ProjectManager.getInstance().getCurrentProject().getSpriteList().indexOf(currentSprite) == 0) {
				addButtonIcon = R.drawable.ic_background;
			} else {
				addButtonIcon = R.drawable.ic_shirt;
			}
			activityHelper.changeButtonIcon(R.id.btn_action_add_sprite, addButtonIcon);
		}

	}

	private View.OnClickListener createAddCostumeClickListener() {
		return new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

				Bundle bundleForPaintroid = new Bundle();
				bundleForPaintroid.putString(CostumeActivity.this.getString(R.string.extra_picture_path_paintroid), "");

				intent.setType("image/*");
				intent.putExtras(bundleForPaintroid);
				Intent chooser = Intent.createChooser(intent, getString(R.string.select_image));
				startActivityForResult(chooser, REQUEST_SELECT_IMAGE);
			}
		};
	}

	@Override
	protected void onPause() {
		super.onPause();
		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject();
		}
	}

	public void updateCostumeAdapter(String name, String fileName) {
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(fileName);
		costumeData.setCostumeName(name);
		costumeDataList.add(costumeData);
		((CostumeAdapter) getListAdapter()).notifyDataSetChanged();

		//scroll down the list to the new item:
		{
			final ListView listView = getListView();
			listView.post(new Runnable() {
				public void run() {
					listView.setSelection(listView.getCount() - 1);
				}
			});
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { //TODO refactor this mess! (please)
		super.onActivityResult(requestCode, resultCode, data);
		//when new sound title is selected and ready to be added to the catroid project
		if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SELECT_IMAGE) {
			String originalImagePath = "";
			//get path of image - will work for most applications
			Bundle bundle = data.getExtras();
			if (bundle != null) {
				originalImagePath = bundle.getString(this.getString(R.string.extra_picture_path_paintroid));
			}
			if (originalImagePath == null || originalImagePath.equalsIgnoreCase("")) {
				Uri imageUri = data.getData();

				String[] projection = { MediaStore.MediaColumns.DATA };
				Cursor cursor = managedQuery(imageUri, projection, null, null, null);
				if (cursor == null) {
					originalImagePath = imageUri.getPath();
				} else {
					int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
					cursor.moveToFirst();
					try {
						originalImagePath = cursor.getString(column_index);
					} catch (CursorIndexOutOfBoundsException e) {
						Utils.displayErrorMessage(this, this.getString(R.string.error_load_image));
						return;
					}
				}

				if (cursor == null && originalImagePath.equalsIgnoreCase("")) {
					Utils.displayErrorMessage(this, this.getString(R.string.error_load_image));
					return;
				}

			}
			//-----------------------------------------------------

			//if image is broken abort
			{
				int[] imageDimensions = new int[2];
				imageDimensions = ImageEditing.getImageDimensions(originalImagePath);
				if (imageDimensions[0] < 0 || imageDimensions[1] < 0) {
					Utils.displayErrorMessage(this, this.getString(R.string.error_load_image));
					return;
				}
			}

			File oldFile = new File(originalImagePath);

			//copy image to catroid:
			try {
				if (originalImagePath.equalsIgnoreCase("")) {
					throw new IOException();
				}
				String projectName = ProjectManager.getInstance().getCurrentProject().getName();
				String imageName;
				File imageFile = StorageHandler.getInstance().copyImage(projectName, originalImagePath, null);
				int extensionDotIndex = oldFile.getName().lastIndexOf('.');
				if (extensionDotIndex <= 0) {
					imageName = oldFile.getName().substring(0, extensionDotIndex - 1);
				} else {
					imageName = oldFile.getName();
				}

				String imageFileName = imageFile.getName();
				//reloadAdapter();
				updateCostumeAdapter(imageName, imageFileName);
			} catch (IOException e) {
				Utils.displayErrorMessage(this, this.getString(R.string.error_load_image));
			}
		}
		if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_PAINTROID_EDIT_IMAGE) {
			Bundle bundle = data.getExtras();
			String pathOfPaintroidImage = bundle.getString(this.getString(R.string.extra_picture_path_paintroid));

			//if image is broken abort
			{
				int[] imageDimensions = new int[2];
				imageDimensions = ImageEditing.getImageDimensions(pathOfPaintroidImage);
				if (imageDimensions[0] < 0 || imageDimensions[1] < 0) {
					return;
				}
			}

			ScriptTabActivity scriptTabActivity = (ScriptTabActivity) getParent();
			CostumeData selectedCostumeData = scriptTabActivity.selectedCostumeData;
			String actualChecksum = Utils.md5Checksum(new File(pathOfPaintroidImage));

			//if costume changed --> saving new image with new checksum and changing costumeData
			if (!selectedCostumeData.getChecksum().equalsIgnoreCase(actualChecksum)) {
				String oldFileName = selectedCostumeData.getCostumeFileName();
				String newFileName = oldFileName.substring(33, oldFileName.length()); //TODO: test this
				String projectName = ProjectManager.getInstance().getCurrentProject().getName();
				try {
					File newCostumeFile = StorageHandler.getInstance().copyImage(projectName, pathOfPaintroidImage,
							newFileName);
					File tempPicFileInPaintroid = new File(pathOfPaintroidImage);
					tempPicFileInPaintroid.delete(); //delete temp file in paintroid
					StorageHandler.getInstance().deleteFile(selectedCostumeData.getAbsolutePath()); //reduce usage in container or delete it
					selectedCostumeData.setCostumeFilename(newCostumeFile.getName());
					selectedCostumeData.resetThumbnailBitmap();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void reloadAdapter() {
		costumeDataList = ProjectManager.getInstance().getCurrentSprite().getCostumeDataList();
		setListAdapter(new CostumeAdapter(this, R.layout.activity_costume_costumelist_item, costumeDataList));
		((CostumeAdapter) getListAdapter()).notifyDataSetChanged();
	}
}
