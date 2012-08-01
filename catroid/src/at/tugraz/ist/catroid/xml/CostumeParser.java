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
package at.tugraz.ist.catroid.xml;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;

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
				Node costumeFileNameNode = costumeElement.getElementsByTagName("fileName").item(0);
				if (costumeFileNameNode != null) {
					costumeFileName = costumeFileNameNode.getChildNodes().item(0).getNodeValue();
				}
				String costumeName = costumeElement.getElementsByTagName("name").item(0).getChildNodes().item(0)
						.getNodeValue();
				foundCostumeData = new CostumeData();
				foundCostumeData.setCostumeFilename(costumeFileName);
				foundCostumeData.setCostumeName(costumeName);
				costumeList.add(foundCostumeData);
				String costumeindexString = "";
				if (costumeIndex > 1) {
					costumeindexString = "[" + costumeIndex + "]";
				}
				referencedObjects.put("CostumeData" + costumeindexString, foundCostumeData);
				costumeIndex++;
			}
		}
		Field costumeListField = sprite.getClass().getDeclaredField("costumeDataList");
		objectGetter.setFieldOfObject(costumeListField, sprite, costumeList);

	}

	public Boolean setCostumedataOfBrick(Brick brickObject, Field valueField, String referenceAttribute,
			Map<String, Object> referencedObjects, List<ForwardReferences> forwardRefs) throws IllegalAccessException {
		int lastIndex = referenceAttribute.lastIndexOf('[');
		String query = "CostumeData";
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
