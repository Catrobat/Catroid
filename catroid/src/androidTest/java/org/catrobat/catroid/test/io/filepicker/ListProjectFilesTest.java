/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ui.filepicker.ListProjectFilesTask;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertTrue;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

@RunWith(AndroidJUnit4.class)
public class ListProjectFilesTest {

	private File tmpFolder;

	@Before
	public void setUp() {
		tmpFolder = new File(InstrumentationRegistry.getTargetContext().getCacheDir(), "ListProjectFilesTestTmp");
		if (tmpFolder.exists()) {
			tmpFolder.delete();
		}
		tmpFolder.mkdirs();
	}

	@After
	public void tearDown() {
		if (tmpFolder.exists()) {
			tmpFolder.delete();
		}
	}

	@Test
	public void testListFiles() throws IOException {

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

		List<File> projectFiles = ListProjectFilesTask
				.task(tmpFolder);

		assertThat(projectFiles, hasItem(file1));
		assertThat(projectFiles, hasItem(file3));

		assertThat(projectFiles, not(hasItem(file0)));
		assertThat(projectFiles, not(hasItem(file2)));
		assertThat(projectFiles, not(hasItem(file4)));
	}
}
