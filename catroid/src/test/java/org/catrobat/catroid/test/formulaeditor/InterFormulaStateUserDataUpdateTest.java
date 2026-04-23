/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import org.catrobat.catroid.formulaeditor.InternFormulaState;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.Operators;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class InterFormulaStateUserDataUpdateTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{InternTokenType.USER_VARIABLE},
				{InternTokenType.USER_LIST}
		});
	}

	@Parameterized.Parameter
	public InternTokenType tokenType;

	private static final String DATA_A_NAME = "abcd";
	private static final String DATA_B_NAME = "xyz";

	private InternFormulaState initialState;
	private InternFormulaState expectedState;

	private InternToken dataA;
	private InternToken dataB;
	private static final InternToken MINUS = new InternToken(InternTokenType.OPERATOR,
			Operators.MINUS.toString());

	@Before
	public void setUp() {
		dataA = new InternToken(tokenType, DATA_A_NAME);
		dataB = new InternToken(tokenType, DATA_B_NAME);
	}

	@Test
	public void testRenameData() {
		List<InternToken> initialTokens = Arrays.asList(dataA);
		initialState = new InternFormulaState(initialTokens, null, 0);

		List<InternToken> expectedTokens = Arrays.asList(dataB);
		expectedState = new InternFormulaState(expectedTokens, null, 0);

		initialState.updateUserDataTokens(tokenType, DATA_A_NAME,
				DATA_B_NAME);

		assertThat(initialState, equalTo(expectedState));
	}

	@Test
	public void testRenameWithInvalidName() {
		List<InternToken> initialTokens = Arrays.asList(dataA);
		initialState = new InternFormulaState(initialTokens, null, 0);

		InternToken dataACopy = new InternToken(tokenType, DATA_A_NAME);
		List<InternToken> expectedTokens = Arrays.asList(dataACopy);
		expectedState = new InternFormulaState(expectedTokens, null, 0);

		initialState.updateUserDataTokens(tokenType, DATA_B_NAME,
				DATA_A_NAME);

		assertThat(initialState, equalTo(expectedState));
	}

	@Test
	public void testRenameWithDifferentDatasets() {
		List<InternToken> initialTokens = Arrays.asList(dataB, MINUS, dataA);
		initialState = new InternFormulaState(initialTokens, null, 0);

		List<InternToken> expectedTokens = Arrays.asList(dataB, MINUS, dataB);
		expectedState = new InternFormulaState(expectedTokens, null, 0);

		initialState.updateUserDataTokens(tokenType, DATA_A_NAME,
				DATA_B_NAME);

		assertThat(initialState, equalTo(expectedState));
	}

	@Test
	public void testRenameMultipleOccurrences() {
		List<InternToken> initialTokens = Arrays.asList(dataA, MINUS, dataA);
		initialState = new InternFormulaState(initialTokens, null, 0);

		List<InternToken> expectedTokens = Arrays.asList(dataB, MINUS, dataB);
		expectedState = new InternFormulaState(expectedTokens, null, 0);

		initialState.updateUserDataTokens(tokenType, DATA_A_NAME,
				DATA_B_NAME);

		assertThat(initialState, equalTo(expectedState));
	}

	@Test
	public void testRenameWithInvalidNameAndMultipleOccurrences() {
		List<InternToken> initialTokens = Arrays.asList(dataA, MINUS, dataA);
		initialState = new InternFormulaState(initialTokens, null, 0);

		InternToken dataACopy = new InternToken(tokenType, DATA_A_NAME);
		List<InternToken> expectedTokens = Arrays.asList(dataACopy, MINUS, dataACopy);
		expectedState = new InternFormulaState(expectedTokens, null, 0);

		initialState.updateUserDataTokens(tokenType, DATA_B_NAME,
				DATA_B_NAME);

		assertThat(initialState, equalTo(expectedState));
	}
}
