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

package org.catrobat.catroid.ui.recyclerview.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.PluralsRes;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Pair;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.io.asynctask.ProjectCopyTask;
import org.catrobat.catroid.io.asynctask.ProjectExportTask;
import org.catrobat.catroid.io.asynctask.ProjectLoadTask;
import org.catrobat.catroid.io.asynctask.ProjectRenameTask;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.fragment.ProjectDetailsFragment;
import org.catrobat.catroid.ui.recyclerview.activity.ProjectUploadActivity;
import org.catrobat.catroid.ui.recyclerview.adapter.ProjectAdapter;
import org.catrobat.catroid.ui.recyclerview.controller.ProjectController;
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableVH;
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask;
import org.catrobat.catroid.utils.FileMetaDataExtractor;
import org.catrobat.catroid.utils.PathBuilder;
import org.catrobat.catroid.utils.StatusBarNotificationManager;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_DETAILS_PROJECTS_PREFERENCE_KEY;

public class ProjectListFragment extends RecyclerViewFragment<ProjectData> implements
		ProjectLoadTask.ProjectLoadListener,
		ProjectCopyTask.ProjectCopyListener,
		ProjectRenameTask.ProjectRenameListener {

	public static final String TAG = ProjectListFragment.class.getSimpleName();
	private static final int PERMISSIONS_REQUEST_EXPORT_TO_EXTERNAL_STORAGE = 801;

	private ProjectController projectController = new ProjectController();

	@Override
	public void onResume() {
		ProjectManager.getInstance().setCurrentProject(null);
		adapter.setItems(getItemList());
		BottomBar.showBottomBar(getActivity());
		super.onResume();
	}

	@Override
	protected void initializeAdapter() {
		sharedPreferenceDetailsKey = SHOW_DETAILS_PROJECTS_PREFERENCE_KEY;
		adapter = new ProjectAdapter(getItemList());
		onAdapterReady();
	}

	private List<ProjectData> getItemList() {
		List<ProjectData> items = new ArrayList<>();

		for (String projectName : FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY)) {
			File codeFile = new File(PathBuilder.buildPath(PathBuilder.buildProjectPath(projectName), Constants.CODE_XML_FILE_NAME));
			items.add(new ProjectData(projectName, codeFile.lastModified()));
		}

		Collections.sort(items, new Comparator<ProjectData>() {
			@Override
			public int compare(ProjectData project1, ProjectData project2) {
				return Long.valueOf(project2.lastUsed).compareTo(project1.lastUsed);
			}
		});

		return items;
	}

	@Override
	protected void prepareActionMode(@ActionModeType int type) {
		if (type == COPY) {
			adapter.allowMultiSelection = false;
		}
		super.prepareActionMode(type);
	}

	@Override
	protected void packItems(List<ProjectData> selectedItems) {
		throw new IllegalStateException(TAG + ": Projects cannot be backpacked");
	}

	@Override
	protected boolean isBackpackEmpty() {
		return true;
	}

	@Override
	protected void switchToBackpack() {
		throw new IllegalStateException(TAG + ": Projects cannot be backpacked");
	}

	@Override
	protected void copyItems(List<ProjectData> selectedItems) {
		finishActionMode();
		setShowProgressBar(true);

		String name = uniqueNameProvider.getUniqueNameInNameables(selectedItems.get(0).getName(), adapter.getItems());
		new ProjectCopyTask(getContext(), this)
				.execute(selectedItems.get(0).getName(), name);
	}

	@Override
	@PluralsRes
	protected int getDeleteAlertTitleId() {
		return R.plurals.delete_projects;
	}

	@Override
	protected void deleteItems(List<ProjectData> selectedItems) {
		setShowProgressBar(true);

		for (ProjectData item : selectedItems) {
			try {
				projectController.delete(item);
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
			adapter.remove(item);
		}

		ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.deleted_projects,
				selectedItems.size(),
				selectedItems.size()));
		finishActionMode();

		adapter.setItems(getItemList());

		if (adapter.getItems().isEmpty()) {
			setShowProgressBar(true);

			if (ProjectManager.getInstance().initializeDefaultProject(getContext())) {
				adapter.setItems(getItemList());
				setShowProgressBar(false);
			} else {
				ToastUtil.showError(getActivity(), R.string.wtf_error);
				getActivity().finish();
			}
		}
	}

	@Override
	protected int getRenameDialogTitle() {
		return R.string.rename_project;
	}

	@Override
	protected int getRenameDialogHint() {
		return R.string.project_name_label;
	}

	@Override
	public void renameItem(ProjectData item, String name) {
		finishActionMode();

		if (!name.equals(item.projectName)) {
			setShowProgressBar(true);

			new ProjectRenameTask(getContext(), this)
					.execute(item.projectName, name);
		}
	}

	@Override
	public void onLoadFinished(boolean success) {
		if (success) {
			Intent intent = new Intent(getActivity(), ProjectActivity.class);
			intent.putExtra(ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SCENES);
			startActivity(intent);
		} else {
			setShowProgressBar(false);
			ToastUtil.showError(getActivity(), R.string.error_load_project);
		}
	}

	@Override
	public void onCopyFinished(boolean success) {
		if (success) {
			adapter.setItems(getItemList());
		} else {
			ToastUtil.showError(getContext(), R.string.error_copy_project);
		}
		setShowProgressBar(false);
	}

	@Override
	public void onRenameFinished(boolean success) {
		if (success) {
			adapter.setItems(getItemList());
		} else {
			ToastUtil.showError(getContext(), R.string.error_rename_incompatible_project);
		}
		setShowProgressBar(false);
	}

	@Override
	@PluralsRes
	protected int getActionModeTitleId(@ActionModeType int actionModeType) {
		switch (actionModeType) {
			case BACKPACK:
				return R.plurals.am_pack_projects_title;
			case COPY:
				return R.plurals.am_copy_projects_title;
			case DELETE:
				return R.plurals.am_delete_projects_title;
			case RENAME:
				return R.plurals.am_rename_projects_title;
			case NONE:
			default:
				throw new IllegalStateException("ActionModeType not set correctly");
		}
	}

	@Override
	public void onItemClick(ProjectData item) {
		if (actionModeType == NONE) {
			setShowProgressBar(true);
			new ProjectLoadTask(getContext(), this)
					.execute(item.projectName);
		}
	}

	@Override
	public void onItemLongClick(final ProjectData item, CheckableVH holder) {
		CharSequence[] items = new CharSequence[]{
				getString(R.string.copy),
				getString(R.string.delete),
				getString(R.string.rename),
				getString(R.string.show_details),
				getString(R.string.upload_button),
				getString(R.string.save_to_external_storage_button)
		};
		new AlertDialog.Builder(getContext())
				.setTitle(item.projectName)
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								copyItems(new ArrayList<>(Collections.singletonList(item)));
								break;
							case 1:
								showDeleteAlert(new ArrayList<>(Collections.singletonList(item)));
								break;
							case 2:
								showRenameDialog(new ArrayList<>(Collections.singletonList(item)));
								break;
							case 3:
								ProjectDetailsFragment fragment = new ProjectDetailsFragment();
								Bundle args = new Bundle();
								args.putSerializable(ProjectDetailsFragment.SELECTED_PROJECT_KEY, item);
								fragment.setArguments(args);
								getFragmentManager().beginTransaction()
										.replace(R.id.fragment_container, fragment, ProjectDetailsFragment.TAG)
										.addToBackStack(ProjectDetailsFragment.TAG)
										.commit();
								break;
							case 4:
								startActivity(new Intent(getActivity(), ProjectUploadActivity.class)
										.putExtra(ProjectUploadActivity.PROJECT_NAME, item.getName()));
								break;
							case 5:
								exportProject(item);
								break;
							default:
								dialog.dismiss();
						}
					}
				})
				.show();
	}

	private void exportProject(final ProjectData item) {
		new RequiresPermissionTask(PERMISSIONS_REQUEST_EXPORT_TO_EXTERNAL_STORAGE,
				Arrays.asList(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),
				R.string.runtime_permission_general) {

			@Override
			public void task() {
				int notificationID = StatusBarNotificationManager.getInstance()
						.createSaveProjectToExternalMemoryNotification(getContext(), item.projectName);

				Pair projectExportInfo = new Pair<>(item.projectName, notificationID);

				new ProjectExportTask()
						.execute(projectExportInfo);
			}
		}.execute(getActivity());
	}
}
