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
package org.catrobat.catroid.io;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ConcurrentFormulaHashMap;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.userbrick.InputFormulaField;

public class XStreamConcurrentFormulaHashMapConverter implements Converter {

	private static final String FORMULA = "formula";
	private static final String CATEGORY = "category";
	private static final String USER_DEFINED_INPUT = "input";

	@Override
	public boolean canConvert(Class type) {
		return type.equals(ConcurrentFormulaHashMap.class);
	}

	@Override
	public void marshal(Object object, HierarchicalStreamWriter hierarchicalStreamWriter,
			MarshallingContext marshallingContext) {
		ConcurrentFormulaHashMap concurrentFormulaHashMap = (ConcurrentFormulaHashMap) object;
		for (Brick.FormulaField formulaField : concurrentFormulaHashMap.keySet()) {
			hierarchicalStreamWriter.startNode(FORMULA);

			String attributeString;
			if (formulaField instanceof InputFormulaField) {
				attributeString = USER_DEFINED_INPUT;
			} else {
				attributeString = CATEGORY;
			}

			hierarchicalStreamWriter.addAttribute(attributeString, formulaField.toString());
			marshallingContext.convertAnother(concurrentFormulaHashMap.get(formulaField).getRoot());
			hierarchicalStreamWriter.endNode();
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader hierarchicalStreamReader, UnmarshallingContext unmarshallingContext) {
		ConcurrentFormulaHashMap concurrentFormulaHashMap = new ConcurrentFormulaHashMap();
		while (hierarchicalStreamReader.hasMoreChildren()) {
			hierarchicalStreamReader.moveDown();

			Brick.FormulaField formulaField;
			if (hierarchicalStreamReader.getAttribute(CATEGORY) != null) {
				formulaField = Brick.BrickField.valueOf(hierarchicalStreamReader.getAttribute(CATEGORY));
			} else {
				formulaField = new InputFormulaField(hierarchicalStreamReader.getAttribute(USER_DEFINED_INPUT));
			}

			Formula formula;
			if (FORMULA.equals(hierarchicalStreamReader.getNodeName())) {
				FormulaElement rootFormula = (FormulaElement) unmarshallingContext.convertAnother(concurrentFormulaHashMap,
						FormulaElement.class);
				formula = new Formula(rootFormula);
			} else {
				formula = new Formula(0);
			}
			hierarchicalStreamReader.moveUp();

			concurrentFormulaHashMap.putIfAbsent(formulaField, formula);
		}
		return concurrentFormulaHashMap;
	}
}
