/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.uitest.content.brick;

import com.badlogic.gdx.graphics.Color;
import com.robotium.solo.Condition;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.PenDownBrick;
import org.catrobat.catroid.content.bricks.PenUpBrick;
import org.catrobat.catroid.content.bricks.SetPenColorBrick;
import org.catrobat.catroid.content.bricks.SetPenSizeBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class PenBricksTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final int NEW_PEN_SIZE = 17;
	private static final Color NEW_PEN_COLOR = Color.GREEN;

	private Sprite sprite;

	public PenBricksTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject();
	}

	public void testPenBricks() {
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		int brickCount = ProjectManager.getInstance().getCurrentSprite().getNumberOfBricks();
		assertEquals("Incorrect number of bricks.", 11, brickCount);

		UiTestUtils.clickOnPlayButton(solo);
		solo.waitForActivity(StageActivity.class);
		solo.waitForCondition(new Condition() {
			@Override
			public boolean isSatisfied() {
				return sprite.penConfiguration.penDown;
			}
		}, 3000);
		solo.sleep(1100);
		assertFalse("Pen not up", sprite.penConfiguration.penDown);
		solo.sleep(1000);
		assertEquals("Pen size wrong", NEW_PEN_SIZE, (int) sprite.penConfiguration.penSize);
		solo.sleep(1000);

		Color color = new Color();
		Color.argb8888ToColor(color, android.graphics.Color.argb(0xFF, (int) NEW_PEN_COLOR.r, (int) NEW_PEN_COLOR.g, (int) NEW_PEN_COLOR.b));
		assertEquals("Wrong red value", color.r, sprite.penConfiguration.penColor.r);
		assertEquals("Wrong blue value", color.g, sprite.penConfiguration.penColor.g);
		assertEquals("Wrong green value", color.b, sprite.penConfiguration.penColor.b);
	}

	private void createProject() {
		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		sprite = new Sprite("cat");
		Script script = new StartScript();
		PenDownBrick penDownBrick = new PenDownBrick();
		PenUpBrick penUpBrick = new PenUpBrick();
		SetPenSizeBrick setPenSizeBrick = new SetPenSizeBrick(NEW_PEN_SIZE);
		SetPenColorBrick setPenColorBrick = new SetPenColorBrick((int) NEW_PEN_COLOR.r, (int) NEW_PEN_COLOR.g, (int) NEW_PEN_COLOR.b);
		WaitBrick waitBrick = new WaitBrick(1000);
		script.addBrick(penDownBrick);
		script.addBrick(waitBrick);
		script.addBrick(penUpBrick);
		script.addBrick(waitBrick);
		script.addBrick(setPenSizeBrick);
		script.addBrick(waitBrick);
		script.addBrick(setPenColorBrick);

		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
