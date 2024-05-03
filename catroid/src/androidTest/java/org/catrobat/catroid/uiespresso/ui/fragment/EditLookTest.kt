/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

import android.app.Activity
import android.content.Intent
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.io.ResourceImporter
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.testsuites.annotations.Cat
import org.catrobat.catroid.testsuites.annotations.Level
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent
import java.io.File
import java.io.IOException

@Category(Cat.AppUi::class, Level.Smoke::class)
@RunWith(AndroidJUnit4::class)
class EditLookTest {
    private val projectManager: ProjectManager by KoinJavaComponent.inject(ProjectManager::class.java)

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java, SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_LOOKS
    )

    @Before
    fun setUp() {
        createProject()
        baseActivityTestRule.launchActivity()
    }

    @After
    fun tearDown() {
        TestUtils.deleteProjects(RenameSpriteTest::class.java.simpleName)
    }

    @Test
    fun overwriteLookFileWhenResizedTest() {
        val fileLengthBeforeEdit = projectManager.currentSprite.lookList[0].file.length()
        val fileNameBeforeEdit = projectManager.currentSprite.lookList[0].name
        val fragment = baseActivityTestRule
            .activity
            .supportFragmentManager
            .findFragmentById(R.id.fragment_container)

        onRecyclerView().atPosition(0).perform(click())

        pressBack()
        createScaledFileInCache()

        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        InstrumentationRegistry.getInstrumentation().runOnMainSync(Runnable {
            fragment!!.onActivityResult(
                SpriteActivity.EDIT_LOOK,
                Activity.RESULT_OK,
                Intent()
            )
        })

        val fileLengthAfterEdit = projectManager.currentSprite.lookList[0].file.length()
        val fileNameAfterEdit = projectManager.currentSprite.lookList[0].name
        assert(fileLengthAfterEdit != fileLengthBeforeEdit)
        assert(fileNameAfterEdit == fileNameBeforeEdit)
    }

    @Throws(IOException::class)
    private fun createProject() {
        val projectName = "deleteLookFragmentTest"
        val project = UiTestUtils.createDefaultTestProject(projectName)
        XstreamSerializer.getInstance().saveProject(project)
        val imageFile = ResourceImporter.createImageFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            org.catrobat.catroid.test.R.drawable.catroid_banzai,
            File(project.defaultScene.directory, Constants.IMAGE_DIRECTORY_NAME),
            "catroid_sunglasses.png",
            1.0
        )
        val lookDataList: MutableList<LookData> = projectManager.getCurrentSprite().getLookList()
        val lookData = LookData()
        lookData.file = imageFile
        lookData.name = "testLook1"
        lookDataList.add(lookData)
    }

    @Throws(IOException::class)
    private fun createScaledFileInCache(): File? {
        return ResourceImporter.createImageFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            org.catrobat.catroid.test.R.drawable.catroid_banzai,
            File(Constants.CACHE_DIRECTORY, Constants.IMAGE_DIRECTORY_NAME),
            "catroid_sunglasses_scaled.png",
            0.5
        )
    }
}
