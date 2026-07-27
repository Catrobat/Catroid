package org.catrobat.catroid.content.actions

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.FaceRecognizer.Recognizer
import org.catrobat.catroid.FaceRecognizer.env.FileUtils
import org.catrobat.catroid.R
import org.catrobat.catroid.stage.StageActivity
import java.util.concurrent.Executors

class FaceNameTrainAction : Action() {

    companion object {
        private const val TAG = "FaceNameTrain"
        private const val REQ_BASE = 1000

        /**
         * Request codes this action owns, one per person index.
         *
         * StageResourceHolder.onActivityResult must let these through, because its
         * default branch calls endStageActivity() and would close the program the
         * moment the photo picker returns.
         */
        const val REQUEST_FIRST = 1000
        const val REQUEST_LAST = 1899

        @JvmStatic
        fun ownsRequestCode(requestCode: Int): Boolean {
            return requestCode in REQUEST_FIRST..REQUEST_LAST
        }

        // Catroid dialog palette
        private const val DIALOG_BG = "#424242"
        private const val ROW_BG = "#535353"
        private const val TEXT_PRIMARY = "#FFFFFF"
        private const val TEXT_SECONDARY = "#E0E0E0"
        private const val FIELD_BG = "#F5F5F5"
        private const val FIELD_BORDER = "#FB8C00"
        private const val FIELD_TEXT = "#212121"
        private const val ACCENT = "#80CBC4"
        private const val DANGER = "#EF9A9A"

        /** Keeps the progress dialog on screen long enough to be seen. */
        private const val MIN_PROGRESS_MS = 700L

        @JvmStatic
        var currentInstance: FaceNameTrainAction? = null

        /**
         * All static on purpose. The document picker runs in another app, Android
         * can destroy StageActivity while it is open, and Catroid then restarts the
         * program. Nothing the training needs may live on the action instance or on
         * an activity reference.
         */
        private var appContext: Context? = null
        private var pendingName: String? = null

        /**
         * True from the moment photos are picked until the embeddings are saved.
         * The brick checks this when it runs, so if the stage restarted while
         * training was still going, the progress dialog comes straight back.
         */
        private var trainingInProgress = false
        private var trainingName: String? = null
        private var progressTotal = 1
        private var progressDone = 0

        /** Result waiting to be shown, if training finished with no window up. */
        private var pendingOutcome: String? = null

        private var progressDialog: AlertDialog? = null
        private var progressBar: ProgressBar? = null
        private var progressLabel: TextView? = null
        private var progressShownAt = 0L

        private val mainHandler = Handler(Looper.getMainLooper())

        /** Training only. Never used for UI work, so the UI cannot queue behind it. */
        private val worker = Executors.newSingleThreadExecutor()

        // ---------------- Test surface ----------------
        // Everything above is private and static, which is what lets training
        // survive the stage being destroyed. That also makes it untestable from
        // outside, so these few accessors exist purely for the unit tests.

        @VisibleForTesting
        @JvmStatic
        fun resetStateForTest(context: Context?) {
            appContext = context?.applicationContext
            pendingName = null
            trainingInProgress = false
            trainingName = null
            pendingOutcome = null
            progressTotal = 1
            progressDone = 0
            progressDialog = null
            progressBar = null
            progressLabel = null
            progressShownAt = 0L
            currentInstance = null
            testActivity = null
        }

        @VisibleForTesting
        @JvmStatic
        fun setPendingNameForTest(name: String?) {
            pendingName = name
        }

        @VisibleForTesting
        @JvmStatic
        fun getPendingNameForTest(): String? = pendingName

        @VisibleForTesting
        @JvmStatic
        fun setTrainingForTest(active: Boolean) {
            trainingInProgress = active
        }

        @VisibleForTesting
        @JvmStatic
        fun isTrainingForTest(): Boolean = trainingInProgress

        @VisibleForTesting
        @JvmStatic
        fun getProgressForTest(): IntArray = intArrayOf(progressDone, progressTotal)

        /** Host activity for UI tests. Null in production, always. */
        @VisibleForTesting
        @JvmStatic
        var testActivity: Activity? = null
    }

    private var recognizer: Recognizer? = null
    private var dialog: AlertDialog? = null

