/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.xmlformat;

import com.thoughtworks.xstream.converters.basic.FloatConverter;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.io.XstreamSerializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.catrobat.catroid.test.xmlformat.ClassDiscoverer.getAllSubClassesOf;
import static org.catrobat.catroid.test.xmlformat.ClassDiscoverer.removeAbstractClasses;
import static org.catrobat.catroid.test.xmlformat.ClassDiscoverer.removeInnerClasses;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;

@RunWith(Parameterized.class)
public class ScriptsXmlSerializerTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		List<Object[]> parameters = new ArrayList<>();

		Set<Class<? extends Script>> scriptClasses = getAllSubClassesOf(Script.class);
		scriptClasses = removeAbstractClasses(scriptClasses);
		scriptClasses = removeInnerClasses(scriptClasses);

		for (Class<?> scriptClazz : scriptClasses) {
			parameters.add(new Object[] {scriptClazz.getName(), scriptClazz});
		}

		return parameters;
	}

	@Parameterized.Parameter
	public String simpleName;

	@Parameterized.Parameter(1)
	public Class scriptClass;

	@Test
	public void testScriptAlias() throws IllegalAccessException, InstantiationException {
		Script script = (Script) scriptClass.newInstance();
		String xml = XstreamSerializer.getInstance().getXstream().toXML(script);
		FloatConverter floatConverter = new FloatConverter();
		String strPosX = floatConverter.toString(script.getPosX());
		String strPosY = floatConverter.toString(script.getPosY());
		assertThat(xml,
				startsWith(String.format("<script type=\"%s\" posX=\"%s\" posY=\"%s\">",
						scriptClass.getSimpleName(), strPosX, strPosY)));
	}

	@Test
	public void testStrayMissingAliasInComponent() throws InstantiationException, IllegalAccessException {
		Script script = (Script) scriptClass.newInstance();
		String xml = XstreamSerializer.getInstance().getXstream().toXML(script);
		assertThat(xml, not(containsString("org.catrobat.catroid")));
	}
}
