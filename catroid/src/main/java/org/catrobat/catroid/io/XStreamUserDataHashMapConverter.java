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
import org.catrobat.catroid.content.bricks.UserDataHashMap;
import org.catrobat.catroid.formulaeditor.UserData;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;

public class XStreamUserDataHashMapConverter implements Converter {

	private static final String USERDATA = "userData";
	private static final String CATEGORY = "category";

	@Override
	public boolean canConvert(Class type) {
		return type.equals(UserDataHashMap.class);
	}

	@Override
	public void marshal(Object object, HierarchicalStreamWriter hierarchicalStreamWriter,
			MarshallingContext marshallingContext) {
		UserDataHashMap userDataHashMap = (UserDataHashMap) object;
		for (Brick.BrickData brickData : userDataHashMap.keySet()) {
			hierarchicalStreamWriter.startNode(USERDATA);
			hierarchicalStreamWriter.addAttribute(CATEGORY, brickData.toString());
			if (userDataHashMap.get(brickData) != null) {
				marshallingContext.convertAnother(userDataHashMap.get(brickData));
			}
			hierarchicalStreamWriter.endNode();
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader hierarchicalStreamReader, UnmarshallingContext unmarshallingContext) {
		UserDataHashMap userDataHashMap = new UserDataHashMap();
		while (hierarchicalStreamReader.hasMoreChildren()) {
			hierarchicalStreamReader.moveDown();
			Brick.BrickData brickData =
					Brick.BrickData.valueOf(hierarchicalStreamReader.getAttribute(CATEGORY));
			UserData userData;
			if (Brick.BrickData.isUserList(brickData)) {
				userData = (UserData) unmarshallingContext.convertAnother(userDataHashMap,
						UserList.class);
			} else {
				userData = (UserData) unmarshallingContext.convertAnother(userDataHashMap,
						UserVariable.class);
			}
			hierarchicalStreamReader.moveUp();

			userDataHashMap.put(brickData, userData);
		}
		return userDataHashMap;
	}
}
