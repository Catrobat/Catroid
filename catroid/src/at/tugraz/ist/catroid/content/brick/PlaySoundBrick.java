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
package at.tugraz.ist.catroid.content.brick;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.io.sound.SoundManager;

public class PlaySoundBrick implements Brick {
	private String pathToSoundfile;
	private static final long serialVersionUID = 1L;

	public PlaySoundBrick(String pathToSoundfile) {
		this.pathToSoundfile = pathToSoundfile;
	}

	public void execute() {
		MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();
		try {
			mediaPlayer.setDataSource(pathToSoundfile);
			mediaPlayer.prepare();
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					mp.release();
				}
			});
			Log.i("PlaySoundBrick", "Starting player with file " + pathToSoundfile);
			mediaPlayer.start();
		} catch (IOException e) {
			throw new IllegalArgumentException("IO error", e);
		}
	}

	public Sprite getSprite() {
		return null;
	}

	/* (non-Javadoc)
	 * @see at.tugraz.ist.catroid.content.brick.Brick#getView(android.content.Context)
	 */
	public View getView(LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.construction_brick_simple_text_view, null);
		TextView textView = (TextView) view.findViewById(R.id.OneElementBrick);
		textView.setText(R.string.come_to_front_main_adapter);
		return view;
	}
}
