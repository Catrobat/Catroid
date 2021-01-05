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

package org.catrobat.catroid.test.embroidery;

import com.android.dex.util.FileUtils;
import com.badlogic.gdx.graphics.Color;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.embroidery.DSTFileGenerator;
import org.catrobat.catroid.embroidery.DSTHeader;
import org.catrobat.catroid.embroidery.DSTStream;
import org.catrobat.catroid.embroidery.EmbroideryStream;
import org.catrobat.catroid.io.StorageOperations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DSTFileGeneratorTest {

	private final String projectName = DSTFileGeneratorTest.class.getSimpleName();
	private File dstFile;

	@Before
	public void setUp() throws IOException {
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);
		ProjectManager.getInstance().setCurrentProject(project);

		dstFile = new File(Constants.CACHE_DIR, projectName + ".dst");
		if (dstFile.exists()) {
			dstFile.delete();
		}
		if (!Constants.CACHE_DIR.exists()) {
			Constants.CACHE_DIR.mkdirs();
		}
		dstFile.createNewFile();
	}

	@Test
	public void testWriteToSampleDSTFile() throws IOException {
		EmbroideryStream stream = new DSTStream(new DSTHeader());
		stream.addStitchPoint(-10, 0, Color.BLACK);
		stream.addStitchPoint(10, 0, Color.BLACK);
		stream.addStitchPoint(10, 10, Color.BLACK);
		stream.addStitchPoint(0, 15, Color.BLACK);
		stream.addStitchPoint(-10, 10, Color.BLACK);
		stream.addStitchPoint(-10, 0, Color.BLACK);
		stream.addStitchPoint(10, 10, Color.BLACK);
		stream.addStitchPoint(-10, 10, Color.BLACK);
		stream.addStitchPoint(10, 0, Color.BLACK);
		DSTFileGenerator fileGenerator = new DSTFileGenerator(stream);
		fileGenerator.writeToDSTFile(dstFile);

		InputStream inputStream = InstrumentationRegistry.getInstrumentation().getContext().getResources().openRawResource(org.catrobat
				.catroid.test.R.raw.sample_dst_file);
		File compareFile = StorageOperations.copyStreamToDir(inputStream, Constants.CACHE_DIR, "sample_dst_file.dst");

		assertEquals(compareFile.length(), dstFile.length());

		byte[] compareFileBytes = FileUtils.readFile(compareFile);
		byte[] dstFileBytes = FileUtils.readFile(dstFile);

		assertArrayEquals(compareFileBytes, dstFileBytes);
	}

	private void addArrowToStream(EmbroideryStream stream, int shiftFactor) {
		stream.addStitchPoint(0, shiftFactor, Color.BLACK);
		stream.addStitchPoint(40, -40 + shiftFactor, Color.BLACK);
		stream.addStitchPoint(-40, -40 + shiftFactor, Color.BLACK);
		stream.addStitchPoint(0, shiftFactor, Color.BLACK);
	}

	private void addColorChangeAndJumpToStream(EmbroideryStream stream, int shiftFactor) {
		stream.addColorChange();
		stream.addStitchPoint(0, shiftFactor, Color.BLACK);
		stream.addJump();
		stream.addStitchPoint(0, shiftFactor, Color.BLACK);
	}

	@Test
	public void testWriteToComplexSampleDSTFile() throws IOException {
		EmbroideryStream stream = new DSTStream(new DSTHeader());
		addArrowToStream(stream, 0);
		addColorChangeAndJumpToStream(stream, 0);
		addArrowToStream(stream, 20);
		addColorChangeAndJumpToStream(stream, 20);
		addArrowToStream(stream, 40);

		DSTFileGenerator fileGenerator = new DSTFileGenerator(stream);
		fileGenerator.writeToDSTFile(dstFile);

		InputStream inputStream = InstrumentationRegistry.getInstrumentation().getContext().getResources().openRawResource(org.catrobat
				.catroid.test.R.raw.complex_sample_dst_file);
		File compareFile = StorageOperations.copyStreamToDir(inputStream, Constants.CACHE_DIR,
				"complex_sample_dst_file.dst");

		assertEquals(compareFile.length(), dstFile.length());

		byte[] compareFileBytes = FileUtils.readFile(compareFile);
		byte[] dstFileBytes = FileUtils.readFile(dstFile);

		assertArrayEquals(compareFileBytes, dstFileBytes);
	}
}
