/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.os.AsyncTask;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Backpack;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.BackpackSerializer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public final class BackpackListManager {

	private static final BackpackListManager INSTANCE = new BackpackListManager();

	private static Backpack backpack;

	public static BackpackListManager getInstance() {
		if (backpack == null) {
			backpack = new Backpack();
		}
		return INSTANCE;
	}

	public Backpack getBackpack() {
		if (backpack == null) {
			backpack = new Backpack();
		}
		return backpack;
	}

	public void removeItemFromScriptBackPack(String scriptGroup) {
		getBackpack().backpackedScripts.remove(scriptGroup);
	}

	public List<Scene> getBackpackedScenes() {
		return getBackpack().backpackedScenes;
	}

	public List<Sprite> getBackpackedSprites() {
		return getBackpack().backpackedSprites;
	}

	public ArrayList<String> getBackpackedScriptGroups() {
		return new ArrayList<>(getBackpack().backpackedScripts.keySet());
	}

	public HashMap<String, List<Script>> getBackpackedScripts() {
		return getBackpack().backpackedScripts;
	}

	public void addScriptToBackPack(String scriptGroup, List<Script> scripts) {
		getBackpack().backpackedScripts.put(scriptGroup, scripts);
	}

	public List<LookData> getBackpackedLooks() {
		return getBackpack().backpackedLooks;
	}

	public List<SoundInfo> getBackpackedSounds() {
		return getBackpack().backpackedSounds;
	}

	public boolean isBackpackEmpty() {
		return getBackpackedLooks().isEmpty() && getBackpackedScriptGroups().isEmpty()
				&& getBackpackedSounds().isEmpty() && getBackpackedSprites().isEmpty();
	}

	public void saveBackpack() {
		SaveBackpackTask saveTask = new SaveBackpackTask();
		saveTask.execute();
	}

	public void loadBackpack() {
		LoadBackpackTask loadTask = new LoadBackpackTask();
		loadTask.execute();
	}

	private class SaveBackpackTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			BackpackSerializer.getInstance().saveBackpack(getBackpack());
			return null;
		}
	}

	private class LoadBackpackTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			backpack = BackpackSerializer.getInstance().loadBackpack();
			setBackPackFlags();

			for (Sprite sprite : getBackpackedSprites()) {
				setLookFileReferences(sprite.getLookList());
				setSoundFileReferences(sprite.getSoundList());
			}
			setLookFileReferences(getBackpackedLooks());
			setSoundFileReferences(getBackpackedSounds());

			ProjectManager.getInstance().checkNestingBrickReferences(false, true);
			return null;
		}

		private void setBackPackFlags() {
			for (Scene scene : getBackpackedScenes()) {
				scene.isBackPackScene = true;
			}
		}

		private void setLookFileReferences(List<LookData> looks) {
			for (Iterator<LookData> iterator = looks.iterator(); iterator.hasNext(); ) {
				LookData lookData = iterator.next();
				File lookFile = new File(Constants.BACKPACK_DIRECTORY, lookData.getXstreamFileName());

				if (lookFile.exists()) {
					lookData.setFile(lookFile);
				} else {
					iterator.remove();
				}
			}
		}

		private void setSoundFileReferences(List<SoundInfo> sounds) {
			for (Iterator<SoundInfo> iterator = sounds.iterator(); iterator.hasNext(); ) {
				SoundInfo soundInfo = iterator.next();
				File soundFile = new File(Constants.BACKPACK_DIRECTORY, soundInfo.getXstreamFileName());

				if (soundFile.exists()) {
					soundInfo.setFile(soundFile);
				} else {
					iterator.remove();
				}
			}
		}
	}
}
