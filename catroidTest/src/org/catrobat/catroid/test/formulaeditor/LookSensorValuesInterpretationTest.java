/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.formulaeditor;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Sensors;

import android.test.AndroidTestCase;

public class LookSensorValuesInterpretationTest extends AndroidTestCase {

	private static final float LOOK_ALPHA = 0.5f;
	private static final float LOOK_Y_POSITION = 23.4f;
	private static final float LOOK_X_POSITION = 5.6f;
	private static final float LOOK_BRIGHTNESS = 0.7f;
	private static final float LOOK_SCALE = 90.3f;
	private static final float LOOK_ROTATION = 30.7f;
	private static final int LOOK_ZPOSITION = 3;
	private static final float DELTA = 0.01f;
	private Sprite testSprite;

	@Override
	protected void setUp() {
		testSprite = new Sprite("sprite");
		testSprite.look.setXPosition(LOOK_X_POSITION);
		testSprite.look.setYPosition(LOOK_Y_POSITION);
		testSprite.look.setAlphaValue(LOOK_ALPHA);
		testSprite.look.setBrightnessValue(LOOK_BRIGHTNESS);
		testSprite.look.setScaleX(LOOK_SCALE);
		testSprite.look.setScaleY(LOOK_SCALE);
		testSprite.look.setRotation(LOOK_ROTATION);
		testSprite.look.setZIndex(LOOK_ZPOSITION);
	}

	public void testLookSensorValues() {

		Formula lookXPositionFormula = new Formula(new FormulaElement(ElementType.SENSOR, Sensors.LOOK_X_.sensorName,
				null));
		assertEquals("Formula interpretation is not as expected", LOOK_X_POSITION,
				lookXPositionFormula.interpretFloat(testSprite), DELTA);

		Formula lookYPositionFormula = new Formula(new FormulaElement(ElementType.SENSOR, Sensors.LOOK_Y_.sensorName,
				null));
		assertEquals("Formula interpretation is not as expected", LOOK_Y_POSITION,
				lookYPositionFormula.interpretFloat(testSprite), DELTA);

		Formula lookAlphaValueFormula = new Formula(new FormulaElement(ElementType.SENSOR,
				Sensors.LOOK_GHOSTEFFECT_.sensorName, null));
		assertEquals("Formula interpretation is not as expected", LOOK_ALPHA,
				lookAlphaValueFormula.interpretFloat(testSprite), DELTA);

		Formula lookBrightnessFormula = new Formula(new FormulaElement(ElementType.SENSOR,
				Sensors.LOOK_BRIGHTNESS_.sensorName, null));
		assertEquals("Formula interpretation is not as expected", LOOK_BRIGHTNESS,
				lookBrightnessFormula.interpretFloat(testSprite), DELTA);

		Formula lookScaleFormula = new Formula(new FormulaElement(ElementType.SENSOR, Sensors.LOOK_SIZE_.sensorName,
				null));
		assertEquals("Formula interpretation is not as expected", LOOK_SCALE,
				lookScaleFormula.interpretFloat(testSprite), DELTA);

		Formula lookRotateFormula = new Formula(new FormulaElement(ElementType.SENSOR,
				Sensors.LOOK_ROTATION_.sensorName, null));
		assertEquals("Formula interpretation is not as expected", LOOK_ROTATION,
				lookRotateFormula.interpretFloat(testSprite), DELTA);

		Formula lookZPositionFormula = new Formula(new FormulaElement(ElementType.SENSOR,
				Sensors.LOOK_LAYER_.sensorName, null));
		assertEquals("Formula interpretation is not as expected", LOOK_ZPOSITION,
				lookZPositionFormula.interpretInteger(testSprite));

	}
}
