/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

package org.catrobat.catroid.pocketmusic.ui;

import org.catrobat.catroid.R;

enum PianoKeyType {
	UNSIGNED(8, R.color.solid_white, R.color.solid_black),
	SIGNED(5, R.color.solid_black, R.color.solid_white);

	private int numberOfKeys;
	private int backgroundColor;
	private int textColor;

	PianoKeyType(int numberOfKeys, int backgroundColor, int textColor) {
		this.numberOfKeys = numberOfKeys;
		this.backgroundColor = backgroundColor;
		this.textColor = textColor;
	}

	public int getNumberOfKeys() {
		return numberOfKeys;
	}

	public int getBackgroundColor() {
		return backgroundColor;
	}

	public int getTextColor() {
		return textColor;
	}
}
