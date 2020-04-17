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
import android.os.Parcelable;
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
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.VisualPlacementBrick;
import org.catrobat.catroid.io.asynctask.ProjectSaveTask;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.dragndrop.BrickListView;
import org.catrobat.catroid.ui.fragment.AddBrickFragment;
import org.catrobat.catroid.ui.fragment.BrickCategoryFragment;
import org.catrobat.catroid.ui.fragment.BrickCategoryFragment.OnCategorySelectedListener;
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory;
import org.catrobat.catroid.ui.fragment.UserBrickListFragment;
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
import java.util.List;

import androidx.annotation.IntDef;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.ListFragment;

public class ScriptFragment extends ListFragment implements
		ActionMode.Callback,
		BrickAdapter.OnItemClickListener,
		BrickAdapter.SelectionListener, OnCategorySelectedListener {

	public static final String TAG = ScriptFragment.class.getSimpleName();

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({NONE, BACKPACK, COPY, DELETE, COMMENT})
	@interface ActionModeType {
	}

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
				adapter.setCheckBoxMode(BrickAdapter.SCRIPTS_ONLY);
				mode.setTitle(getString(R.string.am_backpack));
				break;
			case COPY:
				adapter.setCheckBoxMode(BrickAdapter.ALL);
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
		adapter.setCheckBoxMode(BrickAdapter.NONE);
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
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		new ProjectSaveTask(currentProject, getContext())
				.execute();

		savedListViewState = listView.onSaveInstanceState();
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

	public boolean isCurrentlyMoving() {
		return listView.isCurrentlyMoving();
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

		SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_category);
	}

	@Override
	public void onCategorySelected(String category) {
		ListFragment addListFragment = null;
		String tag = "";

		if (category.equals(getContext().getString(R.string.category_user_bricks))) {
			addListFragment = UserBrickListFragment.newInstance();
			tag = UserBrickListFragment.USER_BRICK_LIST_FRAGMENT_TAG;
		} else {
			addListFragment = AddBrickFragment.newInstance(category, this);
			tag = AddBrickFragment.ADD_BRICK_FRAGMENT_TAG;
		}

		getFragmentManager().beginTransaction()
				.add(R.id.fragment_container, addListFragment, tag)
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
			showCategoryFragment();
		}
	}

	public void addBrick(Brick brick) {
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();

		if (adapter.getCount() == 0) {
			if (brick instanceof ScriptBrick) {
				sprite.addScript(brick.getScript());
			} else {
				Script script = new StartScript();
				script.addBrick(brick);
				sprite.addScript(script);
			}
			adapter.updateItems(sprite);
		} else if (adapter.getCount() == 1) {
			if (brick instanceof ScriptBrick) {
				sprite.addScript(0, brick.getScript());
			} else {
				sprite.getScriptList().get(0).addBrick(brick);
			}
			adapter.updateItems(sprite);
		} else {
			int firstVisibleBrick = listView.getFirstVisiblePosition();
			int lastVisibleBrick = listView.getLastVisiblePosition();
			int position = (1 + lastVisibleBrick - firstVisibleBrick) / 2;
			position += firstVisibleBrick;
			adapter.addItem(position, brick);
			listView.startMoving(brick);
		}
	}

	@Override
	public void onItemClick(Brick brick, int position) {
		if (listView.isCurrentlyHighlighted()) {
			listView.cancelHighlighting();
			return;
		}
		List<Integer> options = getContextMenuItems(brick);
		CharSequence[] items = new CharSequence[options.size()];

		for (int i = 0; i < options.size(); i++) {
			items[i] = getString(options.get(i));
		}

		View brickView = brick.getView(getContext());
		brick.disableSpinners();

		new AlertDialog.Builder(getContext())
				.setCustomTitle(brickView)
				.setItems(items, (dialog, which) -> handleContextMenuItemClick(options.get(which), brick, position))
				.show();
	}

	private List<Integer> getContextMenuItems(Brick brick) {
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
			if (brick.consistsOfMultipleParts()) {
				items.add(R.string.brick_context_dialog_highlight_brick_parts);
			}
			items.add(R.string.brick_context_dialog_delete_brick);

			items.add(brick.isCommentedOut()
					? R.string.brick_context_dialog_comment_in
					: R.string.brick_context_dialog_comment_out);
			if (brick instanceof VisualPlacementBrick) {
				items.add(R.string.brick_option_place_visually);
			}
			if (brick instanceof FormulaBrick) {
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
		switch (itemId) {
			case R.string.backpack_add:
				List<Brick> bricksToPack = new ArrayList<>();
				bricksToPack.add(brick);
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
				onItemLongClick(brick, position);
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
	public boolean onItemLongClick(Brick brick, int position) {
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

		builder.setHint(getString(R.string.script_group_label))
				.setTextWatcher(new UniqueStringTextWatcher(BackpackListManager.getInstance().getBackpackedScriptGroups()))
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

	private void copy(List<Brick> selectedBricks) {
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		brickController.copy(selectedBricks, sprite);
		adapter.updateItems(sprite);
		finishActionMode();
	}

	private void showDeleteAlert(List<Brick> selectedBricks) {
		new AlertDialog.Builder(getContext())
				.setTitle(getResources().getQuantityString(R.plurals.delete_bricks, selectedBricks.size()))
				.setMessage(R.string.dialog_confirm_delete)
				.setPositiveButton(R.string.yes, (dialog, id) -> delete(selectedBricks))
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
		for (Brick brick : adapter.getItems()) {
			brick.setCommentedOut(selectedBricks.contains(brick));
		}
		finishActionMode();
	}
}
