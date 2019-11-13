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

import org.catrobat.catroid.ProjectManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import static org.catrobat.catroid.embroidery.DSTFileConstants.toEmbroideryUnit;

public class DSTHeader implements EmbroideryHeader {
	private float minX;
	private float maxX;
	private float minY;
	private float maxY;
	private float firstX;
	private float firstY;
	private float lastX;
	private float lastY;
	private int colorChangeCount;
	private int pointAmount;

	@Override
	public void initialize(float currentX, float currentY) {
		minX = toEmbroideryUnit(currentX);
		maxX = toEmbroideryUnit(currentX);
		minY = toEmbroideryUnit(currentY);
		maxY = toEmbroideryUnit(currentY);
		firstX = toEmbroideryUnit(currentX);
		firstY = toEmbroideryUnit(currentY);
		lastX = toEmbroideryUnit(currentX);
		lastY = toEmbroideryUnit(currentY);
		colorChangeCount = 1;
		pointAmount = 1;
	}

	@Override
	public void update(float currentX, float currentY) {
		float x = toEmbroideryUnit(currentX);
		float y = toEmbroideryUnit(currentY);
		if (minX > x) {
			minX = x;
		}
		if (maxX < x) {
			maxX = x;
		}
		if (minY > y) {
			minY = y;
		}
		if (maxY < y) {
			maxY = y;
		}
		lastX = x;
		lastY = y;
		pointAmount++;
	}

	@Override
	public void addColorChange() {
		colorChangeCount++;
	}

	@Override
	public void appendToStream(FileOutputStream fileStream) throws IOException {
		final int mx = 0;
		final int my = 0;
		final String pd = "*****";
		StringBuilder stringBuilder = new StringBuilder();
		String label = ProjectManager.getInstance().getCurrentProject().getName();
		if (label.length() > 15) {
			label = label.substring(0, 15);
		}
		stringBuilder.append(String.format(DSTFileConstants.DST_HEADER_LABEL, label))
				.append(String.format(Locale.getDefault(), DSTFileConstants.DST_HEADER, pointAmount,
						colorChangeCount, (int) maxX, (int) minX, (int) maxY, (int) minY,
						(int) (lastX - firstX), (int) (lastY - firstY), mx, my, pd).replace(' ',
						'\0'))
				.append(DSTFileConstants.HEADER_FILL);

		fileStream.write(stringBuilder.toString().getBytes());
	}

	@Override
	public void reset() {
		colorChangeCount = 1;
	}
}
