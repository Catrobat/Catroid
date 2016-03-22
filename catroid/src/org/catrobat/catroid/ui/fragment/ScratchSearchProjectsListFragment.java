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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.util.LruCache;
import android.util.TypedValue;
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
import org.catrobat.catroid.common.ScratchProjectData;
import org.catrobat.catroid.common.ScratchSearchResult;
import org.catrobat.catroid.transfers.FetchScratchProjectsTask;
import org.catrobat.catroid.ui.CapitalizedTextView;
import org.catrobat.catroid.ui.ScratchConverterActivity;
import org.catrobat.catroid.ui.adapter.ScratchProjectAdapter;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ScratchSearchProjectsListFragment extends Fragment
        implements FetchScratchProjectsTask.ScratchProjectListTaskDelegate, ScratchProjectAdapter.OnScratchProjectEditListener {

    private static final String BUNDLE_ARGUMENTS_SCRATCH_PROJECT_DATA = "scratch_project_data";
    private static final String SHARED_PREFERENCE_NAME = "showDetailsScratchProjects";
    private static final String TAG = ScratchSearchProjectsListFragment.class.getSimpleName();

    private static String convertActionModeTitle;

    private SearchView searchView;
    private ImageButton audioButton;
    private ListView searchResultsListView;
    private ProgressDialog progressDialog;
    private List<ScratchProjectData> scratchProjectList;
    private ScratchProjectData scratchProjectToEdit;
    private LruCache<String, ScratchSearchResult> scratchSearchResultCache;
    private ScratchProjectAdapter scratchProjectAdapter;
    private ActionMode actionMode;
    private View selectAllActionModeButton;
    private boolean selectAll = true;
    private boolean actionModeActive = false;

    private void setSearchResultsListViewMargin(int leftDP, int topDP, int rightDP, int bottomDP) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)searchResultsListView.getLayoutParams();
        Resources r = getResources();
        float leftPX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDP, r.getDisplayMetrics());
        float topPX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, topDP, r.getDisplayMetrics());
        float rightPX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDP, r.getDisplayMetrics());
        float bottomPX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bottomDP, r.getDisplayMetrics());
        params.setMargins((int)leftPX, (int)topPX, (int)rightPX, (int)bottomPX);
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
            actionModeActive = true;
            convertActionModeTitle = getString(R.string.convert);
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
                showConfirmConvertDialog();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
        scratchSearchResultCache = new LruCache<>(Constants.SCRATCH_SEARCH_RESULT_CACHE_SIZE);
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

                // TODO: consider pagination for cache!
                ScratchSearchResult cachedResult = scratchSearchResultCache.get(newText);
                if (cachedResult != null) {
                    Log.d(TAG, "Cache hit!");
                    onPostExecute(cachedResult);
                    return false;
                }

                // cache miss
                Log.d(TAG, "Cache miss!");
                new FetchScratchProjectsTask().setDelegate(fragment).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, newText);
                return false;
            }
        });
        // TODO: >> powered by "https://www.google.com/uds/css/small-logo.png" Custom Search << (grey)
        rootView.clearFocus();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(searchResultsListView);
        if (savedInstanceState != null) {
            scratchProjectToEdit = (ScratchProjectData) savedInstanceState.getSerializable(BUNDLE_ARGUMENTS_SCRATCH_PROJECT_DATA);
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

    /*public boolean getActionModeActive() { return actionModeActive; }*/

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
                this.getActivity();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onProjectChecked() {
        Log.d(TAG, "Project checked!");
        /*
        if (scratchProjectAdapter.getSelectMode() == ListView.CHOICE_MODE_SINGLE || actionMode == null) {
            return;
        }
        */
    }

    @Override
    public void onProjectEdit(int position) {
        // TODO: use this after project has been converted!
        Intent intent = new Intent(getActivity(), ScratchConverterActivity.class);
        intent.putExtra(Constants.PROJECTNAME_TO_LOAD, scratchProjectAdapter.getItem(position).getTitle());
        intent.putExtra(Constants.PROJECT_OPENED_FROM_PROJECTS_LIST, true);
        getActivity().startActivity(intent);
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

    private void showConfirmConvertDialog() {
        int titleId;
        if (scratchProjectAdapter.getAmountOfCheckedProjects() == 1) {
            titleId = R.string.dialog_confirm_delete_program_title;
        } else {
            titleId = R.string.dialog_confirm_delete_multiple_programs_title;
        }

        AlertDialog.Builder builder = new CustomAlertDialogBuilder(getActivity());
        builder.setTitle(titleId);
        builder.setMessage(R.string.dialog_confirm_delete_program_message);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                convertCheckedProjects();
                clearCheckedProjectsAndEnableButtons();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                clearCheckedProjectsAndEnableButtons();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void convertProjects(List<ScratchProjectData> projectList) {
        // TODO: create websocket connection and set up receiver
        Log.i(TAG, "Converting projects:");
        for (ScratchProjectData projectData : projectList) {
            Log.i(TAG, projectData.getTitle());
            //convertProject(scratchProjectToEdit);
            // TODO: send convert command
        }
    }

    private void convertCheckedProjects() {
        int numConverted = 0;
        ArrayList<ScratchProjectData> projectsToConvert = new ArrayList<>();
        for (int position : scratchProjectAdapter.getCheckedProjects()) {
            scratchProjectToEdit = (ScratchProjectData) searchResultsListView.getItemAtPosition(position - numConverted);
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
        actionModeActive = false;
        searchView.setVisibility(View.VISIBLE);
        audioButton.setVisibility(View.VISIBLE);
        setSearchResultsListViewMargin(0, 15, 0, 0);
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
        Preconditions.checkNotNull(progressDialog, "No progress dialog set/initialized!");
        progressDialog.setMessage(getActivity().getResources().getString(R.string.search_progress));
        //progressDialog.show();
    }

    @Override
    public void onPostExecute(ScratchSearchResult result) {
        Log.i(TAG, "onPostExecute for FetchScratchProjectsTask called");
        Preconditions.checkNotNull(progressDialog, "No progress dialog set/initialized!");
        Preconditions.checkNotNull(scratchProjectAdapter, "Scratch project adapter cannot be null!");
        progressDialog.hide();
        if (result == null || result.getProjectList() == null) {
            ToastUtil.showError(getActivity(), "Unable to connect to server, please try later");
            return;
        }
        if (result.getQuery() != null) {
            scratchSearchResultCache.put(result.getQuery(), result);
        }
        scratchProjectAdapter.clear();
        for (ScratchProjectData projectData : result.getProjectList()) {
            scratchProjectAdapter.add(projectData);
            Log.d(TAG, projectData.getTitle());
        }
        scratchProjectAdapter.notifyDataSetChanged();
        searchResultsListView.setVisibility(View.VISIBLE);
    }

}
