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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.ui.controller.SoundController;
import org.catrobat.catroid.ui.fragment.BackPackSoundFragment;
import org.catrobat.catroid.ui.fragment.SoundFragment;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.view.ActionMode;

public class SoundAdapter extends ArrayAdapter<SoundInfo> implements ScriptActivityAdapterInterface {

	protected ArrayList<SoundInfo> soundInfoItems;

	public ArrayList<SoundInfo> getSoundInfoItems() {
		return soundInfoItems;
	}

	protected Context context;
	private SoundFragment soundFragment;
	private BackPackSoundFragment backPackSoundActivity;

	private OnSoundEditListener onSoundEditListener;

	private int selectMode;
	private static long elapsedMilliSeconds;
	private static long currentPlayingBase;
	private boolean showDetails;

	private SortedSet<Integer> checkedSounds = new TreeSet<Integer>();

	private int currentPlayingPosition = Constants.NO_POSITION;

	public SoundAdapter(final Context context, int textViewResourceId, ArrayList<SoundInfo> items, boolean showDetails) {
		super(context, textViewResourceId, items);

		Log.d("TAG", "SoundAdapter called!");

		this.context = context;
		this.showDetails = showDetails;
		this.soundInfoItems = items;
		this.selectMode = ListView.CHOICE_MODE_NONE;
	}

	public void setOnSoundEditListener(OnSoundEditListener listener) {
		onSoundEditListener = listener;
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

	@Override
	public int getAmountOfCheckedItems() {
		return checkedSounds.size();
	}

	@Override
	public SortedSet<Integer> getCheckedItems() {
		return checkedSounds;
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
		SoundAdapter.elapsedMilliSeconds = elapsedMilliSeconds;
	}

	public static long getCurrentPlayingBase() {
		return currentPlayingBase;
	}

	public static void setCurrentPlayingBase(long currentPlayingBase) {
		SoundAdapter.currentPlayingBase = currentPlayingBase;
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

	public BackPackSoundFragment getBackPackSoundActivity() {
		return this.backPackSoundActivity;
	}

	public void setBackPackSoundActivity(BackPackSoundFragment backPackSoundActivity) {
		this.backPackSoundActivity = backPackSoundActivity;
	}

	public interface OnSoundEditListener {

		public void onSoundPlay(View view);

		public void onSoundPause(View view);

		public void onSoundChecked();
	}

}
