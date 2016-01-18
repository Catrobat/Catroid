/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.drone.DroneServiceWrapper;
import org.catrobat.catroid.drone.DroneStageActivity;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.adapter.SpriteAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.dialogs.NewSpriteDialog;
import org.catrobat.catroid.ui.fragment.SpritesListFragment;
import org.catrobat.catroid.utils.Utils;

import java.util.concurrent.locks.Lock;

public class ProjectActivity extends BaseActivity {

	private SpritesListFragment spritesListFragment;
	private Lock viewSwitchLock = new ViewSwitchLock();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);

		if (getIntent() != null && getIntent().hasExtra(Constants.PROJECT_OPENED_FROM_PROJECTS_LIST)) {
			setReturnToProjectsList(true);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		String programName = getString(R.string.app_name);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			programName = bundle.getString(Constants.PROJECTNAME_TO_LOAD);
		} else {
			Project project = ProjectManager.getInstance().getCurrentProject();
			if (project != null) {
				programName = project.getName();
			}
		}

		final ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		setTitleActionBar(programName);

		spritesListFragment = (SpritesListFragment) getFragmentManager().findFragmentById(
				R.id.fragment_container);
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		updateFragment(fragmentTransaction);
		fragmentTransaction.commit();

		SettingsActivity.setLegoMindstormsNXTSensorChooserEnabled(this, true);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (spritesListFragment != null && !spritesListFragment.isLoading) {
			handleShowDetails(spritesListFragment.getShowDetails(), menu.findItem(R.id.show_details));
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (spritesListFragment != null) {
			getMenuInflater().inflate(R.menu.menu_current_project, menu);
			menu.findItem(R.id.unpacking).setVisible(false);
			menu.findItem(R.id.unpacking_keep).setVisible(false);
			menu.findItem(R.id.backpack).setVisible(true);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.show_details:
				handleShowDetails(!spritesListFragment.getShowDetails(), item);
				break;

			case R.id.backpack:
				showBackPackChooser();
				break;

			case R.id.copy:
				spritesListFragment.startCopyActionMode();
				break;

			case R.id.cut:
				break;

			case R.id.insert_below:
				break;

			case R.id.move:
				break;

			case R.id.rename:
				spritesListFragment.startRenameActionMode();
				break;

			case R.id.delete:
				spritesListFragment.startDeleteActionMode();
				break;

			case R.id.upload:
				ProjectManager.getInstance().uploadProject(Utils.getCurrentProjectName(this), this);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showBackPackChooser() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		CharSequence[] items;
		int numberOfItemsInBackpack = BackPackListManager.getInstance().getBackPackedSprites().size();

		if (numberOfItemsInBackpack == 0) {
			spritesListFragment.startBackPackActionMode();
		} else {

			items = new CharSequence[] { getString(R.string.packing), getString(R.string.unpacking) };
			builder.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == 0) {
						spritesListFragment.startBackPackActionMode();
					} else if (which == 1) {
						openBackPack();
					}
					dialog.dismiss();
				}
			});
			builder.setTitle(R.string.backpack_title);
			builder.setCancelable(true);
			builder.show();
		}
	}

	private void openBackPack() {
		Intent intent = new Intent(this, BackPackActivity.class);
		intent.putExtra(BackPackActivity.EXTRA_FRAGMENT_POSITION, BackPackActivity.FRAGMENT_BACKPACK_SPRITES);
		startActivity(intent);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PreStageActivity.REQUEST_RESOURCES_INIT && resultCode == RESULT_OK) {

			Intent intent = null;
			if (data != null) {
				if (DroneServiceWrapper.checkARDroneAvailability()) {
					intent = new Intent(ProjectActivity.this, DroneStageActivity.class);
				} else {
					intent = new Intent(ProjectActivity.this, StageActivity.class);
				}
				startActivity(intent);
			}
		}
		if (requestCode == StageActivity.STAGE_ACTIVITY_FINISH) {
			SensorHandler.stopSensorListeners();
			FaceDetectionHandler.stopFaceDetection();
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			sendBroadcast(new Intent(ScriptActivity.ACTION_SPRITES_LIST_INIT));
		}
	}

	private void updateFragment(FragmentTransaction fragmentTransaction) {
		boolean fragmentExists = true;
		if (spritesListFragment == null) {
			spritesListFragment = new SpritesListFragment();
			fragmentExists = false;
		}

		if (fragmentExists) {
			fragmentTransaction.show(spritesListFragment);
		} else {
			fragmentTransaction.add(R.id.fragment_container, spritesListFragment, SpritesListFragment.TAG);
		}
	}

	public void handleCheckBoxClick(View view) {
		spritesListFragment.handleCheckBoxClick(view);
	}

	public void handleAddButton(View view) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		Fragment previousFragment = getFragmentManager().findFragmentByTag(NewSpriteDialog.DIALOG_FRAGMENT_TAG);
		if (previousFragment != null) {
			fragmentTransaction.remove(previousFragment);
		}

		NewSpriteDialog newFragment = new NewSpriteDialog();
		newFragment.show(fragmentTransaction, NewSpriteDialog.DIALOG_FRAGMENT_TAG);
	}

	public void handlePlayButton(View view) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		ProjectManager.getInstance().getCurrentProject().getDataContainer().resetAllDataObjects();
		Intent intent = new Intent(this, PreStageActivity.class);
		startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// Dismiss ActionMode without effecting sounds
		if (spritesListFragment.getActionModeActive() && event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_UP) {
			SpriteAdapter adapter = (SpriteAdapter) spritesListFragment.getListAdapter();
			adapter.clearCheckedItems();
		}

		return super.dispatchKeyEvent(event);
	}

	public void handleShowDetails(boolean showDetails, MenuItem item) {
		spritesListFragment.setShowDetails(showDetails);

		item.setTitle(showDetails ? R.string.hide_details : R.string.show_details);
	}

	public void showEmptyActionModeDialog(String actionMode) {
		@SuppressLint("InflateParams")
		View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_action_mode_empty, null);
		TextView actionModeEmptyText = (TextView) dialogView.findViewById(R.id.dialog_action_mode_emtpy_text);

		if (actionMode.equals(getString(R.string.backpack))) {
			actionModeEmptyText.setText(getString(R.string.nothing_to_backpack_and_unpack));
		} else if (actionMode.equals(getString(R.string.unpacking))) {
			actionModeEmptyText.setText(getString(R.string.nothing_to_unpack));
		} else if (actionMode.equals(getString(R.string.delete))) {
			actionModeEmptyText.setText(getString(R.string.nothing_to_delete));
		} else if (actionMode.equals(getString(R.string.copy))) {
			actionModeEmptyText.setText(getString(R.string.nothing_to_copy));
		} else if (actionMode.equals(getString(R.string.rename))) {
			actionModeEmptyText.setText(getString(R.string.nothing_to_rename));
		}

		AlertDialog actionModeEmptyDialog = new AlertDialog.Builder(this).setView(dialogView)
				.setTitle(actionMode)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();

		actionModeEmptyDialog.setCanceledOnTouchOutside(true);
		actionModeEmptyDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		actionModeEmptyDialog.show();
	}

	public SpritesListFragment getSpritesListFragment() {
		return spritesListFragment;
	}
}
