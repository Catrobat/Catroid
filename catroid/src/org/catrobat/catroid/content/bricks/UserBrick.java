/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2014 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.UserBrickStageToken;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.UserVariablesContainer;
import org.catrobat.catroid.ui.BrickLayout;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class UserBrick extends BrickBaseType implements OnClickListener {
	private static final long serialVersionUID = 1L;
	private static final String TAG = UserBrick.class.getName();

	private UserScriptDefinitionBrick definitionBrick;
	private transient View prototypeView;

	// belonging to brick instance
	private ArrayList<UserBrickUIComponent> uiComponents;

	// belonging to stored brick
	public UserBrickUIDataArray uiDataArray;
	private int lastDataVersion = 0;
	private int userBrickId;

	public UserBrick(int userBrickId) {
		this.userBrickId = userBrickId;
		uiDataArray = new UserBrickUIDataArray();
		this.definitionBrick = new UserScriptDefinitionBrick(this, userBrickId);
		updateUIComponents(null);
	}

	public UserBrick(UserBrickUIDataArray uiData, UserScriptDefinitionBrick definitionBrick) {
		this.userBrickId = definitionBrick.getUserBrickId();
		this.uiDataArray = uiData;
		this.definitionBrick = definitionBrick;
		updateUIComponents(null);
	}

	@Override
	public int getRequiredResources() {
		return definitionBrick.getRequiredResources();
	}

	@Override
	public UserBrick copyBrickForSprite(Sprite sprite) {
		UserBrick copyBrick = clone();
		return copyBrick;
	}

	public int addUILocalizedString(Context context, int id) {
		UserBrickUIData data = new UserBrickUIData();
		data.isVariable = false;
		data.isEditModeLineBreak = false;
		data.name = context.getResources().getString(id);
		int toReturn = uiDataArray.size();
		uiDataArray.add(data);
		uiDataArray.version++;
		return toReturn;
	}

	public int addUIText(String text) {
		UserBrickUIData data = new UserBrickUIData();
		data.isVariable = false;
		data.isEditModeLineBreak = false;
		data.name = text;
		int toReturn = uiDataArray.size();
		uiDataArray.add(data);
		uiDataArray.version++;
		return toReturn;
	}

	public void addUILineBreak() {
		UserBrickUIData data = new UserBrickUIData();
		data.isVariable = false;
		data.isEditModeLineBreak = true;
		data.name = "linebreak";
		uiDataArray.add(data);
		uiDataArray.version++;
	}

	public int addUILocalizedVariable(Context context, int id) {
		UserBrickUIData data = new UserBrickUIData();
		data.isVariable = true;
		data.isEditModeLineBreak = false;
		data.name = context.getResources().getString(id);

		if (ProjectManager.getInstance().getCurrentProject() != null) {
			UserVariablesContainer variablesContainer = null;
			variablesContainer = ProjectManager.getInstance().getCurrentProject().getUserVariables();
			variablesContainer.addUserBrickUserVariableToUserBrick(userBrickId, data.name);
		}

		int toReturn = uiDataArray.size();
		uiDataArray.add(data);
		uiDataArray.version++;
		return toReturn;
	}

	public int addUIVariable(String id) {
		UserBrickUIData data = new UserBrickUIData();
		data.isVariable = true;
		data.isEditModeLineBreak = false;
		data.name = id;

		if (ProjectManager.getInstance().getCurrentProject() != null) {
			UserVariablesContainer variablesContainer = ProjectManager.getInstance().getCurrentProject().getUserVariables();
			variablesContainer.addUserBrickUserVariableToUserBrick(userBrickId, data.name);
		}

		int toReturn = uiDataArray.size();
		uiDataArray.add(data);
		uiDataArray.version++;
		return toReturn;
	}

	public void renameUIElement(String oldName, String newName, Context context) {
		UserBrickUIData variable = null;
		boolean isVariable = false;
		for (UserBrickUIData data : uiDataArray) {
			if (data.name.equals(oldName)) {
				variable = data;
				isVariable = data.isVariable;
				break;
			}
		}

		definitionBrick.renameVariablesInFormulas(oldName, newName, context);

		variable.name = newName;

		if (isVariable && ProjectManager.getInstance().getCurrentProject() != null) {
			UserVariablesContainer variablesContainer = null;
			variablesContainer = ProjectManager.getInstance().getCurrentProject().getUserVariables();
			variablesContainer.deleteUserVariableFromUserBrick(userBrickId, oldName);
			variablesContainer.addUserBrickUserVariableToUserBrick(userBrickId, newName);
		}
	}

	public void removeDataAt(int id, Context context) {
		definitionBrick.removeVariablesInFormulas(uiDataArray.get(id).name, context);
		if (uiDataArray.get(id).isVariable && ProjectManager.getInstance().getCurrentProject() != null) {
			UserVariablesContainer variablesContainer = null;
			variablesContainer = ProjectManager.getInstance().getCurrentProject().getUserVariables();
			variablesContainer.deleteUserVariableFromUserBrick(userBrickId, uiDataArray.get(id).name);
		}
		uiDataArray.remove(id);
		uiDataArray.version++;
	}

	public boolean isInstanceOf(UserBrick other) {
		return (other.uiDataArray == uiDataArray);
	}

	public Iterator<UserBrickUIComponent> getUIComponentIterator() {
		return uiComponents.iterator();
	}

	public ArrayList<UserBrickUIComponent> getUIComponents() {
		return uiComponents;
	}

	public void updateUIComponents(Context context) {
		ArrayList<UserBrickUIComponent> newUIComponents = new ArrayList<UserBrickUIComponent>();

		for (int i = 0; i < uiDataArray.size(); i++) {
			UserBrickUIComponent component = new UserBrickUIComponent();
			component.dataIndex = i;
			if (uiDataArray.get(i).isVariable) {
				component.setFormulaWithBrickField(BrickField.USER_BRICK, new Formula(0));
				component.variableName = uiDataArray.get(i).name;
			}
			newUIComponents.add(component);
		}

		if (uiComponents != null) {
			copyFormulasMatchingNames(uiComponents, newUIComponents);
		}

		uiComponents = newUIComponents;
		lastDataVersion = uiDataArray.version;
	}

	public List<Formula> getFormulas() {
		List<Formula> list = new LinkedList<Formula>();
		for (UserBrickUIComponent uiComponent : uiComponents) {
			if (uiComponent.getFormulaWithBrickField(BrickField.USER_BRICK) != null && uiComponent.variableName != null) {
				list.add(uiComponent.getFormulaWithBrickField(BrickField.USER_BRICK));
			}
		}
		return list;
	}

	public void copyFormulasMatchingNames(ArrayList<UserBrickUIComponent> from, ArrayList<UserBrickUIComponent> to) {

		for (UserBrickUIComponent fromElement : from) {
			if (fromElement.dataIndex < uiDataArray.size()) {
				UserBrickUIData fromData = uiDataArray.get(fromElement.dataIndex);
				if (fromData.isVariable) {
					for (UserBrickUIComponent toElement : to) {
						if (toElement.dataIndex < uiDataArray.size()) {
							UserBrickUIData toData = uiDataArray.get(toElement.dataIndex);
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

	/**
	 * Removes element at <b>from</b> and adds it after element at <b>to</b>
	 */
	public void reorderUIData(int from, int to) {

		if (to == -1) {
			UserBrickUIData d = uiDataArray.remove(from);
			uiDataArray.add(0, d);
		} else if (from <= to) {
			UserBrickUIData d = uiDataArray.remove(from);
			uiDataArray.add(to, d);
		} else {
			// from > to
			UserBrickUIData d = uiDataArray.remove(from);
			uiDataArray.add(to + 1, d);
		}
		uiDataArray.version++;
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
		final Brick brickInstance = this;

		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
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
		if (lastDataVersion < uiDataArray.version || uiComponents == null) {
			updateUIComponents(view.getContext());
			onLayoutChanged(view);
		}

		BrickLayout layout = (BrickLayout) view.findViewById(R.id.brick_user_flow_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);

		for (UserBrickUIComponent component : uiComponents) {
			if (component != null && component.textView != null) {
				component.textView.setTextColor(component.textView.getTextColors().withAlpha(alphaValue));
				if (component.textView.getBackground() != null) {
					component.textView.getBackground().setAlpha(alphaValue);
				}
			}
		}

		this.alphaValue = (alphaValue);
		return view;
	}

	public void onLayoutChanged(View currentView) {
		if (lastDataVersion < uiDataArray.version || uiComponents == null) {
			updateUIComponents(currentView.getContext());
		}

		boolean prototype = (currentView == prototypeView);

		Context context = currentView.getContext();

		BrickLayout layout = (BrickLayout) currentView.findViewById(R.id.brick_user_flow_layout);
		if (layout.getChildCount() > 0) {
			layout.removeAllViews();
		}

		int id = 0;
		for (UserBrickUIComponent component : uiComponents) {
			TextView currentTextView = null;
			UserBrickUIData d = uiDataArray.get(component.dataIndex);
			if (d.isEditModeLineBreak) {
				continue;
			}
			if (d.isVariable) {
				currentTextView = new EditText(context);

				if (prototype) {
					currentTextView.setTextAppearance(context, R.style.BrickPrototypeTextView);
					try {
						currentTextView.setText(String
								.valueOf(component.getFormulaWithBrickField(BrickField.USER_BRICK).interpretInteger(ProjectManager
										.getInstance().getCurrentSprite())));
					} catch (InterpretationException interpretationException) {
						Log.e(TAG, "InterpretationException!", interpretationException);
					}

				} else {
					currentTextView.setId(id);
					currentTextView.setTextAppearance(context, R.style.BrickEditText);

					component.getFormulaWithBrickField(BrickField.USER_BRICK).setTextFieldId(currentTextView.getId());
					String formulaString = component.getFormulaWithBrickField(BrickField.USER_BRICK).getDisplayString(currentTextView.getContext());
					component.getFormulaWithBrickField(BrickField.USER_BRICK).refreshTextField(currentTextView, formulaString);

					// This stuff isn't being included by the style when I use setTextAppearance.
					currentTextView.setFocusable(false);
					currentTextView.setFocusableInTouchMode(false);

					currentTextView.setOnClickListener(this);
				}
				currentTextView.setVisibility(View.VISIBLE);
			} else {
				currentTextView = new TextView(context);
				currentTextView.setTextAppearance(context, R.style.BrickText_Multiple);

				currentTextView.setText(d.name);
			}

			// This stuff isn't being included by the style when I use setTextAppearance.
			if (prototype) {
				currentTextView.setFocusable(false);
				currentTextView.setFocusableInTouchMode(false);
				currentTextView.setClickable(false);
			}

			layout.addView(currentTextView);

			if (d.newLineHint) {
				BrickLayout.LayoutParams params = (BrickLayout.LayoutParams) currentTextView.getLayoutParams();
				params.setNewLine(true);
				currentTextView.setLayoutParams(params);
			}

			if (prototype) {
				component.prototypeView = currentTextView;
			} else {
				component.textView = currentTextView;
			}
			id++;
		}
	}

	public CharSequence getName(Context context) {
		CharSequence name = "";
		for (UserBrickUIData data : uiDataArray) {
			if (!data.isVariable) {
				name = data.name;
				break;
			}
		}
		return name;
	}

	@Override
	public UserBrick clone() {
		return new UserBrick(uiDataArray, definitionBrick);
	}

	@Override
	public void onClick(View eventOrigin) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}

		for (UserBrickUIComponent c : uiComponents) {
			UserBrickUIData d = uiDataArray.get(c.dataIndex);

			if (d.isVariable && c.textView.getId() == eventOrigin.getId()) {
				FormulaEditorFragment.showFragment(view, this, c.getFormulaWithBrickField(BrickField.USER_BRICK));
			}
		}
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {

		UserBrickStageToken stageToken = getStageToken();

		ArrayList<SequenceAction> returnActionList = new ArrayList<SequenceAction>();

		SequenceAction userSequence = ExtendedActions.sequence();
		definitionBrick.getScriptSafe().run(sprite, userSequence);

		returnActionList.add(userSequence);

		sequence.addAction(ExtendedActions.userBrick(userSequence, stageToken));
		ProjectManager.getInstance().setCurrentUserBrick(this);

		return returnActionList;
	}

	private UserBrickStageToken getStageToken() {
		if (ProjectManager.getInstance() == null || ProjectManager.getInstance().getCurrentProject() == null) {
			return null;
		}

		LinkedList<UserBrickVariable> theList = new LinkedList<UserBrickVariable>();

		UserVariablesContainer variablesContainer = null;
		variablesContainer = ProjectManager.getInstance().getCurrentProject().getUserVariables();

		for (UserBrickUIComponent uiComponent : uiComponents) {

			if (uiComponent.getFormulaWithBrickField(BrickField.USER_BRICK) != null && uiComponent.dataIndex < uiDataArray.size()) {
				UserBrickUIData uiData = uiDataArray.get(uiComponent.dataIndex);
				if (uiData.isVariable) {
					List<UserVariable> variables = variablesContainer.getOrCreateVariableListForUserBrick(userBrickId);
					UserVariable variable = variablesContainer.findUserVariable(uiData.name, variables);

					if (variable == null) {
						variable = variablesContainer.addUserBrickUserVariableToUserBrick(userBrickId, uiData.name);
					}
					try {
						variable.setValue(uiComponent.getFormulaWithBrickField(BrickField.USER_BRICK).interpretDouble(ProjectManager.getInstance().getCurrentSprite()));
					} catch (InterpretationException interpretationException) {
						Log.e(TAG, "InterpretationException!", interpretationException);
					}

					theList.add(new UserBrickVariable(variable, uiComponent.getFormulaWithBrickField(BrickField.USER_BRICK)));
				}
			}
		}
		return new UserBrickStageToken(theList, userBrickId);
	}

	public int getId() {
		return userBrickId;
	}

	public void setId(int newId) {
		userBrickId = newId;
	}

	public UserScriptDefinitionBrick getDefinitionBrick() {
		return definitionBrick;
	}

	public void setDefinitionBrick(UserScriptDefinitionBrick definitionBrick) {
		this.definitionBrick = definitionBrick;
	}
}