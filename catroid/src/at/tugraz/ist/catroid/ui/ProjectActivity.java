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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.stage.PreStageActivity;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.dialogs.NewSpriteDialog;
import at.tugraz.ist.catroid.ui.fragment.SpritesListFragment;
import at.tugraz.ist.catroid.utils.Utils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ProjectActivity extends BaseScriptTabActivity implements SpritesListFragment.Callbacks {

	private ActionBar actionBar;
	private boolean twoPane = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);

		if (findViewById(R.id.script_tabs_container) != null) {
			twoPane = true;
			((SpritesListFragment) getSupportFragmentManager().findFragmentById(R.id.fr_sprites_list))
					.setActivateOnItemClick(true);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		String title = getString(R.string.project_name) + " "
				+ ProjectManager.getInstance().getCurrentProject().getName();
		actionBar = getSupportActionBar();
		actionBar.setTitle(title);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (twoPane) {
			getSupportMenuInflater().inflate(R.menu.menu_current_project_twopane, menu);
		} else {
			getSupportMenuInflater().inflate(R.menu.menu_current_project, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home: {
				Intent intent = new Intent(this, MainMenuActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			}
			case R.id.menu_add_sprite: {
				NewSpriteDialog dialog = new NewSpriteDialog();
				dialog.show(getSupportFragmentManager(), "dialog_new_sprite");
				return true;
			}
			case R.id.menu_start: {
				Intent intent = new Intent(this, PreStageActivity.class);
				startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PreStageActivity.REQUEST_RESOURCES_INIT && resultCode == RESULT_OK) {
			Intent intent = new Intent(ProjectActivity.this, StageActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			sendBroadcast(new Intent(ScriptTabActivity.ACTION_SPRITES_LIST_CHANGED));
		}
	}

	public void handleProjectActivityItemLongClick(View view) {
	}

	@Override
	public void onSpriteSelected(Sprite selectedSprite) {
		if (twoPane) {
			Utils.loadProjectIfNeeded(this);
			updateActionBarTitle();
			setUpSpriteTabs();
		} else {
			Intent intent = new Intent(this, ScriptTabActivity.class);
			startActivity(intent);
		}
	}

	private void updateActionBarTitle() {
		String title = getResources().getString(R.string.sprite_name) + " "
				+ ProjectManager.getInstance().getCurrentSprite().getName();
		actionBar.setTitle(title);
	}
}
