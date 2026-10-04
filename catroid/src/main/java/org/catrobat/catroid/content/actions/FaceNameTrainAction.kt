package org.catrobat.catroid.content.actions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.annotation.VisibleForTesting
import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.FaceRecognizer.Recognizer
import org.catrobat.catroid.FaceRecognizer.env.FileUtils
import org.catrobat.catroid.R
import org.catrobat.catroid.stage.BrickDialogManager.DialogType
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.utils.ToastUtil
import java.io.IOException
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
        private const val REQ_BASE = 3000

        /**
         * Request codes this action owns, one per person index.
         *
         * StageResourceHolder.onActivityResult must let these through, because its
         * default branch calls endStageActivity() and would close the program the
         * moment the photo picker returns. They must stay clear of the codes the
         * stage uses itself (1, 101, 1000, 2000).
         */
        const val REQUEST_FIRST = REQ_BASE
        const val REQUEST_LAST = REQ_BASE + 899

        @JvmStatic
        fun ownsRequestCode(requestCode: Int): Boolean {
            return requestCode in REQUEST_FIRST..REQUEST_LAST
        }

        /** One request code per person, so this many people can be given photos. */
        private const val MAX_NAMES = REQUEST_LAST - REQUEST_FIRST + 1

        /** Keeps the progress dialog on screen long enough to be seen. */
        private const val MIN_PROGRESS_MS = 700L

        /** The most recent brick run; it takes over results after a stage restart. */
        @JvmStatic
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
            val owner = pickerOwner?.takeIf { it.isRunningOnTheCurrentStage } ?: currentInstance
            pickerOwner = null
            owner?.handleResult(requestCode, resultCode, data)
        }

        private var appContext: Context? = null
        private var pendingName: String? = null

        /**
         * True from the moment photos are picked until the embeddings are saved.
         * When the brick runs while this is set (the stage restarted during
         * training), the progress dialog is shown instead of the name list.
         */
        private var trainingInProgress = false
        private var progressTotal = 1
        private var progressDone = 0

        /** A training result for the user; success and failure look different. */
        private class Outcome(val message: String, val success: Boolean)

        /** Result waiting to be shown, if training finished with no stage up. */
        private var pendingOutcome: Outcome? = null

        /** A progress dialog and the brick it was shown for. */
        private class ProgressView(
            val brick: FaceNameTrainAction,
            val dialog: AlertDialog,
            val bar: ProgressBar
        )

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

        // ---------------- Test surface ----------------
        // The state above is private and static so training survives the stage
        // being destroyed. These accessors let the tests observe it; they are
        // internal, so not part of the public API.

        @VisibleForTesting
        @JvmStatic
        internal fun resetStateForTest(context: Context?) {
            appContext = context?.applicationContext
            pendingName = null
            trainingInProgress = false
            pendingOutcome = null
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
            trainingInProgress = active
        }

        @VisibleForTesting
        @JvmStatic
        internal fun isTrainingForTest(): Boolean = trainingInProgress

        @VisibleForTesting
        @JvmStatic
        internal fun getProgressForTest(): IntArray = intArrayOf(progressDone, progressTotal)
    }

    private var recognizer: Recognizer? = null

    /** The first act() of this run has started the dialogs. */
    private var started = false

    /** The user pressed Done, or the brick could not run; the script may continue. */
    @Volatile
    private var finished = false

    /** Started and not yet finished: its script is waiting for it. */
    private val isRunning: Boolean
        get() = started && !finished

    /** The stage this brick showed its dialogs on. */
    private var runsOn: WeakReference<StageActivity>? = null

    /**
     * A brick of a stage that Android destroyed is never finished, so it looks
     * like it is running; its script is gone with that stage.
     */
    private val isRunningOnTheCurrentStage: Boolean
        get() {
            @Suppress("SENSELESS_COMPARISON")
            val current = StageActivity.activeStageActivity?.get() ?: return false
            return isRunning && runsOn?.get() === current
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
                   if (initRecognizer()) {
                       showCurrentScreen()
                   } else {
                       showError(R.string.face_train_not_available)
                       finished = true
                   }
               }, "face_train_init").start()
    }

    /** Whatever the brick should be showing right now. */
    private fun showCurrentScreen() {
        pendingOutcome?.let {
            pendingOutcome = null
            showOutcome(it)
        }
        show(if (trainingInProgress) DialogType.FACE_TRAIN_PROGRESS else DialogType.FACE_TRAIN_MENU)
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
            recognizer = Recognizer.getInstance(ctx)
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
        mainHandler.post { ToastUtil.showError(ctx, resource) }
    }

    private fun showOutcome(outcome: Outcome) {
        val ctx = stageActivity() ?: appContext ?: return
        mainHandler.post {
            if (outcome.success) {
                ToastUtil.showSuccess(ctx, outcome.message)
            } else {
                ToastUtil.showError(ctx, outcome.message)
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
        val current = recognizerOrInit()
        if (current == null) {
            show(DialogType.FACE_TRAIN_MENU)
            return
        }
        if (current.classNames.size >= MAX_NAMES && name.trim() !in current.classNames) {
            showError(R.string.face_train_too_many_names)
            show(DialogType.FACE_TRAIN_MENU)
            return
        }
        val index = try {
            current.addPerson(name)
        } catch (exception: IllegalArgumentException) {
            Log.e(TAG, "Name rejected", exception)
            showError(R.string.face_train_name_rejected)
            show(DialogType.FACE_TRAIN_MENU)
            return
        } catch (exception: IOException) {
            Log.e(TAG, "Name not saved", exception)
            showError(R.string.face_train_not_saved)
            show(DialogType.FACE_TRAIN_MENU)
            return
        }
        Log.i(TAG, "Added name '$name' at index $index")
        openImagePicker(name.trim())
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
        try {
            if (recognizerOrInit()?.deletePerson(name) == false) {
                showError(R.string.face_train_name_missing)
            }
        } catch (exception: IOException) {
            Log.e(TAG, "Delete not saved", exception)
            showError(R.string.face_train_not_saved)
        }
        show(DialogType.FACE_TRAIN_MENU)
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
        if (!trainingInProgress) {
            // Training finished while the dialog was being built.
            dialog.dismiss()
            return
        }
        progressViews.add(ProgressView(this, dialog, bar))
        progressShownAt = System.currentTimeMillis()
        refreshProgress()
    }

    // ---------------- Photo picker ----------------

    private fun openImagePicker(name: String) {
        val current = recognizerOrInit()
        val targetIndex = current?.classNames?.indexOf(name) ?: -1
        val activity = stageActivity()
        if (targetIndex < 0 || activity == null) {
            showError(R.string.face_train_name_missing)
            show(DialogType.FACE_TRAIN_MENU)
            return
        }
        if (targetIndex >= MAX_NAMES) {
            showError(R.string.face_train_too_many_names)
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
        try {
            activity.startActivityForResult(intent, targetIndex + REQ_BASE)
        } catch (exception: ActivityNotFoundException) {
            Log.e(TAG, "No app to choose photos", exception)
            pickerOwner = null
            pendingName = null
            showError(R.string.face_train_no_photo_picker)
            show(DialogType.FACE_TRAIN_MENU)
        }
    }

    /** Picker result, forwarded by StageResourceHolder.onActivityResult. */
    fun handleResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Safe to call more than once: training must not run twice.
        if (trainingInProgress) {
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

        for (uri in uris) {
            try {
                ctx.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (t: Throwable) {
                Log.w(TAG, "Could not persist permission for $uri")
            }
        }

        // From here on the brick shows the progress dialog whenever it runs, even
        // if the stage is destroyed and the program restarted meanwhile.
        trainingInProgress = true
        trainingOwner = this
        progressTotal = uris.size
        progressDone = 0

        show(DialogType.FACE_TRAIN_PROGRESS)
        runTraining(ctx, current, name, uris)
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
            view.bar.max = progressTotal
            view.bar.progress = progressDone
            view.dialog.setMessage(progressText(view.dialog.context))
        }
    }

    private fun runTraining(ctx: Context, current: Recognizer, name: String, uris: List<Uri>) {
        val onProgress = Recognizer.ProgressListener { done, total ->
            progressDone = done
            progressTotal = if (total > 0) total else 1
            mainHandler.post { refreshProgress() }
        }

        worker.execute {
            var added = 0
            var error: String? = null
            try {
                val result = current.extractEmbeddings(ctx.contentResolver, uris, onProgress)
                added = result.embeddings.size
                if (added > 0) {
                    current.addEmbeddings(current.addPerson(name), result.embeddings)
                }
                Log.i(TAG, "Training '$name': " + result.report.joinToString(" | "))
            } catch (t: Throwable) {
                error = "${t.javaClass.simpleName}: ${t.message}"
                Log.e(TAG, "Training failed", t)
            }
            val outcome = when {
                error != null -> Outcome(ctx.getString(R.string.face_train_error, error), success = false)
                added == 0 -> Outcome(ctx.getString(R.string.face_train_no_face), success = false)
                else -> Outcome(ctx.getString(R.string.face_train_success), success = true)
            }

            // On the main thread, where the progress dialogs are tracked.
            mainHandler.post {
                val shownFor = System.currentTimeMillis() - progressShownAt
                val wait = if (progressViews.isNotEmpty() && shownFor < MIN_PROGRESS_MS) MIN_PROGRESS_MS - shownFor else 0L
                mainHandler.postDelayed({ finishTraining(outcome) }, wait)
            }
        }
    }

    /**
     * Main thread. Returns to the name list: the lists open first and the
     * progress dialogs close afterwards, so there is no gap between them.
     */
    private fun finishTraining(outcome: Outcome) {
        trainingInProgress = false
        val views = progressViews.toList()
        progressViews.clear()
        progressShownAt = 0L
        val waitingBricks = views.map { it.brick }.distinct()

        // The brick whose photos these were, if it still runs; after a stage
        // restart that brick is gone and the brick of the new run takes over.
        // Only a brick that runs on the current stage may open the menu there:
        // otherwise it would pause another program. The result then waits for
        // the next training brick.
        val owner = (listOf(trainingOwner, currentInstance) + waitingBricks + this)
            .firstOrNull { it?.isRunningOnTheCurrentStage == true }
        trainingOwner = null
        if (owner == null) {
            pendingOutcome = outcome
        } else {
            owner.showOutcome(outcome)
            owner.show(DialogType.FACE_TRAIN_MENU)
        }
        // Every other brick that waited in a progress dialog gets its name list back too.
        waitingBricks
            .filter { it !== owner && it.isRunningOnTheCurrentStage }
            .forEach { it.show(DialogType.FACE_TRAIN_MENU) }
        for (view in views) {
            try {
                view.dialog.dismiss()
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
