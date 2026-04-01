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
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasSibling
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.GroupSprite
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.testsuites.annotations.Cat
import org.catrobat.catroid.testsuites.annotations.Level
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject

@Category(Cat.AppUi::class, Level.Smoke::class)
@RunWith(AndroidJUnit4::class)
class GroupSpriteOptionsTest {
    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        ProjectActivity::class.java, ProjectActivity.EXTRA_FRAGMENT_POSITION,
        ProjectActivity.FRAGMENT_SPRITES
    )

    private val groupSprite = "groupSprite"
    private val projectManager by inject(ProjectManager::class.java)

    @Before
    fun setUp() {
        createProject(RenameSpriteTest::class.java.simpleName)
        baseActivityTestRule.launchActivity()
    }

    @After
    fun tearDown() {
        TestUtils.deleteProjects(RenameSpriteTest::class.java.simpleName)
    }

    @Test
    fun testActionMenuButtonExists() {
        onView(
            allOf(
                withId(R.id.settings_button),
                isDescendantOfA(withId(R.id.view_holder_sprite_group)),
                hasSibling(withText(groupSprite))
            )
        ).check(matches(isDisplayed()))
            .check(matches(isClickable()))
    }

    @Test
    fun testGroupSpriteActionMenuOptionList() {
        onView(
            allOf(
                withId(R.id.settings_button),
                isDescendantOfA(withId(R.id.view_holder_sprite_group)),
                hasSibling(withText(groupSprite))
            )
        ).perform(click())

        onView(withText(R.string.delete))
            .check(matches(isDisplayed()))

        onView(withText(R.string.rename))
            .check(matches(isDisplayed()))

        onView(withText(R.string.backpack))
            .check(doesNotExist())

        onView(withText(R.string.copy))
            .check(doesNotExist())

        onView(withText(R.string.show_details))
            .check(doesNotExist())

        onView(withText(R.string.new_group))
            .check(doesNotExist())

        onView(withText(R.string.new_scene))
            .check(doesNotExist())

        onView(withText(R.string.from_library))
            .check(doesNotExist())

        onView(withText(R.string.project_options))
            .check(doesNotExist())
    }

    @Test
    fun renameDialogDisplayedTest() {
        UiTestUtils.openSpriteActionMenu(groupSprite, isGroupSprite = true)
        onView(withText(R.string.rename))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withText(R.string.rename_sprite_dialog)).inRoot(RootMatchers.isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun deleteGroupSpriteTest() {
        UiTestUtils.openSpriteActionMenu(groupSprite, isGroupSprite = true)
        onView(withText(R.string.delete))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withText(groupSprite))
            .check(doesNotExist())
    }

    private fun createProject(projectName: String) {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        project.defaultScene.addSprite(GroupSprite(groupSprite))
        projectManager.currentProject = project
        projectManager.currentlyEditedScene = project.defaultScene
    }
}
