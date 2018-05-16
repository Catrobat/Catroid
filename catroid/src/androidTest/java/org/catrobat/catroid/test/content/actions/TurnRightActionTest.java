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
package org.catrobat.catroid.test.content.actions;

import android.support.test.InstrumentationRegistry;
import android.test.InstrumentationTestCase;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.test.R;

import java.io.File;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;

public class TurnRightActionTest extends InstrumentationTestCase {

	private String projectName = "testProject";
	private File testImage;
	private LookData lookData;
	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	private static final float VALUE = 33;

	@Override
	public void setUp() throws Exception {
		File projectDir = new File(Constants.DEFAULT_ROOT_DIRECTORY, projectName);

		if (projectDir.exists()) {
			StorageOperations.deleteDir(projectDir);
		}

		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		XstreamSerializer.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		testImage = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getContext().getResources(),
				R.raw.icon,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				"testImage.png",
				1);

		lookData = new LookData();
		lookData.setFile(testImage);
		lookData.setName("LookName");

		ScreenValues.SCREEN_HEIGHT = 800;
		ScreenValues.SCREEN_WIDTH = 480;
	}

	@Override
	protected void tearDown() throws Exception {
		File projectDir = new File(Constants.DEFAULT_ROOT_DIRECTORY, projectName);

		if (projectDir.exists()) {
			StorageOperations.deleteDir(projectDir);
		}
		super.tearDown();
	}

	public void testTurnRightTwice() {
		Sprite sprite = new SingleSprite("test");
		sprite.look.setLookData(lookData);

		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createTurnRightAction(sprite, new Formula(10.0f));
		action.act(1.0f);

		assertEquals(100f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());

		action.restart();
		action.act(1.0f);

		assertEquals(110f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testTurnRightAndScale() {
		Sprite sprite = new SingleSprite("test");
		sprite.look.setLookData(lookData);

		ActionFactory factory = sprite.getActionFactory();
		Action turnRightAction = factory.createTurnRightAction(sprite, new Formula(10.0f));
		Action setSizeToAction = factory.createSetSizeToAction(sprite, new Formula(50.0f));

		turnRightAction.act(1.0f);
		setSizeToAction.act(1.0f);

		assertEquals("Wrong direction", -10f, sprite.look.getRotation(), 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testScaleandTurnRight() {
		Sprite sprite = new SingleSprite("test");
		sprite.look.setLookData(lookData);

		ActionFactory factory = sprite.getActionFactory();
		Action turnRightAction = factory.createTurnRightAction(sprite, new Formula(10.0f));
		Action setSizeToAction = factory.createSetSizeToAction(sprite, new Formula(50.0f));

		setSizeToAction.act(1.0f);
		turnRightAction.act(1.0f);

		assertEquals("Wrong direction", -10f, sprite.look.getRotation(), 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testTurnRightNegative() {
		Sprite sprite = new SingleSprite("test");
		sprite.look.setLookData(lookData);

		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createTurnRightAction(sprite, new Formula(-10.0f));
		action.act(1.0f);

		assertEquals("Wrong direction", 10f, sprite.look.getRotation(), 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testTurnRight() {
		Sprite sprite = new SingleSprite("test");
		sprite.look.setLookData(lookData);

		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createTurnRightAction(sprite, new Formula(370.0f));
		action.act(1.0f);

		assertEquals("Wrong direction", 100f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testTurnRightAndTurnLeft() {
		Sprite sprite = new SingleSprite("test");
		sprite.look.setLookData(lookData);

		ActionFactory factory = sprite.getActionFactory();
		Action turnRightAction = factory.createTurnRightAction(sprite, new Formula(50.0f));
		Action turnLeftAction = factory.createTurnLeftAction(sprite, new Formula(20.0f));
		turnRightAction.act(1.0f);
		turnLeftAction.act(1.0f);

		assertEquals("Wrong direction!", 120f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testBrickWithStringFormula() {
		Sprite sprite = new SingleSprite("test");
		Action action = sprite.getActionFactory().createTurnRightAction(sprite,
				new Formula(String.valueOf(VALUE)));
		action.act(1.0f);
		assertEquals("Wrong direction!", -VALUE, sprite.look.getRotation());
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());

		action = sprite.getActionFactory().createTurnRightAction(sprite,
				new Formula(String.valueOf(NOT_NUMERICAL_STRING)));
		action.act(1.0f);
		assertEquals("Wrong direction!", -VALUE, sprite.look.getRotation());
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testNullFormula() {
		Sprite sprite = new SingleSprite("test");
		Action action = sprite.getActionFactory().createTurnRightAction(sprite, null);
		action.act(1.0f);
		assertEquals("Wrong direction!", 0f, sprite.look.getRotation());
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testNotANumberFormula() {
		Sprite sprite = new SingleSprite("test");
		Action action = sprite.getActionFactory().createTurnRightAction(sprite, new Formula(Double.NaN));
		action.act(1.0f);
		assertEquals("Wrong direction!", 0f, sprite.look.getRotation());
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}
}
