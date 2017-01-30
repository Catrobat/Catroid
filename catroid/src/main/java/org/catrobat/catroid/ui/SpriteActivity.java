/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.ui.fragment.ListActivityFragment;

public class SpriteActivity extends CoreActivity {

	public static final String TAG = SpriteActivity.class.getSimpleName();
	public static final String FRAGMENT = "fragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sprite);

		Class<? extends Fragment> initialFragment = (Class<? extends Fragment>) getIntent().getSerializableExtra(FRAGMENT);
		loadFragment(initialFragment);
	}

	@Override
	public void onNewIntent(Intent intent) {
		getFragment().onNewIntent(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setActionBarTitle();
	}

	private void setActionBarTitle() {
		Scene scene = ProjectManager.getInstance().getCurrentScene();
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();

		boolean multipleScenes = ProjectManager.getInstance().getCurrentProject().isScenesEnabled();
		String title = multipleScenes ? scene.getName().concat(" : ").concat(sprite.getName()) : sprite.getName();
		getActionBar().setTitle(title);
	}

	private ListActivityFragment getFragment() {
		return (ListActivityFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_sprite_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem showDetailsItem = menu.findItem(R.id.show_details);
		if (showDetailsItem != null) {
			getFragment().setShowDetailsTitle(showDetailsItem);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.delete:
				getFragment().startDeleteActionMode();
				break;
			case R.id.copy:
				getFragment().startCopyActionMode();
				break;
			case R.id.rename:
				getFragment().startRenameActionMode();
				break;
			case R.id.backpack:
				getFragment().showPackOrUnpackDialog();
				break;
			case R.id.show_details:
				getFragment().setShowDetails(!getFragment().getShowDetails());
				getFragment().setShowDetailsTitle(menuItem);
				break;
			default:
				return super.onOptionsItemSelected(menuItem);
		}
		return true;
	}

	public void handleAddButton(View view) {
		getFragment().handleAddButton();
	}

	public void handlePlayButton(View view) {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		ProjectManager.getInstance().setSceneToPlay(currentProject.getDefaultScene());
		startPreStageActivity();
	}

	public void startPreStageActivity() {
		Intent intent = new Intent(this, PreStageActivity.class);
		startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
	}
}
