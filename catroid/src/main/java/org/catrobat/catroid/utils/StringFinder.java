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

package org.catrobat.catroid.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringFinder {

	private boolean matcherRun;
	private String result;

	public static String encodeSpecialChars(String string) {
		return Pattern.quote(string);
	}

	public boolean findBetween(String string, String start, String end) {
		Pattern pattern = Pattern.compile(start + "(.*?)" + end, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(string);

		matcherRun = true;

		if (matcher.find()) {
			result = matcher.group(1);
			return true;
		}

		result = null;
		return false;
	}

	public String getResult() {
		if (!matcherRun) {
			throw new IllegalStateException("You must call findBetween(String string, String start, String end) "
					+ "first.");
		}
		matcherRun = false;
		return result;
	}
}
