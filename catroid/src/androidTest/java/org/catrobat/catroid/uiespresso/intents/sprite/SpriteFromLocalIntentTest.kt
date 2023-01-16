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

package org.catrobat.catroid.uiespresso.intents.sprite

import android.Manifest
import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.GrantPermissionRule
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.DefaultProjectHandler
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.merge.ImportLocalObjectActivity
import org.catrobat.catroid.test.merge.MergeTestUtils
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.koin.java.KoinJavaComponent
import java.io.File

class SpriteFromLocalIntentTest {
    private val testSprite: Sprite = Sprite("test")
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
        ProjectActivity.FRAGMENT_SPRITES
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

        expectedIntent = AllOf.allOf(
            IntentMatchers.hasExtra(
                ImportLocalObjectActivity.TAG,
                ImportLocalObjectActivity.REQUEST_PROJECT
            )
        )

        if (!tmpPath.exists()) {
            tmpPath.mkdirs()
        }

        val resultData = Intent()
        resultData.putExtra(
            ImportLocalObjectActivity.REQUEST_PROJECT,
            localProject.directory.absoluteFile
        )
        resultData.putExtra(ImportLocalObjectActivity.REQUEST_SCENE, localProject.defaultScene.name)
        resultData.putExtra(
            ImportLocalObjectActivity.REQUEST_SPRITE,
            arrayListOf(
                localProject.defaultScene.backgroundSprite.name,
                localProject.defaultScene.spriteList[1].name
            )
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

    @Category(AppUi::class, Smoke::class)
    @Test
    fun testMergeWithSpriteFromLocalIntent() {
        UiTestUtils.openSpriteActionMenu(projectManager.currentSprite.name, false)
        Espresso.onView(
            ViewMatchers.withText(
                baseActivityTestRule.activity.getString(R.string.from_local)
            )
        ).perform(ViewActions.click())
        Intents.intended(expectedIntent)
        MergeTestUtils().assertSuccessfulSpriteMerge(
            project, localProject, projectManager.currentSprite, testSprite,
            listOf(
                localProject.defaultScene.backgroundSprite, localProject.defaultScene
                    .spriteList[1]
            )
        )
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun testImportSpriteFromLocalIntentTest() {
        Espresso.onView(ViewMatchers.withId(R.id.button_add))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.dialog_import_sprite_from_local))
            .perform(ViewActions.click())
        Intents.intended(expectedIntent)
        Espresso.onView(ViewMatchers.withText(R.string.import_sprite_dialog_title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(R.string.ok))
            .perform(ViewActions.click())
        MergeTestUtils().assertSuccessfulSpriteImport(
            project, localProject, localProject.defaultScene.spriteList[1],
            project.defaultScene.spriteList.last(), false
        )
    }

    private fun createProjects(projectName: String) {
        project = Project(ApplicationProvider.getApplicationContext(), projectName)
        project.defaultScene.addSprite(Sprite("test"))
        projectManager.currentProject = project
        projectManager.currentlyEditedScene = project.defaultScene
        projectManager.currentSprite = project.defaultScene.getSprite("test")
        projectManager.currentSprite.addUserList(UserList("list"))
        XstreamSerializer.getInstance().saveProject(project)
        localProject = DefaultProjectHandler.createAndSaveDefaultProject(
            "local",
            ApplicationProvider.getApplicationContext(),
            false
        )
        XstreamSerializer.getInstance().saveProject(localProject)
    }
}
