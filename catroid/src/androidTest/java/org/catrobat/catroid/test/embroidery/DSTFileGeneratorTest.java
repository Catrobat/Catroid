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

package org.catrobat.catroid.test.embroidery;

import android.graphics.PointF;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.android.dex.util.FileUtils;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.embroidery.DSTFileGenerator;
import org.catrobat.catroid.embroidery.EmbroideryList;
import org.catrobat.catroid.io.StorageOperations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class DSTFileGeneratorTest {
	final String projectName = DSTFileGeneratorTest.class.getSimpleName();
	DSTFileGenerator fileGenerator;
	final int[] conversion = {-81, 81, -27, 27, -9, 9, -3, 3, -1, 1};

	@Before
	public void setUp() {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		ProjectManager.getInstance().setCurrentProject(project);
		fileGenerator = new DSTFileGenerator();
	}

	private int getByteForValue(int value) {
		int mask = 0x200;
		int byteValue = 0x0;
		for (int i = 0; i < conversion.length; i++) {
			if (((i % 2 == 0) && value <= (conversion[i] - 1) / 2)
					|| ((i % 2 == 1) && value >= (conversion[i] + 1) / 2)) {
				byteValue |= (mask >>> i);
				value -= conversion[i];
			}
		}
		return byteValue;
	}

	@Test
	public void testConversionTable() {
		int[] valueArray = new int[DSTFileGenerator.CONVERSION_TABLE.length];
		for (int element = 0; element < DSTFileGenerator.CONVERSION_TABLE.length; element++) {
			if (element <= 121) {
				valueArray[element] = getByteForValue(element);
			} else {
				valueArray[element] = getByteForValue((element - 121) * (-1));
			}
		}
		assertArrayEquals(DSTFileGenerator.CONVERSION_TABLE, valueArray);
	}

	@Test
	public void testWriteToSampleDSTFile() throws IOException, URISyntaxException {
		EmbroideryList stitchpoints = new EmbroideryList();
		stitchpoints.add(new PointF(-10, 0));
		stitchpoints.add(new PointF(10, 0));
		stitchpoints.add(new PointF(10, 10));
		stitchpoints.add(new PointF(0, 15));
		stitchpoints.add(new PointF(-10, 10));
		stitchpoints.add(new PointF(-10, 0));
		stitchpoints.add(new PointF(10, 10));
		stitchpoints.add(new PointF(-10, 10));
		stitchpoints.add(new PointF(10, 0));

		File dstFile = new File(Constants.CACHE_DIR, projectName + ".dst");
		if (dstFile.exists()) {
			dstFile.delete();
		}
		dstFile.createNewFile();
		fileGenerator.writeToDSTFile(dstFile, stitchpoints);

		InputStream inputStream = InstrumentationRegistry.getContext().getResources().openRawResource(org.catrobat
				.catroid.test.R.raw.sample_dst_file);
		File compareFile = StorageOperations.copyStreamToDir(inputStream, Constants.CACHE_DIR, "sample_dst_file.dst");

		assertEquals(compareFile.length(), dstFile.length());

		byte[] compareFileBytes = FileUtils.readFile(compareFile);
		byte[] dstFileBytes = FileUtils.readFile(dstFile);

		assertArrayEquals(compareFileBytes, dstFileBytes);
	}

	@Test
	public void testPrepareStitchPoints() {
		final DSTFileGenerator.StitchPoint[] expectedStitchpoints = new DSTFileGenerator.StitchPoint[]{
				new DSTFileGenerator.StitchPoint(new PointF(0, 0)),
				new DSTFileGenerator.StitchPoint(new PointF(0, 0), true),
				new DSTFileGenerator.StitchPoint(new PointF(100, 100), true),
				new DSTFileGenerator.StitchPoint(new PointF(200, 200))};

		EmbroideryList points = new EmbroideryList();
		points.add(new PointF(0, 0));
		points.add(new PointF(100, 100));
		ArrayList<DSTFileGenerator.StitchPoint> stitchPoints = fileGenerator.prepareStitchPoints(points);

		assertArrayEquals(expectedStitchpoints, stitchPoints.toArray());
	}

	@Test
	public void testHeaderBytes() {
		final String expectedHeaderString = "LA:DSTFileGenerato\n" + (char) 0x1A + ("ST:3     \n" + (char) 0x1A
				+ "CO:1 \n" + (char) 0x1A + "+X:20  \n" + (char) 0x1A + "-X:0   \n" + (char) 0x1A + "+Y:20  \n"
				+ (char) 0x1A + "-Y:0   \n" + (char) 0x1A + "AX:0    \n" + (char) 0x1A + "AY:0    \n" + (char) 0x1A
				+ "MX:0    \n" + (char) 0x1A + "MY:0    \n" + (char) 0x1A + "PD:*****\n"
				+ (char) 0x1A).replace(' ', '\0') + String.format("%388s", " ");

		EmbroideryList points = new EmbroideryList();
		points.add(new PointF(0, 0));
		points.add(new PointF(10, 10));
		points.add(new PointF(0, 0));

		ArrayList<DSTFileGenerator.StitchPoint> stitchPoints = fileGenerator.prepareStitchPoints(points);
		byte[] header = fileGenerator.getHeaderBytes(stitchPoints);
		byte[] expectedHeader = expectedHeaderString.getBytes();

		assertArrayEquals(expectedHeader, header);
	}

	@Test
	public void testStitchBytes() {
		final byte[] expectedStitchBytes = new byte[] {0, 0, (byte) 0x3,
				(byte) 0xAA, (byte) 0x55, (byte) 0x03,
				(byte) 0x55, (byte) 0xAA, (byte) 0x03};

		ArrayList<DSTFileGenerator.StitchPoint> stitchpoints = new ArrayList<>();
		stitchpoints.add(new DSTFileGenerator.StitchPoint(new PointF(0, 0)));
		stitchpoints.add(new DSTFileGenerator.StitchPoint(new PointF(20, -20)));
		stitchpoints.add(new DSTFileGenerator.StitchPoint(new PointF(0, 0)));
		byte[] stitchBytes = fileGenerator.getStitchesBytes(stitchpoints);

		assertArrayEquals(expectedStitchBytes, stitchBytes);
	}

	@Test
	public void testEndBytes() {
		final byte[] expectedEndBytes = new byte[] {0, 0, (byte) 0xF3};
		byte[] endBytes = fileGenerator.getFileEndBytes();
		assertArrayEquals(expectedEndBytes, endBytes);
	}

	@Test
	public void testStitchPointsEquals() {
		DSTFileGenerator.StitchPoint stitchPoint1 = new DSTFileGenerator.StitchPoint(new PointF(1, 2), true);
		DSTFileGenerator.StitchPoint stitchPoint2 = new DSTFileGenerator.StitchPoint(new PointF(2, 1), true);
		DSTFileGenerator.StitchPoint stitchPoint3 = new DSTFileGenerator.StitchPoint(new PointF(1, 2));
		DSTFileGenerator.StitchPoint stitchPoint4 = stitchPoint1;

		assertNotEquals(stitchPoint1, stitchPoint2);
		assertNotEquals(stitchPoint1, stitchPoint3);
		assertEquals(stitchPoint1, stitchPoint4);
	}

	@Test
	public void testStitchPointsHashCode() {
		DSTFileGenerator.StitchPoint stitchPoint1 = new DSTFileGenerator.StitchPoint(new PointF(1, 2), true);
		DSTFileGenerator.StitchPoint stitchPoint2 = new DSTFileGenerator.StitchPoint(new PointF(2, 1));
		DSTFileGenerator.StitchPoint stitchPoint3 = stitchPoint1;

		assertNotEquals(stitchPoint1.hashCode(), stitchPoint2.hashCode());
		assertEquals(stitchPoint1.hashCode(), stitchPoint3.hashCode());
	}
}
