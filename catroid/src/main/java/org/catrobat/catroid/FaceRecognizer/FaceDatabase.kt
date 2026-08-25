package org.catrobat.catroid.FaceRecognizer

import android.util.Log
import org.catrobat.catroid.FaceRecognizer.env.FileUtils
import org.catrobat.catroid.FaceRecognizer.env.FileUtils.file
import org.catrobat.catroid.FaceRecognizer.env.FileUtils.isReady
import org.catrobat.catroid.FaceRecognizer.env.FileUtils.readLines
import org.catrobat.catroid.FaceRecognizer.env.FileUtils.writeLines
import org.catrobat.catroid.FaceRecognizer.ml.FaceNet
import java.util.Arrays
import java.util.Locale
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Owns the three files and does the matching.
 *
 * label : one person name per line. The line number is the person index.
 * data  : one training embedding per line, "<index> v0 v1 ... v511".
 * model : compiled matcher, rebuilt from data on every change.
 * line 1  "v1 <embeddingSize> <personCount> <minSimilarity> <minMargin>"
 * then    "<index> <sampleCount> c0 c1 ... c511"   centroid, unit length
 *
 * All three are written together from memory, so their indices can never drift apart.
 * Matching score for a person is a blend of the nearest training samples and the
 * centroid, both cosine similarities on unit length embeddings.
 */
class FaceDatabase {
    /** True when the stored embeddings were made by a different pipeline.  */
    private var staleEmbeddings = false

    @Synchronized
    fun hasStaleEmbeddings(): Boolean {
        return staleEmbeddings
    }

    class Match internal constructor(
        val index: Int,
        @JvmField val name: String?,
        @JvmField val similarity: Float,
        @JvmField val margin: Float
    )

    private val names: MutableList<String> = ArrayList<String>()
    private val samples: MutableList<MutableList<FloatArray>> = ArrayList<MutableList<FloatArray>>()
    private val centroids: MutableList<FloatArray?> = ArrayList<FloatArray?>()

    private var loadedStamp = -1L

    // ---------------- Load and save ----------------
    @Synchronized
    fun load() {
        names.clear()
        samples.clear()
        centroids.clear()

        for (name in readLines(FileUtils.LABEL_FILE)) {
            names.add(name)
            samples.add(ArrayList())
            centroids.add(null)
        }

        var loaded = 0
        var skipped = 0
        for (line in readLines(FileUtils.DATA_FILE)) {
            val parts = line.split(" ").filter { it.isNotEmpty() }
            if (parts.size != FaceNet.EMBEDDING_SIZE + 1) {
                skipped++
                continue
            }
            try {
                val index = parts[0].toInt()
                if (index < 0 || index >= names.size) {
                    skipped++
                    continue
                }
                val v = FloatArray(FaceNet.EMBEDDING_SIZE)
                for (i in v.indices) {
                    v[i] = parts[i + 1].toFloat()
                }
                samples[index].add(v)
                loaded++
            } catch (e: NumberFormatException) {
                skipped++
            }
        }

        staleEmbeddings = false
        if (!loadModel()) {
            rebuildCentroids()
            // No readable model file, so the pipeline that made the data is unknown.
            if (samples.isNotEmpty() && readLines(FileUtils.DATA_FILE).isNotEmpty()) {
                staleEmbeddings = true
                Log.w(
                    TAG, "No model header, cannot tell which pipeline made these "
                        + "photos. Retrain if detection returns Unknown."
                )
            }
        }
        stamp()

        Log.i(
            TAG, ("Loaded " + names.size + " people, " + loaded
                + " embeddings, " + skipped + " bad lines skipped")
        )
    }

    /** Reloads if another process changed the files. Cheap, safe to call often.  */
    @Synchronized
    fun ensureFresh() {
        if (currentStamp() != loadedStamp) {
            Log.i(TAG, "Face files changed on disk, reloading")
            load()
        }
    }

