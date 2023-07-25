/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.test.formulaeditor.sensor

import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.nfc.NfcHandler
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(NfcHandler::class)
class SensorNFCSensorsTest {

    @Test
    fun nfcTagIdTest() {
        PowerMockito.mockStatic(NfcHandler::class.java)
        PowerMockito.`when`(NfcHandler.getLastNfcTagId()).thenReturn(expectedId)
        compareToSensor(expectedId, Sensors.NFC_TAG_ID)
    }

    @Test
    fun nfcTagMessageTest() {
        PowerMockito.mockStatic(NfcHandler::class.java)
        PowerMockito.`when`(NfcHandler.getLastNfcTagMessage()).thenReturn(expectedMessage)
        compareToSensor(expectedMessage, Sensors.NFC_TAG_MESSAGE)
    }

    private fun compareToSensor(value: String, sensor: Sensors) {
        Assert.assertEquals(value, sensor.getSensor().getSensorValue() as String)
    }

    companion object {
        const val expectedId: String = "testId"
        const val expectedMessage: String = "testMessage"
    }
}
