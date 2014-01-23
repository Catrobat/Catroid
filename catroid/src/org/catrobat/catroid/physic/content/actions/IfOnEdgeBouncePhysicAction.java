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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physic.PhysicsObject;
import org.catrobat.catroid.physic.PhysicsWorld;

public class IfOnEdgeBouncePhysicAction extends TemporalAction {

	private Sprite sprite;
	private PhysicsWorld physicsWorld;

	@Override
	protected void begin() {
		physicsWorld.setBounceOnce(sprite);
	}

	@Override
	protected void update(float percent) {
		// get boundarybox
		boolean xisOutSide = false;
		boolean yisOutSide = false;
		PhysicsObject physicObject = physicsWorld.getPhysicObject(sprite);
		Vector2 lower = new Vector2();
		Vector2 upper = new Vector2();
		physicObject.getBoundaryBox(lower, upper);

		float width = upper.x - lower.x;
		float height = upper.y - lower.y;
		float xPosition = lower.x + width / 2;
		float yPosition = lower.y + height / 2;

		int virtualScreenWidth = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenWidth / 2;
		int virtualScreenHeight = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenHeight / 2;

		if (xPosition < -virtualScreenWidth + width / 2) {
			// do something
			xisOutSide = true;
			xPosition = -virtualScreenWidth + (width / 2);
		} else if (xPosition > virtualScreenWidth - width / 2) {
			// do something
			xisOutSide = true;
			xPosition = virtualScreenWidth - (width / 2);
		}

		if (yPosition < -virtualScreenHeight + height / 2) {
			// do something
			yisOutSide = true;
			yPosition = -virtualScreenHeight + (height / 2);
		} else if (yPosition > virtualScreenHeight - height / 2) {
			// do something
			yisOutSide = true;
			yPosition = virtualScreenHeight - (height / 2);
		}

		if (xisOutSide) {
			sprite.look.setXInUserInterfaceDimensionUnit(xPosition);
		}
		if (yisOutSide) {
			sprite.look.setYInUserInterfaceDimensionUnit(yPosition);
		}

	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setPhysicWorld(PhysicsWorld physicsWorld) {
		this.physicsWorld = physicsWorld;
	}
}
