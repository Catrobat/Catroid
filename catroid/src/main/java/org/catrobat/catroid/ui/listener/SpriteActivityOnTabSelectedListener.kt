/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

package org.catrobat.catroid.ui.listener

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.Tab
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.SpriteActivity.FRAGMENT_SCRIPTS
import org.catrobat.catroid.ui.recyclerview.fragment.LookListFragment
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment
import org.catrobat.catroid.ui.recyclerview.fragment.SoundListFragment
import kotlin.reflect.KFunction1

@SuppressWarnings("EmptyFunctionBlock")
class SpriteActivityOnTabSelectedListener(val loadFragment: KFunction1<Int, Unit>) :
    OnTabSelectedListener {
    override fun onTabReselected(tab: Tab?) {}

    override fun onTabUnselected(tab: Tab?) {}

    override fun onTabSelected(tab: Tab?) {
        loadFragment(tab?.position ?: FRAGMENT_SCRIPTS)
    }
}

fun Activity?.removeTabLayout() {
    if (this is SpriteActivity) {
        val tabLayout = findViewById<View>(R.id.tab_layout)
        val viewGroup = findViewById<ViewGroup>(R.id.activity_sprite)
        viewGroup.removeView(tabLayout)
    }
}

fun Activity?.addTabLayout() {
    if (this is SpriteActivity) {
        val tabLayoutView = layoutInflater.inflate(R.layout.layout_tabs_sprite_activity, null)
        val gv = findViewById<ViewGroup?>(R.id.activity_sprite)
        gv?.addView(tabLayoutView, 1)
        val tabLayout = findViewById<TabLayout?>(R.id.tab_layout)
        tabLayout?.addOnTabSelectedListener(SpriteActivityOnTabSelectedListener(this::loadFragment))
    }
}

fun SpriteActivity.loadFragment(fragmentPosition: Int) {
    val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
    when (fragmentPosition) {
        FRAGMENT_SCRIPTS -> fragmentTransaction.replace(R.id.fragment_container, ScriptFragment(), ScriptFragment.TAG)
        SpriteActivity.FRAGMENT_LOOKS -> fragmentTransaction.replace(R.id.fragment_container, LookListFragment(), LookListFragment.TAG)
        SpriteActivity.FRAGMENT_SOUNDS -> fragmentTransaction.replace(R.id.fragment_container, SoundListFragment(), SoundListFragment.TAG)
        else -> throw IllegalArgumentException("Invalid fragmentPosition in Activity.")
    }
    fragmentTransaction.commit()
}
