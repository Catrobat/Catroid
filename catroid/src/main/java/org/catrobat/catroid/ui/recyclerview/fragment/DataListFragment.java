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

package org.catrobat.catroid.ui.recyclerview.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserData;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.recyclerview.adapter.DataListAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.RVAdapter;
import org.catrobat.catroid.ui.recyclerview.dialog.NewDataDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.RenameDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;
import org.catrobat.catroid.ui.recyclerview.viewholder.ViewHolder;
import org.catrobat.catroid.utils.ToastUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataListFragment extends Fragment implements
		RVAdapter.SelectionListener,
		RVAdapter.OnItemClickListener<UserData>,
		NewItemInterface<UserData>,
		RenameDialogFragment.RenameInterface {

	public static final String TAG = DataListFragment.class.getSimpleName();

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({NONE, DELETE})
	@interface ActionModeType {}
	private static final int NONE = 0;
	private static final int DELETE = 1;

	private RecyclerView recyclerView;
	private DataListAdapter adapter;
	private ActionMode actionMode;
	String actionModeTitle = "";

	private FormulaEditorDataInterface formulaEditorDataInterface;

	@ActionModeType
	protected int actionModeType = NONE;

	public void setFormulaEditorDataInterface(FormulaEditorDataInterface formulaEditorDataInterface) {
		this.formulaEditorDataInterface = formulaEditorDataInterface;
	}

	protected ActionMode.Callback callback = new ActionMode.Callback() {
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			switch (actionModeType) {
				case DELETE:
					actionModeTitle = getString(R.string.am_delete);
					break;
				case NONE:
					return false;
			}
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.context_menu, menu);

			adapter.showCheckBoxes(true);
			adapter.updateDataSet();
			mode.setTitle(actionModeTitle);
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
		}
	};

	private void handleContextualAction() {
		if (adapter.getSelectedItems().isEmpty()) {
			actionMode.finish();
			return;
		}

		switch (actionModeType) {
			case DELETE:
				showDeleteAlert(adapter.getSelectedItems());
				break;
			case NONE:
				throw new IllegalStateException("ActionModeType not set Correctly");
		}
	}

	protected void resetActionModeParameters() {
		actionModeType = NONE;
		actionModeTitle = "";
		adapter.showCheckBoxes(false);
		adapter.allowMultiSelection = true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.fragment_list_view, container, false);
		recyclerView = parent.findViewById(R.id.recycler_view);
		setHasOptionsMenu(true);
		return parent;
	}

	@Override
	public void onActivityCreated(Bundle savedInstance) {
		super.onActivityCreated(savedInstance);
		initializeAdapter();
	}

	@Override
	public void onResume() {
		super.onResume();
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.formula_editor_data);
		BottomBar.showBottomBar(getActivity());
		BottomBar.hidePlayButton(getActivity());
	}

	@Override
	public void onStop() {
		super.onStop();
		finishActionMode();
		BottomBar.hideBottomBar(getActivity());
	}

	private void initializeAdapter() {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		DataContainer dataContainer = ProjectManager.getInstance().getCurrentScene().getDataContainer();

		List<UserVariable> globalVars = dataContainer.getProjectVariables();
		List<UserVariable> localVars = dataContainer.getOrCreateVariableListForSprite(currentSprite);
		List<UserList> globalLists = dataContainer.getProjectLists();
		List<UserList> localLists = dataContainer.getOrCreateUserListForSprite(currentSprite);

		adapter = new DataListAdapter(globalVars, localVars, globalLists, localLists);
		onAdapterReady();
	}

	private void onAdapterReady() {
		recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
				DividerItemDecoration.VERTICAL));
		recyclerView.setAdapter(adapter);
		adapter.setSelectionListener(this);
		adapter.setOnItemClickListener(this);
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
				startActionMode(DELETE);
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void startActionMode(@ActionModeType int type) {
		if (adapter.getItems().isEmpty()) {
			ToastUtil.showError(getActivity(), R.string.am_empty_list);
		} else {
			actionModeType = type;
			actionMode = getActivity().startActionMode(callback);
		}
	}

	protected void finishActionMode() {
		adapter.clearSelection();
		if (actionModeType != NONE) {
			actionMode.finish();
		}
	}

	public void handleAddButton() {
		NewDataDialogFragment dialog = new NewDataDialogFragment();
		dialog.setNewDataInterface(this);
		dialog.show(getFragmentManager(), NewDataDialogFragment.TAG);
	}

	@Override
	public void addItem(UserData item) {
		adapter.updateDataSet();
	}

	public void showDeleteAlert(final List<UserData> selectedItems) {
		new AlertDialog.Builder(getActivity())
				.setTitle(R.string.deletion_alert_title)
				.setMessage(R.string.deletion_alert_text)
				.setPositiveButton(R.string.deletion_alert_yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						deleteItems(selectedItems);
					}
				})
				.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						finishActionMode();
						dialog.dismiss();
					}
				})
				.setCancelable(false)
				.create()
				.show();
	}

	private void deleteItems(List<UserData> selectedItems) {
		finishActionMode();
		for (UserData item : selectedItems) {
			adapter.remove(item);
		}
		ToastUtil.showSuccess(getActivity(), getResources().getQuantityString(R.plurals.deleted_Items,
				selectedItems.size(),
				selectedItems.size()));
	}

	private void showRenameDialog(List<UserData> selectedItems) {
		String name = selectedItems.get(0).getName();
		RenameDialogFragment dialog = new RenameDialogFragment(R.string.rename_data_dialog, R.string.data_label, name, this);
		dialog.show(getFragmentManager(), RenameDialogFragment.TAG);
	}

	@Override
	public boolean isNameUnique(String name) {
		Set<String> scope = new HashSet<>();
		for (UserData item : adapter.getItems()) {
			scope.add(item.getName());
		}
		return !scope.contains(name);
	}

	@Override
	public void renameItem(String name) {
		UserData item = adapter.getSelectedItems().get(0);
		String previousName = item.getName();
		item.setName(name);
		adapter.updateDataSet();
		finishActionMode();

		if (item instanceof UserVariable) {
			formulaEditorDataInterface.onVariableRenamed(previousName, name);
		} else {
			formulaEditorDataInterface.onListRenamed(previousName, name);
		}
	}

	@Override
	public void onSelectionChanged(int selectedItemCnt) {
		actionMode.setTitle(actionModeTitle + " " + getResources().getQuantityString(R.plurals.am_user_data_items_title,
				selectedItemCnt,
				selectedItemCnt));
	}

	@Override
	public void onItemClick(UserData item) {
		if (actionModeType == NONE) {
			formulaEditorDataInterface.onDataItemSelected(item);
			getFragmentManager().popBackStack();
		}
	}

	@Override
	public void onItemLongClick(final UserData item, ViewHolder holder) {
		CharSequence[] items = new CharSequence[] {getString(R.string.delete), getString(R.string.rename)};
		new AlertDialog.Builder(getActivity())
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								showDeleteAlert(new ArrayList<>(Collections.singletonList(item)));
								break;
							case 1:
								adapter.setSelection(item, true);
								showRenameDialog(adapter.getSelectedItems());
								break;
							default:
								dialog.dismiss();
						}
					}
				})
				.show();
	}

	public interface FormulaEditorDataInterface {

		void onDataItemSelected(UserData item);
		void onVariableRenamed(String previousName, String newName);
		void onListRenamed(String previousName, String newName);
	}
}
