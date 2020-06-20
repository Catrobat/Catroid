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
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.utils.EnumUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Set;

import static org.catrobat.catroid.utils.NumberFormats.trimTrailingCharacters;

public class Formula implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String ERROR_STRING = "ERROR";
	private FormulaElement formulaTree;

	private transient InternFormula internFormula = null;

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
		formulaTree.updateCollisionFormula(oldName, newName);
	}

	public void updateCollisionFormulasToVersion() {
		internFormula.updateCollisionFormulaToVersion(CatroidApplication.getAppContext());
		formulaTree.updateCollisionFormulaToVersion(ProjectManager.getInstance().getCurrentProject());
	}

	public void updateVariableName(String oldName, String newName) {
		internFormula.updateVariableReferences(oldName, newName, CatroidApplication.getAppContext());
		formulaTree.updateVariableReferences(oldName, newName);
	}

	public void updateUserlistName(String oldName, String newName) {
		internFormula.updateListReferences(oldName, newName, CatroidApplication.getAppContext());
		formulaTree.updateListName(oldName, newName);
	}

	public boolean containsSpriteInCollision(String name) {
		return formulaTree.containsSpriteInCollision(name);
	}

	public Integer interpretInteger(Sprite sprite) throws InterpretationException {
		return interpretDouble(sprite).intValue();
	}

	@NotNull
	private String tryInterpretDouble(Sprite sprite) {
		try {
			return String.valueOf(interpretDouble(sprite));
		} catch (InterpretationException interpretationException) {
			return ERROR_STRING;
		}
	}

	public Double interpretDouble(Sprite sprite) throws InterpretationException {
		try {
			return assertNotNaN(interpretDoubleInternal(sprite));
		} catch (ClassCastException | NumberFormatException exception) {
			throw new InterpretationException("Couldn't interpret Formula.", exception);
		}
	}

	@NotNull
	private Double interpretDoubleInternal(Sprite sprite) {
		Object o = formulaTree.interpretRecursive(sprite);
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

	public Float interpretFloat(Sprite sprite) throws InterpretationException {
		return interpretDouble(sprite).floatValue();
	}

	public String interpretString(Sprite sprite) throws InterpretationException {
		Object interpretation = formulaTree.interpretRecursive(sprite);

		if (interpretation instanceof Double && ((Double) interpretation).isNaN()) {
			throw new InterpretationException("NaN in interpretString()");
		}

		String value = String.valueOf(interpretation);
		return trimTrailingCharacters(value);
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

	public String getResultForComputeDialog(StringProvider stringProvider, Sprite sprite) {
		ElementType type = formulaTree.getElementType();
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		if (formulaTree.isLogicalOperator()) {
			return tryInterpretBooleanToString(stringProvider, sprite);
		} else if (isStringInterpretableType(type, formulaTree.getValue())) {
			return tryInterpretString(sprite);
		} else if (isVariableWithTypeString(sprite, currentProject)) {
			return interpretUserVariable(currentProject, currentSprite);
		} else {
			return tryInterpretDouble(sprite);
		}
	}

	private boolean isStringInterpretableType(ElementType type, String formulaValue) {
		return type == ElementType.STRING
				|| type == ElementType.SENSOR
				|| type == ElementType.FUNCTION && isInterpretableFunction(formulaValue);
	}

	private boolean isInterpretableFunction(String formulaValue) {
		Functions function = EnumUtils.getEnum(Functions.class, formulaValue);
		return function == Functions.LETTER || function == Functions.JOIN || function == Functions.REGEX;
	}

	private boolean isVariableWithTypeString(Sprite sprite, Project currentProject) {
		if (formulaTree.getElementType() == ElementType.USER_VARIABLE) {
			UserVariable userVariable = UserDataWrapper.getUserVariable(formulaTree.getValue(), sprite, currentProject);
			return userVariable.getValue() instanceof String;
		} else {
			return false;
		}
	}

	private String interpretUserVariable(Project currentProject, Sprite currentSprite) {
		UserVariable userVariable = UserDataWrapper
				.getUserVariable(formulaTree.getValue(), currentSprite, currentProject);
		return (String) userVariable.getValue();
	}

	private String tryInterpretString(Sprite sprite) {
		try {
			return interpretString(sprite);
		} catch (InterpretationException interpretationException) {
			return ERROR_STRING;
		}
	}

	private String tryInterpretBooleanToString(StringProvider stringProvider, Sprite sprite) {
		try {
			return interpretBooleanToString(sprite, stringProvider);
		} catch (InterpretationException interpretationException) {
			return ERROR_STRING;
		}
	}

	public String interpretBooleanToString(Sprite sprite, StringProvider stringProvider) throws InterpretationException {
		boolean booleanValue = interpretBoolean(sprite);
		return toLocalizedString(booleanValue, stringProvider);
	}

	public boolean interpretBoolean(Sprite sprite) throws InterpretationException {
		return interpretDouble(sprite).intValue() != 0;
	}

	private String toLocalizedString(boolean value, StringProvider stringProvider) {
		return value ? stringProvider.getTrue() : stringProvider.getFalse();
	}

	public interface StringProvider {
		String getTrue();
		String getFalse();
	}
}
