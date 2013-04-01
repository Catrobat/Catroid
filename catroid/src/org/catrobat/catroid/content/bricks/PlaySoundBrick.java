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
package org.catrobat.catroid.content.bricks;

import java.util.List;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class PlaySoundBrick extends BrickBaseType implements OnItemSelectedListener {
	private static final long serialVersionUID = 1L;

	private SoundInfo sound;

	public PlaySoundBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public PlaySoundBrick() {

	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_play_sound, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_play_sound_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		Spinner soundbrickSpinner = (Spinner) view.findViewById(R.id.playsound_spinner);
		soundbrickSpinner.setAdapter(createSoundAdapter(context));
		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			soundbrickSpinner.setClickable(true);
			soundbrickSpinner.setEnabled(true);
		} else {
			soundbrickSpinner.setClickable(false);
			soundbrickSpinner.setEnabled(false);
		}

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			soundbrickSpinner.setOnItemSelectedListener(this);
		}

		if (sprite.getSoundList().contains(sound)) {
			soundbrickSpinner.setSelection(sprite.getSoundList().indexOf(sound) + 1, true);
		} else {
			soundbrickSpinner.setSelection(0);
		}

		return view;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_play_sound_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);
		this.alphaValue = (alphaValue);
		return view;
	}

	private ArrayAdapter<?> createSoundAdapter(Context context) {
		ArrayAdapter<SoundInfo> arrayAdapter = new ArrayAdapter<SoundInfo>(context,
				android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		SoundInfo dummySoundInfo = new SoundInfo();
		dummySoundInfo.setTitle(context.getString(R.string.broadcast_nothing_selected));
		arrayAdapter.add(dummySoundInfo);
		for (SoundInfo soundInfo : sprite.getSoundList()) {
			arrayAdapter.add(soundInfo);
		}
		return arrayAdapter;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_play_sound, null);
	}

	@Override
	public Brick clone() {
		return new PlaySoundBrick(getSprite());
	}

	//for testing purposes:
	public void setSoundInfo(SoundInfo soundInfo) {
		this.sound = soundInfo;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View arg1, int position, long arg3) {
		if (position == 0) {
			sound = null;
		} else {
			sound = (SoundInfo) parent.getItemAtPosition(position);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.playSound(sprite, sound));
		return null;
	}
}
