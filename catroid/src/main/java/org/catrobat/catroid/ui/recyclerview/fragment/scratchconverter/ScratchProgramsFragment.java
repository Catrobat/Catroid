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

package org.catrobat.catroid.ui.recyclerview.fragment.scratchconverter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.catrobat.catroid.R;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.scratchconverter.protocol.Job;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScratchConverterActivity;
import org.catrobat.catroid.ui.recyclerview.adapter.RVAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.ScratchJobAdapter;
import org.catrobat.catroid.ui.recyclerview.asynctask.ProjectLoaderTask;
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableVH;
import org.catrobat.catroid.utils.ToastUtil;

public class ScratchProgramsFragment extends Fragment implements
		ScratchConverterActivity.OnJobListListener,
		RVAdapter.OnItemClickListener<Job>,
		ProjectLoaderTask.ProjectLoaderListener {

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
	public void onItemClick(Job item) {
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
			new AlertDialog.Builder(getActivity())
					.setTitle(R.string.warning)
					.setMessage(R.string.error_cannot_open_not_yet_downloaded_scratch_program)
					.setNeutralButton(R.string.close, null)
					.create()
					.show();
			return;
		}

		if (!XstreamSerializer.getInstance().projectExists(item.getTitle())) {
			new AlertDialog.Builder(getActivity())
					.setTitle(R.string.warning)
					.setMessage(R.string.error_cannot_open_not_existing_scratch_program)
					.setNeutralButton(R.string.close, null)
					.create()
					.show();
			return;
		}

		setShowProgressBar(true);
		ProjectLoaderTask loadProjectTask = new ProjectLoaderTask(getActivity(), this);
		loadProjectTask.execute(item.getTitle());
	}

	public void setShowProgressBar(boolean show) {
		parent.findViewById(R.id.progress_bar).setVisibility(show ? View.VISIBLE : View.GONE);
		runningJobsLayout.setVisibility(show ? View.GONE : View.VISIBLE);
		finishedJobsLayout.setVisibility(show ? View.GONE : View.VISIBLE);
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

	@Override
	public void onItemLongClick(Job item, CheckableVH holder) {
	}
}
