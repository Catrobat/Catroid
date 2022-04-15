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

import android.os.Build;

import org.catrobat.catroid.formulaeditor.common.FormulaElementOperations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.annotation.RequiresApi;

public class UserVariableEntry implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<UserVariableEntry> userVariableEntries;
	private boolean isList = false;

	private transient Object value;

	public UserVariableEntry() {
		userVariableEntries = new ArrayList<>();
		setValue(0d);
	}
	public UserVariableEntry(Object value) {
		userVariableEntries = new ArrayList<>();
		setValue(value);
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
		isList = copyUserVariableEntry.isList;
		this.value = 0d;
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

	public void setValue(Object value) {
		if (value.getClass() == ArrayList.class) {
			ArrayList<Object> valueList = (ArrayList<Object>) value;
			userVariableEntries.clear();
			isList = true;
			for (Object entry : valueList) {
				userVariableEntries.add(new UserVariableEntry(entry));
			}
		} else {
			userVariableEntries.clear();
			isList = false;
			this.value = value;
		}
	}

	public Object getListItem(int index) {
		if (isList() && index < getSize()) {
			return userVariableEntries.get(index).getValue();
		} else if (index == 0) {
			return getValue();
		}
		return null;
	}

	public int getIndexOfListItem(Object item) {
		for (UserVariableEntry entry : userVariableEntries) {
			Object value = entry.getValue();
			if (value.equals(item)) {
				return userVariableEntries.indexOf(entry);
			}
		}
		if (contains(item)) {
			return 0;
		}
		return -1;
	}

	public void addListItem(Object value) {
		if (!(this.value instanceof Double && (Double) this.value == 0d)) {
			userVariableEntries.add(new UserVariableEntry(this.value));
		}
		userVariableEntries.add(new UserVariableEntry(value));
		isList = true;

	}

	public void deleteListItemAtIndex(int index) {
		if (isList()) {
			userVariableEntries.remove(index);
		}
		isList = true;
	}

	public void insertListItemAtIndex(int index, Object item) {
		userVariableEntries.add(index, new UserVariableEntry(item));
		isList = true;
	}

	public void setListItemAtIndex(int index, Object item) {
		if (!isList()) {
			userVariableEntries.add(new UserVariableEntry(item));
		} else {
			userVariableEntries.get(index).setValue(item);
		}
		isList = true;
	}

	public Object getValue() {
		if (isList()) {
			ArrayList<Object> list = new ArrayList<>();
			for (UserVariableEntry currentUserVariableEntry : getUserVariableEntries()) {
				list.add(currentUserVariableEntry.getValue());
			}
			return list;
		}
		return value;
	}

	public List<UserVariableEntry> getUserVariableEntries() {
		return userVariableEntries;
	}

	public boolean contains(Object item) {
		if (value == null || item == null) {
			return false;
		}
		if (FormulaElementOperations.interpretOperatorEqual(value, item)) {
			return true;
		}
		for (UserVariableEntry entry : userVariableEntries) {
			if (entry.contains(item)) {
				return true;
			}
		}
		return false;
	}

	public void setToEmptyList() {
		userVariableEntries.clear();
		isList = true;
	}

	public void reset() {
		setValue(0d);
	}

	public boolean isEmptyList() {
		return isList() && getSize() == 0;
	}

	public int getSize() {
		if (isList()) {
			return getUserVariableEntries().size();
		}
		return 1;
	}
}