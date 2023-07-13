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
package org.catrobat.catroid.uiespresso.ui.fragment

import android.content.Context
import android.media.MediaMetadataRetriever
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.text.format.DateUtils
import android.widget.EditText
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME
import org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_DETAILS_LOOKS_PREFERENCE_KEY
import org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_DETAILS_SOUNDS_PREFERENCE_KEY
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.ResourceImporter.createImageFileFromResourcesInDirectory
import org.catrobat.catroid.io.ResourceImporter.createSoundFileFromResourcesInDirectory
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.SpriteActivity.FRAGMENT_LOOKS
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.getResourcesString
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.openActionBarMenu
import org.catrobat.catroid.uiespresso.util.actions.selectTabAtPosition
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.catrobat.catroid.utils.FileMetaDataExtractor.getSizeAsString
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.util.Locale.getDefault

@Category(AppUi::class, Smoke::class)
@RunWith(AndroidJUnit4::class)
class RenameSceneTest {
    private lateinit var applicationContext: Context
    private lateinit var project: Project
    private val projectName = "RenameSceneTest"
    private val newSceneName = "newSceneName"
    private val otherSceneName = "otherScene"

    private val projectManager by inject(ProjectManager::class.java)

    private var bufferedShowDetailsLooksPreference = false
    private var bufferedShowDetailsSoundsPreference = false

    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        ProjectActivity::class.java, false, false
    )

    @Before
    fun setUp() {
        applicationContext = getApplicationContext()
        createProject()

        val sharedPreferences = getDefaultSharedPreferences(applicationContext)
        bufferedShowDetailsLooksPreference = sharedPreferences.getBoolean(SHOW_DETAILS_LOOKS_PREFERENCE_KEY, false)
        bufferedShowDetailsSoundsPreference = sharedPreferences.getBoolean(SHOW_DETAILS_SOUNDS_PREFERENCE_KEY, false)

        sharedPreferences
            .edit()
            .putBoolean(SHOW_DETAILS_LOOKS_PREFERENCE_KEY, true)
            .putBoolean(SHOW_DETAILS_SOUNDS_PREFERENCE_KEY, true)
            .commit()

        baseActivityTestRule.launchActivity(null)
    }

    @After
    fun tearDown() {
        getDefaultSharedPreferences(getApplicationContext())
            .edit()
            .putBoolean(SHOW_DETAILS_LOOKS_PREFERENCE_KEY, bufferedShowDetailsLooksPreference)
            .putBoolean(SHOW_DETAILS_SOUNDS_PREFERENCE_KEY, bufferedShowDetailsSoundsPreference)
            .commit()

        TestUtils.deleteProjects(projectName)
    }

    @Test
    fun testRenameScene() {
        openActionBarMenu()

        onView(withText(getResourcesString(R.string.rename)))
            .perform(click())

        onRecyclerView().atPosition(0)
            .perform(click())

        onView(withText(getResourcesString(R.string.rename_scene_dialog)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        onView(allOf(withText(getResourcesString(R.string.default_scene_name)), isDisplayed(), instanceOf(EditText::class.java)))
            .perform(replaceText(newSceneName))

        closeSoftKeyboard()

        onView(allOf(withId(android.R.id.button2), withText(getResourcesString(R.string.cancel))))
            .check(matches(isDisplayed()))

        onView(allOf(withId(android.R.id.button1), withText(getResourcesString(R.string.ok))))
            .perform(click())

        onView(withText(newSceneName))
            .check(matches(isDisplayed()))

        assertEquals(newSceneName, project.defaultScene.name)
    }

    @Test
    fun testLooksUnchangedAfterRenameScene() {
        openActionBarMenu()

        onView(withText(getResourcesString(R.string.rename)))
            .perform(click())

        onRecyclerView().atPosition(1)
            .perform(click())

        onView(allOf(withText(otherSceneName), isDisplayed(), instanceOf(EditText::class.java)))
            .perform(replaceText(newSceneName))

        closeSoftKeyboard()

        onView(allOf(withId(android.R.id.button1), withText(getResourcesString(R.string.ok))))
            .perform(click())

        onView(withText(newSceneName))
            .perform(click())

        onRecyclerView().atPosition(0)
            .perform(click())

        onView(withId(R.id.tab_layout))
            .perform(selectTabAtPosition(FRAGMENT_LOOKS))

        val falseDetailsString = String.format(getDefault(), getResourcesString(R.string.look_details), "0 x 0", "0 B")

        onRecyclerView().atPosition(0).onChildView(R.id.details_view)
            .check(matches(not(withText(falseDetailsString))))

        val item = projectManager.currentSprite.lookList.first()
        val measureString = item.measure?.get(0).toString() + " x " + item.measure?.get(1)
        val correctDetailsString = String.format(getDefault(), getResourcesString(R.string.look_details),
            measureString, getSizeAsString(item.file, applicationContext))

        onRecyclerView().atPosition(0).onChildView(R.id.details_view)
            .check(matches(withText(correctDetailsString)))
    }

    @Test
    fun testSoundsUnchangedAfterRenameScene() {
        openActionBarMenu()

        onView(withText(getResourcesString(R.string.rename)))
            .perform(click())

        onRecyclerView().atPosition(1)
            .perform(click())

        onView(allOf(withText(otherSceneName), isDisplayed(), instanceOf(EditText::class.java)))
            .perform(replaceText(newSceneName))

        closeSoftKeyboard()

        onView(allOf(withId(android.R.id.button1), withText(getResourcesString(R.string.ok))))
            .perform(click())

        onView(withText(newSceneName))
            .perform(click())

        onRecyclerView().atPosition(0)
            .perform(click())

        onView(withId(R.id.tab_layout))
            .perform(selectTabAtPosition(SpriteActivity.FRAGMENT_SOUNDS))

        val falseDetailsString = String.format(getDefault(), getResourcesString(R.string.sound_details), "00:00", "0 B")

        onRecyclerView().atPosition(0).onChildView(R.id.details_view)
            .check(matches(not(withText(falseDetailsString))))

        val item = projectManager.currentSprite.soundList.first()
        val correctDetailsString = String.format(
            getDefault(), getResourcesString(R.string.sound_details),
            getSoundDuration(item), getSizeAsString(item.file, applicationContext))

        onRecyclerView().atPosition(0).onChildView(R.id.details_view)
            .check(matches(withText(correctDetailsString)))
    }

    private fun createProject() {
        project = Project(applicationContext, projectName)
        val sprite = Sprite("Test")
        val otherScene = Scene(otherSceneName, project)
        otherScene.addSprite(sprite)
        project.addScene(otherScene)
        projectManager.currentProject = project
        saveProjectSerial(project, applicationContext)
        addLookDataToSprite(sprite, otherScene, "Image.png")
        addSoundInfoToSprite(sprite, otherScene, "Sound.mp3")
        saveProjectSerial(project, applicationContext)
    }

    private fun addLookDataToSprite(sprite: Sprite, currentScene: Scene, name: String) {
        val lookFile = createImageFileFromResourcesInDirectory(
            getInstrumentation().context.resources, org.catrobat.catroid.test.R.raw.icon,
            File(currentScene.directory, IMAGE_DIRECTORY_NAME), name, 1.0)

        val lookData = LookData(lookFile.name, lookFile)
        sprite.lookList.add(lookData)
    }

    private fun addSoundInfoToSprite(sprite: Sprite, currentScene: Scene, name: String) {
        val soundFile = createSoundFileFromResourcesInDirectory(
            getInstrumentation().context.resources, org.catrobat.catroid.test.R.raw.longsound,
            File(currentScene.directory, SOUND_DIRECTORY_NAME), name)

        val soundInfo = SoundInfo(soundFile.name, soundFile)
        sprite.soundList.add(soundInfo)
    }

    private fun getSoundDuration(sound: SoundInfo): String {
        val metadataRetriever = MediaMetadataRetriever()
        metadataRetriever.setDataSource(sound.file?.absolutePath)

        var duration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0

        duration = if (duration / 1000 == 0L) 1 else duration / 1000
        return DateUtils.formatElapsedTime(duration)
    }
}
