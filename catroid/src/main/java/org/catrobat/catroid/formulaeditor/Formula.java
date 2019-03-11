/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;

import java.io.Serializable;
import java.util.Set;

import static org.catrobat.catroid.utils.NumberFormats.stringWithoutTrailingZero;

public class Formula implements Serializable {

	private static final long serialVersionUID = 1L;
	private FormulaElement formulaTree;

	private transient InternFormula internFormula = null;

	public Object readResolve() {

		if (formulaTree == null) {
			formulaTree = new FormulaElement(ElementType.NUMBER, "0 ", null);
		}

		internFormula = new InternFormula(formulaTree.getInternTokenList());

		return this;
	}

	public Formula(FormulaElement formulaElement) {
		formulaTree = formulaElement;
		internFormula = new InternFormula(formulaTree.getInternTokenList());
	}

	public Formula(Integer value) {
		if (value < 0) {
			formulaTree = new FormulaElement(ElementType.OPERATOR, Operators.MINUS.toString(), null);
			formulaTree.setRightChild(new FormulaElement(ElementType.NUMBER, Long.toString(Math.abs((long) value)),
					formulaTree));
			internFormula = new InternFormula(formulaTree.getInternTokenList());
		} else {
			formulaTree = new FormulaElement(ElementType.NUMBER, value.toString(), null);
			internFormula = new InternFormula(formulaTree.getInternTokenList());
		}
	}

	public Formula(Float value) {
		this(Double.valueOf(value));
	}

	public Formula(Double value) {
		if (value < 0) {
			formulaTree = new FormulaElement(ElementType.OPERATOR, Operators.MINUS.toString(), null);
			formulaTree.setRightChild(new FormulaElement(ElementType.NUMBER, Double.toString(Math.abs(value)),
					formulaTree));
			internFormula = new InternFormula(formulaTree.getInternTokenList());
		} else {
			formulaTree = new FormulaElement(ElementType.NUMBER, value.toString(), null);
			internFormula = new InternFormula(formulaTree.getInternTokenList());
		}
	}

	public void updateVariableReferences(String oldName, String newName, Context context) {
		internFormula.updateVariableReferences(oldName, newName, context);
		formulaTree.updateVariableReferences(oldName, newName);
	}

	public void updateCollisionFormulas(String oldName, String newName, Context context) {
		internFormula.updateCollisionFormula(oldName, newName, context);
		formulaTree.updateCollisionFormula(oldName, newName);
	}

	public void updateCollisionFormulasToVersion(float catroidLanguageVersion) {
		internFormula.updateCollisionFormulaToVersion(CatroidApplication.getAppContext(), catroidLanguageVersion);
		formulaTree.updateCollisionFormulaToVersion(catroidLanguageVersion);
	}

	public boolean containsSpriteInCollision(String name) {
		return formulaTree.containsSpriteInCollision(name);
	}

	public Formula(String value) {
		if (value.equalsIgnoreCase(Functions.ARDUINOANALOG.toString())) {
			formulaTree = new FormulaElement(ElementType.SENSOR, Functions.ARDUINOANALOG.toString(), null);
		} else if (value.equalsIgnoreCase(Functions.ARDUINODIGITAL.toString())) {
			formulaTree = new FormulaElement(ElementType.SENSOR, Functions.ARDUINODIGITAL.toString(), null);
		} else {
			formulaTree = new FormulaElement(ElementType.STRING, value, null);
			internFormula = new InternFormula(formulaTree.getInternTokenList());
		}
	}

	public Boolean interpretBoolean(Sprite sprite) throws InterpretationException {
		int result = interpretDouble(sprite).intValue();
		return result != 0;
	}

	public Integer interpretInteger(Sprite sprite) throws InterpretationException {
		Double returnValue = interpretDouble(sprite);
		return returnValue.intValue();
	}

	public Double interpretDouble(Sprite sprite) throws InterpretationException {
		try {
			Object returnValue = formulaTree.interpretRecursive(sprite);
			Double doubleReturnValue;
			if (returnValue instanceof String) {
				doubleReturnValue = Double.valueOf((String) returnValue);
				if (doubleReturnValue.isNaN()) {
					throw new InterpretationException("NaN in interpretDouble()");
				}
				return doubleReturnValue;
			} else {
				doubleReturnValue = (Double) returnValue;
				if (doubleReturnValue.isNaN()) {
					throw new InterpretationException("NaN in interpretDouble()");
				}
				return (Double) returnValue;
			}
		} catch (ClassCastException classCastException) {
			throw new InterpretationException("Couldn't interpret Formula.", classCastException);
		} catch (NumberFormatException numberFormatException) {
			throw new InterpretationException("Couldn't interpret Formula.", numberFormatException);
		}
	}

	public Float interpretFloat(Sprite sprite) throws InterpretationException {
		Double returnValue = interpretDouble(sprite);
		return returnValue.floatValue();
	}

	public String interpretString(Sprite sprite) throws InterpretationException {
		Object interpretation = formulaTree.interpretRecursive(sprite);

		if (interpretation instanceof Double && ((Double) interpretation).isNaN()) {
			throw new InterpretationException("NaN in interpretString()");
		}

		String value = String.valueOf(interpretation);
		return stringWithoutTrailingZero(value);
	}

	public Object interpretObject(Sprite sprite) {
		return formulaTree.interpretRecursive(sprite);
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

	public boolean isSingleNumberFormula() {
		return formulaTree.isSingleNumberFormula();
	}

	@Override
	public Formula clone() {
		if (formulaTree != null) {
			return new Formula(formulaTree.clone());
		}

		return new Formula(0);
	}

	public void removeVariableReferences(String name, Context context) {
		internFormula.removeVariableReferences(name, context);
	}

	public void addRequiredResources(final Set<Integer> requiredResourcesSet) {
		formulaTree.addRequiredResources(requiredResourcesSet);
	}

	public String getResultForComputeDialog(Context context) {
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		ElementType type = formulaTree.getElementType();

		if (formulaTree.isLogicalOperator()) {
			boolean result;
			try {
				result = this.interpretBoolean(sprite);
			} catch (InterpretationException interpretationException) {
				return "ERROR";
			}
			int logicalFormulaResultIdentifier = result ? R.string.formula_editor_true : R.string.formula_editor_false;
			return context.getString(logicalFormulaResultIdentifier);
		} else if (type == ElementType.STRING
				|| type == ElementType.SENSOR
				|| (type == ElementType.FUNCTION
				&& (Functions.getFunctionByValue(formulaTree.getValue()) == Functions.LETTER
				|| Functions.getFunctionByValue(formulaTree.getValue()) == Functions.JOIN)
				|| Functions.getFunctionByValue(formulaTree.getValue()) == Functions.REGEX)) {
			try {
				return interpretString(sprite);
			} catch (InterpretationException interpretationException) {
				return "ERROR";
			}
		} else if (formulaTree.isUserVariableWithTypeString(sprite)) {
			DataContainer userVariables = ProjectManager.getInstance().getCurrentlyPlayingScene().getDataContainer();
			UserVariable userVariable = userVariables.getUserVariable(sprite, formulaTree.getValue());
			return (String) userVariable.getValue();
		} else {
			Double interpretationResult;
			try {
				interpretationResult = this.interpretDouble(sprite);
			} catch (InterpretationException interpretationException) {
				return "ERROR";
			}
			return String.valueOf(interpretationResult);
		}
	}

	public FormulaElement getFormulaTree() {
		return formulaTree;
	}
}
