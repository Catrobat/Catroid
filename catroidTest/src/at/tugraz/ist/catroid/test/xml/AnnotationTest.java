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
package at.tugraz.ist.catroid.test.xml;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.xml.XMLAlias;

public class AnnotationTest extends InstrumentationTestCase {

	@Override
	protected void tearDown() throws Exception {
	}

	public void testReadingFieldsWithAnnotation() throws Exception {
		Map<String, Field> projectFieldsToSet = new HashMap<String, Field>();

		Field[] fields = Project.class.getDeclaredFields();
		for (Field field : fields) {
			boolean isCurrentFieldTransient = Modifier.isTransient(field.getModifiers());

			if (isCurrentFieldTransient) {
				continue;
			}

			String tagName = handleField(field);
			projectFieldsToSet.put(tagName, field);
		}

		Project project = Project.class.newInstance();

		// in the parser, e.g. onEndTag(String xmlTagName, String value)
		String xmlTagName = "projectName";
		String value = "blubb";

		Field fieldToSet = projectFieldsToSet.get(xmlTagName);

		if (fieldToSet != null) {
			fieldToSet.setAccessible(true);
			fieldToSet.set(project, value);
		}

		assertEquals(value, project.getName());
	}

	private String handleField(Field field) {
		boolean isAliasUsed = field.isAnnotationPresent(XMLAlias.class);
		String tagName;
		if (isAliasUsed) {
			XMLAlias xmlAlias = field.getAnnotation(XMLAlias.class);
			tagName = xmlAlias.value();
		} else {
			tagName = field.getName();
		}
		System.out.println("tagName: " + tagName);
		return tagName;
	}

}
