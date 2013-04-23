/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.actions;

import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class PointToAction extends TemporalAction {

	private Sprite sprite;
	private Sprite pointedSprite;

	@Override
	protected void update(float percent) {
		final ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject()
				.getSpriteList();
		if (!spriteList.contains(pointedSprite)) {
			pointedSprite = null;
		}

		if (pointedSprite == null) {
			pointedSprite = this.sprite;
		}

		int spriteXPosition = 0, spriteYPosition = 0;
		int pointedSpriteXPosition = 0, pointedSpriteYPosition = 0;
		double base = 0.0, height = 0.0, value = 0.0;

		spriteXPosition = (int) sprite.look.getXInUserInterfaceDimensionUnit();
		spriteYPosition = (int) sprite.look.getYInUserInterfaceDimensionUnit();
		pointedSpriteXPosition = (int) pointedSprite.look.getXInUserInterfaceDimensionUnit();
		pointedSpriteYPosition = (int) pointedSprite.look.getYInUserInterfaceDimensionUnit();

		double rotationDegrees;
		if (spriteXPosition == pointedSpriteXPosition && spriteYPosition == pointedSpriteYPosition) {
			rotationDegrees = 90;
		} else if (spriteXPosition == pointedSpriteXPosition || spriteYPosition == pointedSpriteYPosition) {
			if (spriteXPosition == pointedSpriteXPosition) {
				if (spriteYPosition > pointedSpriteYPosition) {
					rotationDegrees = 180;
				} else {
					rotationDegrees = 0;
				}
			} else {
				if (spriteXPosition > pointedSpriteXPosition) {
					rotationDegrees = 270;
				} else {
					rotationDegrees = 90;
				}
			}

		} else {
			base = Math.abs(spriteYPosition - pointedSpriteYPosition);
			height = Math.abs(spriteXPosition - pointedSpriteXPosition);
			value = Math.toDegrees(Math.atan(base / height));

			if (spriteXPosition < pointedSpriteXPosition) {
				if (spriteYPosition > pointedSpriteYPosition) {
					rotationDegrees = 90 + value;
				} else {
					rotationDegrees = 90 - value;
				}
			} else {
				if (spriteYPosition > pointedSpriteYPosition) {
					rotationDegrees = 270 - value;
				} else {
					rotationDegrees = 270 + value;
				}
			}
		}
		sprite.look.setRotation((-(float) rotationDegrees) + 90f);
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setPointedSprite(Sprite pointedSprite) {
		this.pointedSprite = pointedSprite;
	}

}
