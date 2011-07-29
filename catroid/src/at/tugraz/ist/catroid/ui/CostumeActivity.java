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

package at.tugraz.ist.catroid.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.adapter.CostumeAdapter;
import at.tugraz.ist.catroid.ui.dialogs.RenameCostumeDialog;
import at.tugraz.ist.catroid.utils.ActivityHelper;
import at.tugraz.ist.catroid.utils.Utils;

public class CostumeActivity extends ListActivity {
	private Sprite sprite;
	private ArrayList<CostumeData> costumeData;
	public CostumeData selectedCostumeInfo;
	private RenameCostumeDialog renameCostumeDialog;
	private Runnable viewCostumes;
	Intent intent = null;
	String filemanagerstring, selectedImagePath, imagePath, costumeName, absolutePath, costume, costumeFormat,
			costumeDisplayName;
	int counter, splitAt, costumeId, column_index;
	Cursor cursor;
	private static final int SELECT_IMAGE = 1;

	private View.OnClickListener createAddCostumeClickListener() {
		return new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
			}
		};
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_costume);

		sprite = ProjectManager.getInstance().getCurrentSprite();
		costumeData = sprite.getCostumeDataList();
		setListAdapter(new CostumeAdapter(this, R.layout.activity_costumelist, costumeData, sprite));
		getListView().setTextFilterEnabled(true);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		final Dialog dialog;
		switch (id) {
			case Consts.DIALOG_RENAME_COSTUME:
				if (selectedCostumeInfo == null) {
					dialog = null;
				} else {
					renameCostumeDialog = new RenameCostumeDialog(this);
					dialog = renameCostumeDialog.createDialog(selectedCostumeInfo);
				}
				break;
			default:
				dialog = null;
				break;
		}
		return dialog;
	}

	@Override
	protected void onPause() {
		super.onPause();
		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject(this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!Utils.checkForSdCard(this)) {
			return;
		}

		ScriptTabActivity scriptTabActivity = (ScriptTabActivity) getParent();
		ActivityHelper activityHelper = scriptTabActivity.activityHelper;
		if (activityHelper != null) {
			activityHelper.changeClickListener(R.id.btn_action_add_sprite, createAddCostumeClickListener());
			scriptTabActivity.activityHelper.changeButtonIcon(R.id.btn_action_add_sprite, R.drawable.ic_plus_black);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SELECT_IMAGE) {
				Uri selectedImageUri = data.getData();

				//OI FILE Manager
				filemanagerstring = selectedImageUri.getPath();

				//MEDIA GALLERY
				selectedImagePath = getPath(selectedImageUri);

				if (selectedImagePath == null) {
					Utils.displayErrorMessage(this, getString(R.string.error_load_image));
					return;
				}
				try {
					File outputFile = StorageHandler.getInstance().copyImage(
							ProjectManager.getInstance().getCurrentProject().getName(), selectedImagePath, null);
					if (outputFile != null) {

						absolutePath = outputFile.getAbsolutePath();
						costume = absolutePath.substring(33);
						int length = costume.length();
						for (int i = length - 1; i > 0; i--) {
							if (costume.charAt(i) == '.') {
								splitAt = i;
							}
						}
						costumeName = outputFile.getName();
						costumeFormat = costume.substring(splitAt);
						costumeId = 0;
						for (int i = 0; i < sprite.getCostumeDataList().size(); i++) {
							if (costumeName.equals(sprite.getCostumeDataList().get(i).getCostumeName())) {
								if (costumeId <= sprite.getCostumeDataList().get(i).getCostumeId()) {
									costumeId = sprite.getCostumeDataList().get(i).getCostumeId();
								}
							}
						}

						costumeId = costumeId + 1;

						costumeDisplayName = costumeName.substring(33, costumeName.length() - 4);

						((CostumeAdapter) getListAdapter()).notifyDataSetChanged();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				viewCostumes = new Runnable() {
					public void run() {
						getCostumes();
					}
				};

				Thread thread = new Thread(null, viewCostumes, "MagentoBackground");
				thread.start();

			}
		}
	}

	public void handlePositiveButtonRenameCostume(View v) {
		renameCostumeDialog.handleOkButton();
	}

	public void handleNegativeButtonRenameCostume(View v) {
		renameCostumeDialog.renameDialog.cancel();
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		imagePath = cursor.getString(column_index);

		return cursor.getString(column_index);
	}

	private Runnable returnRes = new Runnable() {

		public void run() {
			if (costumeData != null && costumeData.size() > 0) {
				((CostumeAdapter) getListAdapter()).notifyDataSetChanged();
				for (int i = 0; i < costumeData.size(); i++) {
					((CostumeAdapter) getListAdapter()).add(costumeData.get(i));
				}
			}
			((CostumeAdapter) getListAdapter()).notifyDataSetChanged();
		}
	};

	private void getCostumes() {
		try {

			this.costumeData = new ArrayList<CostumeData>();
			CostumeData newCostumeData = new CostumeData();
			newCostumeData.setCostumeFilename(costumeName);
			newCostumeData.setCostumeName(costumeDisplayName);
			newCostumeData.setCostumeDataId(costumeId);
			costumeData.add(newCostumeData);
			//sprite.getCostumeDataList().add(newCostumeData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		runOnUiThread(returnRes);
	}
}