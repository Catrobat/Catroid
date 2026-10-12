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

package org.catrobat.catroid.test.mqtt

import android.os.Build
import android.preference.PreferenceManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.catrobat.catroid.ui.recyclerview.repository.MqttPasswordRepository
import org.catrobat.catroid.ui.settingsfragments.MqttSettingsFragment
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.junit.After

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], instrumentedPackages = [])
class MqttSettingsTest {

    private val mockMqttPasswordRepository = mockk<MqttPasswordRepository>(relaxed = true)
    private val context get() = RuntimeEnvironment.getApplication()

    @Before
    fun setUp() {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit()
        stopKoin()
        startKoin {
            modules(module {
                single<MqttPasswordRepository> { mockMqttPasswordRepository }
            })
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    // --- Default value tests ---

    @Test
    fun testMqttEnabledDefaultIsFalse() {
        assertFalse(SettingsFragment.isMqttSharedPreferenceEnabled(context))
    }

    @Test
    fun testMqttHostDefaultIs192168_0_1() {
        assertEquals("192.168.0.1", SettingsFragment.getMqttHost(context))
    }

    @Test
    fun testMqttPortDefaultIs1883() {
        assertEquals(1883, SettingsFragment.getMqttPort(context))
    }

    @Test
    fun testMqttTlsEnabledDefaultIsFalse() {
        assertFalse(SettingsFragment.isMqttTlsEnabled(context))
    }

    @Test
    fun testMqttUsernameDefaultIsEmpty() {
        assertEquals("", SettingsFragment.getMqttUsername(context))
    }

    @Test
    fun testMqttPasswordDefaultIsEmpty() {
        every { mockMqttPasswordRepository.getPassword() } returns ""
        assertEquals("", SettingsFragment.getMqttPassword())
        verify(exactly = 1) { mockMqttPasswordRepository.getPassword() }
    }

    @Test
    fun testMqttClientIdDefaultIsEmpty() {
        assertEquals("", SettingsFragment.getMqttClientId(context))
    }

    // --- Persistence tests ---

    @Test
    fun testMqttEnabledPersistsAfterWrite() {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putBoolean(SettingsFragment.SETTINGS_SHOW_MQTT_BRICKS, true).commit()
        assertTrue(SettingsFragment.isMqttSharedPreferenceEnabled(context))
    }

    @Test
    fun testMqttHostPersistsAfterWrite() {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putString(SettingsFragment.MQTT_HOST, "broker.hivemq.com").commit()
        assertEquals("broker.hivemq.com", SettingsFragment.getMqttHost(context))
    }

    @Test
    fun testMqttPortPersistsAfterWrite() {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putString(SettingsFragment.MQTT_PORT, "8883").commit()
        assertEquals(8883, SettingsFragment.getMqttPort(context))
    }

    @Test
    fun testMqttTlsPersistsAfterWrite() {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putBoolean(SettingsFragment.MQTT_TLS, true).commit()
        assertTrue(SettingsFragment.isMqttTlsEnabled(context))
    }

    @Test
    fun testMqttUsernamePersistsAfterWrite() {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putString(SettingsFragment.MQTT_USERNAME, "testuser").commit()
        assertEquals("testuser", SettingsFragment.getMqttUsername(context))
    }

    @Test
    fun testMqttPasswordPersistsAfterWrite() {
        every { mockMqttPasswordRepository.getPassword() } returns "secret"
        assertEquals("secret", SettingsFragment.getMqttPassword())
        verify(exactly = 1) { mockMqttPasswordRepository.getPassword() }
    }

    @Test
    fun testMqttPasswordNotStoredInDefaultPrefs() {
        assertFalse(
            PreferenceManager.getDefaultSharedPreferences(context)
                .contains(SettingsFragment.MQTT_PASSWORD)
        )
    }

    @Test
    fun testMqttClientIdPersistsAfterWrite() {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putString(SettingsFragment.MQTT_CLIENT_ID, "device-001").commit()
        assertEquals("device-001", SettingsFragment.getMqttClientId(context))
    }

    // --- getMqttPort fallback tests ---

    @Test
    fun testMqttPortFallbackOnNonIntegerValue() {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putString(SettingsFragment.MQTT_PORT, "abc").commit()
        assertEquals(1883, SettingsFragment.getMqttPort(context))
    }

    @Test
    fun testMqttPortFallbackOnEmptyValue() {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putString(SettingsFragment.MQTT_PORT, "").commit()
        assertEquals(1883, SettingsFragment.getMqttPort(context))
    }

    @Test
    fun testMqttPortFallbackOnFloatValue() {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putString(SettingsFragment.MQTT_PORT, "1883.5").commit()
        assertEquals(1883, SettingsFragment.getMqttPort(context))
    }

    // --- Port validation: boundary tests ---

    @Test
    fun testPortValidationRejectsZero() {
        assertFalse(MqttSettingsFragment.isValidPort("0"))
    }

    @Test
    fun testPortValidationRejectsNegativeNumber() {
        assertFalse(MqttSettingsFragment.isValidPort("-1"))
    }

    @Test
    fun testPortValidationRejects65536() {
        assertFalse(MqttSettingsFragment.isValidPort("65536"))
    }

    @Test
    fun testPortValidationAcceptsMinBoundary() {
        assertTrue(MqttSettingsFragment.isValidPort("1"))
    }

    @Test
    fun testPortValidationAccepts1883() {
        assertTrue(MqttSettingsFragment.isValidPort("1883"))
    }

    @Test
    fun testPortValidationAcceptsMaxBoundary() {
        assertTrue(MqttSettingsFragment.isValidPort("65535"))
    }

    // --- Port validation: non-numeric input ---

    @Test
    fun testPortValidationRejectsAlphabeticString() {
        assertFalse(MqttSettingsFragment.isValidPort("abc"))
    }

    @Test
    fun testPortValidationRejectsEmptyString() {
        assertFalse(MqttSettingsFragment.isValidPort(""))
    }

    @Test
    fun testPortValidationRejectsPartialNumber() {
        assertFalse(MqttSettingsFragment.isValidPort("1883abc"))
    }

    @Test
    fun testPortValidationRejectsWhitespace() {
        assertFalse(MqttSettingsFragment.isValidPort(" 1883 "))
    }

    @Test
    fun testPortValidationRejectsFloatString() {
        assertFalse(MqttSettingsFragment.isValidPort("1883.5"))
    }
}
