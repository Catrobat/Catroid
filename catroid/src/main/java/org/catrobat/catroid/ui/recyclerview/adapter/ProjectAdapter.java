/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.io.ProjectAndSceneScreenshotLoader;
import org.catrobat.catroid.ui.recyclerview.viewholder.ExtendedViewHolder;
import org.catrobat.catroid.utils.FileMetaDataExtractor;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProjectAdapter extends ExtendedRVAdapter<ProjectData> {

	public ProjectAdapter(List<ProjectData> items) {
		super(items);
	}

	@Override
	public void onBindViewHolder(ExtendedViewHolder holder, int position) {
		Context context = holder.itemView.getContext();
		int thumbnailWidth = context.getResources().getDimensionPixelSize(R.dimen.project_thumbnail_width);
		int thumbnailHeight = context.getResources().getDimensionPixelSize(R.dimen.project_thumbnail_height);
		ProjectAndSceneScreenshotLoader loader = new ProjectAndSceneScreenshotLoader(thumbnailWidth, thumbnailHeight);
		ProjectData item = items.get(position);

		holder.title.setText(item.getName());
		loader.loadAndShowScreenshot(item.getDirectory().getName(),
				loader.getScreenshotSceneName(item.getDirectory()),
				false,
				holder.image);

		ImageView ripples = holder.itemView.findViewById(R.id.ic_ripples);
		if (ripples != null) {
			ripples.setVisibility(View.GONE);
		}

		if (showDetails) {
			Date lastModified = new Date(item.getLastUsed());
			String lastAccess;
			if (DateUtils.isToday(item.getLastUsed())) {
				lastAccess = context.getString(R.string.last_access_today,
						DateFormat.getTimeInstance(DateFormat.SHORT).format(lastModified));
			} else {
				lastAccess = DateFormat.getDateInstance(DateFormat.MEDIUM).format(lastModified);
			}

			holder.details.setText(String.format(Locale.getDefault(),
					context.getString(R.string.project_details),
					lastAccess,
					FileMetaDataExtractor.getSizeAsString(item.getDirectory(), context)));
			holder.details.setVisibility(View.VISIBLE);
		} else {
			holder.details.setVisibility(View.GONE);
		}
	}
}
