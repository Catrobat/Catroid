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
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.WhenTouchDownScript
import org.catrobat.catroid.content.bricks.FadeParticleEffectBrick
import org.catrobat.catroid.content.bricks.FadeParticleEffectBrick.Companion.FADE_IN
import org.catrobat.catroid.content.bricks.SceneStartBrick
import org.catrobat.catroid.content.bricks.SceneTransitionBrick
import org.catrobat.catroid.content.bricks.WaitBrick
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.actions.CustomActions.wait
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.core.StringContains
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject

class ParticleEffectsTest {

    val projectManager: ProjectManager by inject(ProjectManager::class.java)

    companion object {
        private const val PROJECT_NAME = "particleTestProject"
    }

    lateinit var sprite: Sprite
    lateinit var script: Script
    lateinit var scene2: Scene

    @get:Rule
    val baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    fun setUp() {
        createProject()
        baseActivityTestRule.launchActivity(Intent())
    }

    @Test
    fun particleEffectPauseTest() {
        script.addBrick(FadeParticleEffectBrick(FADE_IN))
        onView(ViewMatchers.withId(R.id.button_play)).perform(click())
        pressBack()
        assertTrue(projectManager.currentSprite.look.isParticleEffectPaused)
    }

    @Test
    fun particleEffectResumeTest() {
        script.addBrick(FadeParticleEffectBrick(FADE_IN))
        onView(ViewMatchers.withId(R.id.button_play)).perform(click())
        pressBack()
        onView(ViewMatchers.withId(R.id.stage_dialog_button_continue)).perform(click())
        assertFalse(projectManager.currentSprite.look.isParticleEffectPaused)
    }

    @Test
    fun particleEffectAfterSceneRestartsTest() {
        script.apply {
            addBrick(WaitBrick(1000))
            addBrick(FadeParticleEffectBrick(FADE_IN))
        }
        onView(ViewMatchers.withId(R.id.button_play)).perform(click())
        onView(isRoot()).perform(wait(100))
        assertFalse(projectManager.currentSprite.look.hasParticleEffect)
        onView(isRoot()).perform(click())
        onView(isRoot()).perform(wait(1000))
        assertTrue(projectManager.currentSprite.look.hasParticleEffect)
    }

    @Test
    fun particleEffectAfterSceneContinuesTest() {
        script.apply {
            addBrick(WaitBrick(200))
            addBrick(FadeParticleEffectBrick(FADE_IN))
            addBrick(SceneStartBrick(scene2.name))
        }
        projectManager.currentlyEditedScene = scene2
        onView(ViewMatchers.withId(R.id.button_play)).perform(click())
        onView(withText(StringContains.containsString(scene2.name))).perform(click())
        onView(withText(R.string.play)).perform(click())
        onView(isRoot()).perform(wait(500))
        assertTrue(projectManager.currentSprite.look.hasParticleEffect)
    }

    private fun createProject() {
        val project = UiTestUtils.createDefaultTestProject(PROJECT_NAME)
        script = UiTestUtils.getDefaultTestScript(project)
        sprite = UiTestUtils.getDefaultTestSprite(project)

        val touchDownScript = WhenTouchDownScript()
        touchDownScript.addBrick(SceneStartBrick("scene2"))
        sprite.addScript(touchDownScript)

        scene2 = Scene("scene2", project)
        val sprite2 = Sprite("testSprite2")
        val script2 = StartScript()
        script2.addBrick(SceneTransitionBrick(project.defaultScene.name))
        sprite2.addScript(script2)
        scene2.addSprite(sprite2)
        project.addScene(scene2)
    }
}
