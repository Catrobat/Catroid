/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.ui.adapter.LookBaseAdapter;
import org.catrobat.catroid.ui.adapter.SoundBaseAdapter;
import org.catrobat.catroid.ui.fragment.LookFragment;

import java.util.ArrayList;

public final class BackPackListManager {
	private static final BackPackListManager INSTANCE = new BackPackListManager();
	private static final ArrayList<SoundInfo> SOUND_INFO_ARRAY_LIST = new ArrayList<SoundInfo>();
	private static final ArrayList<SoundInfo> ACTION_BAR_SOUND_INFO_ARRAY_LIST = new ArrayList<SoundInfo>();

	private static SoundInfo currentSoundInfo;
	private static ArrayList<SoundInfo> currentSoundInfoArrayList;
	private static ArrayList<LookData> currentLookDataArrayList;

	private static SoundBaseAdapter currentAdapter;
	private static LookBaseAdapter currentLookAdapter;

	private static final ArrayList<LookData> LOOK_DATA_ARRAY_LIST = new ArrayList<LookData>();
	private static final ArrayList<LookData> ACTION_BAR_LOOK_DATA_ARRAY_LIST = new ArrayList<LookData>();
	private static LookData currentLookData;

	private static boolean backpackFlag;
	private LookFragment currentLookFragment;

	private BackPackListManager() {
	}

	public static BackPackListManager getInstance() {
		return INSTANCE;
	}

	public ArrayList<LookData> getLookDataArrayList() {
		return LOOK_DATA_ARRAY_LIST;
	}

	public void setLookDataArrayListEmpty() {
		LOOK_DATA_ARRAY_LIST.clear();
	}

	public void addSLookToLookDataArrayList(LookData lookData) {
		LOOK_DATA_ARRAY_LIST.add(lookData);
	}

	public static void setCurrentLookData(LookData lookData) {
		BackPackListManager.currentLookData = lookData;
	}

	public static LookData getCurrentLookData() {
		return BackPackListManager.currentLookData;
	}

	public void addLookToActionBarLookDataArrayList(LookData lookData) {
		ACTION_BAR_LOOK_DATA_ARRAY_LIST.add(lookData);
	}

	public static ArrayList<LookData> getActionBarLookDataArrayList() {
		return ACTION_BAR_LOOK_DATA_ARRAY_LIST;
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

	public static ArrayList<LookData> getCurrentLookDataArrayList() {
		return currentLookDataArrayList;
	}

	public void setCurrentLookDataArrayList(ArrayList<LookData> currentLookDataArrayList) {
		BackPackListManager.currentLookDataArrayList = currentLookDataArrayList;
	}

	public static SoundBaseAdapter getCurrentAdapter() {
		return currentAdapter;
	}

	public static LookBaseAdapter getCurrentLookAdapter() {
		return currentLookAdapter;
	}

	public void setCurrentLookAdapter(LookBaseAdapter currentLookAdapter) {
		BackPackListManager.currentLookAdapter = currentLookAdapter;
	}

	public static void setBackPackFlag(boolean currentBackpackFlag) {
		backpackFlag = currentBackpackFlag;
	}

	public static boolean isBackpackFlag() {
		return backpackFlag;
	}

	public void setCurrentLookFragment(LookFragment lookFragment) {
		this.currentLookFragment = lookFragment;
	}

	public LookFragment getCurrentLookFragment() {
		return currentLookFragment;
	}
}
