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

package org.catrobat.catroid.test.content.sprite

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withSubstring
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.SharedPreferenceKeys
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.util.actions.selectTabAtPosition
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@RunWith(AndroidJUnit4::class)
class LargeImageTest {

    private lateinit var project: Project
    var projectName = "LargeImageTest"

    private val fileNameNormal = "Exif.jpg"
    private val fileNameLarge = "large_image.png"
    private val spriteNameNormal = "Exif"
    private val dummy_sprite = "testSprite"

    private val type = "image/*"
    private lateinit var imageFolder: File
    private lateinit var imageFileNormal: File
    private lateinit var imageFileLarge: File

    @Rule
    @JvmField
    var baseActivityTestRule = FragmentActivityTestRule(
        ProjectActivity::class.java, ProjectActivity.EXTRA_FRAGMENT_POSITION,
        ProjectActivity.FRAGMENT_SPRITES
    )

    @get:Rule
    var permissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.READ_EXTERNAL_STORAGE)

    private fun createCacheFiles(name: String): File {
        val cacheFolder =
            File(ApplicationProvider.getApplicationContext<Context>().cacheDir.absolutePath)
        val cacheFile = File(cacheFolder, name)
        val originalImage = InstrumentationRegistry.getInstrumentation().context.assets.open(name)
        val buf = ByteArray(originalImage.available())
        originalImage.read(buf)
        val outputStream: OutputStream = FileOutputStream(cacheFile)
        outputStream.write(buf)
        return cacheFile
    }

    private fun setupFolderAndImage() {
        imageFileNormal = createCacheFiles(fileNameNormal)
        imageFileLarge = createCacheFiles(fileNameLarge)

        imageFolder = File(
            project.defaultScene.directory,
            Constants.IMAGE_DIRECTORY_NAME
        )
        if (!imageFolder.exists()) {
            imageFolder.mkdirs()
        } else {
            return
        }

        imageFolder = File(
            ApplicationProvider.getApplicationContext<Context>().cacheDir,
            Constants.IMAGE_DIRECTORY_NAME
        )
        if (!imageFolder.exists()) {
            imageFolder.mkdirs()
        }
    }

    @Before
    fun setUp() {
        StorageOperations.deleteDir(ApplicationProvider.getApplicationContext<Context>().cacheDir)
        createProject()
        setupFolderAndImage()
        Intents.init()
        baseActivityTestRule.launchActivity()
        PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
            ?.edit()
            ?.clear()
            ?.apply()
    }

    @After
    fun tearDown() {
        Intents.release()
        baseActivityTestRule.finishActivity()
        PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
            ?.edit()
            ?.remove(SharedPreferenceKeys.NEW_SPRITE_VISUAL_PLACEMENT_KEY)
            ?.apply()
        Assert.assertTrue(imageFileLarge.delete())
        Assert.assertTrue(imageFileNormal.delete())
        StorageOperations.deleteDir(ApplicationProvider.getApplicationContext<Context>().cacheDir)
        StorageOperations.deleteDir(project.directory)
    }

    @Test
    fun testNormalImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType(type)
        intent.data = Uri.fromFile(imageFileNormal)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
        intending(
            hasAction(Intent.ACTION_CHOOSER)
        ).respondWith(result)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        onView(withSubstring(dummy_sprite))
            .perform(click())
        onView(withId(R.id.tab_layout))
            .perform(selectTabAtPosition(SpriteActivity.FRAGMENT_LOOKS))
        onView(withId(R.id.button_add))
            .perform(click())
        onView(withId(R.id.dialog_new_look_gallery))
            .perform(click())

        intended(hasAction(Intent.ACTION_CHOOSER))
        pressBack()
        onView(withSubstring(dummy_sprite))
            .perform(click())
        onView(withId(R.id.tab_layout))
            .perform(selectTabAtPosition(SpriteActivity.FRAGMENT_LOOKS))

        val projectManager by inject(ProjectManager::class.java)
        Assert.assertEquals(
            1,
            projectManager.currentProject.defaultScene.getSprite(dummy_sprite).lookList.size
        )

        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    @Test
    fun testNormalImageDifferentView() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType(type)
        intent.data = Uri.fromFile(imageFileNormal)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
        intending(
            hasAction(Intent.ACTION_CHOOSER)
        ).respondWith(result)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        onView(withId(R.id.button_add))
            .perform(click())
        onView(withId(R.id.dialog_new_look_gallery))
            .perform(click())
        intended(hasAction(Intent.ACTION_CHOOSER))

        onView(withText(R.string.place_visually_text))
            .check(matches(isDisplayed()))
        onView(withId(R.id.place_visually_sprite_switch))
            .check(matches(isChecked()))
        closeSoftKeyboard()

        onView(withId(R.id.place_visually_sprite_switch))
            .perform(click())
        onView(
            CoreMatchers.allOf(
                withId(android.R.id.button1),
                withText(R.string.ok)
            )
        ).perform(click())
        intended(hasAction(Intent.ACTION_CHOOSER))

        onView(withSubstring(spriteNameNormal))
            .perform(click())
        onView(withId(R.id.tab_layout))
            .perform(selectTabAtPosition(SpriteActivity.FRAGMENT_LOOKS))

        val projectManager by inject(ProjectManager::class.java)
        Assert.assertEquals(3, projectManager.currentProject.defaultScene.spriteList.size)

        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    @Test
    fun testLargeImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType(type)
        intent.data = Uri.fromFile(imageFileLarge)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
        intending(
            hasAction(Intent.ACTION_CHOOSER)
        ).respondWith(result)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        onView(withSubstring(dummy_sprite))
            .perform(click())
        onView(withId(R.id.tab_layout))
            .perform(selectTabAtPosition(SpriteActivity.FRAGMENT_LOOKS))
        onView(withId(R.id.button_add))
            .perform(click())
        onView(withId(R.id.dialog_new_look_gallery))
            .perform(click())
        intended(hasAction(Intent.ACTION_CHOOSER))

        onView(withText(R.string.Image_size_too_large_text))
            .check(matches(isDisplayed()))
        onView(withText(R.string.ok))
            .perform(click())

        val projectManager by inject(ProjectManager::class.java)
        Assert.assertEquals(
            0,
            projectManager.currentProject.defaultScene.getSprite(dummy_sprite).lookList.size
        )
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    @Test
    @LargeTest
    fun testLargeImageDifferentView() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType(type)
        intent.data = Uri.fromFile(imageFileLarge)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
        intending(
            hasAction(Intent.ACTION_CHOOSER)
        ).respondWith(result)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        onView(withId(R.id.button_add))
            .perform(click())

        onView(withId(R.id.dialog_new_look_gallery))
            .perform(click())

        intended(hasAction(Intent.ACTION_CHOOSER))
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        onView(withText(R.string.place_visually_text))
            .check(matches(isDisplayed()))
        onView(withId(R.id.place_visually_sprite_switch))
            .check(matches(isChecked()))
        closeSoftKeyboard()

        onView(withId(R.id.place_visually_sprite_switch))
            .perform(click())

        onView(
            CoreMatchers.allOf(
                withId(android.R.id.button1),
                withText(R.string.ok)
            )
        ).perform(click())

        intended(hasAction(Intent.ACTION_CHOOSER))
        onView(withText(R.string.Image_size_too_large_text))
            .check(matches(isDisplayed()))
        onView(withText(R.string.ok))
            .perform(click())

        val projectManager by inject(ProjectManager::class.java)
        Assert.assertEquals(2, projectManager.currentProject.defaultScene.spriteList.size)

        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    private fun createProject() {
        project = Project(ApplicationProvider.getApplicationContext(), projectName)

        project.defaultScene.addSprite(Sprite(dummy_sprite))
        val projectManager by inject(ProjectManager::class.java)
        projectManager.currentProject = project
        projectManager.setCurrentSceneAndSprite(project.defaultScene.name, dummy_sprite)
        projectManager.currentlyEditedScene = project.defaultScene
    }
}
