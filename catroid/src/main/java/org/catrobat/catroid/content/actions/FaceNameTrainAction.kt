package org.catrobat.catroid.content.actions

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.annotation.VisibleForTesting
import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.facerecognition.Recognizer
import org.catrobat.catroid.facerecognition.env.FileUtils
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.R
import org.catrobat.catroid.camera.mlkitdetectors.FaceNameDetector
import org.catrobat.catroid.stage.BrickDialogManager.DialogType
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.utils.ToastUtil
import java.lang.ref.WeakReference
import java.util.concurrent.Executors

/**
 * The Face name train brick: lets the user add names, pick photos for them and
 * delete them, while the program waits.
 *
 * Like the Ask brick, every dialog goes through BrickDialogManager: the action
 * posts SHOW_DIALOG to StageActivity.messageHandler, the stage is paused while
 * a dialog is open, the back key opens the stage menu, and [act] returns false
 * until the user presses Done. The dialogs cannot be closed by tapping outside.
 *
 * The photo picker is another app. Android may destroy StageActivity while it
 * is open and Catroid then restarts the program, so everything training needs
 * is static; a new action instance picks it up when the brick runs again.
 */
class FaceNameTrainAction : Action() {

    companion object {
        private const val TAG = "FaceNameTrain"
        /**
         * The request code of the photo picker. The result is matched to its
         * person through [pendingName], not through the code.
         *
         * StageResourceHolder.onActivityResult must let it through, because its
         * default branch calls endStageActivity() and would close the program the
         * moment the photo picker returns. It must stay clear of the codes the
         * stage uses itself (1, 101, 1000, 2000).
         */
        const val REQUEST_CODE = 3000

        @JvmStatic
        fun ownsRequestCode(requestCode: Int): Boolean = requestCode == REQUEST_CODE

        /** Keeps the progress dialog on screen long enough to be seen. */
        private const val MIN_PROGRESS_MS = 700L

        /** The most recent brick run; it takes over results after a stage restart. */
        @JvmStatic
        @Volatile
        var currentInstance: FaceNameTrainAction? = null

        /**
         * The brick that opened the photo picker, and the brick whose photos are
         * being trained. With several training bricks running at once, their
         * results belong to these, not to the brick that started last.
         */
        private var pickerOwner: FaceNameTrainAction? = null
        private var trainingOwner: FaceNameTrainAction? = null

        /**
         * Photo picker result, forwarded by StageResourceHolder.onActivityResult.
         * It goes to the brick that opened the picker while that brick is still
         * running; after a stage restart that brick is gone and the brick of the
         * new run takes the result.
         */
        @JvmStatic
        fun onPickerResult(requestCode: Int, resultCode: Int, data: Intent?) {
            pickerOpen = false
            val owner = pickerOwner?.takeIf { it.isRunningOnTheCurrentStage }
                ?: currentInstance?.takeIf { it.isRunningOnTheCurrentStage }
            pickerOwner = null
            if (owner == null) {
                // The stage was recreated, or the program restarted, while the
                // picker was open, and the new run's brick has not started yet.
                // It takes the result when it does; the old brick must not show
                // its name list on the new stage.
                synchronized(trainingLock) { pendingPickerResult = PickerResult(requestCode, resultCode, data) }
                return
            }
            owner.handleResult(requestCode, resultCode, data)
        }

        /** A photo picker result that arrived before a brick of the current run started. */
        private class PickerResult(val requestCode: Int, val resultCode: Int, val data: Intent?)

        /** Guarded by [trainingLock]: set on the main thread, taken on a brick's start thread. */
        private var pendingPickerResult: PickerResult? = null

        /**
         * True from opening the photo picker until its result arrives. Closing the
         * name list for the picker must not resume the stage behind the picker.
         */
        @Volatile
        private var pickerOpen = false

        @JvmStatic
        fun isPickerOpen(): Boolean = pickerOpen

        /**
         * Counts program runs. A brick belongs to the run it started in; after a
         * restart from the stage menu the old run's bricks are never finished and
         * stay on the same stage, but they must not take results any more.
         */
        @Volatile
        private var programRun = 0

        /**
         * A program run starts (also a restart from the stage menu, and a stage
         * Android recreated): bricks of earlier runs no longer take results. A
         * picker result that is still waiting stays: on a recreated stage it can
         * arrive after the program started, and the new run's brick takes it.
         */
        @JvmStatic
        fun onProgramStart() {
            programRun++
            pickerOpen = false
        }

        /**
         * The user left the stage for good (not a stage Android recreates): a
         * picker result no brick took, and the name it was for, are dropped, so a
         * later, unrelated program does not train those photos under that name.
         */
        @JvmStatic
        fun onStageLeft() {
            synchronized(trainingLock) { pendingPickerResult = null }
            if (!pickerOpen) {
                pendingName = null
            }
            pickerOwner = null
            currentInstance = null
            // The models (two interpreters and the 4 MB model) are loaded again by
            // the next program that needs them. Not on the main thread: releasing
            // waits until a frame or training batch has stopped using them.
            saver.execute {
                if (!synchronized(trainingLock) { trainingInProgress }) {
                    try {
                        releaseModels()
                    } catch (t: Throwable) {
                        Log.w(TAG, "Could not release the face models: ${t.javaClass.simpleName}")
                    }
                }
            }
        }

        /** Releases the face models; the tests replace it. */
        @VisibleForTesting
        internal var releaseModels: () -> Unit = { Recognizer.releaseModels() }

        /**
         * Releases the photo read permissions training keeps while it reads the
         * photos, unless training runs now. They stay when the app was stopped
         * during training, and Android allows only a limited number. This brick
         * is the only place the app takes such permissions.
         */
        @JvmStatic
        @Suppress("TooGenericExceptionCaught")
        fun releaseLeftoverPhotoPermissions(resolver: ContentResolver) {
            if (synchronized(trainingLock) { trainingInProgress }) {
                return
            }
            val leftovers = try {
                resolver.persistedUriPermissions.filter { it.isReadPermission }
            } catch (t: Throwable) {
                Log.w(TAG, "Could not list the kept photo permissions: ${t.javaClass.simpleName}")
                return
            }
            for (permission in leftovers) {
                try {
                    resolver.releasePersistableUriPermission(permission.uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } catch (t: Throwable) {
                    Log.w(TAG, "Could not release a kept photo permission: ${t.javaClass.simpleName}")
                }
            }
        }

        @Volatile
        private var appContext: Context? = null

        @Volatile
        private var pendingName: String? = null

        /**
         * True from the moment photos are picked until the embeddings are saved.
         * When the brick runs while this is set (the stage restarted during
         * training), the progress dialog is shown instead of the name list.
         */
        private var trainingInProgress = false

        /**
         * Guards [trainingInProgress], [progressWaiters] and [pendingPickerResult]: a
         * brick decides on its start thread whether to show the progress dialog,
         * while training ends on the main thread.
         */
        private val trainingLock = Any()

        /**
         * Every brick that asked for the progress dialog of the training now
         * running. When training ends, each gets exactly one name list, also if
         * its progress dialog has not appeared yet: Android calls a dialog's
         * onShow listener later, from the message queue.
         */
        private val progressWaiters = LinkedHashSet<FaceNameTrainAction>()

        // Counted on the training thread, shown on the main thread.
        @Volatile
        private var progressTotal = 1

        @Volatile
        private var progressDone = 0

        /** A training result for the user; success and failure look different. */
        private class Outcome(val message: String, val success: Boolean)

        /**
         * A progress dialog and the brick it was shown for. Weak, so a stage the
         * user left during training is not kept in memory by its dialog.
         */
        private class ProgressView(
            val brick: FaceNameTrainAction,
            dialog: AlertDialog,
            bar: ProgressBar
        ) {
            private val dialogReference = WeakReference(dialog)
            private val barReference = WeakReference(bar)
            val dialog: AlertDialog? get() = dialogReference.get()
            val bar: ProgressBar? get() = barReference.get()
        }

        /**
         * Every open progress dialog. Several bricks can show one at a time: two
         * scripts with a training brick, run again while training goes on.
         * Main thread only.
         */
        private val progressViews = mutableListOf<ProgressView>()
        private var progressShownAt = 0L

        private val mainHandler = Handler(Looper.getMainLooper())

        /** Training only, so no UI work queues behind it. */
        private val worker = Executors.newSingleThreadExecutor()

        /**
         * Deleting a name, which writes and syncs the face files: never on the main
         * thread, and never waiting behind another brick's training batch.
         */
        private val saver = Executors.newSingleThreadExecutor()

        /**
         * Names being deleted on [saver]. Main thread only. Like the open picker,
         * a save between two dialogs keeps the stage paused.
         */
        private var savesInProgress = 0

        /** A dialog that closes now must not resume the stage: the next step opens it. */
        @JvmStatic
        fun keepsStagePaused(): Boolean = pickerOpen || savesInProgress > 0

        // ---------------- Test surface ----------------
        // The state above is private and static so training survives the stage
        // being destroyed. These accessors let the tests observe it; they are
        // internal, so not part of the public API.

        @VisibleForTesting
        @JvmStatic
        internal fun resetStateForTest(context: Context?) {
            appContext = context?.applicationContext
            pendingName = null
            synchronized(trainingLock) {
                trainingInProgress = false
                progressWaiters.clear()
                pendingPickerResult = null
            }
            pickerOpen = false
            savesInProgress = 0
            recognizerFactory = loadRecognizer
            releaseModels = { Recognizer.releaseModels() }
            progressTotal = 1
            progressDone = 0
            progressViews.clear()
            progressShownAt = 0L
            currentInstance = null
            pickerOwner = null
            trainingOwner = null
        }

        @VisibleForTesting
        @JvmStatic
        internal fun setPendingNameForTest(name: String?) {
            pendingName = name
        }

        @VisibleForTesting
        @JvmStatic
        internal fun getPendingNameForTest(): String? = pendingName

        @VisibleForTesting
        @JvmStatic
        internal fun setTrainingForTest(active: Boolean) {
            synchronized(trainingLock) { trainingInProgress = active }
        }

        @VisibleForTesting
        @JvmStatic
        internal fun isTrainingForTest(): Boolean = synchronized(trainingLock) { trainingInProgress }

        @VisibleForTesting
        @JvmStatic
        internal fun getProgressForTest(): IntArray = intArrayOf(progressDone, progressTotal)

        private val loadRecognizer: (Context) -> Recognizer = { Recognizer.getInstance(it) }

        /** Loads the face models; the tests replace it to hold loading. */
        @VisibleForTesting
        internal var recognizerFactory: (Context) -> Recognizer = loadRecognizer

        @VisibleForTesting
        @JvmStatic
        internal fun setPickerOpenForTest(open: Boolean) {
            pickerOpen = open
        }

        @VisibleForTesting
        @JvmStatic
        internal fun setSavingForTest(saving: Boolean) {
            savesInProgress = if (saving) 1 else 0
        }
    }

    /** Loaded on the face_train_init thread, used on the main thread. */
    @Volatile
    private var recognizer: Recognizer? = null

    /** The first act() of this run has started the dialogs. */
    @Volatile
    private var started = false

    /** The program run this brick belongs to, see [onProgramStart]. */
    @Volatile
    private var startedInRun = programRun

    /** The user pressed Done, or the brick could not run; the script may continue. */
    @Volatile
    private var finished = false

    /** Started and not yet finished: its script is waiting for it. */
    private val isRunning: Boolean
        get() = started && !finished

    /** The stage this brick showed its dialogs on. */
    @Volatile
    private var runsOn: WeakReference<StageActivity>? = null

    /**
     * A brick of a stage that Android destroyed, or of a run before a restart
     * from the stage menu, is never finished, so it looks like it is running;
     * its script is gone. A stage that is closing runs no bricks either.
     */
    private val isRunningOnTheCurrentStage: Boolean
        get() {
            val current = stageActivity() ?: return false
            return isRunning && startedInRun == programRun && runsOn?.get() === current
        }

    /**
     * The current stage, or null if there is none or it is closing.
     *
     * activeStageActivity is a Java static field, so Kotlin treats it as non null
     * and calling .get() on it throws when no stage has ever started. That is the
     * normal state before the first program run, and in every unit test.
     */
    private fun stageActivity(): StageActivity? {
        @Suppress("SENSELESS_COMPARISON")
        val reference = StageActivity.activeStageActivity ?: return null
        val stage = reference.get() ?: return null
        return if (stage.isFinishing || stage.isDestroyed) null else stage
    }

    /** Holds the script, like AskAction, until [finished]. */
    override fun act(delta: Float): Boolean {
        if (!started) {
            started = true
            start()
        }
        return finished
    }

    private fun start() {
        startedInRun = programRun
        currentInstance = this
        val activity = stageActivity()
        if (activity == null || StageActivity.messageHandler == null) {
            Log.w(TAG, "No stage to show the training dialogs on")
            finished = true
            return
        }
        appContext = activity.applicationContext
        runsOn = WeakReference(activity)

        // Loading the face models takes a moment; keep it off the GL and main threads.
        Thread({
                   val loaded = initRecognizer()
                   mainHandler.post { afterLoading(loaded) }
               }, "face_train_init").start()
    }

    /**
     * Main thread, once the models are loaded. The user may have left or
     * restarted the program meanwhile: a brick that no longer runs on the
     * current stage and run shows nothing and takes no waiting picker result.
     */
    private fun afterLoading(loaded: Boolean) {
        if (!isRunningOnTheCurrentStage) {
            Log.i(TAG, "The program changed while the face models loaded; nothing to show")
            return
        }
        if (loaded) {
            showCurrentScreen()
        } else {
            showError(R.string.face_train_not_available)
            finished = true
        }
    }

    /** Whatever the brick should be showing right now. */
    private fun showCurrentScreen() {
        val pickerResult: PickerResult?
        val waitsForTraining: Boolean
        synchronized(trainingLock) {
            waitsForTraining = trainingInProgress
            pickerResult = if (waitsForTraining) null else pendingPickerResult
            pendingPickerResult = null
            if (waitsForTraining) {
                // Counted now, not when the dialog appears, so the end of training finds it.
                progressWaiters.add(this)
            }
        }
        if (pickerResult != null) {
            // The picker was opened before the stage was recreated or the program
            // restarted: this brick takes its result, on the main thread.
            mainHandler.post { handleResult(pickerResult.requestCode, pickerResult.resultCode, pickerResult.data) }
            return
        }
        show(if (waitsForTraining) DialogType.FACE_TRAIN_PROGRESS else DialogType.FACE_TRAIN_MENU)
    }

    /**
     * Asks BrickDialogManager for a dialog through SHOW_DIALOG, like AskAction.
     *
     * On the main thread (a dialog button, the picker result, the end of
     * training) the message is handled at once, so the next dialog is open
     * before the current one closes and the stage never shows through
     * undimmed in between. From other threads it is posted as usual.
     */
    private fun show(type: DialogType, content: String = "") {
        val handler = StageActivity.messageHandler
        if (handler == null) {
            Log.w(TAG, "No stage for $type, ending the brick")
            finished = true
            return
        }
        val message = handler.obtainMessage(StageActivity.SHOW_DIALOG, arrayListOf(type, this, content))
        if (Looper.myLooper() == Looper.getMainLooper()) {
            handler.dispatchMessage(message)
        } else {
            message.sendToTarget()
        }
    }

    private fun initRecognizer(): Boolean {
        val ctx = appContext ?: return false
        return try {
            FileUtils.init(ctx)
            releaseLeftoverPhotoPermissions(ctx.contentResolver)
            recognizer = recognizerFactory(ctx)
            FaceNameDetector.onModelsLoaded()
            true
        } catch (t: Throwable) {
            Log.e(TAG, "Recognizer initialization failed", t)
            false
        }
    }

    private fun recognizerOrInit(): Recognizer? {
        recognizer?.let { return it }
        return if (initRecognizer()) recognizer else null
    }

    /** Catroid's toasts (ToastUtil), always on the main thread. */
    private fun showError(resource: Int) {
        val ctx = stageActivity() ?: appContext ?: return
        toast(ctx, ctx.getString(resource), success = false)
    }

    private fun showOutcome(outcome: Outcome?, ctx: Context? = stageActivity() ?: appContext) {
        if (outcome == null || ctx == null) {
            return
        }
        toast(ctx, outcome.message, outcome.success)
    }

    /**
     * Catroid's toast on a stage. ToastUtil shows nothing without an activity,
     * so with only the application context (no stage open, e.g. training ended
     * after the user left the program) Android's own toast is used.
     */
    private fun toast(ctx: Context, message: String, success: Boolean) {
        mainHandler.post {
            when {
                ctx !is Activity -> Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
                success -> ToastUtil.showSuccess(ctx, message)
                else -> ToastUtil.showError(ctx, message)
            }
        }
    }

    // ---------------- Called by BrickDialogManager, on the main thread ----------------

    fun personNames(): List<String> = recognizerOrInit()?.classNames.orEmpty()

    /** True while stored photos come from an older face model and must be added again. */
    fun needsRetraining(): Boolean = recognizerOrInit()?.needsRetraining() == true

    /**
     * By name, not by row: another training menu may have deleted or added a
     * person since this list was shown, and the row would then be someone else.
     */
    fun onPersonChosen(name: String) {
        openImagePicker(name)
    }

    fun onAddNameChosen() {
        show(DialogType.FACE_TRAIN_NEW_NAME)
    }

    fun onNewName(name: String) {
        if (Recognizer.hasLineBreak(name)) {
            showError(R.string.face_train_name_line_break)
            show(DialogType.FACE_TRAIN_MENU)
            return
        }
        // Only checked here, nothing is saved: the name is stored together with its
        // photos when training ends. Backing out of the picker, or photos without
        // a usable face, leave no name without photos behind.
        val validName = try {
            Recognizer.requireValidName(name)
        } catch (exception: IllegalArgumentException) {
            Log.e(TAG, "Name rejected", exception)
            showError(R.string.face_train_name_rejected)
            show(DialogType.FACE_TRAIN_MENU)
            return
        }
        openImagePicker(validName, isNewName = true)
    }

    /**
     * Saves on [saver], then calls [then] on the main thread with the error, if
     * any, whatever was thrown. Not if the brick stopped running on the current
     * stage meanwhile: the user left or restarted the program, and its next
     * dialog would pause that.
     */
    @Suppress("TooGenericExceptionCaught")
    private fun saveThenOnMainThread(save: () -> Unit, then: (Throwable?) -> Unit) {
        savesInProgress++
        saver.execute {
            var error: Throwable? = null
            try {
                save()
            } catch (throwable: Throwable) {
                error = throwable
            } finally {
                // Always: otherwise the save stays counted and the stage stays paused.
                mainHandler.post {
                    savesInProgress--
                    if (isRunningOnTheCurrentStage) {
                        then(error)
                    } else {
                        Log.i(TAG, "The program changed while a name was saved; nothing to show")
                    }
                }
            }
        }
    }

    fun onNewNameCancelled() {
        show(DialogType.FACE_TRAIN_MENU)
    }

    fun onDeleteChosen() {
        show(DialogType.FACE_TRAIN_DELETE_CHOICE)
    }

    fun onDeleteTargetChosen(name: String) {
        show(DialogType.FACE_TRAIN_DELETE_CONFIRM, name)
    }

    /** By name, like [onPersonChosen]: an older list must not delete someone else. */
    fun onDeleteConfirmed(name: String) {
        val current = recognizerOrInit()
        if (current == null) {
            show(DialogType.FACE_TRAIN_MENU)
            return
        }
        var deleted = true
        saveThenOnMainThread(
            save = { deleted = current.deletePerson(name) },
            then = { error ->
                if (error != null) {
                    Log.e(TAG, "Delete not saved", error)
                    showError(R.string.face_train_not_saved)
                } else if (!deleted) {
                    showError(R.string.face_train_name_missing)
                }
                show(DialogType.FACE_TRAIN_MENU)
            }
        )
    }

    fun onDeleteCancelled() {
        show(DialogType.FACE_TRAIN_MENU)
    }

    /** Done: close the brick and let the script continue. */
    fun onDone() {
        finished = true
    }

    fun progressText(context: Context): String =
        context.getString(R.string.face_train_progress_photo, progressDone, progressTotal)

    fun progressMax(): Int = progressTotal

    fun progressValue(): Int = progressDone

    /** BrickDialogManager hands over the progress dialog it opened for this action. */
    fun onProgressDialogShown(dialog: AlertDialog, bar: ProgressBar) {
        if (!synchronized(trainingLock) { trainingInProgress }) {
            // Training finished while the dialog was being shown. This brick was
            // a waiter then and has already been given its name list.
            dialog.dismiss()
            return
        }
        progressViews.add(ProgressView(this, dialog, bar))
        progressShownAt = System.currentTimeMillis()
        refreshProgress()
    }

    // ---------------- Photo picker ----------------

    /**
     * Asks for photos of [name]. An existing name must still be in the list (an
     * older menu may show a deleted one); a new name is added with its photos.
     */
    private fun openImagePicker(name: String, isNewName: Boolean = false) {
        val activity = stageActivity()
        val known = isNewName || (recognizerOrInit()?.classNames?.contains(name) == true)
        if (!known || activity == null) {
            showError(R.string.face_train_name_missing)
            show(DialogType.FACE_TRAIN_MENU)
            return
        }
        pendingName = name
        runsOn = WeakReference(activity)

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        )
        pickerOwner = this
        pickerOpen = true
        try {
            activity.startActivityForResult(intent, REQUEST_CODE)
        } catch (exception: ActivityNotFoundException) {
            Log.e(TAG, "No app to choose photos", exception)
            pickerOpen = false
            pickerOwner = null
            pendingName = null
            showError(R.string.face_train_no_photo_picker)
            show(DialogType.FACE_TRAIN_MENU)
        }
    }

