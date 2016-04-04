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
import java.util.List;
import java.util.Map;

public class SoundCommands {
	public static class RenameSoundCommand implements Command {
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
	}

	public static class AddSoundCommand implements Command {
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

		public void updateMediaPlayer(MediaPlayer player) {
			this.player = player;
		}
	}

	public static class DeleteSoundCommand implements Command {
		SoundBaseAdapter adapter;
		MediaPlayer player;
		private ArrayList<Integer> positions = new ArrayList<>();
		Map<SoundInfo, PlaySoundBrick> bricks = new HashMap<>();
		ArrayList<SoundInfo> soundsToDelete = new ArrayList<>();

		public DeleteSoundCommand(SoundBaseAdapter adapter, MediaPlayer mediaPlayer, ArrayList<SoundInfo> soundsToDelete) {
			this.adapter = adapter;
			this.player = mediaPlayer;
			this.soundsToDelete = soundsToDelete;
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

		public void updatePlayerAndAdapter(MediaPlayer player, SoundBaseAdapter adapter) {
			this.player = player;
			this.adapter = adapter;
		}
	}

	public static class MoveSoundCommand implements Command {
		private int previousPosition;
		private int newPosition;

		public MoveSoundCommand(int previousPosition, int newPosition) {
			this.previousPosition = previousPosition;
			this.newPosition = newPosition;
		}

		@Override
		public void execute() {
			List<SoundInfo> lookList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
			int direction = newPosition > previousPosition ? 1 : -1;
			int currentPos = previousPosition;
			do {
				Collections.swap(lookList, currentPos, currentPos + direction);
				currentPos += direction;
			} while (currentPos != newPosition);
		}

		@Override
		public void undo() {
			List<SoundInfo> lookList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
			int direction = previousPosition > newPosition ? 1 : -1;
			int currentPos = newPosition;
			do {
				Collections.swap(lookList, currentPos, currentPos + direction);
				currentPos += direction;
			} while (currentPos != previousPosition);
		}
	}
}