    /**
     * The current stage, or null.
     *
     * activeStageActivity is a Java static field, so Kotlin treats it as non null
     * and calling .get() on it throws when no stage has ever started. That is the
     * normal state before the first program run, and in every unit test.
     */
    private fun stageActivity(): StageActivity? {
        @Suppress("SENSELESS_COMPARISON")
        val reference = StageActivity.activeStageActivity ?: return null
        return reference.get()
    }

    override fun act(delta: Float): Boolean {
        currentInstance = this
        val activity = stageActivity() ?: return false
        appContext = activity.applicationContext

        // A plain thread, not the worker, so this never waits behind a training job.
        Thread {
            val ok = initRecognizer()
            mainHandler.post {
                if (!ok) {
                    toast("Face recognition could not start")
                } else {
                    showCurrentScreen()
                }
            }
        }.start()

        return true
    }

    /** Whatever the brick should be showing right now. */
    private fun showCurrentScreen() {
        pendingOutcome?.let {
            pendingOutcome = null
            toast(it)
        }
        if (trainingInProgress) {
            showProgressDialog()
        } else {
            showPersonListDialog()
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

    // ---------------- Shared UI pieces ----------------

    /**
     * The activity to attach dialogs to.
     *
     * Normally the current stage. In an instrumented test it can be any activity,
     * which is what makes the dialogs testable without booting a whole Catroid
     * project. Returns null rather than a dead activity, because attaching a
     * dialog to a window with no token throws.
     */
    private fun liveActivity(): Activity? {
        testActivity?.let {
            return if (it.isFinishing || it.isDestroyed) null else it
        }
        val a = stageActivity() ?: return null
        return if (a.isFinishing || a.isDestroyed) null else a
    }

    private fun toast(message: String) {
        val ctx = liveActivity() ?: appContext ?: return
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
    }

    private fun panel(colour: String): GradientDrawable {
        val d = GradientDrawable()
        d.setColor(Color.parseColor(colour))
        d.cornerRadius = 10f
        return d
    }

    private fun actionButton(ctx: Context, label: String, colour: String = ACCENT): Button {
        val b = Button(ctx)
        b.text = label
        b.isAllCaps = true
        b.textSize = 16f
        b.setTextColor(Color.parseColor(colour))
        b.setTypeface(null, Typeface.BOLD)
        b.background = null
        b.setPadding(36, 26, 36, 26)
        return b
    }

    private fun buildDialog(ctx: Context, root: View, cancelable: Boolean): AlertDialog {
        val d = AlertDialog.Builder(ctx)
            .setView(root)
            .setCancelable(cancelable)
            .create()
        d.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return d
    }

    private fun titleView(ctx: Context, text: String): TextView {
        val t = TextView(ctx)
        t.text = text
        t.textSize = 22f
        t.setTextColor(Color.parseColor(TEXT_PRIMARY))
        t.setTypeface(null, Typeface.BOLD)
        return t
    }

    // ---------------- Main menu ----------------

    private fun showPersonListDialog() {
        if (trainingInProgress) {
            showProgressDialog()
            return
        }

        val ctx = liveActivity() ?: return
        val current = recognizerOrInit() ?: return

        val root = LinearLayout(ctx)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(56, 48, 56, 28)
        root.background = panel(DIALOG_BG)

        root.addView(titleView(ctx, ctx.getString(R.string.face_train_title)))

        val scrollView = ScrollView(ctx)
        val nameList = LinearLayout(ctx)
        nameList.orientation = LinearLayout.VERTICAL
        scrollView.addView(nameList)
        val scrollParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        scrollParams.topMargin = 28
        scrollView.layoutParams = scrollParams
        root.addView(scrollView)

        val names = current.classNames

        if (names.isEmpty()) {
            val empty = TextView(ctx)
            empty.text = ctx.getString(R.string.face_train_no_names)
            empty.textSize = 15f
            empty.setTextColor(Color.parseColor(TEXT_SECONDARY))
            empty.setPadding(0, 14, 0, 14)
            nameList.addView(empty)
        }

        for (i in names.indices) {
            val personRow = LinearLayout(ctx)
            personRow.orientation = LinearLayout.HORIZONTAL
            personRow.gravity = Gravity.CENTER_VERTICAL
            val rowParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            rowParams.bottomMargin = 16
            personRow.layoutParams = rowParams

            val nameButton = Button(ctx)
            nameButton.text = names[i]
            nameButton.isAllCaps = false
            nameButton.textSize = 17f
            nameButton.setTextColor(Color.parseColor(TEXT_PRIMARY))
            nameButton.gravity = Gravity.CENTER_VERTICAL or Gravity.START
            nameButton.background = panel(ROW_BG)
            nameButton.setPadding(34, 30, 34, 30)
            nameButton.layoutParams =
                LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            nameButton.setOnClickListener {
                dialog?.dismiss()
                openImagePicker(i)
            }

            val deleteButton = actionButton(ctx, "\u2715", DANGER)
            deleteButton.textSize = 18f
            deleteButton.setPadding(28, 26, 12, 26)
            deleteButton.setOnClickListener { confirmDelete(i, names[i]) }

            personRow.addView(nameButton)
            personRow.addView(deleteButton)
            nameList.addView(personRow)
        }

        val addRow = LinearLayout(ctx)
        addRow.orientation = LinearLayout.HORIZONTAL
        addRow.gravity = Gravity.END
        val addRowParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        addRowParams.topMargin = 20
        addRow.layoutParams = addRowParams

        val addNewButton = actionButton(ctx, ctx.getString(R.string.face_train_add_new_name))
        addNewButton.setOnClickListener {
            dialog?.dismiss()
            showNewNameDialog()
        }
        addRow.addView(addNewButton)
        root.addView(addRow)

        try {
            dialog?.dismiss()
            dialog = buildDialog(ctx, root, true)
            dialog?.show()
        } catch (t: Throwable) {
            Log.w(TAG, "Window not ready for the name list, retrying")
            dialog = null
            mainHandler.postDelayed({ showPersonListDialog() }, 200L)
        }
    }

    private fun confirmDelete(index: Int, name: String) {
        val ctx = liveActivity() ?: return

        val root = LinearLayout(ctx)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(56, 48, 56, 28)
        root.background = panel(DIALOG_BG)

        root.addView(titleView(ctx, ctx.getString(R.string.face_train_delete_title)))

        val message = TextView(ctx)
        message.text = ctx.getString(R.string.face_train_delete_message, name)
        message.textSize = 15f
        message.setTextColor(Color.parseColor(TEXT_SECONDARY))
        message.setPadding(0, 20, 0, 0)
        root.addView(message)

        val buttonRow = LinearLayout(ctx)
        buttonRow.orientation = LinearLayout.HORIZONTAL
        buttonRow.gravity = Gravity.END
        val rowParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        rowParams.topMargin = 26
        buttonRow.layoutParams = rowParams

        val noButton = actionButton(ctx, ctx.getString(R.string.face_train_no), TEXT_SECONDARY)
        val yesButton = actionButton(ctx, ctx.getString(R.string.face_train_yes), DANGER)
        buttonRow.addView(noButton)
        buttonRow.addView(yesButton)
        root.addView(buttonRow)

        val d = try {
            buildDialog(ctx, root, true)
        } catch (t: Throwable) {
            return
        }
        noButton.setOnClickListener { d.dismiss() }
        yesButton.setOnClickListener {
            d.dismiss()
            recognizerOrInit()?.deletePerson(index)
            dialog?.dismiss()
            showPersonListDialog()
        }
        d.show()
    }

    // ---------------- Add new name ----------------

    private fun showNewNameDialog() {
        val ctx = liveActivity() ?: return
        val current = recognizerOrInit() ?: return

        val root = LinearLayout(ctx)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(56, 48, 56, 28)
        root.background = panel(DIALOG_BG)

        root.addView(titleView(ctx, ctx.getString(R.string.face_train_new_name_title)))

        val subtitle = TextView(ctx)
        subtitle.text = ctx.getString(R.string.face_train_name_subtitle)
        subtitle.textSize = 15f
        subtitle.setTextColor(Color.parseColor(TEXT_SECONDARY))
        subtitle.setPadding(0, 12, 0, 30)
        root.addView(subtitle)

        val input = EditText(ctx)
        input.hint = ctx.getString(R.string.face_train_name_hint)
        input.setSingleLine(true)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        input.textSize = 18f
        input.setTextColor(Color.parseColor(FIELD_TEXT))
        input.setHintTextColor(Color.parseColor("#9E9E9E"))
        input.setPadding(28, 28, 28, 28)
        val field = GradientDrawable()
        field.setColor(Color.parseColor(FIELD_BG))
        field.cornerRadius = 6f
        field.setStroke(5, Color.parseColor(FIELD_BORDER))
        input.background = field
        root.addView(input)

        val buttonRow = LinearLayout(ctx)
        buttonRow.orientation = LinearLayout.HORIZONTAL
        buttonRow.gravity = Gravity.END
        val rowParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        rowParams.topMargin = 26
        buttonRow.layoutParams = rowParams

        val cancelButton =
            actionButton(ctx, ctx.getString(R.string.face_train_cancel), TEXT_SECONDARY)
        val nextButton = actionButton(ctx, ctx.getString(R.string.face_train_next))
        buttonRow.addView(cancelButton)
        buttonRow.addView(nextButton)
        root.addView(buttonRow)

        val nameDialog = try {
            buildDialog(ctx, root, true)
        } catch (t: Throwable) {
            Log.e(TAG, "Could not build the new name dialog", t)
            return
        }

        cancelButton.setOnClickListener {
            nameDialog.dismiss()
            showPersonListDialog()
        }
        nextButton.setOnClickListener {
            val name = input.text.toString().trim()
            if (name.isEmpty()) {
                input.error = ctx.getString(R.string.face_train_name_required)
                return@setOnClickListener
            }
            nameDialog.dismiss()
            val index = current.addPerson(name)
            Log.i(TAG, "Added name '$name' at index $index")
            openImagePicker(index)
        }

        nameDialog.show()
    }

    private fun openImagePicker(targetIndex: Int) {
        val current = recognizerOrInit() ?: return
        pendingName = current.classNames.getOrNull(targetIndex)
        if (pendingName == null) {
            toast("That name is no longer in the list")
            return
        }

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        )

        liveActivity()?.startActivityForResult(intent, targetIndex + REQ_BASE)
    }

    // ---------------- Progress ----------------

    /**
     * Shows the bar using the current counters. Safe to call at any time: while
     * training runs, whenever the brick runs again, or after the stage restarted.
     * Does nothing if it is already up or if there is no window yet.
     */
    private fun showProgressDialog() {
        if (progressDialog != null) {
            return
        }
        val activity = liveActivity() ?: return

        val root = LinearLayout(activity)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(56, 48, 56, 48)
        root.background = panel(DIALOG_BG)

        root.addView(titleView(activity, activity.getString(R.string.face_train_progress_title)))

        val label = TextView(activity)
        label.text = activity.getString(
            R.string.face_train_progress_photo, progressDone, progressTotal
        )
        label.textSize = 15f
        label.setTextColor(Color.parseColor(TEXT_SECONDARY))
        label.setPadding(0, 18, 0, 24)
        root.addView(label)

        val bar = ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal)
        bar.isIndeterminate = false
        bar.max = progressTotal
        bar.progress = progressDone
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bar.progressTintList = ColorStateList.valueOf(Color.parseColor(ACCENT))
        }
        bar.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        root.addView(bar)

