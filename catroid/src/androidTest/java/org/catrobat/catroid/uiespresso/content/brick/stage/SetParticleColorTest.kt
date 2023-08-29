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

package org.catrobat.catroid.uiespresso.content.brick.stage

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.FadeParticleEffectBrick
import org.catrobat.catroid.content.bricks.SetParticleColorBrick
import org.catrobat.catroid.content.bricks.WaitBrick
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.actions.CustomActions.wait
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject

class SetParticleColorTest {

    val projectManager: ProjectManager by inject(ProjectManager::class.java)

    companion object {
        private const val PROJECT_NAME = "particleTestProject"
    }

    lateinit var sprite: Sprite
    lateinit var script: Script

    private val blueArray = FloatArray(3) { i -> if (i == 2) 1f else 0f }
    private val greenArray = FloatArray(3) { i -> if (i == 1) 1f else 0f }
    private val blueString = "#0000ff"
    private val greenString = "#00ff00"
    private fun particleColor() =
        projectManager.currentSprite.look.particleEffect.emitters.first().tint.colors!!

    @get:Rule
    val baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    fun setUp() {
        script = UiTestUtils.createProjectAndGetStartScript(PROJECT_NAME)
        script.addBrick(SetParticleColorBrick(blueString))
        script.addBrick(FadeParticleEffectBrick())
        script.addBrick(WaitBrick(1000))
        script.addBrick(SetParticleColorBrick(greenString))
        baseActivityTestRule.launchActivity(Intent())
    }

    @Test
    fun setParticleColorBeforeFadeTest() {
        onView(ViewMatchers.withId(R.id.button_play)).perform(click())
        onView(isRoot()).perform(wait(200))
        assertTrue(particleColor().contentEquals(blueArray))
        onView(isRoot()).perform(wait(1000))
        assertTrue(particleColor().contentEquals(greenArray))
    }
}
