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
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.SoundViewHolder;
import org.catrobat.catroid.ui.adapter.BackPackSoundAdapter;
import org.catrobat.catroid.ui.adapter.SoundBaseAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.SoundController;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.DeleteSoundDialog;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

public class BackPackSoundFragment extends BackPackActivityFragment implements SoundBaseAdapter.OnSoundEditListener,
		LoaderManager.LoaderCallbacks<Cursor>, Dialog.OnKeyListener {

	public static final String TAG = BackPackSoundFragment.class.getSimpleName();

	private static int selectedSoundPosition = Constants.NO_POSITION;

	private SoundDeletedReceiver soundDeletedReceiver;

	private ActionMode actionMode;
	private View selectAllActionModeButton;

	private static String actionModeTitle;

	private static String singleItemAppendixDeleteActionMode;
	private static String multipleItemAppendixDeleteActionMode;

	private MediaPlayer mediaPlayer;
	private BackPackSoundAdapter adapter;
	private SoundInfo selectedSoundInfoBackPack;

	private ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_sounds, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		listView = getListView();
		registerForContextMenu(listView);

		if (savedInstanceState != null) {
			selectedSoundInfoBackPack = (SoundInfo) savedInstanceState
					.getSerializable(SoundController.BUNDLE_ARGUMENTS_SELECTED_SOUND);
		}
		adapter = new BackPackSoundAdapter(getActivity(), R.layout.fragment_sound_soundlist_item,
				R.id.fragment_sound_item_title_text_view, BackPackListManager.getInstance().getSoundInfoArrayList(),
				false, this);
		adapter.setOnSoundEditListener(this);
		setListAdapter(adapter);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.copy).setVisible(false);
		if (BackPackListManager.getInstance().getSoundInfoArrayList().size() > 0) {
			menu.findItem(R.id.unpacking).setVisible(true);
		}
		BottomBar.hideBottomBar(getActivity());
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		if (SoundController.getInstance().isSoundPlaying(mediaPlayer)) {
			SoundController.getInstance().stopSoundAndUpdateList(mediaPlayer,
					BackPackListManager.getInstance().getSoundInfoArrayList(), adapter);
		}
		selectedSoundInfoBackPack = adapter.getItem(selectedSoundPosition);
		menu.setHeaderTitle(selectedSoundInfoBackPack.getTitle());
		adapter.addCheckedItem(((AdapterContextMenuInfo) menuInfo).position);

		getSherlockActivity().getMenuInflater().inflate(R.menu.context_menu_unpacking, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {

			case R.id.context_menu_unpacking:
				SoundController.getInstance().copySound(selectedSoundInfoBackPack,
						BackPackListManager.getCurrentSoundInfoArrayList(), BackPackListManager.getCurrentAdapter());
				String textForUnPacking = getResources().getQuantityString(R.plurals.unpacking_items_plural, 1);
				ToastUtil.showSuccess(getActivity(), selectedSoundInfoBackPack.getTitle() + " " + textForUnPacking);
				break;
			case R.id.context_menu_delete:
				showConfirmDeleteDialog();
				break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(SoundController.BUNDLE_ARGUMENTS_SELECTED_SOUND, selectedSoundInfoBackPack);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStart() {
		super.onStart();
		mediaPlayer = new MediaPlayer();
		initClickListener();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (soundDeletedReceiver == null) {
			soundDeletedReceiver = new SoundDeletedReceiver();
		}

		IntentFilter intentFilterDeleteSound = new IntentFilter(ScriptActivity.ACTION_SOUND_DELETED);
		getActivity().registerReceiver(soundDeletedReceiver, intentFilterDeleteSound);

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());

		setShowDetails(settings.getBoolean(SoundController.SHARED_PREFERENCE_NAME, false));
	}

	@Override
	public void onPause() {
		super.onPause();

		SoundController.getInstance().stopSound(mediaPlayer, BackPackListManager.getInstance().getSoundInfoArrayList());
		adapter.notifyDataSetChanged();

		if (soundDeletedReceiver != null) {
			getActivity().unregisterReceiver(soundDeletedReceiver);
		}

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();

		editor.putBoolean(SoundController.SHARED_PREFERENCE_NAME, getShowDetails());
		editor.commit();
	}

	@Override
	public void onStop() {
		super.onStop();
		mediaPlayer.reset();
		mediaPlayer.release();
		mediaPlayer = null;
	}

	private void initClickListener() {
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				selectedSoundPosition = position;
				return false;
			}
		});
	}

	private class SoundDeletedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_SOUND_DELETED)) {
				adapter.notifyDataSetChanged();
				getActivity().sendBroadcast(new Intent(ScriptActivity.ACTION_BRICK_LIST_CHANGED));
			}
		}
	}

	public View getView(int position, View convertView) {

		SoundViewHolder holder;

		if (convertView == null) {
			convertView = View.inflate(getActivity(), R.layout.fragment_sound_soundlist_item, null);

			holder = new SoundViewHolder();
			holder.playAndStopButton = (ImageButton) convertView.findViewById(R.id.fragment_sound_item_image_button);
			holder.playAndStopButton.setImageResource(R.drawable.ic_media_play);
			holder.playAndStopButton.setContentDescription(getString(R.string.sound_play));

			holder.soundFragmentButtonLayout = (LinearLayout) convertView
					.findViewById(R.id.fragment_sound_item_main_linear_layout);
			holder.checkbox = (CheckBox) convertView.findViewById(R.id.fragment_sound_item_checkbox);
			holder.titleTextView = (TextView) convertView.findViewById(R.id.fragment_sound_item_title_text_view);
			holder.timeSeparatorTextView = (TextView) convertView
					.findViewById(R.id.fragment_sound_item_time_seperator_text_view);
			holder.soundFileSizePrefixTextView = (TextView) convertView
					.findViewById(R.id.fragment_sound_item_size_prefix_text_view);
			holder.soundFileSizeTextView = (TextView) convertView.findViewById(R.id.fragment_sound_item_size_text_view);

			holder.timePlayedChronometer = (Chronometer) convertView
					.findViewById(R.id.fragment_sound_item_time_played_chronometer);

			convertView.setTag(holder);
		} else {
			holder = (SoundViewHolder) convertView.getTag();
		}
		SoundController controller = SoundController.getInstance();
		controller.updateSoundLogic(getActivity(), position, holder, adapter);

		return convertView;
	}

	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
	}

	@Override
	public void onSoundPlay(View view) {
		SoundController.getInstance().handlePlaySoundButton(view,
				BackPackListManager.getInstance().getSoundInfoArrayList(), mediaPlayer, adapter);
	}

	@Override
	public void onSoundPause(View view) {
		handlePauseSoundButton(view);
	}

	public void handlePauseSoundButton(View view) {
		final int position = (Integer) view.getTag();
		pauseSound(BackPackListManager.getInstance().getSoundInfoArrayList().get(position));
		adapter.notifyDataSetChanged();
	}

	public void pauseSound(SoundInfo soundInfo) {
		mediaPlayer.pause();
		soundInfo.isPlaying = false;
	}

	@Override
	public void onSoundChecked() {
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
			String appendix = multipleItemAppendixDeleteActionMode;

			if (numberOfSelectedItems == 1) {
				appendix = singleItemAppendixDeleteActionMode;
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

	@Override
	public boolean getShowDetails() {
		// TODO CHANGE THIS!!! (was just a quick fix)
		if (adapter != null) {
			return adapter.getShowDetails();
		} else {
			return false;
		}
	}

	@Override
	public void setShowDetails(boolean showDetails) {

		if (adapter != null) {
			adapter.setShowDetails(showDetails);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void setSelectMode(int selectMode) {
		adapter.setSelectMode(selectMode);
		adapter.notifyDataSetChanged();
	}

	@Override
	public int getSelectMode() {
		return adapter.getSelectMode();
	}

	public void clearCheckedSoundsAndEnableButtons() {
		setSelectMode(ListView.CHOICE_MODE_NONE);
		adapter.clearCheckedItems();

		actionMode = null;
		setActionModeActive(false);

		registerForContextMenu(listView);
		BottomBar.hideBottomBar(getActivity());
	}

	@Override
	public void startUnPackingActionMode() {
		if (actionMode == null) {
			SoundController.getInstance().stopSoundAndUpdateList(mediaPlayer,
					BackPackListManager.getInstance().getSoundInfoArrayList(), adapter);
			actionMode = getSherlockActivity().startActionMode(unpackingModeCallBack);
			unregisterForContextMenu(listView);
			BottomBar.hideBottomBar(getActivity());
		}
	}

	private void addSelectAllActionModeButton(ActionMode mode, Menu menu) {
		selectAllActionModeButton = Utils.addSelectAllActionModeButton(getLayoutInflater(null), mode, menu);
		selectAllActionModeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				for (int position = 0; position < adapter.getCount(); position++) {
					adapter.addCheckedItem(position);
				}
				adapter.notifyDataSetChanged();
				onSoundChecked();
			}
		});
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

			actionModeTitle = getString(R.string.unpacking);
			singleItemAppendixDeleteActionMode = getString(R.string.category_sound);
			multipleItemAppendixDeleteActionMode = getString(R.string.sounds);

			mode.setTitle(R.string.unpacking);
			addSelectAllActionModeButton(mode, menu);

			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			showUnpackingConfirmationMessage();
			adapter.onDestroyActionModeUnpacking(mode);
		}
	};

	@Override
	public void startDeleteActionMode() {

		if (actionMode == null) {
			SoundController.getInstance().stopSoundAndUpdateList(mediaPlayer,
					BackPackListManager.getInstance().getSoundInfoArrayList(), adapter);
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
			singleItemAppendixDeleteActionMode = getString(R.string.category_sound);
			multipleItemAppendixDeleteActionMode = getString(R.string.sounds);

			mode.setTitle(R.string.delete);
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
				clearCheckedSoundsAndEnableButtons();
			} else {
				showConfirmDeleteDialog();
			}
		}
	};

	private void showConfirmDeleteDialog() {
		int titleId;
		if (adapter.getAmountOfCheckedItems() == 1) {
			titleId = R.string.dialog_confirm_delete_sound_title;
		} else {
			titleId = R.string.dialog_confirm_delete_multiple_sounds_title;
		}

		AlertDialog.Builder builder = new CustomAlertDialogBuilder(getActivity());
		builder.setTitle(titleId);
		builder.setMessage(R.string.dialog_confirm_delete_sound_message);
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				SoundController.getInstance().stopSoundAndUpdateList(mediaPlayer,
						BackPackListManager.getInstance().getSoundInfoArrayList(), adapter);
				BackPackListManager.getInstance().getSoundInfoArrayList().removeAll(adapter.getCheckedSoundInfos());
				clearCheckedSoundsAndEnableButtons();
			}
		});
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				clearCheckedSoundsAndEnableButtons();
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	@Override
	protected void showDeleteDialog() {
		DeleteSoundDialog deleteSoundDialog = DeleteSoundDialog.newInstance(selectedSoundPosition);
		deleteSoundDialog.show(getFragmentManager(), DeleteSoundDialog.DIALOG_FRAGMENT_TAG);
	}

	private void showUnpackingConfirmationMessage() {
		String messageForUser = getResources().getQuantityString(R.plurals.unpacking_items_plural,
				adapter.getAmountOfCheckedItems());
		ToastUtil.showSuccess(getActivity(), messageForUser);
	}

	public BackPackSoundAdapter getAdapter() {
		return adapter;
	}
}
