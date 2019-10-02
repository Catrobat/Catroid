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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScratchProgramData;
import org.catrobat.catroid.common.ScratchSearchResult;
import org.catrobat.catroid.scratchconverter.ConversionManager;
import org.catrobat.catroid.transfers.SearchScratchProgramsTask;
import org.catrobat.catroid.ui.ScratchProgramDetailsActivity;
import org.catrobat.catroid.ui.recyclerview.adapter.RVAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.ScratchProgramAdapter;
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableVH;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.CatrobatWebClient;
import org.catrobat.catroid.web.ScratchDataFetcher;
import org.catrobat.catroid.web.ServerCalls;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_DETAILS_SCRATCH_PROJECTS_PREFERENCE_KEY;

public class ScratchSearchResultsFragment extends Fragment implements
		ActionMode.Callback,
		RVAdapter.SelectionListener,
		RVAdapter.OnItemClickListener<ScratchProgramData> {

	public static final String TAG = ScratchSearchResultsFragment.class.getSimpleName();

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({NONE, CONVERT})
	@interface ActionModeType {}
	private static final int NONE = 0;
	private static final int CONVERT = 1;

	private RecyclerView recyclerView;
	private SearchView searchView;
	private ScratchProgramAdapter adapter;
	private ActionMode actionMode;

	private ConversionManager conversionManager;
	private SearchScratchProgramsTask searchTask;
	private ScratchDataFetcher dataFetcher = new ServerCalls(CatrobatWebClient.INSTANCE.getClient());

	class OnQueryListener implements SearchView.OnQueryTextListener {

		@Override
		public boolean onQueryTextSubmit(String query) {
			return false;
		}

		@Override
		public boolean onQueryTextChange(String newText) {
			if (newText.length() > 1) {
				searchTaskDelegate.startSearch(newText);
			}
			return false;
		}
	}

	class SearchTaskDelegate implements SearchScratchProgramsTask.SearchScratchProgramsTaskDelegate {

		void startSearch(String query) {
			clearPendingSearch();

			searchTask = new SearchScratchProgramsTask()
					.setDelegate(this)
					.setFetcher(dataFetcher);

			if (query != null) {
				searchTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, query);
			} else {
				searchTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
			}
		}

		void clearPendingSearch() {
			if (searchTask != null) {
				searchTask.cancel(true);
			}
		}

		@Override
		public void onPreExecute() {
		}

		@Override
		public void onPostExecute(ScratchSearchResult result) {
			if (result == null) {
				ToastUtil.showError(getActivity(), R.string.search_failed);
			} else if (result.getProgramDataList() != null) {
				adapter.setItems(result.getProgramDataList());
			}
		}
	}

	private OnQueryListener onQueryListener = new OnQueryListener();
	private SearchTaskDelegate searchTaskDelegate = new SearchTaskDelegate();

	@ActionModeType
	private int actionModeType = NONE;

	public void setConversionManager(ConversionManager conversionManager) {
		this.conversionManager = conversionManager;
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		switch (actionModeType) {
			case CONVERT:
				mode.setTitle(getString(R.string.am_convert));
				break;
			case NONE:
				return false;
		}

		mode.getMenuInflater().inflate(R.menu.context_menu, menu);

		adapter.showCheckBoxes = true;
		adapter.notifyDataSetChanged();
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.confirm:
				handleContextualAction();
				break;
			default:
				return false;
		}
		return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		resetActionModeParameters();
		adapter.clearSelection();
	}

	private void handleContextualAction() {
		if (adapter.getSelectedItems().isEmpty()) {
			actionMode.finish();
			return;
		}

		switch (actionModeType) {
			case CONVERT:
				convertItems(adapter.getSelectedItems());
				break;
			case NONE:
				throw new IllegalStateException("ActionModeType not set Correctly");
		}
	}

	private void resetActionModeParameters() {
		actionModeType = NONE;
		actionMode = null;
		adapter.showCheckBoxes = false;
		adapter.selectionMode = adapter.MULTIPLE;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_scratch_results, container, false);
		recyclerView = view.findViewById(R.id.recycler_view);
		searchView = view.findViewById(R.id.search_view_scratch);
		setHasOptionsMenu(true);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstance) {
		super.onActivityCreated(savedInstance);

		adapter = new ScratchProgramAdapter(new ArrayList<ScratchProgramData>());
		adapter.showDetails = PreferenceManager.getDefaultSharedPreferences(
				getActivity()).getBoolean(SHOW_DETAILS_SCRATCH_PROJECTS_PREFERENCE_KEY, false);

		recyclerView.setAdapter(adapter);

		adapter.setSelectionListener(this);
		adapter.setOnItemClickListener(this);

		searchView.setOnQueryTextListener(onQueryListener);
	}

	@Override
	public void onResume() {
		super.onResume();
		searchTaskDelegate.startSearch(null);
	}

	@Override
	public void onStop() {
		super.onStop();
		searchTaskDelegate.clearPendingSearch();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_scratch_projects, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		adapter.showDetails = PreferenceManager.getDefaultSharedPreferences(
				getActivity()).getBoolean(SHOW_DETAILS_SCRATCH_PROJECTS_PREFERENCE_KEY, false);

		menu.findItem(R.id.show_details).setTitle(adapter.showDetails
				? R.string.hide_details
				: R.string.show_details);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.convert:
				startActionMode(CONVERT);
				break;
			case R.id.show_details:
				adapter.showDetails = !adapter.showDetails;
				PreferenceManager.getDefaultSharedPreferences(getActivity())
						.edit()
						.putBoolean(SHOW_DETAILS_SCRATCH_PROJECTS_PREFERENCE_KEY, adapter.showDetails)
						.commit();
				adapter.notifyDataSetChanged();
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void startActionMode(@ActionModeType int type) {
		if (adapter.getItems().isEmpty()) {
			ToastUtil.showError(getActivity(), R.string.am_empty_list);
			resetActionModeParameters();
		} else {
			actionModeType = type;
			actionMode = getActivity().startActionMode(this);
		}
	}

	private void finishActionMode() {
		adapter.clearSelection();

		if (actionModeType != NONE) {
			actionMode.finish();
		}
	}

	private void convertItems(List<ScratchProgramData> selectedItems) {
		if (conversionManager.getNumberOfJobsInProgress() > Constants.SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT) {
			ToastUtil.showError(getActivity(), getResources().getQuantityString(
					R.plurals.error_cannot_convert_more_than_x_programs,
					Constants.SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT,
					Constants.SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT));
			return;
		}

		int counter = 0;

		for (ScratchProgramData item : selectedItems) {
			if (Utils.isDeprecatedScratchProgram(item)) {
				DateFormat dateFormat = DateFormat.getDateInstance();
				ToastUtil.showError(getActivity(), getString(R.string.error_cannot_convert_deprecated_scratch_program_x_x,
						item.getTitle(), dateFormat.format(Utils.getScratchSecondReleasePublishedDate())));
				continue;
			}
			if (conversionManager.isJobInProgress(item.getId())) {
				continue;
			}

			conversionManager.convertProgram(item.getId(), item.getTitle(), item.getImage(), false);
			counter++;
		}

		if (counter > 0) {
			ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(
					R.plurals.scratch_conversion_scheduled_x,
					counter,
					counter));
		}

		finishActionMode();
	}

	@Override
	public void onSelectionChanged(int selectedItemCnt) {
		switch (actionModeType) {
			case CONVERT:
				actionMode.setTitle(getResources().getQuantityString(R.plurals.am_convert_projects_title,
						selectedItemCnt, selectedItemCnt));
				break;
			case NONE:
			default:
				throw new IllegalStateException("ActionModeType not set correctly");
		}
	}

	@Override
	public void onItemClick(ScratchProgramData item) {
		if (actionModeType == NONE) {
			ScratchProgramDetailsActivity.setConversionManager(conversionManager);
			Intent intent = new Intent(getActivity(), ScratchProgramDetailsActivity.class);
			intent.putExtra(Constants.INTENT_SCRATCH_PROGRAM_DATA, (Parcelable) item);
			startActivity(intent);
		}
	}

	@Override
	public void onItemLongClick(final ScratchProgramData item, CheckableVH holder) {
		CharSequence[] items = new CharSequence[] {getString(R.string.convert)};
		new AlertDialog.Builder(getActivity())
				.setTitle(item.getTitle())
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								convertItems(new ArrayList<>(Collections.singletonList(item)));
								break;
						}
					}
				})
				.show();
	}
}
