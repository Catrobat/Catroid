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
package org.catrobat.catroid.ui.controller;

import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.ui.adapter.SoundBaseAdapter;

import java.util.ArrayList;

public final class BackPackListManager {
	private static final BackPackListManager INSTANCE = new BackPackListManager();
	private static final ArrayList<SoundInfo> SOUND_INFO_ARRAY_LIST = new ArrayList<SoundInfo>();
	private static final ArrayList<SoundInfo> ACTION_BAR_SOUND_INFO_ARRAY_LIST = new ArrayList<SoundInfo>();

	private static SoundInfo currentSoundInfo;
	private static ArrayList<SoundInfo> currentSoundInfoArrayList;
	private static SoundBaseAdapter currentAdapter;
	private static boolean backpackFlag;

	private BackPackListManager() {
	}

	public static BackPackListManager getInstance() {
		return INSTANCE;
	}

	public ArrayList<SoundInfo> getSoundInfoArrayList() {
		return SOUND_INFO_ARRAY_LIST;
	}

	public void setSoundInfoArrayListEmpty() {
		SOUND_INFO_ARRAY_LIST.clear();
	}

	public void addSoundToSoundInfoArrayList(SoundInfo soundInfo) {
		SOUND_INFO_ARRAY_LIST.add(soundInfo);
	}

	public static ArrayList<SoundInfo> getActionBarSoundInfoArrayList() {
		return ACTION_BAR_SOUND_INFO_ARRAY_LIST;
	}

	public void addSoundToActionBarSoundInfoArrayList(SoundInfo soundInfo) {
		ACTION_BAR_SOUND_INFO_ARRAY_LIST.add(soundInfo);
	}

	public static SoundInfo getCurrentSoundInfo() {
		return currentSoundInfo;
	}

	public static void setCurrentSoundInfo(SoundInfo currentSoundInfo) {
		BackPackListManager.currentSoundInfo = currentSoundInfo;
	}

	public void setCurrentSoundInfoList(ArrayList<SoundInfo> soundInfoList) {
		currentSoundInfoArrayList = soundInfoList;
	}

	public void setCurrentSoundAdapter(SoundBaseAdapter adapter) {
		currentAdapter = adapter;
	}

	public static ArrayList<SoundInfo> getCurrentSoundInfoArrayList() {
		return currentSoundInfoArrayList;
	}

	public static SoundBaseAdapter getCurrentAdapter() {
		return currentAdapter;
	}

	public static void setBackPackFlag(boolean currentBackpackFlag) {
		backpackFlag = currentBackpackFlag;
	}

	public static boolean isBackpackFlag() {
		return backpackFlag;
	}
}
