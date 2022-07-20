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

package org.catrobat.catroid.utils

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.huawei.hms.api.HuaweiApiAvailability
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class MobileServiceAvailabilityTest {

    @Mock
    private lateinit var googleApiAvailability: GoogleApiAvailability
    @Mock
    private lateinit var huaweiApiAvailability: HuaweiApiAvailability
    @Mock
    private lateinit var context: Context

    private lateinit var mobileServiceAvailability: MobileServiceAvailability

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mobileServiceAvailability =
            MobileServiceAvailability(googleApiAvailability, huaweiApiAvailability)
    }

    @Test
    fun testGmsAvailable() {
        Mockito.`when`(googleApiAvailability.isGooglePlayServicesAvailable(context))
            .thenReturn(ConnectionResult.SUCCESS)

        assertTrue(mobileServiceAvailability.isGmsAvailable(context))
    }

    @Test
    fun testGmsNotAvailable() {
        Mockito.`when`(googleApiAvailability.isGooglePlayServicesAvailable(context))
            .thenReturn(ConnectionResult.SERVICE_MISSING)

        assertFalse(mobileServiceAvailability.isGmsAvailable(context))
    }

    @Test
    fun testHmsAvailable() {
        Mockito.`when`(huaweiApiAvailability.isHuaweiMobileServicesAvailable(context))
            .thenReturn(com.huawei.hms.api.ConnectionResult.SUCCESS)

        assertTrue(mobileServiceAvailability.isHmsAvailable(context))
    }

    @Test
    fun testHmsNotAvailable() {
        Mockito.`when`(huaweiApiAvailability.isHuaweiMobileServicesAvailable(context))
            .thenReturn(com.huawei.hms.api.ConnectionResult.SERVICE_MISSING)

        assertFalse(mobileServiceAvailability.isHmsAvailable(context))
    }
}
