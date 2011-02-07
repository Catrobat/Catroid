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
import android.media.MediaPlayer;
import at.tugraz.ist.catroid.stage.SoundManager;

public class PlaySoundBrick implements Brick {
    private String pathToSoundfile;
    private static final long serialVersionUID = 1L;

    public PlaySoundBrick(String pathToSoundfile) {
        this.pathToSoundfile = pathToSoundfile;
    }

    public void execute() throws IllegalArgumentException, IllegalStateException {
        MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();
        // MediaPlayer mediaPlayer = new MediaPlayer();
        if (mediaPlayer == null)
            throw new NullPointerException("Media Player is null"); // TODO:
                                                                    // "or retry?"
        try {
            mediaPlayer.setDataSource(pathToSoundfile);
            mediaPlayer.prepare();
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }

        mediaPlayer.start();

    }

}
