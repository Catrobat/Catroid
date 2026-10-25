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

package org.catrobat.catroid.test.bluetooth

import android.Manifest.permission.BLUETOOTH
import android.Manifest.permission.BLUETOOTH_ADMIN
import android.Manifest.permission.BLUETOOTH_ADVERTISE
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import org.catrobat.catroid.bluetooth.BluetoothPermissions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class BluetoothPermissionsTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = mock(Context::class.java)
        `when`(context.checkPermission(anyString(), anyInt(), anyInt()))
            .thenReturn(PackageManager.PERMISSION_GRANTED)
    }

    @Test
    fun testRequiredPermissionsBeforeAndroid12() {
        assertEquals(
            listOf(BLUETOOTH_ADMIN, BLUETOOTH),
            BluetoothPermissions.getRequiredPermissions(Build.VERSION_CODES.R)
        )
    }

    @Test
    fun testRequiredPermissionsSinceAndroid12() {
        assertEquals(
            listOf(BLUETOOTH_ADVERTISE, BLUETOOTH_SCAN, BLUETOOTH_CONNECT),
            BluetoothPermissions.getRequiredPermissions(Build.VERSION_CODES.S)
        )
    }

    @Test
    fun testPermissionGrantedWhenAllRequiredPermissionsAreGranted() {
        assertTrue(BluetoothPermissions.checkBluetoothPermission(context, Build.VERSION_CODES.S))
    }

    @Test
    fun testPermissionMissingWhenConnectPermissionIsDenied() {
        deny(BLUETOOTH_CONNECT)

        assertFalse(BluetoothPermissions.checkBluetoothPermission(context, Build.VERSION_CODES.S))
    }

    @Test
    fun testPermissionMissingWhenScanPermissionIsDenied() {
        deny(BLUETOOTH_SCAN)

        assertFalse(BluetoothPermissions.checkBluetoothPermission(context, Build.VERSION_CODES.S))
    }

    @Test
    fun testAndroid12PermissionsAreNotRequiredBeforeAndroid12() {
        deny(BLUETOOTH_CONNECT)
        deny(BLUETOOTH_SCAN)
        deny(BLUETOOTH_ADVERTISE)

        assertTrue(BluetoothPermissions.checkBluetoothPermission(context, Build.VERSION_CODES.R))
    }

    private fun deny(permission: String) {
        `when`(context.checkPermission(eq(permission), anyInt(), anyInt()))
            .thenReturn(PackageManager.PERMISSION_DENIED)
    }
}
