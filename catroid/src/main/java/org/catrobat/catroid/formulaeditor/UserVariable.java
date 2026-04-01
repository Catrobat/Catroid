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
package org.catrobat.catroid.formulaeditor;

import java.io.Serializable;
import java.util.UUID;

public class UserVariable implements Serializable, UserData<Object> {

	private static final long serialVersionUID = 1L;

	private String name;
	private int initialIndex = -1;
	private UUID deviceValueKey;
	private transient Object value;
	private transient boolean visible = true;
	private transient boolean dummy = false;

	public UserVariable() {
		this.value = 0d;
	}

	public UserVariable(String name) {
		this.name = name;
		this.value = 0d;
		this.deviceValueKey = UUID.randomUUID();
	}

	public UserVariable(final String name, final Object value) {
		this.name = name;
		this.value = value;
		this.deviceValueKey = UUID.randomUUID();
	}

	public UserVariable(UserVariable variable) {
		this.name = variable.name;
		this.value = variable.value;
		this.deviceValueKey = UUID.randomUUID();
	}

	public int getInitialIndex() {
		return initialIndex;
	}

	public void setInitialIndex(int initialIndex) {
		this.initialIndex = initialIndex;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		this.value = value;
	}

	public boolean getVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isDummy() {
		return dummy;
	}

	public void setDummy(boolean dummy) {
		this.dummy = dummy;
	}

	@Override
	public void reset() {
		value = 0d;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!getClass().equals(obj.getClass())) {
			return false;
		}
		return ((UserVariable) obj).name.equals(name);
	}

	public boolean hasSameValue(UserVariable variableToCheck) {
		return variableToCheck.value.equals(value);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public UUID getDeviceKey() {
		return deviceValueKey;
	}

	public void setDeviceValueKey(UUID deviceValueFileName) {
		this.deviceValueKey = deviceValueFileName;
	}
}
