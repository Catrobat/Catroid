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

package org.catrobat.catroid.formulaeditor.function;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserVariableEntry implements Serializable {
	private static final long serialVersionUID = 1L;

	private transient List<UserVariableEntry> userVariableEntries;
	private boolean isList = false;

	private transient Object value;

	public UserVariableEntry() {
		userVariableEntries = new ArrayList<>();
		this.value = 0d;
	}
	public UserVariableEntry(UserVariableEntry copyUserVariableEntry) {
		userVariableEntries = new ArrayList<>();
		if (copyUserVariableEntry.isList()) {
			for (UserVariableEntry currentUserVariableEntry : copyUserVariableEntry.getUserVariableEntries()) {
				userVariableEntries.add(new UserVariableEntry(currentUserVariableEntry));
			}
		} else {
			this.setValue(copyUserVariableEntry.getValue());
		}
	}

	public boolean hasSameValue(UserVariableEntry variableToCheckEntry) {
		if (variableToCheckEntry.isList()) {
			boolean allEqual = true;
			for (UserVariableEntry currentUserVariableEntry : variableToCheckEntry.getUserVariableEntries()) {
				allEqual &= hasSameValue(currentUserVariableEntry);
			}
			return allEqual;
		} else {
			return this.getValue().equals(variableToCheckEntry.getValue());
		}
	}

	public boolean isList() {
		return isList;
	}

	public void setIsList(boolean isList) {
		this.isList = isList;
	}

	public void setValue(Object value) {
		if (value.getClass() == ArrayList.class) {
			userVariableEntries.clear();
			isList = true;
		}
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public List<UserVariableEntry> getUserVariableEntries() {
		return userVariableEntries;
	}

	public void setUserVariableEntries(List<UserVariableEntry> userVariableEntries) {
		//TODO shallow copy maybe bad
		this.userVariableEntries = userVariableEntries;
	}

	public int getSize() {
		if (isList) {
			return ((List<Object>) value).size();
		}
		return 0;
	}
}
