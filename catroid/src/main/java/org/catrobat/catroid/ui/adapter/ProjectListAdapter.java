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
package org.catrobat.catroid.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.io.ProjectAndSceneScreenshotLoader;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class ProjectListAdapter extends CheckBoxListAdapter<ProjectData> {

	public static final String TAG = ProjectListAdapter.class.getSimpleName();

	ProjectAndSceneScreenshotLoader screenshotLoader;

	public ProjectListAdapter(Context context, int resource, List<ProjectData> listItems) {
		super(context, resource, listItems);
		screenshotLoader = new ProjectAndSceneScreenshotLoader(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View listItemView = super.getView(position, convertView, parent);

		ListItemViewHolder listItemViewHolder = (ListItemViewHolder) listItemView.getTag();
		ProjectData projectData = getItem(position);

		listItemViewHolder.name.setText(projectData.projectName);
		String sceneName = StorageHandler.getInstance().getFirstSceneName(projectData.projectName);

		screenshotLoader.loadAndShowScreenshot(projectData.projectName, sceneName, false, listItemViewHolder.image);

		if (showDetails) {
			listItemViewHolder.details.setVisibility(View.VISIBLE);

			Date lastModified = new Date(projectData.lastUsed);
			String lastAccess = DateFormat.getDateInstance(DateFormat.MEDIUM).format(lastModified);

			listItemViewHolder.leftTopDetails.setText(getContext().getString(R.string.last_used));
			listItemViewHolder.rightTopDetails.setText(lastAccess);

			listItemViewHolder.leftBottomDetails.setText(getContext().getString(R.string.size));
			String size = UtilFile.getSizeAsString(new File(Utils.buildProjectPath(projectData.projectName)),getContext());
			listItemViewHolder.rightBottomDetails.setText(size);
		}

		return listItemView;
	}
}
