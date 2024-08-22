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
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withSubstring
import androidx.test.espresso.matcher.ViewMatchers.withText
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
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

class ImportSpritesDialogTest {
    private val projectName = javaClass.simpleName
    private lateinit var project: Project
    private lateinit var localProject: Project
    private var importSpriteCount: Int = 0
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

    @Before
    @Throws(Exception::class)
    fun setUp() {
        createProjects(projectName)
        baseActivityTestRule.launchActivity()
        Intents.init()
        expectedIntent = allOf(
            hasExtra(
                equalTo(ImportLocalObjectActivity.TAG),
                equalTo(ImportLocalObjectActivity.REQUEST_PROJECT)
            )
        )

        if (!tmpPath.exists()) {
            tmpPath.mkdirs()
        }

        val importSprites = arrayListOf(
            localProject.defaultScene.backgroundSprite.name,
            localProject.defaultScene.spriteList[1].name
        )
        importSpriteCount = importSprites.size
        val resultData = Intent()
        resultData.putExtra(
            ImportLocalObjectActivity.REQUEST_PROJECT, localProject.directory
                .absoluteFile
        )
        resultData.putExtra(ImportLocalObjectActivity.REQUEST_SCENE, localProject.defaultScene.name)
        resultData.putExtra(ImportLocalObjectActivity.REQUEST_SPRITE, importSprites)

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
        onView(withId(R.id.button_add)).perform(click())
        onView(withId(R.id.dialog_import_sprite_from_local)).perform(click())
        Intents.intended(expectedIntent)
        onView(withText(R.string.import_sprite_dialog_title)).check(matches(isDisplayed()))
        val linearLayout = onView(
            Matchers.allOf(
                withId(R.id.new_sprites_container),
                withParent(withParent(withId(androidx.appcompat.R.id.custom))),
                isDisplayed()
            )
        )
        linearLayout.check(matches(isDisplayed()))
        linearLayout.check(matches(hasChildCount(importSpriteCount)))
    }

    @Test
    fun testRenameMultipleSpritesBeforeImport() {
        onView(withId(R.id.button_add)).perform(click())
        onView(withId(R.id.dialog_import_sprite_from_local)).perform(click())
        Intents.intended(expectedIntent)
        onView(withText(R.string.import_sprite_dialog_title)).check(matches(isDisplayed()))
        onView(
            Matchers.allOf(
                withSubstring(context.getString(R.string.background)),
                isDisplayed()
            )
        ).perform(replaceText("test"))

        onView(withText(R.string.ok)).perform(click())
        onView(withText("test")).check(matches(isDisplayed()))
    }

    @Test
    fun testDismissDialogImportSprites() {
        val originalProjectData = MergeTestUtils().getOriginalProjectData(project)
        onView(withId(R.id.button_add)).perform(click())
        onView(withId(R.id.dialog_import_sprite_from_local)).perform(click())
        Intents.intended(expectedIntent)
        onView(withText(R.string.import_sprite_dialog_title)).check(matches(isDisplayed()))
        onView(withText(R.string.cancel)).perform(click())
        MergeTestUtils().assertRejectedImport(project, originalProjectData)
    }

    private fun createProjects(projectName: String) {
        project = UiTestUtils.createProjectWithOutDefaultScript(projectName)
        XstreamSerializer.getInstance().saveProject(project)
        localProject = DefaultProjectHandler.createAndSaveDefaultProject(
            "local",
            ApplicationProvider.getApplicationContext(),
            false
        )
        XstreamSerializer.getInstance().saveProject(localProject)
    }
}
