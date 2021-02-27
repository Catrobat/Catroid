/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.sensing.CollisionDetection;

import java.util.List;

public class InternToken {

	private String tokenStringValue = "";
	private InternTokenType internTokenType;

	public InternToken(InternTokenType internTokenType) {
		this.internTokenType = internTokenType;
	}

	public InternToken(InternTokenType internTokenType, String tokenStringValue) {
		this.tokenStringValue = tokenStringValue;
		this.internTokenType = internTokenType;
	}

	public void setTokenStringValue(String tokenString) {
		this.tokenStringValue = tokenString;
	}

	public String getTokenStringValue() {
		return this.tokenStringValue;
	}

	public void updateVariableReferences(String oldName, String newName) {
		if (internTokenType == InternTokenType.USER_VARIABLE && tokenStringValue.equals(oldName)) {
			tokenStringValue = newName;
		}
	}

	public void updateListReferences(String oldName, String newName) {
		if (internTokenType == InternTokenType.USER_LIST && tokenStringValue.equals(oldName)) {
			tokenStringValue = newName;
		}
	}

	public void updateCollisionFormula(String oldName, String newName) {
		if (internTokenType == InternTokenType.COLLISION_FORMULA && tokenStringValue.equals(oldName)) {
			tokenStringValue = newName;
		}
	}

	public void updateCollisionFormulaToVersion() {
		if (internTokenType == InternTokenType.COLLISION_FORMULA) {
			Project currentProject = ProjectManager.getInstance().getCurrentProject();
			String secondSpriteName = CollisionDetection.getSecondSpriteNameFromCollisionFormulaString(tokenStringValue, currentProject);
			if (secondSpriteName != null) {
				tokenStringValue = secondSpriteName;
			}
		}
	}

	public void updateSensorTokens(String oldName, String newName) {
		if (isSensor() && tokenStringValue.equals(oldName)) {
			tokenStringValue = newName;
		}
	}

	public boolean isNumber() {
		return internTokenType == InternTokenType.NUMBER;
	}

	public boolean isOperator() {
		return internTokenType == InternTokenType.OPERATOR && Operators.isOperator(tokenStringValue);
	}

	public boolean isBracketClose() {
		return internTokenType == InternTokenType.BRACKET_CLOSE;
	}

	public boolean isFunctionParameterBracketOpen() {
		return internTokenType == InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN;
	}

	public boolean isFunctionParameterBracketClose() {
		return internTokenType == InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE;
	}

	public boolean isFunctionParameterDelimiter() {
		return internTokenType == InternTokenType.FUNCTION_PARAMETER_DELIMITER;
	}

	public boolean isFunctionName() {
		return internTokenType == InternTokenType.FUNCTION_NAME;
	}

	public boolean isSensor() {
		return internTokenType == InternTokenType.SENSOR;
	}

	public boolean isEndOfFileToken() {
		return internTokenType == InternTokenType.PARSER_END_OF_FILE;
	}

	public boolean isUserVariable() {
		return internTokenType == InternTokenType.USER_VARIABLE;
	}

	public boolean isUserList() {
		return internTokenType == InternTokenType.USER_LIST;
	}

	public boolean isString() {
		return internTokenType == InternTokenType.STRING;
	}

	public void appendToTokenStringValue(String stringToAppend) {
		this.tokenStringValue += stringToAppend;
	}

	public void appendToTokenStringValue(List<InternToken> internTokensToAppend) {
		StringBuilder stringBuilder = new StringBuilder(this.tokenStringValue);
		for (InternToken internToken : internTokensToAppend) {
			stringBuilder.append(internToken.tokenStringValue);
		}
		this.tokenStringValue = stringBuilder.toString();
	}

	public InternTokenType getInternTokenType() {
		return this.internTokenType;
	}

	public InternToken deepCopy() {
		return new InternToken(internTokenType, tokenStringValue);
	}
}
