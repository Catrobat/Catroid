package org.catrobat.catroid.ObjectTrainAndRecognition.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.catrobat.catroid.ObjectTrainAndRecognition.infer.EmbeddingKnn
import org.catrobat.catroid.ObjectTrainAndRecognition.ml.EmbeddingUtils
import org.catrobat.catroid.ObjectTrainAndRecognition.persist.ModelSnapshotIO
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ObjectPredictActivityKotlin : AppCompatActivity() {

    companion object {
        const val EXTRA_LABEL_NAME = "predictedLabel"
        const val EXTRA_ERROR = "error"
        private const val THRESHOLD = 0.95f
    }

    private val pickOne =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            onPicked(uris)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No UI — launch picker immediately
        pickOne.launch("image/*")
    }

    private fun onPicked(uris: List<Uri>?) {
        if (uris.isNullOrEmpty()) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }
        val u = uris.first()

        Thread {
            try {
                val snap = ModelSnapshotIO.loadLatest(this)
                if (snap == null) {
                    setResult(
                        Activity.RESULT_CANCELED,
                        Intent().putExtra(EXTRA_ERROR, "No snapshot found. Train first.")
                    )
                    finish()
                    return@Thread
                }

                val emb: FloatArray = contentResolver.openInputStream(u).use { inp ->
                    if (inp == null) error("open URI failed")
                    val bytes = readAllBytesCompat(inp)
                    EmbeddingUtils.bitmapBytesToEmbedding(bytes)
                }

                val knn = EmbeddingKnn(
                    snap.embeddings,
                    snap.labels,
                    snap.meta.embeddingDim,
                    snap.meta.l2Normalized
                )
                val hit = knn.predict(emb)

                val labelName: String = if (hit == null || hit.score < THRESHOLD) {
                    "Unknown"
                } else {
                    // Robust label lookup (supports Int keys and accidental String keys)
                    val fromInt = snap.meta.labelMap[hit.label]
                    val fromStringKey = try {
                        @Suppress("UNCHECKED_CAST")
                        (snap.meta.labelMap as Map<Any?, Any?>)[hit.label.toString()] as? String
                    } catch (_: Exception) { null }
                    fromInt ?: fromStringKey ?: hit.label.toString()
                }

                setResult(Activity.RESULT_OK, Intent().apply {
                    putExtra(EXTRA_LABEL_NAME, labelName)
                })
                finish()

            } catch (e: Exception) {
                setResult(
                    Activity.RESULT_CANCELED,
                    Intent().putExtra(EXTRA_ERROR, e.message ?: "Unknown error")
                )
                finish()
            }
        }.start()
    }

    private fun readAllBytesCompat(input: InputStream): ByteArray {
        val buffer = ByteArray(8 * 1024)
        val baos = ByteArrayOutputStream()
        while (true) {
            val n = input.read(buffer)
            if (n < 0) break
            baos.write(buffer, 0, n)
        }
        return baos.toByteArray()
    }
}
