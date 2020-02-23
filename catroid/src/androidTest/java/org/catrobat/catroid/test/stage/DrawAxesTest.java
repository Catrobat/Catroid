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

package org.catrobat.catroid.test.stage;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.stage.StageListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.util.Arrays;

import androidx.annotation.IdRes;
import androidx.test.core.app.ApplicationProvider;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class DrawAxesTest {
	private StageListener stageListener;
	private static Project project;

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{1440, 2560, false, 8, 8f},
				{1440, 2560, true, 8, 4.5f},
				{500, 500, false, 16, 0.78125f},
				{768, 1280, true, 16, 1.2f},
				{768, 1280, false, 20, 1.6f},
				{240, 320, true, 24, 0.25f},
				{240, 432, false, 32, 0.3375f},
				{230, 400, true, 10, 0.575f},
				{1080, 1920, false, 40, 1.2f},
				{800, 1280, true, 40, 0.5f},
				{480, 800, true, 48, 0.25f}
		});
	}

	@Parameterized.Parameter
	public @IdRes int projectLayoutHeight;

	@Parameterized.Parameter(1)
	public @IdRes int projectLayoutWidth;

	@Parameterized.Parameter(2)
	public @IdRes boolean projectLandscapeMode;

	@Parameterized.Parameter(3)
	public @IdRes int labelLayoutHeight;

	@Parameterized.Parameter(4)
	public @IdRes float expectedScaleFactor;

	@Before
	public void setUp() throws Exception {
		project = new Project(ApplicationProvider.getApplicationContext(), "Project");
		ProjectManager.getInstance().setCurrentProject(project);
		project.getXmlHeader().setVirtualScreenHeight(projectLayoutHeight);
		project.getXmlHeader().setVirtualScreenWidth(projectLayoutWidth);
		project.getXmlHeader().setlandscapeMode(projectLandscapeMode);
	}

	@Test
	public void testAxisFontSize() {
		stageListener = new StageListener();

		BitmapFont font = Mockito.mock(BitmapFont.class);
		GlyphLayout layout = Mockito.mock(GlyphLayout.class);
		layout.height = labelLayoutHeight;

		float scaleFactor = stageListener.getFontScaleFactor(project, font, layout);
		assertEquals(expectedScaleFactor, scaleFactor);
	}
}
