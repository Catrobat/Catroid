/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.formulaeditor.datacontainer;

import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.List;
import java.util.Map;

class UserBrickVariableBehaviour extends SpriteDataBehaviour<UserBrick, UserVariable> {

	private DataContainer dataContainer;

	UserBrickVariableBehaviour(DataContainer dataContainer) {
		this.dataContainer = dataContainer;
	}

	@Override
	protected void reset(List<UserVariable> dataList) {
		throw new UnsupportedOperationException("Resetting variables of userbricks is not supported!");
	}

	@Override
	protected Map<UserBrick, List<UserVariable>> getDataMap() {
		return dataContainer.getUserBrickVariableMap();
	}

	@Override
	protected UserVariable newInstance(String name) {
		return new UserVariable(name);
	}

	@Override
	protected UserVariable newInstance(String name, Object value) {
		return new UserVariable(name, value);
	}

	@Override
	protected String getDataName(UserVariable data) {
		return data.getName();
	}

	@Override
	protected void setDataName(UserVariable data, String name) {
		data.setName(name);
	}

	@Override
	protected String getKeyName(UserBrick key) {
		return key.getDefinitionBrick().getName().toString();
	}

	@Override
	public void setValue(UserVariable userVariable, Object value) {
		if (userVariable != null) {
			userVariable.setValue(value);
		}
	}

	@Override
	protected boolean isClone(UserBrick key) {
		return false;
	}
}
