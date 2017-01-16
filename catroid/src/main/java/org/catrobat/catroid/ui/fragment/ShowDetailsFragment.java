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
package org.catrobat.catroid.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.io.ProjectAndSceneScreenshotLoader;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ProjectListActivity;
import org.catrobat.catroid.ui.dialogs.SetDescriptionDialog;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.util.Date;

public class ShowDetailsFragment extends Fragment implements SetDescriptionDialog.ChangeDescriptionInterface {

	public static final String TAG = ShowDetailsFragment.class.getSimpleName();
	public static final String SELECTED_PROJECT_KEY = "selectedProject";

	private ProjectData projectData;
	private TextView description;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View showDetailsFragment = inflater.inflate(R.layout.fragment_project_show_details, container, false);

		try {
			projectData = (ProjectData) getArguments().getSerializable(SELECTED_PROJECT_KEY);
			projectData.project = StorageHandler.getInstance().loadProject(projectData.projectName, getActivity());

			if (projectData.project == null) {
				throw new Exception("Can't load Project!");
			}
		} catch (Exception e) {
			getActivity().getFragmentManager().beginTransaction().remove(this).commit();
			((ProjectListActivity) getActivity()).loadFragment(ProjectListFragment.class, false);
		}

		String sceneName = StorageHandler.getInstance().getFirstSceneName(projectData.projectName);
		ProjectAndSceneScreenshotLoader screenshotLoader = new ProjectAndSceneScreenshotLoader(getActivity());
		XmlHeader header = projectData.project.getXmlHeader();

		ImageView projectImage = (ImageView) showDetailsFragment.findViewById(R.id.image);
		TextView name = (TextView) showDetailsFragment.findViewById(R.id.name);
		TextView author = (TextView) showDetailsFragment.findViewById(R.id.author_value);
		TextView size = (TextView) showDetailsFragment.findViewById(R.id.size_value);
		TextView lastAccess = (TextView) showDetailsFragment.findViewById(R.id.last_access_value);
		TextView screenSize = (TextView) showDetailsFragment.findViewById(R.id.screen_size_value);
		TextView mode = (TextView) showDetailsFragment.findViewById(R.id.mode_value);
		TextView remixOf = (TextView) showDetailsFragment.findViewById(R.id.remix_of_value);
		description = (TextView) showDetailsFragment.findViewById(R.id.description_value);

		int modeText = header.islandscapeMode() ? R.string.landscape : R.string.portrait;
		String screen = header.getVirtualScreenWidth() + "x" + header.getVirtualScreenHeight();

		screenshotLoader.loadAndShowScreenshot(projectData.projectName, sceneName, false, projectImage);
		name.setText(projectData.projectName);
		author.setText(getUserHandle());
		size.setText(UtilFile.getSizeAsString(new File(Utils.buildProjectPath(projectData.projectName))));
		lastAccess.setText(getLastAccess());
		screenSize.setText(screen);
		mode.setText(modeText);
		remixOf.setText(getRemixOf());
		description.setText(header.getDescription());
		description.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handleDescriptionPressed();
			}
		});

		BottomBar.hideBottomBar(getActivity());
		return showDetailsFragment;
	}

	private void handleDescriptionPressed() {
		SetDescriptionDialog dialog = new SetDescriptionDialog(R.string.set_description, R.string.description,
				projectData.project.getDescription(), this);
		dialog.show(getFragmentManager(), SetDescriptionDialog.DIALOG_FRAGMENT_TAG);
	}

	private String getLastAccess() {
		Date lastModified = new Date(projectData.lastUsed);
		String lastAccess;
		if (DateUtils.isToday(lastModified.getTime())) {
			lastAccess = getString(R.string.details_date_today).concat(": ");
			lastAccess = lastAccess.concat(DateFormat.getTimeFormat(getActivity()).format(lastModified));
		} else {
			lastAccess = DateFormat.getDateFormat(getActivity()).format(lastModified);
		}
		return lastAccess;
	}

	private String getUserHandle() {
		String userHandle = projectData.project.getXmlHeader().getUserHandle();
		if (userHandle == null || userHandle.equals("")) {
			return getString(R.string.unknown);
		}
		return userHandle;
	}

	private String getRemixOf() {
		String remixOf = projectData.project.getXmlHeader().getRemixParentsUrlString();
		if (remixOf == null || remixOf.equals("")) {
			return getString(R.string.nxt_no_sensor);
		}
		return remixOf;
	}

	@Override
	public void setDescription(String description) {
		projectData.project.setDescription(description);
		if (StorageHandler.getInstance().saveProject(projectData.project)) {
			this.description.setText(description);
		} else {
			ToastUtil.showError(getActivity(), R.string.error_set_description);
		}
	}
}
