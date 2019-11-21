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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DSTFileGenerator {
	private EmbroideryStream stream;

	public DSTFileGenerator(EmbroideryStream stream) {
		this.stream = stream;
	}

	public void writeToDSTFile(File dstFile) throws IOException {
		try (FileOutputStream fileStream = new FileOutputStream(dstFile)) {
			stream.getHeader().appendToStream(fileStream);
			writeStitchPoints(fileStream);
			fileStream.write(DSTFileConstants.FILE_END);
		}
	}

	private void writeStitchPoints(FileOutputStream fileStream) throws IOException {
		ArrayList<StitchPoint> stitchPoints = stream.getPointList();
		for (StitchPoint point : stitchPoints) {
			point.appendToStream(fileStream);
		}
	}
}
