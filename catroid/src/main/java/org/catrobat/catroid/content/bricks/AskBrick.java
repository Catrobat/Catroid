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
package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick;

import java.util.ArrayList;
import java.util.Collection;

@CatrobatLanguageBrick(command = "Ask")
public class AskBrick extends UserVariableBrickWithFormula {

	private static final String ANSWER_VARIABLE_CATLANG_PARAMETER_NAME = "answer variable";
	private static final String QUESTION_CATLANG_PARAMETER_NAME = "question";

	private static final long serialVersionUID = 1L;

	public AskBrick() {
		addAllowedBrickField(BrickField.ASK_QUESTION, R.id.brick_ask_question_edit_text, QUESTION_CATLANG_PARAMETER_NAME);
	}

	public AskBrick(String questionText) {
		this(new Formula(questionText));
	}

	public AskBrick(Formula questionFormula, UserVariable answerVariable) {
		this(questionFormula);
		userVariable = answerVariable;
	}

	public AskBrick(Formula questionFormula) {
		this();
		setFormulaWithBrickField(BrickField.ASK_QUESTION, questionFormula);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_ask;
	}

	@Override
	protected int getSpinnerId() {
		return R.id.brick_ask_spinner;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory()
				.createAskAction(sprite, sequence, getFormulaWithBrickField(BrickField.ASK_QUESTION),
						userVariable));
	}

	@Override
	protected String getUserVariableCatlangArgumentName() {
		return ANSWER_VARIABLE_CATLANG_PARAMETER_NAME;
	}

	@Override
	protected Collection<String> getRequiredCatlangArgumentNames() {
		ArrayList<String> requiredArguments = new ArrayList<>();
		requiredArguments.add(QUESTION_CATLANG_PARAMETER_NAME);
		requiredArguments.add(ANSWER_VARIABLE_CATLANG_PARAMETER_NAME);
		return requiredArguments;
	}
}
