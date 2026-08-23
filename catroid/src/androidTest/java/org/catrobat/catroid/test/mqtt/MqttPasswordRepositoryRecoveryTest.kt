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

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ui.recyclerview.repository.createMqttPasswordRepository
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.MQTT_ENCRYPTED_PREFS
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class MqttPasswordRepositoryRecoveryTest {

    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    private fun prefsFile() =
        File(context.applicationInfo.dataDir, "shared_prefs/$MQTT_ENCRYPTED_PREFS.xml")

    @After
    fun tearDown() {
        context.deleteSharedPreferences(MQTT_ENCRYPTED_PREFS)
        prefsFile().delete()
    }

    @Test
    fun testRepositoryOpensNormally() {
        val repository = createMqttPasswordRepository(context)
        repository.setPassword("secret")
        assertEquals("secret", repository.getPassword())
    }

    @Test
    // A store the keystore key can no longer decrypt must not take StageActivity
    // down with it.
    fun testUndecryptablePreferencesDoNotThrow() {
        createMqttPasswordRepository(context).setPassword("secret")
        context.deleteSharedPreferences(MQTT_ENCRYPTED_PREFS)

        prefsFile().parentFile?.mkdirs()
        prefsFile().writeText(
            """<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
               <map><string name="not_really_encrypted">garbage</string></map>"""
        )

        val repository = createMqttPasswordRepository(context)
        assertNotNull("opening an unreadable store must not throw", repository)
    }

    @Test
    fun testRepositoryIsUsableAgainAfterRecovery() {
        prefsFile().parentFile?.mkdirs()
        prefsFile().writeText("this is not a valid encrypted preferences file")

        val repository = createMqttPasswordRepository(context)
        repository.setPassword("fresh")

        assertEquals("fresh", repository.getPassword())
    }
}
