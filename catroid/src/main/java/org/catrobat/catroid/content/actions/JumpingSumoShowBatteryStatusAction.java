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
package org.catrobat.catroid.content.actions;

import android.util.Log;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.utils.Array;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.drone.jumpingsumo.JumpingSumoDataContainer;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.stage.ShowVariableActor;
import org.catrobat.catroid.stage.StageActivity;

import java.util.List;

public class JumpingSumoShowBatteryStatusAction extends TemporalAction {

	public static final String TAG = JumpingSumoShowBatteryStatusAction.class.getSimpleName();

	private Formula xPosition;
	private Formula yPosition;
	private UserVariable variableToShow;
	private BitmapFont font;
	private float scale = 3f;

	private Sprite sprite;
	private UserBrick userBrick;
	private ShowVariableActor actor;
	JumpingSumoDataContainer batteryStatus = JumpingSumoDataContainer.getInstance();

	@Override
	protected void begin() {
		try {
			init();
			int xPosition = this.xPosition.interpretInteger(sprite);
			int yPosition = this.yPosition.interpretInteger(sprite);

			batteryStatus.setBatteryAction(this);

			DataContainer dataContainer = ProjectManager.getInstance().getCurrentScene().getDataContainer();
			List<UserVariable> variableList = dataContainer.getProjectVariables();

			boolean userVariableInList = false;
			for (UserVariable userVariable : variableList) {
				if (userVariable.getName().equals(variableToShow.getName())) {
					userVariableInList = true;
					break;
				}
			}
			if (!userVariableInList) {
				dataContainer.addProjectUserVariable(batteryStatus.BATTERY_STATUS);
			}
			updateBatteryStatus();

			if (StageActivity.stageListener != null) {
				Array<Actor> stageActors = StageActivity.stageListener.getStage().getActors();
				ShowVariableActor dummyActor = new ShowVariableActor(new UserVariable("dummyActor"), 0, 0, sprite, userBrick);

				for (Actor actor : stageActors) {
					if (actor.getClass().equals(dummyActor.getClass())) {
						ShowVariableActor showVariableActor = (ShowVariableActor) actor;
						if (showVariableActor.getVariableNameToCompare().equals(variableToShow.getName())
								&& showVariableActor.getSprite().equals(sprite)
								&& (userBrick != null ? showVariableActor.getUserBrick().equals(userBrick) : true)) {
							actor.remove();
						}
					}
				}

				actor = new ShowVariableActor(variableToShow, xPosition, yPosition, sprite, userBrick);
				StageActivity.stageListener.addActor(actor);
			}
			setVariablesVisible();
		} catch (InterpretationException e) {
			Log.d(TAG, "InterpretationException: " + e);
		}
	}

	private void setVariablesVisible() {
		variableToShow.setVisible(true);
	}

	private void init() {
		font = new BitmapFont();
		font.setColor(Color.BLACK);
		font.getData().setScale(scale);
	}

	public void updateBatteryStatus() {
		variableToShow.setValue(batteryStatus.getBatteryStatus());
		DataContainer dataContainer = ProjectManager.getInstance().getCurrentScene().getDataContainer();
		List<UserVariable> variableList = dataContainer.getProjectVariables();
		if (variableList == null) {
			return;
		}
		for (UserVariable userVariable : variableList) {
			if (userVariable.getName().equals(variableToShow.getName())) {
				userVariable.setValue(variableToShow.getValue());
				break;
			}
		}
	}

	@Override
	protected void update(float percent) {
		try {
			variableToShow.setValue(batteryStatus.getBatteryStatus());
			int xPosition = this.xPosition.interpretInteger(sprite);
			int yPosition = this.yPosition.interpretInteger(sprite);

			if (actor != null) {
				actor.setPositionX(xPosition);
				actor.setPositionY(yPosition);
			}
		} catch (InterpretationException e) {
			Log.d(TAG, "InterpretationException");
		}
	}

	public void setPosition(Formula xPosition, Formula yPosition) {
		this.xPosition = xPosition;
		this.yPosition = yPosition;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setVariableToShow(UserVariable userVariable) {
		this.variableToShow = userVariable;
	}

	public void setUserBrick(UserBrick userBrick) {
		this.userBrick = userBrick;
	}
}
