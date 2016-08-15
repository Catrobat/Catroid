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
package org.catrobat.catroid.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.SoundInfoHistory;
import org.catrobat.catroid.content.commands.SoundCommands;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.controller.SoundController;
import org.catrobat.catroid.ui.fragment.BackPackSoundFragment;

import java.util.ArrayList;
import java.util.List;

public class BackPackSoundAdapter extends SoundBaseAdapter implements ActionModeActivityAdapterInterface {

	private BackPackSoundFragment backPackSoundFragment;

	public BackPackSoundAdapter(Context context, int resource, int textViewResourceId, List<SoundInfo> items,
			boolean showDetails, BackPackSoundFragment backPackSoundFragment) {
		super(context, resource, textViewResourceId, items, showDetails, true);
		this.backPackSoundFragment = backPackSoundFragment;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (this.backPackSoundFragment == null) {
			return convertView;
		}
		return this.backPackSoundFragment.getView(position, convertView);
	}

	public void onDestroyActionModeUnpacking() {
		ArrayList<SoundInfo> toAdd = new ArrayList<>();
		List<SoundInfo> soundsToUnpack = new ArrayList<>();
		for (Integer checkedPosition : checkedSounds) {
			soundsToUnpack.add(getItem(checkedPosition));
		}
		for (SoundInfo soundInfo : soundsToUnpack) {
			toAdd.add(SoundController.getInstance().unpack(soundInfo, backPackSoundFragment.isDeleteUnpackedItems(),
					false));
		}

		if (!toAdd.isEmpty()) {
			SoundCommands.AddSoundCommand command = new SoundCommands.AddSoundCommand(toAdd, null, null);
			command.execute();
			SoundInfoHistory.getInstance(ProjectManager.getInstance().getCurrentSprite()).add(command);
		}

		boolean returnToScriptActivity = checkedSounds.size() > 0;
		backPackSoundFragment.clearCheckedSoundsAndEnableButtons();

		if (returnToScriptActivity) {
			((BackPackActivity) backPackSoundFragment.getActivity()).returnToScriptActivity(ScriptActivity.FRAGMENT_SOUNDS);
		}
	}
}
