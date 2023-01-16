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

package org.catrobat.catroid.uiespresso.ui.dialog

import android.Manifest
import android.app.Activity
import android.app.Instrumentation
import android.content.Context
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
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.merge.ImportLocalObjectActivity
import org.catrobat.catroid.test.merge.MergeTestUtils
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.core.AllOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject
import java.io.File

class ImportSpritesDialogTest {
    private val projectName = javaClass.simpleName
    private lateinit var project: Project
    private lateinit var localProject: Project
    private var importSpriteCount: Int = 0
    private var projectManager = inject(ProjectManager::class.java).value
    private var context = ApplicationProvider.getApplicationContext<Context>()
    private var expectedIntent: Matcher<Intent>? = null
    private val tmpPath = File(
        Constants.CACHE_DIRECTORY.absolutePath, "Pocket Code Test Temp"
    )

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        ProjectActivity::class.java, ProjectActivity.EXTRA_FRAGMENT_POSITION,
        ProjectActivity.FRAGMENT_SPRITES
    )

    @get:Rule
    var runtimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        createProjects(projectName)
        baseActivityTestRule.launchActivity()
        Intents.init()
        expectedIntent = AllOf.allOf(IntentMatchers.hasExtra(
            Constants.EXTRA_IMPORT_REQUEST_CODE, ImportLocalObjectActivity.REQUEST_SPRITE))

        if (!tmpPath.exists()) {
            tmpPath.mkdirs()
        }

        val importSprites = arrayListOf(
            localProject.defaultScene.backgroundSprite.name,
            localProject.defaultScene.spriteList[1].name
        )
        importSpriteCount = importSprites.size
        val resultData = Intent()
        resultData.putExtra(Constants.EXTRA_PROJECT_PATH, localProject.directory.absoluteFile)
        resultData.putExtra(Constants.EXTRA_SCENE_NAME, localProject.defaultScene.name)
        resultData.putExtra(Constants.EXTRA_SPRITE_NAMES, importSprites)

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

    @Test
    fun testShowMultipleSpritesToImport() {
        Espresso.onView(ViewMatchers.withId(R.id.button_add))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.dialog_import_sprite_from_local))
            .perform(ViewActions.click())
        Intents.intended(expectedIntent)
        Espresso.onView(ViewMatchers.withText(R.string.import_sprite_dialog_title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        val linearLayout = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.new_sprites_container),
                ViewMatchers.withParent(ViewMatchers.withParent(ViewMatchers.withId(androidx.appcompat.R.id.custom))),
                ViewMatchers.isDisplayed()
            )
        )
        linearLayout.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        linearLayout.check(ViewAssertions.matches(ViewMatchers.hasChildCount(importSpriteCount)))
    }

    @Test
    fun testRenameMultipleSpritesBeforeImport() {
        Espresso.onView(ViewMatchers.withId(R.id.button_add))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.dialog_import_sprite_from_local))
            .perform(ViewActions.click())
        Intents.intended(expectedIntent)
        Espresso.onView(ViewMatchers.withText(R.string.import_sprite_dialog_title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withSubstring(context.getString(R.string.background)),
                ViewMatchers.isDisplayed()
            )
        ).perform(ViewActions.replaceText("test"))

        Espresso.onView(ViewMatchers.withText(R.string.ok))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("test"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testDismissDialogImportSprites() {
        val originalProjectData = MergeTestUtils().getOriginalProjectData(project)
        Espresso.onView(ViewMatchers.withId(R.id.button_add))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.dialog_import_sprite_from_local))
            .perform(ViewActions.click())
        Intents.intended(expectedIntent)
        Espresso.onView(ViewMatchers.withText(R.string.import_sprite_dialog_title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(R.string.cancel))
            .perform(ViewActions.click())
        MergeTestUtils().assertRejectedImport(project, originalProjectData)
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
        XstreamSerializer.getInstance().saveProject(localProject)
    }
}
