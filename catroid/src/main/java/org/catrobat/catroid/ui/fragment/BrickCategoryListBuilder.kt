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

package org.catrobat.catroid.ui.fragment

import android.preference.PreferenceManager
import android.view.View
import androidx.fragment.app.FragmentActivity
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.adapter.BrickCategoryAdapter
import org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile
import org.catrobat.catroid.ui.settingsfragments.RaspberryPiSettingsFragment
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import java.util.ArrayList

class BrickCategoryListBuilder(private val activity: FragmentActivity) {

    private fun onlyBeginnerBricks(): Boolean = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(
        AccessibilityProfile.BEGINNER_BRICKS, false)

    @SuppressWarnings("ComplexMethod")
    fun getBrickCategoryViews(): MutableList<View> {
        val categories: MutableList<View> = ArrayList()
        val inflater = activity.layoutInflater

        categories.add(inflater.inflate(R.layout.brick_category_recently_used, null))

        if (SettingsFragment.isEmroiderySharedPreferenceEnabled(activity)) {
            categories.add(inflater.inflate(R.layout.brick_category_embroidery, null))
        }
        if (SettingsFragment.isPlotSharedPreferenceEnabled(activity)) {
            categories.add(inflater.inflate(R.layout.brick_category_plot, null))
        }
        if (SettingsFragment.isMindstormsNXTSharedPreferenceEnabled(activity)) {
            categories.add(inflater.inflate(R.layout.brick_category_lego_nxt, null))
        }
        if (SettingsFragment.isMindstormsEV3SharedPreferenceEnabled(activity)) {
            categories.add(inflater.inflate(R.layout.brick_category_lego_ev3, null))
        }
        if (SettingsFragment.isDroneSharedPreferenceEnabled(activity)) {
            categories.add(inflater.inflate(R.layout.brick_category_drone, null))
        }
        if (SettingsFragment.isJSSharedPreferenceEnabled(activity)) {
            categories.add(inflater.inflate(R.layout.brick_category_drone_js, null))
        }
        if (SettingsFragment.isArduinoSharedPreferenceEnabled(activity)) {
            categories.add(inflater.inflate(R.layout.brick_category_arduino, null))
        }
        if (RaspberryPiSettingsFragment.isRaspiSharedPreferenceEnabled(activity)) {
            categories.add(inflater.inflate(R.layout.brick_category_raspi, null))
        }
        if (SettingsFragment.isPhiroSharedPreferenceEnabled(activity)) {
            categories.add(inflater.inflate(R.layout.brick_category_phiro, null))
        }
        if (ProjectManager.getInstance().currentProject.isCastProject) {
            categories.add(inflater.inflate(R.layout.brick_category_chromecast, null))
        }

        categories.add(inflater.inflate(R.layout.brick_category_event, null))
        categories.add(inflater.inflate(R.layout.brick_category_control, null))
        categories.add(inflater.inflate(R.layout.brick_category_motion, null))
        categories.add(inflater.inflate(R.layout.brick_category_sound, null))
        categories.add(inflater.inflate(R.layout.brick_category_looks, null))

        if (!onlyBeginnerBricks()) {
            categories.add(inflater.inflate(R.layout.brick_category_pen, null))
        }

        categories.add(inflater.inflate(R.layout.brick_category_data, null))
        categories.add(inflater.inflate(R.layout.brick_category_device, null))

        if (!onlyBeginnerBricks()) {
            categories.add(inflater.inflate(R.layout.brick_category_userbrick, null))
        }
        if (SettingsFragment.isTestSharedPreferenceEnabled(activity)) {
            categories.add(inflater.inflate(R.layout.brick_category_assert, null))
        }

        return categories
    }

    fun getCategoryNames(): List<String> {
        val categoryNames = arrayListOf<String>()

        val brickCategoryAdapter = BrickCategoryAdapter(getBrickCategoryViews())
        for (categoryIndex in 0 until brickCategoryAdapter.count) {
            categoryNames.add(brickCategoryAdapter.getItem(categoryIndex))
        }

        return categoryNames
    }
}
