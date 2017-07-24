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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.adapter.CheckBoxListAdapter;
import org.catrobat.catroid.ui.adapter.SceneListAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.BackPackSceneController;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class BackPackSceneListFragment extends BackPackActivityFragment implements CheckBoxListAdapter
		.ListItemClickHandler<Scene>, CheckBoxListAdapter.ListItemLongClickHandler {

	public static final String TAG = BackPackSceneListFragment.class.getSimpleName();
	private static final String BUNDLE_ARGUMENTS_SCENE_TO_EDIT = "scene_to_edit";

	private SceneListAdapter sceneAdapter;

	private Scene sceneToEdit;
	private int selectedScenePosition;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View backPackSceneListFragment = inflater.inflate(R.layout.fragment_backpack, container, false);
		listView = (ListView) backPackSceneListFragment.findViewById(android.R.id.list);

		return backPackSceneListFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		registerForContextMenu(listView);

		singleItemTitle = getString(R.string.scene);
		multipleItemsTitle = getString(R.string.scenes);

		if (savedInstanceState != null) {
			sceneToEdit = (Scene) savedInstanceState.get(BUNDLE_ARGUMENTS_SCENE_TO_EDIT);
		}

		initializeList();
		checkEmptyBackgroundBackPack();
		BottomBar.hideBottomBar(getActivity());
	}

	private void initializeList() {
		List<Scene> sceneList = BackPackListManager.getInstance().getBackPackedScenes();

		sceneAdapter = new SceneListAdapter(getActivity(), R.layout.list_item, sceneList);

		setListAdapter(sceneAdapter);
		sceneAdapter.setListItemClickHandler(this);
		sceneAdapter.setListItemCheckHandler(this);
		sceneAdapter.setListItemLongClickHandler(this);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(BUNDLE_ARGUMENTS_SCENE_TO_EDIT, sceneToEdit);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		super.onPause();
		BackPackListManager.getInstance().saveBackpack();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.findItem(R.id.show_details).setVisible(false);
		if (BackPackListManager.getInstance().getBackPackedScenes().isEmpty()) {
			menu.findItem(R.id.unpacking).setVisible(false);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		sceneToEdit = sceneAdapter.getItem(selectedScenePosition);
		menu.setHeaderTitle(sceneToEdit.getName());

		getActivity().getMenuInflater().inflate(R.menu.context_menu_unpacking, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.context_menu_unpacking:
				unpackCheckedItems(true);
				break;
			case R.id.context_menu_delete:
				showDeleteDialog(true);
				break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void handleOnItemClick(int position, View view, Scene scene) {
		selectedScenePosition = position;
		listView.showContextMenuForChild(view);
	}

	@Override
	public void handleOnItemLongClick(int position, View view) {
		selectedScenePosition = position;
		listView.showContextMenuForChild(view);
	}

	@Override
	public void showDeleteDialog(boolean singleItem) {
		int titleId;
		if (sceneAdapter.getCheckedItems().size() == 1 || singleItem) {
			titleId = R.string.dialog_confirm_delete_scene_title;
		} else {
			titleId = R.string.dialog_confirm_delete_multiple_scenes_title;
		}
		showDeleteDialog(titleId, singleItem);
	}

	@Override
	protected void deleteCheckedItems(boolean singleItem) {
		if (singleItem) {
			deleteScene();
			return;
		}
		for (Scene scene : sceneAdapter.getCheckedItems()) {
			sceneToEdit = scene;
			deleteScene();
		}
	}

	public void deleteScene() {
		ArrayList<Scene> hiddenScenes = new ArrayList<>();
		BackPackListManager.searchForHiddenScenes(sceneToEdit, hiddenScenes, true);
		hiddenScenes.remove(sceneToEdit);
		for (Scene scene : hiddenScenes) {
			BackPackListManager.getInstance().removeItemFromSceneBackPackByName(scene.getName(), true);
		}
		BackPackListManager.getInstance().removeItemFromSceneBackPackByName(sceneToEdit.getName(), false);
		checkEmptyBackgroundBackPack();
		sceneAdapter.notifyDataSetChanged();
	}

	protected void unpackCheckedItems(boolean singleItem) {
		List<Scene> sceneList = new ArrayList<>();
		if (singleItem) {
			sceneList.add(sceneToEdit);
		} else {
			sceneList.addAll(sceneAdapter.getCheckedItems());
		}

		if (conflictingResolutionsDetected(sceneList)) {
			showDifferentResolutionDialog(sceneList);
			return;
		}

		unpackScenes(sceneList);
		clearCheckedItems();
		getActivity().finish();
	}

	private boolean conflictingResolutionsDetected(List<Scene> sceneList) {
		int currentHeight = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenHeight;
		int currentWidth = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenWidth;

		for (Scene scene : sceneList) {
			if (scene.getOriginalHeight() != currentHeight || scene.getOriginalWidth() != currentWidth) {
				return true;
			}
		}

		return false;
	}

	private void showDifferentResolutionDialog(final List<Scene> sceneList) {
		AlertDialog.Builder builder = new CustomAlertDialogBuilder(getActivity());
		builder.setTitle(R.string.warning);
		builder.setMessage(R.string.error_unpack_scene_with_different_resolution);
		builder.setPositiveButton(R.string.main_menu_continue, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				unpackScenes(sceneList);
				clearCheckedItems();
				getActivity().finish();
			}
		});
		builder.setNegativeButton(R.string.abort, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				clearCheckedItems();
			}
		});

		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialogInterface) {
				clearCheckedItems();
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	private void unpackScenes(List<Scene> sceneList) {
		boolean success = BackPackSceneController.getInstance().unpackScenes(sceneList) != null;

		if (success) {
			ProjectManager.getInstance().checkNestingBrickReferences(false, false);
			showUnpackingCompleteToast(sceneList.size());
			clearCheckedItems();
		} else {
			showError(R.string.error_scene_backpack);
		}
	}
}