    @Synchronized
    fun save(): Boolean {
        rebuildCentroids()

        val dataLines: MutableList<String> = ArrayList<String>()
        for (index in samples.indices) {
            for (v in samples[index]) {
                dataLines.add(vectorLine(index, v, -1))
            }
        }

        val modelLines: MutableList<String> = ArrayList<String>()
        modelLines.add(
            String.format(
                Locale.US, "v%d %d %d %.4f %.4f p%d",
                VERSION, FaceNet.EMBEDDING_SIZE, names.size, minSimilarity, minMargin,
                PIPELINE_VERSION
            )
        )
        staleEmbeddings = false
        for (index in centroids.indices) {
            val c = centroids[index]
            if (c != null) {
                modelLines.add(vectorLine(index, c, samples[index].size))
            }
        }

        var ok = writeLines(FileUtils.LABEL_FILE, ArrayList<String>(names))
        ok = ok and writeLines(FileUtils.DATA_FILE, dataLines)
        ok = ok and writeLines(FileUtils.MODEL_FILE, modelLines)
        stamp()

        Log.i(
            TAG, ("Saved label(" + names.size + ") data(" + dataLines.size
                + ") model(" + (modelLines.size - 1) + ") ok=" + ok)
        )
        return ok
    }

    private fun loadModel(): Boolean {
        val lines = readLines(FileUtils.MODEL_FILE)

        if (lines.isEmpty()) {
            return false
        }

        val header = splitLine(lines.first())

        if (!isValidHeader(header)) {
            Log.w(TAG, "Unknown model format, rebuilding from data")
            return false
        }

        if (!loadModelMetadata(header)) {
            return false
        }

        return loadCentroids(lines) > 0
    }

    private fun splitLine(line: String): List<String> =
        line.split(" ").filter { it.isNotEmpty() }

    private fun isValidHeader(header: List<String>): Boolean =
        header.size >= 3 && header[0] == "v$VERSION"

    private fun loadModelMetadata(header: List<String>): Boolean {
        return try {
            if (!modelMatchesLabels(header)) {
                Log.w(TAG, "Model does not match label file, rebuilding from data")
                false
            } else {
                loadThresholds(header)
                checkPipelineVersion(header)
                true
            }
        } catch (e: NumberFormatException) {
            Log.w(TAG, "Invalid numeric value in model header", e)
            false
        }
    }

    private fun modelMatchesLabels(header: List<String>): Boolean =
        header[1].toInt() == FaceNet.EMBEDDING_SIZE &&
            header[2].toInt() == names.size

    private fun loadThresholds(header: List<String>) {
        if (header.size < 5) {
            return
        }

        minSimilarity = header[3].toFloat()
        minMargin = header[4].toFloat()

        Log.i(TAG, "Thresholds from model: $minSimilarity / $minMargin")
    }

    private fun checkPipelineVersion(header: List<String>) {
        val storedPipeline = readPipelineVersion(header)

        if (storedPipeline == PIPELINE_VERSION) {
            return
        }

        staleEmbeddings = true

        Log.e(
            TAG,
            "STORED PHOTOS ARE OUT OF DATE. They were made by face " +
                "pipeline v$storedPipeline, this build uses v$PIPELINE_VERSION. " +
                "They cannot match a new capture at any threshold. Delete each " +
                "person and add their photos again."
        )
    }

    private fun readPipelineVersion(header: List<String>): Int {
        val pipelineValue = header.getOrNull(5) ?: return 1

        return if (pipelineValue.startsWith("p")) {
            pipelineValue.substring(1).toInt()
        } else {
            1
        }
    }

    private fun loadCentroids(lines: List<String>): Int {
        var readCount = 0

        for (i in 1 until lines.size) {
            if (loadCentroid(lines[i])) {
                readCount++
            }
        }

        return readCount
    }

    private fun loadCentroid(line: String): Boolean {
        val parts = splitLine(line)

        if (parts.size != FaceNet.EMBEDDING_SIZE + 2) {
            return false
        }

        return try {
            storeCentroid(parts)
        } catch (e: NumberFormatException) {
            // Ignore malformed model lines.
            false
        }
    }

    private fun storeCentroid(parts: List<String>): Boolean {
        val index = parts[0].toInt()

        if (index !in names.indices) {
            return false
        }

        val centroid = FloatArray(FaceNet.EMBEDDING_SIZE) { position ->
            parts[position + 2].toFloat()
        }

        centroids[index] = centroid
        return true
    }

    private fun rebuildCentroids() {
        centroids.clear()
        for (list in samples) {
            centroids.add(meanUnit(list))
        }
    }

    private fun stamp() {
        loadedStamp = currentStamp()
    }

    private fun currentStamp(): Long {
        if (!isReady) {
            return -1L
        }
        return (file(FileUtils.LABEL_FILE).lastModified() * 31L
            + file(FileUtils.DATA_FILE).lastModified())
    }

