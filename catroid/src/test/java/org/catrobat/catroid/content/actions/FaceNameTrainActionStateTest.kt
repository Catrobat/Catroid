package org.catrobat.catroid.content.actions

import android.content.Context
import androidx.test.core.app.ApplicationProvider
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
}
