/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.WhenBackgroundChangesScript
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject

@RunWith(AndroidJUnit4::class)
class WhenBackgroundChangesToBrickTest {
    private var brickPosition = 0
    private val projectManager by inject(ProjectManager::class.java)

    @JvmField
    @Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    fun setUp() {
        val project = Project(
            ApplicationProvider.getApplicationContext(),
            "WhenBackgroundChangesToBrickTest"
        )

        val sprite = Sprite("testSprite")
        sprite.addScript(WhenBackgroundChangesScript())
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
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun testCreateNewBackground() {
        onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_when_background)
        onBrickAtPosition(brickPosition).onSpinner(R.id.brick_when_background_spinner)
            .performSelectNameable(R.string.new_option)
        onView(withId(R.id.dialog_new_look_paintroid)).perform(click())
        onView(withId(R.id.pocketpaint_drawing_surface_view)).perform(click())
        pressBack()
        TestUtils.waitForBackgroundLookData(1000,10)
    }
}
