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
package org.catrobat.catroid.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.view.ActionMode;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.ui.controller.SoundController;
import org.catrobat.catroid.ui.fragment.BackPackSoundFragment;
import org.catrobat.catroid.ui.fragment.SoundFragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class SoundAdapter extends SoundBaseAdapter implements ScriptActivityAdapterInterface {

	protected ArrayList<SoundInfo> soundInfoItems;

	public ArrayList<SoundInfo> getSoundInfoItems() {
		return soundInfoItems;
	}

	private SoundFragment soundFragment;

	public SoundAdapter(final Context context, int textViewResourceId, ArrayList<SoundInfo> items, boolean showDetails) {
		super(context, textViewResourceId, items, false);
		Log.d("TAG", "SoundAdapter called!");
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (soundFragment == null) {
            return convertView;
        }
        return soundFragment.getView(position, convertView);
    }

	public void onDestroyActionModeRename(ActionMode mode, ListView listView) {
		Iterator<Integer> iterator = checkedSounds.iterator();

		if (iterator.hasNext()) {
			int position = iterator.next();
			soundFragment.setSelectedSoundInfo((SoundInfo) listView.getItemAtPosition(position));
			soundFragment.showRenameDialog();
		}
		soundFragment.clearCheckedSoundsAndEnableButtons();

	}

	public void onDestroyActionModeCopy(ActionMode mode) {
		Iterator<Integer> iterator = checkedSounds.iterator();

		while (iterator.hasNext()) {
			int position = iterator.next();
			SoundController.getInstance().copySound(position, soundFragment.getSoundInfoList(), this);
		}

		soundFragment.clearCheckedSoundsAndEnableButtons();

	}

	public void setSoundFragment(SoundFragment soundFragment) {
		this.soundFragment = soundFragment;
	}
}