    // ---------------- People ----------------
    /** Changes the thresholds and writes them into the model file header.  */
    @Synchronized
    fun setThresholds(similarity: Float, margin: Float) {
        minSimilarity = max(0.05f, min(0.95f, similarity))
        minMargin = max(0f, min(0.5f, margin))
        Log.i(TAG, "Thresholds set to " + minSimilarity + " / " + minMargin)
        save()
    }

    @Synchronized
    fun getNames(): MutableList<String> {
        return ArrayList<String>(names)
    }

    @get:Synchronized
    val personCount: Int
        get() = names.size

    @Synchronized
    fun getEmbeddingCount(index: Int): Int {
        if (index < 0 || index >= samples.size) {
            return 0
        }
        return samples[index].size
    }

    /**
     * Average cosine similarity between a person's own training photos.
     * Healthy FaceNet embeddings of one person sit around 0.6 to 0.9 here.
     * A value near 0.2 means the embeddings carry no identity information at all,
     * which is a preprocessing or model problem, not a threshold problem.
     */
    /**
     * For each stored photo of a person, its similarity to the closest OTHER photo
     * of the same person. A value near zero means that photo resembles none of the
     * others, which usually means a wrong person or a bad crop rather than a
     * genuine age difference.
     */
    @Synchronized
    fun photoAffinities(index: Int): FloatArray? {
        if (index < 0 || index >= samples.size) {
            return null
        }
        val list = samples[index]
        val out = FloatArray(list.size)
        for (i in list.indices) {
            var best: Float = NO_SCORE
            for (j in list.indices) {
                if (i == j) {
                    continue
                }
                best = max(best, dot(list.get(i), list.get(j)))
            }
            out[i] = best
        }
        return out
    }

    /** Drops photos that resemble none of the person's other photos.  */
    @Synchronized
    fun removeStrayPhotos(index: Int, minAffinity: Float): Int {
        val affinities = photoAffinities(index)
        if (affinities == null || affinities.size < 3) {
            return 0
        }
        val list = samples[index]
        var removed = 0
        for (i in affinities.indices.reversed()) {
            if (affinities[i] < minAffinity) {
                list.removeAt(i)
                removed++
            }
        }
        if (removed > 0) {
            save()
        }
        return removed
    }

    @Synchronized
    fun internalConsistency(index: Int): Float {
        if (index < 0 || index >= samples.size) {
            return NO_SCORE
        }
        val list = samples[index]
        if (list.size < 2) {
            return NO_SCORE
        }
        var sum = 0f
        var pairs = 0
        for (i in list.indices) {
            for (j in i + 1..<list.size) {
                sum += dot(list.get(i), list.get(j))
                pairs++
            }
        }
        return if (pairs == 0) NO_SCORE else sum / pairs
    }

    /** Average similarity between two different people's photos. Should be low.  */
    @Synchronized
    fun crossSimilarity(a: Int, b: Int): Float {
        if (a < 0 || b < 0 || a >= samples.size || b >= samples.size) {
            return NO_SCORE
        }
        val la = samples[a]
        val lb = samples[b]
        if (la.isEmpty() || lb.isEmpty()) {
            return NO_SCORE
        }
        var sum = 0f
        var pairs = 0
        for (va in la) {
            for (vb in lb) {
                sum += dot(va, vb)
                pairs++
            }
        }
        return if (pairs == 0) NO_SCORE else sum / pairs
    }

    /** First stored embedding for a person, or null. Used by the self test.  */
    @Synchronized
    fun getFirstEmbedding(index: Int): FloatArray? {
        if (index < 0 || index >= samples.size || samples[index].isEmpty()) {
            return null
        }
        return samples[index].get(0).clone()
    }

    @Synchronized
    fun indexOf(name: String?): Int {
        for (i in names.indices) {
            if (names[i].equals(name, ignoreCase = true)) {
                return i
            }
        }
        return -1
    }

    @Synchronized
    fun addPerson(name: String): Int {
        names.add(name)
        samples.add(ArrayList())
        centroids.add(null)
        return names.size - 1
    }

    @Synchronized
    fun addEmbeddings(index: Int, list: List<FloatArray>?) {
        if (index < 0 || index >= samples.size || list == null) {
            return
        }
        for (v in list) {
            if (v.size == FaceNet.EMBEDDING_SIZE) {
                samples[index].add(v)
            }
        }
    }

    @Synchronized
    fun deletePerson(index: Int) {
        if (index < 0 || index >= names.size) {
            return
        }
        names.removeAt(index)
        samples.removeAt(index)
        centroids.removeAt(index)
    }

