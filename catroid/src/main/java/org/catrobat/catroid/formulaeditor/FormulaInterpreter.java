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

import org.catrobat.catroid.content.Scope;
import org.jetbrains.annotations.NotNull;

import static org.catrobat.catroid.utils.NumberFormats.trimTrailingCharacters;

public class FormulaInterpreter {

	private static final String ERROR_STRING = "ERROR";
	private final FormulaElement formulaTree;

	public FormulaInterpreter(FormulaElement formulaTree) {
		this.formulaTree = formulaTree;
	}

	public Double interpretDouble(Scope scope) throws InterpretationException {
		try {
			return assertNotNaN(interpretDoubleInternal(scope));
		} catch (ClassCastException | NumberFormatException exception) {
			throw new InterpretationException("Couldn't interpret Formula.", exception);
		}
	}

	public Integer interpretInteger(Scope scope) throws InterpretationException {
		return interpretDouble(scope).intValue();
	}

	public Float interpretFloat(Scope scope) throws InterpretationException {
		return interpretDouble(scope).floatValue();
	}

	@NotNull
	private Double interpretDoubleInternal(Scope scope) {
		Object o = formulaTree.interpretRecursive(scope);
		Double doubleReturnValue;
		if (o instanceof String) {
			doubleReturnValue = Double.valueOf((String) o);
		} else {
			doubleReturnValue = (Double) o;
		}
		return doubleReturnValue;
	}

	private double assertNotNaN(Double doubleReturnValue) throws InterpretationException {
		if (doubleReturnValue.isNaN()) {
			throw new InterpretationException("NaN in interpretDouble()");
		}
		return doubleReturnValue;
	}

	public Object interpretObject(Scope scope) {
		return formulaTree.interpretRecursive(scope);
	}

	public String interpretString(Scope scope) throws InterpretationException {
		Object interpretation = formulaTree.interpretRecursive(scope);

		if (interpretation instanceof Double && ((Double) interpretation).isNaN()) {
			throw new InterpretationException("NaN in interpretString()");
		}

		String value = String.valueOf(interpretation);
		return trimTrailingCharacters(value);
	}

	public String getUserFriendlyString(StringProvider stringProvider, Scope scope) {
		if (formulaTree.isBoolean(scope)) {
			return tryInterpretBooleanToString(stringProvider, scope);
		} else {
			return tryInterpretString(scope);
		}
	}

	private String tryInterpretString(Scope scope) {
		try {
			return interpretString(scope);
		} catch (InterpretationException interpretationException) {
			return ERROR_STRING;
		}
	}

	private String toLocalizedString(boolean value, StringProvider stringProvider) {
		return stringProvider.getTrueOrFalse(value);
	}

	public boolean interpretBoolean(Scope scope) throws InterpretationException {
		return interpretDouble(scope).intValue() != 0;
	}

	public String interpretBooleanToString(Scope scope,
			StringProvider stringProvider) throws InterpretationException {
		boolean booleanValue = interpretBoolean(scope);
		return toLocalizedString(booleanValue, stringProvider);
	}

	private String tryInterpretBooleanToString(StringProvider stringProvider, Scope scope) {
		try {
			return interpretBooleanToString(scope, stringProvider);
		} catch (InterpretationException interpretationException) {
			return ERROR_STRING;
		}
	}

	public interface StringProvider {
		String getTrueOrFalse(Boolean value);
	}
}
