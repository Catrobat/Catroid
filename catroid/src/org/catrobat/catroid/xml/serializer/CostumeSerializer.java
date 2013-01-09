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
package org.catrobat.catroid.xml.serializer;

import static org.catrobat.catroid.xml.parser.CatroidXMLConstants.COSTUME_DATA_ELEMENT_NAME;
import static org.catrobat.catroid.xml.parser.CatroidXMLConstants.COSTUME_LIST_ELEMENT_NAME;
import static org.catrobat.catroid.xml.parser.CatroidXMLConstants.FILE_NAME;
import static org.catrobat.catroid.xml.parser.CatroidXMLConstants.NAME;

import java.util.ArrayList;
import java.util.List;

import org.catrobat.catroid.common.CostumeData;

public class CostumeSerializer extends Serializer {

	private final String costumeTabs = TAB + TAB + TAB;

	@Override
	public List<String> serialize(Object object) throws IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchFieldException {
		CostumeData costumedata = (CostumeData) object;
		String costumeFileName = costumedata.getCostumeFileName();
		String costumeName = costumedata.getCostumeName();
		List<String> costumeStringList = new ArrayList<String>();
		String xmlElementString = "";
		xmlElementString = costumeTabs + TAB + getStartTag(COSTUME_DATA_ELEMENT_NAME);
		costumeStringList.add(xmlElementString);
		xmlElementString = costumeTabs + TAB + TAB + getElementString(FILE_NAME, costumeFileName);
		costumeStringList.add(xmlElementString);
		xmlElementString = costumeTabs + TAB + TAB + getElementString(NAME, costumeName);
		costumeStringList.add(xmlElementString);
		xmlElementString = costumeTabs + TAB + getEndTag(COSTUME_DATA_ELEMENT_NAME);
		costumeStringList.add(xmlElementString);

		return costumeStringList;
	}

	public List<String> serializeCostumeList(List<CostumeData> costumeList) throws IllegalArgumentException,
			SecurityException, IllegalAccessException, NoSuchFieldException {
		List<String> costumeStrings = new ArrayList<String>();
		costumeStrings.add(costumeTabs + getStartTag(COSTUME_LIST_ELEMENT_NAME));
		for (CostumeData costumeData : costumeList) {
			costumeStrings.addAll(this.serialize(costumeData));
		}
		costumeStrings.add(costumeTabs + getEndTag(COSTUME_LIST_ELEMENT_NAME));
		return costumeStrings;
	}

}
