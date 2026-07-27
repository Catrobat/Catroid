package org.catrobat.catroid.FaceRecognizer;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.catrobat.catroid.FaceRecognizer.env.FileUtils;
import org.catrobat.catroid.FaceRecognizer.ml.FaceNet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Threading and scale.
 *
 * In the app the database is written from a background training thread and read
 * from the camera thread, while the UI asks for names on the main thread. These
 * check that nothing tears and that a realistic classroom stays fast.
 */
@RunWith(RobolectricTestRunner.class)
public class FaceDatabaseStressTest {

	private static final int DIM = FaceNet.EMBEDDING_SIZE;

	private FaceDatabase database;

	@Before
	public void setUp() {
		Context context = ApplicationProvider.getApplicationContext();
		FileUtils.init(context);
		FileUtils.deleteAll();
		FaceDatabase.minSimilarity = 0.50f;
		FaceDatabase.minMargin = 0.05f;
		database = new FaceDatabase();
		database.load();
	}

	// ---------------- Scale ----------------

	@Test
	public void aClassroomSizedDatabaseStaysCorrect() {
		int people = 30;
		int photosEach = 6;

		for (int p = 0; p < people; p++) {
			int index = database.addPerson("child" + p);
			List<float[]> photos = new ArrayList<>();
			for (int i = 0; i < photosEach; i++) {
				photos.add(near(p, 0.97f - i * 0.01f));
			}
			database.addEmbeddings(index, photos);
		}
		assertTrue(database.save());

		FaceDatabase reloaded = new FaceDatabase();
		reloaded.load();

		assertEquals(people, reloaded.getPersonCount());
		for (int p = 0; p < people; p++) {
			assertEquals(photosEach, reloaded.getEmbeddingCount(p));
		}

		// Every child must still recognise as themselves.
		for (int p = 0; p < people; p++) {
			FaceDatabase.Match match = reloaded.match(axis(p));
			assertNotNull("child" + p + " was not matched", match);
			assertEquals("child" + p, match.name);
		}
	}

	@Test
	public void matchingStaysFastWithManyEmbeddings() {
		for (int p = 0; p < 30; p++) {
			int index = database.addPerson("child" + p);
			List<float[]> photos = new ArrayList<>();
			for (int i = 0; i < 6; i++) {
				photos.add(near(p, 0.95f));
			}
			database.addEmbeddings(index, photos);
		}

		float[] query = axis(7);
		long start = System.nanoTime();
		for (int i = 0; i < 100; i++) {
			database.match(query);
		}
		long perMatchMicros = (System.nanoTime() - start) / 100 / 1000;

		// 180 embeddings of 512 floats. Well under a millisecond on any device.
		assertTrue("match took " + perMatchMicros + " microseconds", perMatchMicros < 5000);
	}

	@Test
	public void deletingHalfAClassLeavesTheOtherHalfIntact() {
		for (int p = 0; p < 10; p++) {
			database.addEmbeddings(database.addPerson("child" + p),
					Collections.singletonList(axis(p)));
		}
		// Delete from the end so the indexes stay predictable while looping.
		for (int p = 9; p >= 0; p -= 2) {
			database.deletePerson(p);
		}
		database.save();

		FaceDatabase reloaded = new FaceDatabase();
		reloaded.load();
		assertEquals(5, reloaded.getPersonCount());

		for (int p = 0; p < 10; p += 2) {
			FaceDatabase.Match match = reloaded.match(axis(p));
			assertNotNull("child" + p + " should have survived", match);
			assertEquals("child" + p, match.name);
		}
		for (int p = 1; p < 10; p += 2) {
			FaceDatabase.Match match = reloaded.match(axis(p));
			if (match != null) {
				assertTrue("deleted child" + p + " came back as " + match.name,
						!match.name.equals("child" + p));
			}
		}
	}

	// ---------------- Threads ----------------

