package org.catrobat.catroid.FaceRecognizer

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.catrobat.catroid.FaceRecognizer.env.FileUtils
import org.catrobat.catroid.FaceRecognizer.ml.MobileFaceNet
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.Reader
import java.io.Writer
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FaceDatabaseTest {

    private lateinit var database: FaceDatabase

    private val savedOpenForWriting: (File) -> Writer = FileUtils.openForWriting
    private val savedOpenForReading: (File) -> Reader = FileUtils.openForReading

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        FileUtils.init(context)
        FileUtils.deleteAll()
        database = FaceDatabase().apply { load() }
    }

    @After
    fun tearDown() {
        FileUtils.openForWriting = savedOpenForWriting
        FileUtils.openForReading = savedOpenForReading
        FileUtils.deleteAll()
    }

    @Test
    fun peopleReceiveStableSequentialIndexes() {
        assertEquals(0, database.addPerson("Person A"))
        assertEquals(1, database.addPerson("Person B"))
        assertEquals(2, database.addPerson("Person C"))
        assertEquals(listOf("Person A", "Person B", "Person C"), database.getNames())
    }

    @Test
    fun saveAndReloadPreservesLabelsAndEmbeddings() {
        database.addPerson("Person A")
        database.addPerson("Person B")
        database.addEmbeddings(0, listOf(unitVector(0), unitVector(1)))
        database.addEmbeddings(1, listOf(unitVector(2)))
        assertTrue(database.save())

        val reloaded = FaceDatabase().apply { load() }

        assertEquals(listOf("Person A", "Person B"), reloaded.getNames())
        assertEquals(2, reloaded.getEmbeddingCount(0))
        assertEquals(1, reloaded.getEmbeddingCount(1))
    }

    @Test
    fun deletingMiddlePersonRemapsFollowingEmbeddings() {
        database.addPerson("Person A")
        database.addPerson("Person B")
        database.addPerson("Person C")
        database.addEmbeddings(0, listOf(unitVector(0)))
        database.addEmbeddings(1, listOf(unitVector(1)))
        database.addEmbeddings(2, listOf(unitVector(2)))

        database.deletePerson(1)
        database.save()

        val reloaded = FaceDatabase().apply { load() }
        val match = reloaded.matchOf(unitVector(2))
        assertNotNull(match)
        assertEquals(listOf("Person A", "Person C"), reloaded.getNames())
        assertEquals(1, match?.index)
        assertEquals("Person C", match?.name)
    }

    @Test
    fun deletedPersonCanNoLongerMatch() {
        database.addPerson("Person A")
        database.addEmbeddings(0, listOf(unitVector(0)))
        database.deletePerson(0)

        assertNull(database.matchOf(unitVector(0)))
        assertTrue(database.getNames().isEmpty())
    }

    @Test
    fun exactEmbeddingMatchesItsOwner() {
        database.addPerson("Person A")
        database.addPerson("Person B")
        database.addEmbeddings(0, listOf(unitVector(0)))
        database.addEmbeddings(1, listOf(unitVector(1)))

        val match = database.matchOf(unitVector(0))

        assertNotNull(match)
        assertEquals("Person A", match?.name)
        assertEquals(1f, match?.similarity ?: 0f, 0.0001f)
    }

    @Test
    fun wrongSizedAndNullEmbeddingsAreRejectedSafely() {
        database.addPerson("Person A")
        database.addEmbeddings(0, listOf(unitVector(0)))

        assertNull(database.scoreAll(null))
        assertNull(database.scoreAll(FloatArray(8)))
        assertNull(database.scoreAllVariants(emptyList()))
    }

    @Test
    fun invalidPersonIndexDoesNotStoreEmbedding() {
        database.addPerson("Person A")

        database.addEmbeddings(99, listOf(unitVector(0)))

        assertEquals(0, database.getEmbeddingCount(0))
    }

    /**
     * Found in review: deleting Alice from [Alice, Bob] could save the new names
     * and then fail on the photos, so after a restart Alice's photos were Bob's.
     * A failed save keeps the files as they were and puts the database back.
     */
    @Test
    fun aSaveThatFailsKeepsEveryoneWithTheirOwnPhotos() {
        database.addPerson("Alice")
        database.addPerson("Bob")
        database.addEmbeddings(0, listOf(unitVector(0)))
        database.addEmbeddings(1, listOf(unitVector(1)))
        assertTrue(database.save())
        FileUtils.openForWriting = { file ->
            if (file.name.startsWith(FileUtils.DATA_FILE)) throw IOException("No space left on device")
            FileWriter(file, false)
        }

        database.deletePerson(0)
        assertFalse("A failed save must say so", database.save())

        assertEquals(listOf("Alice", "Bob"), database.getNames())
        assertEquals("Bob", database.matchOf(unitVector(1))?.name)
        FileUtils.openForWriting = savedOpenForWriting
        val restarted = FaceDatabase().apply { load() }
        assertEquals(listOf("Alice", "Bob"), restarted.getNames())
        assertEquals("Alice", restarted.matchOf(unitVector(0))?.name)
        assertEquals("Bob", restarted.matchOf(unitVector(1))?.name)
    }

    /**
     * Found in a review of the load path: a file that could not be read loaded
     * as empty, and the next change saved that empty state over the real files.
     * A database that could not be read refuses to save until it can.
     */
    @Test
    fun filesThatCannotBeReadAreNeverOverwritten() {
        database.addPerson("Alice")
        database.addPerson("Bob")
        database.addEmbeddings(0, listOf(unitVector(0)))
        database.addEmbeddings(1, listOf(unitVector(1)))
        assertTrue(database.save())
        FileUtils.openForReading = { file ->
            if (file.name == FileUtils.DATA_FILE) throw IOException("I/O error")
            FileReader(file)
        }

        val unreadable = FaceDatabase().apply { load() }
        unreadable.addPerson("Carol")
        assertFalse("A database that could not be read must not save", unreadable.save())

        FileUtils.openForReading = savedOpenForReading
        val restarted = FaceDatabase().apply { load() }
        assertEquals(listOf("Alice", "Bob"), restarted.getNames())
        assertEquals("Alice", restarted.matchOf(unitVector(0))?.name)
        assertEquals("Bob", restarted.matchOf(unitVector(1))?.name)
    }

    /**
     * Only this build writes 128 values per photo, so photos without a model
     * file still come from it: they are not out of date, and still match.
     */
    @Test
    fun photosWithoutAModelFileAreNotOutOfDate() {
        database.addPerson("Alice")
        database.addEmbeddings(0, listOf(unitVector(0)))
        assertTrue(database.save())
        assertTrue(FileUtils.file(FileUtils.MODEL_FILE).delete())

        val reloaded = FaceDatabase().apply { load() }

        assertFalse(reloaded.hasStaleEmbeddings())
        assertEquals("Alice", reloaded.matchOf(unitVector(0))?.name)
    }

    // ---------------- Photos from the FaceNet build (512 values) ----------------

    @Test
    fun photosFromTheFaceNetBuildAreOutOfDateAndNeverCrash() {
        writeFaceNetFiles("Person A", "Person B")

        val reloaded = FaceDatabase().apply { load() }

        assertTrue(reloaded.hasStaleEmbeddings())
        assertEquals(listOf("Person A", "Person B"), reloaded.getNames())
        assertEquals(0, reloaded.getEmbeddingCount(0))
        assertEquals(0, reloaded.getEmbeddingCount(1))
        assertNull(reloaded.matchOf(unitVector(0)))
        assertNull(reloaded.scoreAll(FloatArray(FACENET_SIZE)))
    }

    @Test
    fun outdatedPhotosStayUntilThePersonIsTrainedAgain() {
        writeFaceNetFiles("Person A", "Person B")
        val reloaded = FaceDatabase().apply { load() }

        reloaded.addEmbeddings(0, listOf(unitVector(0)))
        assertTrue(reloaded.save())

        // Person B is still waiting for new photos, also after a restart.
        assertTrue(reloaded.hasStaleEmbeddings())
        val restarted = FaceDatabase().apply { load() }
        assertTrue(restarted.hasStaleEmbeddings())
        assertEquals(1, restarted.getEmbeddingCount(0))
        assertEquals(0, restarted.getEmbeddingCount(1))
        assertEquals("Person A", restarted.matchOf(unitVector(0))?.name)

        restarted.addEmbeddings(1, listOf(unitVector(1)))
        assertTrue(restarted.save())

        val retrained = FaceDatabase().apply { load() }
        assertFalse(retrained.hasStaleEmbeddings())
        assertEquals(1, retrained.getEmbeddingCount(1))
        assertEquals("Person B", retrained.matchOf(unitVector(1))?.name)
    }

    @Test
    fun deletingThePersonWithOutdatedPhotosEndsTheWarning() {
        writeFaceNetFiles("Person A")
        val reloaded = FaceDatabase().apply { load() }

        reloaded.deletePerson(0)
        assertTrue(reloaded.save())

        assertFalse(reloaded.hasStaleEmbeddings())
        assertFalse(FaceDatabase().apply { load() }.hasStaleEmbeddings())
    }

    @Test
    fun photosOfThisBuildAreNotOutOfDate() {
        database.addPerson("Person A")
        database.addEmbeddings(0, listOf(unitVector(0)))
        assertTrue(database.save())

        assertFalse(FaceDatabase().apply { load() }.hasStaleEmbeddings())
    }

    // ---------------- The model file header ----------------

    @Test
    fun theModelHeaderNamesThePipelineAndNoThresholds() {
        database.addPerson("Person A")
        database.addEmbeddings(0, listOf(unitVector(0)))
        assertTrue(database.save())

        assertEquals(
            "v1 ${MobileFaceNet.EMBEDDING_SIZE} 1 0 0 p3",
            FileUtils.readAllLines(FileUtils.MODEL_FILE).first()
        )
    }

    /**
     * Earlier builds of this pipeline wrote two thresholds into the model
     * header. Their files load as before: the thresholds are ignored, the
     * pipeline version is still read, and the centroids are taken from the
     * model file. Person B has no photo, only a centroid, so a match for
     * Person B shows that the model file was read rather than rebuilt.
     */
    @Test
    fun aModelHeaderWithThresholdsStillLoads() {
        writeModelWithThresholds(pipeline = 3)

        val reloaded = FaceDatabase().apply { load() }

        assertFalse(reloaded.hasStaleEmbeddings())
        assertEquals(listOf("Person A", "Person B"), reloaded.getNames())
        assertEquals("Person A", reloaded.matchOf(unitVector(0))?.name)
        assertEquals("Person B", reloaded.matchOf(unitVector(1))?.name)
    }

    @Test
    fun aModelHeaderWithThresholdsFromAnOlderPipelineIsOutOfDate() {
        writeModelWithThresholds(pipeline = 2)

        assertTrue(FaceDatabase().apply { load() }.hasStaleEmbeddings())
    }

    /** Person A with one photo, Person B with only a centroid, under a header with thresholds. */
    private fun writeModelWithThresholds(pipeline: Int) {
        val size = MobileFaceNet.EMBEDDING_SIZE
        assertTrue(FileUtils.writeLines(FileUtils.LABEL_FILE, listOf("Person A", "Person B")))
        assertTrue(FileUtils.writeLines(FileUtils.DATA_FILE, listOf("0 " + vectorText(0))))
        assertTrue(
            FileUtils.writeLines(
                FileUtils.MODEL_FILE,
                listOf("v1 $size 2 0.6100 0.0800 p$pipeline", "0 1 " + vectorText(0), "1 1 " + vectorText(1))
            )
        )
    }

    private fun vectorText(position: Int): String = unitVector(position).joinToString(" ")

    /** The person the sensor would name for [query] at its default threshold. */
    private fun FaceDatabase.matchOf(query: FloatArray): FaceDatabase.Match? =
        scoreAll(query)?.let { decideWithThreshold(it, THRESHOLD) }

    /** The three files as the FaceNet build (pipeline 2, 512 values) wrote them. */
    private fun writeFaceNetFiles(vararg names: String) {
        val vector = (0 until FACENET_SIZE).joinToString(" ") { if (it == 0) "1.0" else "0.0" }
        assertTrue(FileUtils.writeLines(FileUtils.LABEL_FILE, names.toList()))
        assertTrue(
            FileUtils.writeLines(
                FileUtils.DATA_FILE,
                names.indices.flatMap { listOf("$it $vector", "$it $vector") }
            )
        )
        assertTrue(
            FileUtils.writeLines(
                FileUtils.MODEL_FILE,
                listOf("v1 $FACENET_SIZE ${names.size} 0.6000 0.0500 p2") +
                    names.indices.map { "$it 2 $vector" }
            )
        )
    }

    private fun unitVector(position: Int): FloatArray =
        FloatArray(MobileFaceNet.EMBEDDING_SIZE).apply { this[position] = 1f }

    private companion object {
        const val FACENET_SIZE = 512

        /** The default parameter of "detected face name with threshold". */
        const val THRESHOLD = 0.53f
    }
}
