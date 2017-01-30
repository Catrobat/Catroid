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

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.pocketmusic.PocketMusicActivity;
import org.catrobat.catroid.soundrecorder.SoundRecorderActivity;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.ui.adapter.CheckBoxListAdapter;
import org.catrobat.catroid.ui.adapter.SoundListAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.OldSoundController;
import org.catrobat.catroid.ui.controller.SoundController;
import org.catrobat.catroid.ui.dialogs.NewSoundDialog;
import org.catrobat.catroid.ui.dialogs.ReplaceInBackPackDialog;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListView;
import org.catrobat.catroid.utils.SnackbarUtil;

import java.util.List;

public class SoundListFragment extends ListActivityFragment implements CheckBoxListAdapter.ListItemClickHandler {

	public static final String TAG = SoundListFragment.class.getSimpleName();
	public static final String SHARED_PREFERENCE_NAME = "showSoundDetails";
	private static final String BUNDLE_ARGUMENTS_SOUND_TO_EDIT = "sound_to_edit";

	private MediaPlayer mediaPlayer;

	private SoundListAdapter soundAdapter;
	private DragAndDropListView listView;

	private List<SoundInfo> soundList;
	private SoundInfo soundToEdit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View soundListFragment = inflater.inflate(R.layout.fragment_sound_list, container, false);
		listView = (DragAndDropListView) soundListFragment.findViewById(android.R.id.list);

		setHasOptionsMenu(true);
		SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_sounds);

		return soundListFragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		itemIdentifier = R.plurals.sounds;
		deleteDialogTitle = R.plurals.dialog_delete_sound;
		replaceDialogMessage = R.plurals.dialog_replace_sound;

		if (savedInstanceState != null) {
			soundToEdit = (SoundInfo) savedInstanceState
					.getSerializable(OldSoundController.BUNDLE_ARGUMENTS_SELECTED_SOUND);
		}

		initializeList();
	}

	private void initializeList() {
		soundList = ProjectManager.getInstance().getCurrentSprite().getSoundList();

		soundAdapter = new SoundListAdapter(getActivity(), R.layout.list_item, soundList);

		setListAdapter(soundAdapter);
		soundAdapter.setListItemClickHandler(this);
		soundAdapter.setListItemLongClickHandler(listView);
		soundAdapter.setListItemCheckHandler(this);
		listView.setAdapterInterface(soundAdapter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(BUNDLE_ARGUMENTS_SOUND_TO_EDIT, soundToEdit);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();
		loadShowDetailsPreferences(SHARED_PREFERENCE_NAME);
	}

	@Override
	public void onPause() {
		super.onPause();
		saveCurrentProject();
		putShowDetailsPreferences(SHARED_PREFERENCE_NAME);
		//TODO: Stop all sounds
	}

	@Override
	public void onStart() {
		super.onStart();
		mediaPlayer = new MediaPlayer();
	}

	@Override
	public void onStop() {
		super.onStop();
		mediaPlayer.reset();
		mediaPlayer.release();
		mediaPlayer = null;
	}

	@TargetApi(19)
	private void disableGoogleDrive(Intent intent) {
		intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

//		if (resultCode == Activity.RESULT_OK && data != null) {
//			switch (requestCode) {
//				case OldSoundController.REQUEST_SELECT_MUSIC:
//					Bundle arguments = new Bundle();
//					arguments.putParcelable(OldSoundController.BUNDLE_ARGUMENTS_SELECTED_SOUND, data.getData());
//					break;
//				case OldSoundController.REQUEST_MEDIA_LIBRARY:
//					String filePath = data.getStringExtra(WebViewActivity.MEDIA_FILE_PATH);
//					OldSoundController.getInstance().addSoundFromMediaLibrary(filePath, getActivity(), soundList, this);
//			}
//		}
//		if (requestCode == OldSoundController.REQUEST_SELECT_MUSIC) {
//			Log.d(TAG, "onActivityResult RequestMusic");
//		}
	}

	public void addSoundRecord() {
		Intent intent = new Intent(getActivity(), SoundRecorderActivity.class);
		startActivityForResult(intent, OldSoundController.REQUEST_SELECT_OR_RECORD_SOUND);
	}

	public void addSoundChooseFile() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("audio/*");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			disableGoogleDrive(intent);
		}
//		startActivityForResult(Intent.createChooser(intent, getString(R.string.sound_select_source)),
//				OldSoundController.REQUEST_SELECT_MUSIC);
	}

	public void addSoundMediaLibrary() {
		Intent intent = new Intent(getActivity(), WebViewActivity.class);
		String url = Constants.LIBRARY_SOUNDS_URL;;
		intent.putExtra(WebViewActivity.INTENT_PARAMETER_URL, url);
		intent.putExtra(WebViewActivity.CALLING_ACTIVITY, TAG);
		//startActivityForResult(intent, OldSoundController.REQUEST_MEDIA_LIBRARY);
	}

	public void addPocketMusic() {
		Intent intent = new Intent(getActivity(), PocketMusicActivity.class);
		startActivity(intent);
	}

	@Override
	public void handleAddButton() {
		NewSoundDialog dialog = new NewSoundDialog();
		dialog.show(getFragmentManager(), NewSoundDialog.TAG);
	}

	@Override
	public void handleOnItemClick(int position, View view, Object listItem) {
		if (!BuildConfig.FEATURE_POCKETMUSIC_ENABLED) {
			return;
		}

		soundToEdit = (SoundInfo) listItem;

		if (soundToEdit.getSoundFileName().matches(".*MUS-.*\\.midi")) {
			Intent intent = new Intent(getActivity(), PocketMusicActivity.class);

			intent.putExtra("FILENAME", soundToEdit.getSoundFileName());
			intent.putExtra("TITLE", soundToEdit.getTitle());

			startActivity(intent);
		}
	}

	@Override
	public void deleteCheckedItems() {
	}

	@Override
	protected void copyCheckedItems() {
	}

	@Override
	public void showRenameDialog() {
	}

	@Override
	public boolean itemNameExists(String newName) {
		return false;
	}

	@Override
	public void renameItem(String newName) {
	}

	@Override
	public void showReplaceItemsInBackPackDialog() {
		if (!SoundController.existsInBackPack(soundAdapter.getCheckedItems())) {
			packCheckedItems();
			return;
		}

		String name = soundAdapter.getCheckedItems().get(0).getTitle();
		ReplaceInBackPackDialog dialog = new ReplaceInBackPackDialog(replaceDialogMessage, name, this);
		dialog.show(getFragmentManager(), ReplaceInBackPackDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void packCheckedItems() {
	}

	@Override
	protected boolean isBackPackEmpty() {
		return BackPackListManager.getInstance().getBackPackedSounds().isEmpty();
	}

	@Override
	protected void changeToBackPack() {
		Intent intent = new Intent(getActivity(), BackPackActivity.class);
		intent.putExtra(BackPackActivity.FRAGMENT, BackPackSoundListFragment.class);
		startActivity(intent);
	}
}
