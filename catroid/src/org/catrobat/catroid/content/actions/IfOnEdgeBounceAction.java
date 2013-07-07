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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class IfOnEdgeBounceAction extends TemporalAction {
	private final static int UP = 0x1;
	private final static int DOWN = 0x2;
	private final static int LEFT = 0x4;
	private final static int RIGHT = 0x8;

	private Sprite sprite;

	@Override
	protected void update(float percent) {
		float width = sprite.look.getWidthInUserInterfaceDimensionUnit();
		float height = sprite.look.getHeightInUserInterfaceDimensionUnit();
		float xPosition = sprite.look.getXInUserInterfaceDimensionUnit();
		float yPosition = sprite.look.getYInUserInterfaceDimensionUnit();

		int virtualScreenWidth = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenWidth / 2;
		int virtualScreenHeight = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenHeight / 2;
		float newDirection = sprite.look.getDirectionInUserInterfaceDimensionUnit();

		if (xPosition < -virtualScreenWidth + width / 2) {
			if ((getDirection() & LEFT) > 0) {
				newDirection = -newDirection;
			}
			xPosition = -virtualScreenWidth + (width / 2);
		} else if (xPosition > virtualScreenWidth - width / 2) {
			if ((getDirection() & RIGHT) > 0) {
				newDirection = -newDirection;
			}
			xPosition = virtualScreenWidth - (width / 2);
		}

		if (yPosition < -virtualScreenHeight + height / 2) {
			if ((getDirection() & DOWN) > 0) {
				newDirection = 180f - newDirection;
			}
			yPosition = -virtualScreenHeight + (height / 2);
		} else if (yPosition > virtualScreenHeight - height / 2) {
			if ((getDirection() & UP) > 0) {
				newDirection = 180f - newDirection;
			}
			yPosition = virtualScreenHeight - (height / 2);
		}

		sprite.look.setDirectionInUserInterfaceDimensionUnit(newDirection);
		sprite.look.setPositionInUserInterfaceDimensionUnit(xPosition, yPosition);
	}

	private int getDirection() {
		float direction = sprite.look.getDirectionInUserInterfaceDimensionUnit();

		int returnValue = 0;
		if (direction > -90f && direction < 90f) {
			returnValue |= UP;
		} else if (direction > 90f || direction < -90f) {
			returnValue |= DOWN;
		}

		if (direction > 0f && direction < 180f) {
			returnValue |= RIGHT;
		} else if (direction > -180f && direction < 0f) {
			returnValue |= LEFT;
		}

		return returnValue;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

}
