package org.catrobat.catroid.FaceRecognizer

import android.util.Log
import org.catrobat.catroid.FaceRecognizer.env.FileUtils
import org.catrobat.catroid.FaceRecognizer.env.FileUtils.file
import org.catrobat.catroid.FaceRecognizer.env.FileUtils.isReady
import org.catrobat.catroid.FaceRecognizer.env.FileUtils.readAllLines
import org.catrobat.catroid.FaceRecognizer.env.FileUtils.recoverInterruptedWrite
import org.catrobat.catroid.FaceRecognizer.env.FileUtils.writeFiles
import org.catrobat.catroid.FaceRecognizer.ml.MobileFaceNet
import java.io.IOException
import java.util.Arrays
import java.util.Locale
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Owns the three files and does the matching.
 *
 * label : one person name per line. The line number is the person index.
 * data  : one training embedding per line, "<index> v0 v1 ... v127".
 * model : compiled matcher, rebuilt from data on every change.
 * line 1  "v1 <embeddingSize> <personCount> <minSimilarity> <minMargin> p<pipeline>"
 * then    "<index> <sampleCount> c0 c1 ... c127"   centroid, unit length
 *
 * All three are written together from memory, so their indices can never drift apart.
 * Matching score for a person is a blend of the nearest training samples and the
 * centroid, both cosine similarities on unit length embeddings.
 */
class FaceDatabase {
    /** True when the stored embeddings were made by a different pipeline.  */
    private var staleEmbeddings = false

    /**
     * Data lines of another embedding size (512 values from the FaceNet build),
     * by person name, without the index. They cannot be matched, but are written
     * back unchanged until the person gets new photos or is deleted, so the
     * request to retrain survives a restart.
     */
    private val outdatedPhotos: MutableMap<String, MutableList<String>> = LinkedHashMap()

