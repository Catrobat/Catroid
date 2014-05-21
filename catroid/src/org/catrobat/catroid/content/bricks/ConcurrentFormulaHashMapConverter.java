/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2013 The Catrobat Team
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

package org.catrobat.catroid.content.bricks;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import org.catrobat.catroid.formulaeditor.Formula;

public class ConcurrentFormulaHashMapConverter implements Converter {

	@SuppressWarnings("rawtypes")
	@Override
	public boolean canConvert(Class aClass) {
		return aClass.equals(ConcurrentFormulaHashMap.class);
	}

	@Override
	public void marshal(Object object, HierarchicalStreamWriter hierarchicalStreamWriter,
			MarshallingContext marshallingContext) {
		ConcurrentFormulaHashMap concurrentFormulaHashMap = (ConcurrentFormulaHashMap) object;
		for (Brick.BrickField brickField : concurrentFormulaHashMap.keySet()) {
			hierarchicalStreamWriter.startNode(Brick.BrickField.class.getSimpleName());
			hierarchicalStreamWriter.setValue(brickField.toString());
			hierarchicalStreamWriter.endNode();
			hierarchicalStreamWriter.startNode(Formula.class.getSimpleName());
			marshallingContext.convertAnother(concurrentFormulaHashMap.get(brickField));
			hierarchicalStreamWriter.endNode();
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader hierarchicalStreamReader, UnmarshallingContext unmarshallingContext) {
		ConcurrentFormulaHashMap concurrentFormulaHashMap = new ConcurrentFormulaHashMap();
		while (hierarchicalStreamReader.hasMoreChildren()) {
			hierarchicalStreamReader.moveDown();
			Brick.BrickField brickField = Brick.BrickField.valueOf(hierarchicalStreamReader.getValue());
			hierarchicalStreamReader.moveUp();

			hierarchicalStreamReader.moveDown();
			Formula formula = new Formula(0);
			if (Formula.class.getSimpleName().equals(hierarchicalStreamReader.getNodeName())) {
				formula = (Formula) unmarshallingContext.convertAnother(concurrentFormulaHashMap, Formula.class);
			}
			hierarchicalStreamReader.moveUp();

			concurrentFormulaHashMap.putIfAbsent(brickField, formula);
		}
		return concurrentFormulaHashMap;
	}
}
