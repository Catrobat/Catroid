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

package org.catrobat.catroid.embroidery;

import android.graphics.PointF;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.badlogic.gdx.utils.ByteArray;

import org.catrobat.catroid.ProjectManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Locale;

public class DSTFileGenerator {
	/* https://edutechwiki.unige.ch/en/Embroidery_format_DST
	/* http://www.achatina.de/sewing/main/TECHNICL.HTM
	DST Encoding
	BYTE  |  7  |  6  |  5  |  4  ||  3  |  2  |  1  |  0
	------|------------------------------------------------
	  1   | y+1 | y-1 | y+9 | y-9 || x-9 | x+9 | x-1 | x+1
	  2   | y+3 | y-3 | y+27| y-27|| x-27| x+27| x-3 | x+3
	  3   | c2  | c1  | y+81| y-81|| x-81| x+81| set | set
	*/
	private static final String TAG = DSTFileGenerator.class.getSimpleName();
	public static final int MAX_DISTANCE = 121;
	public static final float STITCH_POINT_UNIT_FACTOR = 2f;
	private static final char SUBSTITUTE_CHAR = 0x1A;
	private static final String DST_HEADER_LABEL = "LA:%-15s\n" + SUBSTITUTE_CHAR;
	private static final String DST_HEADER = "ST:%-6d\n" + SUBSTITUTE_CHAR + "CO:%-2d\n" + SUBSTITUTE_CHAR
			+ "+X:%-4d\n" + SUBSTITUTE_CHAR + "-X:%-4d\n" + SUBSTITUTE_CHAR
			+ "+Y:%-4d\n" + SUBSTITUTE_CHAR + "-Y:%-4d\n" + SUBSTITUTE_CHAR
			+ "AX:%-5d\n" + SUBSTITUTE_CHAR + "AY:%-5d\n" + SUBSTITUTE_CHAR
			+ "MX:%-5d\n" + SUBSTITUTE_CHAR + "MY:%-5d\n" + SUBSTITUTE_CHAR
			+ "PD:%-5s\n" + SUBSTITUTE_CHAR;
	private static final String HEADER_FILL = String.format("%388s", " ");
	public static final int[] CONVERSION_TABLE = {0x0, 0x1, 0x6, 0x4, 0x5, 0x1a, 0x18, 0x19, 0x12, 0x10, 0x11, 0x16,
			0x14, 0x15, 0x6a, 0x68, 0x69, 0x62, 0x60, 0x61, 0x66, 0x64, 0x65, 0x4a, 0x48, 0x49, 0x42, 0x40, 0x41,
			0x46, 0x44, 0x45, 0x5a, 0x58, 0x59, 0x52, 0x50, 0x51, 0x56, 0x54, 0x55, 0x1aa, 0x1a8, 0x1a9, 0x1a2,
			0x1a0, 0x1a1, 0x1a6, 0x1a4, 0x1a5, 0x18a, 0x188, 0x189, 0x182, 0x180, 0x181, 0x186, 0x184, 0x185, 0x19a,
			0x198, 0x199, 0x192, 0x190, 0x191, 0x196, 0x194, 0x195, 0x12a, 0x128, 0x129, 0x122, 0x120, 0x121, 0x126,
			0x124, 0x125, 0x10a, 0x108, 0x109, 0x102, 0x100, 0x101, 0x106, 0x104, 0x105, 0x11a, 0x118, 0x119, 0x112,
			0x110, 0x111, 0x116, 0x114, 0x115, 0x16a, 0x168, 0x169, 0x162, 0x160, 0x161, 0x166, 0x164, 0x165, 0x14a,
			0x148, 0x149, 0x142, 0x140, 0x141, 0x146, 0x144, 0x145, 0x15a, 0x158, 0x159, 0x152, 0x150, 0x151, 0x156,
			0x154, 0x155, 0x2, 0x9, 0x8, 0xa, 0x25, 0x24, 0x26, 0x21, 0x20, 0x22, 0x29, 0x28, 0x2a, 0x95, 0x94, 0x96,
			0x91, 0x90, 0x92, 0x99, 0x98, 0x9a, 0x85, 0x84, 0x86, 0x81, 0x80, 0x82, 0x89, 0x88, 0x8a, 0xa5, 0xa4,
			0xa6, 0xa1, 0xa0, 0xa2, 0xa9, 0xa8, 0xaa, 0x255, 0x254, 0x256, 0x251, 0x250, 0x252, 0x259, 0x258, 0x25a,
			0x245, 0x244, 0x246, 0x241, 0x240, 0x242, 0x249, 0x248, 0x24a, 0x265, 0x264, 0x266, 0x261, 0x260, 0x262,
			0x269, 0x268, 0x26a, 0x215, 0x214, 0x216, 0x211, 0x210, 0x212, 0x219, 0x218, 0x21a, 0x205, 0x204, 0x206,
			0x201, 0x200, 0x202, 0x209, 0x208, 0x20a, 0x225, 0x224, 0x226, 0x221, 0x220, 0x222, 0x229, 0x228, 0x22a,
			0x295, 0x294, 0x296, 0x291, 0x290, 0x292, 0x299, 0x298, 0x29a, 0x285, 0x284, 0x286, 0x281, 0x280, 0x282,
			0x289, 0x288, 0x28a, 0x2a5, 0x2a4, 0x2a6, 0x2a1, 0x2a0, 0x2a2, 0x2a9, 0x2a8, 0x2aa};

