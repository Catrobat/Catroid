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

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;

public class SetRotationStyleAction extends TemporalAction {

	private Sprite sprite;
	@Look.RotationStyle
	private int mode;

	@Override
	protected void update(float delta) {
		sprite.look.setRotationMode(mode);
		if (mode != Look.ROTATION_STYLE_LEFT_RIGHT_ONLY && sprite.look.isFlipped()) {
			sprite.look.getLookData().getTextureRegion().flip(true, false);
		}
		boolean orientedLeft = sprite.look.getMotionDirectionInUserInterfaceDimensionUnit() < 0;
		if (mode == Look.ROTATION_STYLE_LEFT_RIGHT_ONLY && orientedLeft) {
			sprite.look.getLookData().getTextureRegion().flip(true, false);
		}

		sprite.look.setMotionDirectionInUserInterfaceDimensionUnit(sprite.look.getMotionDirectionInUserInterfaceDimensionUnit());
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setRotationStyle(@Look.RotationStyle int mode) {
		this.mode = mode;
	}
}
