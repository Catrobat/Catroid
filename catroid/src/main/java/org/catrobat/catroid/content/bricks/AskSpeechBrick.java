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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InternToExternGenerator;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.adapter.DataAdapter;
import org.catrobat.catroid.ui.adapter.UserVariableAdapterWrapper;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.Collections;
import java.util.List;

public class AskSpeechBrick extends UserVariableBrick {

	private static final long serialVersionUID = 1L;

	private transient String defaultPrototypeToken = null;

	public AskSpeechBrick(Formula questionFormula, UserVariable answerVariable) {
		this.userVariable = answerVariable;
		initializeBrickFields(questionFormula);
	}

	public AskSpeechBrick(String questionText) {
		this.userVariable = null;
		initializeBrickFields(new Formula(questionText));
	}

	private void initializeBrickFields(Formula questionFormula) {
		addAllowedBrickField(BrickField.ASK_SPEECH_QUESTION);
		setFormulaWithBrickField(BrickField.ASK_SPEECH_QUESTION, questionFormula);
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.ASK_SPEECH_QUESTION).getRequiredResources()
				| Brick.NETWORK_CONNECTION;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createAskSpeechAction(sprite,
				getFormulaWithBrickField(BrickField.ASK_SPEECH_QUESTION), userVariable));
		return Collections.emptyList();
	}

	@Override
	protected int getLayoutRes() {
		return R.layout.brick_ask_speech;
	}

	@Override
	public View getView(final Context context, BrickAdapter brickAdapter) {
		super.getView(context, brickAdapter);

		TextView textField = (TextView) view.findViewById(R.id.brick_ask_speech_question_edit_text);

		getFormulaWithBrickField(BrickField.ASK_SPEECH_QUESTION).setTextFieldId(R.id.brick_ask_speech_question_edit_text);
		getFormulaWithBrickField(BrickField.ASK_SPEECH_QUESTION).refreshTextField(view);
		textField.setOnClickListener(this);

		Spinner variableSpinner = (Spinner) view.findViewById(R.id.brick_ask_speech_spinner);

		DataAdapter dataAdapter = ProjectManager.getInstance().getCurrentlyPlayingScene().getDataContainer()
				.createDataAdapter(context, ProjectManager.getInstance().getCurrentSprite());
		UserVariableAdapterWrapper userVariableAdapterWrapper = new UserVariableAdapterWrapper(context,
				dataAdapter);
		userVariableAdapterWrapper.setItemLayout(android.R.layout.simple_spinner_item, android.R.id.text1);

		variableSpinner.setAdapter(userVariableAdapterWrapper);

		setSpinnerSelection(variableSpinner, null);

		variableSpinner.setOnTouchListener(createSpinnerOnTouchListener());
		variableSpinner.setOnItemSelectedListener(createVariableSpinnerItemSelectedListener());
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = super.getPrototypeView(context);
		Spinner variableSpinner = (Spinner) prototypeView.findViewById(R.id.brick_ask_speech_spinner);

		DataAdapter dataAdapter = ProjectManager.getInstance().getCurrentlyPlayingScene().getDataContainer()
				.createDataAdapter(context, ProjectManager.getInstance().getCurrentSprite());

		UserVariableAdapterWrapper userVariableAdapterWrapper = new UserVariableAdapterWrapper(context,
				dataAdapter);

		userVariableAdapterWrapper.setItemLayout(android.R.layout.simple_spinner_item, android.R.id.text1);
		variableSpinner.setAdapter(userVariableAdapterWrapper);
		setSpinnerSelection(variableSpinner, null);

		TextView textSetVariable = (TextView) prototypeView.findViewById(R.id.brick_ask_speech_question_edit_text);

		if (defaultPrototypeToken != null) {
			int defaultValueId = InternToExternGenerator.getMappedString(defaultPrototypeToken);
			textSetVariable.setText(context.getText(defaultValueId));
		} else {
			textSetVariable.setText(context.getString(R.string.brick_ask_speech_default_question));
		}
		return prototypeView;
	}

	@Override
	public void onNewVariable(UserVariable userVariable) {
		Spinner spinner = view.findViewById(R.id.brick_ask_speech_spinner);
		setSpinnerSelection(spinner, userVariable);
	}

	@Override
	public AskSpeechBrick clone() {
		return new AskSpeechBrick(getFormulaWithBrickField(BrickField.ASK_SPEECH_QUESTION).clone(), userVariable);
	}

	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.ASK_SPEECH_QUESTION);
	}
}
