/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
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

package org.catrobat.catroid.test.merge

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.merge.ImportLocalObjectActivity
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject

class ImportLocalObjectActivityTest {
    private lateinit var baseProject: Project
    private lateinit var testProject: Project

    private val projectManager = inject(ProjectManager::class.java).value

    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        ImportLocalObjectActivity::class.java, true, false
    )

    @Before
    fun setUp() {
        baseProject = UiTestUtils.getDefaultTestProject(ApplicationProvider.getApplicationContext())
        testProject =
            UiTestUtils.getMultipleScenesAndSpriteProject(ApplicationProvider.getApplicationContext())
        projectManager.currentProject = baseProject
        projectManager.currentlyEditedScene = baseProject.defaultScene
    }

    @After
    fun tearDown() {
        TestUtils.deleteProjects(baseProject.name)
        TestUtils.deleteProjects(testProject.name)
    }

    @Test
    fun testImportSpriteView() {
        ImportLocalObjectActivity.projectToImportFrom = testProject
        ImportLocalObjectActivity.sceneToImportFrom = testProject.defaultScene
        setUpActivity(ImportLocalObjectActivity.REQUEST_SPRITE)

        onView(withText(R.string.import_objects))
            .check(matches(isDisplayed()))
        onView(withId(R.id.bottom_bar))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        onView(withText(baseActivityTestRule.activity.getString(R.string.fragment_sprite_text_description)))
            .check(doesNotExist())

        testProject.defaultScene.spriteList.forEach { sprite ->
            onView(withText(sprite.name)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun testImportFromSceneView() {
        ImportLocalObjectActivity.projectToImportFrom = testProject
        setUpActivity(ImportLocalObjectActivity.REQUEST_SCENE)

        onView(withText(R.string.import_from_scene)).check(matches(isDisplayed()))
        onView(withId(R.id.bottom_bar))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        testProject.sceneList.forEach { scene ->
            onView(withText(scene.name)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun testImportFromProjectView() {
        setUpActivity(ImportLocalObjectActivity.REQUEST_PROJECT)

        onView(withText(R.string.import_from_project)).check(matches(isDisplayed()))
        onView(withId(R.id.bottom_bar)).check(matches(withEffectiveVisibility(Visibility.GONE)))

        onView(withText(testProject.name)).check(matches(isDisplayed()))
        onView(withText(baseProject.name)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateBackFromScene() {
        ImportLocalObjectActivity.projectToImportFrom = baseProject
        setUpActivity(ImportLocalObjectActivity.REQUEST_SCENE)
        onView(withText(R.string.import_from_scene)).check(matches(isDisplayed()))
        pressBack()
        onView(withText(R.string.import_from_project)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateBackFromSpriteToSingleSceneProject() {
        ImportLocalObjectActivity.projectToImportFrom = baseProject
        ImportLocalObjectActivity.sceneToImportFrom = testProject.defaultScene
        setUpActivity(ImportLocalObjectActivity.REQUEST_SPRITE)
        onView(withText(R.string.import_objects)).check(matches(isDisplayed()))
        pressBack()
        onView(withText(R.string.import_from_project)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateBackFromSpriteToSelectScene() {
        ImportLocalObjectActivity.projectToImportFrom = testProject
        ImportLocalObjectActivity.sceneToImportFrom = testProject.defaultScene
        setUpActivity(ImportLocalObjectActivity.REQUEST_SPRITE)
        onView(withText(R.string.import_objects)).check(matches(isDisplayed()))
        pressBack()
        onView(withText(R.string.import_from_scene)).check(matches(isDisplayed()))
    }

    private fun setUpActivity(request: String) {
        val intent = Intent()
        intent.putExtra(ImportLocalObjectActivity.TAG, request)
        baseActivityTestRule.launchActivity(intent)
    }
}
