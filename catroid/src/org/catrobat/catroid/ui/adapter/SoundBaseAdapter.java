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
package org.catrobat.catroid.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class SoundBaseAdapter extends ArrayAdapter<SoundInfo> implements ScriptActivityAdapterInterface {

	protected ArrayList<SoundInfo> soundInfoItems;

	protected Context context;
	protected OnSoundEditListener onSoundEditListener;

	protected int selectMode;
	protected static long elapsedMilliSeconds;
	protected static long currentPlayingBase;
	protected boolean showDetails;

	protected SortedSet<Integer> checkedSounds = new TreeSet<Integer>();

	private int currentPlayingPosition = Constants.NO_POSITION;

	public SoundBaseAdapter(final Context context, int currentPlayingposition) {
		super(context, currentPlayingposition);
	}

	public SoundBaseAdapter(final Context context, int resource, int textViewResourceId, ArrayList<SoundInfo> items,
			boolean showDetails) {
		super(context, resource, textViewResourceId, items);
		this.context = context;
		this.showDetails = showDetails;
		this.soundInfoItems = items;
		this.selectMode = ListView.CHOICE_MODE_NONE;
	}

	public ArrayList<SoundInfo> getSoundInfoItems() {
		return soundInfoItems;
	}

	public void setOnSoundEditListener(OnSoundEditListener listener) {
		onSoundEditListener = listener;
	}

	@Override
	public int getAmountOfCheckedItems() {
		return checkedSounds.size();
	}

	@Override
	public SortedSet<Integer> getCheckedItems() {
		return checkedSounds;
	}

	public ArrayList<SoundInfo> getCheckedSoundInfos() {
		ArrayList<SoundInfo> result = new ArrayList<>();
		for (Integer pos : checkedSounds) {
			result.add(soundInfoItems.get(pos));
		}
		return result;
	}

	public void addCheckedItem(int position) {
		checkedSounds.add(position);
	}

	@Override
	public void clearCheckedItems() {
		checkedSounds.clear();
	}

	@Override
	public void setSelectMode(int mode) {
		selectMode = mode;
	}

	@Override
	public int getSelectMode() {
		return selectMode;
	}

	@Override
	public void setShowDetails(boolean showDetails) {
		this.showDetails = showDetails;
	}

	@Override
	public boolean getShowDetails() {
		return showDetails;
	}

	public void setSoundInfoItems(ArrayList<SoundInfo> soundInfoItems) {
		this.soundInfoItems = soundInfoItems;
	}

	@Override
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public static long getElapsedMilliSeconds() {
		return elapsedMilliSeconds;
	}

	public static void setElapsedMilliSeconds(long elapsedMilliSeconds) {
		SoundBaseAdapter.elapsedMilliSeconds = elapsedMilliSeconds;
	}

	public static long getCurrentPlayingBase() {
		return currentPlayingBase;
	}

	public static void setCurrentPlayingBase(long currentPlayingBase) {
		SoundBaseAdapter.currentPlayingBase = currentPlayingBase;
	}

	public SortedSet<Integer> getCheckedSounds() {
		return checkedSounds;
	}

	public void setCheckedSounds(SortedSet<Integer> checkedSounds) {
		this.checkedSounds = checkedSounds;
	}

	public int getCurrentPlayingPosition() {
		return currentPlayingPosition;
	}

	public void setCurrentPlayingPosition(int currentPlayingPosition) {
		this.currentPlayingPosition = currentPlayingPosition;
	}

	public OnSoundEditListener getOnSoundEditListener() {
		return onSoundEditListener;
	}

	public interface OnSoundEditListener {

		void onSoundPlay(View view);

		void onSoundPause(View view);

		void onSoundChecked();
	}
}
