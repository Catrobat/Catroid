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

package org.catrobat.catroid.uiespresso.ui.dialog

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.DefaultProjectHandler
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.merge.ImportLocalObjectActivity
import org.catrobat.catroid.test.merge.MergeTestUtils
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

class RejectImportDialogTest {
    private val projectName = javaClass.simpleName
    private lateinit var project: Project
    private lateinit var projectWithVariableConflicts: Project
    private lateinit var projectWithProjectNameConflicts: Project
    private var importSpriteCount: Int = 0
    private var expectedIntent: Matcher<Intent>? = null
    private val tmpPath = File(
        Constants.CACHE_DIRECTORY.absolutePath, "Pocket Code Test Temp"
    )

    private val conflict = UserVariable("GlobalAndLocal")
    private val conflict2 = UserVariable("GlobalAndLocal2")
    private lateinit var result1: Instrumentation.ActivityResult
    private lateinit var result2: Instrumentation.ActivityResult

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        ProjectActivity::class.java, ProjectActivity.EXTRA_FRAGMENT_POSITION,
        ProjectActivity.FRAGMENT_SPRITES
    )

    @Before
    fun setUp() {
        createProjects(projectName)
        baseActivityTestRule.launchActivity()
        Intents.init()
        expectedIntent = allOf(
            IntentMatchers.hasExtra(
                equalTo(ImportLocalObjectActivity.TAG),
                equalTo(ImportLocalObjectActivity.REQUEST_PROJECT)
            )
        )

        if (!tmpPath.exists()) {
            tmpPath.mkdirs()
        }
        setUpResults()
    }

    private fun setUpResults() {
        val importSprites = arrayListOf(
            projectWithVariableConflicts.defaultScene.backgroundSprite.name,
            projectWithVariableConflicts.defaultScene.spriteList[1].name
        )
        importSpriteCount = importSprites.size
        val resultData1 = Intent()
        resultData1.putExtra(
            ImportLocalObjectActivity.REQUEST_PROJECT, projectWithVariableConflicts.directory
                .absoluteFile
        )
        resultData1.putExtra(
            ImportLocalObjectActivity.REQUEST_SCENE,
            projectWithVariableConflicts.defaultScene.name
        )
        resultData1.putExtra(ImportLocalObjectActivity.REQUEST_SPRITE, importSprites)

        val resultData2 = Intent()
        resultData2.putExtra(
            ImportLocalObjectActivity.REQUEST_PROJECT, projectWithProjectNameConflicts.directory
                .absoluteFile
        )
        resultData2.putExtra(
            ImportLocalObjectActivity.REQUEST_SCENE,
            projectWithProjectNameConflicts.defaultScene.name
        )
        resultData2.putExtra(ImportLocalObjectActivity.REQUEST_SPRITE, importSprites)

        result1 = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData1)
        result2 = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData2)
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
    fun testRejectImportDialogShowsVariableConflicts() {
        Intents.intending(expectedIntent).respondWith(result1)
        val originalProjectData = MergeTestUtils().getOriginalProjectData(project)
        onView(withId(R.id.button_add)).perform(click())
        onView(withId(R.id.dialog_import_sprite_from_local)).perform(click())
        Intents.intended(expectedIntent)
        onView(withText(R.string.reject_import)).check(matches(isDisplayed()))
        onView(withId(R.id.conflicting_variables)).check(matches(isDisplayed()))
        onView(withText(R.string.ok)).perform(click())
        MergeTestUtils().assertRejectedImport(project, originalProjectData)
    }

    @Test
    fun testRejectImportDialogShowsProjectNameConflict() {
        Intents.intending(expectedIntent).respondWith(result2)
        val original = MergeTestUtils().getOriginalProjectData(project)

        onView(withId(R.id.button_add)).perform(click())
        onView(withId(R.id.dialog_import_sprite_from_local)).perform(click())
        Intents.intended(expectedIntent)
        onView(withText(R.string.import_unresolvable_project_name_reason)).check(matches(isDisplayed()))
        onView(withText(R.string.ok)).inRoot(RootMatchers.isDialog()).check(matches(isDisplayed()))
            .perform(click())

        MergeTestUtils().assertRejectedImport(project, original)
    }

    private fun createProjects(projectName: String) {
        project = UiTestUtils.createProjectWithOutDefaultScript(projectName)
        XstreamSerializer.getInstance().saveProject(project)
        projectWithVariableConflicts = DefaultProjectHandler.createAndSaveDefaultProject(
            "local",
            ApplicationProvider.getApplicationContext(),
            false
        )

        projectWithProjectNameConflicts = DefaultProjectHandler.createAndSaveDefaultProject(
            "local.default.name.error",
            ApplicationProvider.getApplicationContext(),
            false
        )

        project.defaultScene.backgroundSprite.userVariables.add(conflict)
        projectWithVariableConflicts.userVariables.add(conflict)
        project.userVariables.add(conflict2)
        projectWithVariableConflicts.defaultScene.backgroundSprite.userVariables.add(conflict2)
        XstreamSerializer.getInstance().saveProject(projectWithVariableConflicts)
    }
}