    /** Picker result, forwarded by StageResourceHolder.onActivityResult. */
    fun handleResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Safe to call more than once: training must not run twice.
        if (synchronized(trainingLock) { trainingInProgress }) {
            Log.i(TAG, "Ignoring duplicate result, training already running")
            return
        }

        val ctx = appContext
            ?: stageActivity()?.applicationContext
            ?: return
        appContext = ctx

        val current = recognizerOrInit()
        if (current == null) {
            finished = true
            return
        }

        val name = pendingName
        pendingName = null
        val uris = if (resultCode == StageActivity.RESULT_OK && data != null) urisOf(data) else emptyList()

        if (name.isNullOrEmpty() || uris.isEmpty()) {
            if (name.isNullOrEmpty()) {
                Log.e(TAG, "No pending name for requestCode $requestCode")
            }
            showError(R.string.face_train_no_photos)
            show(DialogType.FACE_TRAIN_MENU)
            return
        }

        // From here on the brick shows the progress dialog whenever it runs, even
        // if the stage is destroyed and the program restarted meanwhile. Marked
        // before the photo permissions are kept, so no other brick releases them.
        synchronized(trainingLock) {
            trainingInProgress = true
            progressWaiters.add(this)
        }
        // Kept while training reads the photos, also if the stage is recreated
        // meanwhile; released when training ends.
        val persisted = uris.filter { uri ->
            try {
                ctx.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                true
            } catch (t: Throwable) {
                Log.w(TAG, "Could not keep the permission for a photo: ${t.javaClass.simpleName}")
                false
            }
        }

