/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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
package org.catrobat.catroid.formulaeditor

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * The face name sensor.
 *
 * The original code kept this value in sensorValueMap, an instance field.
 * destroy() sets instance to null and the map goes with it, so the name was
 * stored and then silently deleted, and the sensor returned the number 0.
 *
 * theNameSurvivesDestroy is the test for that. It is why the value has to
 * live in a static field.
 */
@RunWith(RobolectricTestRunner::class)
class SensorHandlerFaceNameTest {

    @Before
    fun setUp() {
        SensorHandler.setFaceNameRecognitionResult("Unknown")
    }

    @After
    fun tearDown() {
        SensorHandler.setFaceNameRecognitionResult("Unknown")
    }

    // ---------------- Storing ----------------

    @Test
    fun aNameIsStoredAndReadBack() {
        SensorHandler.setFaceNameRecognitionResult("salah")
        assertEquals("salah", SensorHandler.getSensorValue(Sensors.ON_DEVICE_FACE_RECOGNITION))
    }

    @Test
    fun aLaterNameReplacesTheEarlierOne() {
        SensorHandler.setFaceNameRecognitionResult("salah")
        SensorHandler.setFaceNameRecognitionResult("karim")
        assertEquals("karim", SensorHandler.getSensorValue(Sensors.ON_DEVICE_FACE_RECOGNITION))
    }

    @Test
    fun nullBecomesUnknown() {
        SensorHandler.setFaceNameRecognitionResult(null)
        assertEquals("Unknown", SensorHandler.getSensorValue(Sensors.ON_DEVICE_FACE_RECOGNITION))
    }

    @Test
    fun anEmptyNameBecomesUnknown() {
        SensorHandler.setFaceNameRecognitionResult("")
        assertEquals("Unknown", SensorHandler.getSensorValue(Sensors.ON_DEVICE_FACE_RECOGNITION))
    }

    @Test
    fun whitespaceOnlyBecomesUnknown() {
        SensorHandler.setFaceNameRecognitionResult("    ")
        assertEquals("Unknown", SensorHandler.getSensorValue(Sensors.ON_DEVICE_FACE_RECOGNITION))
    }

    @Test
    fun surroundingSpacesAreTrimmed() {
        SensorHandler.setFaceNameRecognitionResult("  salah  ")
        assertEquals("salah", SensorHandler.getSensorValue(Sensors.ON_DEVICE_FACE_RECOGNITION))
    }

    @Test
    fun namesWithSpacesInsideAreKept() {
        SensorHandler.setFaceNameRecognitionResult("Kazi Jahid")
        assertEquals("Kazi Jahid", SensorHandler.getSensorValue(Sensors.ON_DEVICE_FACE_RECOGNITION))
    }

    // ---------------- The bug ----------------

    @Test
    fun theNameSurvivesDestroy() {
        SensorHandler.setFaceNameRecognitionResult("salah")
        SensorHandler.destroy()

        assertEquals(
            "destroy() must not delete the detected name",
            "salah",
            SensorHandler.getSensorValue(Sensors.ON_DEVICE_FACE_RECOGNITION)
        )
    }

    @Test
    fun theNameCanBeSetAfterDestroy() {
        SensorHandler.destroy()
        SensorHandler.setFaceNameRecognitionResult("salah")
        assertEquals("salah", SensorHandler.getSensorValue(Sensors.ON_DEVICE_FACE_RECOGNITION))
    }

    @Test
    fun readingAfterDestroyDoesNotThrow() {
        SensorHandler.destroy()
        assertNotNull(SensorHandler.getSensorValue(Sensors.ON_DEVICE_FACE_RECOGNITION))
    }

    // ---------------- Type ----------------

    @Test
    fun theSensorReturnsTextNotANumber() {
        SensorHandler.setFaceNameRecognitionResult("salah")
        val value = SensorHandler.getSensorValue(Sensors.ON_DEVICE_FACE_RECOGNITION)

        assertTrue(
            "a name must come back as text, a Double here means the value " +
                "was lost and getOrDefault returned 0.0",
            value is String
        )
    }

    @Test
    fun anUnsetSensorStillReturnsText() {
        val value = SensorHandler.getSensorValue(Sensors.ON_DEVICE_FACE_RECOGNITION)
        assertTrue(value is String)
        assertFalse((value as String).isEmpty())
    }

    @Test
    fun readingManyTimesIsStable() {
        SensorHandler.setFaceNameRecognitionResult("salah")
        for (i in 0 until 100) {
            assertEquals("salah", SensorHandler.getSensorValue(Sensors.ON_DEVICE_FACE_RECOGNITION))
        }
    }
}