    // ---------------- Matching ----------------
    /** Returns the matched person, or null when nothing is close enough.  */
    /** Score for every enrolled person, in index order. Used for multi frame voting.  */
    @Synchronized
    fun scoreAll(query: FloatArray?): FloatArray? {
        if (query == null || query.size != FaceNet.EMBEDDING_SIZE || names.isEmpty()) {
            return null
        }
        val scores = FloatArray(names.size)
        for (i in names.indices) {
            scores[i] = personScore(i, query)
        }
        return scores
    }

    /**
     * Best score across several query variants of the same face. Element by element
     * maximum, so a person wins on whichever variant fits them best.
     */
    @Synchronized
    fun scoreAllVariants(queries: List<FloatArray>?): FloatArray? {
        if (queries.isNullOrEmpty()) {
            return null
        }

        var best: FloatArray? = null

        for (query in queries) {
            val scores = scoreAll(query) ?: continue
            best = mergeBestScores(best, scores)
        }

        return best
    }

    private fun mergeBestScores(
        currentBest: FloatArray?,
        newScores: FloatArray
    ): FloatArray {
        if (currentBest == null) {
            return newScores
        }

        val comparableSize = minOf(currentBest.size, newScores.size)

        for (index in 0 until comparableSize) {
            currentBest[index] = maxOf(
                currentBest[index],
                newScores[index]
            )
        }

        return currentBest
    }

    /** Applies the two thresholds to an already combined score array.  */
    @Synchronized
    fun decide(scores: FloatArray?): Match? {
        if (scores == null || scores.size != names.size || names.isEmpty()) {
            return null
        }

        var best: Float = NO_SCORE
        var second: Float = NO_SCORE
        var bestIndex = -1

        for (i in scores.indices) {
            if (scores[i] == NO_SCORE) {
                continue
            }
            if (scores[i] > best) {
                second = best
                best = scores[i]
                bestIndex = i
            } else if (scores[i] > second) {
                second = scores[i]
            }
        }

        if (bestIndex < 0) {
            return null
        }
        val margin = if (second == NO_SCORE) 1f else best - second
        val requiredSimilarity: Float = max(minSimilarity, ABSOLUTE_SAFETY_FLOOR)
        if (best < requiredSimilarity || margin < minMargin) {
            Log.i(
                TAG, String.format(
                    Locale.US,
                    "Rejected: best=%.3f margin=%.3f (need %.2f / %.2f)",
                    best, margin, requiredSimilarity, minMargin
                )
            )
            return null
        }
        return Match(bestIndex, names[bestIndex], best, margin)
    }

    @Synchronized
    fun describeScoreArray(scores: FloatArray?): String {
        if (scores == null) {
            return "no scores"
        }
        val sb = StringBuilder()
        var i = 0
        while (i < names.size && i < scores.size) {
            sb.append(String.format(Locale.US, "%s=%.3f ", names[i], scores[i]))
            i++
        }
        return sb.toString().trim { it <= ' ' }
    }

    @Synchronized
    fun match(query: FloatArray?): Match? {
        if (query == null || query.size != FaceNet.EMBEDDING_SIZE || names.isEmpty()) {
            return null
        }

        var best: Float = NO_SCORE
        var second: Float = NO_SCORE
        var bestIndex = -1

        for (i in names.indices) {
            val score = personScore(i, query)
            if (score == NO_SCORE) {
                continue
            }
            if (score > best) {
                second = best
                best = score
                bestIndex = i
            } else if (score > second) {
                second = score
            }
        }

        if (bestIndex < 0) {
            return null
        }

        val margin = if (second == NO_SCORE) 1f else best - second
        val requiredSimilarity: Float = max(minSimilarity, ABSOLUTE_SAFETY_FLOOR)
        if (best < requiredSimilarity || margin < minMargin) {
            Log.i(
                TAG, String.format(
                    Locale.US,
                    "Rejected: best=%.3f margin=%.3f (need %.2f / %.2f)",
                    best, margin, requiredSimilarity, minMargin
                )
            )
            return null
        }
        return Match(bestIndex, names[bestIndex], best, margin)
    }

    /** One line of every person's score. Use this to tune the two thresholds.  */
    @Synchronized
    fun describeScores(query: FloatArray?): String {
        if (query == null) {
            return "no query embedding"
        }
        val sb = StringBuilder()
        for (i in names.indices) {
            sb.append(
                String.format(
                    Locale.US, "%s=%.3f(%d photos) ",
                    names[i], personScore(i, query), samples[i].size
                )
            )
        }
        return sb.toString().trim { it <= ' ' }
    }

