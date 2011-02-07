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
package at.tugraz.ist.catroid.test.content.brick;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.brick.PlaySoundBrick;
import at.tugraz.ist.catroid.stage.SoundManager;
import android.media.MediaPlayer;



public class PlaySoundBrickTest extends AndroidTestCase {

    String legalArgument = "";
    String illegalARgument = "";

    public void testPlaySound() {
        
        MediaPlayer media = SoundManager.getInstance().getMediaPlayer();    
        PlaySoundBrick testBrick = new PlaySoundBrick(legalArgument);
        testBrick.execute();
        assertTrue("Media Player is not playing", media.isPlaying());
    }

    public void testIllegalArgument() {
        try {
            PlaySoundBrick testBrick = new PlaySoundBrick(illegalARgument);
            testBrick.execute();
            fail("Execution of PlaySoundBrick with illegal Argument did not cause a IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            // expected result
        }
    }

    public void testIsPlayerStopping() {
        PlaySoundBrick testBrick = new PlaySoundBrick(legalArgument);
        testBrick.execute();
    }
}
