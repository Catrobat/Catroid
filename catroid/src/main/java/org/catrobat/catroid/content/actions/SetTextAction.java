/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.TextActor;

public class SetTextAction extends TemporalAction {

	private Formula endX;
	private Formula endY;
	private Formula text;
	private Scope scope;
	private TextActor actor;

	@Override
	protected void begin() {
		try {
			String string = text.interpretString(scope);
			int posX = endX.interpretInteger(scope);
			int posY = endY.interpretInteger(scope);

			actor = new TextActor(string, posX, posY);
			StageActivity.stageListener.addActor(actor);
		} catch (InterpretationException exception) {
			Log.e(getClass().getSimpleName(), Log.getStackTraceString(exception));
		}
	}

	@Override
	protected void update(float percent) {
		try {
			String str = text.interpretString(scope);
			int posX = endX.interpretInteger(scope);
			int posY = endY.interpretInteger(scope);

			actor.setText(str);
			actor.setPosX(posX);
			actor.setPosY(posY);
		} catch (InterpretationException exception) {
			Log.e(getClass().getSimpleName(), Log.getStackTraceString(exception));
		}
	}

	public void setPosition(Formula x, Formula y) {
		endX = x;
		endY = y;
	}

	public void setText(Formula text) {
		this.text = text;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}
}