    private fun personScore(index: Int, query: FloatArray): Float {
        val list = samples[index]
        val centroid = centroids[index]

        if (list.isEmpty() && centroid == null) {
            return NO_SCORE
        }
        if (list.isEmpty()) {
            return Companion.dot(centroid!!, query)
        }

        val top = FloatArray(TOP_K)
        Arrays.fill(top, NO_SCORE)
        for (v in list) {
            val s: Float = dot(v, query)
            for (i in 0..<TOP_K) {
                if (s > top[i]) {
                    for (j in TOP_K - 1 downTo i + 1) {
                        top[j] = top[j - 1]
                    }
                    top[i] = s
                    break
                }
            }
        }

        var sum = 0f
        var count = 0
        for (t in top) {
            if (t != NO_SCORE) {
                sum += t
                count++
            }
        }
        val sampleScore = sum / count

        if (centroid == null) {
            return sampleScore
        }
        return SAMPLE_WEIGHT * sampleScore + (1f - SAMPLE_WEIGHT) * dot(centroid, query)
    }

    companion object {
        const val TAG: String = "FaceDatabase"

        /**
         * Accept a match only above this cosine similarity.
         * Raise it if strangers get a name. Lower it if known people come back Unknown.
         * Useful range 0.45 to 0.72.
         */
        @JvmField
        var minSimilarity: Float = 0.50f

        /**
         * The winner must beat the runner up by this much. This is what stops the app
         * putting the wrong person's name on a face.
         */
        @JvmField
        var minMargin: Float = 0.05f

        /** Never accept collapsed/no-detail embeddings even if an old model saved 0.50.  */
        private const val ABSOLUTE_SAFETY_FLOOR = 0.60f

        /**
         * Nearest neighbour. A person is scored against their single closest training
         * photo, not an average of several.
         *
         * This matters when one person is enrolled at different ages and angles. Only
         * the age matched and angle matched photo will be close, and averaging several
         * photos throws that signal away. Raise this only if every photo of a person
         * was taken in one sitting.
         */
        private const val TOP_K = 1

        /** Blend between nearest samples and the centroid.  */ // Weighted towards the nearest training photos. The centroid of a few varied
        // photos scores low against any single query, so leaning on it pushed correct
        // matches under the threshold.
        /**
         * 1.0 means the centroid is ignored when scoring. The centroid of photos
         * spanning several years is a blur that resembles nobody, so mixing it in
         * dragged correct matches down. The centroid is still written to the model
         * file and used as a fallback when a person has no samples loaded.
         */
        private const val SAMPLE_WEIGHT = 1.0f

        private val NO_SCORE = -2f
        private const val VERSION = 1

        /**
         * Bumped whenever the way an embedding is produced changes: the face crop, the
         * alignment, the illumination normalisation, the model itself.
         *
         * Embeddings from an older pipeline cannot match anything from a newer one, at
         * any threshold. Without this stamp that failure is silent and looks exactly
         * like a broken camera or a bad threshold.
         *
         * 1  original square crop, no illumination normalisation
         * 2  illumination normalisation, eye alignment, multi scale variants
         */
        private const val PIPELINE_VERSION = 2

        private fun vectorLine(index: Int, v: FloatArray, count: Int): String {
            val sb = StringBuilder(v.size * 12)
            sb.append(index)
            if (count >= 0) {
                sb.append(' ').append(count)
            }
            for (value in v) {
                sb.append(' ').append(value)
            }
            return sb.toString()
        }

        private fun meanUnit(list: MutableList<FloatArray>?): FloatArray? {
            if (list == null || list.isEmpty()) {
                return null
            }
            val mean = FloatArray(FaceNet.EMBEDDING_SIZE)
            for (v in list) {
                for (i in mean.indices) {
                    mean[i] += v[i]
                }
            }
            var sum = 0.0
            for (value in mean) {
                sum += (value * value).toDouble()
            }
            val norm = sqrt(sum).toFloat()
            if (norm < 1e-10f) {
                return null
            }
            for (i in mean.indices) {
                mean[i] /= norm
            }
            return mean
        }

        /** Both vectors are unit length, so this is the cosine similarity.  */
        private fun dot(a: FloatArray, b: FloatArray): Float {
            var sum = 0f
            for (i in a.indices) {
                sum += a[i] * b[i]
            }
            return sum
        }
    }
}