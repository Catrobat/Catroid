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
package org.catrobat.catroid.uiespresso.ui.regression.activitydestroy

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry

import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.ResourceImporter
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.rules.FlakyTestRule
import org.catrobat.catroid.runner.Flaky
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Cat.Quarantine
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject

import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class LookFragmentActivityRecreateRegressionTest {
    @Rule
    @JvmField
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java, SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_LOOKS
    )

    @Rule
    @JvmField
    var flakyTestRule = FlakyTestRule()

    private val lookName = "testLook"

    @Before
    @Throws(Exception::class)
    fun setUp() {
        createProject()
        baseActivityTestRule.launchActivity()
    }

    @Category(AppUi::class, Smoke::class, Quarantine::class)
    @Flaky
    @Test
    fun testActivityRecreateRenameLookDialog() {
        openActionBarOverflowOrOptionsMenu(
            InstrumentationRegistry.getInstrumentation().targetContext)
        onView(ViewMatchers.withText(R.string.rename)).perform(ViewActions.click())
        onView(ViewMatchers.withText(R.string.rename_look_dialog))
            .inRoot(RootMatchers.isDialog())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        InstrumentationRegistry.getInstrumentation()
            .runOnMainSync { baseActivityTestRule.activity.recreate() }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    @Category(AppUi::class, Smoke::class, Quarantine::class)
    @Flaky
    @Test
    fun testActivityRecreateNewLookDialog() {
        RecyclerViewInteractionWrapper
            .onRecyclerView().atPosition(0).onChildView(R.id.title_view)
            .check(ViewAssertions.matches(ViewMatchers.withText(lookName)))
        onView(ViewMatchers.withId(R.id.button_add))
            .perform(ViewActions.click())
        onView(ViewMatchers.withText(R.string.new_look_dialog_title))
            .inRoot(RootMatchers.isDialog())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        InstrumentationRegistry.getInstrumentation()
            .runOnMainSync { baseActivityTestRule.activity.recreate() }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    @Throws(IOException::class)
    private fun createProject() {
        val projectName = "copyLookFragmentTest"
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        val sprite = Sprite("testSprite")
        project.defaultScene.addSprite(sprite)
        val projectManager: ProjectManager by inject(ProjectManager::class.java)
        projectManager.currentProject = project
        projectManager.currentSprite = sprite
        XstreamSerializer.getInstance().saveProject(project)
        val imageFile = ResourceImporter.createImageFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            org.catrobat.catroid.test.R.drawable.catroid_banzai,
            File(project.defaultScene.directory, Constants.IMAGE_DIRECTORY_NAME),
            "catroid_sunglasses.png", 1.0
        )
        val lookDataList = projectManager.currentSprite.lookList
        val lookData = LookData(lookName, imageFile)
        lookDataList.add(lookData)
    }
}
