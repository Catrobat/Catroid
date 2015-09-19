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
 * Created by Robert Riedl on 29.07.2015.
 */

package org.catrobat.catroid.stage;

import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.List;

public class ShowTextActor extends Actor {
	private int posX;
	private int posY;
	private String variableName;
	private String linkedVariableName;
	private float scale = 3f;
	private BitmapFont font;
	String variableValue;

	public ShowTextActor(String text, int x, int y) {
		this.variableName = text;
		this.posX = x;
		this.posY = y;
		this.linkedVariableName = variableName;
		init();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		DataContainer projectVariableContainer = ProjectManager.getInstance().getCurrentProject().getDataContainer();
		List<UserVariable> variableList = projectVariableContainer.getProjectVariables();

		if (variableName.equals("No variable set")) {
			font.draw(batch, variableName, posX, posY);
		} else {
			for (UserVariable variable : variableList) {
				if (variable.getName().equals(variableName)) {
					variableValue = variable.getValue().toString();
					if (variable.getVisibility()) {
						font.draw(batch, variableValue, posX, posY);
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

	public void setX(int x) {
		this.posX = x;
	}

	public void setY(int y) {
		this.posY = y;
	}

	public String getLinkedVariableName() {
		return linkedVariableName;
	}
}

