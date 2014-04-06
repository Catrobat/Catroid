/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.commands;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.SparseArray;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.SoundBaseAdapter;
import org.catrobat.catroid.ui.controller.SoundController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;

public class DeleteCheckedSounds implements Command {

	private Activity activity;
	private SoundBaseAdapter adapter;
	private ArrayList<SoundInfo> soundInfoList;
	private MediaPlayer mediaPlayer;

	private SparseArray<SoundInfo> checkedSoundInfoList;

	public DeleteCheckedSounds(Activity activity, SoundBaseAdapter adapter, ArrayList<SoundInfo> soundInfoList,
			MediaPlayer mediaPlayer) {
		this.activity = activity;
		this.adapter = adapter;
		this.soundInfoList = soundInfoList;
		this.mediaPlayer = mediaPlayer;
	}

	@Override
	public void execute() {
		if (checkedSoundInfoList == null) {
			// first execution of the command
			SortedSet<Integer> checkedSounds = adapter.getCheckedItems();
			Iterator<Integer> iterator = checkedSounds.iterator();
			SoundController.getInstance().stopSoundAndUpdateList(mediaPlayer, soundInfoList, adapter);
			int numberDeleted = 0;
			while (iterator.hasNext()) {
				int position = iterator.next() - numberDeleted;
				if (checkedSoundInfoList == null) {
					checkedSoundInfoList = new SparseArray<SoundInfo>();
				}
				checkedSoundInfoList.put(position, soundInfoList.get(position));
				deleteSound(position, soundInfoList, activity);
				++numberDeleted;
			}

		} else {
			// redo command
			for (int i = 0; i < checkedSoundInfoList.size(); i++) {
				int position = checkedSoundInfoList.keyAt(i);
				deleteSound(position, soundInfoList, activity);
			}

		}

	}

	@Override
	public void undo() {
		for (int i = 0; i < checkedSoundInfoList.size(); i++) {
			int position = checkedSoundInfoList.keyAt(i);
			addSound(position, checkedSoundInfoList.get(position));
		}
	}

	private void deleteSound(int position, ArrayList<SoundInfo> soundInfoList, Activity activity) {
		soundInfoList.remove(position);
		ProjectManager.getInstance().getCurrentSprite().setSoundList(soundInfoList);
		activity.sendBroadcast(new Intent(ScriptActivity.ACTION_SOUND_DELETED));
	}

	private void addSound(int position, SoundInfo soundInfo) {
		soundInfoList.add(position, soundInfo);
		ProjectManager.getInstance().getCurrentSprite().setSoundList(soundInfoList);
		activity.sendBroadcast(new Intent(ScriptActivity.ACTION_SOUND_DELETED));
	}

}
