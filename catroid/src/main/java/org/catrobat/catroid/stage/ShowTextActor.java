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

package org.catrobat.catroid.stage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.List;
import java.util.Map;

public class ShowTextActor extends Actor {

	private int xPosition;
	private int yPosition;
	private UserVariable variableToShow;
	private String variableNameToCompare;
	private String variableValue;
	private String variableValueWithoutDecimal;

	private Sprite sprite;
	private UserBrick userBrick;
	private float scale = 3f;
	private BitmapFont font;

	public ShowTextActor(UserVariable userVariable, int xPosition, int yPosition, Sprite sprite, UserBrick userBrick) {
		this.variableToShow = userVariable;
		this.variableNameToCompare = variableToShow.getName();
		this.variableValueWithoutDecimal = null;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.sprite = sprite;
		this.userBrick = userBrick;
		init();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		DataContainer dataContainer = ProjectManager.getInstance().getSceneToPlay().getDataContainer();

		List<UserVariable> projectVariableList = dataContainer.getProjectVariables();
		Map<Sprite, List<UserVariable>> spriteVariableMap = dataContainer.getSpriteVariableMap();
		List<UserVariable> spriteVariableList = spriteVariableMap.get(sprite);
		List<UserVariable> userBrickVariableList = dataContainer.getOrCreateVariableListForUserBrick(userBrick);

		drawVariables(projectVariableList, batch);
		drawVariables(spriteVariableList, batch);
		drawVariables(userBrickVariableList, batch);
	}

	private void drawVariables(List<UserVariable> variableList, Batch batch) {
		if (variableList == null) {
			return;
		}

		if (variableToShow.isDummy()) {
			font.draw(batch, Constants.NO_VARIABLE_SELECTED, xPosition, yPosition);
		} else {
			for (UserVariable variable : variableList) {
				if (variable.getName().equals(variableToShow.getName())) {
					variableValue = variable.getValue().toString();
					if (variable.getVisible()) {
						if (isNumberAndInteger(variableValue)) {
							font.draw(batch, variableValueWithoutDecimal, xPosition, yPosition);
						} else {
							font.draw(batch, variableValue, xPosition, yPosition);
						}
					}
					break;
				}
			}
		}
	}

	private void init() {
		font = new BitmapFont();
		font.setColor(Color.BLACK);
		font.getData().setScale(scale);
	}

	private boolean isNumberAndInteger(String variableValue) {
		double variableValueIsNumber = 0;

		if (variableValue.matches("-?\\d+(\\.\\d+)?")) {
			variableValueIsNumber = Double.parseDouble(variableValue);
		} else {
			return false;
		}

		if (((int) variableValueIsNumber) - variableValueIsNumber == 0) {
			variableValueWithoutDecimal = Integer.toString((int) variableValueIsNumber);
			return true;
		} else {
			return false;
		}
	}

	public void setPositionX(int xPosition) {
		this.xPosition = xPosition;
	}

	public void setPositionY(int yPosition) {
		this.yPosition = yPosition;
	}

	public String getVariableNameToCompare() {
		return variableNameToCompare;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public UserBrick getUserBrick() {
		return userBrick;
	}
}
