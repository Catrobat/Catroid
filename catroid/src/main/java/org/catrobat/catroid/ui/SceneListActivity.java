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

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.ui.dialogs.MergeSceneDialog;
import org.catrobat.catroid.ui.fragment.ListActivityFragment;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.Utils;

public class SceneListActivity extends CoreActivity {

	public static final String TAG = SceneListActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scene_list);
		SnackbarUtil.showHintSnackbar(this, R.string.hint_merge);
	}

	private ListActivityFragment getFragment() {
		return (ListActivityFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_scene_list, menu);
		return super.onCreateOptionsMenu(menu);
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
				getFragment().showPackOrUnpackDialog();;
				break;
			case R.id.upload:
				ProjectManager.getInstance().uploadProject(Utils.getCurrentProjectName(this), this);
				break;
			case R.id.merge:
				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				MergeSceneDialog mergeSceneDialog = new MergeSceneDialog();
				mergeSceneDialog.show(fragmentTransaction, MergeSceneDialog.DIALOG_FRAGMENT_TAG);
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
