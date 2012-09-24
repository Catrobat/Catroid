/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.xml.serializer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.xml.parser.CatroidXMLConstants;
import at.tugraz.ist.catroid.xml.parser.ObjectCreator;

public abstract class Serializer {
	ObjectCreator objectCreator;
	Map<String, Field> fieldMap;
	Project serializedProject;
	Sprite serializedSprite;
	Script serializedScript;
	List<Brick> brickList;
	List<String> referenceStrings;
	List<CostumeData> costumeList;
	List<Sprite> spriteList;
	List<SoundInfo> soundList;

	public final String spriteElementPrefix = "<sprite reference=";
	public final String tab = "\t";

	public abstract List<String> serialize(Object object) throws IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchFieldException, SerializeException;

	public String getReference(Field fieldNeedingReference, Object objectWIthField) throws IllegalArgumentException,
			IllegalAccessException {
		// TODO: reference = "tODO ...."
		String reference = "";
		Object referencedObject = fieldNeedingReference.get(objectWIthField);
		if (referencedObject != null) {
			String referencedObjectName = referencedObject.getClass().getSimpleName();
			if (objectWIthField.getClass().getSimpleName().endsWith(CatroidXMLConstants.BRICK_CLASS_SUFFIX)) {
				if (referencedObjectName.endsWith(CatroidXMLConstants.BRICK_CLASS_SUFFIX)) {
					if (brickList.contains(referencedObject)) {
						reference = "../../" + referencedObjectName;

						List<Brick> sameBrickList = new ArrayList<Brick>();
						for (int i = 0; i < brickList.size(); i++) {
							if (brickList.get(i).getClass().getSimpleName().equals(referencedObjectName)) {

								sameBrickList.add(brickList.get(i));
							}
						}

						if (sameBrickList.size() > 1) {
							reference = getReferenceIndexSuffix(reference, referencedObject, sameBrickList);
						}
					} else {
						reference = "TODO for bricks of other scripts";
					}
				} else if (referencedObjectName.equals(CatroidXMLConstants.COSTUME_DATA_CLASS_NAME)) {
					reference = CatroidXMLConstants.COSTUMEREFERENCE_FROM_BRICK;
					reference = getReferenceIndexSuffix(reference, referencedObject, costumeList);
				} else if (referencedObjectName.equals(CatroidXMLConstants.SPRITE_ELEMENT_NAME)) {
					reference = CatroidXMLConstants.SPRITEREFERENCE_FROM_BRICK;
					reference = getReferenceIndexSuffix(reference, referencedObject, spriteList);
				} else if (referencedObjectName.equals(CatroidXMLConstants.SOUND_INFO_CLASS_NAME)) {
					reference = CatroidXMLConstants.SOUNDREFERENCE_FROM_BRICK;
					reference = getReferenceIndexSuffix(reference, referencedObject, soundList);
				} else if (referencedObjectName.endsWith("Script")) {
					reference = "../../../../" + referencedObjectName;
					List<Script> sameScripts = new ArrayList<Script>();
					for (int i = 0; i < serializedSprite.getNumberOfScripts(); i++) {
						if (serializedSprite.getScript(i).getClass().getSimpleName().equals(referencedObjectName)) {
							sameScripts.add(serializedSprite.getScript(i));
						}
					}
					if (sameScripts.size() > 1) {
						reference = getReferenceIndexSuffix(reference, referencedObject, sameScripts);
					}
				}
			} else if (objectWIthField.getClass().getSimpleName().endsWith(CatroidXMLConstants.SCRIPTCLASS_SUFFIX)) {
				reference = "TODO for scripts";
			}
		}
		return reference;

	}

	private String getReferenceIndexSuffix(String reference, Object referencedObject, List<?> sameTypeList) {
		int index = 0;
		for (int j = 0; j < sameTypeList.size(); j++) {
			if (referencedObject.equals(sameTypeList.get(j))) {
				index = j + 1;
				break;
			}
		}
		if (index > 1) {
			reference = reference + "[" + index + "]";
		}
		return reference;
	}

	public String getElementString(String elementName, String value) {
		if (value != null) {
			if (value.equals("")) {
				return getEmptyTag(elementName);
			} else {
				return "<" + elementName + ">" + value + "</" + elementName + ">\n";
			}
		} else {
			return getEmptyTag(elementName);
		}
	}

	public String getStartTag(String tagName) {
		return "<" + tagName + ">\n";
	}

	public String getEndTag(String tagName) {
		return "</" + tagName + ">\n";
	}

	public String getEmptyTag(String tagName) {
		return "<" + tagName + "/>\n";
	}
}
