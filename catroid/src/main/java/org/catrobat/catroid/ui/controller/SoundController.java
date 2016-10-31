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
package org.catrobat.catroid.ui.controller;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.SoundViewHolder;
import org.catrobat.catroid.ui.adapter.SoundBaseAdapter;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.ui.fragment.SoundFragment;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public final class SoundController {
	public static final int REQUEST_SELECT_OR_RECORD_SOUND = 0;
	public static final String BUNDLE_ARGUMENTS_SELECTED_SOUND = "selected_sound";
	public static final String SHARED_PREFERENCE_NAME = "showDetailsSounds";
	public static final int ID_LOADER_MEDIA_IMAGE = 1;
	public static final int REQUEST_SELECT_MUSIC = 0;
	public static final int REQUEST_MEDIA_LIBRARY = 2;

	private static final SoundController INSTANCE = new SoundController();
	private static final String TAG = SoundController.class.getSimpleName();

	private OnBackpackSoundCompleteListener onBackpackSoundCompleteListener;

	private SoundController() {
	}

	public static SoundController getInstance() {
		return INSTANCE;
	}

	/**
	 * Get a file path from a Uri. This will get the the path for Storage Access
	 * Framework Documents, as well as the _data field for the MediaStore and
	 * other file-based ContentProviders.
	 * <p/>
	 * <p/>
	 * solution according to:
	 * http://stackoverflow.com/questions/19834842/android-gallery-on-kitkat-returns-different-uri
	 * -for-intent-action-get-content
	 */
	@TargetApi(19)
	private static String getPathForVersionAboveEqualsVersion19(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			} else if (isDownloadsDocument(uri)) {
				// DownloadsProvider

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			} else if (isMediaDocument(uri)) {
				// MediaProvider
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		} else if ("content".equalsIgnoreCase(uri.getScheme())) {
			// MediaStore (and general)

			// Return the remote address
			if (isGooglePhotosUri(uri)) {
				return uri.getLastPathSegment();
			}
			return getDataColumn(context, uri, null, null);
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			// File
			return uri.getPath();
		}

		return null;
	}

	private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	private static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	private static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	private static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	private static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}

	public void updateSoundLogic(Context context, final int position, final SoundViewHolder holder,
			final SoundBaseAdapter soundAdapter) {
		final SoundInfo soundInfo = soundAdapter.getSoundInfoItems().get(position);

		if (soundInfo == null) {
			return;
		}
		holder.playAndStopButton.setTag(position);
		holder.titleTextView.setText(soundInfo.getTitle());

		handleCheckboxes(position, holder, soundAdapter);
		handleSoundInfo(holder, soundInfo, soundAdapter, position, context);
		handleDetails(soundAdapter, holder, soundInfo);
		setClickListener(soundAdapter, holder, soundInfo);
	}

	private void setClickListener(final SoundBaseAdapter soundAdapter, final SoundViewHolder holder,
			final SoundInfo soundInfo) {
		OnClickListener listItemOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (soundAdapter.getSelectMode() != ListView.CHOICE_MODE_NONE) {
					holder.checkbox.setChecked(!holder.checkbox.isChecked());
				}
			}
		};

		if (soundAdapter.getSelectMode() != ListView.CHOICE_MODE_NONE) {
			holder.playAndStopButton.setOnClickListener(listItemOnClickListener);
		} else {
			if (soundInfo.isPlaying) {
				holder.playAndStopButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if (soundAdapter.getOnSoundEditListener() != null) {
							soundAdapter.getOnSoundEditListener().onSoundPause(view);
						}
					}
				});
			} else {
				holder.playAndStopButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if (soundAdapter.getOnSoundEditListener() != null) {
							soundAdapter.getOnSoundEditListener().onSoundPlay(view);
						}
					}
				});
			}
		}
		holder.soundFragmentButtonLayout.setOnClickListener(listItemOnClickListener);
		setOnTouchListener(holder, soundAdapter);
	}

	private void setOnTouchListener(SoundViewHolder holder, final SoundBaseAdapter soundAdapter) {
		if (soundAdapter.backPackAdapter) {
			return;
		}

		holder.soundFragmentButtonLayout.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Intent intent = new Intent(ScriptActivity.ACTION_SOUND_TOUCH_ACTION_UP);
					soundAdapter.getContext().sendBroadcast(intent);
				}
				return false;
			}
		});
	}

	private void handleDetails(SoundBaseAdapter soundAdapter, SoundViewHolder holder, SoundInfo soundInfo) {
		if (soundAdapter.getShowDetails()) {
			holder.soundFileSizeTextView.setText(UtilFile.getSizeAsString(new File(soundInfo.getAbsolutePath())));
			holder.soundFileSizeTextView.setVisibility(TextView.VISIBLE);
			holder.soundFileSizePrefixTextView.setVisibility(TextView.VISIBLE);
		} else {
			holder.soundFileSizeTextView.setVisibility(TextView.GONE);
			holder.soundFileSizePrefixTextView.setVisibility(TextView.GONE);
		}
	}

	private void handleSoundInfo(SoundViewHolder holder, SoundInfo soundInfo, SoundBaseAdapter soundAdapter,
			int position, Context context) {
		try {
			MediaPlayer tempPlayer = new MediaPlayer();
			tempPlayer.setDataSource(soundInfo.getAbsolutePath());
			tempPlayer.prepare();

			long milliseconds = tempPlayer.getDuration();
			long seconds = milliseconds / 1000;
			if (seconds == 0) {
				seconds = 1;
			}
			String timeDisplayed = DateUtils.formatElapsedTime(seconds);

			holder.timePlayedChronometer.setText(timeDisplayed);
			holder.timePlayedChronometer.setVisibility(Chronometer.VISIBLE);

			if (soundAdapter.getCurrentPlayingPosition() == Constants.NO_POSITION) {
				SoundBaseAdapter.setElapsedMilliSeconds(0);
			} else {
				SoundBaseAdapter.setElapsedMilliSeconds(SystemClock.elapsedRealtime()
						- SoundBaseAdapter.getCurrentPlayingBase());
			}

			if (soundInfo.isPlaying) {
				holder.playAndStopButton.setImageResource(R.drawable.ic_media_stop);
				holder.playAndStopButton.setContentDescription(context.getString(R.string.sound_stop));

				if (soundAdapter.getCurrentPlayingPosition() == Constants.NO_POSITION) {
					startPlayingSound(holder.timePlayedChronometer, position, soundAdapter);
				} else if ((position == soundAdapter.getCurrentPlayingPosition())
						&& (SoundBaseAdapter.getElapsedMilliSeconds() > (milliseconds - 1000))) {
					stopPlayingSound(soundInfo, holder.timePlayedChronometer, soundAdapter);
				} else {
					continuePlayingSound(holder.timePlayedChronometer, SystemClock.elapsedRealtime());
				}
			} else {
				holder.playAndStopButton.setImageResource(R.drawable.ic_media_play);
				holder.playAndStopButton.setContentDescription(context.getString(R.string.sound_play));
				stopPlayingSound(soundInfo, holder.timePlayedChronometer, soundAdapter);
			}

			tempPlayer.reset();
			tempPlayer.release();
		} catch (IOException ioException) {
			Log.e(TAG, "Cannot get view.", ioException);
		}
	}

	private void handleCheckboxes(final int position, SoundViewHolder holder, final SoundBaseAdapter soundAdapter) {
		holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					if (soundAdapter.getSelectMode() == ListView.CHOICE_MODE_SINGLE) {
						soundAdapter.clearCheckedItems();
					}
					soundAdapter.getCheckedSounds().add(position);
				} else {
					soundAdapter.getCheckedSounds().remove(position);
				}
				soundAdapter.notifyDataSetChanged();

				if (soundAdapter.getOnSoundEditListener() != null) {
					soundAdapter.getOnSoundEditListener().onSoundChecked();
				}
			}
		});

		if (soundAdapter.getSelectMode() != ListView.CHOICE_MODE_NONE) {
			holder.checkbox.setVisibility(View.VISIBLE);
			holder.checkbox.setVisibility(View.VISIBLE);
			holder.soundFragmentButtonLayout.setBackgroundResource(R.drawable.button_background_shadowed);
		} else {
			holder.checkbox.setVisibility(View.GONE);
			holder.checkbox.setVisibility(View.GONE);
			holder.soundFragmentButtonLayout.setBackgroundResource(R.drawable.button_background_selector);
			holder.checkbox.setChecked(false);
			soundAdapter.clearCheckedItems();
		}

		if (soundAdapter.getCheckedSounds().contains(position)) {
			holder.checkbox.setChecked(true);
		} else {
			holder.checkbox.setChecked(false);
		}
	}

	private void startPlayingSound(Chronometer chronometer, int position, final SoundBaseAdapter soundAdapter) {
		soundAdapter.setCurrentPlayingPosition(position);
		SoundBaseAdapter.setCurrentPlayingBase(SystemClock.elapsedRealtime());
		continuePlayingSound(chronometer, SoundBaseAdapter.getCurrentPlayingBase());
	}

	private void continuePlayingSound(Chronometer chronometer, long base) {
		chronometer.setBase(base);
		chronometer.start();
	}

	private void stopPlayingSound(SoundInfo soundInfo, Chronometer chronometer, final SoundBaseAdapter soundAdapter) {
		chronometer.stop();
		soundAdapter.setCurrentPlayingPosition(Constants.NO_POSITION);
		soundInfo.isPlaying = false;
	}

	public String soundFileAlreadyInBackPackDirectory(SoundInfo soundInfoToCheck) {
		if (soundInfoToCheck == null) {
			return null;
		}
		for (SoundInfo soundInfo : BackPackListManager.getInstance().getAllBackPackedSounds()) {
			if (soundInfo == null) {
				continue;
			}
			if (soundInfo.getChecksum().equals(soundInfoToCheck.getChecksum())) {
				return soundInfo.getSoundFileName();
			}
		}
		return null;
	}

	public File copySoundBackPack(SoundInfo selectedSoundInfo, String newSoundInfoTitle, boolean copyFromBackpack) {
		try {
			return StorageHandler.getInstance().copySoundFileBackPack(selectedSoundInfo, newSoundInfoTitle,
					copyFromBackpack);
		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
		}
		return null;
	}

	public SoundInfo copySound(SoundInfo selectedSoundInfo, List<SoundInfo> soundInfoList, SoundFragment fragment) {
		try {
			StorageHandler.getInstance().copySoundFile(selectedSoundInfo.getAbsolutePath());
		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
		}
		return updateSoundAdapter(selectedSoundInfo.getTitle(), selectedSoundInfo.getSoundFileName(), soundInfoList,
				fragment);
	}

	public void copySound(int position, List<SoundInfo> soundInfoList, SoundBaseAdapter adapter) {
		SoundInfo soundInfo = soundInfoList.get(position);
		try {
			StorageHandler.getInstance().copySoundFile(soundInfo.getAbsolutePath());
		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
		}
		String newSoundInfoTitle = Utils.getUniqueSoundName(soundInfo, false);
		updateSoundAdapter(soundInfo, adapter, newSoundInfoTitle, false, false);
	}

	private void deleteSound(int position, List<SoundInfo> soundInfoList, Activity activity) {
		if (position < 0 || position >= soundInfoList.size()) {
			Log.d(TAG, "attempted to delete a sound at a position not in soundInfoList");
			return;
		}
		SoundInfo soundInfoToDelete = soundInfoList.get(position);
		if (soundInfoToDelete.isBackpackSoundInfo() && !otherSoundInfoItemsHaveAFileReference(soundInfoToDelete)) {
			StorageHandler.getInstance().deleteFile(soundInfoList.get(position).getAbsolutePath(), true);
		}

		soundInfoList.remove(position);
		ProjectManager.getInstance().getCurrentSprite().setSoundList(soundInfoList);
		activity.sendBroadcast(new Intent(ScriptActivity.ACTION_SOUND_DELETED));
	}

	public boolean otherSoundInfoItemsHaveAFileReference(SoundInfo soundInfoToCheck) {
		for (SoundInfo soundInfo : BackPackListManager.getInstance().getAllBackPackedSounds()) {
			if (soundInfo.equals(soundInfoToCheck)) {
				continue;
			}
			if (soundInfo.getSoundFileName().equals(soundInfoToCheck.getSoundFileName())) {
				return true;
			}
		}
		return false;
	}

	public void deleteCheckedSounds(Activity activity, SoundBaseAdapter adapter, List<SoundInfo> soundInfoList,
			MediaPlayer mediaPlayer) {
		SoundController.getInstance().stopSoundAndUpdateList(mediaPlayer, soundInfoList, adapter);
		Iterator iterator = ((TreeSet) adapter.getCheckedItems()).descendingIterator();
		while (iterator.hasNext()) {
			deleteSound((int) iterator.next(), soundInfoList, activity);
		}
	}

	public SoundInfo updateSoundBackPackAfterInsert(String title, SoundInfo currentSoundInfo, String
			existingFileNameInBackPackDirectory, boolean addToHiddenBackpack) {
		SoundInfo newSoundInfo = new SoundInfo();
		newSoundInfo.setBackpackSoundInfo(true);
		newSoundInfo.setTitle(title);

		if (existingFileNameInBackPackDirectory == null) {
			if (currentSoundInfo != null) {
				String fileName = currentSoundInfo.getSoundFileName();
				String fileFormat = fileName.substring(fileName.lastIndexOf('.'), fileName.length());
				fileName = fileName.substring(0, fileName.indexOf('_') + 1) + title + fileFormat;
				newSoundInfo.setSoundFileName(fileName);
			}
		} else {
			newSoundInfo.setSoundFileName(existingFileNameInBackPackDirectory);
		}

		if (addToHiddenBackpack) {
			BackPackListManager.getInstance().addSoundToHiddenBackpack(newSoundInfo);
		} else {
			BackPackListManager.getInstance().addSoundToBackPack(newSoundInfo);
		}

		return newSoundInfo;
	}

	public SoundInfo updateSoundAdapter(SoundInfo soundInfo,
			SoundBaseAdapter adapter, String title, boolean delete, boolean fromHiddenBackPack) {
		SoundInfo newSoundInfo = new SoundInfo();
		newSoundInfo.setTitle(title);
		String fileName = soundInfo.getSoundFileName();
		String fileFormat = fileName.substring(fileName.lastIndexOf('.'), fileName.length());
		fileName = fileName.substring(0, fileName.indexOf('_') + 1) + title + fileFormat;
		newSoundInfo.setSoundFileName(fileName);
		ProjectManager.getInstance().getCurrentSprite().getSoundList().add(newSoundInfo);

		if (delete) {
			if (fromHiddenBackPack) {
				BackPackListManager.getInstance().removeItemFromSoundHiddenBackpack(soundInfo);
			} else {
				BackPackListManager.getInstance().removeItemFromSoundBackPack(soundInfo);
			}
			if (!otherSoundInfoItemsHaveAFileReference(soundInfo)) {
				StorageHandler.getInstance().deleteFile(soundInfo.getAbsoluteBackPackPath(), true);
			}
		}

		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
		return newSoundInfo;
	}

	public boolean isSoundPlaying(MediaPlayer mediaPlayer) {
		return mediaPlayer.isPlaying();
	}

	public void stopSound(MediaPlayer mediaPlayer, List<SoundInfo> soundInfoList) {
		if (isSoundPlaying(mediaPlayer)) {
			mediaPlayer.stop();
		}

		for (int i = 0; i < soundInfoList.size(); i++) {
			soundInfoList.get(i).isPlaying = false;
		}
	}

	public void handlePlaySoundButton(View view, List<SoundInfo> soundInfoList, MediaPlayer mediaPlayer,
			final SoundBaseAdapter adapter) {
		final int position = (Integer) view.getTag();
		final SoundInfo soundInfo = soundInfoList.get(position);

		stopSound(mediaPlayer, soundInfoList);
		if (!soundInfo.isPlaying) {
			startSound(soundInfo, mediaPlayer);
			adapter.notifyDataSetChanged();
		}

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mediaplayer) {
				soundInfo.isPlaying = false;
				adapter.notifyDataSetChanged();
			}
		});
	}

	public void stopSoundAndUpdateList(MediaPlayer mediaPlayer, List<SoundInfo> soundInfoList,
			SoundBaseAdapter adapter) {
		if (!isSoundPlaying(mediaPlayer)) {
			return;
		}
		stopSound(mediaPlayer, soundInfoList);
		adapter.notifyDataSetChanged();
	}

	public void startSound(SoundInfo soundInfo, MediaPlayer mediaPlayer) {
		if (!soundInfo.isPlaying) {
			try {
				mediaPlayer.reset();
				mediaPlayer.setDataSource(soundInfo.getAbsolutePath());
				mediaPlayer.prepare();
				mediaPlayer.start();

				soundInfo.isPlaying = true;
			} catch (IOException ioException) {
				Log.e(TAG, "Cannot start sound.", ioException);
			}
		}
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle arguments, Activity activity) {
		Uri audioUri = null;

		if (arguments != null) {
			audioUri = (Uri) arguments.get(BUNDLE_ARGUMENTS_SELECTED_SOUND);
		}
		String[] projection = { MediaStore.Audio.Media.DATA };
		return new CursorLoader(activity, audioUri, projection, null, null, null);
	}

	public String onLoadFinished(Loader<Cursor> loader, Cursor data, Activity activity) {
		String audioPath = "";
		CursorLoader cursorLoader = (CursorLoader) loader;

		if (data == null) {
			audioPath = cursorLoader.getUri().getPath();
		} else {
			data.moveToFirst();
			audioPath = data.getString(data.getColumnIndex(MediaStore.Audio.Media.DATA));
		}

		//workaround for android 4.4 issue #848
		if (audioPath == null && Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
			audioPath = getPathForVersionAboveEqualsVersion19(activity, cursorLoader.getUri());
		}
		if (audioPath.equalsIgnoreCase("")) {
			Utils.showErrorDialog(activity, R.string.error_load_sound);
			audioPath = "";
			return audioPath;
		} else {
			return audioPath;
		}
	}

	public void addSoundFromMediaLibrary(String filePath, Activity activity,
			List<SoundInfo> soundData, SoundFragment fragment) {
		File mediaImage = null;
		mediaImage = new File(filePath);
		copySoundToCatroid(mediaImage.toString(), activity, soundData, fragment);
		File soundOnSdCard = new File(mediaImage.getPath());
		soundOnSdCard.delete();
	}

	public void switchToScriptFragment(SoundFragment fragment, ScriptActivity scriptActivity) {
		scriptActivity.setCurrentFragment(ScriptActivity.FRAGMENT_SCRIPTS);
		FragmentTransaction fragmentTransaction = scriptActivity.getFragmentManager().beginTransaction();
		fragmentTransaction.hide(fragment);
		fragmentTransaction.show(scriptActivity.getFragmentManager().findFragmentByTag(ScriptFragment.TAG));
		fragmentTransaction.commit();

		scriptActivity.setIsSoundFragmentFromPlaySoundBrickNewFalse();
		scriptActivity.setIsSoundFragmentHandleAddButtonHandled(false);
	}

	private void copySoundToCatroid(String originalSoundPath, Activity activity, List<SoundInfo> soundList,
			SoundFragment fragment) {
		try {
			File oldFile = new File(originalSoundPath);

			if (originalSoundPath.equals("")) {
				throw new IOException();
			}

			File soundFile = StorageHandler.getInstance().copySoundFile(originalSoundPath);

			String soundName;
			int extensionDotIndex = oldFile.getName().lastIndexOf('.');
			if (extensionDotIndex > 0) {
				soundName = oldFile.getName().substring(0, extensionDotIndex);
			} else {
				soundName = oldFile.getName();
			}

			String soundFileName = soundFile.getName();

			updateSoundAdapter(soundName, soundFileName, soundList, fragment);
		} catch (IOException e) {
			Utils.showErrorDialog(activity, R.string.error_load_sound);
		} catch (NullPointerException e) {
			Log.e(TAG, "probably originalSoundPath null; message: " + e.getMessage());
			Utils.showErrorDialog(activity, R.string.error_load_sound);
		}
		activity.sendBroadcast(new Intent(ScriptActivity.ACTION_BRICK_LIST_CHANGED));
	}

	public SoundInfo updateSoundAdapter(String name, String fileName, List<SoundInfo> soundList, SoundFragment
			fragment) {
		SoundInfo soundInfoToCheck = new SoundInfo();
		soundInfoToCheck.setSoundFileName(fileName);
		soundInfoToCheck.setTitle(name);
		name = Utils.getUniqueSoundName(soundInfoToCheck, false);

		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(fileName);
		soundInfo.setTitle(name);
		soundList.add(soundInfo);

		fragment.updateSoundAdapter(soundInfo);
		return soundInfo;
	}

	public boolean checkSoundReplaceInBackpack(List<SoundInfo> currentSoundInfoList) {
		boolean soundsAlreadyInBackpack = false;
		for (SoundInfo soundInfo : currentSoundInfoList) {
			soundsAlreadyInBackpack = checkSoundReplaceInBackpack(soundInfo);
			if (soundsAlreadyInBackpack) {
				return soundsAlreadyInBackpack;
			}
		}
		return soundsAlreadyInBackpack;
	}

	public boolean checkSoundReplaceInBackpack(SoundInfo currentSoundInfo) {
		return BackPackListManager.getInstance().backPackedSoundsContain(currentSoundInfo, true);
	}

	public void showBackPackReplaceDialog(final List<SoundInfo> currentSoundInfoList, final Context context) {
		Resources resources = context.getResources();
		String replaceLookMessage = resources.getString(R.string.backpack_replace_sound_multiple);

		AlertDialog dialog = new CustomAlertDialogBuilder(context)
				.setTitle(R.string.backpack)
				.setMessage(replaceLookMessage)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						for (SoundInfo currentSoundInfo : currentSoundInfoList) {
							backPackVisibleSound(currentSoundInfo);
						}
						onBackpackSoundCompleteListener.onBackpackSoundComplete(true);
						dialog.dismiss();
					}
				}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						onBackpackSoundCompleteListener.onBackpackSoundComplete(false);
						dialog.dismiss();
					}
				}).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	public void showBackPackReplaceDialog(final SoundInfo currentSoundInfo, final Context context) {
		Resources resources = context.getResources();
		String replaceLookMessage = resources.getString(R.string.backpack_replace_sound, currentSoundInfo.getTitle());

		AlertDialog dialog = new CustomAlertDialogBuilder(context)
				.setTitle(R.string.backpack)
				.setMessage(replaceLookMessage)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						backPackVisibleSound(currentSoundInfo);
						onBackpackSoundCompleteListener.onBackpackSoundComplete(true);
						dialog.dismiss();
					}
				}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	public void backPackVisibleSound(SoundInfo selectedSoundInfo) {
		String soundInfoTitle = selectedSoundInfo.getTitle();
		BackPackListManager.getInstance().removeItemFromSoundBackPackBySoundTitle(soundInfoTitle);
		backPack(selectedSoundInfo, soundInfoTitle, false);
	}

	public SoundInfo backPackHiddenSound(SoundInfo selectedSoundInfo) {
		if (BackPackListManager.getInstance().backPackedSoundsContain(selectedSoundInfo, false)) {
			return selectedSoundInfo;
		}
		String newSoundInfoTitle = Utils.getUniqueSoundName(selectedSoundInfo, true);
		return backPack(selectedSoundInfo, newSoundInfoTitle, true);
	}

	public SoundInfo backPack(SoundInfo selectedSoundInfo, String newSoundInfoTitle, boolean addToHiddenBackpack) {
		String existingFileNameInBackPackDirectory = soundFileAlreadyInBackPackDirectory(selectedSoundInfo);
		if (existingFileNameInBackPackDirectory == null && selectedSoundInfo != null
				&& selectedSoundInfo.getAbsolutePath() != null && !selectedSoundInfo.getAbsolutePath().isEmpty()) {
			copySoundBackPack(selectedSoundInfo, newSoundInfoTitle, false);
		}
		return updateSoundBackPackAfterInsert(newSoundInfoTitle, selectedSoundInfo,
				existingFileNameInBackPackDirectory, addToHiddenBackpack);
	}

	public SoundInfo unpack(SoundInfo currentSoundInfo, boolean deleteUnpackedItems, boolean fromHiddenBackPack) {
		String newSoundTitle = Utils.getUniqueSoundName(currentSoundInfo, false);
		if (copySoundBackPack(currentSoundInfo, newSoundTitle, true) != null) {
			return updateSoundAdapter(currentSoundInfo,
					BackPackListManager.getInstance().getCurrentSoundAdapter(), newSoundTitle, deleteUnpackedItems, fromHiddenBackPack);
		}
		return null;
	}

	public void setOnBackpackSoundCompleteListener(OnBackpackSoundCompleteListener listener) {
		onBackpackSoundCompleteListener = listener;
	}

	public interface OnBackpackSoundCompleteListener {
		void onBackpackSoundComplete(boolean startBackpackActivity);
	}
}
