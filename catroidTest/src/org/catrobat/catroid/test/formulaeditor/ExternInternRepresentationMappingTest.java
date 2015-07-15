/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

package org.catrobat.catroid.test.formulaeditor;

import android.test.InstrumentationTestCase;

import org.catrobat.catroid.formulaeditor.ExternInternRepresentationMapping;

public class ExternInternRepresentationMappingTest extends InstrumentationTestCase {

	public void testGetExternTokenStartIndex() {

		ExternInternRepresentationMapping externInternRepresentationMapping = new ExternInternRepresentationMapping();

		int externTokenStringStartIndex = 1;
		int externTokenStringEndIndex = 3;
		int internTokenListIndex = 0;

		externInternRepresentationMapping.putMapping(externTokenStringStartIndex, externTokenStringEndIndex,
				internTokenListIndex);

		assertEquals("getExternTokenStartIndex returns wrong value", externTokenStringStartIndex,
				externInternRepresentationMapping.getExternTokenStartIndex(internTokenListIndex));

		assertEquals("getExternTokenEndIndex returns wrong value", externTokenStringEndIndex,
				externInternRepresentationMapping.getExternTokenEndIndex(internTokenListIndex));

		assertEquals("Mapping should not exist", ExternInternRepresentationMapping.MAPPING_NOT_FOUND,
				externInternRepresentationMapping.getExternTokenStartIndex(1));
		assertEquals("Mapping should not exist", ExternInternRepresentationMapping.MAPPING_NOT_FOUND,
				externInternRepresentationMapping.getExternTokenEndIndex(1));
	}
}
