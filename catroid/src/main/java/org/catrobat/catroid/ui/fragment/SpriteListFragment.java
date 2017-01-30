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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.SceneListActivity;
import org.catrobat.catroid.ui.SpriteListActivity;
import org.catrobat.catroid.ui.SpriteMemberSelectionActivity;
import org.catrobat.catroid.ui.adapter.CheckBoxListAdapter;
import org.catrobat.catroid.ui.adapter.SpriteListAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.BackPackSpriteController;
import org.catrobat.catroid.ui.dialogs.NewSceneDialog;
import org.catrobat.catroid.ui.dialogs.NewSpriteDialog;
import org.catrobat.catroid.ui.dialogs.RenameItemDialog;
import org.catrobat.catroid.ui.dialogs.ReplaceInBackPackDialog;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListView;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class SpriteListFragment extends ListActivityFragment implements CheckBoxListAdapter
		.ListItemClickHandler<Sprite>, NewSceneDialog.NewSceneInterface {

	public static final String TAG = SpriteListFragment.class.getSimpleName();
	public static final String SHARED_PREFERENCE_NAME = "showSpriteDetails";
	private static final String BUNDLE_ARGUMENTS_SPRITE_TO_EDIT = "sprite_to_edit";

	private SpriteListAdapter spriteAdapter;
	private DragAndDropListView listView;

	private View backgroundItemView;
	private Sprite spriteToEdit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View spriteListFragment = inflater.inflate(R.layout.fragment_sprite_list, container, false);
		listView = (DragAndDropListView) spriteListFragment.findViewById(android.R.id.list);
		backgroundItemView = spriteListFragment.findViewById(R.id.background_sprite);

		SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_objects);
		return spriteListFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		itemIdentifier = R.plurals.sprites;
		deleteDialogTitle = R.plurals.dialog_delete_sprite;
		replaceDialogMessage = R.plurals.dialog_replace_sprite;

		if (savedInstanceState != null) {
			spriteToEdit = (Sprite) savedInstanceState.get(BUNDLE_ARGUMENTS_SPRITE_TO_EDIT);
		}

		initializeList();
	}

	private void initializeList() {
		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentScene().getSpriteList();

		spriteAdapter = new SpriteListAdapter(getActivity(), R.layout.list_item, spriteList);

		setBackgroundSprite();

		setListAdapter(spriteAdapter);
		spriteAdapter.setListItemClickHandler(this);
		spriteAdapter.setListItemLongClickHandler(listView);
		spriteAdapter.setListItemCheckHandler(this);
		listView.setAdapterInterface(spriteAdapter);
	}

	public void setBackgroundSprite() {
		CheckBoxListAdapter.ListItemViewHolder viewHolder = new CheckBoxListAdapter.ListItemViewHolder(backgroundItemView);
		backgroundItemView.setTag(viewHolder);
		spriteAdapter.getWrappedListItem(0, backgroundItemView);

		viewHolder.background.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handleOnItemClick(0, v, spriteAdapter.getItem(0));
			}
		});
	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(BUNDLE_ARGUMENTS_SPRITE_TO_EDIT, spriteToEdit);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (ProjectManager.getInstance().getCurrentProject().isScenesEnabled()) {
			getActivity().getActionBar().setTitle(ProjectManager.getInstance().getCurrentScene().getName());
		} else {
			getActivity().getActionBar().setTitle(ProjectManager.getInstance().getCurrentProject().getName());
		}

		loadShowDetailsPreferences(SHARED_PREFERENCE_NAME);
	}

	@Override
	public void onPause() {
		super.onPause();
		saveCurrentProject();
		putShowDetailsPreferences(SHARED_PREFERENCE_NAME);
	}

	@Override
	public void setShowDetails(boolean showDetails) {
		super.setShowDetails(showDetails);
		backgroundItemView = spriteAdapter.getWrappedListItem(0, backgroundItemView);
	}

	public void showNewSceneDialog() {
		List<Scene> scope = ProjectManager.getInstance().getCurrentProject().getSceneList();
		String defaultSceneName = Utils.getUniqueSceneName(getString(R.string.default_scene_name), scope);
		NewSceneDialog dialog = new NewSceneDialog(defaultSceneName, this);
		dialog.show(getFragmentManager(), NewSceneDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void addAndOpenNewScene(String sceneName) {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		boolean pushSceneListActivityToBackStack = !currentProject.isScenesEnabled();

		Scene scene = new Scene(getActivity(), sceneName, currentProject);
		currentProject.addScene(scene);
		ProjectManager.getInstance().saveProject(getActivity());
		ProjectManager.getInstance().setCurrentScene(scene);
		getActivity().finish();

		if (pushSceneListActivityToBackStack) {
			Intent intent = new Intent(getActivity(), SceneListActivity.class);
			startActivity(intent);
		}

		Intent intent = new Intent(getActivity(), SpriteListActivity.class);
		startActivity(intent);
	}

	@Override
	public void handleAddButton() {
		NewSpriteDialog dialog = new NewSpriteDialog();
		dialog.show(getFragmentManager(), NewSpriteDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void handleOnItemClick(int position, View view, Sprite sprite) {
		if (isActionModeActive()) {
			return;
		}

		ProjectManager.getInstance().setCurrentSprite(sprite);
		Intent intent = new Intent(getActivity(), SpriteMemberSelectionActivity.class);
		startActivity(intent);
	}

	@Override
	public void deleteCheckedItems() {
		for (Sprite sprite : spriteAdapter.getCheckedItems()) {
			spriteToEdit = sprite;
			deleteSprite();
		}
	}

	public void deleteSprite() {
		ProjectManager projectManager = ProjectManager.getInstance();
		DataContainer dataContainer = projectManager.getCurrentScene().getDataContainer();

		for (LookData currentLookData : spriteToEdit.getLookDataList()) {
			currentLookData.getCollisionInformation().cancelCalculation();
		}

		deleteSpriteFiles();
		dataContainer.cleanVariableListForSprite(spriteToEdit);
		dataContainer.cleanUserListForSprite(spriteToEdit);

		if (projectManager.getCurrentSprite() != null && projectManager.getCurrentSprite().equals(spriteToEdit)) {
			projectManager.setCurrentSprite(null);
		}
		spriteAdapter.remove(spriteToEdit);
	}

	private void deleteSpriteFiles() {
		FileChecksumContainer checksumContainer = ProjectManager.getInstance().getFileChecksumContainer();
		for (LookData currentLookData : spriteToEdit.getLookDataList()) {
			StorageHandler.deleteFile(currentLookData.getAbsolutePath(), checksumContainer);
		}
		for (SoundInfo currentSoundInfo : spriteToEdit.getSoundList()) {
			StorageHandler.deleteFile(currentSoundInfo.getAbsolutePath(), checksumContainer);
		}
	}

	@Override
	protected void copyCheckedItems() {
		for (Sprite sprite : spriteAdapter.getCheckedItems()) {
			spriteToEdit = sprite;
			copySprite();
		}
		clearCheckedItems();
	}

	private void copySprite() {
		spriteToEdit.setConvertToSingleSprite(true);
		Sprite copiedSprite = spriteToEdit.clone();
		spriteToEdit.setConvertToSingleSprite(false);

		String oldName = copiedSprite.getName();
		copiedSprite.setName(spriteToEdit.getName().concat(getString(R.string.copied_item_suffix)));
		String newName = copiedSprite.getName();
		copiedSprite.updateCollisionBroadcastMessages(oldName, newName);

		ProjectManager projectManager = ProjectManager.getInstance();
		projectManager.addSprite(copiedSprite);
		projectManager.setCurrentSprite(copiedSprite);
		spriteAdapter.notifyDataSetChanged();
	}

	@Override
	public void showRenameDialog() {
		spriteToEdit = spriteAdapter.getCheckedItems().get(0);
		RenameItemDialog dialog = new RenameItemDialog(R.string.dialog_rename_sprite, R.string.sprite_name,
				spriteToEdit.getName(), this);
		dialog.show(getFragmentManager(), RenameItemDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public boolean itemNameExists(String newName) {
		ProjectManager projectManager = ProjectManager.getInstance();
		return projectManager.spriteExists(newName);
	}

	@Override
	public void renameItem(String newName) {
		renameSpritesInCollisionFormulas(spriteToEdit.getName(), newName, getActivity());
		spriteToEdit.rename(newName);
		clearCheckedItems();
		spriteAdapter.notifyDataSetChanged();
	}

	@Override
	public void showReplaceItemsInBackPackDialog() {
		if (!BackPackSpriteController.existsInBackpack(spriteAdapter.getCheckedItems())) {
			packCheckedItems();
			return;
		}

		String name = spriteAdapter.getCheckedItems().get(0).getName();
		ReplaceInBackPackDialog dialog = new ReplaceInBackPackDialog(replaceDialogMessage, name, this);
		dialog.show(getFragmentManager(), ReplaceInBackPackDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void packCheckedItems() {
		setProgressCircleVisibility(true);
		boolean success = BackPackSpriteController.backpack(spriteAdapter.getCheckedItems(), true);
		clearCheckedItems();

		if (success) {
			changeToBackPack();
			return;
		}

		setProgressCircleVisibility(false);
		ToastUtil.showError(getActivity(), R.string.error_backpack_sprite);
	}

	@Override
	protected boolean isBackPackEmpty() {
		return BackPackListManager.getInstance().getBackPackedSprites().isEmpty();
	}

	@Override
	protected void changeToBackPack() {
		Intent intent = new Intent(getActivity(), BackPackActivity.class);
		intent.putExtra(BackPackActivity.FRAGMENT, BackPackSpriteListFragment.class);
		startActivity(intent);
	}

	private void renameSpritesInCollisionFormulas(String oldName, String newName, Context context) {
		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentScene().getSpriteList();
		for (Sprite sprite : spriteList) {
			for (Script currentScript : sprite.getScriptList()) {
				if (currentScript == null) {
					return;
				}
				List<Brick> brickList = currentScript.getBrickList();
				for (Brick brick : brickList) {
					if (brick instanceof FormulaBrick) {
						List<Formula> formulaList = ((FormulaBrick) brick).getFormulas();
						for (Formula formula : formulaList) {
							formula.updateCollisionFormulas(oldName, newName, context);
						}
					}
				}
			}
		}
	}
}
