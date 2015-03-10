/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.test.formulaeditor;

import android.test.InstrumentationTestCase;

import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.formulaeditor.SensorLoudness;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.SimulatedSoundRecorder;

public class SensorLoudnessTest extends InstrumentationTestCase {

	@Override
	public void tearDown() throws Exception {
		SensorHandler.stopSensorListeners();
		Reflection.setPrivateField(SensorLoudness.class, "instance", null);
		super.tearDown();
	}

	public void testMicRelease() {
		//Initialize
		SensorLoudness.getSensorLoudness();
		SensorLoudness loudnessSensor = (SensorLoudness) Reflection.getPrivateField(SensorLoudness.class, "instance");
		SimulatedSoundRecorder simSoundRec = new SimulatedSoundRecorder("/dev/null");
		Reflection.setPrivateField(loudnessSensor, "recorder", simSoundRec);

		SensorHandler.startSensorListener(getInstrumentation().getTargetContext());
		assertEquals("LoudnessSensor not startet recording, isRecording()", true, simSoundRec.isRecording());
		SensorHandler.stopSensorListeners();
		assertEquals("LoudnessSensor not stopped recording, isRecording()", false, simSoundRec.isRecording());
	}
}
