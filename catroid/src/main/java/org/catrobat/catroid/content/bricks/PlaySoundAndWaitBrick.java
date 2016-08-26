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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.controller.SoundController;
import org.catrobat.catroid.ui.fragment.SoundFragment;
import org.catrobat.catroid.ui.fragment.SoundFragment.OnSoundInfoListChangedAfterNewListener;

import java.util.List;

public class PlaySoundAndWaitBrick extends BrickBaseType implements OnItemSelectedListener,
		OnSoundInfoListChangedAfterNewListener {

	private static final long serialVersionUID = 1L;

	private SoundInfo sound;
	private transient SoundInfo oldSelectedSound;

	public PlaySoundAndWaitBrick() {
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		PlaySoundAndWaitBrick copyBrick = (PlaySoundAndWaitBrick) clone();

		if (sound != null && sound.isBackpackSoundInfo) {
			copyBrick.sound = sound;
			return copyBrick;
		}

		for (SoundInfo soundInfo : sprite.getSoundList()) {
			if (sound != null && soundInfo != null && soundInfo.getAbsolutePath().equals(sound.getAbsolutePath())) {
				copyBrick.sound = soundInfo;
				break;
			}
		}

		return copyBrick;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_play_sound_and_wait, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_play_sound_and_wait_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		final Spinner playSoundAndWaitSpinner = (Spinner) view.findViewById(R.id.playsound_spinner);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			playSoundAndWaitSpinner.setClickable(true);
			playSoundAndWaitSpinner.setEnabled(true);
		} else {
			playSoundAndWaitSpinner.setClickable(false);
			playSoundAndWaitSpinner.setEnabled(false);
		}

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			playSoundAndWaitSpinner.setOnItemSelectedListener(this);
		}

		final ArrayAdapter<SoundInfo> spinnerAdapter = createSoundAdapter(context);

		SpinnerAdapterWrapper spinnerAdapterWrapper = new SpinnerAdapterWrapper(context, spinnerAdapter);

		playSoundAndWaitSpinner.setAdapter(spinnerAdapterWrapper);

		setSpinnerSelection(playSoundAndWaitSpinner);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_play_sound_and_wait, null);

		Spinner playSoundAndWaitSpinner = (Spinner) prototypeView.findViewById(R.id.playsound_spinner);
		playSoundAndWaitSpinner.setFocusableInTouchMode(false);
		playSoundAndWaitSpinner.setFocusable(false);
		playSoundAndWaitSpinner.setEnabled(false);

		SpinnerAdapter playSoundSpinnerAdapter = createSoundAdapter(context);
		playSoundAndWaitSpinner.setAdapter(playSoundSpinnerAdapter);
		setSpinnerSelection(playSoundAndWaitSpinner);

		return prototypeView;
	}

	private void setSpinnerSelection(Spinner spinner) {
		List<SoundInfo> soundsList = ProjectManager.getInstance().getCurrentSprite().getSoundList();

		if (soundsList.contains(sound)) {
			oldSelectedSound = sound;
			spinner.setSelection(soundsList.indexOf(sound) + 1, true);
		} else {
			if (spinner.getAdapter() != null && spinner.getAdapter().getCount() > 1) {
				if (soundsList.indexOf(oldSelectedSound) >= 0) {
					spinner.setSelection(soundsList
							.indexOf(oldSelectedSound) + 1, true);
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
		arrayAdapter.addAll(ProjectManager.getInstance().getCurrentSprite().getSoundList());

		return arrayAdapter;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View arg1, int position, long arg3) {
		if (position == 0) {
			sound = null;
		} else {
			sound = (SoundInfo) parent.getItemAtPosition(position);
			oldSelectedSound = sound;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createPlaySoundAction(sprite, sound));
		sequence.addAction(sprite.getActionFactory().createWaitAction(sprite,
				new Formula(getDurationOfSoundFile(sprite, sound))));

		return null;
	}

	private float getDurationOfSoundFile(Sprite sprite, SoundInfo sound) {
		float duration = 0;

		if (sound != null && sprite.getSoundList().contains(sound) && sound.getAbsolutePath() != null) {
			duration = (SoundManager.getInstance().getDurationOfSoundFile(sound.getAbsolutePath())) / 1000;
		}

		return duration;
	}

	// for testing purposes:
	public void setSoundInfo(SoundInfo soundInfo) {
		this.sound = soundInfo;
	}

	public SoundInfo getSound() {
		return sound;
	}

	private void setOnSoundInfoListChangedAfterNewListener(Context context) {
		ScriptActivity scriptActivity = (ScriptActivity) context;
		SoundFragment soundFragment = (SoundFragment) scriptActivity.getFragment(ScriptActivity.FRAGMENT_SOUNDS);
		if (soundFragment != null) {
			soundFragment.setOnSoundInfoListChangedAfterNewListener(this);
		}
	}

	@Override
	public Brick clone() {
		return new PlaySoundAndWaitBrick();
	}

	private class SpinnerAdapterWrapper implements SpinnerAdapter {

		protected Context context;
		protected ArrayAdapter<SoundInfo> spinnerAdapter;

		private boolean isTouchInDropDownView;

		public SpinnerAdapterWrapper(Context context, ArrayAdapter<SoundInfo> spinnerAdapter) {
			this.context = context;
			this.spinnerAdapter = spinnerAdapter;

			this.isTouchInDropDownView = false;
		}

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
				oldSelectedSound = currentSound;
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

			dropDownView.setOnTouchListener(new OnTouchListener() {
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

			setOnSoundInfoListChangedAfterNewListener(context);
		}
	}

	@Override
	public void onSoundInfoListChangedAfterNew(SoundInfo soundInfo) {
		sound = soundInfo;
		oldSelectedSound = soundInfo;
	}

	@Override
	public void storeDataForBackPack(Sprite sprite) {
		SoundInfo backPackedSoundInfo = SoundController.getInstance().backPackHiddenSound(sound);
		setSoundInfo(backPackedSoundInfo);
		if (sprite != null && !sprite.getSoundList().contains(backPackedSoundInfo)) {
			sprite.getSoundList().add(backPackedSoundInfo);
		}
	}
}
