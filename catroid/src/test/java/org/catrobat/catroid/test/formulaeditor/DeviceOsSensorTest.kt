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

package org.catrobat.catroid.test.formulaeditor

import org.catrobat.catroid.formulaeditor.SensorHandler
import org.junit.Assert
import org.junit.Test
import java.lang.reflect.Method

class DeviceOsSensorTest {

    private var osVersion: String? = null

    private fun setOsVersionForTest(versionToTest: String) {
        osVersion = System.getProperty("os.version")
        System.setProperty("os.version", versionToTest)
    }

    private fun restoreOsVersion() {
        System.setProperty("os.version", osVersion)
    }

    @Test
    fun osTestUnknown() {
        setOsVersionForTest("Test")
        val method: Method = SensorHandler::class.java.getDeclaredMethod("getOSName")
        method.isAccessible = true
        val devicOS = method.invoke(this)
        restoreOsVersion()
        Assert.assertEquals("Unknown System", devicOS)
    }

    @Test
    fun osTestUnknown2() {
        setOsVersionForTest("Test-Test2")
        val method: Method = SensorHandler::class.java.getDeclaredMethod("getOSName")
        method.isAccessible = true
        val devicOS = method.invoke(this)
        restoreOsVersion()
        Assert.assertEquals("Unknown System", devicOS)
    }

    @Test
    fun osTestKnown() {
        setOsVersionForTest("Tasdest-Test-dfgdfg-fds")
        val method: Method = SensorHandler::class.java.getDeclaredMethod("getOSName")
        method.isAccessible = true
        val devicOS = method.invoke(this)
        restoreOsVersion()
        Assert.assertEquals("Test", devicOS)
    }

    @Test
    fun osTestKnown2() {
        setOsVersionForTest("Tasdest2-Test2-2fg-fds")
        val method: Method = SensorHandler::class.java.getDeclaredMethod("getOSName")
        method.isAccessible = true
        val devicOS = method.invoke(this)
        restoreOsVersion()
        Assert.assertEquals("Test2", devicOS)
    }
}
