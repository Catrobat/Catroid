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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.xml.ObjectCreator;

public class ProjectSerializer extends Serializer {

	private final String projectTag = "Project";

	public ProjectSerializer() {
		objectCreator = new ObjectCreator();
	}

	@Override
	public List<String> serialize(Object object) throws IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchFieldException {
		Project project = (Project) object;
		fieldMap = objectCreator.getFieldMap(project.getClass());
		List<String> projectStringList = new ArrayList<String>();
		String xmlElementString = "";

		xmlElementString = getStartTag(projectTag);
		projectStringList.add(xmlElementString);

		Collection<Field> fields = fieldMap.values();
		for (Field projectField : fields) {
			String fieldName = objectCreator.extractTagName(projectField);
			projectField.setAccessible(true);
			Object fieldValue = projectField.get(object);
			if (fieldValue != null) {
				if (!projectField.getType().isPrimitive()) {
					if (projectField.getType().equals(String.class)) {
						xmlElementString = tab + getElementString(fieldName, (String) fieldValue);
						projectStringList.add(xmlElementString);
					}
				} else {
					xmlElementString = tab + getElementString(fieldName, fieldValue.toString());
					projectStringList.add(xmlElementString);
				}
			}
		}

		SpriteSerializer spriteSerializer = new SpriteSerializer(project);
		projectStringList.addAll(spriteSerializer.serializeList());

		xmlElementString = getEndTag(projectTag);
		projectStringList.add(xmlElementString);
		return projectStringList;
	}

}
