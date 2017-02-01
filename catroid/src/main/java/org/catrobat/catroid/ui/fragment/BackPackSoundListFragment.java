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

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.adapter.CheckBoxListAdapter;
import org.catrobat.catroid.ui.adapter.SoundListAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.SoundController;

import java.util.List;

public class BackPackSoundListFragment extends BackPackActivityFragment implements CheckBoxListAdapter
		.ListItemClickHandler, CheckBoxListAdapter.ListItemLongClickHandler {

	public static final String TAG = BackPackSoundListFragment.class.getSimpleName();

	private SoundListAdapter soundAdapter;
	private ListView listView;

	private MediaPlayer mediaPlayer;

	private SoundInfo soundInfoToEdit;
	private int selectedSoundPosition = Constants.NO_POSITION;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View backPackSoundListFragment = inflater.inflate(R.layout.fragment_sound_backpack, container, false);
		listView = (ListView) backPackSoundListFragment.findViewById(android.R.id.list);

		return backPackSoundListFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		registerForContextMenu(listView);

		singleItemTitle = getString(R.string.sound);
		multipleItemsTitle = getString(R.string.sounds);

		if (savedInstanceState != null) {
			soundInfoToEdit = (SoundInfo) savedInstanceState
					.getSerializable(SoundController.BUNDLE_ARGUMENTS_SELECTED_SOUND);
		}

		initializeList();
		checkEmptyBackgroundBackPack();
		BottomBar.hideBottomBar(getActivity());
	}

	private void initializeList() {
		List<SoundInfo> soundList = BackPackListManager.getInstance().getBackPackedSounds();

		soundAdapter = new SoundListAdapter(getActivity(), R.layout.list_item, soundList);
		setListAdapter(soundAdapter);
		soundAdapter.setListItemClickHandler(this);
		soundAdapter.setListItemCheckHandler(this);
		soundAdapter.setListItemLongClickHandler(this);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(SoundController.BUNDLE_ARGUMENTS_SELECTED_SOUND, soundInfoToEdit);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStart() {
		super.onStart();
		mediaPlayer = new MediaPlayer();
	}

	@Override
	public void onResume() {
		super.onResume();

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity()
				.getApplicationContext());

		setShowDetails(settings.getBoolean(SoundController.SHARED_PREFERENCE_NAME, false));
	}

	@Override
	public void onPause() {
		super.onPause();

		BackPackListManager.getInstance().saveBackpack();
		SoundController.getInstance().stopSound(mediaPlayer, BackPackListManager.getInstance().getBackPackedSounds());
		soundAdapter.notifyDataSetChanged();

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

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if (BackPackListManager.getInstance().getBackPackedSounds().isEmpty()) {
			menu.findItem(R.id.unpacking).setVisible(false);
		}

		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		if (SoundController.getInstance().isSoundPlaying(mediaPlayer)) {
			SoundController.getInstance().stopSound(mediaPlayer, BackPackListManager.getInstance().getBackPackedSounds());
		}

		soundInfoToEdit = soundAdapter.getItem(selectedSoundPosition);
		menu.setHeaderTitle(soundInfoToEdit.getTitle());

		getActivity().getMenuInflater().inflate(R.menu.context_menu_unpacking, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.context_menu_unpacking:
				unpackCheckedItems(true);
				break;
			case R.id.context_menu_delete:
				deleteCheckedItems(true);
				break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void handleOnItemClick(int position, View view, Object listItem) {
		selectedSoundPosition = position;
		soundInfoToEdit = soundAdapter.getItem(position);
		listView.showContextMenuForChild(view);
	}

	@Override
	public void handleOnItemLongClick(int position, View view) {
		selectedSoundPosition = position;
		soundInfoToEdit = soundAdapter.getItem(position);
		listView.showContextMenuForChild(view);
	}

	public void pauseSound(SoundInfo soundInfo) {
		mediaPlayer.pause();
		soundInfo.isPlaying = false;
	}

	@Override
	protected void showDeleteDialog(boolean singleItem) {
	}

	@Override
	protected void deleteCheckedItems(boolean singleItem) {
	}

	protected void unpackCheckedItems(boolean singleItem) {
		if (singleItem) {
			unpackSound();
			showUnpackingCompleteToast(1);
			return;
		}
		for (SoundInfo soundInfo : soundAdapter.getCheckedItems()) {
			soundInfoToEdit = soundInfo;
			unpackSound();
		}
		showUnpackingCompleteToast(soundAdapter.getCheckedItems().size());
		clearCheckedItems();
	}

	private void unpackSound() {
		SoundController.getInstance().unpack(soundInfoToEdit, false, false);
	}
}
