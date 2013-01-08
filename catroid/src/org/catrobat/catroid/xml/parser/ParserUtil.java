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
package org.catrobat.catroid.xml.parser;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ParserUtil {
	public static String getElementXPath(Element element) {
		String path = "";

		try {
			for (; element != null; element = (Element) element.getParentNode()) {
				int idx = getElementIndex(element);
				String xname = element.getTagName().toString();

				if (idx > 1) {
					xname += "[" + idx + "]";
				}
				path = "/" + xname + path;
			}
		} catch (Exception ee) {
		}
		return path;
	}

	public static int getElementIndex(Element original) {
		int count = 1;

		for (Node node = original.getPreviousSibling(); node != null; node = node.getPreviousSibling()) {
			if (node instanceof Element) {
				Element element = (Element) node;
				if (element.getTagName().equals(original.getTagName())) {
					count++;
				}
			}
		}

		return count;
	}

}
