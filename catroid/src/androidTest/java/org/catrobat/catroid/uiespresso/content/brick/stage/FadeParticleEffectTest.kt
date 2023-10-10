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
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.WhenClonedScript
import org.catrobat.catroid.content.bricks.CloneBrick
import org.catrobat.catroid.content.bricks.DeleteThisCloneBrick
import org.catrobat.catroid.content.bricks.FadeParticleEffectBrick
import org.catrobat.catroid.content.bricks.FadeParticleEffectBrick.Companion.FADE_IN
import org.catrobat.catroid.content.bricks.FadeParticleEffectBrick.Companion.FADE_OUT
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.content.bricks.SetGravityBrick
import org.catrobat.catroid.content.bricks.WaitBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.actions.CustomActions.wait
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject

class FadeParticleEffectTest {

    val projectManager: ProjectManager by inject(ProjectManager::class.java)

    companion object {
        private const val PROJECT_NAME = "particleTestProject"
    }

    lateinit var sprite: Sprite
    lateinit var script: Script
    lateinit var project: Project

    @get:Rule
    val baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    fun setUp() {
        project = UiTestUtils.createDefaultTestProject(PROJECT_NAME)
        script = UiTestUtils.getDefaultTestScript(project)
        sprite = UiTestUtils.getDefaultTestSprite(project)
        baseActivityTestRule.launchActivity(Intent())
    }

    @Test
    fun fadeParticleEffectTest() {
        script.addBrick(WaitBrick(1000))
        script.addBrick(FadeParticleEffectBrick(FADE_IN))
        script.addBrick(WaitBrick(1000))
        script.addBrick(FadeParticleEffectBrick(FADE_OUT))
        onView(withId(R.id.button_play)).perform(click())

        onView(isRoot()).perform(wait(200))
        assertFalse(sprite.look.hasParticleEffect)

        onView(isRoot()).perform(wait(1000))
        assertTrue(sprite.look.hasParticleEffect)

        onView(isRoot()).perform(wait(1000))
        assertFalse(sprite.look.hasParticleEffect)
    }

    @Test
    fun particleEffectGravityTest() {
        val gravityBefore = 100.0f
        val gravityAfter = -100.0f

        script.addBrick(FadeParticleEffectBrick(FADE_IN))
        script.addBrick(WaitBrick(1000))
        script.addBrick(SetGravityBrick(Formula(0), Formula(gravityBefore)))
        script.addBrick(WaitBrick(1000))
        script.addBrick(SetGravityBrick(Formula(0), Formula(gravityAfter)))
        onView(withId(R.id.button_play)).perform(click())

        onView(isRoot()).perform(wait(200))
        assertEquals(sprite.look.particleEffect.emitters.first().gravity.highMax, -10f)
        assertEquals(sprite.look.particleEffect.emitters.first().gravity.highMin, -10f)

        onView(isRoot()).perform(wait(1000))
        assertEquals(sprite.look.particleEffect.emitters.first().gravity.highMax, gravityBefore)
        assertEquals(sprite.look.particleEffect.emitters.first().gravity.highMin, gravityBefore)

        onView(isRoot()).perform(wait(1000))
        assertEquals(sprite.look.particleEffect.emitters.first().gravity.highMax, gravityAfter)
        assertEquals(sprite.look.particleEffect.emitters.first().gravity.highMin, gravityAfter)
    }

    @Test
    fun fadeParticleEffectOnCloneTest() {
        script.addBrick(PlaceAtBrick(-100, 0))
        script.addBrick(FadeParticleEffectBrick(FADE_IN))
        script.addBrick(CloneBrick())
        script.addBrick(WaitBrick(1000))
        script.addBrick(FadeParticleEffectBrick(FADE_OUT))
        script.addBrick(CloneBrick())

        val cloneScript = WhenClonedScript()
        sprite.addScript(cloneScript)
        cloneScript.addBrick(WaitBrick(900))
        cloneScript.addBrick(DeleteThisCloneBrick())

        onView(withId(R.id.button_play)).perform(click())

        onView(isRoot()).perform(wait(200))
        assertTrue(sprite.look.hasParticleEffect)
        for (sprite in project.spriteListWithClones) {
            if (sprite.isClone) {
                assertTrue(sprite.look.hasParticleEffect)
            }
        }

        onView(isRoot()).perform(wait(1000))
        for (sprite in project.spriteListWithClones) {
            if (sprite.isClone) {
                assertFalse(sprite.look.hasParticleEffect)
            }
        }
    }
}
