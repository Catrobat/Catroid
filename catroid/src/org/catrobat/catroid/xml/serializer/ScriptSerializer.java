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
package org.catrobat.catroid.xml.serializer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.xml.parser.CatroidXMLConstants;
import org.catrobat.catroid.xml.parser.ObjectCreator;

public class ScriptSerializer extends Serializer {

	private final String scriptTabs = TAB + TAB + TAB + TAB;

	public ScriptSerializer(Sprite serializedSprite, Project serializedProject) {
		super.serializedSprite = serializedSprite;
		super.serializedProject = serializedProject;
		objectCreator = new ObjectCreator();
	}

	@Override
	public List<String> serialize(Object object) throws IllegalArgumentException, IllegalAccessException {

		List<String> scriptStringList = new ArrayList<String>();
		serializedScript = (Script) object;
		String xmlElementString = "";
		xmlElementString = scriptTabs + getStartTag(object.getClass().getSimpleName());
		scriptStringList.add(xmlElementString);

		//		if (!(object.getClass().getSuperclass().equals(Object.class))) {
		//			getScriptFieldsAsElements(object, scriptStringList, object.getClass().getSuperclass());
		//		}
		getScriptFieldsAsElements(object, scriptStringList, object.getClass());

		xmlElementString = scriptTabs + getEndTag(object.getClass().getSimpleName());
		scriptStringList.add(xmlElementString);
		if (scriptStringList.size() <= 2) {
			scriptStringList.clear();
			scriptStringList.add(scriptTabs + getEmptyTag(object.getClass().getSimpleName()));
		}
		return scriptStringList;
	}

	private void getScriptFieldsAsElements(Object object, List<String> scriptStringList, Class<?> clazz)
			throws IllegalAccessException {
		String xmlElementString;
		//		fieldMap = objectCreator.getFieldMapOfThisClass(cls);
		fieldMap = objectCreator.getFieldMap(clazz);
		Collection<Field> fields = fieldMap.values();
		for (Field scriptClassField : fields) {
			String fieldName = objectCreator.extractTagName(scriptClassField);
			scriptClassField.setAccessible(true);
			if (!scriptClassField.getType().isPrimitive()) {
				if (fieldName.equals("Sprite")) {
					// sprites are not serialized
				} else if (fieldName.equals(CatroidXMLConstants.BRICK_LIST_ELEMENT_NAME)) {
					if (serializedScript.getBrickList().size() > 0) {
						BrickSerializer brickSerializer = new BrickSerializer(serializedSprite, (Script) object,
								serializedProject);
						List<String> brickStrings = brickSerializer.serializeBrickList(serializedScript.getBrickList());
						scriptStringList.addAll(brickStrings);
					}
				} else if (scriptClassField.getType().equals(String.class)) {
					xmlElementString = scriptTabs + TAB
							+ getElementString(fieldName, (String) scriptClassField.get(object));
					scriptStringList.add(xmlElementString);
				} else {
					String referenceString = getReference(scriptClassField, object);
					xmlElementString = scriptTabs + TAB + "<" + fieldName + " reference=\"" + referenceString + "\"/>"
							+ "\n";
					scriptStringList.add(xmlElementString);
				}
			} else {
				xmlElementString = getElementString(fieldName, scriptClassField.get(object).toString());
				scriptStringList.add(xmlElementString);
			}
		}
	}
}
