/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
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

import java.util.concurrent.locks.Lock;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ProgramMenuActivity extends SherlockFragmentActivity {
	private ActionBar actionBar;
	private Lock viewSwitchLock = new ViewSwitchLock();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_program_menu);

		findViewById(R.id.button_add).setVisibility(View.GONE);
		findViewById(R.id.bottom_bar_separator).setVisibility(View.GONE);

		actionBar = getSupportActionBar();

		String title = ProjectManager.getInstance().getCurrentSprite().getName();
		actionBar.setTitle(title);
		actionBar.setHomeButtonEnabled(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (ProjectManager.getInstance().getCurrentSpritePosition() == 0) {
			((Button) findViewById(R.id.program_menu_button_looks)).setText(R.string.backgrounds);
		} else {
			((Button) findViewById(R.id.program_menu_button_looks)).setText(R.string.looks);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PreStageActivity.REQUEST_RESOURCES_INIT && resultCode == RESULT_OK) {
			SensorHandler.startSensorListener(this);
			Intent intent = new Intent(ProgramMenuActivity.this, StageActivity.class);
			startActivityForResult(intent, StageActivity.STAGE_ACTIVITY_FINISH);
		}
		if (requestCode == StageActivity.STAGE_ACTIVITY_FINISH) {
			SensorHandler.stopSensorListeners();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_program_activity, menu);
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
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		startScriptActivity(ScriptActivity.FRAGMENT_SCRIPTS);
	}

	public void handleLooksButton(View v) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		startScriptActivity(ScriptActivity.FRAGMENT_LOOKS);
	}

	public void handleSoundsButton(View v) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		startScriptActivity(ScriptActivity.FRAGMENT_SOUNDS);
	}

	public void handlePlayButton(View view) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		ProjectManager.getInstance().getCurrentProject().getUserVariables().resetAllUserVariables();
		Intent intent = new Intent(this, PreStageActivity.class);
		startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
	}

	private void startScriptActivity(int fragmentPosition) {
		Intent intent = new Intent(this, ScriptActivity.class);
		intent.putExtra(ScriptActivity.EXTRA_FRAGMENT_POSITION, fragmentPosition);
		startActivity(intent);
	}
}
