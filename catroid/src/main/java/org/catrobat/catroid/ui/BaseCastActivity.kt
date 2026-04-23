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
package org.catrobat.catroid.ui

import android.view.Menu
import android.view.MenuItem
import org.catrobat.catroid.R
import org.catrobat.catroid.cast.CastManager
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment

abstract class BaseCastActivity : BaseActivity() {
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (SettingsFragment.isCastSharedPreferenceEnabled(this)) {
            CastManager.getInstance().setCastButton(menu.findItem(R.id.cast_button))
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cast_button -> CastManager.getInstance().openDeviceSelectorOrDisconnectDialog()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}
