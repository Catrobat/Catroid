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
package org.catrobat.catroid.uiespresso.stage;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.matchers.StageMatchers;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.catrobat.catroid.utils.ScreenValueHandler;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isFocusable;

@RunWith(AndroidJUnit4.class)
public class StageSimpleTest {

	private static final int PROJECT_WIDTH = 480;
	private static final int PROJECT_HEIGHT = 800;

	@Rule
	public BaseActivityTestRule<StageActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProjectWithBlueSprite("blueProject");
	}

	@Category({Cat.Educational.class})
	@Test
	public void checkForBlueSpriteColor() {
		baseActivityTestRule.launchActivity(null);

		byte[] blue = {0, (byte) 162, (byte) 232, (byte) 255};

		//color matcher only accepts a GL20View, this can be aquired by getting the only focusable element in the stage
		onView(isFocusable())
				.check(matches(StageMatchers.isColorAtPx(blue, 1, 1)));
	}

	public Project createProjectWithBlueSprite(String projectName) throws IOException {
		ScreenValues.SCREEN_HEIGHT = PROJECT_HEIGHT;
		ScreenValues.SCREEN_WIDTH = PROJECT_WIDTH;

		Project project = UiTestUtils.createDefaultTestProject(projectName);

		Sprite blueSprite = UiTestUtils.getDefaultTestSprite(project);
		Script blueStartScript = UiTestUtils.getDefaultTestScript(project);
		LookData blueLookData = new LookData();
		String blueImageName = "blue_image.bmp";

		blueLookData.setName(blueImageName);

		blueSprite.getLookList().add(blueLookData);

		blueStartScript.addBrick(new PlaceAtBrick(0, 0));
		blueStartScript.addBrick(new SetSizeToBrick(5000));

		XstreamSerializer.getInstance().saveProject(project);

		File blueImageFile = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.raw.blue_image,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				blueImageName,
				1);

		blueLookData.setFile(blueImageFile);

		XstreamSerializer.getInstance().saveProject(project);
		ScreenValueHandler.updateScreenWidthAndHeight(InstrumentationRegistry.getInstrumentation().getContext());

		return project;
	}
}
