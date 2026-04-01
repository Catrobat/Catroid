package org.catrobat.catroid.content.actions

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.Fragment
import android.app.FragmentManager
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.ObjectTrainAndRecognition.ml.EmbeddingUtils
import org.catrobat.catroid.ObjectTrainAndRecognition.persist.ModelMeta
import org.catrobat.catroid.ObjectTrainAndRecognition.persist.ModelSnapshotIO
import org.catrobat.catroid.stage.StageActivity
import java.io.InputStream
import java.lang.ref.WeakReference
import java.util.LinkedHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.roundToInt

class ObjectlevelTrainAction : Action() {

    var scope: org.catrobat.catroid.content.Scope? = null

    private var started = false
    private var finished = false

    private val io: ExecutorService = Executors.newSingleThreadExecutor()
    private val pending = mutableListOf<String>()
    private var snapshotLabelMap: MutableMap<Int, String> = linkedMapOf()
    private var currentPickLabel: String? = null

    private var mainDialog: Dialog? = null
    private var listContainer: LinearLayout? = null

    // platform-fragment host (no androidx)
    private var pickerHostRef: WeakReference<LegacyResultHostFragment>? = null

    // ---------------- 🎨 CHILD-FRIENDLY PASTEL THEME (colors only) ----------------
    private val COLOR_BG       = 0xFFFFFBF0.toInt()  // warm cream background
    private val COLOR_TITLE    = 0xFF6A1B9A.toInt()  // playful purple
    private val COLOR_TEXT     = 0xFF374151.toInt()  // soft ink gray
    private val COLOR_ADD_BTN  = 0xFFA5D6A7.toInt()  // mint green
    private val COLOR_TRAIN    = 0xFF90CAF9.toInt()  // sky blue
    private val COLOR_DELETE   = 0xFFFFAB91.toInt()  // peach
    private val COLOR_BLACK    = 0xFF000000.toInt()  // for readable text on colored buttons

    override fun act(delta: Float): Boolean {
        if (finished) return true
        val activity = StageActivity.activeStageActivity.get() ?: return false
        if (!started) {
            started = true
            activity.runOnUiThread {
                ensureResultHost(activity)
                showMainDialog(activity)
                refreshFromSnapshot(activity)
            }
        }
        return finished
    }

    override fun reset() {
        super.reset()
        scope = null
        started = false
        finished = false
        snapshotLabelMap.clear()
        pending.clear()
        currentPickLabel = null
        mainDialog = null
        listContainer = null
        io.shutdownNow()
    }

    // ---------------- UI ----------------

