/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.test.formulaeditor;

import android.test.AndroidTestCase;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.Sensors;

import java.util.LinkedList;
import java.util.List;

public class LookSensorValuesInterpretationTest extends AndroidTestCase {

	private static final float LOOK_ALPHA = 0.42f;
	private static final float LOOK_Y_POSITION = 23.4f;
	private static final float LOOK_X_POSITION = 5.6f;
	private static final float LOOK_BRIGHTNESS = 0.7f;
	private static final float LOOK_SCALE = 90.3f;
	private static final float LOOK_ROTATION = 30.7f;
	private static final float DELTA = 0.01f;
	private Sprite testSprite;

	@Override
	protected void setUp() {
		testSprite = new Sprite("sprite");
		testSprite.look.setXInUserInterfaceDimensionUnit(LOOK_X_POSITION);
		testSprite.look.setYInUserInterfaceDimensionUnit(LOOK_Y_POSITION);
		testSprite.look.setTransparencyInUserInterfaceDimensionUnit(LOOK_ALPHA);
		testSprite.look.setBrightnessInUserInterfaceDimensionUnit(LOOK_BRIGHTNESS);
		testSprite.look.setSizeInUserInterfaceDimensionUnit(LOOK_SCALE);
		testSprite.look.setDirectionInUserInterfaceDimensionUnit(LOOK_ROTATION);
	}

	public Formula getFormulaBySensor(Sensors sensor) {
		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.SENSOR, sensor.name()));
		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement parseTree = internParser.parseFormula();
		return new Formula(parseTree);
	}

	public void testLookSensorValues() {

		try {
			Formula lookXPositionFormula = getFormulaBySensor(Sensors.OBJECT_X);
			assertEquals("Formula interpretation is not as expected (x-Position)", LOOK_X_POSITION,
					lookXPositionFormula.interpretDouble(testSprite), DELTA);

			Formula lookYPositionFormula = getFormulaBySensor(Sensors.OBJECT_Y);
			assertEquals("Formula interpretation is not as expected (y-Position)", LOOK_Y_POSITION,
					lookYPositionFormula.interpretDouble(testSprite), DELTA);

			Formula lookAlphaValueFormula = getFormulaBySensor(Sensors.OBJECT_TRANSPARENCY);
			assertEquals("Formula interpretation is not as expected (ghosteffect)", LOOK_ALPHA,
					lookAlphaValueFormula.interpretDouble(testSprite), DELTA);

			Formula lookBrightnessFormula = getFormulaBySensor(Sensors.OBJECT_BRIGHTNESS);
			assertEquals("Formula interpretation is not as expected (brightness)", LOOK_BRIGHTNESS,
					lookBrightnessFormula.interpretDouble(testSprite), DELTA);

			Formula lookScaleFormula = getFormulaBySensor(Sensors.OBJECT_SIZE);
			assertEquals("Formula interpretation is not as expected (size)", LOOK_SCALE,
					lookScaleFormula.interpretDouble(testSprite), DELTA);

			Formula lookRotateFormula = getFormulaBySensor(Sensors.OBJECT_ROTATION);
			assertEquals("Formula interpretation is not as expected (rotation)", LOOK_ROTATION,
					lookRotateFormula.interpretDouble(testSprite), DELTA);

			Formula lookZPositionFormula = getFormulaBySensor(Sensors.OBJECT_LAYER);
			assertEquals("Formula interpretation is not as expected (z-index)", testSprite.look.getZIndex(),
					lookZPositionFormula.interpretInteger(testSprite).intValue());
		} catch (InterpretationException interpretationException) {
			fail("Could not interprete Formula.");
		}
	}

	public void testNotExistingLookSensorValues() {
		FormulaEditorTestUtil.testSingleTokenError(InternTokenType.SENSOR, "", 0);
		FormulaEditorTestUtil.testSingleTokenError(InternTokenType.SENSOR, "notExistingLookSensor", 0);
	}
}
