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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import org.catrobat.catroid.ui.recyclerview.adapter.draganddrop.TouchHelperCallback;
import org.catrobat.catroid.ui.recyclerview.viewholder.ViewHolder;
import org.catrobat.catroid.utils.ExpiringLruMemoryObjectCache;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.web.ScratchDataFetcher;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class SearchScratchSearchProjectsListFragment extends Fragment
		implements RVAdapter.SelectionListener,
		SearchScratchProgramsTask.SearchScratchProgramsTaskDelegate,
		RVAdapter.OnItemClickListener<ScratchProgramData> {
	private static final int NONE = 0;
	private static final int CONVERT = 1;
	private static final String TAG = SearchScratchSearchProjectsListFragment.class.getSimpleName();
	private static final String BUNDLE_ARGUMENTS_SCRATCH_PROJECT_DATA = "scratch_project_data";
	private static final String SHARED_PREFERENCE_NAME = "showDetailsScratchProjects";
	@ActionModeType
	protected int actionModeType = NONE;
	protected String sharedPreferenceDetailsKey = "";
	private ScratchProgramAdapter adapter;
	private ScratchDataFetcher dataFetcher;
	private ConversionManager conversionManager;
	private ScratchConverterActivity activity;
	private String actionModeTitle;
	private SearchView searchView;
	private ImageButton audioButton;
	private RecyclerView searchResultsRecyclerView;
	private List<ScratchProgramData> scratchProgramDataList;
	private ScratchProgramData scratchProgramToEdit;
	private ExpiringLruMemoryObjectCache<ScratchSearchResult> scratchSearchResultCache;
	private ActionMode actionMode;
	private SearchScratchProgramsTask currentSearchTask = null;
	private ActionMode.Callback convertModeCallBack = new ActionMode.Callback() {
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			actionMode = mode;
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {

			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.context_menu, menu);
			actionModeTitle = getString(R.string.convert);
			adapter.showCheckBoxes = true;
			adapter.notifyDataSetChanged();

			mode.setTitle(actionModeTitle);
			searchView.setVisibility(View.GONE);
			audioButton.setVisibility(View.GONE);
			setSearchResultsListViewMargin(0, 0, 0, 0);
			return true;
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
	};

	@Override
	public void onItemLongClick(ScratchProgramData item, ViewHolder holder) {
	}

	private void handleContextualAction() {
		if (adapter.getSelectedItems().isEmpty()) {
			actionMode.finish();
			return;
		}

		switch (actionModeType) {
			case CONVERT:
				convertCheckedProjects();
				break;
			case NONE:
				throw new IllegalStateException("ActionModeType not set Correctly");
		}
		actionMode.finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_scratch_projects_convert:
				prepareActionMode(CONVERT);
				break;
			case R.id.show_details:
				adapter.showDetails = !adapter.showDetails;
				PreferenceManager.getDefaultSharedPreferences(
						getActivity()).edit().putBoolean(sharedPreferenceDetailsKey, adapter.showDetails).commit();
				adapter.notifyDataSetChanged();
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	protected void prepareActionMode(@ActionModeType int type) {
		startActionMode(type);
	}

	private void startActionMode(@ActionModeType int type) {
		if (adapter.getItems().isEmpty()) {
			ToastUtil.showError(getActivity(), R.string.am_empty_list);
			resetActionModeParameters();
		} else {
			actionModeType = type;
			actionMode = getActivity().startActionMode(convertModeCallBack);
		}
	}

	protected void finishActionMode() {
		adapter.clearSelection();
		if (actionModeType != NONE) {
			actionMode.finish();
		}
	}

	public void onSelectionChanged(int selectedItemCnt) {
		actionMode.setTitle(actionModeTitle + " " + getResources().getQuantityString(R.plurals.am_looks_title,
				selectedItemCnt,
				selectedItemCnt));
	}

	private void onAdapterReady() {
		adapter.showDetails = PreferenceManager.getDefaultSharedPreferences(
				getActivity()).getBoolean(sharedPreferenceDetailsKey, false);
		adapter.notifyDataSetChanged();
		searchResultsRecyclerView.setAdapter(adapter);

		searchResultsRecyclerView.addItemDecoration(new DividerItemDecoration(searchResultsRecyclerView.getContext(),
				DividerItemDecoration.VERTICAL));

		adapter.setSelectionListener(this);
		adapter.setOnItemClickListener(this);

		ItemTouchHelper.Callback callback = new TouchHelperCallback(adapter);
		ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
		touchHelper.attachToRecyclerView(searchResultsRecyclerView);
	}

	protected void initializeAdapter() {
		if (scratchProgramDataList == null) {
			scratchProgramDataList = new ArrayList<>();
		}
		adapter = new ScratchProgramAdapter(
				scratchProgramDataList);
		searchResultsRecyclerView.setAdapter(adapter);
		searchResultsRecyclerView.addItemDecoration(new DividerItemDecoration(searchResultsRecyclerView.getContext(),
				DividerItemDecoration.VERTICAL));
		onAdapterReady();
	}

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

	public void setDataFetcher(final ScratchDataFetcher fetcher) {
		dataFetcher = fetcher;
	}

	public void setConversionManager(final ConversionManager manager) {
		conversionManager = manager;
	}

	public void setSearchResultsListViewMargin(int left, int top, int right, int bottom) {
		ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) searchResultsRecyclerView.getLayoutParams();
		params.setMargins(left, top, right, bottom);
		searchResultsRecyclerView.setLayoutParams(params);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		adapter.showDetails = PreferenceManager.getDefaultSharedPreferences(
				getActivity()).getBoolean(sharedPreferenceDetailsKey, false);

		menu.findItem(R.id.show_details).setTitle(adapter.showDetails
				? R.string.hide_details
				: R.string.show_details);
	}

	private void resetActionModeParameters() {
		actionModeType = NONE;
		actionModeTitle = "";
		adapter.showCheckBoxes = false;
		adapter.allowMultiSelection = true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setRetainInstance(true);
		super.onCreate(savedInstanceState);
		scratchSearchResultCache = ExpiringLruMemoryObjectCache.getInstance();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (currentSearchTask != null) {
			currentSearchTask.cancel(true);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(SHARED_PREFERENCE_NAME, getShowDetails());
		editor.commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (actionMode != null) {
			actionMode.finish();
			actionMode = null;
		}
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
		setShowDetails(settings.getBoolean(SHARED_PREFERENCE_NAME, false));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		setHasOptionsMenu(true);
		activity = (ScratchConverterActivity) getActivity();
		final View rootView = inflater.inflate(R.layout.fragment_scratch_search_projects_list, container, false);
		searchView = (SearchView) rootView.findViewById(R.id.search_view_scratch);
		searchResultsRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_search_scratch);
		searchResultsRecyclerView.setVisibility(View.INVISIBLE);
		audioButton = (ImageButton) rootView.findViewById(R.id.mic_button_image_scratch);
		int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
		final TextView searchTextView = (TextView) searchView.findViewById(id);
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

		audioButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchView.setQuery("", false);
				searchView.clearFocus();
				activity.displaySpeechRecognizer();
			}
		});

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				searchView.clearFocus();
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				if (newText.length() <= 2) {
					searchResultsRecyclerView.setVisibility(View.INVISIBLE);
					return false;
				}
				search(newText);
				return false;
			}
		});
		return rootView;
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

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			scratchProgramToEdit = (ScratchProgramData) savedInstanceState.getSerializable(BUNDLE_ARGUMENTS_SCRATCH_PROJECT_DATA);
		}
		searchView.setFocusable(false);
		fetchDefaultProjects();
		initializeAdapter();
	}

	private void fetchDefaultProjects() {
		Log.d(TAG, "Fetching default scratch projects");
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

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(BUNDLE_ARGUMENTS_SCRATCH_PROJECT_DATA, scratchProgramToEdit);
		super.onSaveInstanceState(outState);
	}

	public boolean getShowDetails() {
		return adapter.showDetails;
	}

	public void setShowDetails(boolean showDetails) {
		adapter.showDetails = showDetails;
		adapter.notifyDataSetChanged();
	}

	private void convertCheckedProjects() {
		ArrayList<ScratchProgramData> projectsToConvert = new ArrayList<>();
		for (ScratchProgramData scratchProgramToEdit : adapter.getSelectedItems()) {
			projectsToConvert.add(scratchProgramToEdit);
			Log.d(TAG, "Converting project '" + scratchProgramToEdit.getTitle() + "'");
		}
		initializeAdapter();
		activity.convertProjects(projectsToConvert);
	}

	@Override
	public void onPreExecute() {
		Log.d(TAG, "onPreExecute for SearchScratchProgramsTask called");
	}

	@Override
	public void onPostExecute(final ScratchSearchResult result) {
		Log.d(TAG, "onPostExecute for SearchScratchProgramsTask called");
		Preconditions.checkNotNull(adapter, "Scratch project adapter cannot be null!");

		if (result == null || result.getProgramDataList() == null) {
			ToastUtil.showError(activity, R.string.search_failed);
			return;
		}

		if (result.getQuery() != null) {
			scratchSearchResultCache.put(result.getQuery(), result);
		}

		adapter.getItems().clear();
		for (ScratchProgramData projectData : result.getProgramDataList()) {
			adapter.add(projectData);
			Log.d(TAG, projectData.getTitle());
		}

		adapter.notifyDataSetChanged();
		searchResultsRecyclerView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onStop() {
		super.onStop();
		finishActionMode();
	}

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({NONE, CONVERT})
	@interface ActionModeType {
	}
}
