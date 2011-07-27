/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import java.io.Serializable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Spinner;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.SoundManager;

public class PlaySoundBrick implements Brick, Serializable {
	private static final long serialVersionUID = 1L;
	private SoundInfo soundInfo;
	private Sprite sprite;

	public PlaySoundBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public void execute() {
		if (soundInfo != null && soundInfo.getAbsolutePath() != null) {
			SoundManager.getInstance().playSoundFile(soundInfo.getAbsolutePath());
		}
	}

	public Sprite getSprite() {
		return sprite;
	}

	public String getPathToSoundFile() {
		//return getAbsoluteSoundPath();
		return null;
	}

	public View getView(final Context context, int brickId, BaseExpandableListAdapter adapter) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.construction_brick_play_sound, null);

		final Spinner soundbrickSpinner = (Spinner) view.findViewById(R.id.playsound_spinner);
		soundbrickSpinner.setAdapter(createSoundAdapter(context));

		soundbrickSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			//private boolean start = true;
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				soundInfo = (SoundInfo) parent.getItemAtPosition(position);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		if (sprite.getSoundList().contains(soundInfo)) {
			soundbrickSpinner.setSelection(sprite.getSoundList().indexOf(soundInfo) + 1);
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
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toolbox_brick_play_sound, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new PlaySoundBrick(getSprite());
	}

	public void setPathToSoundfile(String pathToSoundfile) {
		//this.soundfileName = pathToSoundfile;
	}

	public void setTitle(String title) {
		//this.title = title;
	}
}
