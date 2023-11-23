/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.EmptyEventBrick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.UserDefinedBrick;
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick;
import org.catrobat.catroid.content.bricks.VisualPlacementBrick;
import org.catrobat.catroid.formulaeditor.InternToExternGenerator;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.io.asynctask.ProjectLoader;
import org.catrobat.catroid.io.asynctask.ProjectSaver;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ScriptFinder;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.UiUtils;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.controller.RecentBrickListManager;
import org.catrobat.catroid.ui.dragndrop.BrickListView;
import org.catrobat.catroid.ui.fragment.AddBrickFragment;
import org.catrobat.catroid.ui.fragment.BrickCategoryFragment;
import org.catrobat.catroid.ui.fragment.BrickCategoryFragment.OnCategorySelectedListener;
import org.catrobat.catroid.ui.fragment.BrickSearchFragment;
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory;
import org.catrobat.catroid.ui.fragment.UserDefinedBrickListFragment;
import org.catrobat.catroid.ui.recyclerview.adapter.BrickAdapter;
import org.catrobat.catroid.ui.recyclerview.backpack.BackpackActivity;
import org.catrobat.catroid.ui.recyclerview.controller.BrickController;
import org.catrobat.catroid.ui.recyclerview.controller.ScriptController;
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog;
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DuplicateInputTextWatcher;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;

import static org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME;
import static org.catrobat.catroid.common.Constants.UNDO_CODE_XML_FILE_NAME;

