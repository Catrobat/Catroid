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

package org.catrobat.catroid.test.io.asynctask;

import org.catrobat.catroid.content.backwardcompatibility.ProjectMetaDataParser;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.asynctask.ProjectUnzipAndImportTask;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.FileMetaDataExtractor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;

import static org.catrobat.catroid.common.Constants.CACHE_DIR;
import static org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

@RunWith(AndroidJUnit4.class)
public class ProjectUnzipAndImportTaskTest {

	private static final String AIR_FIGHT_0_5 = "Air fight 0.5";
	private static final String FALLING_BALLS = "Falling balls";
	private static final String AIR_FIGHT_0_5_1 = "Air fight 0.5 (1)";
	private File projectAirFightFile;
	private File projectFallingBallsFile;

	@Before
	public void setUp() throws IOException {
		TestUtils.deleteProjects(AIR_FIGHT_0_5, AIR_FIGHT_0_5_1, FALLING_BALLS);
		DEFAULT_ROOT_DIRECTORY.mkdir();
		CACHE_DIR.mkdir();

		String assetName = "Air_fight_0.5.catrobat";
		InputStream inputStream = InstrumentationRegistry.getInstrumentation().getContext().getAssets().open(assetName);
		projectAirFightFile = StorageOperations.copyStreamToDir(inputStream, CACHE_DIR, assetName);

		assetName = "Falling_balls.catrobat";
		inputStream = InstrumentationRegistry.getInstrumentation().getContext().getAssets().open(assetName);
		projectFallingBallsFile = StorageOperations.copyStreamToDir(inputStream, CACHE_DIR, assetName);
	}

	@After
	public void tearDown() throws IOException {
		StorageOperations.deleteDir(CACHE_DIR);
		TestUtils.deleteProjects(AIR_FIGHT_0_5, AIR_FIGHT_0_5_1, FALLING_BALLS);
	}

	@Test
	public void testUnzipAndImportSingleProject() throws IOException {
		assertTrue(ProjectUnzipAndImportTask
				.task(projectAirFightFile));

		FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY);

		assertThat(FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY), hasItem(AIR_FIGHT_0_5));
		File xmlFile = new File(new File(DEFAULT_ROOT_DIRECTORY, AIR_FIGHT_0_5), CODE_XML_FILE_NAME);
		assertEquals(AIR_FIGHT_0_5, new ProjectMetaDataParser(xmlFile).getProjectMetaData().getName());
	}

	@Test
	public void testUnzipAndImportMultipleProjects() throws IOException {
		assertTrue(ProjectUnzipAndImportTask
				.task(projectAirFightFile, projectFallingBallsFile));

		assertThat(FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY), hasItem(AIR_FIGHT_0_5));
		File xmlFile = new File(new File(DEFAULT_ROOT_DIRECTORY, AIR_FIGHT_0_5), CODE_XML_FILE_NAME);
		assertEquals(AIR_FIGHT_0_5, new ProjectMetaDataParser(xmlFile).getProjectMetaData().getName());

		assertThat(FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY), hasItem(FALLING_BALLS));
		xmlFile = new File(new File(DEFAULT_ROOT_DIRECTORY, FALLING_BALLS), CODE_XML_FILE_NAME);
		assertEquals(FALLING_BALLS, new ProjectMetaDataParser(xmlFile).getProjectMetaData().getName());
	}

	@Test
	public void testUnzipAndImportSameProjectTwice() throws IOException {
		assertTrue(ProjectUnzipAndImportTask
				.task(projectAirFightFile));

		assertThat(FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY), hasItem(AIR_FIGHT_0_5));
		File xmlFile = new File(new File(DEFAULT_ROOT_DIRECTORY, AIR_FIGHT_0_5), CODE_XML_FILE_NAME);
		assertEquals(AIR_FIGHT_0_5, new ProjectMetaDataParser(xmlFile).getProjectMetaData().getName());

		assertTrue(ProjectUnzipAndImportTask
				.task(projectAirFightFile));

		assertThat(FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY), hasItem(AIR_FIGHT_0_5_1));
		xmlFile = new File(new File(DEFAULT_ROOT_DIRECTORY, AIR_FIGHT_0_5_1), CODE_XML_FILE_NAME);
		assertEquals(AIR_FIGHT_0_5_1, new ProjectMetaDataParser(xmlFile).getProjectMetaData().getName());
	}
}
