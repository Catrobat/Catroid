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

package org.catrobat.catroid.ui

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.Tab
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.SpriteActivity.FRAGMENT_LOOKS
import org.catrobat.catroid.ui.SpriteActivity.FRAGMENT_SCRIPTS
import org.catrobat.catroid.ui.SpriteActivity.FRAGMENT_SOUNDS
import org.catrobat.catroid.ui.recyclerview.fragment.CatblocksScriptFragment
import org.catrobat.catroid.ui.recyclerview.fragment.CatblocksScriptFragment.Companion.TAG
import org.catrobat.catroid.ui.recyclerview.fragment.LookListFragment
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment
import org.catrobat.catroid.ui.recyclerview.fragment.SoundListFragment
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import kotlin.reflect.KFunction1

@SuppressWarnings("EmptyFunctionBlock")
private class SpriteActivityOnTabSelectedListener(val loadFragment: KFunction1<Int, Unit>) :
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

fun Activity?.addTabLayout(selectedTabPosition: Int) {
    if (this is SpriteActivity) {
        val tabLayoutView = layoutInflater.inflate(R.layout.layout_tabs_sprite_activity, null)
        val gv = findViewById<ViewGroup?>(R.id.activity_sprite)
        gv?.addView(tabLayoutView, 1)
        val tabLayout = findViewById<TabLayout?>(R.id.tab_layout)
        tabLayout?.getTabAt(selectedTabPosition)?.select()
        tabLayout?.addOnTabSelectedListener(SpriteActivityOnTabSelectedListener(this::loadFragment))
    }
}

fun SpriteActivity.loadFragment(fragmentPosition: Int) {
    val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
    if (unableToSelectNewFragmentFromCurrent(currentFragment)) {
        setTabSelection(currentFragment)
        return
    }

    when (fragmentPosition) {
        FRAGMENT_SCRIPTS -> showScripts(fragmentTransaction)
        FRAGMENT_LOOKS -> fragmentTransaction.replace(
            R.id.fragment_container,
            LookListFragment(),
            LookListFragment.TAG
        )
        FRAGMENT_SOUNDS -> fragmentTransaction.replace(
            R.id.fragment_container,
            SoundListFragment(),
            SoundListFragment.TAG
        )
        else -> throw IllegalArgumentException("Invalid fragmentPosition in Activity.")
    }

    fragmentTransaction.commit()
}

private fun SpriteActivity.showScripts(fragmentTransaction: FragmentTransaction) {
    val currentProject = ProjectManager.getInstance().currentProject
    if (!BuildConfig.FEATURE_CATBLOCKS_ENABLED || !SettingsFragment.useCatBlocks(this)) {
        // Classic 1D view
        fragmentTransaction.replace(
            R.id.fragment_container, ScriptFragment(currentProject),
            ScriptFragment.TAG
        )
    } else {
        // start with 2D view
        val currentSprite = ProjectManager.getInstance().currentSprite
        val currentScene = ProjectManager.getInstance().currentlyEditedScene
        fragmentTransaction.replace(
            R.id.fragment_container,
            CatblocksScriptFragment(
                currentProject, currentScene,
                currentSprite, 0
            ),
            TAG
        )
    }
}

fun Fragment?.isFragmentWithTablayout() = this is ScriptFragment || this is LookListFragment || this is SoundListFragment

fun Fragment?.getTabPositionInSpriteActivity(): Int = when (this) {
    is ScriptFragment -> FRAGMENT_SCRIPTS
    is LookListFragment -> FRAGMENT_LOOKS
    is SoundListFragment -> FRAGMENT_SOUNDS
    else -> FRAGMENT_SCRIPTS
}

private fun unableToSelectNewFragmentFromCurrent(fragment: Fragment?) = fragment is ScriptFragment && fragment.isCurrentlyMoving

private fun SpriteActivity?.setTabSelection(fragment: Fragment?) {
    val tabLayout = this?.findViewById<TabLayout?>(R.id.tab_layout)
    tabLayout?.getTabAt(fragment.getTabPositionInSpriteActivity())?.select()
}
