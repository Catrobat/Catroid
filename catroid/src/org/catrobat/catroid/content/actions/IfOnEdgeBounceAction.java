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

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;

public class IfOnEdgeBounceAction extends TemporalAction {
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
			if (isLookingLeft(newDirection)) {
				newDirection = -newDirection;
			}
			xPosition = -virtualScreenWidth + (width / 2);
		} else if (xPosition > virtualScreenWidth - width / 2) {
			if (isLookingRight(newDirection)) {
				newDirection = -newDirection;
			}
			xPosition = virtualScreenWidth - (width / 2);
		}

		if (yPosition < -virtualScreenHeight + height / 2) {
			if (isLookingDown(newDirection)) {
				newDirection = 180f - newDirection;
			}
			yPosition = -virtualScreenHeight + (height / 2);
		} else if (yPosition > virtualScreenHeight - height / 2) {
			if (isLookingUp(newDirection)) {
				newDirection = 180f - newDirection;
			}
			yPosition = virtualScreenHeight - (height / 2);
		}

		sprite.look.setDirectionInUserInterfaceDimensionUnit(newDirection);
		sprite.look.setPositionInUserInterfaceDimensionUnit(xPosition, yPosition);
	}

	private boolean isLookingUp(float direction) {
		return (direction > -90f && direction < 90f);
	}

	private boolean isLookingDown(float direction) {
		return (direction > 90f || direction < -90f);
	}

	private boolean isLookingLeft(float direction) {
		return (direction > -180f && direction < 0f);
	}

	private boolean isLookingRight(float direction) {
		return (direction > 0f && direction < 180f);
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
}
