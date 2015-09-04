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

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class SetRotationStyleAction extends TemporalAction {

	private Sprite sprite;
	private Formula mode;

	@Override
	protected void update(float delta) {
		try {
			Integer newMode = (mode == null) ? Integer.valueOf(0) : mode.interpretInteger(sprite);

			sprite.look.setRotationMode(newMode);
			if (sprite.look.isFlipped()) {
				sprite.look.getLookData().getTextureRegion().flip(true, false);
				sprite.look.setFlipped(false);
			}

			if (!sprite.look.isFlipped() && sprite.look.getRealDirectionInUserInterfaceDimensionUnit() < 0 && newMode == 0) {
				sprite.look.getLookData().getTextureRegion().flip(true, false);
				sprite.look.setFlipped(true);
			}
			sprite.look.setDirectionInUserInterfaceDimensionUnit(sprite.look.getRealDirectionInUserInterfaceDimensionUnit());
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Formula interpretation for this specific Brick failed.", interpretationException);
		}
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setRotationStyle(Formula mode) {
		this.mode = mode;
	}
}
