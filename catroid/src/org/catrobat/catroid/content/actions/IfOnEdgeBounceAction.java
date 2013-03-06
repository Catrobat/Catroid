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

	private Sprite sprite;

	@Override
	protected void update(float percent) {
		float size = sprite.look.getSize();

		float width = sprite.look.getWidth() * size;
		float height = sprite.look.getHeight() * size;
		int xPosition = (int) sprite.look.getXPosition();
		int yPosition = (int) sprite.look.getYPosition();

		int virtualScreenWidth = ProjectManager.getInstance().getCurrentProject().virtualScreenWidth / 2;
		int virtualScreenHeight = ProjectManager.getInstance().getCurrentProject().virtualScreenHeight / 2;
		float rotationResult = -sprite.look.getRotation() + 90f;

		if (xPosition < -virtualScreenWidth + width / 2) {

			rotationResult = Math.abs(rotationResult);
			xPosition = -virtualScreenWidth + (int) (width / 2);

		} else if (xPosition > virtualScreenWidth - width / 2) {

			rotationResult = -Math.abs(rotationResult);

			xPosition = virtualScreenWidth - (int) (width / 2);
		}

		if (yPosition > virtualScreenHeight - height / 2) {

			if (Math.abs(rotationResult) < 90f) {
				if (rotationResult < 0f) {
					rotationResult = -180f - rotationResult;
				} else {
					rotationResult = 180f - rotationResult;
				}
			}

			yPosition = virtualScreenHeight - (int) (height / 2);

		} else if (yPosition < -virtualScreenHeight + height / 2) {

			if (Math.abs(rotationResult) > 90f) {
				if (rotationResult < 0f) {
					rotationResult = -180f - rotationResult;
				} else {
					rotationResult = 180f - rotationResult;
				}
			}

			yPosition = -virtualScreenHeight + (int) (height / 2);
		}

		sprite.look.setRotation(-rotationResult + 90f);
		sprite.look.setXYPosition(xPosition, yPosition);
	}

	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

}
