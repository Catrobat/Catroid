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

package org.catrobat.catroid.ui.bricks;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.fragment.SoundFragment;

/**
 * Brick View Factory for PlaySoundBrick.
 * Created by Illya Boyko on 11/03/15.
 */
public class PlaySoundBrickViewProvider extends BrickViewProvider {
	public PlaySoundBrickViewProvider(Context context, LayoutInflater inflater) {
		super(context, inflater);
	}

	public View createPlaySoundBrickView(final PlaySoundBrick brick, ViewGroup parent) {
		View view = inflateBrickView(parent, R.layout.brick_play_sound);

		final Spinner soundbrickSpinner = (Spinner) view.findViewById(R.id.playsound_spinner);

		soundbrickSpinner.setFocusableInTouchMode(false);
		soundbrickSpinner.setFocusable(false);
		soundbrickSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					brick.setSoundInfo(null);
				} else {
					SoundInfo currentSoundInfo = (SoundInfo) parent.getItemAtPosition(position);
					brick.setSoundInfo(currentSoundInfo);
					brick.setOldSoundInfo(currentSoundInfo);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		soundbrickSpinner.setAdapter(new SpinnerAdapter() {
			protected ArrayAdapter<SoundInfo> spinnerAdapter = createSoundAdapter(context);

			private boolean isTouchInDropDownView = false;

			@Override
			public void registerDataSetObserver(DataSetObserver paramDataSetObserver) {
				spinnerAdapter.registerDataSetObserver(paramDataSetObserver);
			}

			@Override
			public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver) {
				spinnerAdapter.unregisterDataSetObserver(paramDataSetObserver);
			}

			@Override
			public int getCount() {
				return spinnerAdapter.getCount();
			}

			@Override
			public Object getItem(int paramInt) {
				return spinnerAdapter.getItem(paramInt);
			}

			@Override
			public long getItemId(int paramInt) {
				SoundInfo currentSound = spinnerAdapter.getItem(paramInt);
				if (!currentSound.getTitle().equals(context.getString(R.string.new_broadcast_message))) {
					brick.setOldSoundInfo(currentSound);
				}
				return spinnerAdapter.getItemId(paramInt);
			}

			@Override
			public boolean hasStableIds() {
				return spinnerAdapter.hasStableIds();
			}

			@Override
			public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
				if (isTouchInDropDownView) {
					isTouchInDropDownView = false;
					if (paramInt == 0) {
						switchToSoundFragmentFromScriptFragment();
					}
				}
				return spinnerAdapter.getView(paramInt, paramView, paramViewGroup);
			}

			@Override
			public int getItemViewType(int paramInt) {
				return spinnerAdapter.getItemViewType(paramInt);
			}

			@Override
			public int getViewTypeCount() {
				return spinnerAdapter.getViewTypeCount();
			}

			@Override
			public boolean isEmpty() {
				return spinnerAdapter.isEmpty();
			}

			@Override
			public View getDropDownView(int paramInt, View paramView, ViewGroup paramViewGroup) {
				View dropDownView = spinnerAdapter.getDropDownView(paramInt, paramView, paramViewGroup);

				dropDownView.setOnTouchListener(new View.OnTouchListener() {
					@Override
					public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
						isTouchInDropDownView = true;
						return false;
					}
				});

				return dropDownView;
			}

			private void switchToSoundFragmentFromScriptFragment() {
				ScriptActivity scriptActivity = ((ScriptActivity) context);
				scriptActivity.switchToFragmentFromScriptFragment(ScriptActivity.FRAGMENT_SOUNDS);

				setOnSoundInfoListChangedAfterNewListener(context, brick);
			}
		});

		setSpinnerSelection(brick, soundbrickSpinner);

		return view;
	}

	private void setSpinnerSelection(PlaySoundBrick brick, Spinner spinner) {
		if (ProjectManager.getInstance().getCurrentSprite().getSoundList().contains(brick.getSoundInfo())) {
			brick.setOldSoundInfo(brick.getSoundInfo());
			spinner.setSelection(ProjectManager.getInstance().getCurrentSprite().getSoundList().indexOf(brick.getSoundInfo()) + 1, true);
		} else {
			if (spinner.getAdapter() != null && spinner.getAdapter().getCount() > 1) {
				if (ProjectManager.getInstance().getCurrentSprite().getSoundList().indexOf(brick.getOldSoundInfo()) >= 0) {
					spinner.setSelection(ProjectManager.getInstance().getCurrentSprite().getSoundList()
							.indexOf(brick.getOldSoundInfo()) + 1, true);
				} else {
					spinner.setSelection(1, true);
				}
			} else {
				spinner.setSelection(0, true);
			}
		}
	}


	private ArrayAdapter<SoundInfo> createSoundAdapter(Context context) {
		ArrayAdapter<SoundInfo> arrayAdapter = new ArrayAdapter<SoundInfo>(context,
				android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		SoundInfo dummySoundInfo = new SoundInfo();
		dummySoundInfo.setTitle(context.getString(R.string.new_broadcast_message));
		arrayAdapter.add(dummySoundInfo);
		for (SoundInfo soundInfo : ProjectManager.getInstance().getCurrentSprite().getSoundList()) {
			arrayAdapter.add(soundInfo);
		}
		return arrayAdapter;
	}

	private void setOnSoundInfoListChangedAfterNewListener(Context context, final PlaySoundBrick brick) {
		ScriptActivity scriptActivity = (ScriptActivity) context;
		SoundFragment soundFragment = (SoundFragment) scriptActivity.getFragment(ScriptActivity.FRAGMENT_SOUNDS);
		if (soundFragment != null) {
			soundFragment.setOnSoundInfoListChangedAfterNewListener(new SoundFragment.OnSoundInfoListChangedAfterNewListener() {

				@Override
				public void onSoundInfoListChangedAfterNew(SoundInfo soundInfo) {
					brick.setSoundInfo(soundInfo);
					brick.setOldSoundInfo(soundInfo);
				}
			});
		}
	}

}
