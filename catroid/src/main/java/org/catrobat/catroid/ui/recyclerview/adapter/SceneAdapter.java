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

package org.catrobat.catroid.ui.recyclerview.adapter;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.io.ProjectAndSceneScreenshotLoader;
import org.catrobat.catroid.ui.recyclerview.viewholder.ExtendedVH;

import java.util.List;

public class SceneAdapter extends ExtendedRVAdapter<Scene> {

	public SceneAdapter(List<Scene> items) {
		super(items);
	}

	@Override
	public void onBindViewHolder(ExtendedVH holder, int position) {
		ProjectAndSceneScreenshotLoader loader = new ProjectAndSceneScreenshotLoader(holder.itemView.getContext());
		Scene item = items.get(position);

		String projectName = ProjectManager.getInstance().getCurrentProject().getName();
		holder.name.setText(item.getName());

		loader.loadAndShowScreenshot(projectName, item.getName(), false, holder.image);
	}

	@Override
	public boolean onItemMove(int fromPosition, int toPosition) {
		boolean moved = super.onItemMove(fromPosition, toPosition);
		ProjectManager.getInstance().setCurrentScene(items.get(0));
		return moved;
	}
}
