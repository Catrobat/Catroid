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

package org.catrobat.catroid.uiespresso.content.brick.app

import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.base.Stopwatch
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.WaitForConditionAction.Companion.waitFor
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.DeleteLookBrick
import org.catrobat.catroid.content.bricks.PaintNewLookBrick
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.util.actions.CustomActions.wait
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject
import java.util.concurrent.TimeUnit.MILLISECONDS

@Category(AppUi::class, Smoke::class)
@RunWith(AndroidJUnit4::class)
class DeleteLookBrickTest {
    private lateinit var sprite: Sprite
    private lateinit var script: StartScript
    private val projectName = "DeleteLookBrickTest"
    private val projectManager by inject(ProjectManager::class.java)

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    fun setUp() {
        val project = Project(getApplicationContext(), projectName)
        sprite = Sprite("testSprite")
        script = StartScript()
        script.addBrick(PaintNewLookBrick())
        sprite.addScript(script)
        project.defaultScene.addSprite(sprite)
        projectManager.currentProject = project
        projectManager.currentSprite = sprite
        projectManager.currentlyEditedScene = project.defaultScene
        Intents.init()
        baseActivityTestRule.launchActivity()
    }

    @After
    fun tearDown() {
        Intents.release()
        baseActivityTestRule.finishActivity()
        TestUtils.deleteProjects(projectName)
    }

    @Test
    fun testPaintLookWithoutDelete() {
        onView(withId(R.id.button_play))
            .perform(waitFor(isDisplayed(), 3000))
            .perform(click())
        waitOnViewAndClick(R.id.pocketpaint_btn_skip)

        onView(withId(R.id.pocketpaint_drawing_surface_view))
            .perform(waitFor(isDisplayed(), 3000))
            .perform(click())
        pressBack()

        onView(isRoot()).perform(wait(500))
        assertEquals(1, sprite.lookList?.size)
    }

    @Test
    fun testPaintAndDeleteLook() {
        script.addBrick(DeleteLookBrick())

        onView(withId(R.id.button_play))
            .perform(waitFor(isDisplayed(), 3000))
            .perform(click())
        waitOnViewAndClick(R.id.pocketpaint_btn_skip)

        onView(withId(R.id.pocketpaint_drawing_surface_view))
            .perform(waitFor(isDisplayed(), 3000))
            .perform(click())
        pressBack()

        onView(isRoot()).perform(wait(500))
        assertEquals(0, sprite.lookList?.size)
    }

    private fun waitOnViewAndClick(viewId: Int, timeout: Int = 1000): Boolean {
        val stopWatch = Stopwatch.createStarted()
        var viewFound: Boolean
        do {
            viewFound = true
            try {
                onView(withId(viewId)).perform(click())
            } catch (_: NoMatchingViewException) {
                viewFound = false
                if (stopWatch.elapsed(MILLISECONDS) >= timeout) {
                    break
                }
            }
        } while (!viewFound)
        return viewFound
    }
}
