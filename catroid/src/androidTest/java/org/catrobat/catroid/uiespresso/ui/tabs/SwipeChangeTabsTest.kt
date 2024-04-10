/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.ui.tabs

import androidx.fragment.app.Fragment
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.SpriteActivity.FRAGMENT_LOOKS
import org.catrobat.catroid.ui.SpriteActivity.FRAGMENT_SCRIPTS
import org.catrobat.catroid.ui.SpriteActivity.FRAGMENT_SOUNDS
import org.catrobat.catroid.ui.recyclerview.fragment.LookListFragment
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment
import org.catrobat.catroid.ui.recyclerview.fragment.SoundListFragment
import org.catrobat.catroid.ui.recyclerview.fragment.TabLayoutContainerFragment
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.createProjectAndGetStartScript
import org.catrobat.catroid.uiespresso.util.actions.CustomActions
import org.catrobat.catroid.uiespresso.util.actions.selectTabAtPosition
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject

@RunWith(AndroidJUnit4::class)
class SwipeChangeTabsTest {

    private val projectManager: ProjectManager by inject(ProjectManager::class.java)

    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java, SpriteActivity.EXTRA_FRAGMENT_POSITION, FRAGMENT_SCRIPTS
    )

    @Before
    fun setUp() {
        val script = createProjectAndGetStartScript("SpriteActivityTabsTest")
        projectManager.currentProject.addUserVariable(UserVariable("X"))
        script.addBrick(SetVariableBrick())
        baseActivityTestRule.launchActivity()
    }

    @Test
    fun testScriptSwipeRight() {
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(FRAGMENT_SCRIPTS))
        assertTrue(assertFragmentIsShown() is ScriptFragment)

        onView(withId(R.id.fragment_container)).perform(swipeRight())
        onView(withId(R.id.tab_layout)).perform(CustomActions.wait(500))

        assertTrue(assertFragmentIsShown() is ScriptFragment)
    }

    @Test
    fun testScriptSwipeLeft() {
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(FRAGMENT_SCRIPTS))
        assertTrue(assertFragmentIsShown() is ScriptFragment)

        onView(withId(R.id.fragment_container)).perform(swipeLeft())
        onView(withId(R.id.tab_layout)).perform(CustomActions.wait(500))

        assertTrue(assertFragmentIsShown() is LookListFragment)
    }

    @Test
    fun testLooksSwipeRight() {
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(FRAGMENT_LOOKS))
        assertTrue(assertFragmentIsShown() is LookListFragment)

        onView(withId(R.id.fragment_container)).perform(swipeRight())
        onView(withId(R.id.tab_layout)).perform(CustomActions.wait(500))

        assertTrue(assertFragmentIsShown() is ScriptFragment)
    }

    @Test
    fun testLooksSwipeLeft() {
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(FRAGMENT_LOOKS))
        assertTrue(assertFragmentIsShown() is LookListFragment)

        onView(withId(R.id.fragment_container)).perform(swipeLeft())
        onView(withId(R.id.tab_layout)).perform(CustomActions.wait(500))

        assertTrue(assertFragmentIsShown() is SoundListFragment)
    }

    @Test
    fun testSoundSwipeRight() {
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(FRAGMENT_SOUNDS))
        onView(withId(R.id.tab_layout)).perform(CustomActions.wait(250))
        assertTrue(assertFragmentIsShown() is SoundListFragment)

        onView(withId(R.id.fragment_container)).perform(swipeRight())
        onView(withId(R.id.tab_layout)).perform(CustomActions.wait(500))

        assertTrue(assertFragmentIsShown() is LookListFragment)
    }

    @Test
    fun testSoundSwipeLeft() {
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(FRAGMENT_SOUNDS))
        onView(withId(R.id.tab_layout)).perform(CustomActions.wait(250))
        assertTrue(assertFragmentIsShown() is SoundListFragment)

        onView(withId(R.id.fragment_container)).perform(swipeLeft())
        onView(withId(R.id.tab_layout)).perform(CustomActions.wait(500))

        assertTrue(assertFragmentIsShown() is SoundListFragment)
    }

    private fun assertFragmentIsShown(): Fragment {
        Espresso.onIdle()
        val fragment =
            (baseActivityTestRule.activity.supportFragmentManager.findFragmentById(R.id.fragment_container)
                as TabLayoutContainerFragment?)?.getSelectedTabFragment()
        assertNotNull(fragment)
        assertTrue(fragment!!.isVisible)
        return fragment
    }
}