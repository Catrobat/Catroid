/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

package org.catrobat.catroid.formulaeditor.function;

import org.catrobat.catroid.formulaeditor.Functions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class MathFunctionProviderTest {

	private static final double EPSILON = 1e-9;
	private FormulaFunction modFunction;

	@Before
	public void setUp() {
		MathFunctionProvider mathFunctionProvider = new MathFunctionProvider();
		Map<Functions, FormulaFunction> formulaFunctions = new EnumMap<>(Functions.class);
		mathFunctionProvider.addFunctionsToMap(formulaFunctions);
		modFunction = formulaFunctions.get(Functions.MOD);
	}

	private double evaluateMod(double dividend, double divisor) {
		return ((Number) modFunction.execute(dividend, divisor)).doubleValue();
	}

	@Test
	public void testModPositiveDividendPositiveDivisor() {
		assertEquals(2.0, evaluateMod(5.0, 3.0), EPSILON);
		assertEquals(0.0, evaluateMod(6.0, 3.0), EPSILON);
		assertEquals(1.0, evaluateMod(7.0, 3.0), EPSILON);
	}

	@Test
	public void testModWithZero() {
		assertEquals(0.0, evaluateMod(0.0, 5.0), EPSILON);
		assertEquals(5.0, evaluateMod(5.0, 0.0), EPSILON);
		assertEquals(0.0, evaluateMod(0.0, 0.0), EPSILON);
	}

	@Test
	public void testModNegativeDividendPositiveDivisor() {
		assertEquals(2.0, evaluateMod(-1.0, 3.0), EPSILON);
		assertEquals(1.0, evaluateMod(-2.0, 3.0), EPSILON);
		assertEquals(0.0, evaluateMod(-3.0, 3.0), EPSILON);
		assertEquals(1.0, evaluateMod(-5.0, 3.0), EPSILON);
		assertEquals(0.0, evaluateMod(-6.0, 3.0), EPSILON);
	}

	@Test
	public void testModPositiveDividendNegativeDivisor() {
		assertEquals(-1.0, evaluateMod(5.0, -3.0), EPSILON);
		assertEquals(0.0, evaluateMod(6.0, -3.0), EPSILON);
		assertEquals(-2.0, evaluateMod(7.0, -3.0), EPSILON);
	}

	@Test
	public void testModNegativeDividendNegativeDivisor() {
		assertEquals(-2.0, evaluateMod(-5.0, -3.0), EPSILON);
		assertEquals(0.0, evaluateMod(-6.0, -3.0), EPSILON);
		assertEquals(-1.0, evaluateMod(-7.0, -3.0), EPSILON);
	}

	@Test
	public void testModWithDecimals() {
		assertEquals(1.5, evaluateMod(5.5, 2.0), EPSILON);
		assertEquals(0.5, evaluateMod(-5.5, 2.0), EPSILON);
		assertEquals(-0.5, evaluateMod(5.5, -2.0), EPSILON);
		assertEquals(-1.5, evaluateMod(-5.5, -2.0), EPSILON);
	}

	@Test(timeout = 1000)
	public void testModLargeNegativeDividendDoesNotANR() {
		// A negative dividend with large magnitude should evaluate in O(1) time
		// without timing out or looping indefinitely.
		assertEquals(0.0, evaluateMod(-100_000_000.0, 1.0), EPSILON);
		assertEquals(0.5, evaluateMod(-100_000_000.5, 1.0), EPSILON);
		assertEquals(2.0, evaluateMod(-100_000_000.0, 3.0), EPSILON);
		assertEquals(0.0, evaluateMod(-1_000_000_002.0, 3.0), EPSILON);
		assertEquals(1.0, evaluateMod(-1_000_000_001.0, 3.0), EPSILON);
		assertEquals(0.0, evaluateMod(-1_000_000_000_000.0, 1.0), EPSILON);
	}
}
