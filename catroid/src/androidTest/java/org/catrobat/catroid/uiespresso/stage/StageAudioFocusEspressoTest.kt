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
package org.catrobat.catroid.uiespresso.stage

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.bricks.PlaySoundBrick
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.createDefaultTestProject
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StageAudioFocusEspressoTest {
    private lateinit var project: Project

    @get:Rule
    var baseActivityTestRule: BaseActivityTestRule<StageActivity?> =
        BaseActivityTestRule(StageActivity::class.java, true, false)

    @Before
    fun setUp() {
        project = createDefaultTestProject(PROJECT_NAME)
    }

    @Test
    @SdkSuppress(minSdkVersion = 35)
    fun testRequestAndReleaseAudioFocusWithActivityInForeground() {
        val startScript = UiTestUtils.getDefaultTestScript(project)
        startScript.addBrick(PlaySoundBrick())
        baseActivityTestRule.launchActivity(null)

        assertTrue(baseActivityTestRule.getActivity()?.hasAudioFocus() ?: false)

        pressBack()
        assertFalse(baseActivityTestRule.getActivity()?.hasAudioFocus() ?: true)

        onView(ViewMatchers.withId(R.id.stage_dialog_button_continue))
            .perform(click())
        assertTrue(baseActivityTestRule.getActivity()?.hasAudioFocus() ?: false)
    }

    @Test
    @SdkSuppress(minSdkVersion = 35)
    fun testOnlyRequestAudioFocusWithSoundBrick() {
        baseActivityTestRule.launchActivity(null)

        assertFalse(baseActivityTestRule.getActivity()?.hasAudioFocus() ?: true)
    }

    companion object {
        private const val PROJECT_NAME = "dummyProject"
    }
}
