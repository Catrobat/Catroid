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

package org.catrobat.catroid.ui.recyclerview.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.MQTT_ENCRYPTED_PREFS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.MQTT_PASSWORD
import java.io.File

interface MqttPasswordRepository {
    fun getPassword(): String
    fun setPassword(password: String)
    fun clearPassword()
}

/**
 * Opens the encrypted store, recovering rather than failing when it cannot be read.
 *
 * The stored password is encrypted with a key held in the device keystore, and the
 * key is not kept alongside the file. The two can therefore drift apart: keystore
 * entries are known to become unusable after some system updates and on some
 * devices, which leaves a preferences file this device can no longer decrypt and
 * makes EncryptedSharedPreferences.create() throw. Since the repository is a Koin
 * singleton pulled in when a stage starts, an unhandled failure there takes down
 * the whole activity and the user cannot run any project at all.
 *
 * Losing a saved broker password is a far better outcome than a crash, so an
 * unreadable store is discarded and recreated, and if even that fails the password
 * is simply not persisted for this session.
 */
@Suppress("TooGenericExceptionCaught")
fun createMqttPasswordRepository(context: Context): MqttPasswordRepository = try {
    DefaultMqttPasswordRepository(encryptedPreferences(context))
} catch (e: Exception) {
    Log.e(TAG, "MQTT password store unreadable, discarding it", e)
    try {
        deleteEncryptedPreferences(context)
        DefaultMqttPasswordRepository(encryptedPreferences(context))
    } catch (recreateFailure: Exception) {
        Log.e(TAG, "Could not recreate MQTT password store, continuing in memory", recreateFailure)
        InMemoryMqttPasswordRepository()
    }
}

private const val TAG = "MqttPasswordRepository"

private fun encryptedPreferences(context: Context): SharedPreferences =
    EncryptedSharedPreferences.create(
        MQTT_ENCRYPTED_PREFS,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

private fun deleteEncryptedPreferences(context: Context) {
    context.deleteSharedPreferences(MQTT_ENCRYPTED_PREFS)
    File(context.applicationInfo.dataDir, "shared_prefs/$MQTT_ENCRYPTED_PREFS.xml").delete()
}

/** Last resort so the app keeps working when nothing can be persisted. */
class InMemoryMqttPasswordRepository : MqttPasswordRepository {
    private var password = ""
    override fun getPassword() = password
    override fun setPassword(password: String) {
        this.password = password
    }

    override fun clearPassword() {
        password = ""
    }
}

class DefaultMqttPasswordRepository(private val prefs: SharedPreferences) : MqttPasswordRepository {

    override fun getPassword() = prefs.getString(MQTT_PASSWORD, "").orEmpty()

    override fun setPassword(password: String) =
        prefs.edit { putString(MQTT_PASSWORD, password) }

    override fun clearPassword() =
        prefs.edit { remove(MQTT_PASSWORD) }
}
