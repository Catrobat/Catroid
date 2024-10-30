/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.ui.recyclerview.fragment.scratchconverter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.catrobat.catroid.R;
import org.catrobat.catroid.io.asynctask.ProjectLoader;
import org.catrobat.catroid.scratchconverter.protocol.Job;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScratchConverterActivity;
import org.catrobat.catroid.ui.recyclerview.adapter.RVAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.ScratchJobAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.multiselection.MultiSelectionManager;
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableViewHolder;
import org.catrobat.catroid.utils.FileMetaDataExtractor;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

public class ScratchProgramsFragment extends Fragment implements
		ScratchConverterActivity.OnJobListListener,
		RVAdapter.OnItemClickListener<Job>,
		ProjectLoader.ProjectLoadListener {

	public static final String TAG = ScratchProgramsFragment.class.getSimpleName();

	private View parent;
	private LinearLayout runningJobsLayout;
	private LinearLayout finishedJobsLayout;
	private RecyclerView runningJobsRecyclerView;
	private RecyclerView finishedJobsRecyclerView;

	private ScratchJobAdapter runningJobsAdapter;
	private ScratchJobAdapter finishedJobsAdapter;

	public void initializeAdapters(ScratchJobAdapter runningJobsAdapter, ScratchJobAdapter finishedJobsAdapter) {
		this.runningJobsAdapter = runningJobsAdapter;
		this.finishedJobsAdapter = finishedJobsAdapter;

		runningJobsAdapter.setOnItemClickListener(this);
		finishedJobsAdapter.setOnItemClickListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		parent = inflater.inflate(R.layout.fragment_scratch_programs, container, false);

		runningJobsLayout = parent.findViewById(R.id.programs_in_progress);
		finishedJobsLayout = parent.findViewById(R.id.programs_finished);
		runningJobsRecyclerView = parent.findViewById(R.id.recycler_view_in_progress);
		finishedJobsRecyclerView = parent.findViewById(R.id.recycler_view_finished);
		return parent;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		runningJobsRecyclerView.setAdapter(runningJobsAdapter);
		finishedJobsRecyclerView.setAdapter(finishedJobsAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		setShowProgressBar(false);
	}

	@Override
	public void onJobListChanged() {
		runningJobsAdapter.notifyDataSetChanged();
		finishedJobsAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(Job item, MultiSelectionManager selectionManager) {
		if (item.getState() == Job.State.FAILED) {
			ToastUtil.showError(getActivity(), R.string.error_cannot_open_failed_scratch_program);
			return;
		}

		if (item.getDownloadState() == Job.DownloadState.DOWNLOADING) {
			new AlertDialog.Builder(getActivity())
					.setTitle(R.string.warning)
					.setMessage(R.string.error_cannot_open_currently_downloading_scratch_program)
					.setNeutralButton(R.string.close, null)
					.create()
					.show();
			return;
		}

		if (item.getDownloadState() == Job.DownloadState.NOT_READY || item.getDownloadState() == Job.DownloadState.CANCELED) {
			new AlertDialog.Builder(getContext())
					.setTitle(R.string.warning)
					.setMessage(R.string.error_cannot_open_not_yet_downloaded_scratch_program)
					.setNeutralButton(R.string.close, null)
					.create()
					.show();
			return;
		}

		File projectDir = new File(DEFAULT_ROOT_DIRECTORY,
				FileMetaDataExtractor.encodeSpecialCharsForFileSystem(item.getTitle()));

		if (!projectDir.exists()) {
			new AlertDialog.Builder(getContext())
					.setTitle(R.string.warning)
					.setMessage(R.string.error_cannot_open_not_existing_scratch_program)
					.setNeutralButton(R.string.close, null)
					.create()
					.show();
			return;
		}

		setShowProgressBar(true);

		new ProjectLoader(projectDir, getContext())
				.setListener(this)
				.loadProjectAsync();
	}

	public void setShowProgressBar(boolean show) {
		parent.findViewById(R.id.progress_bar).setVisibility(show ? View.VISIBLE : View.GONE);
		runningJobsLayout.setVisibility(show ? View.GONE : View.VISIBLE);
		finishedJobsLayout.setVisibility(show ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onLoadFinished(boolean success) {
		if (success) {
			Intent intent = new Intent(getActivity(), ProjectActivity.class);
			intent.putExtra(ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SCENES);
			startActivity(intent);
		} else {
			setShowProgressBar(false);
			ToastUtil.showError(getContext(), R.string.error_load_project);
		}
	}

	@Override
	public void onItemLongClick(Job item, CheckableViewHolder holder) {
	}

	@Override
	public void onSettingsClick(Job item, View view) {
	}
}
