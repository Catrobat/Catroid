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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.backwardcompatibility.ProjectMetaDataParser;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.io.asynctask.ProjectCopier;
import org.catrobat.catroid.io.asynctask.ProjectImportTask;
import org.catrobat.catroid.io.asynctask.ProjectLoadTask;
import org.catrobat.catroid.io.asynctask.ProjectRenamer;
import org.catrobat.catroid.io.asynctask.ProjectUnZipperAndImporter;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.filepicker.FilePickerActivity;
import org.catrobat.catroid.ui.fragment.ProjectOptionsFragment;
import org.catrobat.catroid.ui.recyclerview.adapter.ProjectAdapter;
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableVH;
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask;
import org.catrobat.catroid.utils.ToastUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.annotation.PluralsRes;
import androidx.appcompat.app.AppCompatActivity;
import kotlin.Unit;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

import static org.catrobat.catroid.common.Constants.CACHE_DIR;
import static org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_DETAILS_PROJECTS_PREFERENCE_KEY;
import static org.catrobat.catroid.common.SharedPreferenceKeys.SORT_PROJECTS_PREFERENCE_KEY;

public class ProjectListFragment extends RecyclerViewFragment<ProjectData> implements
		ProjectLoadTask.ProjectLoadListener {

	public static final String TAG = ProjectListFragment.class.getSimpleName();

	private static final int PERMISSIONS_REQUEST_IMPORT_FROM_EXTERNAL_STORAGE = 801;

	private static final int REQUEST_IMPORT_PROJECT = 7;

	private ArrayList<File> filesForUnzipAndImportTask;
	boolean hasUnzipAndImportTaskFinished;
	private ArrayList<File> filesForImportTask;
	boolean hasImportTaskFinished;

	public void onActivityCreated(Bundle savedInstance) {
		super.onActivityCreated(savedInstance);
		filesForImportTask = new ArrayList<>();
		filesForUnzipAndImportTask = new ArrayList<>();
		hasUnzipAndImportTaskFinished = true;
		hasImportTaskFinished = true;
		if (getArguments() != null) {
			importProject(getArguments().getParcelable("intent"));
		}
	}
	private Unit onImportFinished(boolean success) {
		setAdapterItems(adapter.projectsSorted);
		if (!success) {
			ToastUtil.showError(getContext(), R.string.error_import_project);
		} else {
			ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.imported_projects, filesForUnzipAndImportTask.size(), filesForUnzipAndImportTask.size()));
		}
		filesForUnzipAndImportTask.clear();
		setShowProgressBar(false);
		return Unit.INSTANCE;
	}

	private Unit onRenameFinished(boolean success) {
		if (success) {
			if (hasImportTaskFinished && hasUnzipAndImportTaskFinished) {
				ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.imported_projects, filesForUnzipAndImportTask.size(), filesForUnzipAndImportTask.size()));
				filesForUnzipAndImportTask.clear();
			}
			setAdapterItems(adapter.projectsSorted);
		} else {
			ToastUtil.showError(getContext(), R.string.error_rename_incompatible_project);
		}
		setShowProgressBar(false);
		return Unit.INSTANCE;
	}

	private ProjectImportTask.ProjectImportListener projectImportListener =
			new ProjectImportTask.ProjectImportListener() {
				@Override
				public void onImportFinished(boolean success) {
					hasImportTaskFinished = true;
					setAdapterItems(adapter.projectsSorted);

					if (hasImportTaskFinished && hasUnzipAndImportTaskFinished) {
						ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.imported_projects, filesForImportTask.size(), filesForImportTask.size()));
						filesForImportTask.clear();
					} else {
						ToastUtil.showError(getContext(), R.string.error_import_project);
					}
					setShowProgressBar(false);
				}
			};

	@Override
	public void onResume() {
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.project_list_title);
		ProjectManager.getInstance().setCurrentProject(null);

		setAdapterItems(adapter.projectsSorted);
		checkForEmptyList();

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

		getLocalProjectList(items);

		Collections.sort(items, (project1, project2) -> Long.compare(project2.getLastUsed(), project1.getLastUsed()));

		return items;
	}

	private List<ProjectData> getSortedItemList() {
		List<ProjectData> items = new ArrayList<>();

		getLocalProjectList(items);

		Collections.sort(items, (project1, project2) -> project1.getName().compareTo(project2.getName()));

		return items;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.import_project:
				showImportChooser();
				break;
			case R.id.sort_projects:
				adapter.projectsSorted = !adapter.projectsSorted;
				PreferenceManager.getDefaultSharedPreferences(getActivity())
						.edit()
						.putBoolean(SORT_PROJECTS_PREFERENCE_KEY, adapter.projectsSorted)
						.apply();
				setAdapterItems(adapter.projectsSorted);
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
		ArrayList<Uri> uris = new ArrayList<>();
		if (data == null || (data.getData() == null && !data.hasExtra(Intent.EXTRA_STREAM))) {
			setShowProgressBar(false);
			ToastUtil.showError(getContext(), R.string.error_import_project);
			return;
		} else {
			if (data.hasExtra(Intent.EXTRA_STREAM)) {
				uris = (ArrayList<Uri>) data.getExtras().get(Intent.EXTRA_STREAM);
			} else {
				uris.add(data.getData());
			}
		}
		try {
			prepareFilesForImport(uris);
			if (!filesForImportTask.isEmpty()) {
				File[] filesToImport =
						filesForImportTask.toArray(new File[filesForImportTask.size()]);
				new ProjectImportTask()
						.setListener(projectImportListener)
						.execute(filesToImport);
			}
			if (!filesForUnzipAndImportTask.isEmpty()) {
				File[] filesToUnzipAndImport = filesForUnzipAndImportTask.toArray(new File[filesForUnzipAndImportTask.size()]);
				new ProjectUnZipperAndImporter(this::onImportFinished)
						.unZipAndImportAsync(filesToUnzipAndImport);
			}
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
			ProjectCopier projectCopier = new ProjectCopier(projectData.getDirectory(), name);
			projectCopier.copyProjectAsync(success -> {
				onCopyProjectComplete(success);
				return Unit.INSTANCE;
			});
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
				ProjectManager.getInstance().deleteDownloadedProjectInformation(item.getName());
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

		setAdapterItems(adapter.projectsSorted);

		checkForEmptyList();
	}

	void checkForEmptyList() {
		if (adapter.getItems().isEmpty()) {
			setShowProgressBar(true);

			if (ProjectManager.getInstance().initializeDefaultProject(getContext())) {

				setAdapterItems(adapter.projectsSorted);

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

			new ProjectRenamer(item.getDirectory(), name)
					.renameProjectAsync(this::onRenameFinished);
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

	public void onCopyProjectComplete(boolean success) {
		if (success) {
			setAdapterItems(adapter.projectsSorted);
		} else {
			ToastUtil.showError(getContext(), R.string.error_copy_project);
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
		onItemClick(item);
	}

	@Override
	public void onSettingsClick(ProjectData item, View view) {
		PopupMenu popupMenu = new PopupMenu(getContext(), view);
		List<ProjectData> itemList = new ArrayList<>();
		itemList.add(item);

		popupMenu.getMenuInflater().inflate(R.menu.menu_project_activity, popupMenu.getMenu());
		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem menuItem) {

				switch (menuItem.getItemId()) {
					case R.id.copy:
						copyItems(itemList);
						break;
					case R.id.rename:
						showRenameDialog(item);
						break;
					case R.id.delete:
						deleteItems(itemList);
						break;
					case R.id.project_options:
						try {
							Project project = XstreamSerializer.getInstance().loadProject(item.getDirectory(), getActivity());
							ProjectManager.getInstance().setCurrentProject(project);
						} catch (IOException | LoadingProjectException e) {
							ToastUtil.showError(getActivity(), R.string.error_load_project);
							Log.e(TAG, Log.getStackTraceString(e));
							break;
						}

						getActivity().getSupportFragmentManager().beginTransaction()
								.replace(R.id.fragment_container, new ProjectOptionsFragment(), ProjectOptionsFragment.TAG)
								.addToBackStack(ProjectOptionsFragment.TAG)
								.commit();
						break;
					default:
						break;
				}

				return true;
			}
		});
		popupMenu.getMenu().findItem(R.id.backpack).setVisible(false);
		popupMenu.getMenu().findItem(R.id.new_group).setVisible(false);
		popupMenu.getMenu().findItem(R.id.new_scene).setVisible(false);
		popupMenu.getMenu().findItem(R.id.show_details).setVisible(false);
		popupMenu.show();
	}

	@Override
	public void onPrepareOptionsMenu(@NotNull Menu menu) {
		super.onPrepareOptionsMenu(menu);
		Context context = getActivity();
		if (context != null) {
			adapter.projectsSorted = PreferenceManager.getDefaultSharedPreferences(context)
					.getBoolean(SORT_PROJECTS_PREFERENCE_KEY, false);

			menu.findItem(R.id.sort_projects).setTitle(adapter.projectsSorted
					? R.string.unsort_projects
					: R.string.sort_projects);
		}
	}

	public void setAdapterItems(boolean sortProjects) {
		if (sortProjects) {
			adapter.setItems(getSortedItemList());
		} else {
			adapter.setItems(getItemList());
		}
		adapter.notifyDataSetChanged();
	}

	public static void getLocalProjectList(List<ProjectData> items) {
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
	}
	private void prepareFilesForImport(ArrayList<Uri> urisToImport) throws IOException {
		for (Uri uri : urisToImport) {
			if (!uri.getScheme().equals("file")) {
				throw new IllegalArgumentException("importProject has to be called with a file "
						+ "uri. (not a content uri");
			}

			File src = new File(uri.getPath());
			if (src.isDirectory()) {
				filesForImportTask.add(src);
				hasImportTaskFinished = false;
			} else {
				String fileName = uri.getLastPathSegment();
				fileName = fileName.replace(Constants.CATROBAT_EXTENSION, Constants.ZIP_EXTENSION);
				File projectFile = StorageOperations.copyUriToDir(getActivity().getContentResolver(), uri,
						CACHE_DIR, fileName);
				filesForUnzipAndImportTask.add(projectFile);
				hasUnzipAndImportTaskFinished = false;
			}
		}
	}

	public interface ProjectImportFinishedListener {
		void notifyActivityFinished(boolean success);
	}
}
