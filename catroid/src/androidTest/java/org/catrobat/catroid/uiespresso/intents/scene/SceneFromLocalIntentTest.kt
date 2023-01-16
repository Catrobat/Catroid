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

package org.catrobat.catroid.uiespresso.intents.scene

import android.Manifest
import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.GrantPermissionRule
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.merge.ImportLocalObjectActivity
import org.catrobat.catroid.test.merge.MergeTestUtils
import org.catrobat.catroid.testsuites.annotations.Cat
import org.catrobat.catroid.testsuites.annotations.Level
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.recyclerview.controller.SceneController.newSceneWithBackgroundSprite
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.matchers.IndexMatchers
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.koin.java.KoinJavaComponent
import java.io.File

class SceneFromLocalIntentTest {
    private lateinit var project: Project
    private lateinit var localProject: Project
    private var expectedIntent: Matcher<Intent>? = null
    private var projectManager = KoinJavaComponent.inject(ProjectManager::class.java).value

    private val projectName = javaClass.simpleName
    private val tmpPath = File(
        Constants.CACHE_DIRECTORY.absolutePath, "Pocket Code Test Temp"
    )

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        ProjectActivity::class.java,
        ProjectActivity.EXTRA_FRAGMENT_POSITION,
        ProjectActivity.FRAGMENT_SCENES
    )

    @get:Rule
    var runtimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @Before
    fun setUp() {
        createProjects(projectName)
        baseActivityTestRule.launchActivity()
        Intents.init()

        expectedIntent = allOf(IntentMatchers.hasExtra(Constants.EXTRA_IMPORT_REQUEST_CODE,
                ImportLocalObjectActivity.REQUEST_SCENE)
        )

        if (!tmpPath.exists()) {
            tmpPath.mkdirs()
        }

        val resultData = Intent()
        resultData.putExtra(Constants.EXTRA_PROJECT_PATH, localProject.directory.absoluteFile)
        resultData.putExtra(Constants.EXTRA_SCENE_NAME, localProject.defaultScene.name)
        resultData.putExtra(Constants.EXTRA_SPRITE_NAMES,
            localProject.defaultScene.spriteList.map { it.name } as ArrayList<String>
        )

        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        Intents.intending(expectedIntent).respondWith(result)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        Intents.release()
        baseActivityTestRule.finishActivity()
        StorageOperations.deleteDir(tmpPath)
        StorageOperations.deleteDir(File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, projectName))
    }

    @Category(Cat.AppUi::class, Level.Smoke::class)
    @Test
    fun testMergeSceneWithEmptyScene() {
        val (originalMerges, importSprites) = getSpritesForMerge(project.sceneList[1])
        onView(IndexMatchers().withIndex(withId(R.id.settings_button), 1)).perform(click())
        onView(withText(R.string.from_local)).perform(click())
        Intents.intended(expectedIntent)
        MergeTestUtils().assertSceneMerge(
            importSprites, originalMerges, project, project.sceneList[1], localProject
        )
    }

    @Category(Cat.AppUi::class, Level.Smoke::class)
    @Test
    fun testMergeScene() {
        val (originalMerges, importSprites) = getSpritesForMerge(project.sceneList[2])
        onView(IndexMatchers().withIndex(withId(R.id.settings_button), 2)).perform(click())
        onView(withText(R.string.from_local)).perform(click())
        Intents.intended(expectedIntent)
        MergeTestUtils().assertSceneMerge(
            importSprites, originalMerges, project, project.sceneList[2], localProject
        )
    }

    @Category(Cat.AppUi::class, Level.Smoke::class)
    @Test
    fun testMergeSceneWithEqualScene() {
        val (originalMerges, importSprites) = getSpritesForMerge(project.sceneList[3])
        onView(IndexMatchers().withIndex(withId(R.id.settings_button), 3)).perform(click())
        onView(withText(R.string.from_local)).perform(click())
        Intents.intended(expectedIntent)
        MergeTestUtils().assertSceneMerge(
            importSprites, originalMerges, project, project.sceneList[3],
            localProject
        )
    }

    private fun getSpritesForMerge(scene: Scene): Pair<List<Sprite>, List<Sprite>> {
        val originalMerges = ArrayList<Sprite>()
        val importSprites = ArrayList<Sprite>()
        localProject.defaultScene.spriteList.forEach {
            val sprite = scene.getSprite(it.name)
            if (sprite != null) {
                originalMerges.add(sprite)
            } else {
                importSprites.add(it)
            }
        }
        return Pair(originalMerges, importSprites)
    }

    private fun createProjects(projectName: String) {
        project = UiTestUtils.createEmptyProject(projectName)
        val emptyScene = newSceneWithBackgroundSprite("emptyScene", "Background", project)
        project.addScene(emptyScene)
        val sceneWithSprites = newSceneWithBackgroundSprite(
            "sceneWithSprites",
            "Background",
            project
        )
        project.addScene(sceneWithSprites)
        val sprite1 = Sprite("sprite1")
        sprite1.addScript(StartScript())
        sprite1.userVariables.add(UserVariable("var1", 1))
        val sprite2 = Sprite("Clouds1")
        sceneWithSprites.addSprite(sprite1)
        sceneWithSprites.addSprite(sprite2)
        projectManager.currentProject = project
        projectManager.currentlyEditedScene = project.defaultScene
        localProject =
            UiTestUtils.getDefaultTestProject(ApplicationProvider.getApplicationContext())
        val scene = localProject.defaultScene
        scene.name = "defaultScene"
        project.addScene(scene)
        XstreamSerializer.getInstance().saveProject(project)
        XstreamSerializer.getInstance().saveProject(localProject)
    }
}
