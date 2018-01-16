/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
package org.catrobat.catroid.test.utiltests;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uiespresso.util.FileTestUtils;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;

import static android.support.test.InstrumentationRegistry.getTargetContext;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class UtilFileSizeTranslationsTest {
	private Locale defaultLocale = Locale.getDefault();

	private File projectFolder;
	private File imageFile;
	private File soundFile;
	private NumberFormat currentNumberformat;

	@Before
	public void setUp() throws Exception {
		SettingsActivity.updateLocale(getTargetContext(), new Locale("ar"));

		createProjectWithFiles();
	}

	@After
	public void tearDown() throws Exception {
		SettingsActivity.updateLocale(getTargetContext(), defaultLocale);
	}

	@Test
	public void testFileSize() {
		currentNumberformat = NumberFormat.getInstance(Locale.getDefault());
		String arabicKb = "كيلوبايت";
		double soundExpectedSize = 1.7;
		double lookExpectedSize = 2.6;
		double projectExpectedSize = 5.9;

		assertEquals(currentNumberformat.format(soundExpectedSize) + " " + arabicKb,
				UtilFile.getSizeAsString(soundFile, InstrumentationRegistry.getTargetContext()));

		assertEquals(currentNumberformat.format(lookExpectedSize) + " " + arabicKb,
				UtilFile.getSizeAsString(imageFile, InstrumentationRegistry.getTargetContext()));

		assertEquals(currentNumberformat.format(projectExpectedSize) + " " + arabicKb,
				UtilFile.getSizeAsString(projectFolder, InstrumentationRegistry.getTargetContext()));
	}

	public void createProjectWithFiles() {
		String projectName = "fileSizeArabicTest";
		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);

		projectFolder = new File(Utils.buildProjectPath(projectName));

		imageFile = FileTestUtils.saveFileToProject(project.getName(), project.getDefaultScene().getName(),
				"blue_image.bmp", org.catrobat.catroid.test.R.raw.blue_image, InstrumentationRegistry.getContext(),
				FileTestUtils.FileTypes.IMAGE);

		soundFile = FileTestUtils.saveFileToProject(project.getName(), project.getDefaultScene().getName(),
				"longsound.mp3", org.catrobat.catroid.test.R.raw.longsound, InstrumentationRegistry.getTargetContext(),
				FileTestUtils.FileTypes.SOUND);
	}
}
