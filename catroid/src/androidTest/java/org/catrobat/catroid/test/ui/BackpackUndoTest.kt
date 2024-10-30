import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
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
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.catrobat.catroid.ui.recyclerview.controller.LookController
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.CoreMatchers.allOf
import androidx.test.espresso.NoMatchingViewException

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
class BackpackUndoTest(private val activityType: ActivityType, private val fragmentId: Int) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf(ActivityType.SPRITE, SpriteActivity.FRAGMENT_LOOKS),
                arrayOf(ActivityType.SPRITE, SpriteActivity.FRAGMENT_SOUNDS),
                arrayOf(ActivityType.SPRITE, SpriteActivity.FRAGMENT_SCRIPTS),
                arrayOf(ActivityType.PROJECT, ProjectActivity.FRAGMENT_SPRITES),
                arrayOf(ActivityType.PROJECT, ProjectActivity.FRAGMENT_SCENES)
            )
        }
    }

    @get:Rule
    var baseActivityTestRule1 = FragmentActivityTestRule(
        ProjectActivity::class.java,
        ProjectActivity.EXTRA_FRAGMENT_POSITION,
        fragmentId
    )

    @get:Rule
    var baseActivityTestRule2 = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        fragmentId
    )
    private val backpackManager by KoinJavaComponent.inject(BackpackListManager::class.java)
    private lateinit var spriteController: SpriteController
    private lateinit var looksController: LookController
    private var imageFolder = File(
        ApplicationProvider.getApplicationContext<Context>().cacheDir,
        Constants.IMAGE_DIRECTORY_NAME
    )
    private val fileName = "collision_donut.png"
    private lateinit var lookData: LookData
    private lateinit var project: Project
    private lateinit var soundInfo: SoundInfo
    private lateinit var soundInfo2: SoundInfo
    private lateinit var soundInfo3: SoundInfo
    private val imageFile = ResourceImporter.createImageFileFromResourcesInDirectory(
        InstrumentationRegistry.getInstrumentation().context.resources,
        org.catrobat.catroid.test.R.raw.collision_donut,
        imageFolder, fileName, 1.0
    )

    private val soundFile = ResourceImporter.createSoundFileFromResourcesInDirectory(
        InstrumentationRegistry.getInstrumentation().context.resources,
        org.catrobat.catroid.test.R.raw.testsound,
        imageFolder,
        "testsoundui.mp3"
    )

    private val soundFile2 = ResourceImporter.createSoundFileFromResourcesInDirectory(
        InstrumentationRegistry.getInstrumentation().context.resources,
        org.catrobat.catroid.test.R.raw.testsound,
        imageFolder,
        "testsoundui.mp3"
    )

    @Before
    fun setUp() {
        spriteController = SpriteController()
        looksController = LookController()
        project = UiTestUtils.createDefaultTestProject("testProject")
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
    }

    private fun assignSize(): Int {
        return when (activityType) {
            ActivityType.SPRITE ->
                when (fragmentId) {
                    SpriteActivity.FRAGMENT_LOOKS -> backpackManager.backpackedLooks.size
                    SpriteActivity.FRAGMENT_SOUNDS -> backpackManager.backpackedSounds.size
                    SpriteActivity.FRAGMENT_SCRIPTS -> backpackManager.backpackedScripts.size
                    else -> throw IllegalArgumentException("Unknown fragmentId for SpriteActivity: $fragmentId")
                }

            ActivityType.PROJECT ->
                when (fragmentId) {
                    ProjectActivity.FRAGMENT_SPRITES -> backpackManager.sprites.size
                    ProjectActivity.FRAGMENT_SCENES -> backpackManager.scenes.size
                    else -> throw IllegalArgumentException("Unknown fragmentId for ProjectActivity: $fragmentId")
                }
        }
    }

    private fun getIntoBackpack() {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText(R.string.backpack)).perform(click())
        onView(withText("Unpack")).perform(click())
    }

    private fun checkIfUndoNotDisplayed() {
        var exceptionOccurred = false

        try {
            onView(withId(R.id.menu_undo)).perform(click())
        } catch (e: NoMatchingViewException) {
            exceptionOccurred = true
        }
        assert(exceptionOccurred)
    }

    @Test
    fun testUndoLimitedToOneAction() {
        getIntoBackpack()

        val x = 2
        val sizeBeforeDelete = assignSize()
        for (i in 1..x) {
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
            onView(withText(R.string.delete)).perform(click())

            RecyclerViewInteractionWrapper.onRecyclerView().atPosition(0)
                .performCheckItemClick()
            onView(withId(R.id.confirm)).perform(click())

            onView(allOf(withId(android.R.id.button1), withText(R.string.delete)))
                .check(matches(isDisplayed()))

            onView(allOf(withId(android.R.id.button1), withText(R.string.delete)))
                .perform(click())
        }
        onView(withId(R.id.menu_undo)).perform(click())

        Assert.assertEquals(sizeBeforeDelete - x + 1, assignSize())

        checkIfUndoNotDisplayed()
    }

    @Test
    fun testSingleUndo() {
        getIntoBackpack()

        val sizeBeforeDelete = assignSize()

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText(R.string.delete)).perform(click())

        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(0)
            .performCheckItemClick()
        onView(withId(R.id.confirm)).perform(click())

        onView(allOf(withId(android.R.id.button1), withText(R.string.delete)))
            .check(matches(isDisplayed()))

        onView(allOf(withId(android.R.id.button1), withText(R.string.delete)))
            .perform(click())

        onView(withId(R.id.menu_undo)).perform(click())

        Assert.assertEquals(sizeBeforeDelete, assignSize())

        checkIfUndoNotDisplayed()
    }

    @Test
    fun testUndoOnAllDeleted() {
        getIntoBackpack()

        val sizeBeforeDelete = assignSize()

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText(R.string.delete)).perform(click())

        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(0)
            .performCheckItemClick()
        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(1)
            .performCheckItemClick()
        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(2)
            .performCheckItemClick()
        onView(withId(R.id.confirm)).perform(click())

        onView(allOf(withId(android.R.id.button1), withText(R.string.delete)))
            .check(matches(isDisplayed()))

        onView(allOf(withId(android.R.id.button1), withText(R.string.delete)))
            .perform(click())

        onView(withId(R.id.menu_undo)).perform(click())

        Assert.assertEquals(sizeBeforeDelete, assignSize())

        checkIfUndoNotDisplayed()
    }

    @Test
    fun testUndoNotPossibleAfterReturning() {
        getIntoBackpack()

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText(R.string.delete)).perform(click())

        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(0)
            .performCheckItemClick()
        onView(withId(R.id.confirm)).perform(click())

        onView(allOf(withId(android.R.id.button1), withText(R.string.delete)))
            .check(matches(isDisplayed()))

        onView(allOf(withId(android.R.id.button1), withText(R.string.delete)))
            .perform(click())

        val savedBackpack = backpackManager.backpack
        pressBack()
        backpackManager.backpack = savedBackpack

        getIntoBackpack()

        checkIfUndoNotDisplayed()
    }

    private fun addObjectToBackpack() {
        when (activityType) {
            ActivityType.SPRITE -> {
                when (fragmentId) {
                    SpriteActivity.FRAGMENT_LOOKS -> {
                        lookData = LookData("test", imageFile)
                        backpackManager.backpackedLooks.add(lookData)
                        backpackManager.backpackedLooks.add(lookData)
                        backpackManager.backpackedLooks.add(lookData)
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
                        backpackManager.addScriptToBackPack("start", scriptGroup)
                        backpackManager.addScriptToBackPack("start1", scriptGroup)
                        backpackManager.addScriptToBackPack("start2", scriptGroup)
                    }

                    else -> {
                        throw IllegalArgumentException("Unknown fragmentId for SpriteActivity: $fragmentId")
                    }
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
                        val scene2 = Scene("Scene2", project)
                        backpackManager.scenes.add(scene2)
                        backpackManager.scenes.add(scene2)
                        backpackManager.scenes.add(scene2)
                    }

                    else -> {
                        throw IllegalArgumentException("Unknown fragmentId for ProjectActivity: $fragmentId")
                    }
                }
                backpackManager.saveBackpack()
            }
        }
    }
}
