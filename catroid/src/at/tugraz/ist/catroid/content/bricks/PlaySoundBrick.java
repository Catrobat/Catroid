/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.stage.NativeAppActivity;

public class PlaySoundBrick implements Brick, OnItemSelectedListener {
	private static final long serialVersionUID = 1L;

	private SoundInfo soundInfo;
	private Sprite sprite;

	public PlaySoundBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public void execute() {
		if (soundInfo != null && sprite.getSoundList().contains(soundInfo)) {
			if (!NativeAppActivity.isRunning() && soundInfo.getAbsolutePath() != null) {
				SoundManager.getInstance().playSoundFile(soundInfo.getAbsolutePath());
			} else {
				SoundManager.getInstance().playSoundFile("sounds/" + soundInfo.getSoundFileName());
			}
		}
	}

	public Sprite getSprite() {
		return sprite;
	}

	public View getView(final Context context, int brickId, BaseAdapter adapter) {
		View view = View.inflate(context, R.layout.brick_play_sound, null);

		Spinner soundbrickSpinner = (Spinner) view.findViewById(R.id.playsound_spinner);
		soundbrickSpinner.setAdapter(createSoundAdapter(context));
		soundbrickSpinner.setClickable(true);
		soundbrickSpinner.setFocusable(true);
		soundbrickSpinner.setOnItemSelectedListener(this);

		if (sprite.getSoundList().contains(soundInfo)) {
			soundbrickSpinner.setSelection(sprite.getSoundList().indexOf(soundInfo) + 1, true);
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

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_play_sound, null);
	}

	@Override
	public Brick clone() {
		return new PlaySoundBrick(getSprite());
	}

	//for testing purposes:
	public void setSoundInfo(SoundInfo soundInfo) {
		this.soundInfo = soundInfo;
	}

	public void onItemSelected(AdapterView<?> parent, View arg1, int position, long arg3) {
		if (position == 0) {
			soundInfo = null;
		} else {
			soundInfo = (SoundInfo) parent.getItemAtPosition(position);
		}
	}

	public void onNothingSelected(AdapterView<?> arg0) {
	}
}
