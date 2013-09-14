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
package org.catrobat.catroid.ui.controller;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import org.catrobat.catroid.common.SoundInfo;

import java.util.ArrayList;

public class BackPackListManager extends SherlockFragmentActivity {

	private static BackPackListManager instance;
	private final static ArrayList<SoundInfo> soundInfoArrayList = new ArrayList<SoundInfo>();
	private final static ArrayList<SoundInfo> actionBarSoundInfoArrayList = new ArrayList<SoundInfo>();
	private static SoundInfo currentSoundInfo;

	private BackPackListManager() {
	}

	public static BackPackListManager getInstance() {

		if (instance == null) {
			instance = new BackPackListManager();
		}
		return instance;
	}

	public ArrayList<SoundInfo> getSoundInfoArrayList() {
		return soundInfoArrayList;
	}

	public void setSoundInfoArrayListEmpty() {
		soundInfoArrayList.clear();
	}

	public void addSoundToSoundInfoArrayList(SoundInfo soundInfo) {
		soundInfoArrayList.add(soundInfo);
	}

	// just for Debugging
	/*
	 * public void showSoundInfoArrayList() {
	 * Iterator<SoundInfo> iterator = soundInfoArrayList.iterator();
	 * 
	 * while (iterator.hasNext()) {
	 * SoundInfo soundInfo = iterator.next();
	 * }
	 * 
	 * }
	 */

	public static ArrayList<SoundInfo> getActionBarSoundInfoArrayList() {
		return actionBarSoundInfoArrayList;
	}

	public void addSoundToActionBarSoundInfoArrayList(SoundInfo soundInfo) {
		actionBarSoundInfoArrayList.add(soundInfo);
	}

	public static SoundInfo getCurrentSoundInfo() {
		return currentSoundInfo;
	}

	public static void setCurrentSoundInfo(SoundInfo currentSoundInfo) {
		BackPackListManager.currentSoundInfo = currentSoundInfo;
	}
}
