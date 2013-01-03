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

import static org.catrobat.catroid.xml.parser.CatroidXMLConstants.BRICK_LIST_ELEMENT_NAME;
import static org.catrobat.catroid.xml.parser.CatroidXMLConstants.SPRITE;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.xml.parser.ObjectCreator;

public class BrickSerializer extends Serializer {
	private final String brickTabs = TAB + TAB + TAB + TAB + TAB;

	public BrickSerializer(Sprite serializedSprite, Script serializedScript, Project serializedProject) {
		super.objectCreator = new ObjectCreator();
		super.fieldMap = new HashMap<String, Field>();
		super.serializedSprite = serializedSprite;
		super.serializedScript = serializedScript;
		super.brickList = serializedScript.getBrickList();
		super.referenceStrings = new ArrayList<String>();
		super.costumeList = serializedSprite.getCostumeDataList();
		super.spriteList = serializedProject.getSpriteList();
		super.soundList = serializedSprite.getSoundList();
	}

	@Override
	public List<String> serialize(Object object) throws IllegalAccessException {

		List<String> brickStringList = new ArrayList<String>();
		String xmlElementString = "";

		xmlElementString = brickTabs + TAB + getStartTag(object.getClass().getSimpleName());
		brickStringList.add(xmlElementString);

		setBrickfieldsAsElements(object, brickStringList, object.getClass().getSuperclass());
		setBrickfieldsAsElements(object, brickStringList, object.getClass());

		xmlElementString = brickTabs + TAB + getEndTag(object.getClass().getSimpleName());
		brickStringList.add(xmlElementString);

		if (brickStringList.size() <= 2) {
			brickStringList.clear();
			brickStringList.add(brickTabs + TAB + getEmptyTag(object.getClass().getSimpleName()));
		}
		return brickStringList;
	}

	private void setBrickfieldsAsElements(Object object, List<String> brickStringList, Class<?> clazz)
			throws IllegalAccessException {
		String xmlElementString;
		fieldMap = objectCreator.getFieldMapOfThisClass(clazz);
		Collection<Field> fields = fieldMap.values();
		for (Field brickClassField : fields) {
			String fieldName = objectCreator.extractTagName(brickClassField);
			brickClassField.setAccessible(true);
			if (!brickClassField.getType().isPrimitive()) {
				if (fieldName.equals(SPRITE)) {
					// sprites are not serialized
				} else if (brickClassField.getType().equals(String.class)) {
					xmlElementString = brickTabs + TAB + TAB
							+ getElementString(fieldName, (String) brickClassField.get(object));
					brickStringList.add(xmlElementString);
				} else if (!fieldName.equals(SPRITE)) {
					String referenceString = getReference(brickClassField, object);
					xmlElementString = brickTabs + TAB + TAB + "<" + fieldName + " reference=\"" + referenceString
							+ "\"/>" + "\n";
					brickStringList.add(xmlElementString);
				}
			} else {
				xmlElementString = brickTabs + TAB + TAB
						+ getElementString(fieldName, brickClassField.get(object).toString());
				brickStringList.add(xmlElementString);
			}
		}
	}

	public List<String> serializeBrickList(List<Brick> brickList) throws IllegalArgumentException,
			IllegalAccessException {
		List<String> brickStrings = new ArrayList<String>();
		brickStrings.add(brickTabs + getStartTag(BRICK_LIST_ELEMENT_NAME));
		for (Object brickObject : brickList) {
			brickStrings.addAll(serialize(brickObject));
		}
		brickStrings.add(brickTabs + getEndTag(BRICK_LIST_ELEMENT_NAME));
		return brickStrings;
	}
}
