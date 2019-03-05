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
package org.catrobat.catroid.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.ControlStructureBrick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.dragndrop.BrickListView;
import org.catrobat.catroid.ui.dragndrop.DragAndDropInterface;
import org.catrobat.catroid.ui.fragment.BrickCategoryFragment.OnCategorySelectedListener;
import org.catrobat.catroid.ui.recyclerview.adapter.BrickAdapter;
import org.catrobat.catroid.ui.recyclerview.backpack.BackpackActivity;
import org.catrobat.catroid.ui.recyclerview.controller.BrickController;
import org.catrobat.catroid.ui.recyclerview.controller.ScriptController;
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog;
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.UniqueStringTextWatcher;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.ToastUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ScriptFragment extends ListFragment implements
		ActionMode.Callback,
		DragAndDropInterface,
		BrickAdapter.OnItemClickListener,
		BrickAdapter.SelectionListener, OnCategorySelectedListener {

	public static final String TAG = ScriptFragment.class.getSimpleName();

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({NONE, BACKPACK, COPY, DELETE, COMMENT})
	@interface ActionModeType {}
	private static final int NONE = 0;
	private static final int BACKPACK = 1;
	private static final int COPY = 2;
	private static final int DELETE = 3;
	private static final int COMMENT = 4;

	@ActionModeType
	private int actionModeType = NONE;

	private ActionMode actionMode;
	private BrickAdapter adapter;
	private BrickListView listView;

	private ScriptController scriptController = new ScriptController();
	private BrickController brickController = new BrickController();

	private Parcelable savedListViewState;

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);

		switch (actionModeType) {
			case BACKPACK:
				adapter.checkBoxMode = BrickAdapter.SCRIPTS_ONLY;
				mode.setTitle(getString(R.string.am_backpack));
				break;
			case COPY:
				adapter.checkBoxMode = BrickAdapter.ALL;
				mode.setTitle(getString(R.string.am_copy));
				break;
			case DELETE:
				adapter.checkBoxMode = BrickAdapter.ALL;
				mode.setTitle(getString(R.string.am_delete));
				break;
			case COMMENT:
				adapter.selectAllCommentedOutBricks();
				adapter.checkBoxMode = BrickAdapter.ALL;
				mode.setTitle(getString(R.string.comment_in_out));
				break;
			case NONE:
				adapter.checkBoxMode = NONE;
				actionMode.finish();
				return false;
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
		}
	}

	private void resetActionModeParameters() {
		actionModeType = NONE;
		actionMode = null;
		adapter.checkBoxMode = BrickAdapter.NONE;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.fragment_script, null);
		listView = view.findViewById(android.R.id.list);
		setHasOptionsMenu(true);
		SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_scripts);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		currentProject.getBroadcastMessageContainer().update();

		adapter = new BrickAdapter(ProjectManager.getInstance().getCurrentSprite());
		adapter.setSelectionListener(this);
		adapter.setOnItemClickListener(this);

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(adapter);
		listView.setOnItemLongClickListener(adapter);
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

		adapter.updateItems(ProjectManager.getInstance().getCurrentSprite());

		if (savedListViewState != null) {
			listView.onRestoreInstanceState(savedListViewState);
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		savedListViewState = listView.onSaveInstanceState();

		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentlyEditedScene() != null) {
			projectManager.saveProject(getActivity().getApplicationContext());
		}
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.show_details).setVisible(false);
		menu.findItem(R.id.rename).setVisible(false);
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
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	public void notifyDataSetChanged() {
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean isCurrentlyMoving() {
		return listView.isCurrentlyMoving();
	}

	@Override
	public void highlightMovingItem() {
		listView.highlightMovingItem();
	}

	@Override
	public void startMoving(List<BrickBaseType> bricksToMove, int position) {
		listView.startMoving(bricksToMove, position);
	}

	@Override
	public void stopMoving() {
		listView.stopMoving();
	}

	@Override
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

		SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_category);
	}

	@Override
	public void onCategorySelected(String category) {
		AddBrickFragment addBrickFragment = AddBrickFragment.newInstance(category, this);

		getFragmentManager().beginTransaction()
				.add(R.id.fragment_container, addBrickFragment, AddBrickFragment.ADD_BRICK_FRAGMENT_TAG)
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
				actionMode.setTitle(getResources()
						.getQuantityString(R.plurals.am_pack_scripts_title, selectedItemCnt, selectedItemCnt));
				break;
			case COPY:
				actionMode.setTitle(getResources()
						.getQuantityString(R.plurals.am_copy_scripts_title, selectedItemCnt, selectedItemCnt));
				break;
			case DELETE:
				actionMode.setTitle(getResources()
						.getQuantityString(R.plurals.am_delete_bricks_title, selectedItemCnt, selectedItemCnt));
				break;
			case COMMENT:
				break;
			case NONE:
			default:
				throw new IllegalStateException("ActionModeType not set correctly");
		}
	}

	public void finishActionMode() {
		adapter.clearSelection();
		if (actionModeType != NONE) {
			actionMode.finish();
		}
	}

	public BrickBaseType findBrickByHash(int hashCode) {
		return adapter.findByHash(hashCode);
	}

	public void handleAddButton() {
		if (listView.isCurrentlyHighlighted()) {
			listView.cancelHighlighting();
		}
		if (listView.isCurrentlyMoving()) {
			listView.highlightMovingItem();
		} else {
			showCategoryFragment();
		}
	}

	public void addBrick(Brick brick) {
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();

		List<BrickBaseType> bricksToAdd = new ArrayList<>();
		for (Brick initializedBrick : brickController.initialize(brick)) {
			bricksToAdd.add((BrickBaseType) initializedBrick);
		}

		if (adapter.getCount() == 0) {
			if (brick instanceof ScriptBrick) {
				sprite.addScript(((ScriptBrick) brick).getScript());
			} else {
				Script script = new StartScript();
				script.addBricks(new ArrayList<Brick>(bricksToAdd));
				sprite.addScript(script);
			}
			adapter.updateItems(sprite);
		} else if (adapter.getCount() == 1) {
			if (brick instanceof ScriptBrick) {
				sprite.addScript(0, ((ScriptBrick) brick).getScript());
			} else {
				sprite.getScriptList().get(0).addBricks(new ArrayList<Brick>(bricksToAdd));
			}
			adapter.updateItems(sprite);
		} else {
			int firstVisibleBrick = listView.getFirstVisiblePosition();
			int lastVisibleBrick = listView.getLastVisiblePosition();
			int position = (1 + lastVisibleBrick - firstVisibleBrick) / 2;
			position += firstVisibleBrick;
			adapter.addItem(position, bricksToAdd.get(0));
			listView.startMoving(bricksToAdd, position);
		}
	}

	@Override
	public void onItemClick(final BrickBaseType brick, final int position) {
		final List<Integer> options = getContextMenuItems(brick);
		final CharSequence[] items = new CharSequence[options.size()];

		for (int i = 0; i < options.size(); i++) {
			items[i] = getString(options.get(i));
		}

		View brickView = brick.getView(getContext());
		brick.onPrototypeViewCreated();
		brick.disableSpinners();

		new AlertDialog.Builder(getContext())
				.setCustomTitle(brickView)
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handleContextMenuItemClick(options.get(which), brick, position);
					}
				})
				.show();
		if (listView.isCurrentlyHighlighted()) {
			listView.cancelHighlighting();
		}
	}

	private List<Integer> getContextMenuItems(BrickBaseType brick) {
		List<Integer> items = new ArrayList<>();

		if (brick instanceof ScriptBrick) {
			items.add(R.string.backpack_add);
			items.add(R.string.brick_context_dialog_copy_script);
			items.add(R.string.brick_context_dialog_delete_script);

			items.add(brick.isCommentedOut()
					? R.string.brick_context_dialog_comment_in_script
					: R.string.brick_context_dialog_comment_out_script);

			if (brick instanceof FormulaBrick) {
				items.add(R.string.brick_context_dialog_formula_edit_brick);
			}
			items.add(R.string.brick_context_dialog_move_script);
			items.add(R.string.brick_context_dialog_help);
		} else {
			items.add(R.string.brick_context_dialog_copy_brick);
			if (brick instanceof ControlStructureBrick) {
				items.add(R.string.brick_context_dialog_highlight_brick_parts);
			}
			items.add(R.string.brick_context_dialog_delete_brick);

			items.add(brick.isCommentedOut()
					? R.string.brick_context_dialog_comment_in
					: R.string.brick_context_dialog_comment_out);
			if (brick instanceof PlaceAtBrick) {
				items.add(R.string.brick_place_at_option_place_visually);
			}
			if (brick instanceof FormulaBrick) {
				items.add(R.string.brick_context_dialog_formula_edit_brick);
			}
			items.add(R.string.brick_context_dialog_move_brick);
			if (brick.hasHelpPage()) {
				items.add(R.string.brick_context_dialog_help);
			}
		}
		return items;
	}

	private void handleContextMenuItemClick(int itemId, BrickBaseType brick, int position) {
		switch (itemId) {
			case R.string.backpack_add:
				List<Brick> bricksToPack = new ArrayList<>();
				bricksToPack.add(brick);
				bricksToPack.addAll(((ScriptBrick) brick).getScript().getBrickList());
				showNewScriptGroupAlert(bricksToPack);
				break;
			case R.string.brick_context_dialog_copy_brick:
			case R.string.brick_context_dialog_copy_script:
				if (brick instanceof ControlStructureBrick) {
					brick = (BrickBaseType) ((ControlStructureBrick) brick).getFirstBrick();
				}
				try {
					List<BrickBaseType> bricksToAdd = new ArrayList<>();
					for (Brick initializedBrick : brickController.initialize(brick.clone())) {
						bricksToAdd.add((BrickBaseType) initializedBrick);
					}
					adapter.addItem(position, bricksToAdd.get(0));
					listView.startMoving(bricksToAdd, position);
				} catch (CloneNotSupportedException e) {
					ToastUtil.showError(getContext(), R.string.error_copying_brick);
					Log.e(TAG, Log.getStackTraceString(e));
				}
				break;
			case R.string.brick_context_dialog_delete_brick:
			case R.string.brick_context_dialog_delete_script:
				if (brick instanceof ControlStructureBrick) {
					showDeleteAlert(((ControlStructureBrick) brick).getAllParts());
				} else {
					showDeleteAlert(Collections.singletonList((Brick) brick));
				}
				break;
			case R.string.brick_context_dialog_comment_in:
			case R.string.brick_context_dialog_comment_in_script:
				if (brick instanceof ControlStructureBrick) {
					List<Brick> bricksInControlStructure = brickController
							.getBricksInControlStructure((ControlStructureBrick) brick, new ArrayList<Brick>(adapter.getItems()));
					for (Brick brickInControlStructure : bricksInControlStructure) {
						brickInControlStructure.setCommentedOut(false);
					}
				} else {
					brick.setCommentedOut(false);
				}
				adapter.notifyDataSetChanged();
				break;
			case R.string.brick_context_dialog_comment_out:
			case R.string.brick_context_dialog_comment_out_script:
				if (brick instanceof ControlStructureBrick) {
					List<Brick> bricksInControlStructure = brickController
							.getBricksInControlStructure((ControlStructureBrick) brick, new ArrayList<Brick>(adapter.getItems()));
					for (Brick brickInControlStructure : bricksInControlStructure) {
						brickInControlStructure.setCommentedOut(true);
					}
				} else {
					brick.setCommentedOut(true);
				}
				adapter.notifyDataSetChanged();
				adapter.notifyDataSetChanged();
				break;
			case R.string.brick_place_at_option_place_visually:
				((PlaceAtBrick) brick).placeVisually();
				break;
			case R.string.brick_context_dialog_formula_edit_brick:
				((FormulaBrick) brick).onClick(listView);
				break;
			case R.string.brick_context_dialog_move_brick:
			case R.string.brick_context_dialog_move_script:
				onItemLongClick(brick, position);
				break;
			case R.string.brick_context_dialog_help:
				openWebViewWithHelpPage(brick);
				break;
			case R.string.brick_context_dialog_highlight_brick_parts:
				List<Brick> bricksOfControlStructure = ((ControlStructureBrick) brick).getAllParts();
				List<Integer> positions = adapter.getPositionsOfItems(bricksOfControlStructure);
				listView.highlightControlStructureBricks(positions);
				break;
		}
	}

	private void openWebViewWithHelpPage(BrickBaseType brick) {
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		String language = Locale.getDefault().getLanguage();
		String category = new CategoryBricksFactory().getBrickCategory(brick, sprite, getContext());
		String brickType = brick.getClass().getSimpleName();

		if (!language.equals("en") && !language.equals("de") && !language.equals("es")) {
			language = "en";
		}
		Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse("https://wiki.catrob.at/index" + ".php?title=" + category + "_Bricks/" + language + "#" + brickType));
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(BrickBaseType brick, int position) {
		if (listView.isCurrentlyHighlighted()) {
			listView.cancelHighlighting();
		}
		listView.startMoving(brickController
				.getBricksToMove(brick, new ArrayList<Brick>(adapter.getItems())), position);
		return true;
	}

	private void showBackpackModeChooser() {
		CharSequence[] items = new CharSequence[] {getString(R.string.pack), getString(R.string.unpack)};
		new AlertDialog.Builder(getContext())
				.setTitle(R.string.backpack_title)
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								startActionMode(BACKPACK);
								break;
							case 1:
								switchToBackpack();
						}
					}
				})
				.show();
	}

	public void showNewScriptGroupAlert(final List<Brick> selectedBricks) {
		TextInputDialog.Builder builder = new TextInputDialog.Builder(getContext());

		builder.setHint(getString(R.string.script_group_label))
				.setTextWatcher(new UniqueStringTextWatcher(BackpackListManager.getInstance().getBackpackedScriptGroups()))
				.setPositiveButton(getString(R.string.ok), new TextInputDialog.OnClickListener() {
					@Override
					public void onPositiveButtonClick(DialogInterface dialog, String textInput) {
						pack(textInput, selectedBricks);
					}
				});

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

	private void copy(List<Brick> selectedBricks) {
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		brickController.copy(selectedBricks, sprite);
		adapter.updateItems(sprite);
		finishActionMode();
	}

	private void showDeleteAlert(final List<Brick> selectedBricks) {
		new AlertDialog.Builder(getContext())
				.setTitle(getResources().getQuantityString(R.plurals.delete_bricks, selectedBricks.size()))
				.setMessage(R.string.dialog_confirm_delete)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						delete(selectedBricks);
					}
				})
				.setNegativeButton(R.string.no, null)
				.setCancelable(false)
				.show();
	}

	private void delete(List<Brick> selectedItems) {
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		brickController.delete(selectedItems, sprite);
		adapter.updateItems(sprite);
		finishActionMode();
	}

	private void toggleComments(List<Brick> selectedBricks) {
		for (BrickBaseType brick : adapter.getItems()) {
			brick.setCommentedOut(selectedBricks.contains(brick));
		}
		finishActionMode();
	}
}