    @Synchronized
    fun hasStaleEmbeddings(): Boolean {
        return staleEmbeddings || outdatedPhotos.isNotEmpty()
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

    /**
     * True when a face file exists but could not be read. The database is then
     * empty in memory, and saving it would replace the real files with that:
     * [save] refuses until a load succeeds.
     */
    private var unreadable = false

    // ---------------- Load and save ----------------
    @Synchronized
    fun load() {
        names.clear()
        samples.clear()
        centroids.clear()
        outdatedPhotos.clear()
        staleEmbeddings = false

        if (!isReady) {
            // FileUtils.init has not run: there is nothing to read yet.
            unreadable = false
            loadedStamp = -1L
            return
        }
        // A save the app stopped in, or whose rollback failed, is finished first.
        if (!recoverInterruptedWrite()) {
            // Some files are new and some old; they must not be read together.
            Log.e(TAG, "The face files are mid-save; nothing is read or saved until they are restored")
            unreadable = true
            loadedStamp = -1L
            return
        }
        val labelLines: List<String>
        val dataLines: List<String>
        val modelLines: List<String>
        try {
            labelLines = readAllLines(FileUtils.LABEL_FILE)
            dataLines = readAllLines(FileUtils.DATA_FILE)
            modelLines = readAllLines(FileUtils.MODEL_FILE)
        } catch (e: IOException) {
            Log.e(TAG, "Could not read the face files; nothing is saved until they can be read", e)
            unreadable = true
            loadedStamp = -1L
            return
        }
        unreadable = false

        for (name in labelLines) {
            names.add(name)
            samples.add(ArrayList())
            centroids.add(null)
        }

        val lines = dataLines.map(::loadDataLine)

        loadModelAndCheckPipeline(modelLines)
        stamp()

        Log.i(
            TAG, ("Loaded " + names.size + " people, " + lines.count { it == DataLine.LOADED }
                + " embeddings, " + lines.count { it == DataLine.SKIPPED } + " bad lines skipped")
        )
    }

    private enum class DataLine { LOADED, OUTDATED, SKIPPED }

    /** Stores one "data" line as a photo of this model, or keeps it as an outdated photo. */
    private fun loadDataLine(line: String): DataLine {
        val parts = line.split(" ").filter { it.isNotEmpty() }
        if (parts.size != EMBEDDING_SIZE + 1) {
            return if (keepOutdatedPhoto(parts)) DataLine.OUTDATED else DataLine.SKIPPED
        }
        val index = parts[0].toIntOrNull()
        if (index == null || index !in names.indices) {
            return DataLine.SKIPPED
        }
        return try {
            samples[index].add(FloatArray(EMBEDDING_SIZE) { parts[it + 1].toFloat() })
            DataLine.LOADED
        } catch (e: NumberFormatException) {
            DataLine.SKIPPED
        }
    }

    /** Reads the model file, or rebuilds the centroids, and marks photos from another pipeline. */
    private fun loadModelAndCheckPipeline(modelLines: List<String>) {
        if (outdatedPhotos.isNotEmpty()) {
            Log.e(
                TAG, "STORED PHOTOS ARE OUT OF DATE. " + outdatedPhotos.keys +
                    " have photos from another face model, this build uses " +
                    EMBEDDING_SIZE + " values. Add their photos again."
            )
        }
        if (loadModel(modelLines)) {
            return
        }
        // No usable model file. Photos of this embedding size can only come
        // from this pipeline (older ones are outdated photos above), so only
        // the centroids need rebuilding.
        rebuildCentroids()
    }

    /** Keeps a data line of another embedding size for [outdatedPhotos]. False if it is no such line. */
    private fun keepOutdatedPhoto(parts: List<String>): Boolean {
        if (parts.size < 2) {
            return false
        }
        val index = parts[0].toIntOrNull() ?: return false
        if (index !in names.indices) {
            return false
        }
        outdatedPhotos.getOrPut(names[index]) { ArrayList() }
            .add(parts.subList(1, parts.size).joinToString(" "))
        return true
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
        if (unreadable) {
            // Saving would write the empty database over files that are still there.
            Log.e(TAG, "Not saving: the face files could not be read; the change is undone")
            load()
            return false
        }
        rebuildCentroids()

        val dataLines: MutableList<String> = ArrayList<String>()
        for (index in samples.indices) {
            for (v in samples[index]) {
                dataLines.add(vectorLine(index, v, -1))
            }
            outdatedPhotos[names[index]]?.forEach { dataLines.add("$index $it") }
        }

        val modelLines: MutableList<String> = ArrayList<String>()
        modelLines.add(
            String.format(
                Locale.US, "v%d %d %d %.4f %.4f p%d",
                VERSION, EMBEDDING_SIZE, names.size, minSimilarity, minMargin,
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

        // The files refer to each other by person index: all or none are replaced.
        val ok = writeFiles(
            mapOf(
                FileUtils.LABEL_FILE to ArrayList<String>(names),
                FileUtils.DATA_FILE to dataLines,
                FileUtils.MODEL_FILE to modelLines
            )
        )
        Log.i(
            TAG, ("Saved label(" + names.size + ") data(" + dataLines.size
                + ") model(" + (modelLines.size - 1) + ") ok=" + ok)
        )
        if (!ok) {
            // The files are as before; so is the database, or it would not match them.
            Log.e(TAG, "Could not save the face files; the change is undone")
            load()
            return false
        }
        stamp()
        return true
    }

    private fun loadModel(lines: List<String>): Boolean {
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
            if (header[1].toInt() != EMBEDDING_SIZE) {
                // Another face model; its thresholds are on another scale too.
                Log.w(TAG, "Model file is for " + header[1] + " values, this build uses " + EMBEDDING_SIZE)
                staleEmbeddings = true
                false
            } else if (!modelMatchesLabels(header)) {
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

        if (parts.size != EMBEDDING_SIZE + 2) {
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

        val centroid = FloatArray(EMBEDDING_SIZE) { position ->
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
    /** Changes the thresholds and writes them into the model file header. False if that failed. */
    @Synchronized
    fun setThresholds(similarity: Float, margin: Float): Boolean {
        minSimilarity = max(0.05f, min(0.95f, similarity))
        minMargin = max(0f, min(0.5f, margin))
        Log.i(TAG, "Thresholds set to " + minSimilarity + " / " + minMargin)
        return save()
    }

    @Synchronized
    fun getNames(): List<String> {
        return names.toList()
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
        var added = 0
        for (v in list) {
            if (v.size == EMBEDDING_SIZE) {
                samples[index].add(v)
                added++
            }
        }
        if (added > 0) {
            // New photos replace the ones the old face model made.
            outdatedPhotos.remove(names[index])
        }
    }

    @Synchronized
    fun deletePerson(index: Int) {
        if (index < 0 || index >= names.size) {
            return
        }
        outdatedPhotos.remove(names[index])
        names.removeAt(index)
        samples.removeAt(index)
        centroids.removeAt(index)
    }

    // ---------------- Matching ----------------
    /** Returns the matched person, or null when nothing is close enough.  */
    /** Score for every enrolled person, in index order. Used for multi frame voting.  */
    @Synchronized
    fun scoreAll(query: FloatArray?): FloatArray? {
        if (query == null || query.size != EMBEDDING_SIZE || names.isEmpty()) {
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

    /**
     * The similarity a match needs. With a second enrolled person to compare
     * against, the margin rule also has to pass, so the normal threshold is
     * enough. With nobody to compare against (one enrolled person, or everyone
     * else without photos) the margin says nothing, so a stranger who only
     * resembles that person must not get their name: the bar is higher.
     */
    private fun similarityNeeded(hasRival: Boolean): Float {
        val floor = max(minSimilarity, ABSOLUTE_SAFETY_FLOOR)
        return if (hasRival) floor else max(floor, SINGLE_PERSON_MIN_SIMILARITY)
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
        val requiredSimilarity: Float = similarityNeeded(hasRival = second != NO_SCORE)
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
        if (query == null || query.size != EMBEDDING_SIZE || names.isEmpty()) {
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
        val requiredSimilarity: Float = similarityNeeded(hasRival = second != NO_SCORE)
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

    private fun personScore(index: Int, query: FloatArray): Float {
        val personSamples: List<FloatArray> = samples[index].toList()
        val centroid = centroids[index]

        if (personSamples.isEmpty()) {
            return centroid?.let { dot(it, query) } ?: NO_SCORE
        }

        val sampleScore = calculateTopKAverage(personSamples, query)

        return combineScores(sampleScore, centroid, query)
    }

    private fun calculateTopKAverage(
        personSamples: List<FloatArray>,
        query: FloatArray
    ): Float {
        val topScores = FloatArray(TOP_K) { NO_SCORE }

        for (sample in personSamples) {
            insertIntoTopScores(
                topScores,
                dot(sample, query)
            )
        }

        return averageValidScores(topScores)
    }

    private fun insertIntoTopScores(
        topScores: FloatArray,
        score: Float
    ) {
        for (index in topScores.indices) {
            if (score > topScores[index]) {
                shiftScoresRight(topScores, index)
                topScores[index] = score
                return
            }
        }
    }

    private fun shiftScoresRight(
        topScores: FloatArray,
        insertionIndex: Int
    ) {
        for (index in topScores.lastIndex downTo insertionIndex + 1) {
            topScores[index] = topScores[index - 1]
        }
    }

    private fun averageValidScores(topScores: FloatArray): Float {
        var sum = 0f
        var count = 0

        for (score in topScores) {
            if (score != NO_SCORE) {
                sum += score
                count++
            }
        }

        return if (count > 0) {
            sum / count
        } else {
            NO_SCORE
        }
    }

    private fun combineScores(
        sampleScore: Float,
        centroid: FloatArray?,
        query: FloatArray
    ): Float {
        if (centroid == null) {
            return sampleScore
        }

        val centroidScore = dot(centroid, query)

        return SAMPLE_WEIGHT * sampleScore +
            (1f - SAMPLE_WEIGHT) * centroidScore
    }

    companion object {
        const val TAG: String = "FaceDatabase"

        /**
         * Accept a match only above this cosine similarity.
         * Raise it if strangers get a name. Lower it if known people come back Unknown.
         * Useful range 0.40 to 0.65 (MobileFaceNet scores lower than FaceNet did).
         */
        @JvmField
        var minSimilarity: Float = 0.45f

        /**
         * The winner must beat the runner up by this much. This is what stops the app
         * putting the wrong person's name on a face.
         */
        @JvmField
        var minMargin: Float = 0.04f

        /*
         * The thresholds below were tuned for FaceNet and carried over to the
         * MobileFaceNet scale on the fixture photos (faces.zip, every photo
         * against the others, all views, as scoreFrame scores them):
         *   FaceNet       same person 0.689-0.909 (mean 0.814), others up to 0.325 (mean 0.149)
         *   MobileFaceNet same person 0.575-0.855 (mean 0.704), others up to 0.435 (mean 0.157)
         * Each old threshold t keeps its place between the two means:
         *   t' = 0.157 + (t - 0.149) * 0.823
         * 0.60 -> 0.53, 0.70 -> 0.61, 0.75 -> 0.65, margin 0.05 -> 0.04.
         */

        /** Never accept collapsed/no-detail embeddings even if an old model saved a lower value.  */
        private const val ABSOLUTE_SAFETY_FLOOR = 0.53f

        /**
         * Needed when there is no second person to compare against. With FaceNet,
         * measured on a Galaxy M53, a stranger against the only enrolled person
         * scored up to 0.61 (0.54 on this scale) and genuine matches 0.79-0.93;
         * the bar was 0.70.
         */
        internal const val SINGLE_PERSON_MIN_SIMILARITY = 0.61f

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

        /** The score of a person who has no photo to compare with. */
        internal const val NO_SCORE = -2f
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
         * 3  MobileFaceNet (112x112, 128 values) instead of FaceNet (160x160, 512)
         */
        private const val PIPELINE_VERSION = 3

        private const val EMBEDDING_SIZE = MobileFaceNet.EMBEDDING_SIZE

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

        private fun meanUnit(list: List<FloatArray>?): FloatArray? {
            if (list.isNullOrEmpty()) {
                return null
            }
            val mean = FloatArray(EMBEDDING_SIZE)
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