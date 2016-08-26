/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
 * Created by Robert Riedl on 29.07.2015.
 */

package org.catrobat.catroid.content.actions;

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.utils.Array;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.stage.ShowTextActor;
import org.catrobat.catroid.stage.StageActivity;

import java.util.List;
import java.util.Map;

public class ShowTextAction extends TemporalAction {
	public static final String TAG = ShowTextAction.class.getSimpleName();
	private Formula endX;
	private Formula endY;
	private String variableName;
	private Sprite sprite;
	private UserBrick userBrick;
	private ShowTextActor actor;

	@Override
	protected void begin() {
		try {
			int x = endX.interpretInteger(sprite);
			int y = endY.interpretInteger(sprite);

			DataContainer dataContainer = ProjectManager.getInstance().getCurrentProject().getDataContainer();
			List<UserVariable> variableList = dataContainer.getProjectVariables();

			Map<Sprite, List<UserVariable>> spriteVariableMap = dataContainer.getSpriteVariableMap();
			Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
			List<UserVariable> spriteVariableList = spriteVariableMap.get(currentSprite);
			List<UserVariable> userBrickVariableList = dataContainer.getOrCreateVariableListForUserBrick(userBrick);
			if (StageActivity.stageListener != null) {
				Array<Actor> stageActors = StageActivity.stageListener.getStage().getActors();
				ShowTextActor showTextActor = new ShowTextActor("textActor", 0, 0, sprite, userBrick);

				for (Actor actor : stageActors) {
					if (actor.getClass().equals(showTextActor.getClass())) {
						ShowTextActor textActor = (ShowTextActor) actor;
						if (textActor.getLinkedVariableName().equals(variableName)
								&& textActor.getSprite().equals(sprite)
								&& textActor.getUserBrick().equals(userBrick)) {
							actor.remove();
						}
					}
				}

				actor = new ShowTextActor(variableName, x, y, sprite, userBrick);
				StageActivity.stageListener.addActor(actor);
			}

			setVariablesVisible(variableList);
			setVariablesVisible(spriteVariableList);
			setVariablesVisible(userBrickVariableList);
		} catch (InterpretationException e) {
			Log.d(TAG, "InterpretationException: " + e);
		}
	}

	private void setVariablesVisible(List<UserVariable> variableList) {
		if (variableList == null) {
			return;
		}
		for (UserVariable userVariable : variableList) {
			if (userVariable.getName().equals(variableName)) {
				userVariable.setVisible(true);
				break;
			}
		}
	}

	@Override
	protected void update(float percent) {
		try {
			int x = endX.interpretInteger(sprite);
			int y = endY.interpretInteger(sprite);

			if (actor != null) {
				actor.setX(x);
				actor.setY(y);
			}
		} catch (InterpretationException e) {
			Log.d(TAG, "InterpretationException");
		}
	}

	public void setPosition(Formula x, Formula y) {
		endX = x;
		endY = y;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public void setUserBrick(UserBrick userBrick) {
		this.userBrick = userBrick;
	}
}
