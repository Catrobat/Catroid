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

package org.catrobat.catroid.content.actions;

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.utils.Array;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.stage.ShowTextActor;
import org.catrobat.catroid.stage.StageActivity;

import java.util.List;

public class ShowTextAction extends TemporalAction {
	private Formula endX;
	private Formula endY;
	private String text;

	private Sprite sprite;
	private ShowTextActor actor;

	@Override
	protected void begin() {
		try {
			int x = endX.interpretInteger(sprite);
			int y = endY.interpretInteger(sprite);

			DataContainer userVariableContainer = ProjectManager.getInstance().getCurrentProject().getDataContainer();
			List<UserVariable> variableList = userVariableContainer.getProjectVariables();

			Array<Actor> stageActors = StageActivity.stageListener.getStage().getActors();
			ShowTextActor showTextActor = new ShowTextActor("textActor", 0, 0);

			for (Actor actor : stageActors) {
				if (actor.getClass().equals(showTextActor.getClass())) {
					ShowTextActor textActor = (ShowTextActor) actor;
					if (textActor.getLinkedVariableName().equals(text)) {
						actor.remove();
					}
				}
			}

			actor = new ShowTextActor(text, x, y);
			StageActivity.stageListener.addActor(actor);

			for (UserVariable userVariable : variableList) {
				if (userVariable.getName().equals(text)) {
					userVariable.setVisibility(true);
					break;
				}
			}
		} catch (InterpretationException e) {
			Log.d("ShowTextAction.java", "InterpretationException");
		}
	}

	@Override
	protected void update(float percent) {
		try {
			int x = endX.interpretInteger(sprite);
			int y = endY.interpretInteger(sprite);

			actor.setX(x);
			actor.setY(y);
		} catch (InterpretationException e) {
			Log.d("ShowTextAction.java", "InterpretationException");
		}
	}

	public void setPosition(Formula x, Formula y) {
		endX = x;
		endY = y;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setText(String text) {
		this.text = text;
	}
}
