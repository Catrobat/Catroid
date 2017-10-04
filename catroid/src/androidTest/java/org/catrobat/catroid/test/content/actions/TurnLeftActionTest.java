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
package org.catrobat.catroid.test.content.actions;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.BaseInstrumentationTest;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.UtilFile;

import java.io.File;

public class TurnLeftActionTest extends BaseInstrumentationTest {

	private static final int IMAGE_FILE_ID = R.raw.icon;

	private final String projectName = "testProject";
	private File testImage;
	private LookData lookData;
	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	private static final float VALUE = 33;
	private Sprite sprite;

	@Override
	public void setUp() throws Exception {
		super.setUp();

		File projectFile = new File(Constants.DEFAULT_ROOT + "/" + projectName);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}

		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		sprite = createSprite("testSprite");

		testImage = TestUtils.saveFileToProject(this.projectName, project.getDefaultScene().getName(), "testImage.png", IMAGE_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_IMAGE_FILE);

		lookData = new LookData();
		lookData.setLookFilename(testImage.getName());
		lookData.setLookName("LookName");

		ScreenValues.SCREEN_HEIGHT = 800;
		ScreenValues.SCREEN_WIDTH = 480;
	}

	@Override
	protected void tearDown() throws Exception {
		File projectFile = new File(Constants.DEFAULT_ROOT + "/" + projectName);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}
		if (testImage != null && testImage.exists()) {
			testImage.delete();
		}
		super.tearDown();
	}

	public void testTurnLeftTwice() {
		sprite.look.setLookData(lookData);

		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createTurnLeftAction(sprite, new Formula(10.0f));
		action.act(1.0f);

		assertEquals("Wrong direction!", 10f, sprite.look.getRotation(), 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());

		action.restart();
		action.act(1.0f);

		assertEquals("Wrong direction!", 20f, sprite.look.getRotation(), 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testTurnLeftAndScale() {
		sprite.look.setLookData(lookData);

		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createTurnLeftAction(sprite, new Formula(10.0f));
		Action scaleAction = factory.createSetSizeToAction(sprite, new Formula(50.0f));
		action.act(1.0f);
		scaleAction.act(1.0f);

		assertEquals("Wrong direction!", 10f, sprite.look.getRotation(), 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testScaleAndTurnLeft() {
		sprite.look.setLookData(lookData);

		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createTurnLeftAction(sprite, new Formula(10.0f));
		Action scaleAction = factory.createSetSizeToAction(sprite, new Formula(50.0f));
		scaleAction.act(1.0f);
		action.act(1.0f);

		assertEquals("Wrong direction!", 10f, sprite.look.getRotation(), 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testTurnLeftNegative() {
		sprite.look.setLookData(lookData);

		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createTurnLeftAction(sprite, new Formula(-10.0f));
		action.act(1.0f);

		assertEquals("Wrong direction!", -10f, sprite.look.getRotation(), 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testTurnLeft() {
		sprite.look.setLookData(lookData);

		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createTurnLeftAction(sprite, new Formula(370.0f));
		action.act(1.0f);

		assertEquals("Wrong direction!", 80f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testTurnLeftAndTurnRight() {
		sprite.look.setLookData(lookData);

		ActionFactory factory = sprite.getActionFactory();
		Action turnLeftAction = factory.createTurnLeftAction(sprite, new Formula(50.0f));
		Action turnRightAction = factory.createTurnRightAction(sprite, new Formula(30.0f));
		turnLeftAction.act(1.0f);
		turnRightAction.act(1.0f);

		assertEquals("Wrong direction!", 20f, sprite.look.getRotation(), 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testBrickWithStringFormula() {
		Action action = sprite.getActionFactory().createTurnLeftAction(sprite,
				new Formula(String.valueOf(VALUE)));
		action.act(1.0f);
		assertEquals("Wrong direction!", VALUE, sprite.look.getRotation());
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());

		action = sprite.getActionFactory().createTurnLeftAction(sprite,
				new Formula(String.valueOf(NOT_NUMERICAL_STRING)));
		action.act(1.0f);
		assertEquals("Wrong direction!", VALUE, sprite.look.getRotation());
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testNullFormula() {
		Action action = sprite.getActionFactory().createTurnLeftAction(sprite, null);
		action.act(1.0f);
		assertEquals("Wrong direction!", 0f, sprite.look.getRotation());
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testNotANumberFormula() {
		Action action = sprite.getActionFactory().createTurnLeftAction(sprite,
				new Formula(Double.NaN));
		action.act(1.0f);
		assertEquals("Wrong direction!", 0f, sprite.look.getRotation());
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}
}
