/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
import android.graphics.Rect
import androidx.test.annotation.UiThreadTest
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.camera.VisualDetectionHandler.facesForSensors
import org.catrobat.catroid.camera.VisualDetectionHandler.updateFaceDetectionStatusSensorValues
import org.catrobat.catroid.camera.VisualDetectionHandler.updateFaceSensorValues
import org.catrobat.catroid.camera.VisualDetectionHandlerFace
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
import org.koin.java.KoinJavaComponent.inject
import org.mockito.Mockito

class SensorHandlerTest {
    private val projectManager: ProjectManager by inject(ProjectManager::class.java)

    @get:Rule
    val runtimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )

    @Before
    fun setUp() {
        projectManager.currentProject = Project(
            ApplicationProvider.getApplicationContext(),
            TestUtils.DEFAULT_TEST_PROJECT_NAME
        )
    }

    @Test
    fun testSensorManagerNotInitialized() {
        SensorHandler.destroy()
        SensorHandler.registerListener(null)
        SensorHandler.unregisterListener(null)
        SensorHandler.startSensorListener(ApplicationProvider.getApplicationContext())
        compareToSensor(0, Sensors.X_ACCELERATION)
    }

    @Test
    fun testSensorHandlerWithLookSensorValue() {
        SensorHandler.startSensorListener(ApplicationProvider.getApplicationContext())
        compareToSensor(0, Sensors.OBJECT_BRIGHTNESS)
    }

    @Test
    fun testFirstFaceDetection() {
        SensorHandler.startSensorListener(ApplicationProvider.getApplicationContext())
        compareToSensor(0, Sensors.FACE_DETECTED)
        compareToSensor(0, Sensors.FACE_SIZE)

        val firstFaceSize = 50
        val firstFacePosition = Point(15, -15)
        facesForSensors[0] = VisualDetectionHandlerFace(0, Rect())

        updateFaceDetectionStatusSensorValues()
        updateFaceSensorValues(firstFacePosition, firstFaceSize, 0)

        compareToSensor(1, Sensors.FACE_DETECTED)
        compareToSensor(firstFaceSize, Sensors.FACE_SIZE)
        compareToSensor(firstFacePosition.x, Sensors.FACE_X)
        compareToSensor(firstFacePosition.y, Sensors.FACE_Y)
    }

    @Test
    fun testSecondFaceDetection() {
        SensorHandler.startSensorListener(ApplicationProvider.getApplicationContext())
        compareToSensor(0, Sensors.SECOND_FACE_DETECTED)
        compareToSensor(0, Sensors.SECOND_FACE_SIZE)

        val secondFaceSize = 50
        val secondFacePosition = Point(15, -15)
        facesForSensors[1] = VisualDetectionHandlerFace(1, Rect())

        updateFaceDetectionStatusSensorValues()
        updateFaceSensorValues(secondFacePosition, secondFaceSize, 1)

        compareToSensor(1, Sensors.SECOND_FACE_DETECTED)
        compareToSensor(secondFaceSize, Sensors.SECOND_FACE_SIZE)
        compareToSensor(secondFacePosition.x, Sensors.SECOND_FACE_X)
        compareToSensor(secondFacePosition.y, Sensors.SECOND_FACE_Y)
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
        SensorHandler.destroy()
    }

    private fun compareToSensor(value: Int, sensor: Sensors) {
        assertEquals(value.toDouble(), SensorHandler.getSensorValue(sensor) as Double, DELTA)
    }

    companion object {
        private const val DELTA = 0.01
    }
}
