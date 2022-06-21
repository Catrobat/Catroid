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

package org.catrobat.catroid.test.merge

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.DefaultProjectHandler
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.merge.ImportLocalObjectActivity
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.recyclerview.controller.SceneController
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject
import java.io.File

class ImportLocalObjectActivityTest {
    private lateinit var baseProject: Project
    private lateinit var multipleScenesWithMultipleSpritesProject: Project

    private val projectManager = inject(ProjectManager::class.java).value
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        ImportLocalObjectActivity::class.java, true, false
    )

    @Before
    fun setUp() {
        initProjects()
        projectManager.currentProject = baseProject
        projectManager.currentlyEditedScene = baseProject.defaultScene
        baseActivityTestRule.launchActivity(null)
        ImportLocalObjectActivity.projectToImportFrom = multipleScenesWithMultipleSpritesProject
        ImportLocalObjectActivity.sceneToImportFrom = multipleScenesWithMultipleSpritesProject.defaultScene
        ImportLocalObjectActivity.spriteToImport = multipleScenesWithMultipleSpritesProject.defaultScene.backgroundSprite
    }

    private fun initProjects() {
        baseProject = DefaultProjectHandler.createAndSaveDefaultProject("base", context, false)
        multipleScenesWithMultipleSpritesProject = DefaultProjectHandler.createAndSaveDefaultProject(context)

        val scene2 = SceneController.newSceneWithBackgroundSprite(
            "scene2",
            "background1",
            multipleScenesWithMultipleSpritesProject
        )
        multipleScenesWithMultipleSpritesProject.addScene(scene2)
        multipleScenesWithMultipleSpritesProject.getSceneByName(scene2.name)?.spriteList?.add(
            baseProject.defaultScene?.getSprite(context.getString(R.string.default_project_cloud_sprite_name_1))
        )

        val scene3 = SceneController.newSceneWithBackgroundSprite(
            "scene3",
            "background1",
            multipleScenesWithMultipleSpritesProject
        )

        multipleScenesWithMultipleSpritesProject.addScene(scene3)
        multipleScenesWithMultipleSpritesProject.getSceneByName(scene3.name)?.spriteList?.add(
            baseProject.defaultScene?.getSprite(
                context.getString(R.string.default_project_cloud_sprite_name_2))
        )

        val scene4 = SceneController.newSceneWithBackgroundSprite(
            "scene4",
            "background1",
            multipleScenesWithMultipleSpritesProject
        )

        multipleScenesWithMultipleSpritesProject.addScene(scene4)
        multipleScenesWithMultipleSpritesProject.getSceneByName(scene4.name)?.spriteList?.add(
            baseProject.defaultScene?.getSprite(
                context.getString(R.string.default_project_sprites_animal_name))
        )

        XstreamSerializer.getInstance().saveProject(baseProject)
        XstreamSerializer.getInstance().saveProject(multipleScenesWithMultipleSpritesProject)
        projectManager.loadProject(
            File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, "base")
        )
    }

    @After
    fun tearDown() {
        TestUtils.deleteProjects(baseProject.name)
        TestUtils.deleteProjects(multipleScenesWithMultipleSpritesProject.name)
    }

    @Test
    fun testImportSpriteView() {
        setUpActivity(ImportLocalObjectActivity.REQUEST_SPRITE)

        Espresso.onView(ViewMatchers.withText(R.string.import_object))
        Espresso.onView(ViewMatchers.withId(R.id.bottom_bar)).check(ViewAssertions.matches
            (ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))

        Espresso.onView(ViewMatchers.withText(baseActivityTestRule.activity.applicationContext
                .getString(R.string.fragment_sprite_text_description))).check(ViewAssertions.doesNotExist())

        multipleScenesWithMultipleSpritesProject.defaultScene.spriteList.forEach { sprite ->
            Espresso.onView(ViewMatchers.withText(sprite.name)).check(
                ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }

    @Test
    fun testImportFromSceneView() {
        setUpActivity(ImportLocalObjectActivity.REQUEST_SCENE)

        Espresso.onView(ViewMatchers.withText(R.string.import_from_scene))
        Espresso.onView(ViewMatchers.withId(R.id.bottom_bar)).check(ViewAssertions.matches
            (ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))

        multipleScenesWithMultipleSpritesProject.sceneList.forEach { scene ->
            Espresso.onView(ViewMatchers.withText(scene.name)).check(
                ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }

    @Test
    fun testImportFromProjectView() {
        setUpActivity(ImportLocalObjectActivity.REQUEST_PROJECT)

        Espresso.onView(ViewMatchers.withText(R.string.import_from_project))
        Espresso.onView(ViewMatchers.withId(R.id.bottom_bar)).check(ViewAssertions.matches
            (ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))

        Espresso.onView(ViewMatchers.withText(multipleScenesWithMultipleSpritesProject.name)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText(baseProject.name)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    private fun setUpActivity(request: String) {
        val intent = Intent()
        intent.putExtra(ImportLocalObjectActivity.TAG, request)
        baseActivityTestRule.launchActivity(intent)
    }
}
