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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.io.ProjectAndSceneScreenshotLoader;
import org.catrobat.catroid.ui.recyclerview.ProjectListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HorizontalProjectsAdapter extends RecyclerView.Adapter<HorizontalProjectsAdapter.ViewHolder> {

	private List<ProjectData> items;
	private ProjectListener listener;
	private static final int THUMBNAIL_SIZE = 150;

	public HorizontalProjectsAdapter(ProjectListener listener) {
		this.listener = listener;
	}

	public void setItems(List<ProjectData> items) {
		this.items = items;
		notifyDataSetChanged();
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view =
				LayoutInflater.from(parent.getContext()).inflate(R.layout.project_picture_listitem, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		ProjectData project = items.get(position);

		ProjectAndSceneScreenshotLoader loader =
				new ProjectAndSceneScreenshotLoader(THUMBNAIL_SIZE, THUMBNAIL_SIZE);

		loader.loadAndShowScreenshot(project.getName(),
				loader.getScreenshotSceneName(project.getDirectory()), false,
				holder.image);
	}

	@Override
	public int getItemCount() {
		if (items == null) {
			return 0;
		} else {
			return items.size();
		}
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		ImageView image;

		public ViewHolder(View itemView) {
			super(itemView);
			image = itemView.findViewById(R.id.project_image_view);
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			listener.onProjectClick(items.get(getAdapterPosition()));
		}
	}
}
