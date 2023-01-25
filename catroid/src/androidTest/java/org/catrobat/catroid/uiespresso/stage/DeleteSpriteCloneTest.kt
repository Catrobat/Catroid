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
package org.catrobat.catroid.uiespresso.stage

import androidx.test.InstrumentationRegistry
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.CloneBrick
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import java.io.IOException

@Category(AppUi::class, Smoke::class)
class DeleteSpriteCloneTest {
    private var sprite1: Sprite? = null
    private var sprite2: Sprite? = null
    private var project: Project? = null

    @Rule
    @JvmField
    var baseActivityTestRule = BaseActivityTestRule(
        ProjectActivity::class.java, true, false
    )

    @Before
    @kotlin.jvm.Throws(Exception::class)
    fun setUp() {
        createProject()
        baseActivityTestRule.launchActivity(null)
    }

    @After
    @kotlin.jvm.Throws(IOException::class)
    fun tearDown() {
        TestUtils.deleteProjects(javaClass.simpleName)
    }

    @Test
    fun testDeleteSpriteOfClone() {
        Espresso.onView(withText(sprite1!!.name))
            .perform(click())
        BrickDataInteractionWrapper.onBrickAtPosition(1)
            .onSpinner(R.id.brick_clone_spinner)
            .performSelectNameable(sprite2!!.name)
        Espresso.pressBack()
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        Espresso.onView(withText(R.string.delete))
            .perform(click())
        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(2)
            .performCheckItemClick()
        Espresso.onView(withId(R.id.confirm))
            .perform(click())
        Espresso.onView(allOf(withId(android.R.id.button1), withText(R.string.delete)))
            .perform(click())

        Espresso.onView(withId(R.id.button_play))
            .perform(click())
    }

    private fun createProject() {
        val projectName = javaClass.simpleName
        project = Project(ApplicationProvider.getApplicationContext(), projectName)
        sprite1 = Sprite("Sprite (1)")
        val startScirpt: Script = StartScript()
        startScirpt.addBrick(CloneBrick())
        sprite1!!.addScript(startScirpt)
        sprite2 = Sprite("Sprite (2)")
        val scene1 = project!!.defaultScene
        scene1.addSprite(sprite1)
        scene1.addSprite(sprite2)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().startScene = scene1
    }
}
