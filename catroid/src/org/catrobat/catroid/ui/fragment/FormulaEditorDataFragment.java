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

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.DataAdapter;
import org.catrobat.catroid.ui.dialogs.NewDataDialog;
import org.catrobat.catroid.ui.dialogs.NewDataDialog.NewUserListDialogListener;
import org.catrobat.catroid.ui.dialogs.RenameVariableDialog;
import org.catrobat.catroid.utils.Utils;

public class FormulaEditorDataFragment extends ListFragment implements Dialog.OnKeyListener,
		DataAdapter.OnCheckedChangeListener, DataAdapter.OnListItemClickListener, NewUserListDialogListener, NewDataDialog.NewVariableDialogListener {
	private static final String TAG = FormulaEditorDataFragment.class.getSimpleName();

	public static final String USER_DATA_TAG = "userDataFragment";
	public static final String ACTION_BAR_TITLE_BUNDLE_ARGUMENT = "actionBarTitle";
	public static final String FRAGMENT_TAG_BUNDLE_ARGUMENT = "fragmentTag";

	private String actionBarTitle;
	private ActionMode contextActionMode;
	private View selectAllActionModeButton;
	private boolean inContextMode;
	private int index;
	private DataAdapter adapter;
	private boolean inUserBrick;

	public FormulaEditorDataFragment(boolean inUserBrick) {
		contextActionMode = null;
		index = -1;
		inContextMode = false;
		this.inUserBrick = inUserBrick;
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "FormulaEditorData onresume()");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		initializeDataAdapter();

		this.actionBarTitle = getArguments().getString(ACTION_BAR_TITLE_BUNDLE_ARGUMENT);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_formula_editor_data_list, container, false);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		if (!inContextMode) {
			super.onCreateContextMenu(menu, view, menuInfo);
			getActivity().getMenuInflater().inflate(R.menu.context_menu_formulaeditor_userlist, menu);
			menu.findItem(R.id.context_formula_editor_userlist_delete).setVisible(true);
			menu.findItem(R.id.context_formula_editor_userlist_rename).setVisible(true);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_formulaeditor_data_fragment, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		for (int index = 0; index < menu.size(); index++) {
			menu.getItem(index).setVisible(false);
		}
		menu.findItem(R.id.formula_editor_data_item_delete).setVisible(true);

		getActivity().getActionBar().setDisplayShowTitleEnabled(true);
		getActivity().getActionBar().setTitle(actionBarTitle);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);

		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onListItemClick(int position) {
		if (!inContextMode) {
			FormulaEditorFragment formulaEditor = (FormulaEditorFragment) getActivity()
					.getFragmentManager().findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
			if (formulaEditor != null) {
				Object itemToAdd = adapter.getItem(position);
				if (itemToAdd instanceof UserVariable) {
					formulaEditor.addUserVariableToActiveFormula(((UserVariable) itemToAdd).getName());
				} else if (itemToAdd instanceof UserList) {
					formulaEditor.addUserListToActiveFormula(((UserList) itemToAdd).getName());
				}
				formulaEditor.updateButtonsOnKeyboardAndInvalidateOptionsMenu();
			}
			KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
			onKey(null, keyEvent.getKeyCode(), keyEvent);
		}
	}

	@Override
	public void onCheckedChange() {
		if (!inContextMode) {
			return;
		}

		updateActionModeTitle();
		Utils.setSelectAllActionModeButtonVisibility(selectAllActionModeButton,
				adapter.getCount() > 0 && adapter.getAmountOfCheckedItems() != adapter.getCount());
	}

	private void updateActionModeTitle() {
		String title = adapter.getAmountOfCheckedItems()
				+ " "
				+ getActivity().getResources().getQuantityString(
				R.plurals.formula_editor_data_fragment_context_action_item_selected,
				adapter.getAmountOfCheckedItems());

		contextActionMode.setTitle(title);
	}

	@Override
	public void onStart() {

		this.registerForContextMenu(getListView());
		getListView().setItemsCanFocus(true);
		getListView().setLongClickable(true);
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
				if (!inContextMode) {
					index = position;
					getActivity().openContextMenu(getListView());
					return true;
				}
				return false;
			}
		});

		setAddButtonListener(getActivity());

		adapter.notifyDataSetChanged();

		super.onStart();
	}

	public void setAddButtonListener(final Activity activity) {
		ImageButton buttonAdd = (ImageButton) activity.findViewById(R.id.button_add);
		buttonAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				NewDataDialog dialog = new NewDataDialog(NewDataDialog.DialogType.SHOW_LIST_CHECKBOX);
				dialog.addUserListDialogListener(FormulaEditorDataFragment.this);
				dialog.addVariableDialogListener(FormulaEditorDataFragment.this);
				dialog.show(activity.getFragmentManager(), NewDataDialog.DIALOG_FRAGMENT_TAG);
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.formula_editor_data_item_delete:
				inContextMode = true;
				contextActionMode = getActivity().startActionMode(contextModeCallback);
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {

		switch (item.getItemId()) {
			case R.id.context_formula_editor_userlist_delete:
				if (!adapter.isEmpty()) {
					Object itemToDelete = adapter.getItem(index);
					if (itemToDelete instanceof UserList) {
						ProjectManager.getInstance().getCurrentProject().getDataContainer()
								.deleteUserListByName(getNameOfItemInAdapter(index));
						adapter.notifyDataSetChanged();
						getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_USERLIST_DELETED));
					} else {
						ProjectManager.getInstance().getCurrentProject().getDataContainer()
								.deleteUserVariableByName(getNameOfItemInAdapter(index));
						adapter.notifyDataSetChanged();
						getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_VARIABLE_DELETED));
					}
				}
				return true;
			case R.id.context_formula_editor_userlist_rename:
				Object itemToRename = adapter.getItem(index);
				RenameVariableDialog dialog;
				if (itemToRename instanceof UserVariable) {
					dialog = new RenameVariableDialog((UserVariable) itemToRename, adapter, RenameVariableDialog
							.DialogType.USER_VARIABLE);
				} else if (itemToRename instanceof UserList) {
					dialog = new RenameVariableDialog((UserList) itemToRename, adapter, RenameVariableDialog
							.DialogType.USER_LIST);
				} else {
					return false;
				}
				dialog.show(getActivity().getFragmentManager(), RenameVariableDialog.DIALOG_FRAGMENT_TAG);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onFinishNewUserListDialog(Spinner spinnerToUpdate, UserList userList) {
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onFinishNewVariableDialog(Spinner spinnerToUpdate, UserVariable newUserVariable) {
		adapter.notifyDataSetChanged();
	}

	public void showFragment(Context context) {
		Activity activity = (Activity) context;
		FragmentManager fragmentManager = activity.getFragmentManager();
		FragmentTransaction fragTransaction = fragmentManager.beginTransaction();

		Fragment formulaEditorFragment = fragmentManager
				.findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		fragTransaction.hide(formulaEditorFragment);

		BottomBar.showBottomBar(activity);
		BottomBar.hidePlayButton(activity);

		fragTransaction.show(this);
		fragTransaction.commit();

		if (adapter != null) {
			initializeDataAdapter();
		}
	}

	private void initializeDataAdapter() {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		UserBrick currentBrick = ProjectManager.getInstance().getCurrentUserBrick();
		int userBrickId = (currentBrick == null ? -1 : currentBrick.getUserBrickId());
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		DataContainer dataContainer = currentProject.getDataContainer();
		adapter = dataContainer.createDataAdapter(getActivity(), userBrickId, currentSprite, inUserBrick);
		setListAdapter(adapter);
		adapter.setOnCheckedChangeListener(this);
		adapter.setOnListItemClickListener(this);
	}

	@Override
	public boolean onKey(DialogInterface d, int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				BottomBar.hideBottomBar(getActivity());
				((ScriptActivity) getActivity()).updateHandleAddButtonClickListener();

				FragmentTransaction fragmentTransaction = getActivity().getFragmentManager()
						.beginTransaction();
				fragmentTransaction.hide(this);
				FormulaEditorFragment formulaEditorFragment = (FormulaEditorFragment) getActivity()
						.getFragmentManager().findFragmentByTag(
								FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
				formulaEditorFragment.updateBrickView();
				fragmentTransaction.show(formulaEditorFragment);
				fragmentTransaction.commit();
				return true;
			default:
				break;
		}
		return false;
	}

	private void addSelectAllActionModeButton(ActionMode mode, Menu menu) {
		selectAllActionModeButton = Utils.addSelectAllActionModeButton(getActivity().getLayoutInflater(), mode, menu);
		selectAllActionModeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				for (int position = 0; position < adapter.getCount(); position++) {
					adapter.addCheckedItem(position);
				}
				adapter.notifyDataSetChanged();
				onCheckedChange();
			}
		});
	}

	private ActionMode.Callback contextModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			adapter.setSelectMode(ListView.CHOICE_MODE_MULTIPLE);
			adapter.notifyDataSetChanged();
			mode.setTitle("0 "
					+ getActivity().getResources().getQuantityString(
					R.plurals.formula_editor_data_fragment_context_action_item_selected, 0));
			BottomBar.hideBottomBar(getActivity());
			addSelectAllActionModeButton(mode, menu);
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
			DataContainer dataContainer = ProjectManager.getInstance().getCurrentProject().getDataContainer();
			if (!adapter.isEmpty()) {
				for (UserList var : adapter.getCheckedUserLists()) {
					dataContainer.deleteUserListByName(var.getName());
				}
				for (UserVariable var : adapter.getCheckedUserVariables()) {
					dataContainer.deleteUserVariableByName(var.getName());
				}
			}

			adapter.notifyDataSetChanged();
			getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_USERLIST_DELETED));
			getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_VARIABLE_DELETED));

			adapter.setSelectMode(ListView.CHOICE_MODE_NONE);
			contextActionMode = null;
			inContextMode = false;
			getActivity().findViewById(R.id.bottom_bar).setVisibility(View.VISIBLE);
		}
	};

	private String getNameOfItemInAdapter(int position) {
		Object item = adapter.getItem(position);
		if (item instanceof UserList) {
			return ((UserList) item).getName();
		} else if (item instanceof UserVariable) {
			return ((UserVariable) item).getName();
		}

		return null;
	}
}
