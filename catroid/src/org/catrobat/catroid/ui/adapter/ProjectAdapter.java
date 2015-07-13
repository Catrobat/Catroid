/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.io.ProjectScreenshotLoader;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ProjectAdapter extends ArrayAdapter<ProjectData> {
	private boolean showDetails;
	private int selectMode;
	private Set<Integer> checkedProjects = new TreeSet<Integer>();
	private OnProjectEditListener onProjectEditListener;

	private static class ViewHolder {
		private RelativeLayout background;
		private CheckBox checkbox;
		private TextView projectName;
		private ImageView image;
		private TextView size;
		private TextView dateChanged;
		private View projectDetails;
		private ImageView arrow;
		// temporarily removed - because of upcoming release, and bad performance of projectdescription
		//		public TextView description;
	}

	private static LayoutInflater inflater;
	private ProjectScreenshotLoader screenshotLoader;

	public ProjectAdapter(Context context, int resource, int textViewResourceId, List<ProjectData> objects) {
		super(context, resource, textViewResourceId, objects);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		screenshotLoader = new ProjectScreenshotLoader(context);
		showDetails = false;
		selectMode = ListView.CHOICE_MODE_NONE;
	}

	public void setOnProjectEditListener(OnProjectEditListener listener) {
		onProjectEditListener = listener;
	}

	public void setShowDetails(boolean showDetails) {
		this.showDetails = showDetails;
	}

	public boolean getShowDetails() {
		return showDetails;
	}

	public void setSelectMode(int selectMode) {
		this.selectMode = selectMode;
	}

	public int getSelectMode() {
		return selectMode;
	}

	public Set<Integer> getCheckedProjects() {
		return checkedProjects;
	}

	public int getAmountOfCheckedProjects() {
		return checkedProjects.size();
	}

	public void addCheckedProject(int position) {
		checkedProjects.add(position);
	}

	public void clearCheckedProjects() {
		checkedProjects.clear();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View projectView = convertView;
		final ViewHolder holder;
		if (projectView == null) {
			projectView = inflater.inflate(R.layout.activity_my_projects_list_item, parent, false);
			holder = new ViewHolder();
			holder.background = (RelativeLayout) projectView.findViewById(R.id.my_projects_activity_item_background);
			holder.checkbox = (CheckBox) projectView.findViewById(R.id.project_checkbox);
			holder.projectName = (TextView) projectView.findViewById(R.id.my_projects_activity_project_title);
			holder.image = (ImageView) projectView.findViewById(R.id.my_projects_activity_project_image);
			holder.size = (TextView) projectView.findViewById(R.id.my_projects_activity_size_of_project_2);
			holder.dateChanged = (TextView) projectView.findViewById(R.id.my_projects_activity_project_changed_2);
			holder.projectDetails = projectView.findViewById(R.id.my_projects_activity_list_item_details);
			holder.arrow = (ImageView) projectView.findViewById(R.id.arrow_right);
			// temporarily removed - because of upcoming release, and bad performance of projectdescription
			//			holder.description = (TextView) projectView.findViewById(R.id.my_projects_activity_description);
			projectView.setTag(holder);
		} else {
			holder = (ViewHolder) projectView.getTag();
		}

		// ------------------------------------------------------------
		ProjectData projectData = getItem(position);
		String projectName = projectData.projectName;

		//set name of project:
		holder.projectName.setText(projectName);

		// set size of project:
		holder.size.setText(UtilFile.getSizeAsString(new File(Utils.buildProjectPath(projectName))));

		//set last changed:
		Date projectLastModificationDate = new Date(projectData.lastUsed);
		Date now = new Date();
		Date yesterday = new Date(now.getTime() - DateUtils.DAY_IN_MILLIS);
		DateFormat mediumDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
		DateFormat shortTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
		String projectLastModificationDateString = "";

		Calendar nowCalendar = Calendar.getInstance();
		nowCalendar.setTime(now);

		Calendar yesterdayCalendar = Calendar.getInstance();
		yesterdayCalendar.setTime(yesterday);

		Calendar projectLastModificationDateCalendar = Calendar.getInstance();
		projectLastModificationDateCalendar.setTime(projectLastModificationDate);

		if (mediumDateFormat.format(projectLastModificationDate).equals(mediumDateFormat.format(now))) {
			projectLastModificationDateString = getContext().getString(R.string.details_date_today) + " "
					+ shortTimeFormat.format(projectLastModificationDate);
		} else if (mediumDateFormat.format(projectLastModificationDate).equals(mediumDateFormat.format(yesterday))) {
			projectLastModificationDateString = getContext().getString(R.string.details_date_yesterday);
		} else {
			projectLastModificationDateString = mediumDateFormat.format(projectLastModificationDate);
		}

		holder.dateChanged.setText(projectLastModificationDateString);

		//set project image (threaded):
		screenshotLoader.loadAndShowScreenshot(projectName, holder.image);

		if (!showDetails) {
			holder.projectDetails.setVisibility(View.GONE);
			holder.projectName.setSingleLine(true);
		} else {
			holder.projectDetails.setVisibility(View.VISIBLE);
			holder.projectName.setSingleLine(false);
		}

		holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					if (selectMode == ListView.CHOICE_MODE_SINGLE) {
						clearCheckedProjects();
					}
					checkedProjects.add(position);
				} else {
					checkedProjects.remove(position);
				}
				notifyDataSetChanged();

				if (onProjectEditListener != null) {
					onProjectEditListener.onProjectChecked();
				}
			}
		});

		holder.background.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
				if (selectMode != ListView.CHOICE_MODE_NONE) {
					return true;
				}
				return false;
			}
		});

		holder.background.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (selectMode != ListView.CHOICE_MODE_NONE) {
					holder.checkbox.setChecked(!holder.checkbox.isChecked());
				} else if (onProjectEditListener != null) {
					onProjectEditListener.onProjectEdit(position);
				}
			}
		});

		if (checkedProjects.contains(position)) {
			holder.checkbox.setChecked(true);
		} else {
			holder.checkbox.setChecked(false);
		}
		if (selectMode != ListView.CHOICE_MODE_NONE) {
			holder.checkbox.setVisibility(View.VISIBLE);
			holder.arrow.setVisibility(View.GONE);
			holder.background.setBackgroundResource(R.drawable.button_background_shadowed);
		} else {
			holder.checkbox.setVisibility(View.GONE);
			holder.checkbox.setChecked(false);
			holder.arrow.setVisibility(View.VISIBLE);
			holder.background.setBackgroundResource(R.drawable.button_background_selector);
			clearCheckedProjects();
		}

		//set project description:

		// temporarily removed - because of upcoming release, and bad performance of projectdescription
		//		ProjectManager projectManager = ProjectManager.getInstance();
		//		String currentProjectName = projectManager.getCurrentProject().getName();

		//		if (projectName.equalsIgnoreCase(currentProjectName)) {
		//			holder.description.setText(projectManager.getCurrentProject().description);
		//		} else {
		//			projectManager.loadProject(projectName, context, false);
		//			holder.description.setText(projectManager.getCurrentProject().description);
		//			projectManager.loadProject(currentProjectName, context, false);
		//		}

		return projectView;
	}

	public interface OnProjectEditListener {
		void onProjectChecked();

		void onProjectEdit(int position);
	}
}
