/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
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
package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.DelegateAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.UserBrickStageToken;
import org.catrobat.catroid.content.bricks.UserBrickVariable;
import org.catrobat.catroid.formulaeditor.UserVariablesContainer;

public class UserBrickAction extends DelegateAction {

	private Sprite sprite;
	private UserBrickStageToken userBrickToken;

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setUserBrickStageToken(UserBrickStageToken userBrickToken) {
		this.userBrickToken = userBrickToken;
	}

	@Override
	public boolean act(float delta) {
		for (UserBrickVariable userBrickVariable : userBrickToken.variables) {
			double value = userBrickVariable.formula.interpretDouble(sprite);

			userBrickVariable.variable.setValue(value);
		}

		UserVariablesContainer userVariables = ProjectManager.getInstance().getCurrentProject().getUserVariables();
		userVariables.setCurrentUserBrickBeingEvaluated(userBrickToken.userBrickId);

		return action.act(delta);
	}

}