    private fun showMainDialog(activity: Activity) {
        if (mainDialog?.isShowing == true) return

        val dp = { v: Int -> (v * activity.resources.displayMetrics.density).roundToInt() }

        val root = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(14), dp(20), dp(20))
            // THEME: background
            setBackgroundColor(COLOR_BG)
        }

        val title = TextView(activity).apply {
            text = "🎨 Object Trainer"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(0, 0, 0, dp(8))
            // THEME: title color
            setTextColor(COLOR_TITLE)
        }
        root.addView(title, lpMatch())

        val subtitle = TextView(activity).apply {
            text = "Tap a name to pick photos and train. Add or delete labels anytime."
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setPadding(0, 0, 0, dp(12))
            // THEME: subtitle/body color
            setTextColor(COLOR_TEXT)
        }
        root.addView(subtitle, lpMatch())

        val scroll = ScrollView(activity).apply { isFillViewport = true }
        val list = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
        }
        scroll.addView(
            list,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
            )
        )
        listContainer = list

        val addBtn = Button(activity).apply {
            text = "+ Add New Object Name"
            isAllCaps = false
            // THEME: add button
            setBackgroundColor(COLOR_ADD_BTN)
            setTextColor(COLOR_BLACK)
            setOnClickListener { showAddDialog(activity) }
        }
        root.addView(addBtn, lpMatch().apply { topMargin = dp(12) })

        val closeBtn = Button(activity).apply {
            text = "Close"
            isAllCaps = false
            // THEME: make close neutral but readable
            setBackgroundColor(0xFFE5E7EB.toInt()) // light gray
            setTextColor(COLOR_BLACK)
            setOnClickListener { mainDialog?.dismiss() }
        }
        root.addView(closeBtn, lpMatch().apply { topMargin = dp(6) })

        mainDialog = AlertDialog.Builder(activity)
            .setView(root)
            .setOnDismissListener { finished = true }
            .create()
            .also { it.show() }
    }

    private fun renderRows(activity: Activity) {
        val container = listContainer ?: return
        container.removeAllViews()
        val dp = { v: Int -> (v * activity.resources.displayMetrics.density).roundToInt() }

        data class Row(val id: Int?, val name: String)
        val rows = mutableListOf<Row>()
        if (snapshotLabelMap.isNotEmpty()) {
            val keys = snapshotLabelMap.keys.toMutableList().apply { sort() }
            for (k in keys) rows += Row(k, snapshotLabelMap[k] ?: "")
        }
        for (nm in pending) rows += Row(null, nm)

        rows.forEach { row ->
            val line = LinearLayout(activity).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, dp(6), 0, dp(6))
            }
            val train = Button(activity).apply {
                isAllCaps = false
                text = "🧪  ${row.name}"
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                // THEME: train button
                setBackgroundColor(COLOR_TRAIN)
                setTextColor(COLOR_BLACK)
                setOnClickListener {
                    currentPickLabel = row.name
                    launchPickerForImages(activity)
                }
            }
            val del = Button(activity).apply {
                text = "🗑️"
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { leftMargin = dp(8) }
                // THEME: delete button
                setBackgroundColor(COLOR_DELETE)
                setTextColor(COLOR_BLACK)
                setOnClickListener {
                    if (row.id == null) {
                        pending.removeAll { it.equals(row.name, ignoreCase = true) }
                        renderRows(activity)
                    } else {
                        AlertDialog.Builder(activity)
                            .setTitle("Delete '${row.name}'?")
                            .setMessage("This will remove all samples for this label.")
                            .setPositiveButton("Yes") { _, _ ->
                                io.execute {
                                    val ok = try {
                                        ModelSnapshotIO.removeLabelAndResave(activity, row.id)
                                    } catch (_: Exception) { false }
                                    activity.runOnUiThread {
                                        toast(activity, if (ok) "Deleted ${row.name}" else "Delete failed.")
                                        refreshFromSnapshot(activity)
                                    }
                                }
                            }
                            .setNegativeButton("No", null)
                            .show()
                    }
                }
            }
            line.addView(train)
            line.addView(del)
            val wrapper = LinearLayout(activity).apply {
                orientation = LinearLayout.VERTICAL
                addView(line)
            }
            container.addView(wrapper, lpMatch().apply { bottomMargin = dp(8) })
        }
    }

    private fun showAddDialog(activity: Activity) {
        val dp = { v: Int -> (v * activity.resources.displayMetrics.density).roundToInt() }
        val input = EditText(activity).apply {
            hint = "Enter an object name 🧩"
            setPadding(dp(16), dp(12), dp(16), dp(12))
        }
        val lay = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(16), dp(20), dp(8))
            addView(input)
            // THEME: dialog background to match app bg
            setBackgroundColor(COLOR_BG)
        }
        AlertDialog.Builder(activity)
            .setTitle("🎁 Add New Object Name")
            .setView(lay)
            .setPositiveButton("Add") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isEmpty()) {
                    toast(activity, "Name required"); return@setPositiveButton
                }
                var exists = snapshotLabelMap.values.any { it.equals(name, ignoreCase = true) }
                if (!exists) exists = pending.any { it.equals(name, ignoreCase = true) }
                if (exists) {
                    toast(activity, "Already exists."); return@setPositiveButton
                }
                pending += name
                renderRows(activity)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ---------------- Data / Training ----------------

    private fun refreshFromSnapshot(activity: Activity) {
        io.execute {
            val snap = ModelSnapshotIO.loadLatest(activity)
            snapshotLabelMap = linkedMapOf<Int, String>().apply {
                val lm = snap?.meta?.labelMap
                if (lm != null) putAll(lm)
            }
            activity.runOnUiThread { renderRows(activity) }
        }
    }

    private fun launchPickerForImages(activity: Activity) {
        val host = ensureResultHost(activity)
        host.setCallback { uris ->
            if (uris.isNullOrEmpty()) return@setCallback
            val label = currentPickLabel ?: return@setCallback
            val progress = showProgress(activity, "Training in progress…")
            io.execute {
                try {
                    val latest = ModelSnapshotIO.loadLatest(activity)
                    val existingMap = (latest?.meta?.labelMap?.let { LinkedHashMap(it) } ?: LinkedHashMap())
                    val labelId = EmbeddingUtils.resolveOrCreateLabelId(existingMap, label)

                    val newEmb = ArrayList<Float>()
                    val newLab = ArrayList<Int>()

                    for (u in uris) {
                        try {
                            activity.contentResolver.openInputStream(u)?.use { inp ->
                                val bytes = readAllBytesCompat(inp)
                                val emb = EmbeddingUtils.bitmapBytesToEmbedding(bytes)
                                emb.forEach { v -> newEmb.add(v) }
                                newLab.add(labelId)
                            }
                        } catch (_: Exception) { /* skip */ }
                    }
                    if (newLab.isEmpty()) throw IllegalStateException("No valid images selected.")

                    val dim = EmbeddingUtils.EMBEDDING_DIM
                    val finalEmb: FloatArray
                    val finalLab: IntArray

                    val mergedMap = LinkedHashMap<Int, String>().apply {
                        putAll(existingMap)
                        put(labelId, label)
                    }

                    if (latest != null) {
                        finalEmb = FloatArray(latest.embeddings.size + newEmb.size).also { arr ->
                            System.arraycopy(latest.embeddings, 0, arr, 0, latest.embeddings.size)
                            var base = latest.embeddings.size
                            for (v in newEmb) arr[base++] = v
                        }
                        finalLab = IntArray(latest.labels.size + newLab.size).also { arr ->
                            System.arraycopy(latest.labels, 0, arr, 0, latest.labels.size)
                            for (i in newLab.indices) arr[latest.labels.size + i] = newLab[i]
                        }
                    } else {
                        finalEmb = FloatArray(newEmb.size).also { arr ->
                            newEmb.forEachIndexed { i, v -> arr[i] = v }
                        }
                        finalLab = IntArray(newLab.size).also { arr ->
                            newLab.forEachIndexed { i, v -> arr[i] = v }
                        }
                    }

                    val meta = ModelMeta.createDefault(
                        System.currentTimeMillis(),
                        dim,
                        finalLab.size,
                        true,
                        mergedMap,
                        "1.0",
                        "Train '$label' +${newLab.size} imgs"
                    )

                    ModelSnapshotIO.save(activity, meta, finalEmb, finalLab)

                    activity.runOnUiThread {
                        toast(activity, "Trained '$label' ✅")
                        pending.removeAll { it.equals(label, ignoreCase = true) }
                        refreshFromSnapshot(activity)
                    }
                } catch (e: Exception) {
                    activity.runOnUiThread { toast(activity, "Training failed: ${e.message}") }
                } finally {
                    activity.runOnUiThread { progress.dismiss() }
                }
            }
        }
        host.launchGetMultiple()
    }

    // ---------------- Platform Fragment host ----------------

    class LegacyResultHostFragment : Fragment() {
        private var onPicked: ((List<Uri>?) -> Unit)? = null

        fun setCallback(cb: (List<Uri>?) -> Unit) {
            onPicked = cb
        }

        fun launchGetMultiple() {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            startActivityForResult(Intent.createChooser(intent, "Select images"), REQ_PICK)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == REQ_PICK && resultCode == Activity.RESULT_OK) {
                val uris = mutableListOf<Uri>()
                data?.let {
                    val clip: ClipData? = it.clipData
                    val single: Uri? = it.data
                    if (clip != null) {
                        for (i in 0 until clip.itemCount) {
                            clip.getItemAt(i)?.uri?.let { u -> uris.add(u) }
                        }
                    } else if (single != null) {
                        uris.add(single)
                    }
                }
                onPicked?.invoke(if (uris.isEmpty()) null else uris)
            }
        }

        companion object {
            private const val REQ_PICK = 6421
        }
    }

    private fun ensureResultHost(activity: Activity): LegacyResultHostFragment {
        pickerHostRef?.get()?.let { return it }
        val tag = "otr_legacy_result_host"
        val fm: FragmentManager = activity.fragmentManager
        val existing = fm.findFragmentByTag(tag) as? LegacyResultHostFragment
        val host = existing ?: LegacyResultHostFragment().also {
            fm.beginTransaction().add(it, tag).commitAllowingStateLoss()
            fm.executePendingTransactions()
        }
        pickerHostRef = WeakReference(host)
        return host
    }

    // ---------------- Helpers ----------------

    private fun showProgress(activity: Activity, message: String): Dialog {
        val dp = { v: Int -> (v * activity.resources.displayMetrics.density).roundToInt() }
        val layout = LinearLayout(activity).apply {
            setPadding(dp(24), dp(20), dp(24), dp(20))
            gravity = Gravity.CENTER
            orientation = LinearLayout.VERTICAL
            // THEME: light background and readable text
        }
        val bar = ProgressBar(activity)
        val tv = TextView(activity).apply {
            text = message
            setPadding(0, dp(10), 0, 0)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setTextColor(COLOR_TEXT)
        }
        layout.addView(bar)
        layout.addView(tv)
        return AlertDialog.Builder(activity)
            .setView(layout)
            .setCancelable(false)
            .create().also { it.show() }
    }

    private fun toast(ctx: Context, msg: String) =
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()

    private fun lpMatch() = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )

    companion object {
        @JvmStatic
        fun readAllBytesCompat(`in`: InputStream): ByteArray {
            val baos = java.io.ByteArrayOutputStream()
            val buf = ByteArray(8192)
            var n: Int
            while (`in`.read(buf).also { n = it } != -1) {
                baos.write(buf, 0, n)
            }
            return baos.toByteArray()
        }
    }
}
