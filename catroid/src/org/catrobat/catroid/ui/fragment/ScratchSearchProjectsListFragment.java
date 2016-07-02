/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.common.base.Preconditions;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScratchProjectPreviewData;
import org.catrobat.catroid.common.ScratchSearchResult;
import org.catrobat.catroid.transfers.FetchScratchProjectsTask;
import org.catrobat.catroid.transfers.ScratchConverterClient;
import org.catrobat.catroid.transfers.ScratchConverterWebSocketClient;
import org.catrobat.catroid.ui.CapitalizedTextView;
import org.catrobat.catroid.ui.ScratchConverterActivity;
import org.catrobat.catroid.ui.ScratchProjectDetailsActivity;
import org.catrobat.catroid.ui.adapter.ScratchProjectAdapter;
import org.catrobat.catroid.utils.ExpiringLruMemoryObjectCache;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ScratchSearchProjectsListFragment extends Fragment
        implements FetchScratchProjectsTask.ScratchProjectListTaskDelegate, ScratchProjectAdapter.OnScratchProjectEditListener {

    private static final String BUNDLE_ARGUMENTS_SCRATCH_PROJECT_DATA = "scratch_project_data";
    private static final String SHARED_PREFERENCE_NAME = "showDetailsScratchProjects";
    private static final String TAG = ScratchSearchProjectsListFragment.class.getSimpleName();
    private static ScratchConverterClient converterClient = ScratchConverterWebSocketClient.getInstance();

    private String convertActionModeTitle;
    private static String singleItemAppendixConvertActionMode;
    private static String multipleItemAppendixConvertActionMode;

    private SearchView searchView;
    private ImageButton audioButton;
    private ListView searchResultsListView;
    private ProgressDialog progressDialog;
    private List<ScratchProjectPreviewData> scratchProjectList;
    private ScratchProjectPreviewData scratchProjectToEdit;
    private ExpiringLruMemoryObjectCache<ScratchSearchResult> scratchSearchResultCache;
    private ScratchProjectAdapter scratchProjectAdapter;
    private ActionMode actionMode;
    private View selectAllActionModeButton;
    private FetchScratchProjectsTask currentTask = null;
    private boolean selectAll = true;

    // dependency-injection for testing with mock object
    public static void setConverterClient(ScratchConverterClient client) {
        converterClient = client;
    }

    private void setSearchResultsListViewMargin(int left, int top, int right, int bottom) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)searchResultsListView.getLayoutParams();
        params.setMargins(left, top, right, bottom);
        searchResultsListView.setLayoutParams(params);
    }

    private ActionMode.Callback convertModeCallBack = new ActionMode.Callback() {
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            setSelectMode(ListView.CHOICE_MODE_MULTIPLE);

            convertActionModeTitle = getString(R.string.convert);
            singleItemAppendixConvertActionMode = getString(R.string.program);
            multipleItemAppendixConvertActionMode = getString(R.string.programs);

            mode.setTitle(convertActionModeTitle);
            addSelectAllActionModeButton(mode, menu);
            searchView.setVisibility(View.GONE);
            audioButton.setVisibility(View.GONE);
            setSearchResultsListViewMargin(0, 0, 0, 0);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (scratchProjectAdapter.getAmountOfCheckedProjects() == 0) {
                clearCheckedProjectsAndEnableButtons();
            } else {
                convertCheckedProjects();
                clearCheckedProjectsAndEnableButtons();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
        scratchSearchResultCache = ExpiringLruMemoryObjectCache.getInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
        if (currentTask != null) {
            currentTask.cancel(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
                .getApplicationContext());
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
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
                .getApplicationContext());
        setShowDetails(settings.getBoolean(SHARED_PREFERENCE_NAME, false));
        initAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final ScratchSearchProjectsListFragment fragment = this;
        final ScratchConverterActivity activity = (ScratchConverterActivity)getActivity();
        final View rootView = inflater.inflate(R.layout.fragment_scratch_search_projects_list, container, false);
        searchView = (SearchView)rootView.findViewById(R.id.search_view_scratch);
        searchResultsListView = (ListView) rootView.findViewById(R.id.list_view_search_scratch);
        searchResultsListView.setVisibility(View.INVISIBLE);
        audioButton = (ImageButton) rootView.findViewById(R.id.mic_button_image_scratch);
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setGravity(Gravity.CENTER);
        progressDialog.hide();

        int id = searchView.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(id);
        //textView.setTextColor(Color.BLACK);
        //textView.setHintTextColor(Color.GRAY);
        textView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.displaySpeechRecognizer();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, "SUBMIT");
                Log.i(TAG, query);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i(TAG, newText);
                if (newText.length() <= 2) {
                    searchResultsListView.setVisibility(View.INVISIBLE);
                    return false;
                }
                search(newText);
                return false;
            }
        });
        // TODO: >> powered by "https://www.google.com/uds/css/small-logo.png" Custom Search << (grey)
        rootView.clearFocus();
        return rootView;
    }

    public void searchAndUpdateText(final String text) {
        searchView.setQuery(text, false);
    }

    public void search(final String text) {
        // TODO: consider pagination for cache!
        ScratchSearchResult cachedResult = scratchSearchResultCache.get(text);
        if (cachedResult != null) {
            Log.d(TAG, "Cache hit!");
            onPostExecute(cachedResult);
            return;
        }

        // cache miss
        Log.d(TAG, "Cache miss!");
        if (currentTask != null) {
            currentTask.cancel(true);
        }
        currentTask = new FetchScratchProjectsTask();
        currentTask.setDelegate(this).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, text);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(searchResultsListView);
        if (savedInstanceState != null) {
            scratchProjectToEdit = (ScratchProjectPreviewData) savedInstanceState.getSerializable(BUNDLE_ARGUMENTS_SCRATCH_PROJECT_DATA);
        }
        initAdapter();
        searchView.setFocusable(false);
        fetchDefaultProjects();
    }

    private void fetchDefaultProjects() {
        Log.d(TAG, "Fetching default scratch projects");
        searchView.setQuery("", false);
        new FetchScratchProjectsTask().setDelegate(this).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(BUNDLE_ARGUMENTS_SCRATCH_PROJECT_DATA, scratchProjectToEdit);
        super.onSaveInstanceState(outState);
    }

    public boolean getShowDetails() {
        return scratchProjectAdapter.getShowDetails();
    }

    public void setShowDetails(boolean showDetails) {
        scratchProjectAdapter.setShowDetails(showDetails);
        scratchProjectAdapter.notifyDataSetChanged();
    }

    public int getSelectMode() {
        return scratchProjectAdapter.getSelectMode();
    }

    public void setSelectMode(int selectMode) {
        scratchProjectAdapter.setSelectMode(selectMode);
        scratchProjectAdapter.notifyDataSetChanged();
    }

    private void initAdapter() {
        if (scratchProjectList == null) {
            scratchProjectList = new ArrayList<>();
        }
        scratchProjectAdapter = new ScratchProjectAdapter(getActivity(),
                R.layout.fragment_scratch_project_list_item,
                R.id.scratch_projects_list_item_title,
                scratchProjectList);
        searchResultsListView.setAdapter(scratchProjectAdapter);
        //setListAdapter(scratchProjectAdapter);
        initClickListener();
    }

    private void initClickListener() {
        scratchProjectAdapter.setOnScratchProjectEditListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        scratchProjectToEdit = scratchProjectAdapter.getItem(info.position);
        scratchProjectAdapter.addCheckedProject(info.position);
        getActivity().getMenuInflater().inflate(R.menu.context_menu_scratch_projects, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu_show_description:
                showDescriptionDialog();
                break;

            case R.id.context_menu_convert:
                Log.d(TAG, "Clicked convert item in context menu for scratch project '"
                        + scratchProjectToEdit.getTitle() + "'");
                ArrayList<ScratchProjectPreviewData> projectList = new ArrayList<>();
                projectList.add(scratchProjectToEdit);
                convertProjects(projectList);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onProjectChecked() {
        if (scratchProjectAdapter.getSelectMode() == ListView.CHOICE_MODE_SINGLE || actionMode == null) {
            return;
        }

        updateActionModeTitle();
    }

    private void updateActionModeTitle() {
        int numberOfSelectedItems = scratchProjectAdapter.getAmountOfCheckedProjects();
        if (numberOfSelectedItems == 0) {
            actionMode.setTitle(convertActionModeTitle);
        } else {
            String appendix = multipleItemAppendixConvertActionMode;

            if (numberOfSelectedItems == 1) {
                appendix = singleItemAppendixConvertActionMode;
            }

            String numberOfItems = Integer.toString(numberOfSelectedItems);
            String completeTitle = convertActionModeTitle + " " + numberOfItems + " " + appendix;

            int titleLength = convertActionModeTitle.length();

            Spannable completeSpannedTitle = new SpannableString(completeTitle);
            completeSpannedTitle.setSpan(
                    new ForegroundColorSpan(getResources().getColor(R.color.actionbar_title_color)), titleLength + 1,
                    titleLength + (1 + numberOfItems.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            actionMode.setTitle(completeSpannedTitle);
        }
    }

    @Override
    public void onProjectEdit(int position) {
        Intent intent = new Intent(getActivity(), ScratchProjectDetailsActivity.class);
        intent.putExtra(Constants.SCRATCH_PROJECT_DATA, (Parcelable) scratchProjectAdapter.getItem(position));
        getActivity().startActivity(intent);
        // searchResultsListView.setSelectionAfterHeaderView(); // scroll to top...
    }

    public void startConvertActionMode() {
        if (actionMode == null) {
            actionMode = getActivity().startActionMode(convertModeCallBack);
        }
    }

    private void showDescriptionDialog() {
        ToastUtil.showError(getActivity(), "Not implemented yet!");
        /*
        SetDescriptionDialog dialogSetDescription = SetDescriptionDialog.newInstance(projectToEdit.projectName);
        dialogSetDescription.setOnUpdateProjectDescriptionListener(ProjectsListFragment.this);
        dialogSetDescription.show(getActivity().getFragmentManager(), SetDescriptionDialog.DIALOG_FRAGMENT_TAG);
        */
    }

    private void convertProjects(List<ScratchProjectPreviewData> projectList) {
        // TODO: create websocket connection and set up receiver
        Log.i(TAG, "Converting projects:");
        for (ScratchProjectPreviewData projectData : projectList) {
            Log.i(TAG, projectData.getTitle());
            //convertProject(scratchProjectToEdit);
            // TODO: send convert command
            converterClient.convertProject(projectData.getId(), projectData.getTitle());
        }
        ToastUtil.showSuccess(getActivity(), getActivity().getString(R.string.scratch_conversion_started));
    }

    private void convertCheckedProjects() {
        int numConverted = 0;
        ArrayList<ScratchProjectPreviewData> projectsToConvert = new ArrayList<>();
        for (int position : scratchProjectAdapter.getCheckedProjects()) {
            scratchProjectToEdit = (ScratchProjectPreviewData) searchResultsListView.getItemAtPosition(position - numConverted);
            projectsToConvert.add(scratchProjectToEdit);
            Log.d(TAG, "Converting project '" + scratchProjectToEdit.getTitle() + "'");
            numConverted++;
        }
        initAdapter();
        convertProjects(projectsToConvert);
    }

    private void clearCheckedProjectsAndEnableButtons() {
        setSelectMode(ListView.CHOICE_MODE_NONE);
        scratchProjectAdapter.clearCheckedProjects();
        actionMode = null;
        searchView.setVisibility(View.VISIBLE);
        audioButton.setVisibility(View.VISIBLE);
        int marginTop = getActivity().getResources().getDimensionPixelSize(R.dimen.scratch_project_search_list_view_margin_top);
        int marginBottom = getActivity().getResources().getDimensionPixelSize(R.dimen.scratch_project_search_list_view_margin_bottom);
        setSearchResultsListViewMargin(0, marginTop, 0, marginBottom);
    }

    private void addSelectAllActionModeButton(ActionMode mode, Menu menu) {
        selectAll = true;
        selectAllActionModeButton = Utils.addSelectAllActionModeButton(getActivity().getLayoutInflater(), mode, menu);
        selectAllActionModeButton.setOnClickListener(new View.OnClickListener() {

            CapitalizedTextView selectAllView = (CapitalizedTextView) selectAllActionModeButton.findViewById(R.id.select_all);

            @Override
            public void onClick(View view) {
                if (selectAll) {
                    for (int position = 0; position < scratchProjectList.size(); position++) {
                        scratchProjectAdapter.addCheckedProject(position);
                    }
                    scratchProjectAdapter.notifyDataSetChanged();
                    onProjectChecked();
                    selectAll = false;
                    selectAllView.setText(R.string.deselect_all);
                } else {
                    scratchProjectAdapter.clearCheckedProjects();
                    scratchProjectAdapter.notifyDataSetChanged();
                    onProjectChecked();
                    selectAll = true;
                    selectAllView.setText(R.string.select_all);
                }
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    // Scratch Search Task Delegate Methods
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPreExecute() {
        Log.i(TAG, "onPreExecute for FetchScratchProjectsTask called");
        final ScratchSearchProjectsListFragment fragment = this;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Preconditions.checkNotNull(progressDialog, "No progress dialog set/initialized!");
                fragment.progressDialog.setMessage(fragment.getActivity().getResources().getString(R.string.search_progress));
                //fragment.progressDialog.show();
            }
        });
    }

    @Override
    public void onPostExecute(final ScratchSearchResult result) {
        Log.i(TAG, "onPostExecute for FetchScratchProjectsTask called");
        Preconditions.checkNotNull(progressDialog, "No progress dialog set/initialized!");
        Preconditions.checkNotNull(scratchProjectAdapter, "Scratch project adapter cannot be null!");

        final ScratchSearchProjectsListFragment fragment = this;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragment.progressDialog.hide();
                if (result == null || result.getProjectList() == null) {
                    ToastUtil.showError(fragment.getActivity(), "Unable to connect to server, please try later");
                    return;
                }
                if (result.getQuery() != null) {
                    fragment.scratchSearchResultCache.put(result.getQuery(), result);
                }
                fragment.scratchProjectAdapter.clear();
                for (ScratchProjectPreviewData projectData : result.getProjectList()) {
                    fragment.scratchProjectAdapter.add(projectData);
                    Log.d(TAG, projectData.getTitle());
                }
                fragment.scratchProjectAdapter.notifyDataSetChanged();
                fragment.searchResultsListView.setVisibility(View.VISIBLE);
            }
        });
    }

}
