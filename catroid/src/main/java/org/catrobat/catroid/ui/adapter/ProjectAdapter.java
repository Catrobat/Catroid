/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.CatrobatProject;
import org.catrobat.catroid.ui.recyclerview.viewholder.ViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

	private Context context;
	private List<CatrobatProject> list;

	public ProjectAdapter(Context context, List<CatrobatProject> list) {
		this.context = context;
		this.list = list;
	}
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(context).inflate(R.layout.projects_recyclerview_single_item, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		CatrobatProject project = list.get(position);

		Log.d("projectloader", "onBindViewHolder: " + list.get(position).getProjectName());

		holder.projectName.setText(String.format("Name : %s", project.getProjectName()));
		holder.projectAuthor.setText(String.format("Author : %s", project.getAuthor()));
		holder.projectId.setText(String.format("Project ID : %s",project.getProjectId()));
		holder.projectDescription.setText(String.format("Description : %s", project.getDescription()));

	}

	@Override
	public int getItemCount() {
		return list.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public TextView projectId, projectName, projectAuthor, projectDescription;

		public ViewHolder(View itemView) {
			super(itemView);

			projectAuthor = itemView.findViewById(R.id.project_author);
			projectId = itemView.findViewById(R.id.project_id);
			projectName = itemView.findViewById(R.id.project_name);
			projectDescription = itemView.findViewById(R.id.project_description);
		}
	}
}
