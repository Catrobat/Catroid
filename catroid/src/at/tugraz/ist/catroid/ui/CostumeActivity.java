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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
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
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.costumeData;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.dialogs.RenameCostumeDialog;
import at.tugraz.ist.catroid.utils.ActivityHelper;
import at.tugraz.ist.catroid.utils.ImageEditing;
import at.tugraz.ist.catroid.utils.Utils;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class CostumeActivity extends ListActivity {
	private Sprite sprite;
	private ArrayList<costumeData> costumeData;
	public costumeData selectedCostumeInfo;
	private RenameCostumeDialog renameCostumeDialog;
	private CostumeAdapter c_adapter;
	private Runnable viewCostumes;
	Intent intent = null;
	String filemanagerstring, selectedImagePath, imagePath, costumeName, absolutePath, costume, costumeFormat,
			costumeDisplayName;
	int counter, splitAt, costumeId, column_index;
	Cursor cursor;
	@XStreamOmitField
	private transient Bitmap thumbnail;
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
		costumeData = new ArrayList<costumeData>();
		ArrayList<costumeData> currentCostume = sprite.getCostumeList();
		for (int i = 0; i < currentCostume.size(); i++) {
			currentCostume.get(i).setCostumeImage(
					ImageEditing.getScaledBitmap(currentCostume.get(i).getCostumeAbsoluteImagepath(),
							Consts.THUMBNAIL_HEIGHT, Consts.THUMBNAIL_WIDTH));
		}

		if (currentCostume != null) {
			costumeData.addAll(currentCostume);
		}

		c_adapter = new CostumeAdapter(this, R.layout.activity_costumelist, costumeData);
		setListAdapter(c_adapter);
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
						absolutePath = outputFile.getName();
						thumbnail = ImageEditing.getScaledBitmap(getAbsoluteImagePath(), Consts.THUMBNAIL_HEIGHT,
								Consts.THUMBNAIL_WIDTH);
						costume = absolutePath.substring(33);
						int length = costume.length();
						for (int i = length - 1; i > 0; i--) {
							if (costume.charAt(i) == '.') {
								splitAt = i;
							}
						}
						costumeName = absolutePath.substring(33, splitAt + 33);
						costumeFormat = costume.substring(splitAt);
						costumeId = 0;
						for (int i = 0; i < sprite.getCostumeList().size(); i++) {
							if (costumeName.equals(sprite.getCostumeList().get(i).getCostumeName())) {
								if (costumeId <= sprite.getCostumeList().get(i).getCostumeId()) {
									costumeId = sprite.getCostumeList().get(i).getCostumeId();
								}
							}
						}

						costumeId = costumeId + 1;

						costumeDisplayName = costumeName.concat("_" + costumeId);

						c_adapter.notifyDataSetChanged();
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
				c_adapter.notifyDataSetChanged();
				for (int i = 0; i < costumeData.size(); i++) {
					c_adapter.add(costumeData.get(i));
				}
			}
			c_adapter.notifyDataSetChanged();
		}
	};

	private void getCostumes() {
		try {
			costumeData = new ArrayList<costumeData>();
			costumeData c = new costumeData();
			c.setCostumeName(costumeName);
			c.setCostumeImage(thumbnail);
			c.setCostumeAbsoluteImagepath(absolutePath);
			c.setCostumeFormat(costumeFormat);
			c.setCostumeDisplayName(costumeDisplayName);
			c.setCostumeId(costumeId);
			costumeData.add(c);
			sprite.addCostumeDataToCostumeList(c);

			Log.i("ARRAY", "" + costumeData.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		runOnUiThread(returnRes);
	}

	private String getAbsoluteImagePath() {
		return Consts.DEFAULT_ROOT + "/" + ProjectManager.getInstance().getCurrentProject().getName()
				+ Consts.IMAGE_DIRECTORY + "/" + absolutePath;
	}

	private class CostumeAdapter extends ArrayAdapter<costumeData> {

		private ArrayList<costumeData> items;
		private CostumeActivity activity;

		public CostumeAdapter(final CostumeActivity activity, int textViewResourceId, ArrayList<costumeData> items) {
			super(activity, textViewResourceId, items);
			this.items = items;
			this.activity = activity;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.activity_costumelist, null);
			}

			final costumeData c = items.get(position);
			if (c != null) {
				TextView costumeNameTextView = (TextView) v.findViewById(R.id.costume_edit_name);
				costumeNameTextView.setText(c.getCostumeDisplayName());

				Button copyCostume = (Button) v.findViewById(R.id.copy_costume);
				copyCostume.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						int costumeId = 0;
						absolutePath = c.getCostumeAbsoluteImagepath();
						String costumeName = c.getCostumeName();
						String costumeFormat = c.getCostumeFormat();

						for (int i = 0; i < sprite.getCostumeList().size(); i++) {
							if (costumeName.equals(sprite.getCostumeList().get(i).getCostumeName())) {
								if (costumeId <= sprite.getCostumeList().get(i).getCostumeId()) {
									costumeId = sprite.getCostumeList().get(i).getCostumeId();
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
								thumbnail = ImageEditing.getScaledBitmap(getAbsoluteImagePath(),
										Consts.THUMBNAIL_HEIGHT, Consts.THUMBNAIL_WIDTH);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						costumeData c = new costumeData();
						c.setCostumeName(costumeName);
						c.setCostumeImage(thumbnail);
						c.setCostumeAbsoluteImagepath(absolutePath);
						c.setCostumeFormat(costumeFormat);
						c.setCostumeDisplayName(costumeDisplayName);
						c.setCostumeId(costumeId);
						items.add(c);
						sprite.addCostumeDataToCostumeList(c);
						notifyDataSetChanged();
					}
				});

				Button deleteCostume = (Button) v.findViewById(R.id.delete_button);
				deleteCostume.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (costumeData.size() > 1) {
							items.remove(c);
							sprite.removeCostumeDataFromCostumeList(c);
							notifyDataSetChanged();
						}
					}
				});

				Button renameCostume = (Button) v.findViewById(R.id.rename_button);
				renameCostume.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						activity.selectedCostumeInfo = c;
						activity.showDialog(Consts.DIALOG_RENAME_COSTUME);
					}
				});

				ImageView costumeImage = (ImageView) v.findViewById(R.id.costume_image);
				if (costumeImage != null) {
					costumeImage.setImageBitmap(c.getCostumeImage());
				}
			}
			return v;
		}
	}

}