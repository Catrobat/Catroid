package org.catrobat.catroid.content.actions

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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

    @Test
    fun currentActionCanBeRecoveredAfterPickerReturns() {
        val action = FaceNameTrainAction()

        FaceNameTrainAction.currentInstance = action

        assertSame(action, FaceNameTrainAction.currentInstance)
    }

    @Test
    fun duplicatePickerResultIsIgnoredDuringTraining() {
        FaceNameTrainAction.setPendingNameForTest("Person A")
        FaceNameTrainAction.setTrainingForTest(true)

        FaceNameTrainAction().handleResult(
            FaceNameTrainAction.REQUEST_FIRST,
            -1,
            Intent().setData(android.net.Uri.parse("file:///duplicate.jpg"))
        )

        assertTrue(FaceNameTrainAction.isTrainingForTest())
        assertEquals("Person A", FaceNameTrainAction.getPendingNameForTest())
    }
}
