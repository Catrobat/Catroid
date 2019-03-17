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
package org.catrobat.catroid.test.utiltests;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.utils.FileMetaDataExtractor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import static android.support.test.InstrumentationRegistry.getTargetContext;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

@RunWith(AndroidJUnit4.class)
public class UtilFileSizeTranslationsTest {
	private Locale defaultLocale = Locale.getDefault();

	private File projectFolder;
	private File imageFile;
	private File soundFile;
	private String projectName = "fileSizeArabicTest";

	@Before
	public void setUp() throws Exception {
		SettingsFragment.updateLocale(getTargetContext(), new Locale("ar"));
		createProjectWithFiles();
	}

	@After
	public void tearDown() throws Exception {
		SettingsFragment.updateLocale(getTargetContext(), defaultLocale);
		File projectDir = new File(DEFAULT_ROOT_DIRECTORY, projectName);
		if (projectDir.isDirectory()) {
			StorageOperations.deleteDir(projectDir);
		}
	}

	@Test
	public void testFileSize() {
		NumberFormat currentNumberFormat = NumberFormat.getInstance(Locale.getDefault());
		currentNumberFormat.setMaximumFractionDigits(1);
		String arabicKb = "كيلوبايت";

		double soundExpectedSize = scaleFileSize(FileMetaDataExtractor.getSizeOfFileOrDirectoryInByte(soundFile));
		double lookExpectedSize = scaleFileSize(FileMetaDataExtractor.getSizeOfFileOrDirectoryInByte(imageFile));
		double projectExpectedSize =
				scaleFileSize(FileMetaDataExtractor.getSizeOfFileOrDirectoryInByte(projectFolder));

		assertEquals(currentNumberFormat.format(soundExpectedSize) + " " + arabicKb,
				FileMetaDataExtractor.getSizeAsString(soundFile, InstrumentationRegistry.getTargetContext()));

		assertEquals(currentNumberFormat.format(lookExpectedSize) + " " + arabicKb,
				FileMetaDataExtractor.getSizeAsString(imageFile, InstrumentationRegistry.getTargetContext()));

		assertEquals(currentNumberFormat.format(projectExpectedSize) + " " + arabicKb,
				FileMetaDataExtractor.getSizeAsString(projectFolder, InstrumentationRegistry.getTargetContext()));
	}

	private void createProjectWithFiles() throws IOException {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		XstreamSerializer.getInstance().saveProject(project);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentlyEditedScene(project.getDefaultScene());

		projectFolder = new File(DEFAULT_ROOT_DIRECTORY, projectName);

		imageFile = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getContext().getResources(),
				org.catrobat.catroid.test.R.raw.blue_image,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				"blue_image.bmp",
				1);

		soundFile = ResourceImporter.createSoundFileFromResourcesInDirectory(
				InstrumentationRegistry.getContext().getResources(),
				org.catrobat.catroid.test.R.raw.longsound,
				new File(project.getDefaultScene().getDirectory(), SOUND_DIRECTORY_NAME),
				"longsound.mp3");
	}

	private double scaleFileSize(long sizeInByte) {
		return sizeInByte / (double) 1024;
	}
}
