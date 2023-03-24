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
package org.catrobat.catroid.test.common

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert
import org.junit.runner.RunWith
import org.catrobat.catroid.common.CatroidService
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService
import org.catrobat.catroid.common.ServiceProvider
import org.catrobat.catroid.test.common.ServiceProviderTest.TestService
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class ServiceProviderTest {
    @Test
    fun testCommonServices() {
        val service: CatroidService =
            ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE)
        Assert.assertNotNull(service)
        Assert.assertTrue(service is BluetoothDeviceService)
    }

    @Test
    fun testRegisterAndGetService() {
        Assert.assertNull(
            ServiceProvider.getService(
                TestService::class.java
            )
        )
        ServiceProvider.registerService(
            TestService::class.java, TestService()
        )
        val service = ServiceProvider.getService(
            TestService::class.java
        )
        Assert.assertNotNull(service)
    }

    private class TestService : CatroidService
}