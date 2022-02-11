/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.utils.Array;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.stage.ShowTextActor;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.utils.ShowTextUtils.AndroidStringProvider;

public class ShowTextColorSizeAlignmentAction extends TemporalAction {

	public static final String TAG = ShowTextColorSizeAlignmentAction.class.getSimpleName();

	private Formula xPosition;
	private Formula yPosition;
	private Formula relativeTextSize;
	private Formula color;
	private UserVariable variableToShow;
	private Scope scope;
	private int alignment;
	private ShowTextActor actor;
	AndroidStringProvider androidStringProvider;

	@Override
	protected void begin() {
		try {
			int xPosition = this.xPosition.interpretInteger(scope);
			int yPosition = this.yPosition.interpretInteger(scope);
			float relativeTextSize = this.relativeTextSize.interpretFloat(scope) / 100;
			String color = this.color.interpretString(scope);
			if (StageActivity.stageListener != null) {
				Array<Actor> stageActors = StageActivity.stageListener.getStage().getActors();
				ShowTextActor dummyActor = new ShowTextActor(new UserVariable("dummyActor"), 0,
						0, relativeTextSize, color, scope.getSprite(), alignment, androidStringProvider);
				for (Actor actor : stageActors) {
					if (actor.getClass().equals(dummyActor.getClass())) {
						ShowTextActor showTextActor = (ShowTextActor) actor;
						if (showTextActor.getVariableNameToCompare().equals(variableToShow.getName())
								&& showTextActor.getSprite().equals(scope.getSprite())) {
							actor.remove();
						}
					}
				}
				actor = new ShowTextActor(variableToShow, xPosition, yPosition, relativeTextSize,
						color, scope.getSprite(), alignment, androidStringProvider);
			}
			if (relativeTextSize <= 0.f) {
				variableToShow.setVisible(false);
			} else {
				StageActivity.stageListener.addActor(actor);
				variableToShow.setVisible(true);
			}
		} catch (InterpretationException e) {
			Log.d(TAG, "InterpretationException: " + e);
		}
	}

	@Override
	protected void update(float percent) {
		try {
			int xPosition = this.xPosition.interpretInteger(scope);
			int yPosition = this.yPosition.interpretInteger(scope);

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

	public void setRelativeTextSize(Formula relativeTextSize) {
		this.relativeTextSize = relativeTextSize;
	}

	public void setColor(Formula color) {
		this.color = color;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public void setVariableToShow(UserVariable userVariable) {
		this.variableToShow = userVariable;
	}

	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}

	public void setAndroidStringProvider(AndroidStringProvider androidStringProvider) {
		this.androidStringProvider = androidStringProvider;
	}
}
