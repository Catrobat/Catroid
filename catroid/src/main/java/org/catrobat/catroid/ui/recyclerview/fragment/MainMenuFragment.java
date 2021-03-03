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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.io.ProjectAndSceneScreenshotLoader;
import org.catrobat.catroid.io.asynctask.ProjectLoadTask;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ProjectListActivity;
import org.catrobat.catroid.ui.ProjectUploadActivity;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.ui.recyclerview.ProjectListener;
import org.catrobat.catroid.ui.recyclerview.RVButton;
import org.catrobat.catroid.ui.recyclerview.adapter.ButtonAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.HorizontalProjectsAdapter;
import org.catrobat.catroid.ui.recyclerview.dialog.NewProjectDialogFragment;
import org.catrobat.catroid.ui.recyclerview.viewholder.ButtonVH;
import org.catrobat.catroid.utils.FileMetaDataExtractor;
import org.catrobat.catroid.utils.ProjectDownloadUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import kotlin.Lazy;

import static org.catrobat.catroid.common.Constants.EXTRA_PROJECT_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.koin.androidx.viewmodel.compat.ViewModelCompat.viewModel;

public class MainMenuFragment extends Fragment implements
		ProjectListener.OnProjectListener,
		ButtonAdapter.OnItemClickListener,
		ProjectLoadTask.ProjectLoadListener {

	public static final String TAG = MainMenuFragment.class.getSimpleName();

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({HELP, EXPLORE})
	@interface ButtonId {
	}

	private static final int HELP = 3;
	private static final int EXPLORE = 4;

	private static final int CURRENTTHUMBNAILSIZE = 500;

	private View parent;
	private RecyclerView recyclerView;
	private ButtonAdapter buttonAdapter;
	private View.OnClickListener listener;
	private HorizontalProjectsAdapter projectsAdapter;
	private RecyclerView projectsRecyclerView;
	String currentProject;

	private final Lazy<ProjectsViewModel> lazyVM = viewModel(this, ProjectsViewModel.class);

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		parent = inflater.inflate(R.layout.landing_page, container, false);
		recyclerView = parent.findViewById(R.id.recycler_view);
		setShowProgressBar(true);
		return parent;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		List<RVButton> items = getItems();
		buttonAdapter = new ButtonAdapter(items) {

			@NonNull
			@Override
			public ButtonVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
				View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_button, parent, false);
				view.setMinimumHeight(recyclerView.getHeight() / items.size());

				return new ButtonVH(view);
			}
		};

		buttonAdapter.setOnItemClickListener(this);
		recyclerView.setAdapter(buttonAdapter);

		listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onProgramClick(v);
			}
		};
		parent.findViewById(R.id.edit_button).setOnClickListener(listener);
		parent.findViewById(R.id.upload_button).setOnClickListener(listener);
		parent.findViewById(R.id.floating_action_button).setOnClickListener(listener);
		ProjectDownloadUtil.INSTANCE.setFragment(this);
		projectsRecyclerView = parent.findViewById(R.id.my_projects_recyclerview);
		projectsRecyclerView.setHasFixedSize(true);
		parent.findViewById(R.id.my_projects).setOnClickListener(listener);
		parent.findViewById(R.id.image_view).setOnClickListener(listener);

		SnapHelper snap = new LinearSnapHelper();
		snap.attachToRecyclerView(projectsRecyclerView);

		projectsAdapter = new HorizontalProjectsAdapter(this);
		projectsRecyclerView.setAdapter(projectsAdapter);

		lazyVM.getValue().getProjects().observe(getViewLifecycleOwner(), projectData -> {
			setAndLoadCurrentProject(projectData);
			updateRecyclerview(projectData);
		});

		setShowProgressBar(false);
	}

	private List<RVButton> getItems() {
		List<RVButton> items = new ArrayList<>();
		items.add(new RVButton(HELP, ContextCompat.getDrawable(getActivity(), R.drawable.ic_main_menu_help),
				getString(R.string.main_menu_help)));
		items.add(new RVButton(EXPLORE, ContextCompat.getDrawable(getActivity(), R.drawable.ic_main_menu_community),
				getString(R.string.main_menu_web)));
		return items;
	}

	@Override
	public void onResume() {
		super.onResume();
		setShowProgressBar(false);

		String projectName = getActivity().getIntent().getStringExtra(EXTRA_PROJECT_NAME);
		if (projectName != null) {
			getActivity().getIntent().removeExtra(EXTRA_PROJECT_NAME);
			loadDownloadedProject(projectName);
		}
		refreshData();
	}

	private void setAndLoadCurrentProject(List<ProjectData> myProjects) {
		if (myProjects.size() != 0) {
			currentProject = myProjects.get(0).getName();
		} else {
			currentProject = Utils.getCurrentProjectName(getContext());
		}
		File projectDir = new File(DEFAULT_ROOT_DIRECTORY,
				FileMetaDataExtractor.encodeSpecialCharsForFileSystem(currentProject));
		ProjectLoadTask.task(projectDir, getContext());
		loadProjectImage();
	}

	private void loadDownloadedProject(String name) {
		File projectDir = new File(DEFAULT_ROOT_DIRECTORY, FileMetaDataExtractor.encodeSpecialCharsForFileSystem(name));
		new ProjectLoadTask(projectDir, getContext())
				.setListener(this)
				.execute();
	}

	public void setShowProgressBar(boolean show) {
		parent.findViewById(R.id.progress_bar).setVisibility(show ? View.VISIBLE : View.GONE);
		recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onItemClick(@ButtonId int id) {
		switch (id) {
			case HELP:
				setShowProgressBar(true);
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.CATROBAT_HELP_URL)));
				break;
			case EXPLORE:
				setShowProgressBar(true);
				startActivity(new Intent(getActivity(), WebViewActivity.class));
				break;
		}
	}

	public void onProgramClick(View view) {
		switch (view.getId()) {
			case R.id.image_view:
			case R.id.edit_button:
				setShowProgressBar(true);
				File projectDir = new File(DEFAULT_ROOT_DIRECTORY,
						FileMetaDataExtractor.encodeSpecialCharsForFileSystem(currentProject));
				new ProjectLoadTask(projectDir, getContext())
						.setListener(this)
						.execute();
				break;
			case R.id.floating_action_button:
				new NewProjectDialogFragment()
						.show(getFragmentManager(), NewProjectDialogFragment.TAG);
				break;
			case R.id.upload_button:
				setShowProgressBar(true);
				Intent intent = new Intent(getActivity(), ProjectUploadActivity.class)
						.putExtra(ProjectUploadActivity.PROJECT_DIR,
								new File(DEFAULT_ROOT_DIRECTORY, FileMetaDataExtractor
										.encodeSpecialCharsForFileSystem(Utils.getCurrentProjectName(getActivity()))));
				startActivity(intent);
				break;
			case R.id.my_projects:
				setShowProgressBar(true);
				startActivity(new Intent(getActivity(), ProjectListActivity.class));
				break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
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

	private void loadProjectImage() {
		File projectDir = new File(DEFAULT_ROOT_DIRECTORY,
				FileMetaDataExtractor.encodeSpecialCharsForFileSystem(currentProject));

		ProjectAndSceneScreenshotLoader loader =
				new ProjectAndSceneScreenshotLoader(CURRENTTHUMBNAILSIZE, CURRENTTHUMBNAILSIZE);

		loader.loadAndShowScreenshot(projectDir.getName(), loader.getScreenshotSceneName(projectDir), false,
				parent.findViewById(R.id.image_view));
	}

	private void updateRecyclerview(List<ProjectData> myProjects) {
		if (myProjects.size() < 2) {
			projectsAdapter.setItems(null);
		} else {
			int projectsCount = Math.min(myProjects.size(), 10);
			projectsAdapter.setItems(myProjects.subList(1, projectsCount));
		}
	}

	@Override
	public void onProjectClick(ProjectData projectData) {
		setShowProgressBar(true);
		File projectDir = new File(DEFAULT_ROOT_DIRECTORY, FileMetaDataExtractor
				.encodeSpecialCharsForFileSystem(projectData.getName()));

		new ProjectLoadTask(projectDir, getContext())
				.setListener(this)
				.execute();
	}

	public void refreshData() {
		lazyVM.getValue().forceUpdate();
	}
}
