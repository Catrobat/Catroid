/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.io.ResourceImporter
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.catrobat.catroid.ui.recyclerview.controller.LookController
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.koin.java.KoinJavaComponent
import java.io.File

enum class ActivityType {
    SPRITE, PROJECT
}

@RunWith(Parameterized::class)
class BackpackUndoTest(private val activityType: ActivityType, private val fragmentId: Int, private val testName: String) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{2}")
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf(ActivityType.SPRITE, SpriteActivity.FRAGMENT_LOOKS,"LOOKS"),
                arrayOf(ActivityType.SPRITE, SpriteActivity.FRAGMENT_SOUNDS,"SOUNDS"),
                arrayOf(ActivityType.SPRITE, SpriteActivity.FRAGMENT_SCRIPTS,"SCRIPTS"),
                arrayOf(ActivityType.PROJECT, ProjectActivity.FRAGMENT_SPRITES,"SPRITES"),
                arrayOf(ActivityType.PROJECT, ProjectActivity.FRAGMENT_SCENES, "SCENES")
            )
        }
    }

    @get:Rule
    var baseActivityTestRule1 = FragmentActivityTestRule(
        ProjectActivity::class.java, ProjectActivity.EXTRA_FRAGMENT_POSITION, fragmentId
    )

    @get:Rule
    var baseActivityTestRule2 = FragmentActivityTestRule(
        SpriteActivity::class.java, SpriteActivity.EXTRA_FRAGMENT_POSITION, fragmentId
    )
    private val backpackManager by KoinJavaComponent.inject(BackpackListManager::class.java)
    private lateinit var spriteController: SpriteController
    private lateinit var looksController: LookController
    private lateinit var lookData1: LookData
    private lateinit var lookData2: LookData
    private lateinit var lookData3: LookData

    private lateinit var project: Project
    private lateinit var soundInfo: SoundInfo
    private lateinit var soundInfo2: SoundInfo
    private lateinit var soundInfo3: SoundInfo

    private lateinit var imageFile: File

    private lateinit var soundFile: File

    private lateinit var soundFile2: File

    @Before
    fun setUp() {
        spriteController = SpriteController()
        looksController = LookController()
        project = UiTestUtils.createDefaultTestProject("testProject")
        val scene = project.defaultScene
        XstreamSerializer.getInstance().saveProject(project)

        ResourceImporter.createImageFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            org.catrobat.catroid.test.R.raw.icon,
            File(scene.directory.path),
            Constants.SCREENSHOT_AUTOMATIC_FILE_NAME,
            1.0
        )

        imageFile = ResourceImporter.createImageFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            org.catrobat.catroid.test.R.raw.red_image,
            File(project.defaultScene.directory, Constants.IMAGE_DIRECTORY_NAME),
            "red_image.png",
            1.0
        )

        soundFile = ResourceImporter.createSoundFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            org.catrobat.catroid.test.R.raw.longsound,
            File(project.defaultScene.directory, Constants.SOUND_DIRECTORY_NAME),
            "longsound.mp3"
        )

        soundFile2 = ResourceImporter.createSoundFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            org.catrobat.catroid.test.R.raw.longsound,
            File(project.defaultScene.directory, Constants.SOUND_DIRECTORY_NAME),
            "longsound.mp3"
        )
        project.addScene(Scene("Scene2", project))
        project.defaultScene.getSprite(TestUtils.DEFAULT_TEST_SPRITE_NAME).soundList.add(
            SoundInfo("testSound1", soundFile)
        )
        project.defaultScene.getSprite(TestUtils.DEFAULT_TEST_SPRITE_NAME).lookList.add(
            LookData("test", imageFile)
        )
        when (activityType) {
            ActivityType.SPRITE -> baseActivityTestRule2.launchActivity()
            ActivityType.PROJECT -> baseActivityTestRule1.launchActivity()
        }
        addObjectToBackpack()
    }

    @After
    fun tearDown() {
        TestUtils.clearBackPack(backpackManager)
        TestUtils.deleteProjects("testProject")
    }

    private fun assignSize(): Int {
        return when (activityType) {
            ActivityType.SPRITE -> when (fragmentId) {
                SpriteActivity.FRAGMENT_LOOKS -> backpackManager.backpackedLooks.size
                SpriteActivity.FRAGMENT_SOUNDS -> backpackManager.backpackedSounds.size
                SpriteActivity.FRAGMENT_SCRIPTS -> backpackManager.backpackedScripts.size
                else -> throw IllegalArgumentException("Unknown fragmentId for SpriteActivity: $fragmentId")
            }

            ActivityType.PROJECT -> when (fragmentId) {
                ProjectActivity.FRAGMENT_SPRITES -> backpackManager.sprites.size
                ProjectActivity.FRAGMENT_SCENES -> backpackManager.scenes.size
                else -> throw IllegalArgumentException("Unknown fragmentId for ProjectActivity: $fragmentId")
            }
        }
    }

    private fun getItemName(index: Int): String {
        return when (activityType) {
            ActivityType.SPRITE -> when (fragmentId) {
                SpriteActivity.FRAGMENT_LOOKS -> backpackManager.backpackedLooks[index].name
                SpriteActivity.FRAGMENT_SOUNDS -> backpackManager.backpackedSounds[index].name
                SpriteActivity.FRAGMENT_SCRIPTS -> backpackManager.backpackedScripts.keys.elementAt(
                    index
                )

                else -> throw IllegalArgumentException("Unknown fragmentId for SpriteActivity: $fragmentId")
            }

            ActivityType.PROJECT -> when (fragmentId) {
                ProjectActivity.FRAGMENT_SPRITES -> backpackManager.sprites[index].name
                ProjectActivity.FRAGMENT_SCENES -> backpackManager.scenes[index].name
                else -> throw IllegalArgumentException("Unknown fragmentId for ProjectActivity: $fragmentId")
            }
        }
    }

    private fun getIntoBackpack() {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText(R.string.backpack)).perform(click())
        onView(withText("Unpack")).perform(click())
    }

    @Test
    fun testUndoLimitedToOneAction() {
        getIntoBackpack()

        val x = 2
        val sizeBeforeDelete = assignSize()
        var deletedItemName = ""

        repeat(x) {
            deletedItemName = getItemName(0)
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
            onView(withText(R.string.delete)).perform(click())
            RecyclerViewInteractionWrapper.onRecyclerView().atPosition(0).performCheckItemClick()
            onView(withId(R.id.confirm)).perform(click())
        }

        onView(withId(R.id.menu_undo)).perform(click())

        Assert.assertEquals(sizeBeforeDelete - x + 1, assignSize())
        val retrievedItemName: String = getItemName(0)
        Assert.assertEquals(deletedItemName, retrievedItemName)

        onView(withId(R.id.menu_undo)).check(doesNotExist())
    }

    @Test
    fun testSingleUndo() {
        getIntoBackpack()

        val sizeBeforeDelete = assignSize()

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        val deletedItemName: String = getItemName(0)
        onView(withText(R.string.delete)).perform(click())

        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(0).performCheckItemClick()
        onView(withId(R.id.confirm)).perform(click())

        onView(withId(R.id.menu_undo)).perform(click())
        val retrievedItemName: String = getItemName(0)

        Assert.assertEquals(sizeBeforeDelete, assignSize())
        Assert.assertEquals(deletedItemName, retrievedItemName)
        onView(withId(R.id.menu_undo)).check(doesNotExist())
    }

    @Test
    fun testUndoOnAllDeleted() {
        getIntoBackpack()

        val sizeBeforeDelete = assignSize()
        val deletedItemsName = mutableListOf<String>()
        val retrievedItemsName = mutableListOf<String>()

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText(R.string.delete)).perform(click())

        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(0).performCheckItemClick()
        deletedItemsName.add(getItemName(0))
        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(1).performCheckItemClick()
        deletedItemsName.add(getItemName(1))
        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(2).performCheckItemClick()
        deletedItemsName.add(getItemName(2))
        onView(withId(R.id.confirm)).perform(click())

        onView(withId(R.id.menu_undo)).perform(click())
        retrievedItemsName.add(getItemName(0))
        retrievedItemsName.add(getItemName(1))
        retrievedItemsName.add(getItemName(2))

        Assert.assertEquals(sizeBeforeDelete, assignSize())
        Assert.assertEquals(deletedItemsName, retrievedItemsName)

        onView(withId(R.id.menu_undo)).check(doesNotExist())
    }

    @Test
    fun testUndoNotPossibleAfterReturning() {
        getIntoBackpack()

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText(R.string.delete)).perform(click())

        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(0).performCheckItemClick()
        onView(withId(R.id.confirm)).perform(click())

        val savedBackpack = backpackManager.backpack
        pressBack()
        backpackManager.backpack = savedBackpack

        getIntoBackpack()

        onView(withId(R.id.menu_undo)).check(doesNotExist())
    }

    private fun addObjectToBackpack() {
        when (activityType) {
            ActivityType.SPRITE -> {
                when (fragmentId) {
                    SpriteActivity.FRAGMENT_LOOKS -> {
                        lookData1 = LookData("test1", imageFile)
                        lookData2 = LookData("test2", imageFile)
                        lookData3 = LookData("test3", imageFile)

                        backpackManager.backpackedLooks.add(lookData1)
                        backpackManager.backpackedLooks.add(lookData2)
                        backpackManager.backpackedLooks.add(lookData3)
                    }

                    SpriteActivity.FRAGMENT_SOUNDS -> {
                        soundInfo = SoundInfo("testSound1", soundFile)
                        soundInfo2 = SoundInfo("testSound2", soundFile2)
                        soundInfo3 = SoundInfo("testSound3", soundFile2)
                        backpackManager.backpackedSounds.add(soundInfo)
                        backpackManager.backpackedSounds.add(soundInfo2)
                        backpackManager.backpackedSounds.add(soundInfo3)
                    }

                    SpriteActivity.FRAGMENT_SCRIPTS -> {
                        val scriptGroup = project.defaultScene.spriteList[1].scriptList
                        backpackManager.addScriptToBackPack("start1", scriptGroup)
                        backpackManager.addScriptToBackPack("start2", scriptGroup)
                        backpackManager.addScriptToBackPack("start3", scriptGroup)
                    }

                    else -> throw IllegalArgumentException("Unknown fragmentId for SpriteActivity: $fragmentId")
                }
                backpackManager.saveBackpack()
            }

            ActivityType.PROJECT -> {
                when (fragmentId) {
                    ProjectActivity.FRAGMENT_SPRITES -> {
                        val sprite = project.defaultScene.spriteList[0]
                        backpackManager.sprites.add(spriteController.pack(sprite))
                        backpackManager.sprites.add(spriteController.pack(sprite))
                        backpackManager.sprites.add(spriteController.pack(sprite))
                    }

                    ProjectActivity.FRAGMENT_SCENES -> {
                        val scene1 = Scene("Scene1", project)
                        val scene2 = Scene("Scene2", project)
                        val scene3 = Scene("Scene3", project)

                        backpackManager.scenes.add(scene1)
                        backpackManager.scenes.add(scene2)
                        backpackManager.scenes.add(scene3)
                    }

                    else -> throw IllegalArgumentException("Unknown fragmentId for ProjectActivity: $fragmentId")
                }
                backpackManager.saveBackpack()
            }
        }
    }
}
