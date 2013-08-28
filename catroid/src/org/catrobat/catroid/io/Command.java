/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.io;

import java.io.Serializable;

public class Command implements Serializable {
	private static final long serialVersionUID = 1L;

	public static enum commandType {
		SINGLE_KEY, KEY_COMBINATION, MOUSE
	};

	private int key;
	private commandType type;
	private int[] keyCombination;

	public Command(int key, commandType type) {
		this.key = key;
		this.type = type;
	}

	public Command(int[] keyCombination, commandType type) {
		this.keyCombination = keyCombination;
		this.type = type;
	}

	public int getKey() {
		return key;
	}

	public int[] getKeyComb() {
		return keyCombination;
	}

	public commandType getCommandType() {
		return type;
	}
}