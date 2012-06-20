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

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import at.tugraz.ist.catroid.content.Project;

public class ObjectCreator {

	public Project reflectionSet(InputStream XMLFile) {
		HeaderTagsParser parser = new HeaderTagsParser();
		Map<String, String> headerValues = parser.parse(XMLFile);

		Project project = null;

		try {
			project = Project.class.newInstance();

			Field[] projectClassFields = Project.class.getDeclaredFields();

			for (Field field : projectClassFields) {
				boolean isCurrentFieldTransient = Modifier.isTransient(field.getModifiers());

				if (isCurrentFieldTransient) {
					continue;
				}
				String tagName = extractTagName(field);
				Object value = null;
				String canName = field.getType().getCanonicalName();
				if (field.getType().getCanonicalName().equals("int")) {
					value = new Integer(Integer.valueOf(headerValues.get(tagName)));
				} else if (field.getType().getCanonicalName().equals("java.lang.String")) {
					value = headerValues.get(tagName);
				}
				if (value != null) {
					field.setAccessible(true);
					field.set(project, value);
				}
			}

		} catch (Throwable e) {
			System.err.println(e);

		}

		return project;
	}

	private String extractTagName(Field field) {
		String tagName;
		if (field.isAnnotationPresent(XMLAlias.class)) {
			XMLAlias xmlAlias = field.getAnnotation(XMLAlias.class);
			tagName = xmlAlias.value();
		} else {
			tagName = field.getName();
		}
		return tagName;
	}
}
