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

package org.catrobat.catroid.uiespresso.ui.fragment

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.UiTestCatroidApplication.Companion.projectManager
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.FinishStageBrick
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.FinderDataManager
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.uiespresso.ui.fragment.actionutils.ActionUtils
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchTest {

    var projectName = "searchTestProject"

    @Rule
    @JvmField
    var baseActivityTestRule = BaseActivityTestRule(
        ProjectActivity::class.java, false, false
    )

    @Before
    fun setUp() {
        FinderDataManager.instance.clearSearchResults()
        FinderDataManager.instance.clearSearchResultsNames()
        FinderDataManager.instance.setSearchQuery(null)
        FinderDataManager.instance.startingIndexSet = false
        FinderDataManager.instance.currentMatchIndex = -1
        FinderDataManager.instance.setInitiatingFragment(FinderDataManager.FragmentType.NONE)

        createProject(projectName)

        IdlingRegistry.getInstance().register(FinderDataManager.instance.idlingResource)
        baseActivityTestRule.launchActivity(null)
    }

    @After
    fun tearDown() {
        baseActivityTestRule.finishActivity()
        IdlingRegistry.getInstance().unregister(FinderDataManager.instance.idlingResource)
        TestUtils.deleteProjects(projectName)
    }

    @Test
    fun testSearchMixed() {
        val queryString = "test"
        val expectedResults = arrayOf(
            "testsprite2",
            "finish tests",
            "testlook3",
            "testlook4",
            "testsound3",
            "testsound4",
            "testsprite3",
            "testscene2",
            "testsprite5",
            "testscene3"
        )

        Espresso.openActionBarOverflowOrOptionsMenu(
            InstrumentationRegistry.getInstrumentation().targetContext
        )
        Espresso.onView(ViewMatchers.withText(org.catrobat.catroid.R.string.search))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(org.catrobat.catroid.R.id.search_bar))
            .perform(ViewActions.replaceText(queryString))

        Espresso.onView(ViewMatchers.withId(org.catrobat.catroid.R.id.find))
            .perform(ViewActions.click())

        for (i in expectedResults.indices) {
            val currentIndex = FinderDataManager.instance.getSearchResultIndex()
            ViewMatchers.assertThat(
                FinderDataManager.instance.getSearchResultsNames()[currentIndex],
                Matchers.`is`(expectedResults[currentIndex])
            )
            Espresso.onView(ViewMatchers.withId(org.catrobat.catroid.R.id.find_next))
                .perform(ViewActions.click())
        }

        for (i in expectedResults.indices.last downTo 0) {
            val currentIndex = FinderDataManager.instance.getSearchResultIndex()
            ViewMatchers.assertThat(
                FinderDataManager.instance.getSearchResultsNames()[currentIndex],
                Matchers.`is`(expectedResults[currentIndex])
            )
            Espresso.onView(ViewMatchers.withId(org.catrobat.catroid.R.id.find_previous))
                .perform(ViewActions.click())
        }
    }

    private fun createProject(projectName: String?) {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        project.sceneList.clear()

        val scene1Name = "scene1"
        val scene2Name = "testScene2"
        val scene3Name = "testScene3"
        val scene4Name = "scene4"

        val sprite1 = Sprite("background")
        val sprite2 = Sprite("testSprite2")
        val sprite3 = Sprite("testSprite3")
        val sprite4 = Sprite("background")
        val sprite5 = Sprite("testSprite5")

        val script = StartScript()
        script.addBrick(FinishStageBrick())
        sprite2.addScript(script)

        val scene1 = Scene(scene1Name, project)
        val scene2 = Scene(scene2Name, project)
        val scene3 = Scene(scene3Name, project)
        val scene4 = Scene(scene4Name, project)

        project.addScene(scene1)
        project.addScene(scene2)
        project.addScene(scene3)
        project.addScene(scene4)

        XstreamSerializer.getInstance().saveProject(project)

        ProjectManager.getInstance().setCurrentProject(project)
        ProjectManager.getInstance().setCurrentSceneAndSprite(scene1Name, sprite2.name)
        ProjectManager.getInstance().setCurrentlyEditedScene(scene1)
        ProjectManager.getInstance().currentSprite = sprite2

        ActionUtils.addSound(projectManager, "testSound3")
        ActionUtils.addSound(projectManager, "Sound3")
        ActionUtils.addSound(projectManager, "testSound4")
        ActionUtils.addSound(projectManager, "Sound4")

        val l1 = LookData(); l1.setName("testLook3"); sprite2.lookList.add(l1)
        val l2 = LookData(); l2.setName("Look3"); sprite2.lookList.add(l2)
        val l3 = LookData(); l3.setName("testLook4"); sprite2.lookList.add(l3)
        val l4 = LookData(); l4.setName("Look4"); sprite2.lookList.add(l4)

        project.getSceneByName(scene1Name).addSprite(sprite1)
        project.getSceneByName(scene1Name).addSprite(sprite2)
        project.getSceneByName(scene1Name).addSprite(sprite3)
        project.getSceneByName(scene2Name).addSprite(sprite4)
        project.getSceneByName(scene2Name).addSprite(sprite5)

        ProjectManager.getInstance().setCurrentProject(project)
        ProjectManager.getInstance().setCurrentSceneAndSprite(scene1Name, sprite2.name)
        ProjectManager.getInstance().setCurrentlyEditedScene(scene1)
        ProjectManager.getInstance().currentSprite = sprite2

        saveProjectSerial(project, ApplicationProvider.getApplicationContext())
    }
}