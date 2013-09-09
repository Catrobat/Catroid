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

import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.ui.fragment.BackPackSoundFragment;

import java.util.ArrayList;
import java.util.Iterator;

public class BackPackListManager extends SherlockFragmentActivity {

	private static BackPackListManager instance;

	private static BackPackSoundFragment backPackSoundFragment;

	private final static ArrayList<SoundInfo> soundInfoArrayList = new ArrayList<SoundInfo>();

	private static SoundInfo currentSoundInfo;

	private BackPackListManager() {

		Log.d("TAG", "Set up BackPackListManager (Constructor)");
		// change
		if (backPackSoundFragment == null) {
			backPackSoundFragment = new BackPackSoundFragment();
		}

	}

	public static BackPackListManager getInstance() {

		Log.d("TAG", "BackPackListManager-->getInstance()");

		if (instance == null) {
			instance = new BackPackListManager();
		}
		return instance;
	}

	public BackPackSoundFragment getBackPackSoundFragment() {
		return backPackSoundFragment;
	}

	public ArrayList<SoundInfo> getSoundInfoArrayList() {
		return soundInfoArrayList;
	}

	public void setSoundInfoArrayListEmpty() {

		Log.d("TAG", "Set ArrayList empty, Size of ArrayList before setting empty: " + soundInfoArrayList.size());

		soundInfoArrayList.clear();

		Log.d("TAG", "Set ArrayList empty, Size of ArrayList after setting empty: " + soundInfoArrayList.size());
	}

	//	public void setSoundInfoArrayList(ArrayList<SoundInfo> soundInfoArrayList) {
	//		soundInfoArrayList = soundInfoArrayList;
	//	}

	public void addSoundToSoundInfoArrayList(SoundInfo soundInfo) {
		soundInfoArrayList.add(soundInfo);
	}

	public void showSoundInfoArrayList() {
		Iterator<SoundInfo> iterator = soundInfoArrayList.iterator();

		while (iterator.hasNext()) {
			SoundInfo soundInfo = iterator.next();

			Log.d("TAG", "Content of soundInfoArrayList: " + soundInfo.getTitle());
		}

	}

	public static SoundInfo getCurrentSoundInfo() {
		return currentSoundInfo;
	}

	public static void setCurrentSoundInfo(SoundInfo currentSoundInfo) {
		BackPackListManager.currentSoundInfo = currentSoundInfo;
	}
}
