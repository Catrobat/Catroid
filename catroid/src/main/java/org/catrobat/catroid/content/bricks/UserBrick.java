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

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.BrickLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UserBrick extends BrickBaseType implements OnClickListener {
	private static final long serialVersionUID = 1L;
	private static final String TAG = UserBrick.class.getSimpleName();

	@XStreamAlias("definitionBrick")
	private UserScriptDefinitionBrick definitionBrick;
	private transient View prototypeView;

	@XStreamAlias("userBrickParameters")
	private List<UserBrickParameter> userBrickParameters = new ArrayList<>();

	public UserBrick() {
		this.definitionBrick = new UserScriptDefinitionBrick();
	}

	public UserBrick(UserScriptDefinitionBrick definitionBrick) {
		this.definitionBrick = definitionBrick;
	}

	@Override
	public int getRequiredResources() {
		return definitionBrick.getRequiredResources();
	}

	@Override
	public UserBrick copyBrickForSprite(Sprite sprite) {
		UserBrick clonedBrick = (UserBrick) clone();
		clonedBrick.definitionBrick = (UserScriptDefinitionBrick) definitionBrick.copyBrickForSprite(sprite);
		return clonedBrick;
	}

	@Override
	public Brick clone() {
		animationState = false;
		UserBrick clonedUserBrick = new UserBrick(definitionBrick);
		clonedUserBrick.userBrickParameters = new ArrayList<>();
		if (userBrickParameters != null) {
			for (int position = 0; position < userBrickParameters.size(); position++) {
				UserBrickParameter userBrickParameter = userBrickParameters.get(position);
				clonedUserBrick.userBrickParameters.add(userBrickParameter.clone());
			}
		}
		return clonedUserBrick;
	}

	public List<UserBrickParameter> getUserBrickParameters() {
		return userBrickParameters;
	}

	private UserBrickParameter getUserBrickParameterByUserBrickElement(UserScriptDefinitionBrickElement element) {
		if (userBrickParameters == null) {
			return null;
		}
		for (UserBrickParameter parameter : userBrickParameters) {
			if (parameter.getElement().equals(element)) {
				return parameter;
			}
		}
		return null;
	}

	public void updateUserBrickParametersAndVariables() {
		updateUserBrickParameters();
		updateUserVariableValues();
	}

	public void updateUserBrickParameters() {
		List<UserBrickParameter> newParameters = new ArrayList<>();
		List<UserScriptDefinitionBrickElement> elements = getUserScriptDefinitionBrickElements();

		for (UserScriptDefinitionBrickElement element : elements) {
			if (!element.isVariable()) {
				continue;
			}
			UserBrickParameter parameter = getUserBrickParameterByUserBrickElement(element);
			if (parameter == null) {
				parameter = new UserBrickParameter(this, element);
				parameter.setFormulaWithBrickField(BrickField.USER_BRICK, new Formula(0));
			}
			newParameters.add(parameter);
		}

		if (userBrickParameters != null) {
			copyFormulasMatchingNames(userBrickParameters, newParameters);
		}

		userBrickParameters = newParameters;
	}

	public void copyFormulasMatchingNames(List<UserBrickParameter> originalParameters, List<UserBrickParameter> copiedParameters) {
		for (UserBrickParameter originalParameter : originalParameters) {
			UserScriptDefinitionBrickElement originalElement = originalParameter.getElement();
			if (!originalElement.isVariable()) {
				return;
			}

			for (UserBrickParameter copiedParameter : copiedParameters) {
				UserScriptDefinitionBrickElement copiedElement = copiedParameter.getElement();
				if (originalElement.equals(copiedElement)) {
					Formula formula = originalParameter.getFormulaWithBrickField(BrickField.USER_BRICK);
					copiedParameter.setFormulaWithBrickField(BrickField.USER_BRICK, formula.clone());
				}
			}
		}
	}

	private void updateUserVariableValues() {
		DataContainer dataContainer = ProjectManager.getInstance().getCurrentScene().getDataContainer();
		List<UserVariable> variables = new ArrayList<>();

		for (UserBrickParameter userBrickParameter : userBrickParameters) {
			UserScriptDefinitionBrickElement element = userBrickParameter.getElement();
			if (element != null) {
				List<Formula> formulas = userBrickParameter.getFormulas();
				Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
				try {
					for (Formula formula : formulas) {
						variables.add(new UserVariable(element.getText(), formula.interpretDouble(sprite)));
					}
				} catch (InterpretationException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}

		if (variables.isEmpty()) {
			return;
		}

		dataContainer.setUserBrickVariables(this, variables);
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		if (currentSprite != null) {
			currentSprite.updateUserVariableReferencesInUserVariableBricks(variables);
		}
	}

	public List<Formula> getFormulas() {
		List<Formula> formulaList = new LinkedList<>();
		for (UserBrickParameter parameter : userBrickParameters) {
			UserScriptDefinitionBrickElement element = parameter.getElement();
			Formula formula = parameter.getFormulaWithBrickField(BrickField.USER_BRICK);
			if (formula != null && element != null && element.isVariable()) {
				formulaList.add(formula);
			}
		}
		return formulaList;
	}

	public void appendBrickToScript(Brick brick) {
		definitionBrick.appendBrickToScript(brick);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		setUserBrickParametersParent();

		view = View.inflate(context, R.layout.brick_user, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_user_checkbox);
		onLayoutChanged(view);

		return view;
	}

	private void setUserBrickParametersParent() {
		if (userBrickParameters != null) {
			for (UserBrickParameter parameter : userBrickParameters) {
				parameter.setParent(this);
			}
		}
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_user, null);
		onLayoutChanged(prototypeView);
		return prototypeView;
	}

	public void onLayoutChanged(View currentView) {
		boolean prototype = (currentView == prototypeView);

		Context context = currentView.getContext();

		BrickLayout layout = (BrickLayout) currentView.findViewById(R.id.brick_user_flow_layout);
		if (layout.getChildCount() > 0) {
			layout.removeAllViews();
		}

		int id = 0;
		for (UserScriptDefinitionBrickElement element : getUserScriptDefinitionBrickElements()) {
			TextView currentEditText;
			if (element.isLineBreak()) {
				continue;
			} else if (element.isVariable()) {
				UserBrickParameter parameter = getUserBrickParameterByUserBrickElement(element);
				currentEditText = new EditText(context);

				currentEditText.setId(id);
				currentEditText.setTextAppearance(context, R.style.BrickEditText);

				if (parameter != null) {
					parameter.getFormulaWithBrickField(BrickField.USER_BRICK).setTextFieldId(currentEditText.getId());
					String formulaString = parameter.getFormulaWithBrickField(BrickField.USER_BRICK).getDisplayString(currentEditText.getContext());
					parameter.getFormulaWithBrickField(BrickField.USER_BRICK).refreshTextField(currentEditText, formulaString);
				}

				// This stuff isn't being included by the style when I use setTextAppearance.
				currentEditText.setFocusable(false);
				currentEditText.setFocusableInTouchMode(false);

				currentEditText.setOnClickListener(this);

				currentEditText.setVisibility(View.VISIBLE);
				if (parameter != null) {
					if (prototype) {
						parameter.setPrototypeView(currentEditText);
					} else {
						parameter.setTextView(currentEditText);
					}
				}
			} else {
				currentEditText = new TextView(context);
				currentEditText.setTextAppearance(context, R.style.BrickText_Multiple);

				currentEditText.setText(element.getText());
			}

			// This stuff isn't being included by the style when I use setTextAppearance.
			if (prototype) {
				currentEditText.setFocusable(false);
				currentEditText.setFocusableInTouchMode(false);
				currentEditText.setClickable(false);
			}

			layout.addView(currentEditText);

			if (element.isNewLineHint()) {
				BrickLayout.LayoutParams params = (BrickLayout.LayoutParams) currentEditText.getLayoutParams();
				params.setNewLine(true);
				currentEditText.setLayoutParams(params);
			}
			id++;
		}
	}

	@Override
	public void onClick(View eventOrigin) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}

		for (UserBrickParameter userBrickParameter : userBrickParameters) {
			int currentUserBrickParameterIndex = userBrickParameter.getTextView().getId();
			int clickedUserBrickParameterIndex = eventOrigin.getId();
			if (currentUserBrickParameterIndex == clickedUserBrickParameterIndex) {
				userBrickParameter.showFormulaEditorToEditFormula(view);
			}
		}
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		updateUserVariableValues();
		List<SequenceAction> returnActionList = new ArrayList<>();

		ActionFactory actionFactory = sprite.getActionFactory();
		SequenceAction userSequence = (SequenceAction) actionFactory.createSequence();
		definitionBrick.getScriptSafe().run(sprite, userSequence);
		returnActionList.add(userSequence);
		sequence.addAction(actionFactory.createUserBrickAction(userSequence, this));
		ProjectManager.getInstance().setCurrentUserBrick(this);

		if (sprite.isClone) {
			sprite.addUserBrick(this);
		}

		return returnActionList;
	}

	public UserScriptDefinitionBrick getDefinitionBrick() {
		return definitionBrick;
	}

	public void setDefinitionBrick(UserScriptDefinitionBrick definitionBrick) {
		this.definitionBrick = definitionBrick;
	}

	public List<UserScriptDefinitionBrickElement> getUserScriptDefinitionBrickElements() {
		return definitionBrick.getUserScriptDefinitionBrickElements();
	}

	@Override
	public void storeDataForBackPack(Sprite sprite) {
		definitionBrick = (UserScriptDefinitionBrick) definitionBrick.copyBrickForSprite(sprite);
		for (Brick brick : definitionBrick.getUserScript().getBrickList()) {
			brick.storeDataForBackPack(sprite);
		}
	}
}
