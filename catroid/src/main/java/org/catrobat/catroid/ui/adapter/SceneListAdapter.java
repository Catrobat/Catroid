/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.io.ProjectAndSceneScreenshotLoader;
import org.catrobat.catroid.ui.dragndrop.DragAndDropAdapterInterface;

import java.util.List;

public class SceneListAdapter extends CheckBoxListAdapter<Scene> implements DragAndDropAdapterInterface {

	public static final String TAG = SceneListAdapter.class.getSimpleName();
	private ProjectAndSceneScreenshotLoader screenshotLoader;

	public SceneListAdapter(Context context, int resource, List<Scene> itemList) {
		super(context, resource, itemList);
		screenshotLoader = new ProjectAndSceneScreenshotLoader(context);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View listItemView = super.getView(position, convertView, parent);

		ListItemViewHolder listItemViewHolder = (ListItemViewHolder) listItemView.getTag();
		String projectName = null;
		Scene scene = getItem(position);

		if (scene.getProject() != null) {
			projectName = scene.getProject().getName();
		}

		String name = scene.getName();
		if (scene == getItem(0)) {
			name = getContext().getString(R.string.start_scene_name, name);
		}

		listItemViewHolder.name.setText(name);
		screenshotLoader.loadAndShowScreenshot(projectName, scene.getName(), scene.isBackPackScene, listItemViewHolder
				.image);
		return listItemView;
	}
}
