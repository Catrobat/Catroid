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
package org.catrobat.catroid.test.formulaeditor.parser;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.test.MockUtil;
import org.catrobat.catroid.test.formulaeditor.FormulaEditorTestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ParserTestObject {

	private static final float LOOK_ALPHA = 0.42f;
	private static final double LOOK_LAYER = 1d;
	private static final float LOOK_Y_POSITION = 23.4f;
	private static final float LOOK_X_POSITION = 5.6f;
	private static final float LOOK_BRIGHTNESS = 0.7f;
	private static final float LOOK_COLOR = 0f;
	private static final float LOOK_SCALE = 90.3f;
	private static final float LOOK_ROTATION = 30.7f;
	private static final float DELTA = 0.01f;
	private Sprite testSprite;

	@Before
	public void setUp() {
		Project project = new Project(MockUtil.mockContextForProject(), "testProject");
		ProjectManager.getInstance().setCurrentProject(project);
		testSprite = new SingleSprite("sprite");
		project.getDefaultScene().addSprite(testSprite);
		ProjectManager.getInstance().setCurrentSprite(testSprite);
		testSprite.look.setXInUserInterfaceDimensionUnit(LOOK_X_POSITION);
		testSprite.look.setYInUserInterfaceDimensionUnit(LOOK_Y_POSITION);
		testSprite.look.setTransparencyInUserInterfaceDimensionUnit(LOOK_ALPHA);
		testSprite.look.setBrightnessInUserInterfaceDimensionUnit(LOOK_BRIGHTNESS);
		testSprite.look.setColorInUserInterfaceDimensionUnit(LOOK_COLOR);
		testSprite.look.setSizeInUserInterfaceDimensionUnit(LOOK_SCALE);
		testSprite.look.setDirectionInUserInterfaceDimensionUnit(LOOK_ROTATION);
	}

	public Double interpretSensor(Sensors sensor) throws InterpretationException {
		List<InternToken> internTokenList = new LinkedList<>();
		internTokenList.add(new InternToken(InternTokenType.SENSOR, sensor.name()));
		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();
		Formula sensorFormula = new Formula(parseTree);
		return sensorFormula.interpretDouble(testSprite);
	}

	@Test
	public void testLookSensorValues() throws InterpretationException {
		assertEquals(LOOK_X_POSITION, interpretSensor(Sensors.OBJECT_X), DELTA);
		assertEquals(LOOK_Y_POSITION, interpretSensor(Sensors.OBJECT_Y), DELTA);
		assertEquals(LOOK_ALPHA, interpretSensor(Sensors.OBJECT_TRANSPARENCY), DELTA);
		assertEquals(LOOK_BRIGHTNESS, interpretSensor(Sensors.OBJECT_BRIGHTNESS), DELTA);
		assertEquals(LOOK_COLOR, interpretSensor(Sensors.OBJECT_COLOR), DELTA);
		assertEquals(LOOK_SCALE, interpretSensor(Sensors.OBJECT_SIZE), DELTA);
		assertEquals(LOOK_ROTATION, interpretSensor(Sensors.OBJECT_ROTATION), DELTA);
		assertEquals(LOOK_LAYER, interpretSensor(Sensors.OBJECT_LAYER));
	}

	@Test
	public void testNotExistingLookSensorValues() {
		FormulaEditorTestUtil.testSingleTokenError(InternTokenType.SENSOR, "", 0);
		FormulaEditorTestUtil.testSingleTokenError(InternTokenType.SENSOR, "notExistingSensor O_O", 0);
	}
}
