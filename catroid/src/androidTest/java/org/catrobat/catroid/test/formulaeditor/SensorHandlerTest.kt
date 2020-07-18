/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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
package org.catrobat.catroid.test.formulaeditor

import android.Manifest
import android.graphics.Point
import androidx.test.annotation.UiThreadTest
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.camera.FaceDetector
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.formulaeditor.SensorLoudness
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.soundrecorder.SoundRecorder
import org.catrobat.catroid.test.utils.TestUtils
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class SensorHandlerTest {
    @get:Rule
    val runtimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )

    @Before
    fun setUp() {
        ProjectManager.getInstance().currentProject = Project(
            ApplicationProvider.getApplicationContext(),
            TestUtils.DEFAULT_TEST_PROJECT_NAME
        )
    }

    @Test
    fun testSensorManagerNotInitialized() {
        SensorHandler.registerListener(null)
        SensorHandler.unregisterListener(null)
        SensorHandler.startSensorListener(ApplicationProvider.getApplicationContext())
        compareToSensor(0, Sensors.X_ACCELERATION)
    }

    @Test
    fun testSensorHandlerWithLookSensorValue() {
        compareToSensor(0, Sensors.OBJECT_BRIGHTNESS)
    }

    @Test
    fun testFaceDetection() {
        SensorHandler.startSensorListener(ApplicationProvider.getApplicationContext())
        compareToSensor(0, Sensors.FACE_DETECTED)
        compareToSensor(0, Sensors.FACE_SIZE)

        val size = 50
        val position = Point(15, -15)
        FaceDetector.updateDetectionStatus(true)
        FaceDetector.onFaceDetected(position, size)

        compareToSensor(1, Sensors.FACE_DETECTED)
        compareToSensor(size, Sensors.FACE_SIZE)
        compareToSensor(position.x, Sensors.FACE_X_POSITION)
        compareToSensor(position.y, Sensors.FACE_Y_POSITION)
    }

    @Test
    @UiThreadTest
    fun testMicRelease() {
        val loudnessSensor = SensorLoudness()
        val soundRecorder = Mockito.mock(SoundRecorder::class.java)
        loudnessSensor.soundRecorder = soundRecorder

        Mockito.`when`(soundRecorder.isRecording).thenReturn(false)
        SensorHandler.getInstance(ApplicationProvider.getApplicationContext()).setSensorLoudness(loudnessSensor)

        SensorHandler.startSensorListener(ApplicationProvider.getApplicationContext())
        Mockito.`when`(soundRecorder.isRecording).thenReturn(true)
        Mockito.verify(soundRecorder).start()

        SensorHandler.stopSensorListeners()
        Mockito.verify(soundRecorder).stop()
    }

    @After
    fun tearDown() {
        SensorHandler.stopSensorListeners()
    }

    private fun compareToSensor(value: Int, sensor: Sensors) {
        assertEquals(value.toDouble(), SensorHandler.getSensorValue(sensor) as Double, DELTA)
    }

    companion object {
        private const val DELTA = 0.01
    }
}