public class ScriptFragment extends ListFragment implements
		ActionMode.Callback,
		BrickAdapter.OnBrickClickListener,
		BrickAdapter.SelectionListener, OnCategorySelectedListener,
		AddBrickFragment.OnAddBrickListener,
		ProjectLoader.ProjectLoadListener {

	public static final String TAG = ScriptFragment.class.getSimpleName();
	private static final String BRICK_TAG = "brickToFocus";
	private static final String SCRIPT_TAG = "scriptToFocus";

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({NONE, BACKPACK, COPY, DELETE, COMMENT, CATBLOCKS})
	@interface ActionModeType {
	}

	private static final int NONE = 0;
	private static final int BACKPACK = 1;
	private static final int COPY = 2;
	private static final int DELETE = 3;
	private static final int COMMENT = 4;
	private static final int CATBLOCKS = 5;

	@ActionModeType
	private int actionModeType = NONE;

	private ActionMode actionMode;
	private BrickAdapter adapter;
	private BrickListView listView;
	private ScriptFinder scriptFinder;
	private String currentSceneName;
	private String currentSpriteName;
	private int undoBrickPosition;

	private ScriptController scriptController = new ScriptController();
	private BrickController brickController = new BrickController();

	private Parcelable savedListViewState;
	private Brick brickToFocus;
	private Script scriptToFocus;

	private SpriteActivity activity;

	public static ScriptFragment newInstance(Brick brickToFocus) {
		ScriptFragment scriptFragment = new ScriptFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(BRICK_TAG, brickToFocus);
		scriptFragment.setArguments(bundle);
		return scriptFragment;
	}

	public static ScriptFragment newInstance(Script scriptToFocus) {
		ScriptFragment scriptFragment = new ScriptFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(SCRIPT_TAG, scriptToFocus);
		scriptFragment.setArguments(bundle);
		return scriptFragment;
	}

	public ScriptFragment() {
		// required empty constructor
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle != null) {
			this.brickToFocus = (Brick) bundle.get(BRICK_TAG);
			this.scriptToFocus = (Script) bundle.get(SCRIPT_TAG);
		}
	}

	private List<UserVariable> savedUserVariables;
	private List<UserVariable> savedMultiplayerVariables;
	private List<UserList> savedUserLists;
	private transient List<UserVariable> savedLocalUserVariables;
	private transient List<UserList> savedLocalLists;

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);

		switch (actionModeType) {
			case BACKPACK:
				adapter.setCheckBoxMode(BrickAdapter.SCRIPTS_ONLY);
				mode.setTitle(getString(R.string.am_backpack));
				break;
			case COPY:
				adapter.setCheckBoxMode(BrickAdapter.CONNECTED_ONLY);
				mode.setTitle(getString(R.string.am_copy));
				break;
			case DELETE:
				adapter.setCheckBoxMode(BrickAdapter.ALL);
				mode.setTitle(getString(R.string.am_delete));
				break;
			case COMMENT:
				adapter.selectAllCommentedOutBricks();
				adapter.setCheckBoxMode(BrickAdapter.ALL);
				mode.setTitle(getString(R.string.comment_in_out));
				break;
			case NONE:
				adapter.setCheckBoxMode(NONE);
				actionMode.finish();
				return false;
			case CATBLOCKS:
				break;
		}
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
		BottomBar.showBottomBar(getActivity());
	}

	private void handleContextualAction() {
		if (adapter.isEmpty()) {
			actionMode.finish();
		}

		switch (actionModeType) {
			case BACKPACK:
				showNewScriptGroupAlert(adapter.getSelectedItems());
				break;
			case COPY:
				copy(adapter.getSelectedItems());
				break;
			case DELETE:
				showDeleteAlert(adapter.getSelectedItems());
				break;
			case COMMENT:
				toggleComments(adapter.getSelectedItems());
				break;
			case NONE:
				throw new IllegalStateException("ActionModeType not set correctly");
			case CATBLOCKS:
				break;
		}
	}

	private void resetActionModeParameters() {
		actionModeType = NONE;
		actionMode = null;
		adapter.setCheckBoxMode(BrickAdapter.NONE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.fragment_script, null);
		listView = view.findViewById(android.R.id.list);
		int bottomListPadding;
		if (BuildConfig.FEATURE_AI_ASSIST_ENABLED) {
			bottomListPadding = (int) (ScreenValues.SCREEN_HEIGHT / 2.5);
		} else {
			bottomListPadding = ScreenValues.SCREEN_HEIGHT / 3;
		}
		listView.setPadding(0, 0, 0, bottomListPadding);
		listView.setClipToPadding(false);

		activity = (SpriteActivity) getActivity();
		SettingsFragment.setToChosenLanguage(activity);

		scriptFinder = view.findViewById(R.id.findview);
		scriptFinder.setOnResultFoundListener((sceneIndex, spriteIndex, brickIndex, totalResults,
				textView
				) -> {
			Project currentProject = ProjectManager.getInstance().getCurrentProject();
			Scene currentScene = currentProject.getSceneList().get(sceneIndex);
			Sprite currentSprite = currentScene.getSpriteList().get(spriteIndex);

			textView.setText(createActionBarTitle(currentProject,
					currentScene,
					currentSprite));

			ProjectManager.getInstance().setCurrentSceneAndSprite(currentScene.getName(),
					currentSprite.getName());

			adapter.updateItems(currentSprite);
			adapter.notifyDataSetChanged();
			listView.smoothScrollToPosition(brickIndex);
			highlightBrickAtIndex(brickIndex);
			hideKeyboard();
		});

		scriptFinder.setOnCloseListener(() -> {
			listView.cancelHighlighting();
			finishActionMode();
			if (activity != null && !activity.isFinishing()) {
				activity.setCurrentSceneAndSprite(ProjectManager.getInstance().getCurrentlyEditedScene(),
						ProjectManager.getInstance().getCurrentSprite());
				activity.getSupportActionBar().setTitle(activity.createActionBarTitle());
				activity.addTabs();
			}
			activity.findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
		});

		scriptFinder.setOnOpenListener(() -> {
			activity.removeTabs();
			activity.findViewById(R.id.toolbar).setVisibility(View.GONE);
		});

		setHasOptionsMenu(true);
		return view;
	}

	public String createActionBarTitle(Project currentProject, Scene currentScene, Sprite currentSprite) {
		if (currentProject.getSceneList().size() == 1) {
			return currentSprite.getName();
		} else {
			return currentScene.getName() + ": " + currentSprite.getName();
		}
	}

	private void highlightBrickAtIndex(int index) {
		listView.getBrickPositionsToHighlight().clear();
		listView.getBrickPositionsToHighlight().add(index);
	}

	private void hideKeyboard() {
		InputMethodManager imm =
				(InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (scriptFinder.isOpen() && activity != null) {
			activity.findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		currentProject.getBroadcastMessageContainer().update();

		adapter = new BrickAdapter(ProjectManager.getInstance().getCurrentSprite());
		adapter.setSelectionListener(this);
		adapter.setOnItemClickListener(this);

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(adapter);
		listView.setOnItemLongClickListener(adapter);

		if (currentSprite.equals(currentScene.getBackgroundSprite())) {
			InternToExternGenerator.setInternExternLanguageConverterMap(Sensors.OBJECT_NUMBER_OF_LOOKS,
					R.string.formula_editor_object_number_of_backgrounds);
		} else {
			InternToExternGenerator.setInternExternLanguageConverterMap(Sensors.OBJECT_NUMBER_OF_LOOKS,
					R.string.formula_editor_object_number_of_looks);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		Project project = ProjectManager.getInstance().getCurrentProject();
		Scene scene = ProjectManager.getInstance().getCurrentlyEditedScene();
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();

		ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

		if (project.getSceneList().size() > 1) {
			actionBar.setTitle(scene.getName() + ": " + sprite.getName());
		} else {
			actionBar.setTitle(sprite.getName());
		}

		if (BackpackListManager.getInstance().isBackpackEmpty()) {
			BackpackListManager.getInstance().loadBackpack();
		}

		BottomBar.showBottomBar(getActivity());
		BottomBar.showPlayButton(getActivity());
		BottomBar.showAddButton(getActivity());

		if (BuildConfig.FEATURE_AI_ASSIST_ENABLED) {
			BottomBar.showAiAssistButton(getActivity());
		}

		adapter.updateItems(ProjectManager.getInstance().getCurrentSprite());

		if (savedListViewState != null) {
			listView.onRestoreInstanceState(savedListViewState);
		}

		scrollToFocusItem();
		SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_scripts);
	}

	@Override
	public void onPause() {
		super.onPause();
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		new ProjectSaver(currentProject, getContext()).saveProjectAsync();

		savedListViewState = listView.onSaveInstanceState();

		((SpriteActivity) getActivity()).setUndoMenuItemVisibility(false);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.show_details).setVisible(false);
		menu.findItem(R.id.rename).setVisible(false);
		menu.findItem(R.id.catblocks_reorder_scripts).setVisible(false);
		menu.findItem(R.id.find).setVisible(true);
		if (!BuildConfig.FEATURE_CATBLOCKS_ENABLED) {
			menu.findItem(R.id.catblocks).setVisible(false);
		}
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (listView.isCurrentlyMoving()) {
			listView.highlightMovingItem();
			return true;
		}
		if (listView.isCurrentlyHighlighted()) {
			listView.cancelHighlighting();
		}
		switch (item.getItemId()) {
			case R.id.menu_undo:
				loadProjectAfterUndoOption();
				break;
			case R.id.backpack:
				prepareActionMode(BACKPACK);
				break;
			case R.id.copy:
				prepareActionMode(COPY);
				break;
			case R.id.delete:
				prepareActionMode(DELETE);
				break;
			case R.id.comment_in_out:
				prepareActionMode(COMMENT);
				break;
			case R.id.catblocks:
				switchToCatblocks();
				break;
			case R.id.find:
				scriptFinder.open();
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	public void notifyDataSetChanged() {
		adapter.notifyDataSetChanged();
	}

	public boolean isCurrentlyMoving() {
		if (listView != null) {
			return listView.isCurrentlyMoving();
		}
		return false;
	}

	public void highlightMovingItem() {
		listView.highlightMovingItem();
	}

	public void cancelMove() {
		listView.cancelMove();
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		adapter.updateItems(sprite);
	}

	public boolean isCurrentlyHighlighted() {
		return listView.isCurrentlyHighlighted();
	}

	public void cancelHighlighting() {
		listView.cancelHighlighting();
	}

	private void showCategoryFragment() {
		BrickCategoryFragment brickCategoryFragment = new BrickCategoryFragment();
		brickCategoryFragment.setOnCategorySelectedListener(this);

		getFragmentManager().beginTransaction()
				.add(R.id.fragment_container, brickCategoryFragment, BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG)
				.addToBackStack(BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG)
				.commit();
	}

	@Override
	public void onCategorySelected(String category) {
		ListFragment fragment = null;
		String tag = "";
		Fragment currentFragment = getParentFragmentManager().findFragmentById(R.id.fragment_container);
		if (category.equals(getContext().getString(R.string.category_user_bricks))) {
			fragment = UserDefinedBrickListFragment.newInstance(this);
			tag = UserDefinedBrickListFragment.USER_DEFINED_BRICK_LIST_FRAGMENT_TAG;
		} else if (currentFragment instanceof AddBrickFragment || category.equals(getContext().getString(R.string.category_search_bricks))) {
			fragment = BrickSearchFragment.newInstance(this, category);
			tag = BrickSearchFragment.BRICK_SEARCH_FRAGMENT_TAG;
		} else {
			fragment = AddBrickFragment.newInstance(category, this);
			tag = AddBrickFragment.ADD_BRICK_FRAGMENT_TAG;
		}

		getFragmentManager().beginTransaction()
				.add(R.id.fragment_container, fragment, tag)
				.addToBackStack(null)
				.commit();
	}

	protected void prepareActionMode(@ActionModeType int type) {
		if (type == BACKPACK) {
			if (BackpackListManager.getInstance().getBackpackedScriptGroups().isEmpty()) {
				startActionMode(BACKPACK);
			} else if (adapter.isEmpty()) {
				switchToBackpack();
			} else {
				showBackpackModeChooser();
			}
		} else {
			startActionMode(type);
		}
	}

	private void startActionMode(@ActionModeType int type) {
		if (adapter.isEmpty()) {
			ToastUtil.showError(getActivity(), R.string.am_empty_list);
		} else {
			actionModeType = type;
			actionMode = getActivity().startActionMode(this);
			BottomBar.hideBottomBar(getActivity());
		}
	}

	@Override
	public void onSelectionChanged(int selectedItemCnt) {
		switch (actionModeType) {
			case BACKPACK:
				actionMode.setTitle(getString(R.string.am_backpack) + " " + selectedItemCnt);
				break;
			case COPY:
				actionMode.setTitle(getString(R.string.am_copy) + " " + selectedItemCnt);
				break;
			case DELETE:
				actionMode.setTitle(getString(R.string.am_delete) + " " + selectedItemCnt);
				break;
			case COMMENT:
				actionMode.setTitle(getString(R.string.comment_in_out) + " " + selectedItemCnt);
				break;
			case NONE:
				throw new IllegalStateException("ActionModeType not set Correctly");
			case CATBLOCKS:
				break;
		}
	}

	public void finishActionMode() {
		adapter.clearSelection();
		if (actionModeType != NONE) {
			actionMode.finish();
		}
	}

	public Brick findBrickByHash(int hashCode) {
		return adapter.findByHash(hashCode);
	}

	public void handleAddButton() {
		if (listView.isCurrentlyHighlighted()) {
			listView.cancelHighlighting();
		}
		if (listView.isCurrentlyMoving()) {
			listView.highlightMovingItem();
		} else {
			((SpriteActivity) getActivity()).setUndoMenuItemVisibility(false);
			showCategoryFragment();
		}
	}

	public void addBrick(Brick brick) {
		try {
			if (!brick.getClass().equals(UserDefinedReceiverBrick.class) && !brick.getClass().equals(UserDefinedBrick.class)) {
				RecentBrickListManager.getInstance().addBrick(brick.clone());
			}
		} catch (CloneNotSupportedException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		addBrick(brick, sprite, adapter, listView);
	}

	@VisibleForTesting
	public void addBrick(Brick brick, Sprite sprite, BrickAdapter brickAdapter, BrickListView brickListView) {
		if (brickAdapter.getCount() == 0) {
			if (brick instanceof ScriptBrick) {
				sprite.addScript(brick.getScript());
			} else {
				Script script = new StartScript();
				script.addBrick(brick);
				sprite.addScript(script);
			}
			brickAdapter.updateItems(sprite);
		} else if (brickAdapter.getCount() == 1 && !(brick instanceof ScriptBrick)) {
			sprite.getScriptList().get(0).addBrick(brick);
			brickAdapter.updateItems(sprite);
		} else {
			int firstVisibleBrick = brickListView.getFirstVisiblePosition();
			int lastVisibleBrick = brickListView.getLastVisiblePosition();
			int position = (1 + lastVisibleBrick - firstVisibleBrick) / 2;
			position += firstVisibleBrick;
			brickAdapter.addItem(position, brick);
			brickListView.startMoving(brick);
		}
	}

	@Override
	public void onBrickClick(Brick brick, int position) {
		if (listView.isCurrentlyHighlighted()) {
			listView.cancelHighlighting();
			return;
		}

		List<Integer> options = getContextMenuItems(brick);
		List<String> names = new ArrayList<>();
		for (Integer option: options) {
			names.add(getString(option));
		}

		ListAdapter arrayAdapter = UiUtils.getAlertDialogAdapterForMenuIcons(options, names,
				requireContext(), requireActivity());

		View brickView = brick.getView(getContext());
		brick.disableSpinners();

		new AlertDialog.Builder(getContext())
			.setCustomTitle(brickView)
			.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					handleContextMenuItemClick(options.get(which), brick, position);
				}
			}).show();
	}

	@VisibleForTesting
	public static List<Integer> getContextMenuItems(Brick brick) {
		List<Integer> items = new ArrayList<>();

		if (brick instanceof UserDefinedReceiverBrick) {
			items.add(R.string.backpack_add);
			items.add(R.string.brick_context_dialog_delete_definition);
			items.add(R.string.brick_context_dialog_move_definition);
			items.add(R.string.brick_context_dialog_help);
			return items;
		}

		if (brick instanceof ScriptBrick) {
			items.add(R.string.backpack_add);

			if (!(brick instanceof EmptyEventBrick)) {
				items.add(brick.isCommentedOut()
						? R.string.brick_context_dialog_comment_in_script
						: R.string.brick_context_dialog_comment_out_script);
			}

			items.add(R.string.brick_context_dialog_copy_script);

			items.add(R.string.brick_context_dialog_delete_script);

			if (brick instanceof FormulaBrick && ((FormulaBrick) brick).hasEditableFormulaField()) {
				items.add(R.string.brick_context_dialog_formula_edit_brick);
			}
			items.add(R.string.brick_context_dialog_move_script);

			items.add(R.string.brick_context_dialog_help);
		} else {
			items.add(R.string.brick_context_dialog_copy_brick);
			if (brick.consistsOfMultipleParts()) {
				items.add(R.string.brick_context_dialog_highlight_brick_parts);
			}
			items.add(R.string.brick_context_dialog_delete_brick);

			items.add(brick.isCommentedOut()
					? R.string.brick_context_dialog_comment_in
					: R.string.brick_context_dialog_comment_out);
			if (brick instanceof VisualPlacementBrick && ((VisualPlacementBrick) brick).areAllBrickFieldsNumbers()) {
				items.add(R.string.brick_option_place_visually);
			}
			if (brick instanceof FormulaBrick && ((FormulaBrick) brick).hasEditableFormulaField()) {
				items.add(R.string.brick_context_dialog_formula_edit_brick);
			}
			if (brick.equals(brick.getAllParts().get(0))) {
				items.add(R.string.brick_context_dialog_move_brick);
			}

			if (brick.hasHelpPage()) {
				items.add(R.string.brick_context_dialog_help);
			}
		}
		return items;
	}

	private void handleContextMenuItemClick(int itemId, Brick brick, int position) {
		showUndo(false);
		switch (itemId) {
			case R.string.backpack_add:
				List<Brick> bricksToPack = new ArrayList<>();
				brick.addToFlatList(bricksToPack);
				showNewScriptGroupAlert(bricksToPack);
				break;
			case R.string.brick_context_dialog_copy_brick:
			case R.string.brick_context_dialog_copy_script:
				try {
					Brick clonedBrick = brick.getAllParts().get(0).clone();
					adapter.addItem(position, clonedBrick);
					listView.startMoving(clonedBrick);
				} catch (CloneNotSupportedException e) {
					ToastUtil.showError(getContext(), R.string.error_copying_brick);
					Log.e(TAG, Log.getStackTraceString(e));
				}
				break;
			case R.string.brick_context_dialog_delete_brick:
			case R.string.brick_context_dialog_delete_script:
				showDeleteAlert(brick.getAllParts());
				break;
			case R.string.brick_context_dialog_delete_definition:
				showDeleteAlert(brick.getAllParts());
				break;
			case R.string.brick_context_dialog_comment_in:
			case R.string.brick_context_dialog_comment_in_script:
				for (Brick brickPart : brick.getAllParts()) {
					brickPart.setCommentedOut(false);
				}
				adapter.notifyDataSetChanged();
				break;
			case R.string.brick_context_dialog_comment_out:
			case R.string.brick_context_dialog_comment_out_script:
				for (Brick brickPart : brick.getAllParts()) {
					brickPart.setCommentedOut(true);
				}
				adapter.notifyDataSetChanged();
				break;
			case R.string.brick_option_place_visually:
				VisualPlacementBrick visualPlacementBrick = (VisualPlacementBrick) brick;
				visualPlacementBrick.placeVisually(visualPlacementBrick.getXBrickField(),
						visualPlacementBrick.getYBrickField());
				break;
			case R.string.brick_context_dialog_formula_edit_brick:
				((FormulaBrick) brick).onClick(listView);
				break;
			case R.string.brick_context_dialog_move_brick:
			case R.string.brick_context_dialog_move_script:
			case R.string.brick_context_dialog_move_definition:
				onBrickLongClick(brick, position);
				break;
			case R.string.brick_context_dialog_help:
				openWebViewWithHelpPage(brick);
				break;
			case R.string.brick_context_dialog_highlight_brick_parts:
				List<Brick> bricksOfControlStructure = brick.getAllParts();
				List<Integer> positions = new ArrayList<>();
				for (Brick brickInControlStructure : bricksOfControlStructure) {
					positions.add(adapter.getPosition(brickInControlStructure));
				}
				listView.highlightControlStructureBricks(positions);
				break;
		}
	}

	private void openWebViewWithHelpPage(Brick brick) {
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		Sprite backgroundSprite = ProjectManager.getInstance().getCurrentlyEditedScene().getBackgroundSprite();
		String category = new CategoryBricksFactory().getBrickCategory(brick, sprite == backgroundSprite, getContext());

		String brickHelpUrl = brick.getHelpUrl(category);
		Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse(brickHelpUrl));
		startActivity(intent);
	}

	@Override
	public boolean onBrickLongClick(Brick brick, int position) {
		showUndo(false);
		if (listView.isCurrentlyHighlighted()) {
			listView.cancelHighlighting();
		} else {
			listView.startMoving(brick);
		}
		return true;
	}

	private void showBackpackModeChooser() {
		CharSequence[] items = new CharSequence[] {getString(R.string.pack), getString(R.string.unpack)};
		new AlertDialog.Builder(getContext())
				.setTitle(R.string.backpack_title)
				.setItems(items, (dialog, which) -> {
					switch (which) {
						case 0:
							startActionMode(BACKPACK);
							break;
						case 1:
							switchToBackpack();
					}
				})
				.show();
	}

	public void showNewScriptGroupAlert(List<Brick> selectedBricks) {
		TextInputDialog.Builder builder = new TextInputDialog.Builder(getContext());
		DuplicateInputTextWatcher duplicateInputTextwatcher = new DuplicateInputTextWatcher(null);
		duplicateInputTextwatcher.setScope(BackpackListManager.getInstance().getBackpackedScriptGroups());
		builder.setText(new UniqueNameProvider().getUniqueName(getString(R.string.default_script_group_name), BackpackListManager.getInstance().getBackpackedScriptGroups()));

		builder.setHint(getString(R.string.script_group_label))
				.setTextWatcher(duplicateInputTextwatcher)
				.setPositiveButton(getString(R.string.ok), (TextInputDialog.OnClickListener) (dialog, textInput) -> pack(textInput, selectedBricks));

		builder.setTitle(R.string.new_group)
				.setNegativeButton(R.string.cancel, null)
				.show();
	}

	public void pack(String name, List<Brick> selectedBricks) {
		try {
			scriptController.pack(name, selectedBricks);
			ToastUtil.showSuccess(getActivity(), getString(R.string.packed_script_group));
			switchToBackpack();
		} catch (CloneNotSupportedException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}

		finishActionMode();
	}

	private void switchToBackpack() {
		Intent intent = new Intent(getActivity(), BackpackActivity.class);
		intent.putExtra(BackpackActivity.EXTRA_FRAGMENT_POSITION, BackpackActivity.FRAGMENT_SCRIPTS);
		startActivity(intent);
	}

	private void switchToCatblocks() {
		if (!BuildConfig.FEATURE_CATBLOCKS_ENABLED) {
			return;
		}

		int firstVisible = listView.getFirstVisiblePosition();
		UUID firstVisibleBrickID = null;
		if (listView.getCount() > 0 && firstVisible >= 0) {
			Object firstVisibleObject = listView.getItemAtPosition(firstVisible);
			if (firstVisibleObject instanceof Brick) {
				Brick firstVisibleBrick = (Brick) firstVisibleObject;
				if (firstVisibleBrick instanceof ScriptBrick) {
					firstVisibleBrickID = firstVisibleBrick.getScript().getScriptId();
				} else {
					firstVisibleBrickID = firstVisibleBrick.getBrickID();
				}
			}
		}

		SettingsFragment.setUseCatBlocks(getContext(), true);

		CatblocksScriptFragment catblocksFragment = new CatblocksScriptFragment(firstVisibleBrickID);

		FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.fragment_container, catblocksFragment,
				CatblocksScriptFragment.Companion.getTAG());
		fragmentTransaction.commit();
	}

	private void copy(List<Brick> selectedBricks) {
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		brickController.copy(selectedBricks, sprite);
		adapter.updateItems(sprite);
		finishActionMode();
	}

	private void showDeleteAlert(List<Brick> selectedBricks) {
		if (selectedBricks.size() > 0 && copyProjectForUndoOption()) {
			showUndo(true);
			undoBrickPosition = adapter.getPosition(selectedBricks.get(0));
		}
		delete(selectedBricks);
	}

	private void delete(List<Brick> selectedItems) {
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		brickController.delete(selectedItems, sprite);
		adapter.updateItems(sprite);
		finishActionMode();
	}

	private void toggleComments(List<Brick> selectedBricks) {
		for (Brick brick : adapter.getItems()) {
			brick.setCommentedOut(selectedBricks.contains(brick));
		}
		finishActionMode();
	}

	public void setUndoBrickPosition(Brick brick) {
		undoBrickPosition = adapter.getPosition(brick);
	}

	public boolean copyProjectForUndoOption() {
		ProjectManager projectManager = ProjectManager.getInstance();
		Sprite currentSprite = projectManager.getCurrentSprite();
		currentSpriteName = currentSprite.getName();
		currentSceneName = projectManager.getCurrentlyEditedScene().getName();
		Project project = projectManager.getCurrentProject();
		XstreamSerializer.getInstance().saveProject(project);
		File currentCodeFile = new File(project.getDirectory(), CODE_XML_FILE_NAME);
		File undoCodeFile = new File(project.getDirectory(), UNDO_CODE_XML_FILE_NAME);

		if (currentCodeFile.exists()) {
			try {
				StorageOperations.transferData(currentCodeFile, undoCodeFile);
				saveVariables();
				return true;
			} catch (IOException exception) {
				Log.e(TAG, "Copying project " + project.getName() + " failed.", exception);
			}
		}
		return false;
	}

	public void loadProjectAfterUndoOption() {
		Project project = ProjectManager.getInstance().getCurrentProject();
		File currentCodeFile = new File(project.getDirectory(), CODE_XML_FILE_NAME);
		File undoCodeFile = new File(project.getDirectory(), UNDO_CODE_XML_FILE_NAME);

		if (currentCodeFile.exists()) {
			try {
				StorageOperations.transferData(undoCodeFile, currentCodeFile);
				new ProjectLoader(project.getDirectory(), getContext()).setListener(this).loadProjectAsync();
			} catch (IOException exception) {
				Log.e(TAG, "Replaceing project " + project.getName() + " failed.", exception);
			}
		}
	}

	@Override
	public void onLoadFinished(boolean success) {
		ProjectManager.getInstance().setCurrentSceneAndSprite(currentSceneName, currentSpriteName);
		if (checkVariables()) {
			loadVariables();
		}
		refreshFragmentAfterUndo();
	}

	private void saveVariables() {
		ProjectManager projectManager = ProjectManager.getInstance();
		Sprite currentSprite = projectManager.getCurrentSprite();
		Project project = projectManager.getCurrentProject();

		savedUserVariables = project.getUserVariablesCopy();
		savedMultiplayerVariables = project.getMultiplayerVariablesCopy();
		savedUserLists = project.getUserListsCopy();
		savedLocalUserVariables = currentSprite.getUserVariablesCopy();
		savedLocalLists = currentSprite.getUserListsCopy();
	}

	public boolean checkVariables() {
		ProjectManager projectManager = ProjectManager.getInstance();
		Sprite currentSprite = projectManager.getCurrentSprite();
		Project project = projectManager.getCurrentProject();

		return (project.hasUserDataChanged(project.getUserVariables(), savedUserVariables)
				|| project.hasUserDataChanged(project.getMultiplayerVariables(), savedMultiplayerVariables)
				|| project.hasUserDataChanged(project.getUserLists(), savedUserLists)
				|| currentSprite.hasUserDataChanged(currentSprite.getUserVariables(), savedLocalUserVariables)
				|| currentSprite.hasUserDataChanged(currentSprite.getUserLists(), savedLocalLists));
	}

	private void loadVariables() {
		ProjectManager projectManager = ProjectManager.getInstance();
		Sprite currentSprite = projectManager.getCurrentSprite();
		Project project = projectManager.getCurrentProject();

		project.restoreUserDataValues(project.getUserVariables(), savedUserVariables);
		project.restoreUserDataValues(project.getMultiplayerVariables(), savedMultiplayerVariables);
		project.restoreUserDataValues(project.getUserLists(), savedUserLists);
		currentSprite.restoreUserDataValues(currentSprite.getUserVariables(), savedLocalUserVariables);
		currentSprite.restoreUserDataValues(currentSprite.getUserLists(), savedLocalLists);
	}

	private void refreshFragmentAfterUndo() {
		Fragment scriptFragment = getActivity().getSupportFragmentManager().findFragmentByTag(TAG);
		final FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
		fragmentTransaction.detach(scriptFragment);
		fragmentTransaction.attach(scriptFragment);
		fragmentTransaction.commit();
		if (undoBrickPosition < listView.getFirstVisiblePosition() || undoBrickPosition > listView.getLastVisiblePosition()) {
			listView.post(() -> listView.setSelection(undoBrickPosition));
		}
	}

	public void showUndo(boolean visible) {
		SpriteActivity activity = (SpriteActivity) getActivity();
		if (activity != null) {
			((SpriteActivity) getActivity()).showUndo(visible);
		}
	}

	private void scrollToFocusItem() {
		if (scriptToFocus == null && brickToFocus == null) {
			return;
		}

		int scrollToIndex = -1;
		for (int i = 0; i < listView.getAdapter().getCount(); ++i) {
			Object item = listView.getItemAtPosition(i);
			if (!(item instanceof Brick)) {
				continue;
			}
			Brick brick = (Brick) item;
			if ((brickToFocus != null && brick == brickToFocus)
					|| (scriptToFocus != null && brick.getScript() == scriptToFocus)) {
				scrollToIndex = i;
				break;
			}
		}
		if (scrollToIndex == -1) {
			return;
		}
		if (getActivity() != null) {
			int finalScrollToIndex = scrollToIndex;
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					listView.setSelection(finalScrollToIndex);
				}
			});
		}
		scriptToFocus = null;
		brickToFocus = null;
	}

	public int getActionModeType() {
		return actionModeType;
	}

	public boolean isFinderOpen() {
		return scriptFinder.isOpen();
	}

	public void closeFinder() {
		if (!scriptFinder.isClosed()) {
			scriptFinder.close();
		}
	}
}
