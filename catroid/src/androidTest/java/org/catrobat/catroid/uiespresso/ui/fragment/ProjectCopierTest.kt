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
package org.catrobat.catroid.uiespresso.ui.fragment

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.BrickValues
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.io.ResourceImporter
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.ProjectListActivity
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ProjectCopierTest {
    @get:Rule
    var baseActivityTestRule =
        BaseActivityTestRule(
            ProjectListActivity::class.java, true, false
        )
    private val toBeCopiedProjectName = "testProject"

    @Before
    @Throws(Exception::class)
    fun setUp() {
        createProject(toBeCopiedProjectName)
        baseActivityTestRule.launchActivity(null)
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun copyProjectTest() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(ViewMatchers.withText(R.string.copy))
            .perform(ViewActions.click())
        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(0)
            .performCheckItemClick()
        onView(ViewMatchers.withId(R.id.confirm))
            .perform(ViewActions.click())
        onView(ViewMatchers.withText(toBeCopiedProjectName))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText("$toBeCopiedProjectName (1)"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun copyCopiedProjectsTest() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(ViewMatchers.withText(R.string.copy))
            .perform(ViewActions.click())
        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(0)
            .performCheckItemClick()
        onView(ViewMatchers.withId(R.id.confirm))
            .perform(ViewActions.click())
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(ViewMatchers.withText(R.string.copy))
            .perform(ViewActions.click())
        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(0)
            .performCheckItemClick()
        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(1)
            .performCheckItemClick()
        onView(ViewMatchers.withId(R.id.confirm))
            .perform(ViewActions.click())
        onView(ViewMatchers.withText(toBeCopiedProjectName))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText("$toBeCopiedProjectName (1)"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText("$toBeCopiedProjectName (2)"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText("$toBeCopiedProjectName (3)"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Throws(IOException::class)
    private fun createProject(projectName: String) {
        val project = Project(getApplicationContext(), projectName)
        val sprite = Sprite("firstSprite")
        val script: Script = StartScript()
        script.addBrick(SetXBrick(Formula(BrickValues.X_POSITION)))
        sprite.addScript(script)
        project.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite
        XstreamSerializer.getInstance().saveProject(project)
        val soundFile = ResourceImporter.createSoundFileFromResourcesInDirectory(
            getInstrumentation().context.resources,
            org.catrobat.catroid.test.R.raw.longsound,
            File(project.defaultScene.directory, Constants.SOUND_DIRECTORY_NAME),
            "longsound.mp3"
        )
        val soundInfoList = sprite.soundList
        val soundInfo = SoundInfo()
        soundInfo.file = soundFile
        soundInfo.name = "testSound1"
        soundInfoList.add(soundInfo)
        val imageFile = ResourceImporter.createImageFileFromResourcesInDirectory(
            getInstrumentation().context.resources,
            org.catrobat.catroid.test.R.drawable.catroid_banzai,
            File(project.defaultScene.directory, Constants.IMAGE_DIRECTORY_NAME),
            "catroid_sunglasses.png",
            1.0
        )
        val lookDataList = sprite.lookList
        val lookData = LookData()
        lookData.file = imageFile
        lookData.name = "testLook1"
        lookDataList.add(lookData)
        val secondScene = Scene("secondScene", project)
        val backgroundSprite =
            Sprite(getApplicationContext<Context>().getString(R.string.background))
        backgroundSprite.look.zIndex = Constants.Z_INDEX_BACKGROUND
        secondScene.addSprite(backgroundSprite)
        project.addScene(secondScene)
        saveProjectSerial(project, getApplicationContext())
    }
}
