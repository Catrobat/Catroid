package org.catrobat.catroid.content.actions

import android.content.ActivityNotFoundException
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockk
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

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        FaceNameTrainAction.resetStateForTest(context)
    }

    @After
    fun tearDown() {
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
