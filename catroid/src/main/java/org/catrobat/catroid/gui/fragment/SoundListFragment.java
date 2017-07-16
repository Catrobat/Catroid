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

package org.catrobat.catroid.gui.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.data.SoundInfo;
import org.catrobat.catroid.data.SpriteInfo;
import org.catrobat.catroid.gui.adapter.RecyclerViewAdapter;
import org.catrobat.catroid.gui.adapter.ViewHolder;
import org.catrobat.catroid.gui.dialog.RenameItemDialog;
import org.catrobat.catroid.projecthandler.ProjectHolder;
import org.catrobat.catroid.storage.DirectoryPathInfo;
import org.catrobat.catroid.storage.FilePathInfo;

import java.io.IOException;

public class SoundListFragment extends RecyclerViewListFragment<SoundInfo> {

	public static final String TAG = SoundListFragment.class.getSimpleName();
	private SpriteInfo sprite;

	@Override
	public void onActivityCreated(Bundle savedInstance) {
		String sceneName = getActivity().getIntent().getStringExtra(SceneListFragment.SELECTED_SCENE);
		String spriteName = getActivity().getIntent().getStringExtra(SpriteListFragment.SELECTED_SPRITE);
		sprite = ProjectHolder.getInstance().getCurrentProject().getSceneByName(sceneName).getSpriteByName(spriteName);
		super.onActivityCreated(savedInstance);
	}

	@Override
	protected RecyclerViewAdapter<SoundInfo> createAdapter() {
		return new RecyclerViewAdapter<SoundInfo>(sprite.getSounds()) {

			private MediaPlayer mediaPlayer = new MediaPlayer();

			@Override
			public void onBindViewHolder(final ViewHolder holder, final int position) {
				super.onBindViewHolder(holder, position);

				final SoundInfo sound = items.get(holder.getAdapterPosition());
				holder.imageSwitcher.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mediaPlayer.isPlaying()) {
							stopSound();
						} else {
							playSound(sound);
						}
					}
				});
			}

			private void playSound(SoundInfo sound) {
				try {
					mediaPlayer.release();
					mediaPlayer = new MediaPlayer();
					mediaPlayer.setDataSource(sound.getFilePathInfo().getAbsolutePath());
					mediaPlayer.prepare();
					mediaPlayer.start();
				} catch (IOException e) {
					Log.e(TAG, Log.getStackTraceString(e));
				}
			}

			private void stopSound() {
				mediaPlayer.stop();
			}
		};
	}

	@Override
	protected Class getItemType() {
		return SoundInfo.class;
	}

	@Override
	protected DirectoryPathInfo getCurrentDirectory() {
		return sprite.getDirectoryInfo();
	}

	@Override
	public void addItem(String name) {
		SoundInfo sound = new SoundInfo(name, new FilePathInfo(getCurrentDirectory(), ""));
		adapter.addItem(sound);
	}

	@Override
	public void onItemClick(SoundInfo item) {
	}

	@Override
	protected void showRenameDialog(String name) {
		RenameItemDialog dialog = new RenameItemDialog(R.string.rename_sound_dialog, name, this);
		dialog.show(getFragmentManager(), RenameItemDialog.TAG);
	}
}
