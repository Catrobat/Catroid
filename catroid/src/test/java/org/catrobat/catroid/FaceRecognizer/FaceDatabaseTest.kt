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
import java.io.FileWriter
import java.io.IOException
import java.io.Writer
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FaceDatabaseTest {

    private lateinit var database: FaceDatabase

    // The thresholds are process-wide statics. Remember them so this class does
    // not leak its values into whichever test runs next in the same JVM.
    private var savedMinSimilarity = 0f
    private var savedMinMargin = 0f
    private val savedOpenForWriting: (File) -> Writer = FileUtils.openForWriting

    @Before
    fun setUp() {
        savedMinSimilarity = FaceDatabase.minSimilarity
        savedMinMargin = FaceDatabase.minMargin

        val context = ApplicationProvider.getApplicationContext<Context>()
        FileUtils.init(context)
        FileUtils.deleteAll()
        FaceDatabase.minSimilarity = 0.53f
        FaceDatabase.minMargin = 0.04f
        database = FaceDatabase().apply { load() }
    }

    @After
    fun tearDown() {
        FileUtils.openForWriting = savedOpenForWriting
        FileUtils.deleteAll()
        FaceDatabase.minSimilarity = savedMinSimilarity
        FaceDatabase.minMargin = savedMinMargin
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
        val match = reloaded.match(unitVector(2))
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

        assertNull(database.match(unitVector(0)))
        assertTrue(database.getNames().isEmpty())
    }

    @Test
    fun exactEmbeddingMatchesItsOwner() {
        database.addPerson("Person A")
        database.addPerson("Person B")
        database.addEmbeddings(0, listOf(unitVector(0)))
        database.addEmbeddings(1, listOf(unitVector(1)))

        val match = database.match(unitVector(0))

        assertNotNull(match)
        assertEquals("Person A", match?.name)
        assertEquals(1f, match?.similarity ?: 0f, 0.0001f)
    }

    @Test
    fun ambiguousFaceIsRejectedByMargin() {
        val same = unitVector(0)
        database.addPerson("Person A")
        database.addPerson("Person B")
        database.addEmbeddings(0, listOf(same))
        database.addEmbeddings(1, listOf(same.copyOf()))

        assertNull(database.match(same))
    }

    @Test
    fun wrongSizedAndNullEmbeddingsAreRejectedSafely() {
        database.addPerson("Person A")
        database.addEmbeddings(0, listOf(unitVector(0)))

        assertNull(database.match(null))
        assertNull(database.match(FloatArray(8)))
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
        assertEquals("Bob", database.match(unitVector(1))?.name)
        FileUtils.openForWriting = savedOpenForWriting
        val restarted = FaceDatabase().apply { load() }
        assertEquals(listOf("Alice", "Bob"), restarted.getNames())
        assertEquals("Alice", restarted.match(unitVector(0))?.name)
        assertEquals("Bob", restarted.match(unitVector(1))?.name)
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
        assertNull(reloaded.match(unitVector(0)))
        assertNull(reloaded.match(FloatArray(FACENET_SIZE)))
        assertNull(reloaded.decide(reloaded.scoreAll(unitVector(0))))
    }

    @Test
    fun theFaceNetThresholdsAreNotTakenOver() {
        writeFaceNetFiles("Person A")
        FaceDatabase.minSimilarity = 0.33f
        FaceDatabase.minMargin = 0.02f

        FaceDatabase().load()

        // The old header says 0.60 / 0.05, on the FaceNet scale.
        assertEquals(0.33f, FaceDatabase.minSimilarity, 0.0001f)
        assertEquals(0.02f, FaceDatabase.minMargin, 0.0001f)
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
        assertEquals("Person A", restarted.match(unitVector(0))?.name)

        restarted.addEmbeddings(1, listOf(unitVector(1)))
        assertTrue(restarted.save())

        val retrained = FaceDatabase().apply { load() }
        assertFalse(retrained.hasStaleEmbeddings())
        assertEquals(1, retrained.getEmbeddingCount(1))
        assertEquals("Person B", retrained.match(unitVector(1))?.name)
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
    }
}
