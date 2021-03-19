/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import org.catrobat.catroid.stage.ShowBubbleActor;
import org.catrobat.catroid.stage.StageActivity;

public class ThinkSayBubbleAction extends TemporalAction {
	private static final String TAG = ThinkSayBubbleAction.class.getSimpleName();

	private Scope scope;
	private Formula text;
	private int type;

	@Override
	protected void update(float delta) {
		ShowBubbleActor showBubbleActor;
		try {
			showBubbleActor = createBubbleActor();
		} catch (InterpretationException e) {
			Log.d(TAG, "Failed to create Bubble Actor", e);
			return;
		}

		if (StageActivity.stageListener.getBubbleActorForSprite(scope.getSprite()) != null) {
			StageActivity.stageListener.removeBubbleActorForSprite(scope.getSprite());
		}
		if (showBubbleActor != null) {
			StageActivity.stageListener.setBubbleActorForSprite(scope.getSprite(), showBubbleActor);
		}
	}

	public ShowBubbleActor createBubbleActor() throws InterpretationException {
		String textToDisplay = text == null ? "" : text.interpretString(scope);
		if (textToDisplay.isEmpty()) {
			return null;
		}
		return new ShowBubbleActor(textToDisplay, scope.getSprite(), type);
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public void setText(Formula text) {
		this.text = text;
	}

	public void setType(int type) {
		this.type = type;
	}
}
