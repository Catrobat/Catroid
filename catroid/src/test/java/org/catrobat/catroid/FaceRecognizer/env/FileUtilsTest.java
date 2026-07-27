package org.catrobat.catroid.FaceRecognizer.env;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Storage layer tests.
 *
 * The original bug was three different ways of resolving the same folder, so
 * training wrote to one place and detection read another. These pin the folder
 * down and check the write is atomic.
 */
@RunWith(RobolectricTestRunner.class)
public class FileUtilsTest {

	private Context context;

	@Before
	public void setUp() {
		context = ApplicationProvider.getApplicationContext();
		FileUtils.init(context);
		FileUtils.deleteAll();
	}

	@Test
	public void initCreatesTheFolder() {
		assertTrue(FileUtils.isReady());
		File folder = FileUtils.file(FileUtils.LABEL_FILE).getParentFile();
		assertTrue(folder.isDirectory());
	}

	@Test
	public void everyFileLivesInTheSameFolder() {
		File label = FileUtils.file(FileUtils.LABEL_FILE).getParentFile();
		File data = FileUtils.file(FileUtils.DATA_FILE).getParentFile();
		File model = FileUtils.file(FileUtils.MODEL_FILE).getParentFile();

		assertEquals(label.getAbsolutePath(), data.getAbsolutePath());
		assertEquals(label.getAbsolutePath(), model.getAbsolutePath());
	}

	@Test
	public void theFolderIsInsideTheAppPrivateFilesDir() {
		File expected = new File(context.getFilesDir(), "facerecog");
		File actual = FileUtils.file(FileUtils.LABEL_FILE).getParentFile();
		// Exact comparison, not startsWith. A temp dir reached through a symlink
		// would pass startsWith on one machine and fail on another.
		assertEquals(expected.getAbsolutePath(), actual.getAbsolutePath());
	}

	@Test
	public void initFollowsTheContextWhenTheFilesDirChanges() {
		String first = FileUtils.getAbsolutePath(FileUtils.DATA_FILE);
		FileUtils.init(context);
		String second = FileUtils.getAbsolutePath(FileUtils.DATA_FILE);
		assertEquals(first, second);
		assertTrue(second.contains("facerecog"));
	}

	@Test
	public void initIsIdempotent() {
		String first = FileUtils.getAbsolutePath(FileUtils.DATA_FILE);
		FileUtils.init(context);
		FileUtils.init(context);
		assertEquals(first, FileUtils.getAbsolutePath(FileUtils.DATA_FILE));
	}

	@Test
	public void writeThenReadReturnsTheSameLines() {
		List<String> lines = Arrays.asList("salah", "karim", "rahim");
		assertTrue(FileUtils.writeLines(FileUtils.LABEL_FILE, lines));
		assertEquals(lines, FileUtils.readLines(FileUtils.LABEL_FILE));
	}

	@Test
	public void writeReplacesRatherThanAppends() {
		FileUtils.writeLines(FileUtils.LABEL_FILE, Arrays.asList("a", "b", "c"));
		FileUtils.writeLines(FileUtils.LABEL_FILE, Collections.singletonList("z"));
		assertEquals(Collections.singletonList("z"),
				FileUtils.readLines(FileUtils.LABEL_FILE));
	}

	@Test
	public void writingAnEmptyListEmptiesTheFile() {
		FileUtils.writeLines(FileUtils.LABEL_FILE, Arrays.asList("a", "b"));
		FileUtils.writeLines(FileUtils.LABEL_FILE, Collections.<String>emptyList());
		assertTrue(FileUtils.readLines(FileUtils.LABEL_FILE).isEmpty());
	}

	@Test
	public void noTempFileIsLeftBehind() {
		FileUtils.writeLines(FileUtils.DATA_FILE, Arrays.asList("1", "2"));
		File temp = FileUtils.file(FileUtils.DATA_FILE + ".tmp");
		assertFalse("temp file must be renamed, not left on disk", temp.exists());
	}

	@Test
	public void readingAMissingFileGivesAnEmptyListNotACrash() {
		assertTrue(FileUtils.readLines("does_not_exist").isEmpty());
	}

	@Test
	public void blankLinesAreIgnoredOnRead() {
		FileUtils.writeLines(FileUtils.LABEL_FILE, Arrays.asList("a", "", "   ", "b"));
		assertEquals(Arrays.asList("a", "b"), FileUtils.readLines(FileUtils.LABEL_FILE));
	}

	@Test
	public void fileExistsIsFalseForAnEmptyFile() {
		FileUtils.writeLines(FileUtils.DATA_FILE, Collections.<String>emptyList());
		assertFalse(FileUtils.fileExists(context, FileUtils.DATA_FILE));
	}

	@Test
	public void fileExistsIsTrueOnceThereIsContent() {
		FileUtils.writeLines(FileUtils.DATA_FILE, Collections.singletonList("x"));
		assertTrue(FileUtils.fileExists(context, FileUtils.DATA_FILE));
	}

	@Test
	public void deleteAllClearsTheFolder() {
		FileUtils.writeLines(FileUtils.LABEL_FILE, Collections.singletonList("a"));
		FileUtils.writeLines(FileUtils.DATA_FILE, Collections.singletonList("b"));
		FileUtils.deleteAll();

		assertTrue(FileUtils.readLines(FileUtils.LABEL_FILE).isEmpty());
		assertTrue(FileUtils.readLines(FileUtils.DATA_FILE).isEmpty());
	}

	@Test
	public void namesWithSpacesSurviveTheRoundTrip() {
		List<String> lines = Arrays.asList("Kazi Jahid", "Abu Bakar Siddique");
		FileUtils.writeLines(FileUtils.LABEL_FILE, lines);
		assertEquals(lines, FileUtils.readLines(FileUtils.LABEL_FILE));
	}
}