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

package org.catrobat.catroid.test.content.sprite

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
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.DefaultProjectHandler
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.merge.ImportLocalObjectActivity
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.koin.java.KoinJavaComponent
import java.io.File

class DeleteImportedSpriteTest {
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
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun testDeleteOriginalAndImportedSprites() {
        importSprite()
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        SpriteController().delete(localProject.defaultScene.getSprite("Animal"))
        project.defaultScene.spriteList[1].lookList.forEach {
            assertTrue(it.file.exists())
        }
        project.defaultScene.spriteList[1].soundList.forEach {
            assertTrue(it.file.exists())
        }
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun testOriginalLooksAndSoundsExistAfterDeleteImport() {
        importSprite()
        SpriteController().delete(project.defaultScene.getSprite("Animal"))
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        localProject.defaultScene.spriteList[1].lookList.forEach {
            assertTrue(it.file.exists())
        }
        localProject.defaultScene.spriteList[1].soundList.forEach {
            assertTrue(it.file.exists())
        }
    }

    private fun importSprite() {
        Espresso.onView(ViewMatchers.withId(R.id.button_add))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.dialog_import_sprite_from_local))
            .perform(ViewActions.click())
        Intents.intended(expectedIntent)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(withText(R.string.import_sprite_dialog_title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(R.string.ok))
            .perform(ViewActions.click())
    }

    private fun createProjects(projectName: String) {
        project = Project(ApplicationProvider.getApplicationContext(), projectName)
        projectManager.currentProject = project
        projectManager.currentlyEditedScene = project.defaultScene
        XstreamSerializer.getInstance().saveProject(project)
        localProject = DefaultProjectHandler.createAndSaveDefaultProject(
            "local",
            ApplicationProvider.getApplicationContext(),
            false
        )
        localProject.defaultScene.spriteList.removeAt(2)
        localProject.defaultScene.spriteList.removeAt(1)
        XstreamSerializer.getInstance().saveProject(localProject)
    }
}
