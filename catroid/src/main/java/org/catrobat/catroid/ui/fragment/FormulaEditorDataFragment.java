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
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.adapter.DataAdapter;
import org.catrobat.catroid.ui.dialogs.RenameVariableDialog;
import org.catrobat.catroid.ui.recyclerview.dialog.NewDataDialog;
import org.catrobat.catroid.utils.ToastUtil;

public class FormulaEditorDataFragment extends ListFragment implements
		DataAdapter.OnCheckedChangeListener,
		DataAdapter.OnListItemClickListener,
		NewDataDialog.NewDataInterface {

	public static final String USER_DATA_TAG = "userDataFragment";
	public static final String ACTION_BAR_TITLE_BUNDLE_ARGUMENT = "actionBarTitle";
	public static final String FRAGMENT_TAG_BUNDLE_ARGUMENT = "fragmentTag";

	private DataAdapter adapter;
	private ActionMode actionMode;

	private ActionMode.Callback callback = new ActionMode.Callback() {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			adapter.setSelectMode(ListView.CHOICE_MODE_MULTIPLE);
			adapter.notifyDataSetChanged();

			mode.setTitle(getResources().getQuantityString(R.plurals.formula_editor_data_am_title, 0, 0));
			BottomBar.hideBottomBar(getActivity());
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			if (adapter.getCheckedItems().isEmpty()) {
				finishActionMode();
			} else {
				showDeleteAlert();
			}
		}
	};

	private void finishActionMode() {
		adapter.setSelectMode(ListView.CHOICE_MODE_NONE);
		adapter.clearCheckedItems();
		adapter.notifyDataSetChanged();
		actionMode = null;
		BottomBar.showBottomBar(getActivity());
	}

	public void showDeleteAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.deletion_alert_title)
				.setMessage(R.string.deletion_alert_text)
				.setPositiveButton(R.string.deletion_alert_yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						deleteItems();
						finishActionMode();
					}
				})
				.setNegativeButton(R.string.deletion_alert_no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finishActionMode();
						dialog.dismiss();
					}
				});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	private void deleteItems() {
		DataContainer dataContainer = ProjectManager.getInstance().getCurrentScene().getDataContainer();

		for (UserList var : adapter.getCheckedUserLists()) {
			dataContainer.deleteUserListByName(var.getName());
		}
		for (UserVariable var : adapter.getCheckedUserVariables()) {
			dataContainer.deleteUserVariableByName(var.getName());
		}

		adapter.notifyDataSetChanged();
		finishActionMode();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_formula_editor_data_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstance) {
		super.onActivityCreated(savedInstance);
		setHasOptionsMenu(true);
		initializeDataAdapter();

		getListView().setLongClickable(true);
		getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

				CharSequence[] items = new CharSequence[] {
						getString(R.string.delete),
						getString(R.string.rename),
				};

				builder.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								adapter.addCheckedItem(position);
								showDeleteAlert();
								break;
							case 1:
								Object itemToRename = adapter.getItem(position);
								RenameVariableDialog renameDialog;

								if (itemToRename instanceof UserVariable) {
									renameDialog = new RenameVariableDialog((UserVariable) itemToRename, adapter,
											RenameVariableDialog.DialogType.USER_VARIABLE);
									renameDialog.show(getFragmentManager(), RenameVariableDialog.DIALOG_FRAGMENT_TAG);
								} else if (itemToRename instanceof UserList) {
									renameDialog = new RenameVariableDialog((UserList) itemToRename, adapter,
											RenameVariableDialog.DialogType.USER_LIST);
									renameDialog.show(getFragmentManager(), RenameVariableDialog.DIALOG_FRAGMENT_TAG);
								}
								break;
							default:
								dialog.dismiss();
						}
					}
				});

				builder.setCancelable(true);
				builder.show();
				return false;
			}
		});
	}

	@Override
	public void onResume() {
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.category_data);
		BottomBar.showBottomBar(getActivity());
		BottomBar.hidePlayButton(getActivity());
		super.onResume();
	}

	@Override
	public void onStop() {
		super.onStop();
		BottomBar.hideBottomBar(getActivity());
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
		getActivity().getMenuInflater().inflate(R.menu.context_menu_formulaeditor_userlist, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		for (int index = 0; index < menu.size(); index++) {
			menu.getItem(index).setVisible(false);
		}
		menu.findItem(R.id.delete).setVisible(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.delete:
				startActionMode();
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void startActionMode() {
		// This won't work because getCount() of the data adapter.
		if (adapter.isEmpty()) {
			ToastUtil.showError(getActivity(), R.string.am_empty_list);
		} else {
			actionMode = getActivity().startActionMode(callback);
		}
	}

	@Override
	public void onListItemClick(int position) {
		FormulaEditorFragment formulaEditor = (FormulaEditorFragment) getFragmentManager()
				.findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);

		Object itemToAdd = adapter.getItem(position);
		if (itemToAdd instanceof UserVariable) {
			formulaEditor.addUserVariableToActiveFormula(((UserVariable) itemToAdd).getName());
		} else if (itemToAdd instanceof UserList) {
			formulaEditor.addUserListToActiveFormula(((UserList) itemToAdd).getName());
		}

		formulaEditor.updateButtonsOnKeyboardAndInvalidateOptionsMenu();
		getActivity().onBackPressed();
	}

	@Override
	public void onCheckedChange() {
		if (actionMode != null) {
			actionMode.setTitle(getResources().getQuantityString(R.plurals.formula_editor_data_am_title,
					adapter.getAmountOfCheckedItems(),
					adapter.getAmountOfCheckedItems()));
		}
	}

	public void handleAddButton() {
		NewDataDialog dialog = new NewDataDialog();
		dialog.setNewDataInterface(this);
		dialog.show(getFragmentManager(), NewDataDialog.TAG);
	}

	@Override
	public void onNewData() {
		adapter.notifyDataSetChanged();
	}

	private void initializeDataAdapter() {
		Scene currentScene = ProjectManager.getInstance().getCurrentScene();
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		UserBrick currentUserBrick = ProjectManager.getInstance().getCurrentUserBrick();
		DataContainer dataContainer = currentScene.getDataContainer();
		adapter = dataContainer.createDataAdapter(getActivity(), currentUserBrick, currentSprite);
		setListAdapter(adapter);
		adapter.setOnCheckedChangeListener(this);
		adapter.setOnListItemClickListener(this);
		adapter.notifyDataSetChanged();
	}
}
