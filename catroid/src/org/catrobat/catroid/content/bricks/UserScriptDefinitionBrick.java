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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.Formula;

import java.util.List;

public class UserScriptDefinitionBrick extends ScriptBrick {

	private static final long serialVersionUID = 1L;
	private static final String LINE_BREAK = "linebreak";

	private StartScript script;
	private UserScriptDefinitionBrickElements userScriptDefinitionBrickElements;

	private transient UserBrick brick; //TODO: remove this when bitmap is loaded differently (double reference)

	public UserScriptDefinitionBrick(UserBrick brick) {
		this.script = new StartScript(true);
		this.brick = brick;
		this.userScriptDefinitionBrickElements = new UserScriptDefinitionBrickElements();
	}

	public int getUserBrickId() {
		return brick.getUserBrickId();
	}

	public void setUserBrick(UserBrick brick) {
		this.brick = brick;
	}

	public int getRequiredResources() {
		int resources = Brick.NO_RESOURCES;

		for (Brick brick : script.getBrickList()) {
			if (brick instanceof UserBrick && ((UserBrick) brick).getDefinitionBrick() == this) {
				continue;
			}
			resources |= brick.getRequiredResources();
		}
		return resources;
	}

	public void appendBrickToScript(Brick brick) {
		this.getScriptSafe().addBrick(brick);
	}


	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		UserScriptDefinitionBrick copyBrick = (UserScriptDefinitionBrick) clone();
		return copyBrick;
	}

	public void renameVariablesInFormulas(String oldName, String newName, Context context) {
		if (ProjectManager.getInstance().getCurrentScript() == null) {
			return;
		}
		List<Brick> brickList = ProjectManager.getInstance().getCurrentScript().getBrickList();
		for (Brick brick : brickList) {
			if (brick instanceof UserBrick) {
				List<Formula> formulaList = ((UserBrick) brick).getFormulas();
				for (Formula formula : formulaList) {
					formula.updateVariableReferences(oldName, newName, context);
				}
			}
			if (brick instanceof FormulaBrick) {
				Formula formula = ((FormulaBrick) brick).getFormula();
				formula.updateVariableReferences(oldName, newName, context);
			}
		}
	}

	public void removeVariablesInFormulas(String name, Context context) {
		if (ProjectManager.getInstance().getCurrentScript() == null) {
			return;
		}
		List<Brick> brickList = ProjectManager.getInstance().getCurrentScript().getBrickList();
		for (Brick brick : brickList) {
			if (brick instanceof UserBrick) {
				List<Formula> formulaList = ((UserBrick) brick).getFormulas();
				for (Formula formula : formulaList) {
					formula.removeVariableReferences(name, context);
				}
			}
			if (brick instanceof FormulaBrick) {
				Formula formula = ((FormulaBrick) brick).getFormula();
				formula.removeVariableReferences(name, context);
			}
		}
	}

	@Override
	public Brick clone() {
		return new UserScriptDefinitionBrick(brick);
	}

	@Override
	public Script getScriptSafe() {
		if (getUserScript() == null) {
			script.addBrick(this);
		}

		return getUserScript();
	}

	public Script getUserScript() {
		return script;
	}

	public void setUserScript(StartScript script) {
		this.script = script;
	}

	public int addUIText(String text) {
		UserScriptDefinitionBrickElement data = new UserScriptDefinitionBrickElement();
		data.isVariable = false;
		data.isEditModeLineBreak = false;
		data.name = text;
		int toReturn = userScriptDefinitionBrickElements.getUserScriptDefinitionBrickElementList().size();
		userScriptDefinitionBrickElements.getUserScriptDefinitionBrickElementList().add(data);
		userScriptDefinitionBrickElements.incrementVersion();
		return toReturn;
	}

	public void addUILineBreak() {
		UserScriptDefinitionBrickElement data = new UserScriptDefinitionBrickElement();
		data.isVariable = false;
		data.isEditModeLineBreak = true;
		data.name = LINE_BREAK;
		userScriptDefinitionBrickElements.getUserScriptDefinitionBrickElementList().add(data);
		userScriptDefinitionBrickElements.incrementVersion();
	}

	public int addVariableWithId(Context context, int id) {
		String name = context.getResources().getString(id);
		return addUILocalizedVariable(name);
	}

	public int addUILocalizedVariable(String name) {
		UserScriptDefinitionBrickElement data = new UserScriptDefinitionBrickElement();
		data.isVariable = true;
		data.isEditModeLineBreak = false;
		data.name = name;

		if (ProjectManager.getInstance().getCurrentProject() != null) {
			DataContainer dataContainer = ProjectManager.getInstance().getCurrentProject().getDataContainer();
			if (ProjectManager.getInstance().getCurrentUserBrick() != null) {
				dataContainer.addUserBrickUserVariableToUserBrick(ProjectManager.getInstance().getCurrentUserBrick().getUserBrickId(), data.name, Double.valueOf(0));
			}
			else {
				dataContainer.addUserBrickUserVariableToUserBrick(getUserBrickId(), data.name, Double.valueOf(0));
			}
		}

		int toReturn = userScriptDefinitionBrickElements.getUserScriptDefinitionBrickElementList().size();
		userScriptDefinitionBrickElements.getUserScriptDefinitionBrickElementList().add(data);
		userScriptDefinitionBrickElements.incrementVersion();
		return toReturn;
	}

	public void renameUIElement(String oldName, String newName, Context context) {
		UserScriptDefinitionBrickElement variable = null;
		boolean isVariable = false;
		for (UserScriptDefinitionBrickElement data : userScriptDefinitionBrickElements.getUserScriptDefinitionBrickElementList()) {
			if (data.name.equals(oldName)) {
				variable = data;
				isVariable = data.isVariable;
				break;
			}
		}

		renameVariablesInFormulas(oldName, newName, context);

		variable.name = newName;

		if (isVariable && ProjectManager.getInstance().getCurrentProject() != null) {
			DataContainer dataContainer = ProjectManager.getInstance().getCurrentProject().getDataContainer();
			dataContainer.deleteUserVariableFromUserBrick(getUserBrickId(), oldName);
			dataContainer.addUserBrickUserVariableToUserBrick(getUserBrickId(), newName, Double.valueOf(0));
		}
	}

	public void removeDataAt(int id, Context context) {
		removeVariablesInFormulas(userScriptDefinitionBrickElements.getUserScriptDefinitionBrickElementList().get(id).name, context);
		if (userScriptDefinitionBrickElements.getUserScriptDefinitionBrickElementList().get(id).isVariable && ProjectManager.getInstance().getCurrentProject() != null) {
			DataContainer dataContainer = ProjectManager.getInstance().getCurrentProject().getDataContainer();
			dataContainer.deleteUserVariableFromUserBrick(getUserBrickId(), userScriptDefinitionBrickElements.getUserScriptDefinitionBrickElementList().get(id).name);
		}
		userScriptDefinitionBrickElements.getUserScriptDefinitionBrickElementList().remove(id);
		userScriptDefinitionBrickElements.incrementVersion();
	}

	/**
	 * Removes element at <b>from</b> and adds it after element at <b>to</b>
	 */
	public void reorderUIData(int from, int to) {

		if (to == -1) {
			UserScriptDefinitionBrickElement element = userScriptDefinitionBrickElements.getUserScriptDefinitionBrickElementList().remove(from);
			userScriptDefinitionBrickElements.getUserScriptDefinitionBrickElementList().add(0, element);
		} else if (from <= to) {
			UserScriptDefinitionBrickElement element = userScriptDefinitionBrickElements.getUserScriptDefinitionBrickElementList().remove(from);
			userScriptDefinitionBrickElements.getUserScriptDefinitionBrickElementList().add(to, element);
		} else {
			// from > to
			UserScriptDefinitionBrickElement element = userScriptDefinitionBrickElements.getUserScriptDefinitionBrickElementList().remove(from);
			userScriptDefinitionBrickElements.getUserScriptDefinitionBrickElementList().add(to + 1, element);
		}
		userScriptDefinitionBrickElements.incrementVersion();
	}

	public CharSequence getName() {
		CharSequence name = "";
		for (UserScriptDefinitionBrickElement element : userScriptDefinitionBrickElements.getUserScriptDefinitionBrickElementList()) {
			if (!element.isVariable) {
				name = element.name;
				break;
			}
		}
		return name;
	}

	public UserScriptDefinitionBrickElements getUserScriptDefinitionBrickElements() {
		return userScriptDefinitionBrickElements;
	}

	public void setUserScriptDefinitionBrickElements(UserScriptDefinitionBrickElements userScriptDefinitionBrickElements) {
		this.userScriptDefinitionBrickElements = userScriptDefinitionBrickElements;
	}

	public UserBrick getBrick() {
		return brick;
	}
}