	public DSTFileGenerator() {
	}

	@VisibleForTesting
	public ArrayList<StitchPoint> prepareStitchPoints(ArrayList<PointF> points) {
		ArrayList<PointF> convertedPoints = convertStitchPointsUnit(points);
		ArrayList<StitchPoint> stitchPoints = getStitchPoints(convertedPoints);
		return stitchPoints;
	}

	private ArrayList<StitchPoint> getStitchPoints(ArrayList<PointF> points) {
		ArrayList<StitchPoint> preparedStitchPoints = new ArrayList<>();

		ListIterator<PointF> iterator = points.listIterator();
		PointF currentPoint = points.get(0);
		int xChange;
		int yChange;
		while (iterator.hasNext()) {
			PointF nextPoint = iterator.next();

			xChange = Math.abs((int) (nextPoint.x - currentPoint.x));
			yChange = Math.abs((int) (nextPoint.y - currentPoint.y));
			if ((xChange > MAX_DISTANCE) || (yChange > MAX_DISTANCE)) {
				preparedStitchPoints.addAll(getInterpolatedJumpPoints(nextPoint, currentPoint));
			}
			preparedStitchPoints.add(new StitchPoint(nextPoint));
			currentPoint = nextPoint;
		}
		return preparedStitchPoints;
	}

	public void writeToDSTFile(File dstFile, ArrayList<PointF> rawPoints) throws IOException {
		FileOutputStream fileStream = null;
		try {
			fileStream = new FileOutputStream(dstFile);
			ArrayList<StitchPoint> stitchPoints = prepareStitchPoints(rawPoints);
			fileStream.write(getHeaderBytes(stitchPoints));
			fileStream.write(getStitchesBytes(stitchPoints));
			fileStream.write(getFileEndBytes());
		} catch (FileNotFoundException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} finally {
			if (fileStream != null) {
				fileStream.flush();
				fileStream.close();
			}
		}
	}

	private ArrayList<PointF> convertStitchPointsUnit(ArrayList<PointF> stitchPoints) {
		ArrayList<PointF> convertedStitchPoints = new ArrayList<>();

		for (PointF stitch: stitchPoints) {
			PointF stitchUnitMm = new PointF();
			stitchUnitMm.x = Math.round(stitch.x * STITCH_POINT_UNIT_FACTOR);
			stitchUnitMm.y = Math.round(stitch.y * STITCH_POINT_UNIT_FACTOR);
			convertedStitchPoints.add(stitchUnitMm);
		}
		return convertedStitchPoints;
	}

	private int[] getBoundingBox(ArrayList<StitchPoint> stitchPoints) {
		float minX = stitchPoints.get(0).point.x;
		float maxX = stitchPoints.get(0).point.x;
		float minY = stitchPoints.get(0).point.y;
		float maxY = stitchPoints.get(0).point.y;
		for (StitchPoint stitch : stitchPoints) {
			if (minX > stitch.point.x) {
				minX = stitch.point.x;
			}
			if (maxX < stitch.point.x) {
				maxX = stitch.point.x;
			}
			if (minY > stitch.point.y) {
				minY = stitch.point.y;
			}
			if (maxY < stitch.point.y) {
				maxY = stitch.point.y;
			}
		}
		return new int[] {(int) maxX, (int) minX, (int) maxY, (int) minY };
	}

	private String getHeaderString(int size, int colorChanges, int[] boundingBox, PointF firstStitch, PointF
			lastStitch) {
		final int mx = 0;
		final int my = 0;
		final String pd = "*****";
		StringBuilder stringBuilder = new StringBuilder();
		String label = ProjectManager.getInstance().getCurrentProject().getName();
		if (label.length() > 15) {
			label = label.substring(0, 15);
		}
		stringBuilder.append(String.format(DST_HEADER_LABEL, label))
				.append(String.format(Locale.getDefault(), DST_HEADER, size, colorChanges, boundingBox[0],
				boundingBox[1], boundingBox[2], boundingBox[3], (int) (lastStitch.x - firstStitch.x),
				(int) (lastStitch.y - firstStitch.y), mx, my, pd).replace(' ', '\0'))
				.append(HEADER_FILL);

		return stringBuilder.toString();
	}

