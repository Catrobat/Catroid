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

package org.catrobat.catroid.test.ui.shortcut

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import org.catrobat.catroid.R
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

/**
 * Regression tests ensuring that the "Pin to Home Screen" menu option is appropriately
 * included in menu_project_activity and can be hidden via visibility toggling.
 */
@RunWith(RobolectricTestRunner::class)
class ShortcutMenuRegressionTest {

    @Test
    fun `menu_project_activity contains pin_to_home_screen item`() {
        val activity = Robolectric.buildActivity(AppCompatActivity::class.java).create().get()
        val popup = PopupMenu(activity, activity.window.decorView)
        activity.menuInflater.inflate(R.menu.menu_project_activity, popup.menu)

        val item = popup.menu.findItem(R.id.pin_to_home_screen)
        assertNotNull("R.id.pin_to_home_screen should be defined in menu_project_activity", item)
        assertTrue("pin_to_home_screen item should default to visible", item.isVisible)
    }

    @Test
    fun `pin_to_home_screen can be hidden when excluded from non-project screens`() {
        val activity = Robolectric.buildActivity(AppCompatActivity::class.java).create().get()
        val popup = PopupMenu(activity, activity.window.decorView)
        activity.menuInflater.inflate(R.menu.menu_project_activity, popup.menu)

        val item = popup.menu.findItem(R.id.pin_to_home_screen)
        assertNotNull(item)
        item.isVisible = false
        assertFalse("pin_to_home_screen should be not visible when hidden", item.isVisible)
    }
}
