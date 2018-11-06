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
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.exceptions.ProjectException;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ProjectListActivity;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.ui.recyclerview.RVButton;
import org.catrobat.catroid.ui.recyclerview.activity.ProjectUploadActivity;
import org.catrobat.catroid.ui.recyclerview.adapter.ButtonAdapter;
import org.catrobat.catroid.ui.recyclerview.asynctask.ProjectLoaderTask;
import org.catrobat.catroid.ui.recyclerview.dialog.NewProjectDialogFragment;
import org.catrobat.catroid.ui.recyclerview.viewholder.ButtonVH;
import org.catrobat.catroid.utils.StatusBarNotificationManager;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class MainMenuFragment extends Fragment implements ButtonAdapter.OnItemClickListener,
		ProjectLoaderTask.ProjectLoaderListener {

	public static final String TAG = MainMenuFragment.class.getSimpleName();

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({CONTINUE, NEW, PROGRAMS, HELP, EXPLORE, UPLOAD})
	@interface ButtonId {}
	private static final int CONTINUE = 0;
	private static final int NEW = 1;
	private static final int PROGRAMS = 2;
	private static final int HELP = 3;
	private static final int EXPLORE = 4;
	private static final int UPLOAD = 5;

	private View parent;
	private RecyclerView recyclerView;
	private ButtonAdapter adapter;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		parent = inflater.inflate(R.layout.fragment_list_view, container, false);
		recyclerView = parent.findViewById(R.id.recycler_view);
		setShowProgressBar(true);
		return parent;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		List<RVButton> items = getItems();
		adapter = new ButtonAdapter(items) {

			@NonNull
			@Override
			public ButtonVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
				View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_button, parent, false);
				int itemHeight = parent.getHeight() / items.size();
				view.setMinimumHeight(itemHeight);
				return new ButtonVH(view);
			}
		};
		adapter.setOnItemClickListener(this);
		recyclerView.setAdapter(adapter);
		setShowProgressBar(false);
	}

	private List<RVButton> getItems() {
		List<RVButton> items = new ArrayList<>();
		items.add(new RVButton(CONTINUE, ContextCompat.getDrawable(getActivity(), R.drawable.ic_main_menu_continue),
				getString(R.string.main_menu_continue)));
		items.add(new RVButton(NEW, ContextCompat.getDrawable(getActivity(), R.drawable.ic_main_menu_new),
				getString(R.string.main_menu_new)));
		items.add(new RVButton(PROGRAMS, ContextCompat.getDrawable(getActivity(), R.drawable.ic_main_menu_programs),
				getString(R.string.main_menu_programs)));
		items.add(new RVButton(HELP, ContextCompat.getDrawable(getActivity(), R.drawable.ic_main_menu_help),
				getString(R.string.main_menu_help)));
		items.add(new RVButton(EXPLORE, ContextCompat.getDrawable(getActivity(), R.drawable.ic_main_menu_community),
				getString(R.string.main_menu_web)));
		items.add(new RVButton(UPLOAD, ContextCompat.getDrawable(getActivity(), R.drawable.ic_main_menu_upload),
				getString(R.string.main_menu_upload)));
		return items;
	}

	@Override
	public void onResume() {
		super.onResume();
		setShowProgressBar(false);
		adapter.items.get(0).subtitle = Utils.getCurrentProjectName(getActivity());
		adapter.notifyDataSetChanged();

		String projectName = getActivity().getIntent().getStringExtra(StatusBarNotificationManager.EXTRA_PROJECT_NAME);
		if (projectName != null) {
			loadDownloadedProject(projectName);
		}
	}

	private void loadDownloadedProject(String name) {
		try {
			ProjectManager.getInstance().loadProject(name, getContext());
			Intent intent = new Intent(getContext(), ProjectActivity.class);
			intent.putExtra(ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SCENES);
			startActivity(intent);
		} catch (ProjectException e) {
			Log.e(TAG, Log.getStackTraceString(e));
			ToastUtil.showError(getActivity(), R.string.error_load_project);
		}
	}

	public void setShowProgressBar(boolean show) {
		parent.findViewById(R.id.progress_bar).setVisibility(show ? View.VISIBLE : View.GONE);
		recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onItemClick(@ButtonId int id) {
		switch (id) {
			case CONTINUE:
				ProjectLoaderTask loaderTask = new ProjectLoaderTask(getActivity(), this);
				setShowProgressBar(true);
				loaderTask.execute(Utils.getCurrentProjectName(getActivity()));
				break;
			case NEW:
				new NewProjectDialogFragment().show(getFragmentManager(), NewProjectDialogFragment.TAG);
				break;
			case PROGRAMS:
				setShowProgressBar(true);
				startActivity(new Intent(getActivity(), ProjectListActivity.class));
				break;
			case HELP:
				setShowProgressBar(true);
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.CATROBAT_HELP_URL)));
				break;
			case EXPLORE:
				setShowProgressBar(true);
				startActivity(new Intent(getActivity(), WebViewActivity.class));
				break;
			case UPLOAD:
				setShowProgressBar(true);
				Intent intent = new Intent(getActivity(), ProjectUploadActivity.class)
						.putExtra(ProjectUploadActivity.PROJECT_NAME, Utils.getCurrentProjectName(getActivity()));
				startActivity(intent);
				break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onLoadFinished(boolean success, String message) {
		if (success) {
			Intent intent = new Intent(getActivity(), ProjectActivity.class);
			intent.putExtra(ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SCENES);
			startActivity(intent);
		} else {
			setShowProgressBar(false);
			ToastUtil.showError(getActivity(), message);
		}
	}
}
