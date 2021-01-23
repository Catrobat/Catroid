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

package org.catrobat.catroid.ui.recyclerview.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.content.backwardcompatibility.ProjectMetaDataParser;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.asynctask.ProjectCopyTask;
import org.catrobat.catroid.io.asynctask.ProjectExportTask;
import org.catrobat.catroid.io.asynctask.ProjectImportTask;
import org.catrobat.catroid.io.asynctask.ProjectLoadTask;
import org.catrobat.catroid.io.asynctask.ProjectRenameTask;
import org.catrobat.catroid.io.asynctask.ProjectUnzipAndImportTask;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ProjectUploadActivity;
import org.catrobat.catroid.ui.filepicker.FilePickerActivity;
import org.catrobat.catroid.ui.fragment.ProjectDetailsFragment;
import org.catrobat.catroid.ui.recyclerview.adapter.ProjectAdapter;
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableVH;
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.notifications.NotificationData;
import org.catrobat.catroid.utils.notifications.StatusBarNotificationManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.PluralsRes;
import androidx.appcompat.app.AlertDialog;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

import static org.catrobat.catroid.common.Constants.CACHED_PROJECT_ZIP_FILE_NAME;
import static org.catrobat.catroid.common.Constants.CACHE_DIR;
import static org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_DETAILS_PROJECTS_PREFERENCE_KEY;

