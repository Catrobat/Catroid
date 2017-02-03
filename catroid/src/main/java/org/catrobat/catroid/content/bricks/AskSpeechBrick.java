/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InternToExternGenerator;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.adapter.DataAdapter;
import org.catrobat.catroid.ui.adapter.UserVariableAdapterWrapper;
import org.catrobat.catroid.ui.dialogs.NewDataDialog;
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
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createAskSpeechAction(sprite,
				getFormulaWithBrickField(BrickField.ASK_SPEECH_QUESTION), userVariable));
		return Collections.emptyList();
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_ask_speech, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);
		setCheckboxView(R.id.brick_ask_speech_checkbox);

		TextView textField = (TextView) view.findViewById(R.id.brick_ask_speech_question_edit_text);

		getFormulaWithBrickField(BrickField.ASK_SPEECH_QUESTION).setTextFieldId(R.id.brick_ask_speech_question_edit_text);
		getFormulaWithBrickField(BrickField.ASK_SPEECH_QUESTION).refreshTextField(view);
		textField.setOnClickListener(this);

		Spinner variableSpinner = (Spinner) view.findViewById(R.id.brick_ask_speech_spinner);

		UserBrick currentBrick = ProjectManager.getInstance().getCurrentUserBrick();

		DataAdapter dataAdapter = ProjectManager.getInstance().getSceneToPlay().getDataContainer()
				.createDataAdapter(context, currentBrick, ProjectManager.getInstance().getCurrentSprite());
		UserVariableAdapterWrapper userVariableAdapterWrapper = new UserVariableAdapterWrapper(context,
				dataAdapter);
		userVariableAdapterWrapper.setItemLayout(android.R.layout.simple_spinner_item, android.R.id.text1);

		variableSpinner.setAdapter(userVariableAdapterWrapper);

		setSpinnerSelection(variableSpinner, null);

		variableSpinner.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN
						&& (((Spinner) view).getSelectedItemPosition() == 0
						&& ((Spinner) view).getAdapter().getCount() == 1)) {
					NewDataDialog dialog = new NewDataDialog((Spinner) view, NewDataDialog.DialogType.USER_VARIABLE);
					dialog.addVariableDialogListener(AskSpeechBrick.this);
					dialog.show(((Activity) view.getContext()).getFragmentManager(),
							NewDataDialog.DIALOG_FRAGMENT_TAG);
					return true;
				}

				return false;
			}
		});
		variableSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0 && ((UserVariableAdapterWrapper) parent.getAdapter()).isTouchInDropDownView()) {
					NewDataDialog dialog = new NewDataDialog((Spinner) parent, NewDataDialog.DialogType.USER_VARIABLE);
					dialog.addVariableDialogListener(AskSpeechBrick.this);
					int spinnerPos = ((UserVariableAdapterWrapper) parent.getAdapter())
							.getPositionOfItem(userVariable);
					dialog.setUserVariableIfCancel(spinnerPos);
					dialog.show(((Activity) view.getContext()).getFragmentManager(),
							NewDataDialog.DIALOG_FRAGMENT_TAG);
				}
				((UserVariableAdapterWrapper) parent.getAdapter()).resetIsTouchInDropDownView();
				userVariable = (UserVariable) parent.getItemAtPosition(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				userVariable = null;
			}
		});
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_ask_speech, null);
		Spinner variableSpinner = (Spinner) prototypeView.findViewById(R.id.brick_ask_speech_spinner);
		UserBrick currentBrick = ProjectManager.getInstance().getCurrentUserBrick();

		DataAdapter dataAdapter = ProjectManager.getInstance().getSceneToPlay().getDataContainer()
				.createDataAdapter(context, currentBrick, ProjectManager.getInstance().getCurrentSprite());

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
	public AskSpeechBrick copyBrickForSprite(Sprite sprite) {
		AskSpeechBrick copyBrick = clone();
		if (userVariable != null) {
			copyBrick.userVariable = userVariable;
		}

		return copyBrick;
	}

	@Override
	public AskSpeechBrick clone() {
		AskSpeechBrick clonedBrick = new AskSpeechBrick(getFormulaWithBrickField(BrickField.ASK_SPEECH_QUESTION)
				.clone(), userVariable);
		clonedBrick.setBackPackedData(backPackedData);
		return clonedBrick;
	}

	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.ASK_SPEECH_QUESTION);
	}

	@Override
	public void updateReferenceAfterMerge(Scene into, Scene from) {
		updateUserVariableReference(into, from);
	}
}
