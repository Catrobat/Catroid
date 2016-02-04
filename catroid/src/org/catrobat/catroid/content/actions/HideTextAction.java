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

/**
 * Created by Robert Riedl on 12.08.2015.
 */

package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.List;
import java.util.Map;

public class HideTextAction extends TemporalAction {
	private String variableName;

	@Override
	protected void begin() {
		DataContainer projectVariableContainer = ProjectManager.getInstance().getCurrentProject().getDataContainer();
		List<UserVariable> variableList = projectVariableContainer.getProjectVariables();

		Map<Sprite, List<UserVariable>> spriteVariableMap = projectVariableContainer.getSpriteVariableMap();
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		List<UserVariable> spriteVariableList = spriteVariableMap.get(currentSprite);

		for (UserVariable variable : variableList) {
			if (variable.getName().equals(variableName)) {
				variable.setVisible(false);
				break;
			}
		}
		for (UserVariable variable : spriteVariableList) {
			if (variable.getName().equals(variableName)) {
				variable.setVisible(false);
				break;
			}
		}
	}

	@Override
	protected void update(float percent) {
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
}