	@VisibleForTesting
	public byte[] getHeaderBytes(ArrayList<StitchPoint> stitchPoints) {
		final int colorChanges = 1;
		PointF firstStitch = stitchPoints.get(0).point;
		PointF lastStitch = stitchPoints.get(stitchPoints.size() - 1).point;
		int[] boundingBox = getBoundingBox(stitchPoints);

		return getHeaderString(stitchPoints.size(), colorChanges, boundingBox, firstStitch, lastStitch).getBytes();
	}

	private byte[] getBytesForStitchPoint(int x, int y, boolean jump) {
		char yPart = (char) ((((y & 0x1) << 3) | ((y & 0x2) << 1) | ((y & 0x10) >>> 3)
				| ((y & 0x20) >>> 5)) << 4);
		char xPart = (char) (((x >>> 2) & 0xC) | (x & 0x3));
		char byte0 = (char) (yPart | xPart);

		yPart = (char) ((((y & 0x4) << 1) | ((y & 0x8) >>> 1) | ((y & 0x40) >>> 5)
				| ((y & 0x80) >>> 7)) << 4);
		xPart = (char) (((x >>> 4) & 0xC) | ((x >>> 2) & 0x3));
		char byte1 = (char) (yPart | xPart);

		yPart = (char) (((y >>> 5) & 0x10) | ((y >>> 3) & 0x20));
		xPart = (char) ((x >>> 6) & 0xC);
		char byte2 = (char) (yPart | xPart | 0x03);

		if (jump) {
			byte2 = (char) (byte2 | (0x1 << 7));
		}

		return new byte[] {(byte) byte0, (byte) byte1, (byte) byte2};
	}

	@VisibleForTesting
	public byte[] getStitchesBytes(ArrayList<StitchPoint> stitchPoints) {
		int xChange;
		int yChange;
		StitchPoint currentPoint = stitchPoints.get(0);
		ByteArray stitchBytes = new ByteArray();

		for (int i = 0; i < stitchPoints.size(); i++) {
			StitchPoint nextPoint = stitchPoints.get(i);
			xChange = (int) (nextPoint.point.x - currentPoint.point.x);
			yChange = (int) (nextPoint.point.y - currentPoint.point.y);
			int xValue = (xChange < 0 ? CONVERSION_TABLE[(xChange * (-1)) + 121] : CONVERSION_TABLE[xChange]);
			int yValue = (yChange < 0 ? CONVERSION_TABLE[(yChange * (-1)) + 121] : CONVERSION_TABLE[yChange]);
			stitchBytes.addAll(getBytesForStitchPoint(xValue, yValue, currentPoint.jump));
			currentPoint = nextPoint;
		}
		return stitchBytes.toArray();
	}

	@VisibleForTesting
	public byte[] getFileEndBytes() {
		byte byte0 = 0;
		byte byte1 = 0;
		byte byte2 = (byte) 0xF3;
		return new byte[] {byte0, byte1, byte2};
	}

	private ArrayList<StitchPoint> getInterpolatedJumpPoints(PointF nextPoint, PointF lastValidPoint) {
		float xDistance = nextPoint.x - lastValidPoint.x;
		float yDistance = nextPoint.y - lastValidPoint.y;
		float maxDistance = Math.max(Math.abs(xDistance), Math.abs(yDistance));
		int splitCount = (int) Math.ceil(maxDistance / DSTFileGenerator.MAX_DISTANCE);

		ArrayList<StitchPoint> jumpPoints = new ArrayList<>();
		jumpPoints.add(new StitchPoint(lastValidPoint, true));

		for (int count = 1; count < splitCount; count++) {
			float splitFactor = (float) count / splitCount;

			PointF newJumpPointPoint = new PointF();
			newJumpPointPoint.x = interpolate(nextPoint.x, lastValidPoint.x, splitFactor);
			newJumpPointPoint.y = interpolate(nextPoint.y, lastValidPoint.y, splitFactor);
			jumpPoints.add(new StitchPoint(newJumpPointPoint, true));
		}
		return jumpPoints;
	}

	private float interpolate(float endValue, float startValue, float percentage) {
		return Math.round(startValue + percentage * (endValue - startValue));
	}

	public static class StitchPoint {
		public PointF point;
		public boolean jump = false;

		public StitchPoint(PointF point, boolean jump) {
			this.point = point;
			this.jump = jump;
		}

		public StitchPoint(PointF point) {
			this.point = point;
		}

		@Override
		public boolean equals(Object obj) {
			if ((obj == null) || !(obj instanceof StitchPoint)) {
				return false;
			}
			StitchPoint stitchPoint = (StitchPoint) obj;
			return stitchPoint.point.equals(this.point) && stitchPoint.jump == this.jump;
		}

		@Override
		public int hashCode() {
			int xPart = (int) this.point.x & 0xFFFF;
			int yPart = ((int) this.point.y & 0x7FFF) << 15;
			int jumpPart = (jump ? 1 : 0) << 31;
			return xPart | yPart | jumpPart;
		}
	}
}
