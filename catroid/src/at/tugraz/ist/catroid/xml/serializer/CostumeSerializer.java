/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.xml.serializer;

import java.util.ArrayList;
import java.util.List;

import at.tugraz.ist.catroid.common.CostumeData;

public class CostumeSerializer extends Serializer {

	private final String costumeDataTag = "Common.CostumeData";
	private final String costumeFileNameTag = "fileName";
	private final String costumeNameTag = "name";
	private final String costumeListTag = "costumeDataList";

	@Override
	public List<String> serialize(Object object) throws IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchFieldException {
		CostumeData costumedata = (CostumeData) object;
		String costumeFileName = costumedata.getCostumeFileName();
		String costumeName = costumedata.getCostumeName();
		List<String> costumeStringList = new ArrayList<String>();
		String xmlElementString = "";
		xmlElementString = getStartTag(costumeDataTag);
		costumeStringList.add(xmlElementString);
		xmlElementString = getElementString(costumeFileNameTag, costumeFileName);
		costumeStringList.add(xmlElementString);
		xmlElementString = getElementString(costumeNameTag, costumeName);
		costumeStringList.add(xmlElementString);
		xmlElementString = getEndTag(costumeDataTag);
		costumeStringList.add(xmlElementString);

		return costumeStringList;
	}

	public List<String> serializeCostumeList(List<CostumeData> costumeList) throws IllegalArgumentException,
			SecurityException, IllegalAccessException, NoSuchFieldException {
		List<String> costumeStrings = new ArrayList<String>();
		costumeStrings.add(getStartTag(costumeListTag));
		for (CostumeData costumeData : costumeList) {
			costumeStrings.addAll(this.serialize(costumeData));
		}
		costumeStrings.add(getEndTag(costumeListTag));
		return costumeStrings;
	}

}
