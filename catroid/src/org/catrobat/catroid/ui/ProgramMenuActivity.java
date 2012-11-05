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
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.utils.ErrorListenerInterface;
import org.catrobat.catroid.utils.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ProgramMenuActivity extends SherlockFragmentActivity implements ErrorListenerInterface {
	private ActionBar actionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_program_menu);
		findViewById(R.id.btn_add).setVisibility(View.GONE);
		findViewById(R.id.bottom_bar_separator).setVisibility(View.GONE);
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		final ArrayAdapter<Sprite> spinnerAdapter = new ArrayAdapter<Sprite>(this,
				android.R.layout.simple_dropdown_item_1line, ProjectManager.INSTANCE.getCurrentProject()
						.getSpriteList());
		actionBar.setListNavigationCallbacks(spinnerAdapter, new OnNavigationListener() {

			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				ProjectManager.INSTANCE.setCurrentSprite(spinnerAdapter.getItem(itemPosition));
				if (ProjectManager.INSTANCE.getCurrentSpritePosition() == 0) {
					((Button) findViewById(R.id.btn_costumes)).setText(R.string.backgrounds);
				} else {
					((Button) findViewById(R.id.btn_costumes)).setText(R.string.costumes);
				}
				return true;
			}
		});
		actionBar.setSelectedNavigationItem(ProjectManager.INSTANCE.getCurrentSpritePosition());

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PreStageActivity.REQUEST_RESOURCES_INIT && resultCode == RESULT_OK) {
			Intent intent = new Intent(ProgramMenuActivity.this, StageActivity.class);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_program_menu_activity, menu);
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
			case R.id.settings: {
				Intent intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	public void handleScriptsButton(View v) {
		//TODO: start ScriptActivity
		Intent intent = new Intent(this, ScriptTabActivity.class);
		startActivity(intent);
	}

	public void handleCostumesButton(View v) {
		//TODO: start LookActivity
		Intent intent = new Intent(this, ScriptTabActivity.class);
		startActivity(intent);
	}

	public void handleSoundsButton(View v) {
		//TODO: start SoundActivity
		Intent intent = new Intent(this, ScriptTabActivity.class);
		startActivity(intent);
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
