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
package org.catrobat.catroid.test.formulaeditor;

import java.io.IOException;

import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.soundrecorder.SoundRecorder;
import org.catrobat.catroid.test.utils.Reflection;

import android.test.InstrumentationTestCase;

public class SensorLoudnessTest extends InstrumentationTestCase {

	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		SensorHandler.stopSensorListeners();
		Reflection.setPrivateField(SensorHandler.class, "instance", null);
		super.tearDown();
	}

	public void testMicRelease() {
		SoundRecorder outside_recorder = new SoundRecorder("dev/null");
		try {
			outside_recorder.start();
			//Wait till SoundRecorder actually started
			Thread.sleep(100L);
		} catch (Exception e) {
			fail("Couldn't start Recorder Outside, some other app may use a MIC");
		}
		assertEquals("Outside Recorder is not recording after start", outside_recorder.isRecording(), true);
		try {
			outside_recorder.stop();
		} catch (IOException e) {
		}

		SensorHandler.startSensorListener(getInstrumentation().getTargetContext());

		outside_recorder = new SoundRecorder("dev/null");
		try {
			outside_recorder.start();
			//Wait till SoundRecorder actually started
			Thread.sleep(100L);
		} catch (Exception e) {
			//expected behavior
		}
		assertEquals("LoudnessSensor may not use Microphone-Input", outside_recorder.isRecording(), false);

		SensorHandler.stopSensorListeners();

		outside_recorder = new SoundRecorder("dev/null");
		try {
			outside_recorder.start();
			//Wait till SoundRecorder actually started
			Thread.sleep(100L);
			outside_recorder.stop();
		} catch (Exception e) {
			fail("Couldn't start Recorder after stopping Sensors, LoudnessSensor may still holds MIC");
		}
	}
}
