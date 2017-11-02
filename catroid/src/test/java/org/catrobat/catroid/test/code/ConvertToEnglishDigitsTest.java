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

package org.catrobat.catroid.test.code;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.stage.ShowTextActor.convertToEnglishDigits;

public class ConvertToEnglishDigitsTest {
	private static final int ZERO_TO_NINE = 1023456789;
	private static final String AR_NUMBER_ZERO_TO_NINE = "١٠٢٣٤٥٦٧٨٩";
	private static final String FA_NUMBER_ZERO_TO_NINE = "۱۰۲۳۴۵۶۷۸۹";
	private static final String HI_NUMBER_ZERO_TO_NINE = "१०२३४५६७८९";
	private static final String ASSAMESE_NUMBER_ZERO_TO_NINE = "১০২৩৪৫৬৭৮৯";
	private static final String GUJARATI_NUMBER_ZERO_TO_NINE = "૧૦૨૩૪૫૬૭૮૯";
	private static final String TAMIL_NUMBER_ZERO_TO_NINE = "௧௦௨௩௪௫௬௭௮௯";

	@Test
	public void testArabicDigits() throws Exception {
		// arabic
		assertEquals(ZERO_TO_NINE, Integer.parseInt(convertToEnglishDigits(AR_NUMBER_ZERO_TO_NINE)));
		assertEquals((double) ZERO_TO_NINE, Double.parseDouble(convertToEnglishDigits(AR_NUMBER_ZERO_TO_NINE)));

		// Farsi
		assertEquals(ZERO_TO_NINE, Integer.parseInt(convertToEnglishDigits(FA_NUMBER_ZERO_TO_NINE)));
		assertEquals((double) ZERO_TO_NINE, Double.parseDouble(convertToEnglishDigits(FA_NUMBER_ZERO_TO_NINE)));

		// Hindi
		assertEquals(ZERO_TO_NINE, Integer.parseInt(convertToEnglishDigits(HI_NUMBER_ZERO_TO_NINE)));
		assertEquals((double) ZERO_TO_NINE, Double.parseDouble(convertToEnglishDigits(HI_NUMBER_ZERO_TO_NINE)));

		// Assamese
		assertEquals(ZERO_TO_NINE, Integer.parseInt(convertToEnglishDigits(ASSAMESE_NUMBER_ZERO_TO_NINE)));
		assertEquals((double) ZERO_TO_NINE, Double.parseDouble(convertToEnglishDigits(ASSAMESE_NUMBER_ZERO_TO_NINE)));

		// Gujarati
		assertEquals(ZERO_TO_NINE, Integer.parseInt(convertToEnglishDigits(GUJARATI_NUMBER_ZERO_TO_NINE)));
		assertEquals((double) ZERO_TO_NINE, Double.parseDouble(convertToEnglishDigits(GUJARATI_NUMBER_ZERO_TO_NINE)));

		// Tamil
		assertEquals(ZERO_TO_NINE, Integer.parseInt(convertToEnglishDigits(TAMIL_NUMBER_ZERO_TO_NINE)));
		assertEquals((double) ZERO_TO_NINE, Double.parseDouble(convertToEnglishDigits(TAMIL_NUMBER_ZERO_TO_NINE)));
	}
}
