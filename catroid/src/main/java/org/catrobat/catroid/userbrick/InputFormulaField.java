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

package org.catrobat.catroid.userbrick;

import org.catrobat.catroid.content.bricks.Brick;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

public class InputFormulaField implements Brick.FormulaField {

	String input;

	public InputFormulaField(String input) {
		this.input = input;
	}

	@VisibleForTesting
	public InputFormulaField() {
		input = null;
	}

	@Override
	public int hashCode() {
		return this.input.hashCode();
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (obj instanceof InputFormulaField) {
			return this.input.equals(((InputFormulaField) obj).input);
		}
		return false;
	}

	@NonNull
	@Override
	public String toString() {
		return this.input;
	}
}
