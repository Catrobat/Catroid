/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.fragment.SoundFragment;
import org.catrobat.catroid.utils.ErrorListenerInterface;
import org.catrobat.catroid.utils.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class SoundActivity extends SherlockFragmentActivity implements ErrorListenerInterface {

	public static final String ACTION_SOUND_DELETED = "org.catrobat.catroid.SOUND_DELETED";
	public static final String ACTION_SOUND_RENAMED = "org.catrobat.catroid.SOUND_RENAMED";

	private ActionBar actionBar;
	private SoundFragment soundFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sound);
	}

	@Override
	protected void onStart() {
		super.onStart();

		actionBar = getSupportActionBar();

		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		soundFragment = (SoundFragment) getSupportFragmentManager().findFragmentById(R.id.fr_sound);
	}

	// Code from Stackoverflow to reduce memory problems
	// onDestroy() and unbindDrawables() methods taken from
	// http://stackoverflow.com/a/6779067
	@Override
	protected void onDestroy() {
		super.onDestroy();

		unbindDrawables(findViewById(R.id.SoundActivityRoot));
		System.gc();
	}

	private void unbindDrawables(View view) {
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_sound_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home: {
				Intent intent = new Intent(this, MainMenuActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
			}

			//			case R.id.show_details: {
			//				if (soundFragment.getShowDetails()) {
			//					soundFragment.setShowDetails(false);
			//					item.setTitle(getString(R.string.show_details));
			//				} else {
			//					soundFragment.setShowDetails(true);
			//					item.setTitle(getString(R.string.hide_details));
			//				}
			//				//TODO
			//				break;
			//			}

			case R.id.copy: {
				break;
			}

			case R.id.cut: {
				break;
			}

			case R.id.insert_below: {
				break;
			}

			case R.id.move: {
				break;
			}

			case R.id.rename: {
				//TODO startRenameActionMode();
				break;
			}

			case R.id.delete: {
				//TODO startDeleteActionMode();
				break;
			}

			case R.id.settings: {
				Intent intent = new Intent(SoundActivity.this, SettingsActivity.class);
				startActivity(intent);
				break;
			}

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PreStageActivity.REQUEST_RESOURCES_INIT && resultCode == RESULT_OK) {
			Intent intent = new Intent(SoundActivity.this, StageActivity.class);
			startActivityForResult(intent, StageActivity.STAGE_ACTIVITY_FINISH);
		}
		if (requestCode == StageActivity.STAGE_ACTIVITY_FINISH) {
			ProjectManager projectManager = ProjectManager.getInstance();
			int currentSpritePos = projectManager.getCurrentSpritePosition();
			int currentScriptPos = projectManager.getCurrentScriptPosition();
			projectManager.loadProject(projectManager.getCurrentProject().getName(), this, this, false);
			projectManager.setCurrentSpriteWithPosition(currentSpritePos);
			projectManager.setCurrentScriptWithPosition(currentScriptPos);
		}
	}

	public void handleProjectActivityItemLongClick(View view) {
		//TODO
	}

	public void handleCheckBoxClick(View view) {
		//TODO soundFragment.handleCheckBoxClick(view);
	}

	public void handleAddButton(View view) {
		soundFragment.startSelectSoundIntent();
	}

	public void handlePlayButton(View view) {
		Intent intent = new Intent(this, PreStageActivity.class);
		startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
	}

	@Override
	public void showErrorDialog(String errorMessage) {
		Utils.displayErrorMessageFragment(getSupportFragmentManager(), errorMessage);
	}
}
