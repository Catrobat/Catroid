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
package org.catrobat.catroid.nativetest.content.sprite;

import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.stage.NativeAppActivity;

import android.media.MediaPlayer;
import android.test.InstrumentationTestCase;

public class SoundManagerTest extends InstrumentationTestCase {

	@Override
	protected void tearDown() throws Exception {
		NativeAppActivity.setContext(null);
		super.tearDown();
	}

	public void testPlaySoundfile() throws InterruptedException {
		String soundfileName = "test_sound.mp3";
		NativeAppActivity.setContext(getInstrumentation().getContext());
		MediaPlayer mediaPlayer = SoundManager.getInstance().playSoundFile(soundfileName);
		assertTrue("MediaPlayer is not playing", mediaPlayer.isPlaying());

		SoundManager.getInstance().pause();
		assertFalse("MediaPlayer is still playing after SoundManager was paused", mediaPlayer.isPlaying());

		SoundManager.getInstance().resume();
		assertTrue("MediaPlayer is not playing after resume", mediaPlayer.isPlaying());
	}
}
