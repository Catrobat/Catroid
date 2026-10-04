package org.catrobat.catroid.content.actions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.catrobat.catroid.FaceRecognizer.Recognizer
import org.catrobat.catroid.bluetooth.BluetoothManager
import org.catrobat.catroid.stage.BrickDialogManager.DialogType
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
import org.robolectric.Shadows.shadowOf
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

            action.onPersonChosen("Ada")

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
        oldBrick.onPersonChosen("Ada")

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

        brick.onPersonChosen("Person $limit")

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

    // ---------------- Menus that show an older list of names ----------------

    /**
     * Found in review: two training menus both list [Alice, Bob]. Alice is
     * deleted through one of them; the other still shows Alice in the first row,
     * which by then is Bob. Choosing Alice there must not add photos to Bob.
     */
    @Test
    fun choosingANameThatWasDeletedMeanwhileGivesNobodyThePhotos() {
        recordDialogs()
        val stage = stage()
        StageActivity.activeStageActivity = WeakReference(stage)
        val brick = runningBrick(listOf("Bob"))

        brick.onPersonChosen("Alice")

        assertNull(FaceNameTrainAction.getPendingNameForTest())
        verify(exactly = 0) { stage.startActivityForResult(any(), any()) }

        brick.onPersonChosen("Bob")

        assertEquals("Bob", FaceNameTrainAction.getPendingNameForTest())
        verify { stage.startActivityForResult(any(), FaceNameTrainAction.REQUEST_FIRST) }
    }

    /** The same with delete: Alice's old row must not delete Bob. */
    @Test
    fun deletingANameThatWasDeletedMeanwhileDeletesNobodyElse() {
        recordDialogs()
        StageActivity.activeStageActivity = WeakReference(stage())
        val recognizer = recognizer(listOf("Bob"))
        val brick = runningBrick(recognizer)

        brick.onDeleteTargetChosen("Alice")
        brick.onDeleteConfirmed("Alice")

        verify(exactly = 0) { recognizer.deletePerson(any<Int>()) }
        verify(exactly = 0) { recognizer.deletePerson("Bob") }
    }

    // ---------------- Training that ends after the stage changed ----------------

    /**
     * Found in review: training runs on, the user leaves the program and opens
     * another one. When training ends, its brick no longer runs; the result must
     * not open the training menu on the other program's stage and pause it.
     */
    @Test
    fun trainingThatEndsOnAnotherStageOpensNoMenuThere() {
        val shown = recordDialogs()
        val oldStage = stage()
        StageActivity.activeStageActivity = WeakReference(oldStage)
        val recognizer = recognizer(listOf("Ada"))
        every {
            recognizer.extractEmbeddings(any(), any(), any<Recognizer.ProgressListener>())
        } returns Recognizer.EnrolResult().apply { embeddings.add(FloatArray(EMBEDDING_SIZE)) }
        val brick = runningBrick(recognizer)
        FaceNameTrainAction::class.java.getDeclaredField("runsOn").apply { isAccessible = true }
            .set(brick, WeakReference(oldStage))
        FaceNameTrainAction.currentInstance = brick
        FaceNameTrainAction.setPendingNameForTest("Ada")

        brick.handleResult(
            FaceNameTrainAction.REQUEST_FIRST,
            StageActivity.RESULT_OK,
            Intent().setData(Uri.parse("content://photos/1"))
        )
        val shownOnTheOldStage = shown.size
        StageActivity.activeStageActivity = WeakReference(stage())
        waitForTrainingToFinish()

        val shownLater = shown.drop(shownOnTheOldStage).map { it[0] }
        assertFalse(
            "No training menu on the other program's stage: $shownLater",
            DialogType.FACE_TRAIN_MENU in shownLater
        )
        verify { recognizer.addEmbeddings(any(), any()) }
    }

    private fun waitForTrainingToFinish() {
        val deadline = System.currentTimeMillis() + TRAINING_TIMEOUT_MS
        while (FaceNameTrainAction.isTrainingForTest() && System.currentTimeMillis() < deadline) {
            shadowOf(Looper.getMainLooper()).idle()
            Thread.sleep(POLL_MS)
        }
        assertFalse("Training did not finish", FaceNameTrainAction.isTrainingForTest())
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

    private companion object {
        const val EMBEDDING_SIZE = 128
        const val TRAINING_TIMEOUT_MS = 5000L
        const val POLL_MS = 10L
    }
}
