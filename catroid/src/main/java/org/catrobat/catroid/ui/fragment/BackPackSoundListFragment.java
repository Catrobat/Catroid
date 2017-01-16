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

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.ui.adapter.CheckBoxListAdapter;
import org.catrobat.catroid.ui.adapter.SoundListAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.OldSoundController;

import java.util.List;

public class BackPackSoundListFragment extends BackPackActivityFragment implements CheckBoxListAdapter
		.ListItemClickHandler {

	public static final String TAG = BackPackSoundListFragment.class.getSimpleName();

	private SoundListAdapter soundAdapter;
	private MediaPlayer mediaPlayer;
	private SoundInfo soundInfoToEdit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_sound_backpack, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		itemIdentifier = R.plurals.sounds;
		deleteDialogTitle = R.plurals.dialog_delete_sound;

		if (savedInstanceState != null) {
			soundInfoToEdit = (SoundInfo) savedInstanceState.getSerializable(OldSoundController.BUNDLE_ARGUMENTS_SELECTED_SOUND);
		}

		initializeList();
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
		outState.putSerializable(OldSoundController.BUNDLE_ARGUMENTS_SELECTED_SOUND, soundInfoToEdit);
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
		loadShowDetailsPreferences(OldSoundController.SHARED_PREFERENCE_NAME);
	}

	@Override
	public void onPause() {
		super.onPause();
		OldSoundController.getInstance().stopSound(mediaPlayer, BackPackListManager.getInstance().getBackPackedSounds());
		soundAdapter.notifyDataSetChanged();
		putShowDetailsPreferences(OldSoundController.SHARED_PREFERENCE_NAME);
	}

	@Override
	public void onStop() {
		super.onStop();
		mediaPlayer.reset();
		mediaPlayer.release();
		mediaPlayer = null;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		if (OldSoundController.getInstance().isSoundPlaying(mediaPlayer)) {
			OldSoundController.getInstance().stopSound(mediaPlayer, BackPackListManager.getInstance().getBackPackedSounds());
		}

		soundInfoToEdit = soundAdapter.getItem(selectedItemPosition);
		menu.setHeaderTitle(soundInfoToEdit.getTitle());

		getActivity().getMenuInflater().inflate(R.menu.context_menu_backpack, menu);
	}

	@Override
	public void handleOnItemClick(int position, View view, Object listItem) {
		selectedItemPosition = position;
		getListView().showContextMenuForChild(view);
	}

	public void pauseSound(SoundInfo soundInfo) {
		mediaPlayer.pause();
		soundInfo.isPlaying = false;
	}

	@Override
	public void deleteCheckedItems() {
	}

	@Override
	protected void unpackCheckedItems() {
		if (soundAdapter.getCheckedItems().isEmpty()) {
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
		OldSoundController.getInstance().unpack(soundInfoToEdit, false, false);
	}
}
