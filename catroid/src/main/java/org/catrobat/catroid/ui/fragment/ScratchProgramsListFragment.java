/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.common.base.Preconditions;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScratchProgramData;
import org.catrobat.catroid.common.ScratchSearchResult;
import org.catrobat.catroid.scratchconverter.ConversionManager;
import org.catrobat.catroid.transfers.SearchScratchProgramsTask;
import org.catrobat.catroid.ui.ScratchConverterActivity;
import org.catrobat.catroid.ui.ScratchProgramDetailsActivity;
import org.catrobat.catroid.ui.recyclerview.adapter.RVAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.ScratchProgramAdapter;
import org.catrobat.catroid.ui.recyclerview.viewholder.ViewHolder;
import org.catrobat.catroid.utils.ExpiringLruMemoryObjectCache;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.web.ScratchDataFetcher;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public class ScratchProgramsListFragment extends Fragment implements RVAdapter.SelectionListener,
		SearchScratchProgramsTask.SearchScratchProgramsTaskDelegate,
		RVAdapter.OnItemClickListener<ScratchProgramData> {

	private static final String TAG = ScratchProgramsListFragment.class.getSimpleName();
	private static final String SHARED_PREFERENCE_DETAILS_KEY = "showDetailsScratchProjects";

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({NONE, CONVERT})
	@interface ActionModeType {}
	private static final int NONE = 0;
	private static final int CONVERT = 1;

	private RecyclerView recyclerView;
	private ScratchProgramAdapter adapter;
	private ActionMode actionMode;
	private String actionModeTitle = "";

	@ActionModeType
	protected int actionModeType = NONE;

	private ScratchDataFetcher dataFetcher;
	private ConversionManager conversionManager;
	private ScratchConverterActivity activity;

	private SearchView searchView;
	private ImageButton voiceSearchButton;

	private ExpiringLruMemoryObjectCache<ScratchSearchResult> scratchSearchResultCache;
	private SearchScratchProgramsTask currentSearchTask = null;

	private ActionMode.Callback callback = new ActionMode.Callback() {
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			switch (actionModeType) {
				case CONVERT:
					actionModeTitle = getString(R.string.am_convert);
					break;
				case NONE:
					return false;
			}
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.context_menu, menu);

			adapter.showCheckBoxes = true;
			adapter.notifyDataSetChanged();
			mode.setTitle(actionModeTitle);
			searchView.setVisibility(View.GONE);
			voiceSearchButton.setVisibility(View.GONE);
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
			searchView.setVisibility(View.VISIBLE);
			voiceSearchButton.setVisibility(View.VISIBLE);
		}
	};

	private void handleContextualAction() {
		if (adapter.getSelectedItems().isEmpty()) {
			actionMode.finish();
			return;
		}

		switch (actionModeType) {
			case CONVERT:
				convertItems();
				break;
			case NONE:
				throw new IllegalStateException("ActionModeType not set Correctly");
		}
	}

	private void resetActionModeParameters() {
		actionModeType = NONE;
		actionModeTitle = "";
		adapter.showCheckBoxes = false;
		adapter.allowMultiSelection = true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		scratchSearchResultCache = ExpiringLruMemoryObjectCache.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		setHasOptionsMenu(true);

		activity = (ScratchConverterActivity) getActivity();

		View view = inflater.inflate(R.layout.fragment_scratch_projects_list, container, false);

		searchView = view.findViewById(R.id.search_view_scratch);
		voiceSearchButton = view.findViewById(R.id.mic_button_image_scratch);

		recyclerView = view.findViewById(R.id.recycler_view);
		recyclerView.setVisibility(View.INVISIBLE);

		int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
		TextView searchTextView = searchView.findViewById(id);
		searchTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus && !activity.isSlideUpPanelEmpty()) {
					activity.showSlideUpPanelBar(500);
				} else {
					activity.hideSlideUpPanelBar();
				}
			}
		});

		searchTextView.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				searchView.clearFocus();
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				if (newText.length() <= 2) {
					recyclerView.setVisibility(View.INVISIBLE);
				} else {
					search(newText);
				}
				return false;
			}
		});

		voiceSearchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchView.setQuery("", false);
				searchView.clearFocus();
				activity.displaySpeechRecognizer();
			}
		});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		fetchDefaultProjects();

		adapter = new ScratchProgramAdapter(new ArrayList<ScratchProgramData>());
		recyclerView.setAdapter(adapter);

		adapter.showDetails = PreferenceManager.getDefaultSharedPreferences(
				getActivity()).getBoolean(SHARED_PREFERENCE_DETAILS_KEY, false);
		adapter.notifyDataSetChanged();
		recyclerView.setAdapter(adapter);

		adapter.setSelectionListener(this);
		adapter.setOnItemClickListener(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		adapter.clearSelection();
		if (actionModeType != NONE) {
			actionMode.finish();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (currentSearchTask != null) {
			currentSearchTask.cancel(true);
		}
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		adapter.showDetails = PreferenceManager.getDefaultSharedPreferences(
				getActivity()).getBoolean(SHARED_PREFERENCE_DETAILS_KEY, false);

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
						.putBoolean(SHARED_PREFERENCE_DETAILS_KEY, adapter.showDetails)
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
			actionMode = getActivity().startActionMode(callback);
		}
	}

	public void onSelectionChanged(int selectedItemCnt) {
		actionMode.setTitle(actionModeTitle + " " + getResources().getQuantityString(R.plurals.am_projects_title,
				selectedItemCnt,
				selectedItemCnt));
	}

	@Override
	public void onItemClick(ScratchProgramData item) {
		if (actionModeType != NONE) {
			return;
		}

		Preconditions.checkState(conversionManager != null);

		ScratchProgramDetailsActivity.setDataFetcher(dataFetcher);
		ScratchProgramDetailsActivity.setConversionManager(conversionManager);
		Intent intent = new Intent(activity, ScratchProgramDetailsActivity.class);
		intent.putExtra(Constants.INTENT_SCRATCH_PROGRAM_DATA, (Parcelable) item);
		activity.startActivityForResult(intent, Constants.INTENT_REQUEST_CODE_CONVERT);
	}

	@Override
	public void onItemLongClick(ScratchProgramData item, ViewHolder holder) {
	}

	public void setDataFetcher(final ScratchDataFetcher fetcher) {
		dataFetcher = fetcher;
	}

	public void setConversionManager(final ConversionManager manager) {
		conversionManager = manager;
	}

	public void setSearchResultsListViewMargin(int left, int top, int right, int bottom) {
		ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) recyclerView.getLayoutParams();
		params.setMargins(left, top, right, bottom);
		recyclerView.setLayoutParams(params);
	}

	public void searchAndUpdateText(final String text) {
		searchView.setQuery(text, false);
	}

	public void search(final String text) {
		ScratchSearchResult cachedResult = scratchSearchResultCache.get(text);
		if (cachedResult != null) {
			onPostExecute(cachedResult);
			return;
		}

		if (currentSearchTask != null) {
			currentSearchTask.cancel(true);
		}

		currentSearchTask = new SearchScratchProgramsTask();
		currentSearchTask.setContext(activity).setDelegate(this).setFetcher(dataFetcher);
		currentSearchTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, text);
	}

	private void fetchDefaultProjects() {
		searchView.setQuery("", false);

		if (currentSearchTask != null) {
			currentSearchTask.cancel(true);
		}

		currentSearchTask = new SearchScratchProgramsTask();
		currentSearchTask.setContext(activity);
		currentSearchTask.setDelegate(this);
		currentSearchTask.setFetcher(dataFetcher);
		currentSearchTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
	}

	private void convertItems() {
		ArrayList<ScratchProgramData> projectsToConvert = new ArrayList<>();
		projectsToConvert.addAll(adapter.getSelectedItems());
		activity.convertProjects(projectsToConvert);
	}

	@Override
	public void onPreExecute() {
		Log.d(TAG, "onPreExecute for SearchScratchProgramsTask called");
	}

	@Override
	public void onPostExecute(final ScratchSearchResult result) {
		if (result == null || result.getProgramDataList() == null) {
			ToastUtil.showError(activity, R.string.search_failed);
			return;
		}

		if (result.getQuery() != null) {
			scratchSearchResultCache.put(result.getQuery(), result);
		}

		adapter.getItems().clear();
		adapter.getItems().addAll(result.getProgramDataList());
		adapter.notifyDataSetChanged();

		recyclerView.setVisibility(View.VISIBLE);
	}
}
