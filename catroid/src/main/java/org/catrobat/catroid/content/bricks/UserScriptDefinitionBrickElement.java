/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import java.io.Serializable;

public class UserScriptDefinitionBrickElement implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String TAG = UserScriptDefinitionBrickElement.class.getSimpleName();

	private enum UserBrickElementType {
		VARIABLE(10),
		LINEBREAK(20),
		TEXT(30);

		private int value;

		UserBrickElementType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	private String text;
	private UserBrickElementType elementType;
	private transient boolean newLineHint;

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UserScriptDefinitionBrickElement)) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		UserScriptDefinitionBrickElement elementToCompare = (UserScriptDefinitionBrickElement) obj;
		if (!(elementToCompare.getElementType().getValue() == elementType.getValue())
				|| !(elementToCompare.getText().equals(text)) || !(elementToCompare.isNewLineHint() == newLineHint)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return super.hashCode() * TAG.hashCode();
	}

	public String getText() {
		return text;
	}

	public void setText(String name) {
		this.text = name;
	}

	public UserBrickElementType getElementType() {
		return elementType;
	}

	public void setElementType(UserBrickElementType elementType) {
		this.elementType = elementType;
	}

	public boolean isLineBreak() {
		return elementType.equals(UserBrickElementType.LINEBREAK);
	}

	public void setIsLineBreak() {
		elementType = UserBrickElementType.LINEBREAK;
	}

	public boolean isVariable() {
		return elementType.equals(UserBrickElementType.VARIABLE);
	}

	public void setIsVariable() {
		elementType = UserBrickElementType.VARIABLE;
	}

	public boolean isText() {
		return elementType.equals(UserBrickElementType.TEXT);
	}

	public void setIsText() {
		elementType = UserBrickElementType.TEXT;
	}

	public boolean isNewLineHint() {
		return newLineHint;
	}

	public void setNewLineHint(boolean newLineHint) {
		this.newLineHint = newLineHint;
	}
}
