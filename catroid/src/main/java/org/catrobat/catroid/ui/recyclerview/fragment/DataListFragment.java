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

import android.os.Bundle;
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
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserData;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.recyclerview.adapter.DataListAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.RVAdapter;
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog;
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.RenameItemTextWatcher;
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableVH;
import org.catrobat.catroid.utils.ToastUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.IntDef;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class DataListFragment extends Fragment implements
		ActionMode.Callback,
		RVAdapter.SelectionListener,
		RVAdapter.OnItemClickListener<UserData> {

	public static final String TAG = DataListFragment.class.getSimpleName();

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({NONE, DELETE})
	@interface ActionModeType {
	}

	private static final int NONE = 0;
	private static final int DELETE = 1;

	private RecyclerView recyclerView;
	private DataListAdapter adapter;
	private ActionMode actionMode;

	private FormulaEditorDataInterface formulaEditorDataInterface;

	@ActionModeType
	protected int actionModeType = NONE;

	public void setFormulaEditorDataInterface(FormulaEditorDataInterface formulaEditorDataInterface) {
		this.formulaEditorDataInterface = formulaEditorDataInterface;
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		switch (actionModeType) {
			case DELETE:
				mode.setTitle(getString(R.string.am_delete));
				break;
			case NONE:
				return false;
		}
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);

		adapter.showCheckBoxes(true);
		adapter.updateDataSet();
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
		actionMode = null;
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
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		List<UserVariable> globalVars = currentProject.getUserVariables();
		List<UserVariable> localVars = currentSprite.getUserVariables();
		List<UserList> globalLists = currentProject.getUserLists();
		List<UserList> localLists = currentSprite.getUserLists();

		adapter = new DataListAdapter(globalVars, localVars, globalLists, localLists);
		onAdapterReady();
	}

	private void onAdapterReady() {
		recyclerView.setAdapter(adapter);
		adapter.setSelectionListener(this);
		adapter.setOnItemClickListener(this);
	}

	public void notifyDataSetChanged() {
		adapter.notifyDataSetChanged();
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
			actionMode = getActivity().startActionMode(this);
		}
	}

	protected void finishActionMode() {
		adapter.clearSelection();
		if (actionModeType != NONE) {
			actionMode.finish();
		}
	}

	public void showDeleteAlert(final List<UserData> selectedItems) {
		new AlertDialog.Builder(getContext())
				.setTitle(R.string.deletion_alert_title)
				.setMessage(R.string.deletion_alert_text)
				.setPositiveButton(R.string.deletion_alert_yes, (dialog, id) -> deleteItems(selectedItems))
				.setNegativeButton(R.string.no, null)
				.setCancelable(false)
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
		final UserData item = selectedItems.get(0);

		TextInputDialog.Builder builder = new TextInputDialog.Builder(getContext());

		builder.setHint(getString(R.string.data_label))
				.setText(item.getName())
				.setTextWatcher(new RenameItemTextWatcher<>(item, adapter.getItems()))
				.setPositiveButton(getString(R.string.ok), (TextInputDialog.OnClickListener) (dialog, textInput) -> renameItem(item, textInput));

		builder.setTitle(R.string.rename_data_dialog)
				.setNegativeButton(R.string.cancel, null)
				.show();
	}

	public void renameItem(UserData item, String name) {
		String previousName = item.getName();
		updateUserDataReferences(previousName, name, item);
		item.setName(name);
		adapter.updateDataSet();
		finishActionMode();

		if (item instanceof UserVariable) {
			formulaEditorDataInterface.onVariableRenamed(previousName, name);
		} else {
			formulaEditorDataInterface.onListRenamed(previousName, name);
		}
	}

	public static void updateUserDataReferences(String oldName, String newName,
			UserData item) {
		for (Scene scene : ProjectManager.getInstance().getCurrentProject().getSceneList()) {
			for (Sprite sprite : scene.getSpriteList()) {
				for (Script script : sprite.getScriptList()) {
					List<Brick> flatList = new ArrayList();
					script.addToFlatList(flatList);
					for (Brick brick : flatList) {
						if (brick instanceof FormulaBrick) {
							FormulaBrick formulaBrick = (FormulaBrick) brick;
							for (Formula formula : formulaBrick.getFormulas()) {
								if (item instanceof UserVariable) {
									formula.updateVariableName(oldName, newName);
								} else {
									formula.updateUserlistName(oldName, newName);
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void onSelectionChanged(int selectedItemCnt) {
		actionMode.setTitle(getResources().getQuantityString(R.plurals.am_delete_user_data_items_title,
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
	public void onItemLongClick(final UserData item, CheckableVH holder) {
		CharSequence[] items = new CharSequence[] {getString(R.string.delete), getString(R.string.rename)};
		new AlertDialog.Builder(getActivity())
				.setItems(items, (dialog, which) -> {
					switch (which) {
						case 0:
							showDeleteAlert(new ArrayList<>(Collections.singletonList(item)));
							break;
						case 1:
							showRenameDialog(new ArrayList<>(Collections.singletonList(item)));
							break;
						default:
							dialog.dismiss();
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
