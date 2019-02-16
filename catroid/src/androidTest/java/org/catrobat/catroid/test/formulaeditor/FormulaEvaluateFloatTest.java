/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.Operators;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class FormulaEvaluateFloatTest {

	@Test
	public void testInterpretNotExisitingUnaryOperator() throws InterpretationException {

		FormulaElement formulaElement = new FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.PLUS.name(), null,
				new FormulaElement(FormulaElement.ElementType.NUMBER, "1.1", null),
				new FormulaElement(FormulaElement.ElementType.NUMBER, "0.1", null));

		Formula formula = new Formula(formulaElement);
		assertEquals("1.2", formula.getResultForComputeDialog(null));
	}

}
