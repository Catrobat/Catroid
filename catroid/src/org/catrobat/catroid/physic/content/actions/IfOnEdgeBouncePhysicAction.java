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
package org.catrobat.catroid.physic.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physic.PhysicsObject;
import org.catrobat.catroid.physic.PhysicsWorld;

public class IfOnEdgeBouncePhysicAction extends TemporalAction {

	private Sprite sprite;
	private PhysicsWorld physicsWorld;

	@Override
	protected void update(float percent) {
		// get boundarybox
		PhysicsObject physicObject = physicsWorld.getPhysicObject(sprite);

		float width = sprite.look.getWidthInUserInterfaceDimensionUnit();
		float height = sprite.look.getHeightInUserInterfaceDimensionUnit();
		float xPosition = sprite.look.getXInUserInterfaceDimensionUnit();
		float yPosition = sprite.look.getYInUserInterfaceDimensionUnit();

		int virtualScreenWidth = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenWidth / 2;
		int virtualScreenHeight = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenHeight / 2;

		if (xPosition < -virtualScreenWidth + width / 2) {
			// do something
			xPosition = -virtualScreenWidth + (width / 2);
		} else if (xPosition > virtualScreenWidth - width / 2) {
			// do something
			xPosition = virtualScreenWidth - (width / 2);
		}

		if (yPosition < -virtualScreenHeight + height / 2) {
			// do something
			yPosition = -virtualScreenHeight + (height / 2);
		} else if (yPosition > virtualScreenHeight - height / 2) {
			// do something
			yPosition = virtualScreenHeight - (height / 2);
		}

		sprite.look.setPositionInUserInterfaceDimensionUnit(xPosition, yPosition);
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setPhysicWorld(PhysicsWorld physicsWorld) {
		this.physicsWorld = physicsWorld;
	}
}
