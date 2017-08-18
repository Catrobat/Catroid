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

import org.catrobat.catroid.formula.FormulaInterpreter;
import org.catrobat.catroid.formula.Token;
import org.catrobat.catroid.formula.dataprovider.SensorDataProvider;
import org.catrobat.catroid.formula.sensor.SensorListener;
import org.catrobat.catroid.formula.value.ValueToken.VariableToken;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import static junit.framework.Assert.assertEquals;

public class SensorListenerTest {

	private FormulaInterpreter interpreter = new FormulaInterpreter();

	@Test
	public void testAcceleration() {
		VariableToken variable = new VariableToken("x_acceleration", 0.0);

		SensorManager sensorManager = (SensorManager) InstrumentationRegistry
				.getTargetContext()
				.getSystemService(Context.SENSOR_SERVICE);

		SensorDataProvider dataProvider = new SensorDataProvider();
		dataProvider.add(variable, SensorListener.SensorType.X_ACCELERATION);

		SensorListener.INSTANCE.registerListeners(sensorManager);

		dataProvider.updateValues();
		assertEquals(0.0, interpreter.eval(new ArrayList<Token>(Arrays.asList(variable))).getValue());
	}

	@Test
	public void testData() {
		VariableToken year = new VariableToken("year", 0.0);
		VariableToken month = new VariableToken("month", 0.0);
		VariableToken day = new VariableToken("day", 0.0);

		SensorManager sensorManager = (SensorManager) InstrumentationRegistry
				.getTargetContext()
				.getSystemService(Context.SENSOR_SERVICE);

		SensorDataProvider dataProvider = new SensorDataProvider();
		dataProvider.add(year, SensorListener.SensorType.DATE_YEAR);
		dataProvider.add(month, SensorListener.SensorType.DATE_MONTH);
		dataProvider.add(day, SensorListener.SensorType.DATE_DAY);

		SensorListener.INSTANCE.registerListeners(sensorManager);

		dataProvider.updateValues();

		assertEquals((double) Calendar.getInstance().get(Calendar.YEAR),
				interpreter.eval(new ArrayList<Token>(Arrays.asList(year))).getValue());
		assertEquals((double) Calendar.getInstance().get(Calendar.MONTH),
				interpreter.eval(new ArrayList<Token>(Arrays.asList(month))).getValue());
		assertEquals((double) Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
				interpreter.eval(new ArrayList<Token>(Arrays.asList(day))).getValue());
	}
}
