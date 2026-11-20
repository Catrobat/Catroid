package org.catrobat.catroid.content.actions

import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.UriPermission
import android.content.res.Resources
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.ImageProxy
import androidx.test.core.app.ApplicationProvider
import com.google.mlkit.vision.common.InputImage
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.catrobat.catroid.facerecognition.FaceNameWindow
import org.catrobat.catroid.facerecognition.Recognizer
import org.catrobat.catroid.facerecognition.env.FileUtils
import org.catrobat.catroid.R
import org.catrobat.catroid.bluetooth.BluetoothManager
import org.catrobat.catroid.camera.DetectorsCompleteListener
import org.catrobat.catroid.camera.mlkitdetectors.FaceNameDetector
import org.catrobat.catroid.stage.BrickDialogManager.DialogType
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageResourceHolder
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowToast
import java.io.IOException
import java.lang.ref.WeakReference
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
class FaceNameTrainActionStateTest {

    private lateinit var context: Context
    private var savedStage: WeakReference<StageActivity>? = null
    private var savedHandler: Handler? = null
    private var restoreDetector: () -> Unit = {}

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
        restoreDetector()
    }

    @Test
    fun theRequestCodeBelongsOnlyToFaceTraining() {
        assertTrue(FaceNameTrainAction.ownsRequestCode(FaceNameTrainAction.REQUEST_CODE))
        assertFalse(FaceNameTrainAction.ownsRequestCode(FaceNameTrainAction.REQUEST_CODE - 1))
        assertFalse(FaceNameTrainAction.ownsRequestCode(FaceNameTrainAction.REQUEST_CODE + 1))
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

        val newStage = stage()
        StageActivity.activeStageActivity = WeakReference(newStage)
        val newBrick = runningBrick(listOf("Ada"))
        runsOn(newBrick, newStage)
        FaceNameTrainAction.currentInstance = newBrick

        FaceNameTrainAction.onPickerResult(FaceNameTrainAction.REQUEST_CODE, StageActivity.RESULT_CANCELED, null)

        assertSame("The brick of the new stage shows the names again", newBrick, shown.last()[1])
    }

    /**
     * Found in review: one request code per person allowed only 900 people.
     * The result is matched to its person by name, so any person gets photos.
     */
    @Test
    fun aPersonAfterNineHundredOthersStillGetsThePicker() {
        recordDialogs()
        val stage = stage()
        StageActivity.activeStageActivity = WeakReference(stage)
        val brick = runningBrick(List(MANY_PEOPLE + 1) { "Person $it" })

        brick.onPersonChosen("Person $MANY_PEOPLE")

        assertEquals("Person $MANY_PEOPLE", FaceNameTrainAction.getPendingNameForTest())
        verify { stage.startActivityForResult(any(), FaceNameTrainAction.REQUEST_CODE) }
    }

    /** The name dialog checks names, but the recognizer's rules have the last word; it must not end the app. */
    @Test
    fun aNameTheRecognizerRejectsDoesNotEndTheApp() {
        val shown = recordDialogs()
        val stage = stage()
        StageActivity.activeStageActivity = WeakReference(stage)
        val brick = runningBrick(recognizer(emptyList()))
        runsOn(brick, stage)

        brick.onNewName(" ")

        waitUntil { menusShownFor(shown, brick) == 1 }
        assertSame(brick, shown.last()[1])
        verify(exactly = 0) { stage.startActivityForResult(any(), any()) }
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
        verify { stage.startActivityForResult(any(), FaceNameTrainAction.REQUEST_CODE) }
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

    // ---------------- Photos with several people ----------------

    @Test
    fun onlyPhotosWithSeveralPeopleAskForPhotosOfThisPersonOnly() {
        assertEquals(
            context.getString(R.string.face_train_several_people),
            trainingMessage(Recognizer.EnrolResult().apply { severalPeople = 2 })
        )
    }

    @Test
    fun skippedPhotosWithSeveralPeopleAreMentionedAfterSuccessfulTraining() {
        assertEquals(
            context.getString(R.string.face_train_success_some_skipped),
            trainingMessage(
                Recognizer.EnrolResult().apply {
                    embeddings.add(FloatArray(EMBEDDING_SIZE))
                    severalPeople = 1
                }
            )
        )
    }

    /** The message training shows for [result]; no brick runs, so it is a plain toast. */
    private fun trainingMessage(result: Recognizer.EnrolResult): String? {
        recordDialogs()
        val stage = stage()
        StageActivity.activeStageActivity = WeakReference(stage)
        val recognizer = recognizer(listOf("Ada"))
        every { recognizer.extractEmbeddings(any(), any(), any<Recognizer.ProgressListener>()) } returns result
        val trainer = runningBrick(recognizer)
        runsOn(trainer, stage)
        FaceNameTrainAction.setPendingNameForTest("Ada")
        trainer.handleResult(
            FaceNameTrainAction.REQUEST_CODE, StageActivity.RESULT_OK,
            Intent().setData(Uri.parse("content://photos/1"))
        )
        StageActivity.activeStageActivity = WeakReference(stage())
        waitForTrainingToFinish()
        shadowOf(Looper.getMainLooper()).idle()
        return ShadowToast.getTextOfLatestToast()
    }

    // ---------------- Leftovers of training ----------------

    /**
     * Found in a review of this feature: the message for the result was built
     * after the training step's finally block. If building it threw (a broken
     * translation, say), training stayed marked as running for the rest of the
     * process, and every later training brick showed a progress dialog forever.
     */
    @Test
    fun trainingEndsEvenWhenItsMessageCannotBeBuilt() {
        recordDialogs()
        val brokenStrings = spyk(context)
        every { brokenStrings.applicationContext } returns brokenStrings
        every { brokenStrings.getString(any()) } throws Resources.NotFoundException("broken translation")
        every { brokenStrings.getString(any(), *anyVararg()) } throws Resources.NotFoundException("broken translation")
        FaceNameTrainAction.resetStateForTest(brokenStrings)
        val stage = stage()
        every { stage.applicationContext } returns brokenStrings
        StageActivity.activeStageActivity = WeakReference(stage)
        val recognizer = recognizer(listOf("Ada"))
        every {
            recognizer.extractEmbeddings(any(), any(), any<Recognizer.ProgressListener>())
        } returns Recognizer.EnrolResult()
        val brick = runningBrick(recognizer)
        runsOn(brick, stage)
        FaceNameTrainAction.setPendingNameForTest("Ada")

        brick.handleResult(
            FaceNameTrainAction.REQUEST_CODE, StageActivity.RESULT_OK,
            Intent().setData(Uri.parse("content://photos/1"))
        )

        waitForTrainingToFinish()
    }

    /**
     * Found in a review of this feature: a picker result that no brick took was
     * kept for the next training brick, also after the user had left the stage.
     * A later, unrelated program then trained those photos under the old name.
     */
    @Test
    fun aPickerResultIsDroppedWhenTheUserLeavesTheStage() {
        val shown = recordDialogs()
        val stage = stage()
        StageActivity.activeStageActivity = WeakReference(stage)
        FaceNameTrainAction.setPendingNameForTest("Ada")
        FaceNameTrainAction.onPickerResult(
            FaceNameTrainAction.REQUEST_CODE, StageActivity.RESULT_OK,
            Intent().setData(Uri.parse("content://photos/1"))
        )

        FaceNameTrainAction.onStageLeft()
        val later = runningBrick(listOf("Ada")).also { runsOn(it, stage) }
        showCurrentScreen(later)
        shadowOf(Looper.getMainLooper()).idle()

        assertNull(FaceNameTrainAction.getPendingNameForTest())
        assertFalse(FaceNameTrainAction.isTrainingForTest())
        assertEquals(1, menusShownFor(shown, later))
    }

    /**
     * Found in review: the face models (two interpreters and the 4 MB model)
     * stayed in memory after the user left a program that used face names, so
     * Android was more likely to stop the app on a phone with little memory.
     */
    @Test
    fun theFaceModelsAreReleasedWhenTheUserLeavesTheStage() {
        val released = CountDownLatch(1)
        val releasedOn = LinkedBlockingQueue<Thread>()
        FaceNameTrainAction.releaseModels = { _ ->
            releasedOn.add(Thread.currentThread())
            released.countDown()
        }

        FaceNameTrainAction.onStageLeft()

        assertTrue("The models were not released", released.await(TRAINING_TIMEOUT_MS, TimeUnit.MILLISECONDS))
        assertNotSame("Releasing waits for the models; not on the main thread", Looper.getMainLooper().thread, releasedOn.poll())
    }

    @Test
    fun theFaceModelsStayWhileTrainingRuns() {
        var released = false
        FaceNameTrainAction.releaseModels = { _ -> released = true }
        FaceNameTrainAction.setTrainingForTest(true)

        FaceNameTrainAction.onStageLeft()
        Thread.sleep(LOADING_POLLS * POLL_MS)

        assertFalse(released)
    }

    /**
     * Found in a review of this feature: the photos' read permissions are kept
     * while training reads them and released when it ends. If the app was
     * stopped during training they stayed for good, and Android allows only a
     * limited number. They are released the next time the models load.
     */
    @Test
    fun photoPermissionsLeftByAnEarlierProcessAreReleased() {
        val leftover = Uri.parse("content://photos/7")
        val permission = mockk<UriPermission>()
        every { permission.uri } returns leftover
        every { permission.isReadPermission } returns true
        val resolver = mockk<ContentResolver>(relaxed = true)
        every { resolver.persistedUriPermissions } returns listOf(permission)

        FaceNameTrainAction.releaseLeftoverPhotoPermissions(resolver)

        verify { resolver.releasePersistableUriPermission(leftover, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
    }

    @Test
    fun photoPermissionsOfTrainingThatStillRunsAreKept() {
        val permission = mockk<UriPermission>()
        every { permission.uri } returns Uri.parse("content://photos/7")
        every { permission.isReadPermission } returns true
        val resolver = mockk<ContentResolver>(relaxed = true)
        every { resolver.persistedUriPermissions } returns listOf(permission)
        FaceNameTrainAction.setTrainingForTest(true)

        FaceNameTrainAction.releaseLeftoverPhotoPermissions(resolver)

        verify(exactly = 0) { resolver.releasePersistableUriPermission(any(), any()) }
    }

    // ---------------- A new name is stored with its photos ----------------

    /**
     * Found in review: a new name was saved before the photo picker opened, so
     * backing out of the picker, or choosing photos without a usable face, left
     * a name with no photos. The name waits in pendingName; training stores it
     * together with its photos.
     */
    @Test
    fun aNewNameIsStoredOnlyWithItsPhotos() {
        recordDialogs()
        val stage = stage()
        StageActivity.activeStageActivity = WeakReference(stage)
        val recognizer = recognizer(listOf("Ada"))
        val brick = runningBrick(recognizer)
        runsOn(brick, stage)

        brick.onNewName("Grace")

        assertEquals("Grace", FaceNameTrainAction.getPendingNameForTest())
        verify { stage.startActivityForResult(any(), FaceNameTrainAction.REQUEST_CODE) }

        brick.handleResult(FaceNameTrainAction.REQUEST_CODE, StageActivity.RESULT_CANCELED, null)
        shadowOf(Looper.getMainLooper()).idle()

        verify(exactly = 0) { recognizer.addPerson(any()) }
        verify(exactly = 0) { recognizer.addPhotos(any(), any()) }
    }

    @Test
    fun aNewNameWithoutAUsableFaceIsNotStored() {
        recordDialogs()
        val stage = stage()
        StageActivity.activeStageActivity = WeakReference(stage)
        val recognizer = recognizer(listOf("Ada"))
        every {
            recognizer.extractEmbeddings(any(), any(), any<Recognizer.ProgressListener>())
        } returns Recognizer.EnrolResult()
        val brick = runningBrick(recognizer)
        runsOn(brick, stage)

        brick.onNewName("Grace")
        brick.handleResult(
            FaceNameTrainAction.REQUEST_CODE, StageActivity.RESULT_OK,
            Intent().setData(Uri.parse("content://photos/1"))
        )
        waitForTrainingToFinish()

        verify(exactly = 0) { recognizer.addPerson(any()) }
        verify(exactly = 0) { recognizer.addPhotos(any(), any()) }
    }

    // ---------------- Deleting off the main thread ----------------

    /**
     * Found in review: deleting a name saved the face files from the dialog's
     * click handler, with several file and folder syncs on the main thread.
     * With many photos stored, or slow storage, the dialog froze.
     */
    @Test
    fun aDeleteIsSavedOffTheMainThreadThenTheMenuOpens() {
        val shown = recordDialogs()
        val stage = stage()
        StageActivity.activeStageActivity = WeakReference(stage)
        val recognizer = recognizer(listOf("Ada"))
        val savedOn = LinkedBlockingQueue<Thread>()
        every { recognizer.deletePerson("Ada") } answers {
            savedOn.add(Thread.currentThread())
            true
        }
        val brick = runningBrick(recognizer)
        runsOn(brick, stage)

        brick.onDeleteConfirmed("Ada")

        val thread = savedOn.poll(TRAINING_TIMEOUT_MS, TimeUnit.MILLISECONDS)
        assertNotNull("The delete was not saved", thread)
        assertNotSame(Looper.getMainLooper().thread, thread)
        waitUntil { menusShownFor(shown, brick) == 1 }
    }

    /**
     * Found in review: only IOException and IllegalArgumentException came back
     * to the main thread. Any other error left the save counted as running, so
     * the stage stayed paused with no dialog.
     */
    @Test
    fun aDeleteThatFailsUnexpectedlyStillReopensTheMenu() {
        val shown = recordDialogs()
        val stage = stage()
        StageActivity.activeStageActivity = WeakReference(stage)
        val recognizer = recognizer(listOf("Ada"))
        every { recognizer.deletePerson("Ada") } throws IllegalStateException("unexpected")
        val brick = runningBrick(recognizer)
        runsOn(brick, stage)

        brick.onDeleteConfirmed("Ada")

        waitUntil { menusShownFor(shown, brick) == 1 }
        assertFalse(FaceNameTrainAction.keepsStagePaused())
    }

    /**
     * Found in review: deletes shared the single training thread, so a delete in
     * one brick waited for another brick's whole training batch, with no dialog.
     */
    @Test
    fun aDeleteDoesNotWaitForTraining() {
        val shown = recordDialogs()
        val stage = stage()
        StageActivity.activeStageActivity = WeakReference(stage)
        val training = CountDownLatch(1)
        val trainer = recognizer(listOf("Ada", "Grace"))
        every {
            trainer.extractEmbeddings(any(), any(), any<Recognizer.ProgressListener>())
        } answers {
            training.await(TRAINING_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            Recognizer.EnrolResult()
        }
        val trainingBrick = runningBrick(trainer)
        runsOn(trainingBrick, stage)
        FaceNameTrainAction.setPendingNameForTest("Ada")
        trainingBrick.handleResult(
            FaceNameTrainAction.REQUEST_CODE, StageActivity.RESULT_OK,
            Intent().setData(Uri.parse("content://photos/1"))
        )
        val deleted = CountDownLatch(1)
        val deleter = recognizer(listOf("Ada", "Grace"))
        every { deleter.deletePerson("Grace") } answers {
            deleted.countDown()
            true
        }
        val deletingBrick = runningBrick(deleter)
        runsOn(deletingBrick, stage)

        try {
            deletingBrick.onDeleteConfirmed("Grace")

            assertTrue("The delete waited for training", deleted.await(TRAINING_TIMEOUT_MS, TimeUnit.MILLISECONDS))
            waitUntil { menusShownFor(shown, deletingBrick) == 1 }
        } finally {
            training.countDown()
            waitForTrainingToFinish()
        }
    }

    /** The user left the program while the name was deleted: nothing opens on the next stage. */
    @Test
    fun aDeleteSavedAfterTheStageClosedOpensNoMenu() {
        val shown = recordDialogs()
        val stage = stage()
        StageActivity.activeStageActivity = WeakReference(stage)
        val recognizer = recognizer(listOf("Ada"))
        val saving = CountDownLatch(1)
        val saved = CountDownLatch(1)
        every { recognizer.deletePerson("Ada") } answers {
            saving.await(TRAINING_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            saved.countDown()
            true
        }
        val brick = runningBrick(recognizer)
        runsOn(brick, stage)

        brick.onDeleteConfirmed("Ada")
        StageActivity.activeStageActivity = WeakReference(stage())
        saving.countDown()

        assertTrue(saved.await(TRAINING_TIMEOUT_MS, TimeUnit.MILLISECONDS))
        repeat(LOADING_POLLS) { shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(POLL_MS)) }
        assertEquals(0, menusShownFor(shown, brick))
        assertFalse(FaceNameTrainAction.keepsStagePaused())
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
            FaceNameTrainAction.REQUEST_CODE,
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
        verify { recognizer.addPhotos("Ada", any()) }
    }

    /**
     * Found in review: training runs on while the user leaves the program and
     * opens it again, and the new run has two scripts with a training brick.
     * Both show the progress dialog. When training ended, only the last one was
     * closed; the other, which has no buttons, kept the stage paused for good.
     * Every progress dialog closes, and every brick gets its name list back.
     */
    @Test
    fun trainingRecoveredByTwoBricksClosesBothProgressDialogs() {
        val shown = recordDialogs()
        val oldStage = stage()
        StageActivity.activeStageActivity = WeakReference(oldStage)
        val recognizer = recognizer(listOf("Ada"))
        val trainingMayEnd = CountDownLatch(1)
        every {
            recognizer.extractEmbeddings(any(), any(), any<Recognizer.ProgressListener>())
        } answers {
            trainingMayEnd.await(TRAINING_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            Recognizer.EnrolResult().apply { embeddings.add(FloatArray(EMBEDDING_SIZE)) }
        }
        val trainer = runningBrick(recognizer)
        runsOn(trainer, oldStage)
        FaceNameTrainAction.setPendingNameForTest("Ada")
        trainer.handleResult(
            FaceNameTrainAction.REQUEST_CODE,
            StageActivity.RESULT_OK,
            Intent().setData(Uri.parse("content://photos/1"))
        )

        // The program is opened again while training runs: two training bricks.
        val newStage = stage()
        StageActivity.activeStageActivity = WeakReference(newStage)
        val first = runningBrick(recognizer).also { runsOn(it, newStage) }
        val second = runningBrick(recognizer).also { runsOn(it, newStage) }
        FaceNameTrainAction.currentInstance = second
        val firstDialog = mockk<AlertDialog>(relaxed = true)
        val secondDialog = mockk<AlertDialog>(relaxed = true)
        showCurrentScreen(first)
        showCurrentScreen(second)
        first.onProgressDialogShown(firstDialog, mockk<ProgressBar>(relaxed = true))
        second.onProgressDialogShown(secondDialog, mockk<ProgressBar>(relaxed = true))

        trainingMayEnd.countDown()
        waitForTrainingToFinish()

        verify { firstDialog.dismiss() }
        verify { secondDialog.dismiss() }
        assertEquals("The first brick shows its names again, once", 1, menusShownFor(shown, first))
        assertEquals("The second brick shows its names again, once", 1, menusShownFor(shown, second))
    }

    /**
     * Found in review: Android calls a dialog's onShow listener later, from the
     * message queue. When training ended in between, the brick whose progress
     * dialog was still opening was not counted as waiting; its late callback
     * only closed the dialog, so the brick got no name list and its script
     * waited for ever. Every brick that asked for the progress dialog gets
     * exactly one name list, whenever its dialog appears.
     */
    @Test
    fun aProgressDialogThatAppearsAfterTrainingEndedStillGivesItsBrickTheNames() {
        val shown = recordDialogs()
        val oldStage = stage()
        StageActivity.activeStageActivity = WeakReference(oldStage)
        val recognizer = recognizer(listOf("Ada"))
        val trainingMayEnd = CountDownLatch(1)
        every {
            recognizer.extractEmbeddings(any(), any(), any<Recognizer.ProgressListener>())
        } answers {
            trainingMayEnd.await(TRAINING_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            Recognizer.EnrolResult().apply { embeddings.add(FloatArray(EMBEDDING_SIZE)) }
        }
        val trainer = runningBrick(recognizer)
        runsOn(trainer, oldStage)
        FaceNameTrainAction.setPendingNameForTest("Ada")
        trainer.handleResult(
            FaceNameTrainAction.REQUEST_CODE,
            StageActivity.RESULT_OK,
            Intent().setData(Uri.parse("content://photos/1"))
        )
        val newStage = stage()
        StageActivity.activeStageActivity = WeakReference(newStage)
        val first = runningBrick(recognizer).also { runsOn(it, newStage) }
        val second = runningBrick(recognizer).also { runsOn(it, newStage) }
        FaceNameTrainAction.currentInstance = second
        val firstDialog = mockk<AlertDialog>(relaxed = true)
        val secondDialog = mockk<AlertDialog>(relaxed = true)

        // Both ask for the progress dialog; only the second one's has appeared.
        showCurrentScreen(first)
        showCurrentScreen(second)
        second.onProgressDialogShown(secondDialog, mockk<ProgressBar>(relaxed = true))
        trainingMayEnd.countDown()
        waitForTrainingToFinish()
        // Now the first brick's dialog appears.
        first.onProgressDialogShown(firstDialog, mockk<ProgressBar>(relaxed = true))
        shadowOf(Looper.getMainLooper()).idle()

        verify { firstDialog.dismiss() }
        verify { secondDialog.dismiss() }
        assertEquals("The first brick shows its names again, once", 1, menusShownFor(shown, first))
        assertEquals("The second brick shows its names again, once", 1, menusShownFor(shown, second))
    }

    // ---------------- Picker results across a new stage or a restart ----------------

    /**
     * Found in a review: Android recreated the stage while the photo picker was
     * open. The result reached the new stage before the new run's brick had
     * started, and went to the old brick, which opened its name list on the new
     * stage; after Done the new brick opened its own, so the list came twice.
     * The result now waits for the new run's brick, which shows one list.
     */
    @Test
    fun aPickerResultForARecreatedStageWaitsForTheNewRunsBrick() {
        val shown = recordDialogs()
        val oldStage = stage()
        StageActivity.activeStageActivity = WeakReference(oldStage)
        val recognizer = recognizer(listOf("Ada"))
        val oldBrick = runningBrick(recognizer)
        runsOn(oldBrick, oldStage)
        FaceNameTrainAction.currentInstance = oldBrick
        oldBrick.onPersonChosen("Ada")

        val newStage = stage()
        StageActivity.activeStageActivity = WeakReference(newStage)
        FaceNameTrainAction.onProgramStart()
        FaceNameTrainAction.onPickerResult(FaceNameTrainAction.REQUEST_CODE, StageActivity.RESULT_CANCELED, null)
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals("The old brick shows nothing on the new stage", 0, shownFor(shown, oldBrick))

        val newBrick = runningBrick(recognizer).also { runsOn(it, newStage) }
        FaceNameTrainAction.currentInstance = newBrick
        showCurrentScreen(newBrick)
        shadowOf(Looper.getMainLooper()).idle()

        assertEquals("The new brick shows its name list once", 1, menusShownFor(shown, newBrick))
        assertEquals(0, shownFor(shown, oldBrick))
    }

    /**
     * Found in a review: after Restart from the stage menu the old run's brick
     * was never finished and ran on the same stage, so it still took results.
     * A brick of an earlier program run takes none.
     */
    @Test
    fun aBrickFromBeforeARestartTakesNoResults() {
        val shown = recordDialogs()
        val stage = stage()
        StageActivity.activeStageActivity = WeakReference(stage)
        val recognizer = recognizer(listOf("Ada"))
        val before = runningBrick(recognizer)
        runsOn(before, stage)
        FaceNameTrainAction.currentInstance = before
        before.onPersonChosen("Ada")

        FaceNameTrainAction.onProgramStart()
        FaceNameTrainAction.onPickerResult(FaceNameTrainAction.REQUEST_CODE, StageActivity.RESULT_CANCELED, null)
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals("The brick from before the restart shows nothing", 0, shownFor(shown, before))

        val after = runningBrick(recognizer).also { runsOn(it, stage) }
        FaceNameTrainAction.currentInstance = after
        showCurrentScreen(after)
        shadowOf(Looper.getMainLooper()).idle()

        assertEquals(1, menusShownFor(shown, after))
    }

    /**
     * Found in a review: training that ended with no training brick running
     * kept its result and showed it the next time any training brick ran, in
     * any project, maybe much later. The result is shown when training ends.
     */
    @Test
    fun aResultWithNoBrickRunningIsShownWhenTrainingEndsNotLater() {
        val shown = recordDialogs()
        val oldStage = stage()
        StageActivity.activeStageActivity = WeakReference(oldStage)
        val recognizer = recognizer(listOf("Ada"))
        every {
            recognizer.extractEmbeddings(any(), any(), any<Recognizer.ProgressListener>())
        } returns Recognizer.EnrolResult().apply { embeddings.add(FloatArray(EMBEDDING_SIZE)) }
        val trainer = runningBrick(recognizer)
        runsOn(trainer, oldStage)
        FaceNameTrainAction.setPendingNameForTest("Ada")
        trainer.handleResult(
            FaceNameTrainAction.REQUEST_CODE,
            StageActivity.RESULT_OK,
            Intent().setData(Uri.parse("content://photos/1"))
        )
        StageActivity.activeStageActivity = WeakReference(stage())
        val toastsBefore = ShadowToast.shownToastCount()
        waitForTrainingToFinish()
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals("The result is shown when training ends", toastsBefore + 1, ShadowToast.shownToastCount())

        val later = runningBrick(recognizer).also { runsOn(it, StageActivity.activeStageActivity.get()!!) }
        val toastsLater = ShadowToast.shownToastCount()
        showCurrentScreen(later)
        shadowOf(Looper.getMainLooper()).idle()

        assertEquals("A later brick shows no old result", toastsLater, ShadowToast.shownToastCount())
        assertEquals(1, menusShownFor(shown, later))
    }

    /**
     * Found in a review: a stage that is closing, or already destroyed but not
     * yet collected, is still the active one, so its bricks counted as running
     * and took training results. They do not.
     */
    @Test
    fun aBrickOfAClosingStageTakesNoTrainingResult() {
        val shown = recordDialogs()
        val closing = stage()
        every { closing.isFinishing } returns true
        StageActivity.activeStageActivity = WeakReference(closing)
        val recognizer = recognizer(listOf("Ada"))
        every {
            recognizer.extractEmbeddings(any(), any(), any<Recognizer.ProgressListener>())
        } returns Recognizer.EnrolResult().apply { embeddings.add(FloatArray(EMBEDDING_SIZE)) }
        val trainer = runningBrick(recognizer)
        runsOn(trainer, closing)
        FaceNameTrainAction.currentInstance = trainer
        FaceNameTrainAction.setPendingNameForTest("Ada")
        trainer.handleResult(
            FaceNameTrainAction.REQUEST_CODE,
            StageActivity.RESULT_OK,
            Intent().setData(Uri.parse("content://photos/1"))
        )
        val shownBefore = shown.size

        waitForTrainingToFinish()

        assertFalse(
            "No name list for a brick of a closing stage",
            shown.drop(shownBefore).any { it[0] == DialogType.FACE_TRAIN_MENU }
        )
    }

    /**
     * Found in a review: every photo ever picked kept a persisted read
     * permission, until the system limit per app. Training reads the photos
     * at once; their permissions are released when it ends.
     */
    @Test
    fun photoPermissionsAreReleasedWhenTrainingEnds() {
        recordDialogs()
        val stage = stage()
        StageActivity.activeStageActivity = WeakReference(stage)
        val resolver = mockk<ContentResolver>(relaxed = true)
        val appContext = spyk(context)
        every { appContext.applicationContext } returns appContext
        every { appContext.contentResolver } returns resolver
        FaceNameTrainAction.resetStateForTest(appContext)
        val recognizer = recognizer(listOf("Ada"))
        every {
            recognizer.extractEmbeddings(any(), any(), any<Recognizer.ProgressListener>())
        } returns Recognizer.EnrolResult().apply { embeddings.add(FloatArray(EMBEDDING_SIZE)) }
        val trainer = runningBrick(recognizer)
        runsOn(trainer, stage)
        FaceNameTrainAction.setPendingNameForTest("Ada")
        val photo = Uri.parse("content://photos/1")

        trainer.handleResult(FaceNameTrainAction.REQUEST_CODE, StageActivity.RESULT_OK, Intent().setData(photo))
        waitForTrainingToFinish()

        verify { resolver.takePersistableUriPermission(photo, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
        verify { resolver.releasePersistableUriPermission(photo, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
    }

    // ---------------- A brick whose models load while the stage changes ----------------

    /**
     * Found in review: a brick loads the face models on a background thread and
     * then showed its dialog through whichever stage was current by then. If
     * the user left the program, or restarted it, while the models loaded, the
     * old brick opened its name list on the new stage and paused it, and could
     * take a picker result that belongs to the new run. After loading, a brick
     * that no longer runs on the current stage and run shows nothing and takes
     * nothing.
     */
    @Test
    fun aBrickWhoseModelsLoadWhileTheStageChangesShowsNothingThere() {
        assertNothingShownAfterLoading { FaceNameTrainAction.onProgramStart() ; stage() }
    }

    @Test
    fun aBrickWhoseModelsLoadWhileTheProgramRestartsShowsNothing() {
        assertNothingShownAfterLoading { FaceNameTrainAction.onProgramStart() ; null }
    }

    /**
     * Starts a brick whose models load until [change] has happened: a new stage
     * (returned), or a restart on the same stage (null). Then checks the brick
     * showed nothing, and that the waiting picker result is still there for a
     * brick of the new run.
     */
    private fun assertNothingShownAfterLoading(change: () -> StageActivity?) {
        val shown = recordDialogs()
        val firstStage = stage()
        StageActivity.activeStageActivity = WeakReference(firstStage)
        val recognizer = recognizer(listOf("Ada"))
        val loadingMayEnd = CountDownLatch(1)
        val loaded = CountDownLatch(1)
        FaceNameTrainAction.recognizerFactory = {
            loadingMayEnd.await(TRAINING_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            loaded.countDown()
            recognizer
        }
        val obsolete = FaceNameTrainAction()
        obsolete.act(0f)

        val newStage = change() ?: firstStage
        StageActivity.activeStageActivity = WeakReference(newStage)
        FaceNameTrainAction.setPendingNameForTest("Ada")
        FaceNameTrainAction.onPickerResult(FaceNameTrainAction.REQUEST_CODE, StageActivity.RESULT_CANCELED, null)
        loadingMayEnd.countDown()
        assertTrue(loaded.await(TRAINING_TIMEOUT_MS, TimeUnit.MILLISECONDS))
        repeat(LOADING_POLLS) {
            Thread.sleep(POLL_MS)
            shadowOf(Looper.getMainLooper()).idle()
        }
        assertEquals("The obsolete brick shows nothing", 0, shownFor(shown, obsolete))

        val current = runningBrick(recognizer).also { runsOn(it, newStage) }
        FaceNameTrainAction.currentInstance = current
        showCurrentScreen(current)
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals("The new run's brick takes the picker result", 1, menusShownFor(shown, current))
        assertEquals(0, shownFor(shown, obsolete))
    }

    private fun shownFor(shown: List<List<*>>, brick: FaceNameTrainAction): Int =
        shown.count { it[1] === brick }

    /** What a running brick does when its first act() has loaded the models. */
    private fun showCurrentScreen(brick: FaceNameTrainAction) {
        FaceNameTrainAction::class.java.getDeclaredMethod("showCurrentScreen").apply { isAccessible = true }
            .invoke(brick)
    }

    private fun menusShownFor(shown: List<List<*>>, brick: FaceNameTrainAction): Int =
        shown.count { it[0] == DialogType.FACE_TRAIN_MENU && it[1] === brick }

    private fun runsOn(brick: FaceNameTrainAction, stage: StageActivity) {
        FaceNameTrainAction::class.java.getDeclaredField("runsOn").apply { isAccessible = true }
            .set(brick, WeakReference(stage))
    }

    /** Runs the main thread until [condition] holds; the training worker runs meanwhile. */
    private fun waitUntil(condition: () -> Boolean) {
        val deadline = System.currentTimeMillis() + TRAINING_TIMEOUT_MS
        while (!condition() && System.currentTimeMillis() < deadline) {
            shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(POLL_MS))
            Thread.sleep(POLL_MS)
        }
        assertTrue("Timed out", condition())
    }

    private fun waitForTrainingToFinish() {
        val deadline = System.currentTimeMillis() + TRAINING_TIMEOUT_MS
        while (FaceNameTrainAction.isTrainingForTest() && System.currentTimeMillis() < deadline) {
            // Also runs what is posted with a delay (the progress dialog's minimum time).
            shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(POLL_MS))
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

    // ---------------- Detection after the face models failed to load ----------------

    /**
     * Found in review: when loading the face models failed once while the camera
     * was analysed (an exception, or no memory), detection stopped until the next
     * program start, so that the failing load is not repeated on every frame. A
     * training brick that then loaded the models and trained successfully left it
     * stopped: the trained person stayed "Unknown" until the program restarted.
     * Successful manual loading or training must start detection again, while a
     * failed load is still not retried on every frame.
     */
    @Test
    fun detectionResumesWhenTheTrainingBrickLoadsTheModelsAfterAFailedLoad() {
        val recognizer = detectionGivesUpThenModelsWork()
        FaceNameTrainAction.recognizerFactory = { recognizer }

        trainSuccessfully(FaceNameTrainAction())

        assertEquals("Ada", detectedNameAfterFrames())
    }

    @Test
    fun detectionResumesWhenABrickWithLoadedModelsTrainsAfterAFailedLoad() {
        val recognizer = detectionGivesUpThenModelsWork()

        trainSuccessfully(runningBrick(recognizer))

        assertEquals("Ada", detectedNameAfterFrames())
    }

    @Test
    fun withoutSuccessfulTrainingAFailedLoadIsStillNotRetriedOnEveryFrame() {
        detectionGivesUpThenModelsWork()

        assertEquals(FaceNameWindow.UNKNOWN, detectedNameAfterFrames())
        assertEquals("The failed load is not repeated for later frames", 1, modelLoads)
    }

    private val queuedRecognitions = ArrayDeque<Runnable>()
    private var cameraTime = 10_000L
    private var modelLoads = 0
    private var modelsWork = false

    /**
     * The program reads the sensor, the first frame fails to load the models and
     * detection gives up; afterwards the models can be loaded.
     */
    private fun detectionGivesUpThenModelsWork(): Recognizer {
        FileUtils.init(context)
        FileUtils.deleteAll()
        val recognizer = spyk(Recognizer.withoutModelsForTest(context))
        recognizer.addPerson("Ada")
        every { recognizer.scoreFrameForNames(any(), any()) } returns
            Recognizer.ScoredFrame(listOf("Ada"), floatArrayOf(HIGH_SCORE))
        every {
            recognizer.extractEmbeddings(any(), any(), any<Recognizer.ProgressListener>())
        } returns Recognizer.EnrolResult().apply { embeddings.add(FloatArray(EMBEDDING_SIZE)) }
        every { recognizer.addPhotos(any(), any()) } returns 1

        val savedClock = FaceNameDetector.clock
        val savedExecutor = FaceNameDetector.recognitionExecutor
        val savedProvider = FaceNameDetector.recognizerProvider
        restoreDetector = {
            FaceNameDetector.frameSource = null
            FaceNameDetector.clock = savedClock
            FaceNameDetector.recognitionExecutor = savedExecutor
            FaceNameDetector.recognizerProvider = savedProvider
            FaceNameDetector.reset()
            FileUtils.deleteAll()
        }
        FaceNameDetector.clock = { cameraTime }
        FaceNameDetector.recognitionExecutor = Executor { queuedRecognitions.addLast(it) }
        FaceNameDetector.frameSource = { Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888) }
        FaceNameDetector.recognizerProvider = {
            modelLoads++
            if (!modelsWork) {
                throw IOException("mobile_facenet.tflite could not be read")
            }
            recognizer
        }
        FaceNameDetector.reset()
        FaceNameDetector.getNameWithThreshold(DEFAULT_THRESHOLD)

        offerFrame()
        assertEquals("The first frame tried to load the models", 1, modelLoads)
        modelsWork = true
        return recognizer
    }

    private fun trainSuccessfully(brick: FaceNameTrainAction) {
        recordDialogs()
        val stage = stage()
        StageActivity.activeStageActivity = WeakReference(stage)
        runsOn(brick, stage)
        FaceNameTrainAction.setPendingNameForTest("Ada")
        brick.handleResult(
            FaceNameTrainAction.REQUEST_CODE, StageActivity.RESULT_OK,
            Intent().setData(Uri.parse("content://photos/1"))
        )
        waitForTrainingToFinish()
    }

    /** The name after enough camera frames for a decision, without a program restart. */
    private fun detectedNameAfterFrames(): Any? {
        repeat(FaceNameWindow.FRAMES) {
            cameraTime += FaceNameWindow.FRAME_INTERVAL_MS
            offerFrame()
        }
        return FaceNameDetector.getNameWithThreshold(DEFAULT_THRESHOLD)
    }

    /** One camera frame through the detector, recognised right away. */
    private fun offerFrame() {
        FaceNameDetector.processImage(
            mockk<Image>(relaxed = true),
            mockk<InputImage>(relaxed = true),
            DetectorsCompleteListener(1, mockk<ImageProxy>(relaxed = true))
        )
        while (queuedRecognitions.isNotEmpty()) {
            queuedRecognitions.removeFirst().run()
        }
    }

    private companion object {
        const val EMBEDDING_SIZE = 128
        const val TRAINING_TIMEOUT_MS = 5000L
        const val POLL_MS = 10L
        const val LOADING_POLLS = 20
        const val HIGH_SCORE = 0.95f
        const val MANY_PEOPLE = 900

        /** The default parameter of "detected face name with threshold". */
        const val DEFAULT_THRESHOLD = 0.53
    }
}
