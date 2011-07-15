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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.costumeData;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.dialogs.NewSpriteDialog;
import at.tugraz.ist.catroid.ui.dialogs.RenameCostumeDialog;
import at.tugraz.ist.catroid.utils.ImageEditing;
import at.tugraz.ist.catroid.utils.Utils;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class CostumeActivity extends ListActivity {
	private Sprite sprite;
	private ArrayList<costumeData> costumeData;
	private ArrayList<costumeData> currentCostume;
	private costumeData costumetoEdit;
	private CostumeAdapter c_adapter;
	private Runnable viewCostumes;
	Bitmap bm;
	int column_index;
	Intent intent = null;
	// Declare our Views, so we can access them later
	String filemanagerstring, selectedImagePath, imagePath, costumeName, absolutePath, costume, costumeFormat,
			costumeDisplayName;
	int counter, positiontoEdit, splitAt, costumeId;
	Cursor cursor;
	@XStreamOmitField
	private transient Bitmap thumbnail;
	private static final int SELECT_IMAGE = 1;

	private void initListeners() {
		Button addnewcostume = (Button) findViewById(R.id.add_costume_button);
		addnewcostume.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);

			}
		});

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_costume);

		sprite = ProjectManager.getInstance().getCurrentSprite();
		costumeData = new ArrayList<costumeData>();
		currentCostume = sprite.getCostumeList();
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
		initListeners();
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
		removeDialog(Consts.DIALOG_RENAME_COSTUME);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;

		switch (id) {
			case Consts.DIALOG_NEW_SPRITE:
				dialog = new NewSpriteDialog(this);
				break;
			case Consts.DIALOG_RENAME_COSTUME:
				dialog = new RenameCostumeDialog(this);
				break;
			default:
				dialog = null;
				break;
		}

		return dialog;
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

	//UPDATED!
	public String getPath(Uri uri) {
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		imagePath = cursor.getString(column_index);

		return cursor.getString(column_index);
	}

	public costumeData getCostumeToEdit() {
		return costumetoEdit;
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
			sprite.setCostumeList(c);

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

		public CostumeAdapter(Context context, int textViewResourceId, ArrayList<costumeData> items) {
			super(context, textViewResourceId, items);
			this.items = items;
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
				//Button editCostume = (Button) v.findViewById(R.id.edit_costume);

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
						sprite.setCostumeList(c);
						notifyDataSetChanged();
					}
				});

				ImageButton deleteCostume = (ImageButton) v.findViewById(R.id.delete_button);
				deleteCostume.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (costumeData.size() > 1) {
							items.remove(c);
							sprite.removeCostumeList(c);
							notifyDataSetChanged();
						}
					}
				});

				final EditText editName = (EditText) v.findViewById(R.id.costume_edit_name);
				final Button done = (Button) v.findViewById(R.id.rename_costume);
				done.setVisibility(View.INVISIBLE);
				if (editName != null) {
					editName.setText(c.getCostumeDisplayName());
				}

				editName.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						done.setVisibility(View.VISIBLE);
					}
				});

				done.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						String nameToEdit = ((EditText) findViewById(R.id.costume_edit_name)).getText().toString();
						if (nameToEdit.equalsIgnoreCase(c.getCostumeName())) {
							editName.setText(nameToEdit);
							notifyDataSetChanged();
							return;
						}
						if (nameToEdit != null && !nameToEdit.equalsIgnoreCase("")) {
							for (costumeData tempCostume : ProjectManager.getInstance().getCurrentSprite()
									.getCostumeList()) {
								if (tempCostume.getCostumeName().equalsIgnoreCase(nameToEdit)) {
									//Utils.displayErrorMessage(costumeActivity,costumeActivity.getString(R.string.costumename_already_exists));
									return;
								}
							}
							c.setCostumeName(nameToEdit);
							c.setCostumeDisplayName(nameToEdit);
							editName.setText(nameToEdit);
							notifyDataSetChanged();
						} else {
							//Utils.displayErrorMessage(this,this.getString(R.string.costumename_invalid));
							return;
						}
						notifyDataSetChanged();
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