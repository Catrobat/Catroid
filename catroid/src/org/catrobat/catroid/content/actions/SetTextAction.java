/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.stage.DrawTextActor;
import org.catrobat.catroid.stage.StageActivity;

public class SetTextAction extends TemporalAction {

	private Formula endX;
	private Formula endY;
	private Formula duration;
	private Formula text;

	private Sprite sprite;
	private DrawTextActor actor;

	@Override
	protected void begin() {
		try {
			String string = text.interpretString(sprite);
			int posX = endX.interpretInteger(sprite);
			int posY = endY.interpretInteger(sprite);

			actor = new DrawTextActor(string, posX, posY);
			StageActivity.stageListener.addActor(actor);
		} catch (InterpretationException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void update(float percent) {
		try {
			String str = text.interpretString(sprite);
			int posX = endX.interpretInteger(sprite);
			int posY = endY.interpretInteger(sprite);

		    actor.setText(str);
			actor.setPosX(posX);
			actor.setPosY(posY);
		} catch (InterpretationException e) {
			e.printStackTrace();
		}
	}

	public void setDuration(Formula duration) {
		this.duration = duration;
	}

	public void setPosition(Formula x, Formula y) {
		endX = x;
		endY = y;
	}

	public void setText(Formula text) {
		this.text = text;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}


}