	@Test
	public void readingWhileWritingDoesNotTear() throws Exception {
		database.addEmbeddings(database.addPerson("salah"), Collections.singletonList(axis(0)));

		final AtomicReference<Throwable> failure = new AtomicReference<>();
		final CountDownLatch done = new CountDownLatch(2);

		Thread writer = new Thread(() -> {
			try {
				for (int i = 1; i <= 40; i++) {
					database.addEmbeddings(0, Collections.singletonList(near(0, 0.95f)));
					if (i % 10 == 0) {
						database.save();
					}
				}
			} catch (Throwable t) {
				failure.compareAndSet(null, t);
			} finally {
				done.countDown();
			}
		});

		Thread reader = new Thread(() -> {
			try {
				for (int i = 0; i < 200; i++) {
					database.getNames();
					database.getEmbeddingCount(0);
					database.match(axis(0));
				}
			} catch (Throwable t) {
				failure.compareAndSet(null, t);
			} finally {
				done.countDown();
			}
		});

		writer.start();
		reader.start();
		assertTrue("threads did not finish", done.await(30, TimeUnit.SECONDS));

		if (failure.get() != null) {
			throw new AssertionError("concurrent access failed", failure.get());
		}
		assertEquals(41, database.getEmbeddingCount(0));
	}

	@Test
	public void deletingWhileMatchingNeverReturnsAGhost() throws Exception {
		for (int p = 0; p < 6; p++) {
			database.addEmbeddings(database.addPerson("child" + p),
					Collections.singletonList(axis(p)));
		}

		final AtomicReference<Throwable> failure = new AtomicReference<>();
		final CountDownLatch done = new CountDownLatch(2);

		Thread deleter = new Thread(() -> {
			try {
				for (int p = 5; p >= 3; p--) {
					Thread.sleep(5);
					database.deletePerson(p);
				}
			} catch (Throwable t) {
				failure.compareAndSet(null, t);
			} finally {
				done.countDown();
			}
		});

		Thread matcher = new Thread(() -> {
			try {
				for (int i = 0; i < 300; i++) {
					FaceDatabase.Match match = database.match(axis(i % 6));
					if (match != null) {
						// Whatever comes back must be a name that still exists.
						assertTrue("matched a deleted person: " + match.name,
								database.getNames().contains(match.name));
					}
				}
			} catch (Throwable t) {
				failure.compareAndSet(null, t);
			} finally {
				done.countDown();
			}
		});

		deleter.start();
		matcher.start();
		assertTrue(done.await(30, TimeUnit.SECONDS));

		if (failure.get() != null) {
			throw new AssertionError("concurrent delete failed", failure.get());
		}
		assertEquals(3, database.getPersonCount());
	}

	// ---------------- Robustness ----------------

	@Test
	public void aTruncatedDataFileDoesNotBreakLoading() {
		database.addEmbeddings(database.addPerson("salah"),
				java.util.Arrays.asList(axis(0), axis(1)));
		database.save();

		List<String> lines = FileUtils.readLines(FileUtils.DATA_FILE);
		String half = lines.get(0).substring(0, lines.get(0).length() / 2);
		FileUtils.writeLines(FileUtils.DATA_FILE, java.util.Arrays.asList(half, lines.get(1)));

		FaceDatabase reloaded = new FaceDatabase();
		reloaded.load();
		assertEquals(1, reloaded.getPersonCount());
		assertEquals(1, reloaded.getEmbeddingCount(0));
	}

	@Test
	public void aMissingModelFileIsRebuiltFromData() {
		database.addEmbeddings(database.addPerson("salah"), Collections.singletonList(axis(0)));
		database.save();

		FileUtils.writeLines(FileUtils.MODEL_FILE, Collections.<String>emptyList());

		FaceDatabase reloaded = new FaceDatabase();
		reloaded.load();
		assertNotNull("matching must still work without a model file",
				reloaded.match(axis(0)));
	}

	@Test
	public void aMissingDataFileLeavesNamesButNoMatches() {
		database.addEmbeddings(database.addPerson("salah"), Collections.singletonList(axis(0)));
		database.save();

		FileUtils.writeLines(FileUtils.DATA_FILE, Collections.<String>emptyList());

		FaceDatabase reloaded = new FaceDatabase();
		reloaded.load();
		assertEquals(1, reloaded.getPersonCount());
		assertEquals(0, reloaded.getEmbeddingCount(0));
		assertNull(reloaded.match(axis(0)));
	}

	// ---------------- Helpers ----------------

	private static float[] axis(int index) {
		float[] v = new float[DIM];
		v[index % DIM] = 1f;
		return v;
	}

	/** Unit vector with exactly the given similarity to axis(index). */
	private static float[] near(int index, float similarity) {
		float[] v = new float[DIM];
		v[index % DIM] = similarity;
		v[(index + 250) % DIM] = (float) Math.sqrt(1f - similarity * similarity);
		return v;
	}
}
