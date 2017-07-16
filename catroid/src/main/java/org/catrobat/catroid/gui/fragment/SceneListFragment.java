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

package org.catrobat.catroid.gui.fragment;

import android.content.Intent;

import org.catrobat.catroid.R;
import org.catrobat.catroid.data.ProjectInfo;
import org.catrobat.catroid.data.SceneInfo;
import org.catrobat.catroid.gui.activity.SpriteListActivity;
import org.catrobat.catroid.gui.adapter.RecyclerViewAdapter;
import org.catrobat.catroid.gui.dialog.RenameItemDialog;
import org.catrobat.catroid.projecthandler.ProjectHolder;
import org.catrobat.catroid.storage.DirectoryPathInfo;

public class SceneListFragment extends RecyclerViewListFragment<SceneInfo> {

	public static final String TAG = SceneListFragment.class.getSimpleName();
	public static final String SELECTED_SCENE = "SCENE_NAME";

	private ProjectInfo project = ProjectHolder.getInstance().getCurrentProject();

	@Override
	protected RecyclerViewAdapter<SceneInfo> createAdapter() {
		return new RecyclerViewAdapter<>(project.getScenes());
	}

	@Override
	protected Class getItemType() {
		return SceneInfo.class;
	}

	@Override
	protected DirectoryPathInfo getCurrentDirectory() {
		return project.getDirectoryInfo();
	}

	@Override
	public void addItem(String name) {
		SceneInfo scene = new SceneInfo(name, getCurrentDirectory());
		adapter.addItem(scene);
	}

	@Override
	public void onItemClick(SceneInfo item) {
		Intent intent = new Intent(getActivity(), SpriteListActivity.class);
		intent.putExtra(SELECTED_SCENE, item.getName());
		getActivity().startActivity(intent);
	}

	@Override
	protected void showRenameDialog(String name) {
		RenameItemDialog dialog = new RenameItemDialog(R.string.rename_scene_dialog, name, this);
		dialog.show(getFragmentManager(), RenameItemDialog.TAG);
	}
}
