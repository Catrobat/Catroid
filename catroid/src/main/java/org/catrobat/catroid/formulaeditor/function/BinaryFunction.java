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

package org.catrobat.catroid.formulaeditor.function;

public class BinaryFunction implements FormulaFunction, BinaryFunctionAction {
	private final BinaryFunctionAction action;

	public BinaryFunction(BinaryFunctionAction action) {
		this.action = action;
	}

	@Override
	public Double execute(Double firstArgument, Double secondArgument) {
		if (firstArgument == null || secondArgument == null) {
			return 0d;
		} else {
			return action.execute(firstArgument, secondArgument);
		}
	}

	@Override
	public Double execute(Double... args) {
		if (args == null || args.length < 2) {
			return 0d;
		} else {
			return execute(args[0], args[1]);
		}
	}
}
