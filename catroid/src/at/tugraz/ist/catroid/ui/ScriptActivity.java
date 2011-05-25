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

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.adapter.BrickAdapter;
import at.tugraz.ist.catroid.ui.dialogs.AddBrickDialog;
import at.tugraz.ist.catroid.ui.dragndrop.DragNDropListView;
import at.tugraz.ist.catroid.utils.ActivityHelper;
import at.tugraz.ist.catroid.utils.Utils;

public class ScriptActivity extends Activity implements OnDismissListener, OnCancelListener {
	private BrickAdapter adapter;
	private DragNDropListView listView;
	private Sprite sprite;
	private Script scriptToEdit;
	private ActivityHelper activityHelper = new ActivityHelper(this);

	private void initListeners() {
		sprite = ProjectManager.getInstance().getCurrentSprite();
		listView = (DragNDropListView) findViewById(R.id.brick_list_view);
		adapter = new BrickAdapter(this, ProjectManager.getInstance().getCurrentSprite(), listView);
		if (adapter.getGroupCount() > 0) {
			ProjectManager.getInstance().setCurrentScript(adapter.getGroup(adapter.getGroupCount() - 1));
		}

		listView.setTrashView((ImageView) findViewById(R.id.trash));
		listView.setOnCreateContextMenuListener(this);
		listView.setOnDropListener(adapter);
		listView.setOnRemoveListener(adapter);
		listView.setAdapter(adapter);
		// Sets scroll behavior. --> Find a better way to do it.
		//listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		listView.setGroupIndicator(null);
		listView.setOnGroupClickListener(adapter);
		registerForContextMenu(listView);

		//		Button mainMenuButton = (Button) findViewById(R.id.main_menu_button);
		//		mainMenuButton.setOnClickListener(new View.OnClickListener() {
		//			public void onClick(View v) {
		//				Intent intent = new Intent(ScriptActivity.this, MainMenuActivity.class);
		//				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		//				startActivity(intent);
		//			}
		//		});

		//		Button toStageButton = (Button) findViewById(R.id.toStageButton);
		//		toStageButton.setOnClickListener(new View.OnClickListener() {
		//			public void onClick(View v) {
		//				Intent intent = new Intent(ScriptActivity.this, StageActivity.class);
		//				startActivity(intent);
		//			}
		//		});

		Button addBrickButton = (Button) findViewById(R.id.add_brick_button);
		addBrickButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(Consts.DIALOG_ADD_BRICK);
			}
		});

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_script);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		activityHelper.setupActionBar(false, ProjectManager.getInstance().getCurrentSprite().getName());

		activityHelper.addActionButton(R.id.btn_action_play, R.drawable.ic_play_black, new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ScriptActivity.this, StageActivity.class);
				startActivity(intent);
			}
		}, false);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;

		switch (id) {
			case Consts.DIALOG_ADD_BRICK:
				dialog = new AddBrickDialog(this);
				dialog.setOnDismissListener(this);
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
	protected void onStart() {
		super.onStart();
		initListeners();
		if (adapter.getGroupCount() > 0) {
			listView.expandGroup(adapter.getGroupCount() - 1);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!Utils.checkForSdCard(this)) {
			return;
		}
	}

	public void onDismiss(DialogInterface dialog) {
		for (int i = 0; i < adapter.getGroupCount() - 1; ++i) {
			listView.collapseGroup(i);
		}

		adapter.notifyDataSetChanged();
		if (adapter.getGroupCount() > 0) {
			listView.expandGroup(adapter.getGroupCount() - 1);
		}
	}

	public void onCancel(DialogInterface arg0) {
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		/*
		 * This is used for getting the results of the gallery intent when the
		 * user selected an image. requestCode holds the ID / position of the
		 * brick that issued the request. If and when we have different kinds of
		 * intents we need to find a better way.
		 */

		if (resultCode == RESULT_OK) {
			SetCostumeBrick affectedBrick = (SetCostumeBrick) adapter
					.getChild(adapter.getGroupCount() - 1, requestCode);
			if (affectedBrick != null) {
				Uri selectedImageUri = data.getData();
				String selectedImagePath = getPathFromContentUri(selectedImageUri);
				if (selectedImagePath == null) {
					Utils.displayErrorMessage(this, getString(R.string.error_load_image));
					return;
				}
				try {
					if (affectedBrick.getImagePath() != null) {
						StorageHandler.getInstance().deleteFile(affectedBrick.getImagePath());
					}
					File outputFile = StorageHandler.getInstance().copyImage(
							ProjectManager.getInstance().getCurrentProject().getName(), selectedImagePath);
					if (outputFile != null) {
						affectedBrick.setCostume(outputFile.getName());
						adapter.notifyDataSetChanged();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String getPathFromContentUri(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(columnIndex);
		} else {
			return null;
		}
	}

	public BrickAdapter getAdapter() {
		return adapter;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		if (view.getId() == R.id.brick_list_view) {
			ExpandableListView.ExpandableListContextMenuInfo info =
					(ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
			menu.setHeaderTitle("Script Menu");

			if (ExpandableListView.getPackedPositionType(info.packedPosition) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
				return;
			}

			int position = ExpandableListView.getPackedPositionGroup(info.packedPosition);
			scriptToEdit = adapter.getGroup(position);

			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.script_menu, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.script_menu_delete: {
				sprite.getScriptList().remove(scriptToEdit);
				if (sprite.getScriptList().isEmpty()) {
					ProjectManager.getInstance().setCurrentScript(null);
					adapter.notifyDataSetChanged();
					return true;
				}
				int lastScriptIndex = sprite.getScriptList().size() - 1;
				Script lastScript = sprite.getScriptList().get(lastScriptIndex);
				ProjectManager.getInstance().setCurrentScript(lastScript);
				adapter.notifyDataSetChanged();
				listView.expandGroup(adapter.getGroupCount() - 1);
			}
		}
		return true;
	}
}
