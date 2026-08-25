package org.catrobat.catroid.FaceRecognizer

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.catrobat.catroid.FaceRecognizer.env.FileUtils
import org.catrobat.catroid.FaceRecognizer.ml.FaceNet
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FaceDatabaseTest {

    private lateinit var database: FaceDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        FileUtils.init(context)
        FileUtils.deleteAll()
        FaceDatabase.minSimilarity = 0.60f
        FaceDatabase.minMargin = 0.05f
        database = FaceDatabase().apply { load() }
    }

    @After
    fun tearDown() {
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

    private fun unitVector(position: Int): FloatArray =
        FloatArray(FaceNet.EMBEDDING_SIZE).apply { this[position] = 1f }
}
