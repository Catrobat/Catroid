package org.catrobat.catroid.content.actions

import android.app.AlertDialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.FaceRecognizer.Recognizer
import org.catrobat.catroid.FaceRecognizer.env.FileUtils
import org.catrobat.catroid.FaceRecognizer.env.Logger
import org.catrobat.catroid.stage.StageActivity
import java.util.concurrent.Executors

class FaceNameTrainAction : Action() {
    private var context: Context? = null
    private var recognizer: Recognizer? = null
    private var initialized = false
    private var training = false
    private var dialog: AlertDialog? = null
    private val LOGGER = Logger()

    companion object {
        var currentInstance: FaceNameTrainAction? = null
    }

    override fun act(delta: Float): Boolean {
        currentInstance = this
        context = StageActivity.activeStageActivity.get() ?: return false

        FileUtils.initializeRoot(context)

        Executors.newSingleThreadExecutor().execute {
            initRecognizer()
            while (!initialized) {
                Thread.sleep(100)
            }
            StageActivity.activeStageActivity.get()?.runOnUiThread {
                showInteractivePopup()
            }
        }
        return true
    }

    private fun initRecognizer() {
        val assets = context!!.assets
        FileUtils.copyAsset(assets, FileUtils.DATA_FILE)
        FileUtils.copyAsset(assets, FileUtils.MODEL_FILE)
        FileUtils.copyAsset(assets, FileUtils.LABEL_FILE)

        try {
            recognizer = Recognizer.getInstance(assets)
            initialized = true
        } catch (e: Exception) {
            LOGGER.e("Recognizer init failed", e)
        }
    }

    private fun showInteractivePopup() {
        val builder = AlertDialog.Builder(context!!)
        builder.setCancelable(true)

        val root = LinearLayout(context)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(60, 40, 60, 40)
        root.setBackgroundColor(Color.parseColor("#FFF9C4"))

        val title = TextView(context)
        title.text = "🎨 Select or Add a Friend Photos!"
        title.textSize = 22f
        title.setTextColor(Color.parseColor("#6A1B9A"))
        title.gravity = Gravity.CENTER_HORIZONTAL
        title.setPadding(0, 0, 0, 30)
        title.setTypeface(null, android.graphics.Typeface.BOLD)
        root.addView(title)

        val scrollView = ScrollView(context)
        val nameList = LinearLayout(context)
        nameList.orientation = LinearLayout.VERTICAL
        scrollView.addView(nameList)
        root.addView(scrollView)

        val names = recognizer?.classNames?.map { it.toString() }?.toTypedArray() ?: arrayOf()

        for (i in 1 until names.size) {
            val index = i - 1
            val personRow = LinearLayout(context)
            personRow.orientation = LinearLayout.HORIZONTAL
            personRow.setPadding(0, 10, 0, 10)

            val nameButton = Button(context)
            nameButton.text = " \uD83E\uDDD2 " + names[i]
            nameButton.setAllCaps(false)
            nameButton.setBackgroundColor(Color.parseColor("#AED581"))
            nameButton.setTextColor(Color.BLACK)
            nameButton.setPadding(30, 25, 30, 25)
            nameButton.textSize = 18f
            nameButton.setOnClickListener {
                dialog?.dismiss()
                openImagePicker(index)
            }
            val nameParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            nameButton.layoutParams = nameParams

            val deleteButton = Button(context)
            deleteButton.text = "🗑️"
            deleteButton.setBackgroundColor(Color.parseColor("#FFCDD2"))
            deleteButton.setTextColor(Color.BLACK)
            deleteButton.setPadding(20, 20, 20, 20)
            deleteButton.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("Delete Friend?")
                    .setMessage("Are you sure you want to delete \"${names[i]}\"?")
                    .setPositiveButton("Yes") { _, _ ->
                        recognizer?.deletePerson(index)
                        dialog?.dismiss()
                        showInteractivePopup()
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
            val deleteParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            deleteParams.marginStart = 16
            deleteButton.layoutParams = deleteParams

            personRow.addView(nameButton)
            personRow.addView(deleteButton)
            nameList.addView(personRow)
        }

        val addNewButton = Button(context)
        addNewButton.text = "+ Add New Friend Name"
        addNewButton.setBackgroundColor(Color.parseColor("#F06292"))
        addNewButton.setTextColor(Color.WHITE)
        addNewButton.textSize = 18f
        addNewButton.setPadding(40, 30, 40, 30)
        addNewButton.setAllCaps(false)
        val addParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        addParams.topMargin = 50
        addNewButton.layoutParams = addParams
        addNewButton.setOnClickListener {
            dialog?.dismiss()
            showNewNameDialog()
        }

        root.addView(addNewButton)
        builder.setView(root)
        dialog = builder.create()
        dialog?.show()
    }

    private fun showNewNameDialog() {
        val input = EditText(context)
        input.hint = "Enter a friendly name 🧒"
        input.setPadding(50, 30, 50, 30)

        val layout = LinearLayout(context)
        layout.setPadding(60, 40, 60, 20)
        layout.addView(input)

        AlertDialog.Builder(context!!)
            .setTitle("🎁 Add New Friend Name")
            .setView(layout)
            .setPositiveButton("Add") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    val idx = recognizer!!.addPerson(name)
                    openImagePicker(idx - 1)
                } else {
                    Toast.makeText(context, "Name required", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun openImagePicker(requestCode: Int) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        StageActivity.activeStageActivity.get()?.startActivityForResult(
            intent,
            requestCode + 1000
        )
    }

    fun handleResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!initialized || resultCode != StageActivity.RESULT_OK || data == null) {
            Toast.makeText(context, "Try again later", Toast.LENGTH_SHORT).show()
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

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Training in Progress")

        val progressBar = ProgressBar(context)
        progressBar.isIndeterminate = true

        val layout = LinearLayout(context)
        layout.setPadding(60, 40, 60, 20)
        layout.gravity = Gravity.CENTER
        layout.addView(progressBar)

        builder.setView(layout)
        builder.setCancelable(false)

        val trainingDialog = builder.create()
        trainingDialog.show()

        training = true

        Thread {
            try {
                recognizer?.updateData(requestCode, context!!.contentResolver, uris)
            } catch (e: Exception) {
                LOGGER.e("Training exception", e)
            } finally {
                training = false
                StageActivity.activeStageActivity.get()?.runOnUiThread {
                    trainingDialog.dismiss()
                    Toast.makeText(context, "Training complete!", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    override fun reset() {
        currentInstance = null
        context = null
        recognizer = null
        initialized = false
        training = false
    }
}
