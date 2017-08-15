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

package org.catrobat.catroid.test.formula;

import android.content.Context;
import android.hardware.SensorManager;
import android.support.test.InstrumentationRegistry;

import org.catrobat.catroid.formula.Formula;
import org.catrobat.catroid.formula.FormulaInterpreter;
import org.catrobat.catroid.formula.SensorDataProvider;
import org.catrobat.catroid.formula.sensor.SensorListener;
import org.catrobat.catroid.formula.Token;
import org.catrobat.catroid.formula.value.ValueToken;
import org.catrobat.catroid.formula.value.ValueToken.NumericValueToken.NumericVariableToken;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class SensorListenerTest {

	private void testNumericFormula(Formula formula, double expectedResult, String expectedString) {
		FormulaInterpreter<ValueToken.NumericValueToken> interpreter = new FormulaInterpreter<>();
		assertEquals(expectedString, formula.getDisplayText());
		assertEquals(expectedResult, interpreter.eval(formula.getTokens()).getValue());
	}

	private void testNumericVariable(NumericVariableToken variable, double expectedResult, String expectedString) {

		List<Token> tokens = new ArrayList<>();
		tokens.add(variable);
		Formula formula = new Formula(tokens);

		testNumericFormula(formula, expectedResult, expectedString);
	}

	@Test
	public void testAcceleration() {
		NumericVariableToken variable = new NumericVariableToken("x_acceleration", 0.0);

		SensorManager sensorManager = (SensorManager) InstrumentationRegistry
				.getTargetContext()
				.getSystemService(Context
				.SENSOR_SERVICE);

		SensorDataProvider dataProvider = new SensorDataProvider();
		dataProvider.add(variable, SensorListener.SensorType.X_ACCELERATION);

		SensorListener.INSTANCE.registerListeners(sensorManager);

		dataProvider.updateValues();
		testNumericVariable(variable, 0.0, "x_acceleration");
	}

	@Test
	public void testData() {
		NumericVariableToken year = new NumericVariableToken("year", 0.0);
		NumericVariableToken month = new NumericVariableToken("month", 0.0);
		NumericVariableToken day = new NumericVariableToken("day", 0.0);


		SensorManager sensorManager = (SensorManager) InstrumentationRegistry
				.getTargetContext()
				.getSystemService(Context
						.SENSOR_SERVICE);

		SensorDataProvider dataProvider = new SensorDataProvider();
		dataProvider.add(year, SensorListener.SensorType.DATE_YEAR);
		dataProvider.add(month, SensorListener.SensorType.DATE_MONTH);
		dataProvider.add(day, SensorListener.SensorType.DATE_DAY);

		SensorListener.INSTANCE.registerListeners(sensorManager);

		dataProvider.updateValues();

		testNumericVariable(year, Calendar.getInstance().get(Calendar.YEAR), "year");
		testNumericVariable(month, Calendar.getInstance().get(Calendar.MONTH), "month");
		testNumericVariable(day, Calendar.getInstance().get(Calendar.DAY_OF_MONTH), "day");
	}
}
