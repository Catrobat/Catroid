/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.matchers.StageMatchers;
import org.catrobat.catroid.utils.UtilUi;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isFocusable;

@RunWith(AndroidJUnit4.class)
public class StageTestSimple {
	private static final int PROJECT_WIDTH = 480;
	private static final int PROJECT_HEIGHT = 800;

	@Rule
	public BaseActivityInstrumentationRule<StageActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void checkForBlueSpriteColor() {
		createProjectWithBlueSprite("blueProject");
		baseActivityTestRule.launchActivity(null);

		byte[] blue = { 0, (byte) 162, (byte) 232, (byte) 255 };

		//color matcher only accepts a GL20View, this can be aquired by getting the only focusable element in the stage
		onView(isFocusable())
				.check(matches(StageMatchers.isColorAtPx(blue, 1, 1)));
	}

	public Project createProjectWithBlueSprite(String projectName) {
		ScreenValues.SCREEN_HEIGHT = PROJECT_HEIGHT;
		ScreenValues.SCREEN_WIDTH = PROJECT_WIDTH;

		Project project = new Project(null, projectName);

		// blue Sprite
		Sprite blueSprite = new SingleSprite("blueSprite");
		StartScript blueStartScript = new StartScript();
		LookData blueLookData = new LookData();
		String blueImageName = "blue_image.bmp";

		blueLookData.setLookName(blueImageName);

		blueSprite.getLookDataList().add(blueLookData);

		blueStartScript.addBrick(new PlaceAtBrick(0, 0));
		blueStartScript.addBrick(new SetSizeToBrick(5000));
		blueSprite.addScript(blueStartScript);

		project.getDefaultScene().addSprite(blueSprite);

		StorageHandler.getInstance().saveProject(project);
		File blueImageFile = UiTestUtils.saveFileToProject(project.getName(), project.getDefaultScene().getName(),
				blueImageName,
				org.catrobat.catroid.test.R.raw.blue_image, InstrumentationRegistry.getContext(),
				UiTestUtils.FileTypes.IMAGE);

		blueLookData.setLookFilename(blueImageFile.getName());

		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(blueSprite);
		UtilUi.updateScreenWidthAndHeight(InstrumentationRegistry.getContext());

		return project;
	}
}
