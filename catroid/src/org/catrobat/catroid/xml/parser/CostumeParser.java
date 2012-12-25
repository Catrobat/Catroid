/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.catrobat.catroid.common.CostumeData;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CostumeParser {
	ObjectCreator objectGetter = new ObjectCreator();
	List<CostumeData> costumeList;
	References reference = new References();

	public void parseCostumeList(NodeList costumeNodes, Sprite sprite, Map<String, Object> referencedObjects)
			throws SecurityException, NoSuchFieldException, IllegalAccessException {

		costumeList = new ArrayList<CostumeData>();
		int costumeIndex = 1;
		for (int m = 0; m < costumeNodes.getLength(); m++) {
			CostumeData foundCostumeData = null;
			if (costumeNodes.item(m).getNodeType() != Node.TEXT_NODE) {

				Element costumeElement = (Element) costumeNodes.item(m);
				String costumeFileName = null;
				Node costumeFileNameNode = costumeElement.getElementsByTagName(CatroidXMLConstants.FILE_NAME).item(0);
				if (costumeFileNameNode != null) {
					costumeFileName = costumeFileNameNode.getChildNodes().item(0).getNodeValue();
				}
				String costumeName = costumeElement.getElementsByTagName(CatroidXMLConstants.NAME).item(0)
						.getChildNodes().item(0).getNodeValue();
				foundCostumeData = new CostumeData();
				foundCostumeData.setCostumeFilename(costumeFileName);
				foundCostumeData.setCostumeName(costumeName);
				costumeList.add(foundCostumeData);
				String costumeindexString = "";
				if (costumeIndex > 1) {
					costumeindexString = "[" + costumeIndex + "]";
				}
				referencedObjects.put(CatroidXMLConstants.COSTUME_DATA_ELEMENT_NAME + costumeindexString,
						foundCostumeData);
				costumeIndex++;
			}
		}
		Field costumeListField = sprite.getClass().getDeclaredField(CatroidXMLConstants.COSTUME_LIST_FIELD_NAME);
		objectGetter.setFieldOfObject(costumeListField, sprite, costumeList);
	}

	public Boolean setCostumeDataOfBrick(Brick brickObject, Field valueField, String referenceAttribute,
			Map<String, Object> referencedObjects) throws IllegalAccessException {
		int lastIndex = referenceAttribute.lastIndexOf('[');
		String query = CatroidXMLConstants.COSTUME_DATA_ELEMENT_NAME;
		String suffix = "";
		if (lastIndex != -1) {
			char referenceNo = referenceAttribute.charAt(referenceAttribute.lastIndexOf('[') + 1);
			suffix = "[" + referenceNo + "]";
		}
		CostumeData referencedCostume = (CostumeData) referencedObjects.get(query + suffix);
		if (referencedCostume == null) {
			return false;
		} else {
			valueField.set(brickObject, referencedCostume);
		}
		return true;
	}

}
