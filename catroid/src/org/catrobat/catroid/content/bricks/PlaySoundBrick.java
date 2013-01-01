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

import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.stage.NativeAppActivity;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import org.catrobat.catroid.R;

public class PlaySoundBrick implements Brick, OnItemSelectedListener {
	private static final long serialVersionUID = 1L;

	private SoundInfo sound;
	private Sprite sprite;

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
	public void execute() {
		if (sound != null && sprite.getSoundList().contains(sound)) {
			if (!NativeAppActivity.isRunning() && sound.getAbsolutePath() != null) {
				SoundManager.getInstance().playSoundFile(sound.getAbsolutePath());
			} else {
				SoundManager.getInstance().playSoundFile("sounds/" + sound.getSoundFileName());
			}
		}
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter adapter) {
		View view = View.inflate(context, R.layout.brick_play_sound, null);

		Spinner soundbrickSpinner = (Spinner) view.findViewById(R.id.playsound_spinner);
		soundbrickSpinner.setAdapter(createSoundAdapter(context));
		soundbrickSpinner.setClickable(true);
		soundbrickSpinner.setFocusable(true);
		soundbrickSpinner.setOnItemSelectedListener(this);

		if (sprite.getSoundList().contains(sound)) {
			soundbrickSpinner.setSelection(sprite.getSoundList().indexOf(sound) + 1, true);
		} else {
			soundbrickSpinner.setSelection(0);
		}

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
}
