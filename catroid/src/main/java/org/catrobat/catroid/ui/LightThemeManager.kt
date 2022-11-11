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

package org.catrobat.catroid.ui

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import org.catrobat.catroid.CatroidApplication.getAppContext
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.setLightThemeSharedPreferenceEnabled

object LightThemeManager {

    @JvmStatic
    fun setLightTheme(value: Boolean) {
        setLightThemeSharedPreferenceEnabled(getAppContext(), value)

        if (value) {
            setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    @JvmStatic
    fun isLightThemeEnabled(): Boolean {
        val currentNightMode =
            getAppContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_NO
    }

}