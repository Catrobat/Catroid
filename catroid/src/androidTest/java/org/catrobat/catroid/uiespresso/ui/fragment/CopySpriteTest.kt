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

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.GroupItemSprite
import org.catrobat.catroid.content.GroupSprite
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.koin.java.KoinJavaComponent.inject
import java.io.IOException
import java.util.Arrays

@Category(AppUi::class, Smoke::class)
class CopySpriteTest {
    private val projectManager: ProjectManager by inject(ProjectManager::class.java)

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        ProjectActivity::class.java, ProjectActivity.EXTRA_FRAGMENT_POSITION,
        ProjectActivity.FRAGMENT_SPRITES
    )

    private val spriteList = Arrays.asList("groupSprite", "sprite", "standaloneSprite")
    private var uniqueNameProvider: UniqueNameProvider? = null

    @Before
    @Throws(IOException::class)
    fun setUp() {
        createProject(CopySpriteTest::class.java.simpleName)
        baseActivityTestRule.launchActivity()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        TestUtils.deleteProjects(CopySpriteTest::class.java.simpleName)
    }

    @Test
    fun copySpriteTest() {
        openCopyAction()
        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(3).performCheckItemClick()
        Espresso.onView(withId(R.id.confirm)).perform(ViewActions.click())
        Espresso.onView(
            ViewMatchers.withText(
                uniqueNameProvider!!.getUniqueName(
                    spriteList[1],
                    spriteList
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun selectFragmentToCopyTest() {
        openCopyAction()
        Espresso.onView(withText(R.string.copy)).perform(ViewActions.click())
        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(1).perform(ViewActions.click())
        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(1).performCheckItemCheck()
    }

    @Test
    fun copySpritePositionTest() {
        openCopyAction()
        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(3).performCheckItemClick()
        Espresso.onView(withId(R.id.confirm)).perform(ViewActions.click())
        val name = uniqueNameProvider!!.getUniqueName(
            spriteList[1],
            spriteList
        )
        val lastItem = projectManager.currentProject.defaultScene.spriteList
            .last()
        assert(lastItem.name.equals(name))
    }

    @Test
    fun copyTwoDifferentSpritesTest() {
        openCopyAction()
        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(1).performCheckItemClick()
        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(3).performCheckItemClick()
        Espresso.onView(withId(R.id.confirm)).perform(ViewActions.click())
        Espresso.onView(
            ViewMatchers.withText(
                uniqueNameProvider!!.getUniqueName(
                    spriteList[1],
                    spriteList
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(
            ViewMatchers.withText(
                uniqueNameProvider!!.getUniqueName(
                    spriteList[2],
                    spriteList
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    private fun openCopyAction() {
        Espresso.onView(ViewMatchers.withText(spriteList[0])).perform(ViewActions.click())
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        Espresso.onView(withText(R.string.copy)).perform(ViewActions.click())
    }

    private fun createProject(projectName: String) {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        uniqueNameProvider = UniqueNameProvider()
        val standaloneSprite = Sprite(spriteList[2])
        val groupSprite = GroupSprite(spriteList[0])
        val sprite: Sprite = GroupItemSprite(spriteList[1])
        project.defaultScene.addSprite(standaloneSprite)
        project.defaultScene.addSprite(groupSprite)
        project.defaultScene.addSprite(sprite)
        projectManager.currentProject = project
        projectManager.currentlyEditedScene = project.defaultScene
    }
}
