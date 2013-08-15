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

import java.util.ArrayList;
import java.util.Iterator;

import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.ui.BackPackSoundActivity;

import android.util.Log;

public class BackPackListManager {

	private static BackPackListManager instance;

	private final static BackPackSoundActivity backPackSoundActivityFragment = new BackPackSoundActivity();

	private ArrayList<SoundInfo> soundInfoArrayList;

	private BackPackListManager() {

		Log.d("TAG", "Set up BackPackListManager");

		this.soundInfoArrayList = new ArrayList<SoundInfo>();
	}

	public static BackPackListManager getInstance() {
		if (instance == null) {
			instance = new BackPackListManager();
		}
		return instance;
	}

	public BackPackSoundActivity getBackPackSoundActivityFragment() {
		return backPackSoundActivityFragment;
	}

	public ArrayList<SoundInfo> getSoundInfoArrayList() {
		return soundInfoArrayList;
	}

	public void setSoundInfoArrayList(ArrayList<SoundInfo> soundInfoArrayList) {
		this.soundInfoArrayList = soundInfoArrayList;
	}

	public void addSoundToSoundInfoArrayList(SoundInfo soundInfo) {
		this.soundInfoArrayList.add(soundInfo);
	}

	public void showSoundInfoArrayList() {
		Iterator<SoundInfo> iterator = soundInfoArrayList.iterator();

		while (iterator.hasNext()) {
			SoundInfo soundInfo = iterator.next();

			Log.d("TAG", "Content of soundInfoArrayList: " + soundInfo.getTitle());
		}

	}
}
