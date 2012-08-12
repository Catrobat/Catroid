/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui.adapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.io.ProjectScreenshotLoader;
import at.tugraz.ist.catroid.ui.MyProjectsActivity.ProjectData;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.utils.Utils;

public class ProjectAdapter extends ArrayAdapter<ProjectData> {

	private static class ViewHolder {
		private TextView projectName;
		private ImageView image;
		private TextView size;
		private TextView dateChanged;
		// temporarily removed - because of upcoming release, and bad performance of projectdescription
		//		public TextView description;
	}

	private static LayoutInflater inflater;
	private ProjectScreenshotLoader screenshotLoader;

	public ProjectAdapter(Context context, int resource, int textViewResourceId, List<ProjectData> objects) {
		super(context, resource, textViewResourceId, objects);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		screenshotLoader = new ProjectScreenshotLoader(context);
	}

	@Override
	public View getView(int position, View convView, ViewGroup parent) {
		View convertView = convView;
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.activity_my_projects_item, null);
			holder = new ViewHolder();
			holder.projectName = (TextView) convertView.findViewById(R.id.my_projects_activity_project_title);
			holder.image = (ImageView) convertView.findViewById(R.id.my_projects_activity_project_image);
			holder.size = (TextView) convertView.findViewById(R.id.my_projects_activity_size_of_project_2);
			holder.dateChanged = (TextView) convertView.findViewById(R.id.my_projects_activity_project_changed_2);
			// temporarily removed - because of upcoming release, and bad performance of projectdescription
			//			holder.description = (TextView) convertView.findViewById(R.id.my_projects_activity_description);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// ------------------------------------------------------------
		ProjectData projectData = getItem(position);
		String projectName = projectData.projectName;

		//set name of project:
		holder.projectName.setText(projectName);

		// set size of project:
		holder.size.setText(UtilFile.getSizeAsString(new File(Utils.buildProjectPath(projectName))));

		//set last changed:
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		Date projectLastModificationDate = new Date(projectData.lastUsed);
		holder.dateChanged.setText(dateFormat.format(projectLastModificationDate));

		//set project image (threaded):
		screenshotLoader.loadAndShowScreenshot(projectName, holder.image);

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

		return convertView;
	}
}