public class ProjectListFragment extends RecyclerViewFragment<ProjectData> implements
		ProjectLoadTask.ProjectLoadListener,
		ProjectCopyTask.ProjectCopyListener,
		ProjectRenameTask.ProjectRenameListener {

	public static final String TAG = ProjectListFragment.class.getSimpleName();

	private static final int PERMISSIONS_REQUEST_IMPORT_FROM_EXTERNAL_STORAGE = 801;
	private static final int PERMISSIONS_REQUEST_EXPORT_TO_EXTERNAL_STORAGE = 802;

	private static final int REQUEST_IMPORT_PROJECT = 7;

	public void onActivityCreated(Bundle savedInstance) {
		super.onActivityCreated(savedInstance);

		if (getArguments() != null) {
			importProject(getArguments().getParcelable("intent"));
		}
	}

	private ProjectUnzipAndImportTask.ProjectUnzipAndImportListener projectUnzipAndImportListener =
			new ProjectUnzipAndImportTask.ProjectUnzipAndImportListener() {
				@Override
				public void onImportFinished(boolean success) {
					adapter.setItems(getItemList());
					if (!success) {
						ToastUtil.showError(getContext(), R.string.error_import_project);
					}
					setShowProgressBar(false);
				}
			};

	private ProjectImportTask.ProjectImportListener projectImportListener =
			new ProjectImportTask.ProjectImportListener() {
				@Override
				public void onImportFinished(boolean success) {
					adapter.setItems(getItemList());
					if (!success) {
						ToastUtil.showError(getContext(), R.string.error_import_project);
					}
					setShowProgressBar(false);
				}
			};

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

		for (File projectDir : DEFAULT_ROOT_DIRECTORY.listFiles()) {
			File xmlFile = new File(projectDir, CODE_XML_FILE_NAME);
			if (!xmlFile.exists()) {
				continue;
			}

			ProjectMetaDataParser metaDataParser = new ProjectMetaDataParser(xmlFile);

			try {
				items.add(metaDataParser.getProjectMetaData());
			} catch (IOException e) {
				Log.e(TAG, "Well, that's awkward.", e);
			}
		}

		Collections.sort(items, new Comparator<ProjectData>() {
			@Override
			public int compare(ProjectData project1, ProjectData project2) {
				return Long.compare(project2.getLastUsed(), project1.getLastUsed());
			}
		});

		return items;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.import_project:
				showImportChooser();
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void showImportChooser() {
		setShowProgressBar(true);

		new RequiresPermissionTask(PERMISSIONS_REQUEST_IMPORT_FROM_EXTERNAL_STORAGE,
				Arrays.asList(READ_EXTERNAL_STORAGE),
				R.string.runtime_permission_general) {

			@Override
			public void task() {
				startActivityForResult(
						new Intent(getContext(), FilePickerActivity.class), REQUEST_IMPORT_PROJECT);
			}
		}.execute(getActivity());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_IMPORT_PROJECT && resultCode == RESULT_OK) {
			importProject(data);
		}
	}

	private void importProject(Intent data) {
		if (data == null || data.getData() == null) {
			setShowProgressBar(false);
			ToastUtil.showError(getContext(), R.string.error_import_project);
			return;
		}

		try {
			File cacheFile = new File(CACHE_DIR, CACHED_PROJECT_ZIP_FILE_NAME);
			if (cacheFile.exists()) {
				cacheFile.delete();
			}
			File src = new File(data.getData().getPath());
			if (src.isDirectory()) {
				new ProjectImportTask()
						.setListener(projectImportListener)
						.execute(src);
			} else {
				File projectFile = StorageOperations
						.copyUriToDir(getContext().getContentResolver(), data.getData(), CACHE_DIR, CACHED_PROJECT_ZIP_FILE_NAME);
				new ProjectUnzipAndImportTask()
						.setListener(projectUnzipAndImportListener)
						.execute(projectFile);
			}
			setShowProgressBar(true);
		} catch (IOException e) {
			Log.e(TAG, "Cannot resolve project to import.", e);
		}
	}

	@Override
	protected void prepareActionMode(@ActionModeType int type) {
		if (type == COPY) {
			adapter.selectionMode = adapter.MULTIPLE;
		} else if (type == MERGE) {
			adapter.selectionMode = adapter.PAIRS;
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

		ArrayList<Nameable> usedProjectNames = new ArrayList<>(adapter.getItems());

		for (ProjectData projectData : selectedItems) {
			String name = uniqueNameProvider.getUniqueNameInNameables(projectData.getName(), usedProjectNames);
			usedProjectNames.add(new ProjectData(name, null, 0, false));
			new ProjectCopyTask(projectData.getDirectory(), name)
					.setListener(this)
					.execute();
		}
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
				StorageOperations.deleteDir(item.getDirectory());
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

		if (!name.equals(item.getName())) {
			setShowProgressBar(true);

			new ProjectRenameTask(item.getDirectory(), name)
					.setListener(this)
					.execute();
		}
	}

	@Override
	public void onLoadFinished(boolean success) {
		if (getActivity() == null) {
			return;
		}
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
	public void onItemClick(ProjectData item) {
		if (actionModeType == RENAME) {
			super.onItemClick(item);
			return;
		}
		if (actionModeType == NONE) {
			setShowProgressBar(true);
			new ProjectLoadTask(item.getDirectory(), getContext())
					.setListener(this)
					.execute();
		}
	}

	@Override
	public void onItemLongClick(final ProjectData item, CheckableVH holder) {
		CharSequence[] items = new CharSequence[] {
				getString(R.string.copy),
				getString(R.string.delete),
				getString(R.string.rename),
				getString(R.string.show_details),
				getString(R.string.upload_button),
				getString(R.string.save_to_external_storage_button)
		};
		new AlertDialog.Builder(getContext())
				.setTitle(item.getName())
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
								showRenameDialog(item);
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
								ProjectLoadTask.task(item.getDirectory(), getContext());
								startActivity(new Intent(getActivity(), ProjectUploadActivity.class)
										.putExtra(ProjectUploadActivity.PROJECT_DIR, item.getDirectory()));
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
				Context context = getContext();
				if (context == null) {
					return;
				}
				NotificationData notificationData = new StatusBarNotificationManager(context)
						.createSaveProjectToExternalMemoryNotification(context, item.getName());

				new ProjectExportTask(item.getDirectory(), notificationData, context)
						.execute();
			}
		}.execute(getActivity());
	}
}
