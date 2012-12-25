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
import org.catrobat.catroid.xml.parser.CatroidXMLConstants;
import org.catrobat.catroid.xml.parser.ObjectCreator;

public class ProjectSerializer extends Serializer {

	public ProjectSerializer() {
		objectCreator = new ObjectCreator();
	}

	@Override
	public List<String> serialize(Object object) throws IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchFieldException, SerializeException {
		Project project = (Project) object;
		List<String> projectStringList = new ArrayList<String>();
		String xmlElementString = "";
		if (!(object.getClass().getSuperclass().equals(Object.class))) {
			String className = object.getClass().getName();
			className = className.replace("$", "_-");
			xmlElementString = getStartTag(className);
		} else {
			xmlElementString = getStartTag(CatroidXMLConstants.PROJECT_ELEMENT_NAME
					+ CatroidXMLConstants.PROJECT_ELEMENT_NAME_OPTIONAL_STARTTAG);
		}

		projectStringList.add(xmlElementString);
		xmlElementString = getStartTag(CatroidXMLConstants.PROJECT_HEADER_NAME);
		projectStringList.add(xmlElementString);
		if (!(object.getClass().getSuperclass().equals(Object.class))) {
			getProjectStringsofClass(object, project, projectStringList, object.getClass().getSuperclass());
		}

		getProjectStringsofClass(object, project, projectStringList, object.getClass());
		xmlElementString = getEndTag(CatroidXMLConstants.PROJECT_HEADER_NAME);
		projectStringList.add(xmlElementString);
		SpriteSerializer spriteSerializer = new SpriteSerializer(project);
		projectStringList.addAll(spriteSerializer.serializeList());
		if (!(object.getClass().getSuperclass().equals(Object.class))) {
			String className = object.getClass().getName();
			className = className.replace("$", "_-");
			xmlElementString = getEndTag(className);
		} else {
			xmlElementString = getEndTag(CatroidXMLConstants.PROJECT_ELEMENT_NAME);
		}

		projectStringList.add(xmlElementString);
		return projectStringList;
	}

	private void getProjectStringsofClass(Object object, Project project, List<String> projectStringList,
			Class<?> classOfObject) throws IllegalAccessException, NoSuchFieldException, SerializeException {
		String xmlElementString;
		fieldMap = objectCreator.getFieldMapOfThisClass(classOfObject);
		Collection<Field> fields = fieldMap.values();
		for (Field projectField : fields) {
			String fieldName = objectCreator.extractTagName(projectField);
			projectField.setAccessible(true);
			Object fieldValue = projectField.get(object);
			if (!projectField.getType().isPrimitive()) {
				if (projectField.getType().equals(String.class)) {
					xmlElementString = TAB + getElementString(fieldName, (String) fieldValue);
					projectStringList.add(xmlElementString);
				} else if (projectField.getName().equals("spriteList")) {
					//						SpriteSerializer spriteSerializer = new SpriteSerializer(project);
					//						projectStringList.addAll(spriteSerializer.serializeList());
				} else {
					throw new SerializeException("unknown field found in Project class");
				}
			} else {
				xmlElementString = TAB + getElementString(fieldName, fieldValue.toString());
				projectStringList.add(xmlElementString);
			}
		}
	}

}