        trainingOwner = this
        progressTotal = uris.size
        progressDone = 0

        show(DialogType.FACE_TRAIN_PROGRESS)
        runTraining(ctx, current, name, uris, persisted)
    }

    private fun urisOf(data: Intent): List<Uri> {
        val uris = ArrayList<Uri>()
        val clipData = data.clipData
        if (clipData != null) {
            for (i in 0 until clipData.itemCount) {
                uris.add(clipData.getItemAt(i).uri)
            }
        } else {
            data.data?.let { uris.add(it) }
        }
        return uris
    }

    // ---------------- Training ----------------

    private fun refreshProgress() {
        for (view in progressViews) {
            view.bar?.let {
                it.max = progressTotal
                it.progress = progressDone
            }
            view.dialog?.let { it.setMessage(progressText(it.context)) }
        }
    }

    private fun runTraining(ctx: Context, current: Recognizer, name: String, uris: List<Uri>, persisted: List<Uri>) {
        val onProgress = Recognizer.ProgressListener { done, total ->
            progressDone = done
            progressTotal = if (total > 0) total else 1
            mainHandler.post { refreshProgress() }
        }

        worker.execute {
            var outcome: Outcome? = null
            try {
                outcome = train(ctx, current, name, uris, onProgress)
            } catch (t: Throwable) {
                // The message itself could not be built (a broken translation,
                // say): training still ends, without a message.
                Log.e(TAG, "Training result could not be shown: ${t.javaClass.simpleName}")
            } finally {
                releasePermissions(ctx, persisted)
                // Always: otherwise training stays marked as running, and every
                // later training brick shows its progress dialog forever.
                val result = outcome
                mainHandler.post {
                    val shownFor = System.currentTimeMillis() - progressShownAt
                    val wait = if (progressViews.isNotEmpty() && shownFor < MIN_PROGRESS_MS) MIN_PROGRESS_MS - shownFor else 0L
                    mainHandler.postDelayed({ finishTraining(result) }, wait)
                }
            }
        }
    }

    /** Reads the photos and stores them under [name]; the message for the user. Training thread. */
    private fun train(
        ctx: Context,
        current: Recognizer,
        name: String,
        uris: List<Uri>,
        onProgress: Recognizer.ProgressListener
    ): Outcome {
        var added = 0
        var severalPeople = 0
        var error: String? = null
        try {
            val result = current.extractEmbeddings(ctx.contentResolver, uris, onProgress)
            added = result.embeddings.size
            severalPeople = result.severalPeople
            if (added > 0) {
                current.addPhotos(name, result.embeddings)
                FaceNameDetector.onModelsLoaded()
            }
            // With the name: debug builds only.
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Training '$name': " + result.report.joinToString(" | "))
            }
        } catch (t: Throwable) {
            error = t.javaClass.simpleName
            // The message can hold a photo's address: debug builds only.
            Log.e(TAG, "Training failed: $error")
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Training failed", t)
            }
        }
        return when {
            error != null -> Outcome(ctx.getString(R.string.face_train_error, error), success = false)
            added == 0 && severalPeople > 0 -> Outcome(ctx.getString(R.string.face_train_several_people), success = false)
            added == 0 -> Outcome(ctx.getString(R.string.face_train_no_face), success = false)
            severalPeople > 0 -> Outcome(ctx.getString(R.string.face_train_success_some_skipped), success = true)
            else -> Outcome(ctx.getString(R.string.face_train_success), success = true)
        }
    }

    /** The photos have been read: their persisted permissions are not needed any more. */
    private fun releasePermissions(ctx: Context, uris: List<Uri>) {
        for (uri in uris) {
            try {
                ctx.contentResolver.releasePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (t: Throwable) {
                Log.w(TAG, "Could not release the permission for a photo: ${t.javaClass.simpleName}")
            }
        }
    }

    /**
     * Main thread. Returns to the name list: the lists open first and the
     * progress dialogs close afterwards, so there is no gap between them.
     */
    private fun finishTraining(outcome: Outcome?) {
        val waiters: List<FaceNameTrainAction>
        synchronized(trainingLock) {
            trainingInProgress = false
            waiters = progressWaiters.toList()
            progressWaiters.clear()
        }
        val views = progressViews.toList()
        progressViews.clear()
        progressShownAt = 0L
        val waitingBricks = (waiters + views.map { it.brick }).distinct()

        // The brick whose photos these were, if it still runs; after a stage
        // restart that brick is gone and the brick of the new run takes over.
        // Only a brick that runs on the current stage may open the menu there:
        // otherwise it would pause another program. Without one the result is
        // only shown, now, not kept for some later training brick.
        val owner = (listOf(trainingOwner, currentInstance) + waitingBricks + this)
            .firstOrNull { it?.isRunningOnTheCurrentStage == true }
        trainingOwner = null
        if (owner == null) {
            // Over whatever screen is in front, which may be another program.
            showOutcome(outcome, appContext)
        } else {
            owner.showOutcome(outcome)
            owner.show(DialogType.FACE_TRAIN_MENU)
        }
        // Every other brick that waited for training gets its name list back too, once.
        waitingBricks
            .filter { it !== owner && it.isRunningOnTheCurrentStage }
            .forEach { it.show(DialogType.FACE_TRAIN_MENU) }
        for (view in views) {
            try {
                view.dialog?.dismiss()
            } catch (t: Throwable) {
                Log.w(TAG, "Progress dialog already gone")
            }
        }
    }

    override fun restart() {
        super.restart()
        started = false
        finished = false
        runsOn = null
    }

    /** Resets this brick run only. The picker and training may outlive it. */
    override fun reset() {
        super.reset()
        started = false
        finished = false
        runsOn = null
    }
}
