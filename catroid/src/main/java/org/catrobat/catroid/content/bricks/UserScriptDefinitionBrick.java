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
import android.view.View.OnClickListener;
import android.widget.CheckBox;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.ui.fragment.UserBrickElementEditorFragment;

import java.util.ArrayList;
import java.util.List;

public class UserScriptDefinitionBrick extends BrickBaseType implements ScriptBrick, OnClickListener {

	private static final long serialVersionUID = 1L;
	private static final String TAG = UserScriptDefinitionBrick.class.getSimpleName();
	private static final String LINE_BREAK = "linebreak";

	private StartScript script;

	@XStreamAlias("userBrickElements")
	private List<UserScriptDefinitionBrickElement> userScriptDefinitionBrickElements;

	public UserScriptDefinitionBrick() {
		this.script = new StartScript();
		this.userScriptDefinitionBrickElements = new ArrayList<>();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UserScriptDefinitionBrick)) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		UserScriptDefinitionBrick definitionBrick = (UserScriptDefinitionBrick) obj;
		List<UserScriptDefinitionBrickElement> elements = definitionBrick.getUserScriptDefinitionBrickElements();

		if (userScriptDefinitionBrickElements.size() != elements.size()) {
			return false;
		}

		for (int elementPosition = 0; elementPosition < userScriptDefinitionBrickElements.size(); elementPosition++) {
			UserScriptDefinitionBrickElement elementToCompare = elements.get(elementPosition);
			UserScriptDefinitionBrickElement element = userScriptDefinitionBrickElements.get(elementPosition);
			if (!(elementToCompare.equals(element))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return super.hashCode() * TAG.hashCode();
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		for (Brick brick : script.getBrickList()) {
			if (brick instanceof UserBrick && ((UserBrick) brick).getDefinitionBrick() == this) {
				continue;
			}
			brick.addRequiredResources(requiredResourcesSet);
		}
	}

	@Override
	public CheckBox getCheckBox() {
		return null;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_user_definition;
	}

	@Override
	public View getView(final Context context) {
		super.getView(context);
		return view;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		return null;
	}

	@Override
	public void onClick(View eventOrigin) {
		UserBrickElementEditorFragment.showFragment(view, this);
	}

	@Override
	public BrickBaseType clone() throws CloneNotSupportedException {
		UserScriptDefinitionBrick clone = (UserScriptDefinitionBrick) super.clone();
		clone.script = null;
		return clone;
	}

	@Override
	public Script getScript() {
		return getUserScript();
	}

	public Script getUserScript() {
		return script;
	}

	public int addUIText(String text) {
		UserScriptDefinitionBrickElement element = new UserScriptDefinitionBrickElement();
		element.setIsText();
		element.setText(text);
		int toReturn = userScriptDefinitionBrickElements.size();
		userScriptDefinitionBrickElements.add(element);
		return toReturn;
	}

	public void addUILineBreak() {
		UserScriptDefinitionBrickElement element = new UserScriptDefinitionBrickElement();
		element.setIsLineBreak();
		element.setText(LINE_BREAK);
		userScriptDefinitionBrickElements.add(element);
	}

	public int addUILocalizedVariable(String name) {
		UserScriptDefinitionBrickElement element = new UserScriptDefinitionBrickElement();
		element.setIsVariable();
		element.setText(name);

		int toReturn = userScriptDefinitionBrickElements.size();
		userScriptDefinitionBrickElements.add(element);
		return toReturn;
	}

	public void renameUIElement(UserScriptDefinitionBrickElement element, String oldName, String newName, Context context) {
		if (element.getText().equals(oldName)) {
			element.setText(newName);
			if (element.isVariable()) {
				Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();
				Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
				DataContainer dataContainer = currentScene.getDataContainer();
				if (dataContainer != null) {
					List<UserBrick> matchingBricks = currentSprite.getUserBricksByDefinitionBrick(this, true, true);
					for (UserBrick userBrick : matchingBricks) {
						UserVariable userVariable = dataContainer.getUserVariable(currentSprite, userBrick, oldName);
						if (userVariable != null) {
							userVariable.setName(newName);
						}
					}
				}
			}
		}

		renameVariablesInFormulasAndBricks(oldName, newName, context);
	}

	public void removeDataAt(int id, Context context) {
		userScriptDefinitionBrickElements.remove(id);
	}

	public CharSequence getName() {
		CharSequence name = "";
		for (UserScriptDefinitionBrickElement element : getUserScriptDefinitionBrickElements()) {
			if (!element.isVariable()) {
				name = element.getText();
				break;
			}
		}
		return name;
	}

	public List<UserScriptDefinitionBrickElement> getUserScriptDefinitionBrickElements() {
		return userScriptDefinitionBrickElements;
	}

	public void renameVariablesInFormulasAndBricks(String oldName, String newName, Context context) {
		List<Brick> brickList = script.getBrickList();
		for (Brick brick : brickList) {
			if (brick instanceof UserBrick) {
				List<Formula> formulaList = ((UserBrick) brick).getFormulas();
				for (Formula formula : formulaList) {
					formula.updateVariableReferences(oldName, newName, context);
				}
			}
			if (brick instanceof FormulaBrick) {
				List<Formula> formulas = ((FormulaBrick) brick).getFormulas();
				for (Formula formula : formulas) {
					formula.updateVariableReferences(oldName, newName, context);
				}
			}
			if (brick instanceof ShowTextBrick) {
				ShowTextBrick showTextBrick = (ShowTextBrick) brick;
				if (showTextBrick.getUserVariable().getName().equals(oldName)) {
					((ShowTextBrick) brick).getUserVariable().setName(newName);
				}
			}
			if (brick instanceof HideTextBrick) {
				HideTextBrick showTextBrick = (HideTextBrick) brick;
				if (showTextBrick.getUserVariable().getName().equals(oldName)) {
					((HideTextBrick) brick).getUserVariable().setName(newName);
				}
			}
		}
	}

	@Override
	public void setCommentedOut(boolean commentedOut) {
		super.setCommentedOut(commentedOut);
		getScript().setCommentedOut(commentedOut);
	}
}
