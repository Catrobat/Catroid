/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

import android.content.Context;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Set;

import static org.catrobat.catroid.utils.NumberFormats.trimTrailingCharacters;

public class Formula implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String ERROR_STRING = "ERROR";
	private FormulaElement formulaTree;

	private transient InternFormula internFormula = null;

	private boolean sceneFirstStart = false;

	public Formula(FormulaElement formulaElement) {
		formulaTree = formulaElement;
		internFormula = new InternFormula(formulaTree.getInternTokenList());
	}

	public Formula(Integer value) {
		if (value < 0) {
			initInverted(Long.toString(Math.abs((long) value)));
		} else {
			init(ElementType.NUMBER, value.toString());
		}
	}

	public Formula(Double value) {
		if (value < 0) {
			initInverted(Double.toString(Math.abs(value)));
		} else {
			init(ElementType.NUMBER, value.toString());
		}
	}

	public Formula(Float value) {
		this(Double.valueOf(value));
	}

	public Formula(String value) {
		if (value.equalsIgnoreCase(Functions.ARDUINOANALOG.toString())) {
			formulaTree = new FormulaElement(ElementType.SENSOR, Functions.ARDUINOANALOG.toString(), null);
		} else if (value.equalsIgnoreCase(Functions.ARDUINODIGITAL.toString())) {
			formulaTree = new FormulaElement(ElementType.SENSOR, Functions.ARDUINODIGITAL.toString(), null);
		} else {
			init(ElementType.STRING, value);
		}
	}

	public FormulaElement getFormulaTree() {
		return formulaTree;
	}

	private void init(ElementType number, String s) {
		formulaTree = new FormulaElement(number, s, null);
		internFormula = new InternFormula(formulaTree.getInternTokenList());
	}

	private void initInverted(String value) {
		formulaTree = new FormulaElement(ElementType.OPERATOR, Operators.MINUS.toString(), null);
		formulaTree.setRightChild(new FormulaElement(ElementType.NUMBER, value, formulaTree));
		internFormula = new InternFormula(formulaTree.getInternTokenList());
	}

	public void updateCollisionFormulas(String oldName, String newName, Context context) {
		internFormula.updateCollisionFormula(oldName, newName, context);
		formulaTree.updateElementByName(oldName, newName, ElementType.COLLISION_FORMULA);
	}

	public void flattenAllLists() {
		formulaTree.insertFlattenForAllUserLists(formulaTree, null);
		formulaTree = formulaTree.getRoot();
		internFormula.setInternTokenFormulaList(formulaTree.getInternTokenList());
	}

	public void updateCollisionFormulasToVersion() {
		internFormula.updateCollisionFormulaToVersion(CatroidApplication.getAppContext());
		formulaTree.updateCollisionFormulaToVersion(ProjectManager.getInstance().getCurrentProject());
	}

	public void updateDirectionPropertyToVersion() {
		String oldName = "OBJECT_ROTATION";
		String newName = "MOTION_DIRECTION";
		internFormula.updateSensorTokens(oldName, newName, CatroidApplication.getAppContext());
		formulaTree.updateElementByName(oldName, newName, ElementType.SENSOR);
	}

	public void updateVariableName(String oldName, String newName) {
		internFormula.updateVariableReferences(oldName, newName, CatroidApplication.getAppContext());
		formulaTree.updateElementByName(oldName, newName, ElementType.USER_VARIABLE);
	}

	public void updateUserlistName(String oldName, String newName) {
		internFormula.updateListReferences(oldName, newName, CatroidApplication.getAppContext());
		formulaTree.updateElementByName(oldName, newName, ElementType.USER_LIST);
	}

	public boolean containsSpriteInCollision(String name) {
		return formulaTree.containsSpriteInCollision(name);
	}

	public Integer interpretInteger(Scope scope) throws InterpretationException {
		return interpretDouble(scope).intValue();
	}

	public Double interpretDouble(Scope scope) throws InterpretationException {
		try {
			if (sceneFirstStart) {
				sceneFirstStart = false;
				return 0.0;
			} else {
				return assertNotNaN(interpretDoubleInternal(scope));
			}
		} catch (ClassCastException | NumberFormatException exception) {
			throw new InterpretationException("Couldn't interpret Formula.", exception);
		}
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

	public Float interpretFloat(Scope scope) throws InterpretationException {
		return interpretDouble(scope).floatValue();
	}

	public String interpretString(Scope scope) throws InterpretationException {
		Object interpretation = formulaTree.interpretRecursive(scope);

		if (interpretation instanceof Double && ((Double) interpretation).isNaN()) {
			throw new InterpretationException("NaN in interpretString()");
		}

		String value = String.valueOf(interpretation);
		return trimTrailingCharacters(value);
	}

	public Object interpretObject(Scope scope) {
		return formulaTree.interpretRecursive(scope);
	}

	public void setRoot(FormulaElement formula) {
		formulaTree = formula;
		internFormula = new InternFormula(formula.getInternTokenList());
	}

	public FormulaElement getRoot() {
		return formulaTree;
	}

	public String getTrimmedFormulaString(Context context) {
		return internFormula.trimExternFormulaString(context);
	}

	public InternFormulaState getInternFormulaState() {
		return internFormula.getInternFormulaState();
	}

	public boolean containsElement(FormulaElement.ElementType elementType) {
		return formulaTree.containsElement(elementType);
	}

	public boolean isNumber() {
		return formulaTree.isNumber();
	}

	@Override
	public Formula clone() {
		if (formulaTree != null) {
			return new Formula(formulaTree.clone());
		}

		return new Formula(0);
	}

	public void addRequiredResources(final Set<Integer> requiredResourcesSet) {
		formulaTree.addRequiredResources(requiredResourcesSet);
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

	private String tryInterpretBooleanToString(StringProvider stringProvider, Scope scope) {
		try {
			return interpretBooleanToString(scope, stringProvider);
		} catch (InterpretationException interpretationException) {
			return ERROR_STRING;
		}
	}

	public String interpretBooleanToString(Scope scope,
			StringProvider stringProvider) throws InterpretationException {
		boolean booleanValue = interpretBoolean(scope);
		return toLocalizedString(booleanValue, stringProvider);
	}

	public boolean interpretBoolean(Scope scope) throws InterpretationException {
		return interpretDouble(scope).intValue() != 0;
	}

	private String toLocalizedString(boolean value, StringProvider stringProvider) {
		return stringProvider.getTrueOrFalse(value);
	}

	public interface StringProvider {
		String getTrueOrFalse(Boolean value);
	}

	public void sceneFirstStart(boolean sceneFirstStart) {
		this.sceneFirstStart = sceneFirstStart;
	}
}
