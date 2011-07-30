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
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.adapter.CustomIconContextMenu;
import at.tugraz.ist.catroid.ui.dialogs.RenameCostumeDialog;
import at.tugraz.ist.catroid.utils.ActivityHelper;
import at.tugraz.ist.catroid.utils.Utils;

public class CostumeActivity extends ListActivity {
	public CostumeData selectedCostumeData;
	private RenameCostumeDialog renameCostumeDialog;
	private ArrayList<CostumeData> costumeDataList;
	private CustomIconContextMenu iconContextMenu;

	private final int REQUEST_SELECT_IMAGE = 0;
	private static final int CONTEXT_MENU_ITEM_RENAME = 0;
	private static final int CONTEXT_MENU_ITEM_EDIT = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_costume);
		costumeDataList = ProjectManager.getInstance().getCurrentSprite().getCostumeDataList();

		setListAdapter(new CostumeAdapter2(this, R.layout.activity_costume_costumelist_item, costumeDataList));
		initCustomContextMenu();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!Utils.checkForSdCard(this)) {
			return;
		}

		costumeDataList = ProjectManager.getInstance().getCurrentSprite().getCostumeDataList();
		setListAdapter(new CostumeAdapter2(this, R.layout.activity_costume_costumelist_item, costumeDataList));
		((CostumeAdapter2) getListAdapter()).notifyDataSetChanged();

		//change actionbar:
		ScriptTabActivity scriptTabActivity = (ScriptTabActivity) getParent();
		ActivityHelper activityHelper = scriptTabActivity.activityHelper;
		if (activityHelper != null) {
			//set new functionality for actionbar add button:
			activityHelper.changeClickListener(R.id.btn_action_add_sprite, createAddCostumeClickListener());
			//set new icon for actionbar plus button:
			activityHelper.changeButtonIcon(R.id.btn_action_add_sprite, R.drawable.ic_plus_black);
		}
	}

	private View.OnClickListener createAddCostumeClickListener() {
		return new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_SELECT_IMAGE);
			}
		};
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		final Dialog dialog;
		switch (id) {
			case Consts.DIALOG_RENAME_SPRITE: //TODO: trololol not sprite .. sucker
				if (selectedCostumeData == null) {
					dialog = null;
				} else {
					renameCostumeDialog = new RenameCostumeDialog(this);
					dialog = renameCostumeDialog.createDialog(selectedCostumeData);
				}
				break;
			case Consts.DIALOG_CONTEXT_MENU:
				if (iconContextMenu == null || selectedCostumeData == null) {
					dialog = null;
				} else {
					dialog = iconContextMenu.createMenu(selectedCostumeData.getCostumeName());
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

	private void updateCostumeAdapter(String name, String fileName) {
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(fileName);
		costumeData.setCostumeName(name);
		costumeDataList.add(costumeData);
		((CostumeAdapter2) getListAdapter()).notifyDataSetChanged();

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

	public void handlePositiveButtonRenameCostume(View v) {
		renameCostumeDialog.handleOkButton();
	}

	public void handleNegativeButtonRenameCostume(View v) {
		renameCostumeDialog.renameDialog.cancel();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//when new sound title is selected and ready to be added to the catroid project
		if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SELECT_IMAGE) {
			String originalImagePath = "";
			//get path of image --------------------------
			{
				Uri imageUri = data.getData();
				String[] projection = { MediaStore.MediaColumns.DATA };
				Cursor cursor = managedQuery(imageUri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
				cursor.moveToFirst();
				originalImagePath = cursor.getString(column_index);
			}
			//-----------------------------------------------------

			File oldFile = new File(originalImagePath);

			//copy image to catroid:
			try {
				if (originalImagePath.equalsIgnoreCase("")) {
					throw new IOException();
				}
				String projectName = ProjectManager.getInstance().getCurrentProject().getName();
				File imageFile;
				String imageName;
				imageFile = StorageHandler.getInstance().copyImage(projectName, originalImagePath, null);
				imageName = oldFile.getName().substring(0, oldFile.getName().length() - 4);

				String imageFileName = imageFile.getName();
				updateCostumeAdapter(imageName, imageFileName);
			} catch (IOException e) {
				Utils.displayErrorMessage(this, this.getString(R.string.error_load_image));
			}
		}
	}

	private void initCustomContextMenu() {
		Resources resources = getResources();
		iconContextMenu = new CustomIconContextMenu(this, Consts.DIALOG_CONTEXT_MENU);
		iconContextMenu.addItem(resources, this.getString(R.string.rename), R.drawable.ic_context_rename,
				CONTEXT_MENU_ITEM_RENAME);
		iconContextMenu.addItem(resources, this.getString(R.string.delete), R.drawable.ic_context_delete,
				CONTEXT_MENU_ITEM_EDIT);

		iconContextMenu.setOnClickListener(new CustomIconContextMenu.IconContextMenuOnClickListener() {
			public void onClick(int menuId) {
				switch (menuId) {
					case CONTEXT_MENU_ITEM_RENAME:
						CostumeActivity.this.showDialog(Consts.DIALOG_RENAME_SPRITE);
						break;
					case CONTEXT_MENU_ITEM_EDIT:
						break;
				}
			}
		});
	}

	private class CostumeAdapter2 extends ArrayAdapter<CostumeData> {
		protected ArrayList<CostumeData> costumeDataItems;
		protected CostumeActivity activity;

		public CostumeAdapter2(final CostumeActivity activity, int textViewResourceId, ArrayList<CostumeData> items) {
			super(activity, textViewResourceId, items);
			this.activity = activity;
			costumeDataItems = items;
		}

		@Override
		public View getView(final int position, View convView, ViewGroup parent) {

			View convertView = convView;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.activity_costume_costumelist_item, null);
			}

			final CostumeData costumeData = costumeDataItems.get(position);

			if (costumeData != null) {
				final ImageView costumeImage = (ImageView) convertView.findViewById(R.id.costume_image);
				final TextView costumeNameTextField = (TextView) convertView.findViewById(R.id.costume_name);
				final Button editCostumeButton = (Button) convertView.findViewById(R.id.btn_costume_edit);
				final Button copyCostumeButton = (Button) convertView.findViewById(R.id.btn_costume_copy);
				Button deleteCostumeButton = (Button) convertView.findViewById(R.id.btn_costume_delete);

				copyCostumeButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_copy, 0, 0);
				editCostumeButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_menu_edit, 0, 0);
				deleteCostumeButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_trash, 0, 0);

				costumeImage.setImageBitmap(costumeData.getThumbnailBitmap());
				costumeNameTextField.setText(costumeData.getCostumeName());

				copyCostumeButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {

						notifyDataSetChanged();
					}
				});

				editCostumeButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						activity.selectedCostumeData = costumeData;
						activity.removeDialog(Consts.DIALOG_CONTEXT_MENU);
						activity.showDialog(Consts.DIALOG_CONTEXT_MENU);
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
}
