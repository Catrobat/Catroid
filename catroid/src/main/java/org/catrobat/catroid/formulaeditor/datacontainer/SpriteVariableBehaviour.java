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

import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

class SpriteVariableBehaviour extends SpriteDataBehaviour<Sprite, UserVariable> {

	private DataContainer dataContainer;

	SpriteVariableBehaviour(DataContainer dataContainer) {
		this.dataContainer = dataContainer;
	}

	@Override
	public List<UserVariable> getOrCreate(Sprite key) {
		List<UserVariable> data = super.getOrCreate(key);
		removeIllegalSpriteVariableEntries(key);
		return data;
	}

	@Override
	Map<Sprite, List<UserVariable>> cloneForScene(Scene scene, Map<Sprite, List<UserVariable>> originalData) {
		removeIllegalSpriteVariableEntries(null);
		return super.cloneForScene(scene, originalData);
	}

	@Override
	protected void reset(List<UserVariable> dataList) {
		for (UserVariable data : dataList) {
			data.setValue(0.0);
		}
	}

	@Override
	protected Map<Sprite, List<UserVariable>> getDataMap() {
		return dataContainer.getSpriteVariableMap();
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
	protected String getKeyName(Sprite key) {
		return key.getName();
	}

	@Override
	public void setValue(UserVariable userVariable, Object value) {
		if (userVariable != null) {
			userVariable.setValue(value);
		}
	}

	@Override
	protected boolean isClone(Sprite key) {
		return key.isClone();
	}

	private void removeIllegalSpriteVariableEntries(Sprite spriteToKeep) {
		Iterator iterator = getDataMap().keySet().iterator();
		while (iterator.hasNext()) {
			Sprite sprite = (Sprite) iterator.next();
			if (sprite == null
					|| spriteToKeep != null
					&& !(sprite == spriteToKeep)
					&& getDataMap().get(sprite).size() == 0
					&& sprite.getName().equals(spriteToKeep.getName())) {
				iterator.remove();
			}
		}
	}
}
