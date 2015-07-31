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
package org.catrobat.catroid.content.bricks;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserScriptDefinitionBrickElements implements Serializable {

	private static final long serialVersionUID = 1L;

	private transient int version = 0;
	@XStreamAlias("userBrickElements")
	private List<UserScriptDefinitionBrickElement> userScriptDefinitionBrickElements = new ArrayList<UserScriptDefinitionBrickElement>();

	@Override
	public UserScriptDefinitionBrickElements clone() {
		UserScriptDefinitionBrickElements result = new UserScriptDefinitionBrickElements();
		List<UserScriptDefinitionBrickElement> cloneList = new ArrayList<UserScriptDefinitionBrickElement>();
		for (UserScriptDefinitionBrickElement original : userScriptDefinitionBrickElements) {
			UserScriptDefinitionBrickElement clone = new UserScriptDefinitionBrickElement();
			clone.isEditModeLineBreak = original.isEditModeLineBreak;
			clone.isVariable = original.isVariable;
			clone.key = original.key;
			clone.name = original.name;
			clone.newLineHint = original.newLineHint;
			cloneList.add(clone);
		}
		result.setUserScriptDefinitionBrickElements(cloneList);
		result.version = 0;
		return result;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public List<UserScriptDefinitionBrickElement> getUserScriptDefinitionBrickElementList() {
		return userScriptDefinitionBrickElements;
	}

	public void setUserScriptDefinitionBrickElements(List<UserScriptDefinitionBrickElement> userScriptDefinitionBrickElements) {
		this.userScriptDefinitionBrickElements = userScriptDefinitionBrickElements;
	}

	public void incrementVersion() {
		version++;
	}
}
