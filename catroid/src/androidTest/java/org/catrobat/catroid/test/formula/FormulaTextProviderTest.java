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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.formula.Token;
import org.catrobat.catroid.formula.operator.BinaryOperatorToken.AddOperatorToken;
import org.catrobat.catroid.formula.operator.BinaryOperatorToken.DivOperatorToken;
import org.catrobat.catroid.formula.operator.BinaryOperatorToken.MultOperatorToken;
import org.catrobat.catroid.formula.operator.BinaryOperatorToken.SubOperatorToken;
import org.catrobat.catroid.formula.stringprovider.FormulaTextProvider;
import org.catrobat.catroid.formula.value.ValueToken;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class FormulaTextProviderTest {

	private MultOperatorToken mult = new MultOperatorToken();
	private DivOperatorToken div = new DivOperatorToken();
	private AddOperatorToken add = new AddOperatorToken();
	private SubOperatorToken sub = new SubOperatorToken();

	@Test
	public void testMathOperators() {
		List<Token> tokens = new ArrayList<>();
		tokens.add(new ValueToken(1));
		tokens.add(mult);
		tokens.add(new ValueToken(2));
		tokens.add(add);
		tokens.add(new ValueToken(3));
		tokens.add(div);
		tokens.add(new ValueToken(4));
		tokens.add(sub);
		tokens.add(new ValueToken(5));

		FormulaTextProvider formulaTextProvider = new FormulaTextProvider(InstrumentationRegistry.getTargetContext().getResources());
		assertEquals(formulaTextProvider.getText(tokens), "1 × 2 + 3 ÷ 4 - 5");
	}
}
