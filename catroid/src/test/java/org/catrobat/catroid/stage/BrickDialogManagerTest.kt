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
package org.catrobat.catroid.stage

import android.app.Dialog
import android.content.DialogInterface
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkStatic
import io.mockk.verify
import org.catrobat.catroid.content.actions.FaceNameTrainAction
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Android's Dialog posts its dismiss notice with the dialog behind a weak
 * reference (Dialog.ListenersHandler calls onDismiss(mDialog.get())). When the
 * dialog was garbage collected before the notice runs, the listener gets null.
 *
 * Found on the phone: FaceTrainingUiTest stopped with "NullPointerException:
 * Parameter specified as non-null is null: BrickDialogManager.onDismiss,
 * parameter dialog", which killed the app. These tests make that call directly.
 */
@RunWith(RobolectricTestRunner::class)
class BrickDialogManagerTest {

    private val stageActivity = mockk<StageActivity>(relaxed = true)
    private val manager = BrickDialogManager(stageActivity)
    private val dismissListener: DialogInterface.OnDismissListener = manager

    @After
    fun tearDown() {
        FaceNameTrainAction.resetStateForTest(null)
        unmockkStatic(StageLifeCycleController::class)
    }

    /**
     * Found in a review: tapping a name opens the photo picker from inside the
     * list, and Android closes the list afterwards. With no dialog left, the
     * close resumed the stage behind the picker: sounds, sensors and the camera
     * came back while the user was choosing photos. Not while the picker is open;
     * its result opens the next dialog.
     */
    @Test
    fun closingADialogForThePhotoPickerDoesNotResumeTheStage() {
        mockkStatic(StageLifeCycleController::class)
        every { StageLifeCycleController.stageResume(any()) } just runs
        FaceNameTrainAction.setPickerOpenForTest(true)

        dismissListener.onDismiss(mockk<Dialog>(relaxed = true))

        verify(exactly = 0) { StageLifeCycleController.stageResume(any()) }
    }

    @Test
    fun closingTheLastDialogOtherwiseResumesTheStage() {
        mockkStatic(StageLifeCycleController::class)
        every { StageLifeCycleController.stageResume(any()) } just runs
        FaceNameTrainAction.setPickerOpenForTest(false)

        dismissListener.onDismiss(mockk<Dialog>(relaxed = true))

        verify(exactly = 1) { StageLifeCycleController.stageResume(stageActivity) }
    }

    @Test
    fun aDismissNoticeWithoutItsDialogDoesNotCrash() {
        dismissListener.onDismiss(null)

        assertFalse(manager.dialogIsShowing())
    }

    @Test
    fun aDismissNoticeWithoutItsDialogLeavesTheStageAlone() {
        dismissListener.onDismiss(null)

        // No dialog to account for, so nothing to resume.
        verify(exactly = 0) { stageActivity.isFinishing }
    }

    @Test
    fun aLateDismissNoticeAfterTheStageClosedItsDialogsDoesNotCrash() {
        // dismissAllDialogs() lets go of the dialogs; their notices still arrive.
        manager.dismissAllDialogs()

        dismissListener.onDismiss(null)

        assertFalse(manager.dialogIsShowing())
    }
}
