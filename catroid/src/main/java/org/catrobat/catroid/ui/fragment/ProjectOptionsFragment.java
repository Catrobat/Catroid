/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.common.ScreenModes;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.io.asynctask.ProjectExportTask;
import org.catrobat.catroid.io.asynctask.ProjectLoadTask;
import org.catrobat.catroid.io.asynctask.ProjectRenameTask;
import org.catrobat.catroid.io.asynctask.ProjectSaveTask;
import org.catrobat.catroid.merge.NewProjectNameTextWatcher;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ProjectUploadActivity;
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.utils.notifications.NotificationData;
import org.catrobat.catroid.utils.notifications.StatusBarNotificationManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ProjectOptionsFragment extends Fragment {

	public static final String TAG = ProjectOptionsFragment.class.getSimpleName();
	private static final int PERMISSIONS_REQUEST_EXPORT_TO_EXTERNAL_STORAGE = 802;

	private View view;

	private Project project;

	private TextInputLayout nameInputLayout;
	private TextInputLayout descriptionInputLayout;
	private TextInputLayout notesAndCreditsInputLayout;

	private ChipGroup tagsChipGroup;
	private List<String> tags = new ArrayList<>();

	private TextView projectUpload;
	private TextView projectSaveExternal;
	private TextView projectMoreDetails;

	private Switch projectAspectRatio;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_project_options, container, false);
		setHasOptionsMenu(true);
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.project_options);

		project = ProjectManager.getInstance().getCurrentProject();

		nameInputLayout = view.findViewById(R.id.project_options_name_layout);
		nameInputLayout.getEditText().setText(project.getName());
		nameInputLayout.getEditText().addTextChangedListener(new NewProjectNameTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (!s.toString().equals(project.getName())) {
					String error = validateInput(s.toString(), getContext());
					nameInputLayout.setError(error);
				} else {
					nameInputLayout.setError(null);
				}
			}
		});

		descriptionInputLayout = view.findViewById(R.id.project_options_description_layout);
		descriptionInputLayout.getEditText().setText(project.getDescription());

		notesAndCreditsInputLayout = view.findViewById(R.id.project_options_notes_and_credits_layout);
		notesAndCreditsInputLayout.getEditText().setText(project.getNotesAndCredits());

		tagsChipGroup = view.findViewById(R.id.chip_group_tags);

		addTags();

		projectAspectRatio = view.findViewById(R.id.project_options_aspect_ratio);
		projectAspectRatio.setChecked(project.getScreenMode() == ScreenModes.STRETCH);

		projectAspectRatio.setOnCheckedChangeListener((compoundButton, checked) -> {
			handleAspectRatioChecked(checked);
		});

		projectUpload = view.findViewById(R.id.project_options_upload);
		projectUpload.setOnClickListener(this::projectUpload);

		projectSaveExternal = view.findViewById(R.id.project_options_save_external);
		projectSaveExternal.setOnClickListener(v -> exportProject());

		projectMoreDetails = view.findViewById(R.id.project_options_more_details);
		projectMoreDetails.setOnClickListener(v -> moreDetails());

		view.findViewById(R.id.project_options_delete).setOnClickListener(v -> handleDeleteButtonPressed());

		BottomBar.hideBottomBar(getActivity());
		return view;
	}

	private void addTags() {
		tagsChipGroup.removeAllViews();
		tags = project.getTags();
		LinearLayout tagsLayout = view.findViewById(R.id.tags);

		if (tags.size() == 1 && tags.get(0).isEmpty()) {
			tagsLayout.setVisibility(View.GONE);
			return;
		}

		tagsLayout.setVisibility(View.VISIBLE);
		for (String tag : tags) {
			Chip chip = new Chip(getContext());
			chip.setText(tag);
			chip.setClickable(false);
			tagsChipGroup.addView(chip);
		}
	}

	private void handleAspectRatioChecked(boolean checked) {
		if (checked) {
			project.setScreenMode(ScreenModes.STRETCH);
		} else {
			project.setScreenMode(ScreenModes.MAXIMIZE);
		}
	}

	private void handleDeleteButtonPressed() {
		ProjectData projectData = new ProjectData(project.getName(), project.getDirectory(),
				project.getCatrobatLanguageVersion(), project.hasScene());
		new AlertDialog.Builder(getContext())
				.setTitle(getResources().getQuantityString(R.plurals.delete_projects, 1))
				.setMessage(R.string.dialog_confirm_delete)
				.setPositiveButton(R.string.yes, (dialog, id) -> deleteProject(projectData))
				.setNegativeButton(R.string.no, null)
				.setCancelable(false)
				.show();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		for (int index = 0; index < menu.size(); index++) {
			menu.getItem(index).setVisible(false);
		}

		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onPause() {
		saveProject();
		super.onPause();
	}

	private void saveProject() {
		if (project != null) {
			setProjectName();
			saveDescription();
			saveCreditsAndNotes();
			ProjectSaveTask.task(project, getContext());
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		ProjectManager.getInstance().setCurrentProject(project);

		nameInputLayout.getEditText().setText(project.getName());

		descriptionInputLayout.getEditText().setText(project.getDescription());

		notesAndCreditsInputLayout.getEditText().setText(project.getNotesAndCredits());

		addTags();

		BottomBar.hideBottomBar(getActivity());
	}

	private void setProjectName() {
		String name = nameInputLayout.getEditText().getText().toString().trim();

		if (!project.getName().equals(name)) {
			try {
				File renamedDirectory = ProjectRenameTask.task(project.getDirectory(), name);
				ProjectLoadTask.task(renamedDirectory, getActivity().getApplicationContext());
				project = ProjectManager.getInstance().getCurrentProject();
			} catch (IOException e) {
				Log.e(TAG, "Creating renamed directory failed!", e);
			}
		}
	}

	public void saveDescription() {
		String description = descriptionInputLayout.getEditText().getText().toString().trim();

		if (project.getDescription() == null || !project.getDescription().equals(description)) {
			project.setDescription(description);
			if (!XstreamSerializer.getInstance().saveProject(project)) {
				ToastUtil.showError(getActivity(), R.string.error_set_description);
			}
		}
	}

	public void saveCreditsAndNotes() {
		String notesAndCredits =
				notesAndCreditsInputLayout.getEditText().getText().toString().trim();

		if (project.getNotesAndCredits() == null || !project.getNotesAndCredits().equals(notesAndCredits)) {
			project.setNotesAndCredits(notesAndCredits);
			if (!XstreamSerializer.getInstance().saveProject(project)) {
				ToastUtil.showError(getActivity(), R.string.error_set_notes_and_credits);
			}
		}
	}

	public void projectUpload(View view) {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		new ProjectSaveTask(currentProject, getActivity().getApplicationContext()).setListener(this::onSaveProjectComplete).execute();
		Utils.setLastUsedProjectName(getActivity().getApplicationContext(), currentProject.getName());
	}

	public void onSaveProjectComplete(boolean success) {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		Intent intent = new Intent(getContext(), ProjectUploadActivity.class);
		intent.putExtra(ProjectUploadActivity.PROJECT_DIR, currentProject.getDirectory());
		startActivity(intent);
	}

	private void exportProject() {
		saveProject();
		new RequiresPermissionTask(PERMISSIONS_REQUEST_EXPORT_TO_EXTERNAL_STORAGE,
				Arrays.asList(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),
				R.string.runtime_permission_general) {

			@Override
			public void task() {
				Context context = getContext();
				if (context == null) {
					return;
				}
				NotificationData notificationData = new StatusBarNotificationManager(context)
						.createSaveProjectToExternalMemoryNotification(context, project.getName());

				new ProjectExportTask(project.getDirectory(), notificationData, context)
						.execute();
			}
		}.execute(getActivity());
	}

	private void moreDetails() {
		ProjectDetailsFragment fragment = new ProjectDetailsFragment();
		Bundle args = new Bundle();
		ProjectData projectData = new ProjectData(project.getName(), project.getDirectory(),
				project.getCatrobatLanguageVersion(), project.hasScene());
		args.putSerializable(ProjectDetailsFragment.SELECTED_PROJECT_KEY, projectData);
		fragment.setArguments(args);
		getActivity().getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_container, fragment, ProjectDetailsFragment.TAG)
				.addToBackStack(ProjectDetailsFragment.TAG)
				.commit();
	}

	protected void deleteProject(ProjectData selectedProject) {
		try {
			StorageOperations.deleteDir(selectedProject.getDirectory());
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}

		ToastUtil.showSuccess(getActivity(),
				getResources().getQuantityString(R.plurals.deleted_projects, 1, 1));

		project = null;
		ProjectManager.getInstance().setCurrentProject(project);
		getActivity().onBackPressed();
	}
}
