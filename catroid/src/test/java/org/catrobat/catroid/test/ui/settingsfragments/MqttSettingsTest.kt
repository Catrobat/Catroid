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
package org.catrobat.catroid.test.ui.settingsfragments

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.catrobat.catroid.common.SharedPreferenceKeys.MQTT_BROKER_HOST_KEY
import org.catrobat.catroid.common.SharedPreferenceKeys.MQTT_BROKER_PORT_KEY
import org.catrobat.catroid.common.SharedPreferenceKeys.MQTT_ENABLED_KEY
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class MqttSettingsTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun mqttEnabledKeyFollowsNamingConvention() {
        assertEquals("setting_mqtt_enabled", MQTT_ENABLED_KEY)
    }

    @Test
    fun mqttBrokerHostKeyFollowsNamingConvention() {
        assertEquals("setting_mqtt_broker_host", MQTT_BROKER_HOST_KEY)
    }

    @Test
    fun mqttBrokerPortKeyFollowsNamingConvention() {
        assertEquals("setting_mqtt_broker_port", MQTT_BROKER_PORT_KEY)
    }

    @Test
    fun getMqttEnabledDefaultValueIsFalse() {
        val result = SettingsFragment.getMqttEnabled(context)
        assertFalse(result)
    }

    @Test
    fun getMqttBrokerHostDefaultValueIsEmptyString() {
        val result = SettingsFragment.getMqttBrokerHost(context)
        assertEquals("", result)
    }

    @Test
    fun getMqttBrokerPortDefaultValueIs1883() {
        val result = SettingsFragment.getMqttBrokerPort(context)
        assertEquals(1883, result)
    }
}
