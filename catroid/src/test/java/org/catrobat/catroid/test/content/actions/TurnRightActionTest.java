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

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.io.File;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class TurnRightActionTest {

	private String projectName = "testProject";
	private LookData lookData;
	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	private static final float VALUE = 33;

	@Before
	public void setUp() throws Exception {
		Project project = new Project(MockUtil.mockContextForProject(), projectName);
		ProjectManager.getInstance().setCurrentProject(project);

		lookData = new LookData();
		lookData.setFile(Mockito.mock(File.class));
		lookData.setName("LookName");

		ScreenValues.SCREEN_HEIGHT = 800;
		ScreenValues.SCREEN_WIDTH = 480;
	}

	@Test
	public void testTurnRightTwice() {
		Sprite sprite = new Sprite("test");
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

	@Test
	public void testTurnRightAndScale() {
		Sprite sprite = new Sprite("test");
		sprite.look.setLookData(lookData);

		ActionFactory factory = sprite.getActionFactory();
		Action turnRightAction = factory.createTurnRightAction(sprite, new Formula(10.0f));
		Action setSizeToAction = factory.createSetSizeToAction(sprite, new Formula(50.0f));

		turnRightAction.act(1.0f);
		setSizeToAction.act(1.0f);

		assertEquals(-10f, sprite.look.getRotation(), 1e-3);
		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testScaleandTurnRight() {
		Sprite sprite = new Sprite("test");
		sprite.look.setLookData(lookData);

		ActionFactory factory = sprite.getActionFactory();
		Action turnRightAction = factory.createTurnRightAction(sprite, new Formula(10.0f));
		Action setSizeToAction = factory.createSetSizeToAction(sprite, new Formula(50.0f));

		setSizeToAction.act(1.0f);
		turnRightAction.act(1.0f);

		assertEquals(-10f, sprite.look.getRotation(), 1e-3);
		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testTurnRightNegative() {
		Sprite sprite = new Sprite("test");
		sprite.look.setLookData(lookData);

		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createTurnRightAction(sprite, new Formula(-10.0f));
		action.act(1.0f);

		assertEquals(10f, sprite.look.getRotation(), 1e-3);
		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testTurnRight() {
		Sprite sprite = new Sprite("test");
		sprite.look.setLookData(lookData);

		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createTurnRightAction(sprite, new Formula(370.0f));
		action.act(1.0f);

		assertEquals(100f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testTurnRightAndTurnLeft() {
		Sprite sprite = new Sprite("test");
		sprite.look.setLookData(lookData);

		ActionFactory factory = sprite.getActionFactory();
		Action turnRightAction = factory.createTurnRightAction(sprite, new Formula(50.0f));
		Action turnLeftAction = factory.createTurnLeftAction(sprite, new Formula(20.0f));
		turnRightAction.act(1.0f);
		turnLeftAction.act(1.0f);

		assertEquals(120f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testBrickWithStringFormula() {
		Sprite sprite = new Sprite("test");
		Action action = sprite.getActionFactory().createTurnRightAction(sprite,
				new Formula(String.valueOf(VALUE)));
		action.act(1.0f);
		assertEquals(-VALUE, sprite.look.getRotation());
		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());

		action = sprite.getActionFactory().createTurnRightAction(sprite,
				new Formula(String.valueOf(NOT_NUMERICAL_STRING)));
		action.act(1.0f);
		assertEquals(-VALUE, sprite.look.getRotation());
		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNullFormula() {
		Sprite sprite = new Sprite("test");
		Action action = sprite.getActionFactory().createTurnRightAction(sprite, null);
		action.act(1.0f);
		assertEquals(0f, sprite.look.getRotation());
		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNotANumberFormula() {
		Sprite sprite = new Sprite("test");
		Action action = sprite.getActionFactory().createTurnRightAction(sprite, new Formula(Double.NaN));
		action.act(1.0f);
		assertEquals(0f, sprite.look.getRotation());
		assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}
}
