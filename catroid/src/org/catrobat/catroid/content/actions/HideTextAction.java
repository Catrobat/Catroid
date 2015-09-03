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
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.List;

public class HideTextAction extends TemporalAction {
	private String text;

	@Override
	protected void begin() {
		DataContainer projectVariableContainer = ProjectManager.getInstance().getCurrentProject().getDataContainer();
		List<UserVariable> variableList = projectVariableContainer.getProjectVariables();

		for (UserVariable var : variableList) {
			if (var.getName().equals(text)) {
				var.setVisibility(false);
				break;
			}
		}
	}

	@Override
	protected void update(float percent) {
		DataContainer projectVariableContainer = ProjectManager.getInstance().getCurrentProject().getDataContainer();
		List<UserVariable> variableList = projectVariableContainer.getProjectVariables();

		for (UserVariable var : variableList) {
			if (var.getName().equals(text)) {
				break;
			}
		}
	}

	public void setText(String text) {
		this.text = text;
	}
}
