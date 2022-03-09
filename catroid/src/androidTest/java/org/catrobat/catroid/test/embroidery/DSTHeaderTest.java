/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.embroidery.DSTFileConstants;
import org.catrobat.catroid.embroidery.DSTHeader;
import org.catrobat.catroid.embroidery.EmbroideryHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.koin.java.KoinJavaComponent.inject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class DSTHeaderTest {
	private final String projectName = DSTHeaderTest.class.getSimpleName();
	private FileOutputStream fileOutputStream;

	@Before
	public void setUp() {
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);
		final ProjectManager projectManager = inject(ProjectManager.class).getValue();
		projectManager.setCurrentProject(project);
		fileOutputStream = Mockito.mock(FileOutputStream.class);
	}

	@Test
	public void testDSTHeaderInitialize() throws IOException {
		final float expectedX = 2.0f;
		final float expectedY = 4.0f;
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(String.format(DSTFileConstants.DST_HEADER_LABEL, projectName))
				.append(String.format(Locale.getDefault(), DSTFileConstants.DST_HEADER, 1,
						1, (int) expectedX, (int) expectedX, (int) expectedY, (int) expectedY,
						(int) (expectedX - expectedX), (int) (expectedY - expectedY), 0, 0,
						"*****").replace(' ',
						'\0'))
				.append(DSTFileConstants.HEADER_FILL);

		EmbroideryHeader header = new DSTHeader();
		header.initialize(1.0f, 2.0f);
		header.appendToStream(fileOutputStream);
		verify(fileOutputStream, times(1)).write(stringBuilder.toString().getBytes());
	}

	@Test
	public void testDSTHeaderColorChangeReset() throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(String.format(DSTFileConstants.DST_HEADER_LABEL, projectName))
				.append(String.format(Locale.getDefault(), DSTFileConstants.DST_HEADER, 1,
						2, 0, 0, 0, 0, 0, 0, 0, 0, "*****").replace(' ', '\0'))
				.append(DSTFileConstants.HEADER_FILL);
		EmbroideryHeader header = new DSTHeader();
		header.initialize(0, 0);
		header.addColorChange();

		header.appendToStream(fileOutputStream);
		verify(fileOutputStream, times(1)).write(stringBuilder.toString().getBytes());
	}

	@Test
	public void testDSTHeaderUpdate() throws IOException {
		final float expectedXInit = 0.0f;
		final float expectedXHigh = 10.0f;
		final float expectedXLow = -4.0f;
		final float expectedYInit = 0.0f;
		final float expectedYHigh = 4.0f;
		final float expectedYLow = -10.0f;
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(String.format(DSTFileConstants.DST_HEADER_LABEL, projectName))
				.append(String.format(Locale.getDefault(), DSTFileConstants.DST_HEADER, 3,
						1, (int) expectedXHigh, (int) expectedXLow, (int) expectedYHigh,
						(int) expectedYLow, (int) (expectedXHigh - expectedXInit),
						(int) (expectedYHigh - expectedYInit), 0, 0, "*****").replace(' ',
						'\0'))
				.append(DSTFileConstants.HEADER_FILL);

		EmbroideryHeader header = new DSTHeader();
		header.initialize(0, 0);
		header.update(-2.0f, -5.0f);
		header.update(5.0f, 2.0f);

		header.appendToStream(fileOutputStream);
		verify(fileOutputStream, times(1)).write(stringBuilder.toString().getBytes());
	}
}
