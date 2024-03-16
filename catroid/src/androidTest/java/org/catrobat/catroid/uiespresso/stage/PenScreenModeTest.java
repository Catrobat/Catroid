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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.ScreenModes;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.MoveNStepsBrick;
import org.catrobat.catroid.content.bricks.PenDownBrick;
import org.catrobat.catroid.content.bricks.PenUpBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.SetPenColorBrick;
import org.catrobat.catroid.content.bricks.SetPenSizeBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uiespresso.util.matchers.StageMatchers;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.catrobat.catroid.utils.ScreenValueHandler;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isFocusable;

@RunWith(AndroidJUnit4.class)
public class PenScreenModeTest {

	private static final int PROJECT_WIDTH = 480;
	private static final int PROJECT_HEIGHT = 800;

	Project project;
	byte[] colorToCheck = {0, (byte) 0, (byte) 255, (byte) 255};

	@Rule
	public BaseActivityTestRule<StageActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		project = createProject("penStretchProject");
	}

	@Test
	public void checkForPenLineStretch() {
		project.setScreenMode(ScreenModes.STRETCH);

		baseActivityTestRule.launchActivity(null);

		onView(isFocusable()).check(matches(StageMatchers.isColorAtPx(colorToCheck, 100, 100)));
	}

	@Test
	public void checkForPenLineMaximize() {
		project.setScreenMode(ScreenModes.MAXIMIZE);

		baseActivityTestRule.launchActivity(null);

		onView(isFocusable()).check(matches(StageMatchers.isColorAtPx(colorToCheck, 100, 100)));
	}

	public Project createProject(String projectName) throws IOException {
		ScreenValues.SCREEN_HEIGHT = PROJECT_HEIGHT;
		ScreenValues.SCREEN_WIDTH = PROJECT_WIDTH;

		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);

		Sprite sprite = new Sprite("penSprite");
		StartScript startScript = new StartScript();

		startScript.addBrick(new PlaceAtBrick(0, 0));
		startScript.addBrick(new SetPenSizeBrick(10000));
		startScript.addBrick(new SetPenColorBrick(new Formula(0), new Formula(0),
				new Formula(255)));
		startScript.addBrick(new PenDownBrick());
		startScript.addBrick(new MoveNStepsBrick(300));
		startScript.addBrick(new PenUpBrick());
		sprite.addScript(startScript);

		project.getDefaultScene().addSprite(sprite);

		XstreamSerializer.getInstance().saveProject(project);

		XstreamSerializer.getInstance().saveProject(project);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ScreenValueHandler.updateScreenWidthAndHeight(InstrumentationRegistry.getInstrumentation().getContext());

		return project;
	}
}
