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
package org.catrobat.catroid.test.xml;

import java.io.IOException;
import java.io.InputStream;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.xml.parser.FullParser;
import org.catrobat.catroid.xml.parser.ParseException;

import android.content.Context;

class XmlTestUtils {

	public static Project loadProjectFromAssets(String xmlFile, Context context) throws ParseException {

		Project parsedProject = null;
		try {
			InputStream inputStreamForSprites = context.getAssets().open(xmlFile);
			parsedProject = FullParser.parseSpritesWithProject(inputStreamForSprites);
			inputStreamForSprites.close();
			inputStreamForSprites = null;
		} catch (ParseException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw new ParseException("IO exception in full parser", e);
		}
		return parsedProject;
	}
}
