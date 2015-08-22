/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.BrickLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UserBrick extends BrickBaseType implements OnClickListener {
	private static final long serialVersionUID = 1L;
	private static final String TAG = UserBrick.class.getName();

	@XStreamAlias("definitionBrick")
	private UserScriptDefinitionBrick definitionBrick;
	private transient View prototypeView;

	// belonging to brick instance
	@XStreamAlias("userBrickParameters")
	private ArrayList<UserBrickParameter> userBrickParameters;

	@XStreamAlias("userBrickPositionToParameter")
	private ArrayList<Pair<Integer, Integer>> userBrickPositionToParameter;

	// belonging to stored brick
	private transient int lastDataVersion = 0;

	@XStreamAlias("userBrickId")
	private int userBrickId;

	public UserBrick(int userBrickId) {
		this.userBrickId = userBrickId;
		this.definitionBrick = new UserScriptDefinitionBrick(this);
		//first = first occurance of this Userbrick in script, second = parameterindex of this occurence of this Userbrick in script
		userBrickPositionToParameter = new ArrayList<Pair<Integer, Integer>>();
		updateUserBrickParameters(null);
	}

	public UserBrick(UserScriptDefinitionBrick definitionBrick, Sprite sprite) {
		this.definitionBrick = definitionBrick;
		this.userBrickId = sprite.getUserBrickList().size() - 1;
		definitionBrick.setUserBrick(this);
		userBrickPositionToParameter = new ArrayList<Pair<Integer, Integer>>();
		updateUserBrickParameters(null);
	}

	@Override
	public int getRequiredResources() {
		return definitionBrick.getRequiredResources();
	}

	@Override
	public UserBrick copyBrickForSprite(Sprite sprite) {
		UserBrick copyBrick = new UserBrick(definitionBrick, sprite);
		return copyBrick;
	}

	public boolean isInstanceOf(UserBrick other) {
		return (other.getUserScriptDefinitionBrickElements() == getUserScriptDefinitionBrickElements());
	}

	public ArrayList<UserBrickParameter> getUserBrickParameters() {
		return userBrickParameters;
	}

	public void updateUserBrickParameters(ArrayList<UserBrickParameter> userBrickParameterList) {
		ArrayList<UserBrickParameter> newParameters = new ArrayList<UserBrickParameter>();

		for (int elementPosition = 0; elementPosition < getUserScriptDefinitionBrickElements().getUserScriptDefinitionBrickElementList().size(); elementPosition++) {
			UserBrickParameter parameter = new UserBrickParameter(this);
			parameter.parameterIndex = elementPosition;
			if (getUserScriptDefinitionBrickElements().getUserScriptDefinitionBrickElementList().get(elementPosition).isVariable) {
				if (userBrickParameterList == null) {
					parameter.setFormulaWithBrickField(BrickField.USER_BRICK, new Formula(0));
				} else {
					parameter.setFormulaWithBrickField(BrickField.USER_BRICK, userBrickParameterList.get(elementPosition).getFormulaWithBrickField(BrickField.USER_BRICK));
				}
				parameter.variableName = getUserScriptDefinitionBrickElements().getUserScriptDefinitionBrickElementList().get(elementPosition).name;
			}
			newParameters.add(parameter);
		}

		if (userBrickParameters != null) {
			copyFormulasMatchingNames(userBrickParameters, newParameters);
		}

		userBrickParameters = newParameters;
		lastDataVersion = getUserScriptDefinitionBrickElements().getVersion();
	}

	public List<Formula> getFormulas() {
		List<Formula> formulaList = new LinkedList<Formula>();
		for (UserBrickParameter parameter : userBrickParameters) {
			if (parameter.getFormulaWithBrickField(BrickField.USER_BRICK) != null && parameter.variableName != null) {
				formulaList.add(parameter.getFormulaWithBrickField(BrickField.USER_BRICK));
			}
		}
		return formulaList;
	}

	public void copyFormulasMatchingNames(ArrayList<UserBrickParameter> from, ArrayList<UserBrickParameter> to) {
		for (UserBrickParameter fromElement : from) {
			UserScriptDefinitionBrickElements elements = getUserScriptDefinitionBrickElements();
			if (fromElement.parameterIndex < elements.getUserScriptDefinitionBrickElementList().size()) {
				UserScriptDefinitionBrickElement fromData = elements.getUserScriptDefinitionBrickElementList().get(fromElement.parameterIndex);
				if (fromData.isVariable) {
					for (UserBrickParameter toElement : to) {
						if (toElement.parameterIndex < elements.getUserScriptDefinitionBrickElementList().size()) {
							UserScriptDefinitionBrickElement toData = elements.getUserScriptDefinitionBrickElementList().get(toElement.parameterIndex);
							if (fromData.name.equals(toData.name)) {
								toElement.setFormulaWithBrickField(BrickField.USER_BRICK, fromElement.getFormulaWithBrickField(BrickField.USER_BRICK).clone());
								toElement.variableName = toData.name;
							}
						}
					}
				}
			}
		}
	}

	public void appendBrickToScript(Brick brick) {
		definitionBrick.appendBrickToScript(brick);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_user, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_user_checkbox);
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(UserBrick.this, isChecked);
			}
		});
		onLayoutChanged(view);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_user, null);
		onLayoutChanged(prototypeView);
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		if (view == null) {
			return null;
		}
		if (lastDataVersion < getUserScriptDefinitionBrickElements().getVersion() || userBrickParameters == null) {
			updateUserBrickParameters(null);
			onLayoutChanged(view);
		}

		BrickLayout layout = (BrickLayout) view.findViewById(R.id.brick_user_flow_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);

		for (UserBrickParameter component : userBrickParameters) {
			if (component != null && component.textView != null) {
				component.textView.setTextColor(component.textView.getTextColors().withAlpha(alphaValue));
				if (component.textView.getBackground() != null) {
					component.textView.getBackground().setAlpha(alphaValue);
				}
			}
		}

		this.alphaValue = alphaValue;
		return view;
	}

	public void onLayoutChanged(View currentView) {
		if (lastDataVersion < getUserScriptDefinitionBrickElements().getVersion() || userBrickParameters == null) {
			updateUserBrickParameters(null);
		}

		boolean prototype = (currentView == prototypeView);

		Context context = currentView.getContext();

		BrickLayout layout = (BrickLayout) currentView.findViewById(R.id.brick_user_flow_layout);
		if (layout.getChildCount() > 0) {
			layout.removeAllViews();
		}

		int id = 0;
		for (UserBrickParameter parameter : userBrickParameters) {
			TextView currentTextView;
			UserScriptDefinitionBrickElement uiData = getUserScriptDefinitionBrickElements().getUserScriptDefinitionBrickElementList().get(parameter.parameterIndex);
			if (uiData.isEditModeLineBreak) {
				continue;
			}
			if (uiData.isVariable) {
				currentTextView = new EditText(context);

				if (prototype) {
					currentTextView.setTextAppearance(context, R.style.BrickPrototypeTextView);
					try {
						currentTextView.setText(String
								.valueOf(parameter.getFormulaWithBrickField(BrickField.USER_BRICK).interpretInteger(ProjectManager
										.getInstance().getCurrentSprite())));
					} catch (InterpretationException interpretationException) {
						Log.e(TAG, "InterpretationException!", interpretationException);
					}
				} else {
					currentTextView.setId(id);
					currentTextView.setTextAppearance(context, R.style.BrickEditText);

					parameter.getFormulaWithBrickField(BrickField.USER_BRICK).setTextFieldId(currentTextView.getId());
					String formulaString = parameter.getFormulaWithBrickField(BrickField.USER_BRICK).getDisplayString(currentTextView.getContext());
					parameter.getFormulaWithBrickField(BrickField.USER_BRICK).refreshTextField(currentTextView, formulaString);

					// This stuff isn't being included by the style when I use setTextAppearance.
					currentTextView.setFocusable(false);
					currentTextView.setFocusableInTouchMode(false);

					currentTextView.setOnClickListener(this);
				}
				currentTextView.setVisibility(View.VISIBLE);
			} else {
				currentTextView = new TextView(context);
				currentTextView.setTextAppearance(context, R.style.BrickText_Multiple);

				currentTextView.setText(uiData.name);
			}

			// This stuff isn't being included by the style when I use setTextAppearance.
			if (prototype) {
				currentTextView.setFocusable(false);
				currentTextView.setFocusableInTouchMode(false);
				currentTextView.setClickable(false);
			}

			layout.addView(currentTextView);

			if (uiData.newLineHint) {
				BrickLayout.LayoutParams params = (BrickLayout.LayoutParams) currentTextView.getLayoutParams();
				params.setNewLine(true);
				currentTextView.setLayoutParams(params);
			}

			if (prototype) {
				parameter.prototypeView = currentTextView;
			} else {
				parameter.textView = currentTextView;
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
			UserScriptDefinitionBrickElement userBrickElement = getUserScriptDefinitionBrickElements()
					.getUserScriptDefinitionBrickElementList().get(userBrickParameter.parameterIndex);

			if (userBrickElement.isVariable && userBrickParameter.textView.getId() == eventOrigin.getId()) {
				userBrickParameter.showFormulaEditorToEditFormula(view);
			}
		}
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {

		ArrayList<SequenceAction> returnActionList = new ArrayList<SequenceAction>();

		SequenceAction userSequence = ExtendedActions.sequence();
		definitionBrick.getScriptSafe().run(sprite, userSequence);
		returnActionList.add(userSequence);
		sequence.addAction(ExtendedActions.userBrick(userSequence));
		ProjectManager.getInstance().setCurrentUserBrick(this);

		return returnActionList;
	}

	public UserScriptDefinitionBrick getDefinitionBrick() {
		return definitionBrick;
	}

	public void setDefinitionBrick(UserScriptDefinitionBrick definitionBrick) {
		this.definitionBrick = definitionBrick;
	}

	public UserScriptDefinitionBrickElements getUserScriptDefinitionBrickElements() {
		return definitionBrick.getUserScriptDefinitionBrickElements();
	}

	public void setUserScriptDefinitionBrickElements(UserScriptDefinitionBrickElements elements) {
		definitionBrick.setUserScriptDefinitionBrickElements(elements);
	}

	public int getUserBrickId() {
		return userBrickId;
	}

	public void setUserBrickId(int userBrickId) {
		this.userBrickId = userBrickId;
	}

	public ArrayList<Pair<Integer, Integer>> getUserBrickPositionToParameter() {
		return userBrickPositionToParameter;
	}

	public void setUserBrickPositionToParameter(Pair<Integer, Integer> userBrickPositionToParameterPair, int index) {
		userBrickPositionToParameter.set(index, userBrickPositionToParameterPair);
	}

	public void addUserBrickPositionToParameter(Pair<Integer, Integer> userBrickPositionToParameterPair) {
		userBrickPositionToParameter.add(userBrickPositionToParameterPair);
	}

	public int getUserBrickIndexInScript(Pair<Integer, Integer> userBrickPositionToParameterPair) {
		return userBrickPositionToParameter.indexOf(userBrickPositionToParameterPair);
	}
}
