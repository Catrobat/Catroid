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

package org.catrobat.catroid.content.commands;

import android.media.MediaPlayer;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.ui.adapter.SoundBaseAdapter;
import org.catrobat.catroid.ui.controller.SoundController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SoundCommands {
	public static class RenameSoundCommand implements MediaCommand {
		private String oldName;
		private String newName;
		private SoundInfo soundInfo;

		public RenameSoundCommand(SoundInfo soundInfo, String newName) {
			this.oldName = soundInfo.getTitle();
			this.newName = newName;
			this.soundInfo = soundInfo;
		}

		@Override
		public void execute() {
			soundInfo.setTitle(newName);
		}

		@Override
		public void undo() {
			soundInfo.setTitle(oldName);
		}

		@Override
		public void update() {
			SoundInfo temp = ProjectManager.getInstance().getSoundById(soundInfo.getId());
			if (temp != null) {
				soundInfo = temp;
			}
		}
	}

	public static class AddSoundCommand implements MediaCommand {
		private ArrayList<SoundInfo> sounds;
		Map<SoundInfo, PlaySoundBrick> bricks = new HashMap<>();
		private MediaPlayer player;
		SoundBaseAdapter adapter;

		public AddSoundCommand(ArrayList<SoundInfo> sounds, MediaPlayer player, SoundBaseAdapter adapter) {
			this.sounds = sounds;
			this.player = player;
			this.adapter = adapter;
		}

		@Override
		public void execute() {
			ProjectManager.getInstance().getCurrentSprite().getSoundList().addAll(sounds);

			for (SoundInfo soundInfo : bricks.keySet()) {
				bricks.get(soundInfo).setSoundInfo(soundInfo);
			}

			bricks.clear();
		}

		@Override
		public void undo() {
			for (PlaySoundBrick brick : ProjectManager.getInstance().getCurrentSprite().getPlaySoundBricks()) {
				if (sounds.contains(brick.getSoundInfo())) {
					bricks.put(brick.getSoundInfo(), brick);
				}
			}
			SoundController.getInstance().stopSoundAndUpdateList(player,
					ProjectManager.getInstance().getCurrentSprite().getSoundList(), adapter);
			ProjectManager.getInstance().getCurrentSprite().getSoundList().removeAll(sounds);

			for (SoundInfo sound : sounds) {
				sound.isPlaying = false;
			}
		}

		@Override
		public void update() {
			for (int i = 0; i < sounds.size(); i++) {
				SoundInfo temp = ProjectManager.getInstance().getSoundById(sounds.get(i).getId());
				if (temp != null) {
					sounds.remove(i);
					sounds.add(i, temp);
				}
			}

			Map<SoundInfo, PlaySoundBrick> temp = new HashMap<>();

			for (Map.Entry<SoundInfo, PlaySoundBrick> entry : bricks.entrySet()) {
				Map.Entry<SoundInfo, PlaySoundBrick> tempEntry = entry;
				PlaySoundBrick tempBrick = ProjectManager.getInstance().getPlaySoundBrickById(entry.getValue().getId());
				SoundInfo tempSound = ProjectManager.getInstance().getSoundById(entry.getKey().getId());
				if (tempBrick != null) {
					tempEntry.setValue(tempBrick);
				}

				if (tempSound != null) {
					temp.put(tempSound, tempEntry.getValue());
				} else {
					temp.put(tempEntry.getKey(), tempEntry.getValue());
				}
			}

			bricks.clear();
			bricks.putAll(temp);
		}

		public void updateMediaPlayer(MediaPlayer player) {
			this.player = player;
		}
	}

	public static class DeleteSoundCommand implements MediaCommand {
		SoundBaseAdapter adapter;
		MediaPlayer player;
		private ArrayList<Integer> positions = new ArrayList<>();
		Map<SoundInfo, PlaySoundBrick> bricks = new HashMap<>();
		ArrayList<SoundInfo> soundsToDelete = new ArrayList<>();

		public DeleteSoundCommand(SoundBaseAdapter adapter, MediaPlayer mediaPlayer) {
			this.adapter = adapter;
			this.player = mediaPlayer;
			this.soundsToDelete = adapter.getCheckedSoundInfos();
		}

		@Override
		public void execute() {
			for (PlaySoundBrick brick : ProjectManager.getInstance().getCurrentSprite().getPlaySoundBricks()) {
				if (soundsToDelete.contains(brick.getSoundInfo())) {
					bricks.put(brick.getSoundInfo(), brick);
				}
			}
			for (SoundInfo soundInfo : soundsToDelete) {
				positions.add(ProjectManager.getInstance().getCurrentSprite().getSoundPositionById(soundInfo));
			}
			SoundController.getInstance().stopSoundAndUpdateList(player,
					ProjectManager.getInstance().getCurrentSprite().getSoundList(), adapter);
			ProjectManager.getInstance().getCurrentSprite().getSoundList().removeAll(soundsToDelete);
			for (SoundInfo sound : soundsToDelete) {
				sound.isPlaying = false;
			}
		}

		@Override
		public void undo() {
			for (int i = 0; i < soundsToDelete.size(); i++) {
				ProjectManager.getInstance().getCurrentSprite().getSoundList().add(positions.get(i), soundsToDelete.get(i));
			}

			for (SoundInfo soundInfo : bricks.keySet()) {
				bricks.get(soundInfo).setSoundInfo(soundInfo);
			}

			positions.clear();
			bricks.clear();
		}

		@Override
		public void update() {
			for (int i = 0; i < this.soundsToDelete.size(); i++) {
				SoundInfo temp = ProjectManager.getInstance().getSoundById(soundsToDelete.get(i).getId());
				if (temp != null) {
					soundsToDelete.remove(i);
					soundsToDelete.add(i, temp);
				}
			}

			Map<SoundInfo, PlaySoundBrick> temp = new HashMap<>();

			for (Map.Entry<SoundInfo, PlaySoundBrick> entry : bricks.entrySet()) {
				Map.Entry<SoundInfo, PlaySoundBrick> tempEntry = entry;
				PlaySoundBrick tempBrick = ProjectManager.getInstance().getPlaySoundBrickById(entry.getValue().getId());
				SoundInfo tempSound = ProjectManager.getInstance().getSoundById(entry.getKey().getId());
				if (tempBrick != null) {
					tempEntry.setValue(tempBrick);
				}

				if (tempSound != null) {
					temp.put(tempSound, tempEntry.getValue());
				} else {
					temp.put(tempEntry.getKey(), tempEntry.getValue());
				}
			}

			bricks.clear();
			bricks.putAll(temp);
		}

		public void updatePlayer(MediaPlayer player) {
			this.player = player;
		}
	}

	public static class MoveSoundCommand implements MediaCommand {
		private int previousPosition;
		private int newPosition;

		public MoveSoundCommand(int previousPosition, int newPosition) {
			this.previousPosition = previousPosition;
			this.newPosition = newPosition;
		}

		@Override
		public void execute() {
			ArrayList<SoundInfo> soundList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
			Collections.swap(soundList, previousPosition, newPosition);
		}

		@Override
		public void undo() {
			ArrayList<SoundInfo> soundList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
			Collections.swap(soundList, newPosition, previousPosition);
		}

		@Override
		public void update() {
		}
	}

	public static class MoveSoundToBottomCommand implements MediaCommand {
		private int position;

		public MoveSoundToBottomCommand(int position) {
			this.position = position;
		}

		@Override
		public void execute() {
			ArrayList<SoundInfo> soundList = ProjectManager.getInstance().getCurrentSprite().getSoundList();

			for (int i = position; i < soundList.size() - 1; i++) {
				Collections.swap(soundList, i, i + 1);
			}
		}

		@Override
		public void undo() {
			ArrayList<SoundInfo> soundList = ProjectManager.getInstance().getCurrentSprite().getSoundList();

			for (int i = soundList.size() - 1; i > position; i--) {
				Collections.swap(soundList, i, i - 1);
			}
		}

		@Override
		public void update() {
		}
	}

	public static class MoveSoundToTopCommand implements MediaCommand {
		private int position;

		public MoveSoundToTopCommand(int position) {
			this.position = position;
		}

		@Override
		public void execute() {
			ArrayList<SoundInfo> soundList = ProjectManager.getInstance().getCurrentSprite().getSoundList();

			for (int i = position; i > 0; i--) {
				Collections.swap(soundList, i, i - 1);
			}
		}

		@Override
		public void undo() {
			ArrayList<SoundInfo> soundList = ProjectManager.getInstance().getCurrentSprite().getSoundList();

			for (int i = 0; i < position; i++) {
				Collections.swap(soundList, i, i + 1);
			}
		}

		@Override
		public void update() {
		}
	}
}
