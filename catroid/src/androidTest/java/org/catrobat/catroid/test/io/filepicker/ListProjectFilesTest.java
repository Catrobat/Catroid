/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.test.io.filepicker;

import android.os.Build;

import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.ui.filepicker.ListProjectFilesTask;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME;
import static org.catrobat.catroid.common.Constants.TMP_DIR_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.EXTERNAL_STORAGE_ROOT_DIRECTORY;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

@RunWith(AndroidJUnit4.class)
public class ListProjectFilesTest {

	private File tmpFolder;

	@Before
	public void setUp() throws IOException {
		tmpFolder = new File(ApplicationProvider.getApplicationContext().getCacheDir(), "ListProjectFilesTestTmp");
		if (tmpFolder.isDirectory()) {
			StorageOperations.deleteDir(tmpFolder);
		}
		tmpFolder.mkdirs();

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
			if (EXTERNAL_STORAGE_ROOT_DIRECTORY.isDirectory()) {
				StorageOperations.deleteDir(EXTERNAL_STORAGE_ROOT_DIRECTORY);
			}
			EXTERNAL_STORAGE_ROOT_DIRECTORY.mkdirs();
		}
	}

	@After
	public void tearDown() throws IOException {
		if (tmpFolder.isDirectory()) {
			StorageOperations.deleteDir(tmpFolder);
		}

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
				&& EXTERNAL_STORAGE_ROOT_DIRECTORY.isDirectory()) {
			StorageOperations.deleteDir(EXTERNAL_STORAGE_ROOT_DIRECTORY);
		}
	}

	@Test
	public void testListCatrobatFiles() throws IOException {

		File file0 = new File(tmpFolder, "projectWithoutExtension");
		assertTrue(file0.createNewFile());
		File file1 = new File(tmpFolder, "projectA.catrobat");
		assertTrue(file1.createNewFile());
		File file2 = new File(tmpFolder, "project.catrobat.somethingelse");
		assertTrue(file2.createNewFile());

		File subFolder = new File(tmpFolder, "subfolder");
		assertTrue(subFolder.mkdirs());
		File file3 = new File(subFolder, "projectB.catrobat");
		assertTrue(file3.createNewFile());
		File file4 = new File(subFolder, ".projectB.catrobat.somethingelse");
		assertTrue(file4.createNewFile());

		List<File> projectFiles = ListProjectFilesTask.task(tmpFolder);

		assertThat(projectFiles, hasItem(file1));
		assertThat(projectFiles, hasItem(file3));

		assertThat(projectFiles, not(hasItem(file0)));
		assertThat(projectFiles, not(hasItem(file2)));
		assertThat(projectFiles, not(hasItem(file4)));
	}

	@Test
	public void testListProjectsOnExternalStorage() throws IOException {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
			File backpackFolder = new File(EXTERNAL_STORAGE_ROOT_DIRECTORY, "backpack");
			assertTrue(backpackFolder.mkdirs());

			File tempFolder = new File(EXTERNAL_STORAGE_ROOT_DIRECTORY, TMP_DIR_NAME);
			assertTrue(tempFolder.mkdirs());

			File projectFolder1 = new File(EXTERNAL_STORAGE_ROOT_DIRECTORY, "projectFolder1");
			assertTrue(projectFolder1.mkdirs());
			assertTrue(new File(projectFolder1, CODE_XML_FILE_NAME).createNewFile());

			File projectFolder2 = new File(EXTERNAL_STORAGE_ROOT_DIRECTORY, "projectFolder2");
			assertTrue(projectFolder2.mkdirs());
			assertTrue(new File(projectFolder2, CODE_XML_FILE_NAME).createNewFile());

			File someFolder = new File(EXTERNAL_STORAGE_ROOT_DIRECTORY, "someFolder");
			assertTrue(someFolder.mkdirs());

			List<File> projectFiles = ListProjectFilesTask
					.task(tmpFolder);

			assertThat(projectFiles, hasItem(projectFolder1));
			assertThat(projectFiles, hasItem(projectFolder2));

			assertThat(projectFiles, not(hasItem(backpackFolder)));
			assertThat(projectFiles, not(hasItem(tempFolder)));
			assertThat(projectFiles, not(hasItem(someFolder)));
		}
	}
}
