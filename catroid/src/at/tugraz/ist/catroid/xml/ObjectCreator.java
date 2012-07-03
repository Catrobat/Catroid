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
package at.tugraz.ist.catroid.xml;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import at.tugraz.ist.catroid.content.Project;

public class ObjectCreator {

	public Project reflectionSet(InputStream XMLFile) throws ParseException {
		HeaderTagsParser parser = new HeaderTagsParser();

		Project project = null;

		try {
			Map<String, String> headerValues = parser.parseHeader(XMLFile);
			project = Project.class.newInstance();

			Field[] projectClassFields = Project.class.getDeclaredFields();

			for (Field fieldinProject : projectClassFields) {
				boolean isCurrentFieldTransient = Modifier.isTransient(fieldinProject.getModifiers());

				if (isCurrentFieldTransient) {
					continue;
				}
				String tagName = extractTagName(fieldinProject);

				String valueInString = headerValues.get(tagName);

				if (valueInString != null) {
					Object finalObject = getObjectWithValue(fieldinProject, valueInString);
					fieldinProject.setAccessible(true);
					fieldinProject.set(project, finalObject);
				}
			}

		} catch (Throwable e) {
			throw new ParseException("Exception when creating object", e);

		}

		return project;
	}

	public Object getObjectWithValue(Field field, String valueInString) {
		String fieldClassCannonicalName = field.getType().getCanonicalName();
		if (fieldClassCannonicalName.equals("int") || fieldClassCannonicalName.equals("java.lang.Integer")) {
			return new Integer(valueInString);
		} else if (fieldClassCannonicalName.equals("java.lang.String")) {
			return valueInString;
		} else if (fieldClassCannonicalName.equals("java.lang.Double") || fieldClassCannonicalName.equals("double")) {
			return new Double(valueInString);
		} else if (fieldClassCannonicalName.equals("java.lang.Float") || fieldClassCannonicalName.equals("float")) {
			return new Float(valueInString);
		}
		return null;
	}

	public static Object createWithoutConstructor(final Class clazz) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
		Method newInstance = ObjectInputStream.class.getDeclaredMethod("newInstance", Class.class, Class.class);
		newInstance.setAccessible(true);
		return newInstance.invoke(null, clazz, Object.class);

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
