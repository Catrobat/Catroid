package org.catrobat.catroid.content.actions

import android.content.ActivityNotFoundException
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.catrobat.catroid.FaceRecognizer.Recognizer
import org.catrobat.catroid.bluetooth.BluetoothManager
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageResourceHolder
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.lang.ref.WeakReference

@RunWith(RobolectricTestRunner::class)
class FaceNameTrainActionStateTest {

    private lateinit var context: Context
    private var savedStage: WeakReference<StageActivity>? = null
    private var savedHandler: Handler? = null

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        FaceNameTrainAction.resetStateForTest(context)
        savedStage = StageActivity.activeStageActivity
        savedHandler = StageActivity.messageHandler
    }

    @After
    fun tearDown() {
        StageActivity.activeStageActivity = savedStage
        StageActivity.messageHandler = savedHandler
        FaceNameTrainAction.resetStateForTest(null)
    }

    @Test
    fun requestCodeRangeBelongsOnlyToFaceTraining() {
        assertTrue(FaceNameTrainAction.ownsRequestCode(FaceNameTrainAction.REQUEST_FIRST))
        assertTrue(FaceNameTrainAction.ownsRequestCode(FaceNameTrainAction.REQUEST_LAST))
        assertFalse(FaceNameTrainAction.ownsRequestCode(FaceNameTrainAction.REQUEST_FIRST - 1))
        assertFalse(FaceNameTrainAction.ownsRequestCode(FaceNameTrainAction.REQUEST_LAST + 1))
    }

    /**
     * StageResourceHolder.onActivityResult hands the face training codes over
     * before its own cases. Overlapping the Bluetooth device picker's code sent
     * every robot project's connection result to face training, and the stage
     * never finished loading.
     */
    @Test
    fun theStagesOwnRequestCodesAreNotFaceTrainingCodes() {
        val stageCodes = mapOf(
            "REQUEST_CONNECT_DEVICE" to stageResourceHolderCode("REQUEST_CONNECT_DEVICE"),
            "REQUEST_GPS" to stageResourceHolderCode("REQUEST_GPS"),
            "REQUEST_ENABLE_BT" to BluetoothManager.REQUEST_ENABLE_BT,
            "REQUEST_START_STAGE" to StageActivity.REQUEST_START_STAGE
        )
        for ((name, code) in stageCodes) {
            assertFalse("$name ($code) is taken by face training", FaceNameTrainAction.ownsRequestCode(code))
        }
    }

    /**
     * Some phones have no app for ACTION_OPEN_DOCUMENT (a disabled or missing
     * document picker). Tapping a name then threw ActivityNotFoundException on
     * the main thread and ended the app.
     */
    @Test
    fun noAppToChoosePhotosDoesNotEndTheApp() {
        val stage = mockk<StageActivity>(relaxed = true)
        every { stage.isFinishing } returns false
        every { stage.isDestroyed } returns false
        every { stage.startActivityForResult(any(), any()) } throws ActivityNotFoundException("no document picker")
        val recognizer = mockk<Recognizer>(relaxed = true)
        every { recognizer.classNames } returns listOf("Ada")
        val previousStage = StageActivity.activeStageActivity
        StageActivity.activeStageActivity = WeakReference(stage)
        try {
            val action = FaceNameTrainAction()
            FaceNameTrainAction::class.java.getDeclaredField("recognizer").apply { isAccessible = true }
                .set(action, recognizer)

            action.onPersonChosen(0)

            assertNull("No photos are expected for Ada", FaceNameTrainAction.getPendingNameForTest())
        } finally {
            StageActivity.activeStageActivity = previousStage
        }
    }

    /**
     * Android may destroy the stage while the photo picker is open and create a
     * new one. The brick that opened the picker belonged to the old stage, and
     * nothing finishes it, so it still looked like it was running and took the
     * result: the brick of the new run kept waiting with no dialog.
     */
    @Test
    fun aPickerResultForAClosedStageGoesToTheBrickOfTheNewStage() {
        val shown = recordDialogs()
        val oldStage = stage()
        StageActivity.activeStageActivity = WeakReference(oldStage)
        val oldBrick = runningBrick(listOf("Ada"))
        oldBrick.onPersonChosen(0)

        StageActivity.activeStageActivity = WeakReference(stage())
        val newBrick = runningBrick(listOf("Ada"))
        FaceNameTrainAction.currentInstance = newBrick

        FaceNameTrainAction.onPickerResult(FaceNameTrainAction.REQUEST_FIRST, StageActivity.RESULT_CANCELED, null)

        assertSame("The brick of the new stage shows the names again", newBrick, shown.last()[1])
    }

    /** One request code per person: a person past the range cannot be given photos. */
    @Test
    fun aNameBeyondTheRequestCodesIsNotAdded() {
        recordDialogs()
        val stage = stage()
        StageActivity.activeStageActivity = WeakReference(stage)
        val limit = FaceNameTrainAction.REQUEST_LAST - FaceNameTrainAction.REQUEST_FIRST + 1
        val recognizer = recognizer(List(limit) { "Person $it" })
        every { recognizer.addPerson(any()) } returns limit
        val brick = runningBrick(recognizer)

        brick.onNewName("Grace")

        verify(exactly = 0) { recognizer.addPerson(any()) }
        verify(exactly = 0) { stage.startActivityForResult(any(), any()) }
    }

    @Test
    fun aPersonBeyondTheRequestCodesDoesNotOpenThePicker() {
        recordDialogs()
        val stage = stage()
        StageActivity.activeStageActivity = WeakReference(stage)
        val limit = FaceNameTrainAction.REQUEST_LAST - FaceNameTrainAction.REQUEST_FIRST + 1
        val brick = runningBrick(List(limit + 1) { "Person $it" })

        brick.onPersonChosen(limit)

        verify(exactly = 0) { stage.startActivityForResult(any(), any()) }
    }

    /** The name dialog checks names, but the recognizer has the last word; it must not end the app. */
    @Test
    fun aNameTheRecognizerRejectsDoesNotEndTheApp() {
        val shown = recordDialogs()
        StageActivity.activeStageActivity = WeakReference(stage())
        val recognizer = recognizer(emptyList())
        every { recognizer.addPerson(any()) } throws IllegalArgumentException("Person name must not be blank")
        val brick = runningBrick(recognizer)

        brick.onNewName(" ")

        assertSame(brick, shown.last()[1])
    }

    /** Photos from the FaceNet build cannot match; the menu asks for them again. */
    @Test
    fun theMenuAsksForPhotosAgainWhileTheRecognizerNeedsRetraining() {
        val recognizer = recognizer(listOf("Ada"))
        every { recognizer.needsRetraining() } returns true
        assertTrue(runningBrick(recognizer).needsRetraining())

        every { recognizer.needsRetraining() } returns false
        assertFalse(runningBrick(recognizer).needsRetraining())
    }

    private fun stage(): StageActivity {
        val stage = mockk<StageActivity>(relaxed = true)
        every { stage.isFinishing } returns false
        every { stage.isDestroyed } returns false
        every { stage.applicationContext } returns context
        return stage
    }

    private fun recognizer(names: List<String>): Recognizer {
        val recognizer = mockk<Recognizer>(relaxed = true)
        every { recognizer.classNames } returns names
        return recognizer
    }

    private fun runningBrick(names: List<String>): FaceNameTrainAction = runningBrick(recognizer(names))

    /** A brick whose dialogs are open; act() would load the real face models. */
    private fun runningBrick(recognizer: Recognizer): FaceNameTrainAction {
        val brick = FaceNameTrainAction()
        FaceNameTrainAction::class.java.getDeclaredField("recognizer").apply { isAccessible = true }
            .set(brick, recognizer)
        FaceNameTrainAction::class.java.getDeclaredField("started").apply { isAccessible = true }
            .setBoolean(brick, true)
        return brick
    }

    /** The dialogs the bricks ask the stage for: (type, brick, content). */
    private fun recordDialogs(): MutableList<List<*>> {
        val shown = mutableListOf<List<*>>()
        StageActivity.messageHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(message: Message) {
                shown.add(message.obj as List<*>)
            }
        }
        return shown
    }

    private fun stageResourceHolderCode(name: String): Int =
        StageResourceHolder::class.java.getDeclaredField(name).apply { isAccessible = true }.getInt(null)

    @Test
    fun pickerStateSurvivesActionReset() {
        FaceNameTrainAction.setPendingNameForTest("Person A")
        FaceNameTrainAction.setTrainingForTest(true)

        FaceNameTrainAction().reset()

        assertEquals("Person A", FaceNameTrainAction.getPendingNameForTest())
        assertTrue(FaceNameTrainAction.isTrainingForTest())
    }

    /**
     * StageResourceHolder.onActivityResult hands the picker result to
     * FaceNameTrainAction.currentInstance. Running the brick is what must
     * register the action there, and the most recent brick run must win.
     * (The duplicate-picker-result check moved to FaceTrainingUiTest, where a
     * real training run gives it a positive control.)
     */
    @Test
    fun runningTheBrickRegistersTheActionForThePickerResult() {
        assertNull(FaceNameTrainAction.currentInstance)

        val first = FaceNameTrainAction()
        first.act(0f)
        assertSame(first, FaceNameTrainAction.currentInstance)

        val second = FaceNameTrainAction()
        second.act(0f)
        assertSame(second, FaceNameTrainAction.currentInstance)
    }

    /**
     * The brick holds the script only while its dialogs can be shown. With no
     * stage (no StageActivity, no message handler) it must finish instead of
     * blocking the script forever. Holding until Done is checked on the real
     * stage in FaceTrainingUiTest.
     */
    @Test
    fun withoutAStageTheBrickDoesNotBlockTheScript() {
        val action = FaceNameTrainAction()

        assertTrue(action.act(0f))
        assertTrue(action.act(0f))
    }
}
