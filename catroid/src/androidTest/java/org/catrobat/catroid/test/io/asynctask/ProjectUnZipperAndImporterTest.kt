/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
package org.catrobat.catroid.test.io.asynctask

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase
import org.catrobat.catroid.common.Constants.CACHE_DIR
import org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME
import org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY
import org.catrobat.catroid.content.backwardcompatibility.ProjectMetaDataParser
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.asynctask.unzipAndImportProjects
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.utils.FileMetaDataExtractor
import org.hamcrest.MatcherAssert
import org.hamcrest.core.IsCollectionContaining
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

private const val AIR_FIGHT_0_5 = "Air fight 0.5"
private const val FALLING_BALLS = "Falling balls"
private const val AIR_FIGHT_0_5_1 = "Air fight 0.5 (1)"

@RunWith(AndroidJUnit4::class)
class ProjectUnZipperAndImporterTest {
    private lateinit var projectAirFightFile: File
    private lateinit var projectFallingBallsFile: File

    @Before
    @Throws(IOException::class)
    fun setUp() {
        TestUtils.deleteProjects(AIR_FIGHT_0_5, AIR_FIGHT_0_5_1, FALLING_BALLS)
        DEFAULT_ROOT_DIRECTORY.mkdir()
        CACHE_DIR.mkdir()
        var assetName = "Air_fight_0.5.catrobat"
        var inputStream =
            InstrumentationRegistry.getInstrumentation().context.assets.open(assetName)
        projectAirFightFile = StorageOperations.copyStreamToDir(inputStream, CACHE_DIR, assetName)
        assetName = "Falling_balls.catrobat"
        inputStream = InstrumentationRegistry.getInstrumentation().context.assets.open(assetName)
        projectFallingBallsFile =
            StorageOperations.copyStreamToDir(inputStream, CACHE_DIR, assetName)
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        StorageOperations.deleteDir(CACHE_DIR)
        TestUtils.deleteProjects(AIR_FIGHT_0_5, AIR_FIGHT_0_5_1, FALLING_BALLS)
    }

    @Test
    @Throws(IOException::class)
    fun testUnzipAndImportSingleProject() {
        TestCase.assertTrue(unzipAndImportProjects(arrayOf(projectAirFightFile)))
        MatcherAssert.assertThat(
            FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY),
            IsCollectionContaining.hasItem(AIR_FIGHT_0_5)
        )
        val xmlFile = File(File(DEFAULT_ROOT_DIRECTORY, AIR_FIGHT_0_5), CODE_XML_FILE_NAME)
        assertEquals(AIR_FIGHT_0_5, ProjectMetaDataParser(xmlFile).projectMetaData.name)
    }

    @Test
    @Throws(IOException::class)
    fun testUnzipAndImportMultipleProjects() {
        TestCase.assertTrue(unzipAndImportProjects(arrayOf(projectAirFightFile, projectFallingBallsFile)))
        var projectNames = FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY)
        MatcherAssert.assertThat(projectNames, IsCollectionContaining.hasItem(AIR_FIGHT_0_5))

        var xmlFile = File(File(DEFAULT_ROOT_DIRECTORY, AIR_FIGHT_0_5), CODE_XML_FILE_NAME)
        assertEquals(AIR_FIGHT_0_5, ProjectMetaDataParser(xmlFile).projectMetaData.name)
        projectNames = FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY)
        MatcherAssert.assertThat(projectNames, IsCollectionContaining.hasItem(FALLING_BALLS))

        xmlFile = File(File(DEFAULT_ROOT_DIRECTORY, FALLING_BALLS), CODE_XML_FILE_NAME)
        assertEquals(FALLING_BALLS, ProjectMetaDataParser(xmlFile).projectMetaData.name)
    }

    @Test
    @Throws(IOException::class)
    fun testUnzipAndImportSameProjectTwice() {
        TestCase.assertTrue(unzipAndImportProjects(arrayOf(projectAirFightFile)))
        var projectNames = FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY)
        MatcherAssert.assertThat(projectNames, IsCollectionContaining.hasItem(AIR_FIGHT_0_5))

        var xmlFile = File(File(DEFAULT_ROOT_DIRECTORY, AIR_FIGHT_0_5), CODE_XML_FILE_NAME)
        assertEquals(AIR_FIGHT_0_5, ProjectMetaDataParser(xmlFile).projectMetaData.name)
        TestCase.assertTrue(unzipAndImportProjects(arrayOf(projectAirFightFile)))
        projectNames = FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY)
        MatcherAssert.assertThat(projectNames, IsCollectionContaining.hasItem(AIR_FIGHT_0_5_1))

        xmlFile = File(File(DEFAULT_ROOT_DIRECTORY, AIR_FIGHT_0_5_1), CODE_XML_FILE_NAME)
        assertEquals(AIR_FIGHT_0_5_1, ProjectMetaDataParser(xmlFile).projectMetaData.name)
    }
}
