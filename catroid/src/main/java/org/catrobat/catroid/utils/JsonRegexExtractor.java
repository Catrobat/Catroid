/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

import android.content.Context;

public class JsonRegexExtractor {
	private Context context;
	public JsonRegexExtractor(Context context) {
		this.context = context;
	}
	public static String getJsonParserRegex(String keyword) {
		return "(?x) # with comments & spaces ignored\n"
			+ "\"" + keyword + "\" \\s* : \\s* \"? # find keyword\n"
			+ "( # start capturing, see https://www.json.org/json-en.html :\n"
			+ "(?<=\") (\\\\\"|[^\"])* (?=\") # text\n"
			+ "| ((?<!\") # other cases:\n"
			+ "  [+-]?(0|[1-9]\\d*)(\\.\\d+)?([eE][+-]?\\d+)? # numbers\n"
			+ "| ( true | false | null ) # logical values\n"
			+ "| \\{ [^{]* \\} # un-nested object\n"
			+ "| \\[ [^\\[]* \\] # un-nested array\n"
			+ "(?!\") ) # end of other cases\n"
			+ ")\"? # end of captured return value\n" 
			+ "(?=\\s*[,\\]}]) # correct ending of json";
	}
}
