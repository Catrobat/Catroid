/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.LookViewHolder;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BackPackLookAdapter;
import org.catrobat.catroid.ui.adapter.LookBaseAdapter.OnLookEditListener;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.LookController;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.DeleteLookDialog;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

public class BackPackLookFragment extends BackPackActivityFragment implements Dialog.OnKeyListener, OnLookEditListener {

	private BackPackLookAdapter adapter;
	private LookData selectedLookDataBackPack;
	private int selectedLookPosition;
	private ListView listView;
	private ActionMode actionMode;
	protected String singleItemAppendixDeleteActionMode;
	protected String multipleItemAppendixDeleteActionMode;
	protected String actionModeTitle;
	protected String singleItemAppendixActionMode;
	protected String multipleItemAppendixActionMode;
	private LookDeletedReceiver lookDeletedReceiver;
	private LooksListInitReceiver looksListInitReceiver;

	public static final String TAG = BackPackLookFragment.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_look, container, false);
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

		if (ProjectManager.getInstance().getCurrentSpritePosition() == 0) {
			TextView emptyViewHeading = (TextView) getActivity().findViewById(R.id.fragment_look_text_heading);
			emptyViewHeading.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60.0f);
			emptyViewHeading.setText(R.string.backgrounds);
			TextView emptyViewDescription = (TextView) getActivity().findViewById(R.id.fragment_look_text_description);
			emptyViewDescription.setText(R.string.fragment_background_text_description);
		}
		adapter = new BackPackLookAdapter(getActivity(), R.layout.fragment_look_looklist_item,
				R.id.fragment_look_item_name_text_view, BackPackListManager.getInstance().getLookDataArrayList(), false);
		adapter.setOnLookEditListener(this);
		setListAdapter(adapter);
		adapter.setBackpackLookFragment(this);
		adapter.setCurrentActivity(getActivity());
		Utils.loadProjectIfNeeded(getActivity());
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if (BackPackListManager.getInstance().getLookDataArrayList().size() > 0) {
			menu.findItem(R.id.unpacking).setVisible(true);
		}
		BottomBar.hideBottomBar(getActivity());
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		selectedLookDataBackPack = adapter.getItem(selectedLookPosition);
		menu.setHeaderTitle(selectedLookDataBackPack.getLookName());
		adapter.addCheckedItem(((AdapterContextMenuInfo) menuInfo).position);

		getSherlockActivity().getMenuInflater().inflate(R.menu.context_menu_unpacking, menu);

		menu.findItem(R.id.context_menu_unpacking).setVisible(true);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {

			case R.id.context_menu_unpacking:

				LookController.getInstance().copyLookUnpacking(selectedLookDataBackPack,
						BackPackListManager.getCurrentLookDataArrayList(),
						BackPackListManager.getInstance().getCurrentLookFragment().getLookDataList(), getActivity(),
						BackPackListManager.getInstance().getCurrentLookFragment(), adapter);

				String textForUnPacking = getResources().getQuantityString(R.plurals.unpacking_items_plural, 1);
				ToastUtil.showSuccess(getActivity(), selectedLookDataBackPack.getLookName() + " " + textForUnPacking);
				break;
			case R.id.context_menu_delete:
				showConfirmDeleteDialog();
				break;
		}
		return super.onContextItemSelected(item);
	}

	private void showConfirmDeleteDialog() {
		int titleId;
		if (adapter.getAmountOfCheckedItems() == 1) {
			titleId = R.string.dialog_confirm_delete_look_title;
		} else {
			titleId = R.string.dialog_confirm_delete_multiple_looks_title;
		}

		AlertDialog.Builder builder = new CustomAlertDialogBuilder(getActivity());
		builder.setTitle(titleId);
		builder.setMessage(R.string.dialog_confirm_delete_look_message);
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				LookController.getInstance().deleteCheckedLooks(adapter,
						BackPackListManager.getInstance().getLookDataArrayList(), getActivity());
				clearCheckedLooksAndEnableButtons();
			}
		});
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				clearCheckedLooksAndEnableButtons();
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	public void clearCheckedLooksAndEnableButtons() {
		setSelectMode(ListView.CHOICE_MODE_NONE);
		adapter.clearCheckedItems();

		actionMode = null;
		setActionModeActive(false);

		registerForContextMenu(listView);
		BottomBar.showBottomBar(getActivity());
	}

	private void initClickListener() {
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				selectedLookPosition = position;
				return false;
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
	public void startDeleteActionMode() {
		if (actionMode == null) {
			actionMode = getSherlockActivity().startActionMode(deleteModeCallBack);
			unregisterForContextMenu(listView);
			BottomBar.hideBottomBar(getActivity());

		}
	}

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
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			if (adapter.getAmountOfCheckedItems() == 0) {
				clearCheckedLooksAndEnableButtons();
			} else {
				showConfirmDeleteDialog();
			}
		}
	};

	private void addSelectAllActionModeButton(ActionMode mode, Menu menu) {
		Utils.addSelectAllActionModeButton(getLayoutInflater(null), mode, menu).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View view) {
						for (int position = 0; position < BackPackListManager.getInstance().getLookDataArrayList()
								.size(); position++) {
							adapter.addCheckedItem(position);
						}
						adapter.notifyDataSetChanged();
						view.setVisibility(View.GONE);
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

	@Override
	public void startUnPackingActionMode() {
		if (actionMode == null) {
			actionMode = getSherlockActivity().startActionMode(unpackingModeCallBack);
			unregisterForContextMenu(listView);
			BottomBar.hideBottomBar(getActivity());
		}
	}

	private ActionMode.Callback unpackingModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			setSelectMode(ListView.CHOICE_MODE_MULTIPLE);
			setActionModeActive(true);

			mode.setTitle(R.string.unpacking);

			actionModeTitle = getString(R.string.unpacking);
			singleItemAppendixDeleteActionMode = getString(R.string.category_looks);
			multipleItemAppendixDeleteActionMode = getString(R.string.looks);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			showUnpackingConfirmationMessage();
			adapter.onDestroyActionModeUnpacking();
		}
	};

	private void showUnpackingConfirmationMessage() {
		String messageForUser = getResources().getQuantityString(R.plurals.unpacking_items_plural,
				adapter.getAmountOfCheckedItems());
		ToastUtil.showSuccess(getActivity(), messageForUser);
	}

	public LookData getSelectedLookDataBackPack() {
		return selectedLookDataBackPack;
	}

	public void setSelectedLookDataBackPack(LookData selectedLookDataBackPack) {
		this.selectedLookDataBackPack = selectedLookDataBackPack;
	}

	public View getView(int position, View convertView) {
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
			holder.lookArrowView = (ImageView) convertView.findViewById(R.id.fragment_look_item_arrow_image_view);
			holder.lookElement = (RelativeLayout) convertView.findViewById(R.id.fragment_look_item_relative_layout);
			convertView.setTag(holder);
		} else {
			holder = (LookViewHolder) convertView.getTag();
		}

		LookController controller = LookController.getInstance();
		controller.updateLookLogic(position, holder, adapter);
		return convertView;
	}

	public void updateLookAdapterBackPack(LookData lookData) {
		adapter.notifyDataSetChanged();
		final ListView listView = getListView();
		listView.post(new Runnable() {
			@Override
			public void run() {
				listView.setSelection(listView.getCount() - 1);
			}
		});
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
			projectManager.saveProject();
		}

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
	}

	public ActionMode getActionMode() {
		return actionMode;
	}

	public void setActionMode(ActionMode actionMode) {
		this.actionMode = actionMode;
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
