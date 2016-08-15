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

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.LookDataHistory;
import org.catrobat.catroid.content.commands.LookCommands;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.LookViewHolder;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BackPackLookAdapter;
import org.catrobat.catroid.ui.adapter.LookBaseAdapter.OnLookEditListener;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.LookController;
import org.catrobat.catroid.ui.dialogs.DeleteLookDialog;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class BackPackLookFragment extends BackPackActivityFragment implements Dialog.OnKeyListener, OnLookEditListener {

	public static final String TAG = BackPackLookFragment.class.getSimpleName();
	protected String actionModeTitle;
	protected String singleItemAppendixActionMode;
	protected String multipleItemAppendixActionMode;
	private BackPackLookAdapter adapter;
	private LookData selectedLookDataBackPack;
	private int selectedLookPosition;
	private ListView listView;
	private ActionMode actionMode;
	private LookDeletedReceiver lookDeletedReceiver;
	private LooksListInitReceiver looksListInitReceiver;
	private View selectAllActionModeButton;
	private ActionMode.Callback deleteModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);
			setActionModeActive(true);

			actionModeTitle = getString(R.string.delete);
			singleItemAppendixActionMode = getString(R.string.look);
			multipleItemAppendixActionMode = getString(R.string.looks);

			mode.setTitle(actionModeTitle);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			if (adapter.getAmountOfCheckedItems() == 0) {
				clearCheckedLooksAndEnableButtons();
			} else {
				deleteLooks();
			}
		}
	};
	private ActionMode.Callback unpackingModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);
			setActionModeActive(true);

			mode.setTitle(R.string.unpack);

			actionModeTitle = getString(R.string.unpack);
			singleItemAppendixActionMode = getString(R.string.category_looks);
			multipleItemAppendixActionMode = getString(R.string.looks);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			if (adapter.getAmountOfCheckedItems() > 0) {
				showUnpackingConfirmationMessage();
			}
			adapter.onDestroyActionModeUnpacking();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_back_pack_look, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		listView = getListView();
		registerForContextMenu(listView);

		if (savedInstanceState != null) {
			setSelectedLookDataBackPack((LookData) savedInstanceState
					.getSerializable(LookController.BUNDLE_ARGUMENTS_SELECTED_LOOK));
		}

		adapter = new BackPackLookAdapter(getActivity(), R.layout.fragment_look_looklist_item,
				R.id.fragment_look_item_name_text_view, BackPackListManager.getInstance().getBackPackedLooks(), false, this);
		adapter.setOnLookEditListener(this);
		setListAdapter(adapter);
		checkEmptyBackgroundBackPack();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.copy).setVisible(false);
		if (!BackPackListManager.getInstance().getBackPackedLooks().isEmpty()) {
			menu.findItem(R.id.unpacking).setVisible(true);
		}
		menu.findItem(R.id.unpacking_keep).setVisible(false);
		BottomBar.hideBottomBar(getActivity());
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		selectedLookDataBackPack = adapter.getItem(selectedLookPosition);
		menu.setHeaderTitle(selectedLookDataBackPack.getLookName());
		adapter.addCheckedItem(((AdapterContextMenuInfo) menuInfo).position);

		getActivity().getMenuInflater().inflate(R.menu.context_menu_unpacking, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {

			case R.id.context_menu_unpacking_keep:
				contextMenuUnpacking(false);
				break;
			case R.id.context_menu_unpacking:
				contextMenuUnpacking(false);
				break;
			case R.id.context_menu_delete:
				deleteLooks();
				break;
		}
		return super.onContextItemSelected(item);
	}

	private void contextMenuUnpacking(boolean delete) {
		List<LookData> before = new ArrayList<>();
		before.addAll(ProjectManager.getInstance().getCurrentSprite().getLookDataList());
		LookController.getInstance().unpack(selectedLookDataBackPack, delete, false);
		List<LookData> after = ProjectManager.getInstance().getCurrentSprite().getLookDataList();
		if (before.size() < after.size()) {
			ArrayList<LookData> toAdd = new ArrayList<>();
			for (int i = before.size(); i < after.size(); i++) {
				toAdd.add(after.get(i));
			}
			LookCommands.AddLookCommand command = new LookCommands.AddLookCommand(toAdd);
			LookDataHistory.getInstance(ProjectManager.getInstance().getCurrentSprite()).add(command);
		}

		String textForUnPacking = getResources().getQuantityString(R.plurals.unpacking_items_plural, 1);
		ToastUtil.showSuccess(getActivity(), selectedLookDataBackPack.getLookName() + " " + textForUnPacking);
		((BackPackActivity) getActivity()).returnToScriptActivity(ScriptActivity.FRAGMENT_LOOKS);
	}

	private void deleteLooks() {
		LookController.getInstance().deleteCheckedLooks(adapter,
				BackPackListManager.getInstance().getBackPackedLooks(), getActivity());
		checkEmptyBackgroundBackPack();
		clearCheckedLooksAndEnableButtons();
	}

	public void clearCheckedLooksAndEnableButtons() {
		setSelectMode(ListView.CHOICE_MODE_NONE);
		adapter.clearCheckedItems();

		actionMode = null;
		setActionModeActive(false);

		registerForContextMenu(listView);
		BottomBar.hideBottomBar(getActivity());
	}

	private void initClickListener() {
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedLookPosition = position;
			}
		});
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(LookController.BUNDLE_ARGUMENTS_SELECTED_LOOK, selectedLookDataBackPack);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStart() {
		super.onStart();
		initClickListener();
	}

	@Override
	public boolean getShowDetails() {
		if (adapter != null) {
			return adapter.getShowDetails();
		}
		return false;
	}

	@Override
	public void setShowDetails(boolean showDetails) {
		if (adapter != null) {
			adapter.setShowDetails(showDetails);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public int getSelectMode() {
		return adapter.getSelectMode();
	}

	@Override
	public void setSelectMode(int selectMode) {
		adapter.setSelectMode(selectMode);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void startUnPackingActionMode(boolean deleteUnpackedItems) {
		startActionMode(unpackingModeCallBack, deleteUnpackedItems);
	}

	@Override
	public void startDeleteActionMode() {
		startActionMode(deleteModeCallBack, true);
	}

	private void startActionMode(ActionMode.Callback actionModeCallback, boolean deleteUnpackedItems) {
		if (actionMode == null) {
			if (adapter.isEmpty()) {
				if (actionModeCallback.equals(unpackingModeCallBack)) {
					((BackPackActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.unpack));
				} else if (actionModeCallback.equals(deleteModeCallBack)) {
					((BackPackActivity) getActivity()).showEmptyActionModeDialog(getString(R.string.delete));
				}
			} else {
				if (actionModeCallback.equals(unpackingModeCallBack)) {
					this.deleteUnpackedItems = deleteUnpackedItems;
				}
				actionMode = getActivity().startActionMode(actionModeCallback);
				unregisterForContextMenu(listView);
				BottomBar.hideBottomBar(getActivity());
			}
		}
	}

	private void addSelectAllActionModeButton(ActionMode mode, Menu menu) {
		selectAllActionModeButton = Utils.addSelectAllActionModeButton(getActivity().getLayoutInflater(), mode,
				menu);

		selectAllActionModeButton.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						for (int position = 0; position < adapter.getCount(); position++) {
							adapter.addCheckedItem(position);
						}
						adapter.notifyDataSetChanged();
						onLookChecked();
					}
				});
	}

	@Override
	protected void showDeleteDialog() {
		DeleteLookDialog deleteLookDialog = DeleteLookDialog.newInstance(selectedLookPosition);
		deleteLookDialog.show(getFragmentManager(), DeleteLookDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		return false;
	}

	private void showUnpackingConfirmationMessage() {
		String messageForUser = getResources().getQuantityString(R.plurals.unpacking_items_plural,
				adapter.getAmountOfCheckedItems());
		ToastUtil.showSuccess(getActivity(), messageForUser);
	}

	public void setSelectedLookDataBackPack(LookData selectedLookDataBackPack) {
		this.selectedLookDataBackPack = selectedLookDataBackPack;
	}

	public View getView(final int position, View convertView) {
		LookViewHolder holder;

		if (convertView == null) {
			convertView = View.inflate(getActivity(), R.layout.fragment_look_looklist_item, null);

			holder = new LookViewHolder();

			holder.lookImageView = (ImageView) convertView.findViewById(R.id.fragment_look_item_image_view);
			holder.checkbox = (CheckBox) convertView.findViewById(R.id.fragment_look_item_checkbox);
			holder.lookNameTextView = (TextView) convertView.findViewById(R.id.fragment_look_item_name_text_view);
			holder.lookDetailsLinearLayout = (LinearLayout) convertView
					.findViewById(R.id.fragment_look_item_detail_linear_layout);
			holder.lookFileSizeTextView = (TextView) holder.lookDetailsLinearLayout
					.findViewById(R.id.fragment_look_item_size_text_view);
			holder.lookMeasureTextView = (TextView) holder.lookDetailsLinearLayout
					.findViewById(R.id.fragment_look_item_measure_text_view);
			holder.lookElement = (RelativeLayout) convertView.findViewById(R.id.fragment_look_item_relative_layout);
			convertView.setTag(holder);
		} else {
			holder = (LookViewHolder) convertView.getTag();
		}

		holder.lookElement.setLongClickable(false);
		holder.lookElement.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					selectedLookPosition = position;
					listView.showContextMenuForChild(view);
				}
				return false;
			}
		});

		LookController.getInstance().updateLookLogic(position, holder, adapter);
		return convertView;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(getActivity())) {
			return;
		}

		if (lookDeletedReceiver == null) {
			lookDeletedReceiver = new LookDeletedReceiver();
		}

		if (looksListInitReceiver == null) {
			looksListInitReceiver = new LooksListInitReceiver();
		}

		IntentFilter intentFilterDeleteLook = new IntentFilter(ScriptActivity.ACTION_LOOK_DELETED);
		getActivity().registerReceiver(lookDeletedReceiver, intentFilterDeleteLook);

		IntentFilter intentFilterLooksListInit = new IntentFilter(ScriptActivity.ACTION_LOOKS_LIST_INIT);
		getActivity().registerReceiver(looksListInitReceiver, intentFilterLooksListInit);

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());

		setShowDetails(settings.getBoolean(LookController.SHARED_PREFERENCE_NAME, false));
	}

	@Override
	public void onPause() {
		super.onPause();

		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject(getActivity().getApplicationContext());
		}

		BackPackListManager.getInstance().saveBackpack();

		if (lookDeletedReceiver != null) {
			getActivity().unregisterReceiver(lookDeletedReceiver);
		}

		if (looksListInitReceiver != null) {
			getActivity().unregisterReceiver(looksListInitReceiver);
		}

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();

		editor.putBoolean(LookController.SHARED_PREFERENCE_NAME, getShowDetails());
		editor.commit();
	}

	public BackPackLookAdapter getAdapter() {
		return adapter;
	}

	@Override
	public void onLookEdit(View view) {
	}

	@Override
	public void onLookChecked() {
		if (actionMode == null) {
			return;
		}

		updateActionModeTitle();
		Utils.setSelectAllActionModeButtonVisibility(selectAllActionModeButton,
				adapter.getCount() > 0 && adapter.getAmountOfCheckedItems() != adapter.getCount());
	}

	private void updateActionModeTitle() {
		int numberOfSelectedItems = adapter.getAmountOfCheckedItems();

		if (numberOfSelectedItems == 0) {
			actionMode.setTitle(actionModeTitle);
		} else {
			String appendix = multipleItemAppendixActionMode;

			if (numberOfSelectedItems == 1) {
				appendix = singleItemAppendixActionMode;
			}

			String numberOfItems = Integer.toString(numberOfSelectedItems);
			String completeTitle = actionModeTitle + " " + numberOfItems + " " + appendix;

			int titleLength = actionModeTitle.length();

			Spannable completeSpannedTitle = new SpannableString(completeTitle);
			completeSpannedTitle.setSpan(
					new ForegroundColorSpan(getResources().getColor(R.color.actionbar_title_color)), titleLength + 1,
					titleLength + (1 + numberOfItems.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			actionMode.setTitle(completeSpannedTitle);
		}
	}

	public ActionMode getActionMode() {
		return actionMode;
	}

	public void setActionMode(ActionMode actionMode) {
		this.actionMode = actionMode;
	}

	private void checkEmptyBackgroundBackPack() {
		if (BackPackListManager.getInstance().getBackPackedLooks().isEmpty()) {
			TextView emptyViewHeading = (TextView) getActivity().findViewById(R.id.fragment_look_text_heading);
			emptyViewHeading.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60.0f);
			emptyViewHeading.setText(R.string.backpack);
			TextView emptyViewDescription = (TextView) getActivity().findViewById(R.id.fragment_look_text_description);
			emptyViewDescription.setText(R.string.is_empty);
		}
	}

	private class LookDeletedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_LOOK_DELETED)) {
				adapter.notifyDataSetChanged();
				getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_BRICK_LIST_CHANGED));
			}
		}
	}

	private class LooksListInitReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_LOOKS_LIST_INIT)) {
				adapter.notifyDataSetChanged();
			}
		}
	}
}
