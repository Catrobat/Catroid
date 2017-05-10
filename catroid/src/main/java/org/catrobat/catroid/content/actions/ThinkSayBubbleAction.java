/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.stage.ShowBubbleActor;
import org.catrobat.catroid.stage.StageActivity;

public class ThinkSayBubbleAction extends TemporalAction {

	private Sprite sprite;
	private Formula text;
	private int type;

	@Override
	protected void update(float delta) {
		String textToDisplay;
		try {
			textToDisplay = text == null ? "" : text.interpretString(sprite);
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
			return;
		}
		if (StageActivity.stageListener.getBubbleActorForSprite(sprite) != null) {
			StageActivity.stageListener.getStage().getActors().removeValue(StageActivity.stageListener.getBubbleActorForSprite(sprite), true);
			StageActivity.stageListener.removeBubbleActorForSprite(sprite);
		}
		if (!textToDisplay.equals("")) {
			ShowBubbleActor showBubbleActor = new ShowBubbleActor(textToDisplay, sprite, type);
			StageActivity.stageListener.addActor(showBubbleActor);
			StageActivity.stageListener.putBubbleActor(sprite, showBubbleActor);
		}
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setText(Formula text) {
		this.text = text;
	}

	public void setType(int type) {
		this.type = type;
	}
}
