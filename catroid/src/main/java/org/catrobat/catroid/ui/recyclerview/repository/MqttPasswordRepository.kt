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

import android.content.SharedPreferences
import androidx.core.content.edit
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.MQTT_PASSWORD

interface MqttPasswordRepository {
    fun getPassword(): String
    fun setPassword(password: String)
    fun clearPassword()
}

class DefaultMqttPasswordRepository(private val prefs: SharedPreferences) : MqttPasswordRepository {

    override fun getPassword() = prefs.getString(MQTT_PASSWORD, "").orEmpty()

    override fun setPassword(password: String) =
        prefs.edit { putString(MQTT_PASSWORD, password) }

    override fun clearPassword() =
        prefs.edit { remove(MQTT_PASSWORD) }
}
