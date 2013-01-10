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
package org.catrobat.catroid.test.xml;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.xml.parser.XMLAlias;

import android.test.InstrumentationTestCase;

public class AnnotationTest extends InstrumentationTestCase {

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
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

		String xmlTagName = "programName";
		String value = "blubb";

		Field fieldToSet = projectFieldsToSet.get(xmlTagName);

		if (fieldToSet != null) {
			fieldToSet.setAccessible(true);
			fieldToSet.set(project, value);
		}

		assertEquals("The value is not set correctly", value, project.getName());
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
		return tagName;
	}

}
