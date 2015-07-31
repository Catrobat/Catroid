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
package org.catrobat.catroid.content.actions;

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

import java.util.List;

public class GoNStepsBackAction extends TemporalAction {

	private Sprite sprite;
	private Formula steps;

	@Override
	protected void update(float delta) {
		Float stepsValue;
		try {
			stepsValue = steps == null ? Float.valueOf(0f) : steps.interpretFloat(sprite);
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
			return;
		}

		int zPosition = sprite.look.getZIndex();
		if (stepsValue.intValue() > 0 && (zPosition - stepsValue.intValue()) < 1) {
			sprite.look.setZIndex(1);
		} else if (stepsValue.intValue() < 0 && (zPosition - stepsValue.intValue()) < zPosition) {
			toFront();
		} else {
			goNStepsBack(stepsValue.intValue());
		}
	}

	private void toFront() {

		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		int actualSpriteZIndex = sprite.look.getZIndex();

		for (int i = 0; i < spriteList.size(); i++) {
			if (spriteList.get(i).look.getZIndex() > actualSpriteZIndex) {
				spriteList.get(i).look.setZIndex(spriteList.get(i).look.getZIndex() - 1);
			}
		}
		sprite.look.setZIndex(spriteList.size() - 1);
	}

	private void goNStepsBack(int steps) {
		int zPosition = sprite.look.getZIndex();
		int newSpriteZIndex = zPosition - steps;

		if (newSpriteZIndex < 1) {

			newSpriteZIndex = 1;
		}

		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentProject().getSpriteList();

		for (int i = 0; i < spriteList.size(); i++) {
			if (steps > 0) {
				if (spriteList.get(i).look.getZIndex() >= newSpriteZIndex && spriteList.get(i).look.getZIndex() < zPosition) {
					spriteList.get(i).look.setZIndex(spriteList.get(i).look.getZIndex() + 1);
				}
			} else {
				if (spriteList.get(i).look.getZIndex() <= newSpriteZIndex && spriteList.get(i).look.getZIndex() > zPosition) {
					spriteList.get(i).look.setZIndex(spriteList.get(i).look.getZIndex() - 1);
				}
			}
		}
		sprite.look.setZIndex(newSpriteZIndex);
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setSteps(Formula steps) {
		this.steps = steps;
	}
}