        try {
            dialog?.dismiss()
            val d = buildDialog(activity, root, false)
            d.show()
            progressDialog = d
            progressBar = bar
            progressLabel = label
            progressShownAt = System.currentTimeMillis()
            Log.i(TAG, "Progress dialog shown, $progressDone of $progressTotal")
        } catch (t: Throwable) {
            Log.w(TAG, "Window not ready for progress, will retry")
            progressDialog = null
            progressBar = null
            progressLabel = null
            if (trainingInProgress) {
                mainHandler.postDelayed({ showProgressDialog() }, 200L)
            }
        }
    }

    private fun updateProgress(done: Int, total: Int) {
        progressDone = done
        progressTotal = if (total > 0) total else 1
        mainHandler.post {
            if (progressDialog == null && trainingInProgress) {
                showProgressDialog()
                return@post
            }
            progressBar?.let {
                it.max = progressTotal
                it.progress = progressDone
            }
            progressLabel?.let {
                it.text = it.context.getString(
                    R.string.face_train_progress_photo, progressDone, progressTotal
                )
            }
        }
    }

    private fun dismissProgressDialog(immediate: Boolean) {
        val d = progressDialog
        progressDialog = null
        progressBar = null
        progressLabel = null
        if (d == null) {
            return
        }

        val close = Runnable {
            try {
                if (d.isShowing) {
                    d.dismiss()
                }
            } catch (t: Throwable) {
                Log.w(TAG, "Progress dialog already gone")
            }
        }

        val shownFor = System.currentTimeMillis() - progressShownAt
        progressShownAt = 0L
        if (immediate || shownFor >= MIN_PROGRESS_MS) {
            close.run()
        } else {
            mainHandler.postDelayed(close, MIN_PROGRESS_MS - shownFor)
        }
    }

    // ---------------- Result ----------------

    fun handleResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Safe to call more than once. StageActivity and StageResourceHolder may
        // both forward the result, and training must not run twice.
        if (trainingInProgress) {
            Log.i(TAG, "Ignoring duplicate result, training already running")
            return
        }

        val ctx = appContext
            ?: stageActivity()?.applicationContext
            ?: return
        appContext = ctx

        val current = recognizerOrInit() ?: return

        if (resultCode != StageActivity.RESULT_OK || data == null) {
            toast(ctx.getString(R.string.face_train_no_photos))
            showPersonListDialog()
            return
        }

        val name = pendingName
        pendingName = null
        if (name.isNullOrEmpty()) {
            Log.e(TAG, "No pending name for requestCode $requestCode")
            showPersonListDialog()
            return
        }

        val uris = ArrayList<Uri>()
        val clipData = data.clipData
        if (clipData != null) {
            for (i in 0 until clipData.itemCount) {
                uris.add(clipData.getItemAt(i).uri)
            }
        } else {
            data.data?.let { uris.add(it) }
        }

        if (uris.isEmpty()) {
            toast(ctx.getString(R.string.face_train_no_photos))
            showPersonListDialog()
            return
        }

        for (uri in uris) {
            try {
                ctx.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (t: Throwable) {
                Log.w(TAG, "Could not persist permission for $uri")
            }
        }

        // Mark training as running before anything else. From here on, whenever the
        // brick runs or a dialog is asked for, the progress bar is what appears,
        // even if the stage was destroyed and the program restarted meanwhile.
        trainingInProgress = true
        trainingName = name
        progressTotal = uris.size
        progressDone = 0

        showProgressDialog()
        runTraining(ctx, current, name, uris)
    }

    private fun runTraining(
        ctx: Context,
        current: Recognizer,
        name: String,
        uris: ArrayList<Uri>
    ) {
        val onProgress = Recognizer.ProgressListener { done, total ->
            updateProgress(done, total)
        }

        worker.execute {
            var added = 0
            var error: String? = null
            var lines = listOf<String>()
            try {
                val result = current.extractEmbeddings(ctx.contentResolver, uris, onProgress)
                lines = result.report
                added = result.embeddings.size
                if (added > 0) {
                    current.addEmbeddings(current.addPerson(name), result.embeddings)
                }
            } catch (t: Throwable) {
                error = "${t.javaClass.simpleName}: ${t.message}"
                Log.e(TAG, "Training failed", t)
            } finally {
                val outcome = when {
                    error != null -> ctx.getString(R.string.face_train_error, error)
                    added == 0 -> ctx.getString(R.string.face_train_no_face)
                    else -> ctx.getString(R.string.face_train_success)
                }
                Log.i(TAG, "$outcome | " + lines.joinToString(" | "))

                val shownFor = System.currentTimeMillis() - progressShownAt
                val wait = if (progressDialog != null && shownFor < MIN_PROGRESS_MS) {
                    MIN_PROGRESS_MS - shownFor
                } else {
                    0L
                }

                mainHandler.postDelayed({
                                            trainingInProgress = false
                                            trainingName = null
                                            dismissProgressDialog(true)

                                            if (liveActivity() == null) {
                                                // No window. Keep the result and show it when the brick runs.
                                                pendingOutcome = outcome
                                            } else {
                                                toast(outcome)
                                                showPersonListDialog()
                                            }
                                        }, wait)
            }
        }
    }

    /** Opens the main menu against whatever activity is current. Tests only. */
    @VisibleForTesting
    fun openMenuForTest(context: Context) {
        appContext = context.applicationContext
        currentInstance = this
        if (initRecognizer()) {
            showCurrentScreen()
        }
    }

    /** Keeps everything. The picker can outlive this action by minutes. */
    override fun reset() {
        dialog = null
    }
}