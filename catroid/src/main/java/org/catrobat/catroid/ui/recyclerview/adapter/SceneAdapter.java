/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.ProjectAndSceneScreenshotLoader;
import org.catrobat.catroid.ui.recyclerview.viewholder.ExtendedViewHolder;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class SceneAdapter extends ExtendedRVAdapter<Scene> {

	public SceneAdapter(List<Scene> items) {
		super(items);
	}

	@Override
	public void onBindViewHolder(ExtendedViewHolder holder, int position) {
		int thumbnailWidth = holder.itemView.getContext().getResources().getDimensionPixelSize(R.dimen.project_thumbnail_width);
		int thumbnailHeight = holder.itemView.getContext().getResources().getDimensionPixelSize(R.dimen.project_thumbnail_height);
		ProjectAndSceneScreenshotLoader loader = new ProjectAndSceneScreenshotLoader(thumbnailWidth, thumbnailHeight);
		Scene item = items.get(position);

		File projectDir = ProjectManager.getInstance().getCurrentProject().getDirectory();
		holder.title.setText(item.getName());

		loader.loadAndShowScreenshot(projectDir.getName(), item.getDirectory().getName(), false, holder.image);

		if (showDetails) {
			holder.details.setText(String.format(Locale.getDefault(),
					holder.itemView.getContext().getString(R.string.scene_details),
					item.getSpriteList().size(),
					getLookCount(item),
					getSoundCount(item)));
			holder.details.setVisibility(View.VISIBLE);
		} else {
			holder.details.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onItemMove(int sourcePosition, int targetPosition) {
		boolean moved = super.onItemMove(sourcePosition, targetPosition);
		ProjectManager.getInstance().setCurrentlyEditedScene(items.get(0));
		return moved;
	}

	private int getLookCount(Scene scene) {
		int lookCount = 0;
		for (Sprite sprite : scene.getSpriteList()) {
			lookCount += sprite.getLookList().size();
		}
		return lookCount;
	}

	private int getSoundCount(Scene scene) {
		int soundCount = 0;
		for (Sprite sprite : scene.getSpriteList()) {
			soundCount += sprite.getSoundList().size();
		}
		return soundCount;
	}
}
