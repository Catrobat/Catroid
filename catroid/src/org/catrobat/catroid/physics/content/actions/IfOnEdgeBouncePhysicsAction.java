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
package org.catrobat.catroid.physics.content.actions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsWorld;

public class IfOnEdgeBouncePhysicsAction extends TemporalAction {

	private Sprite sprite;
	private PhysicsWorld physicsWorld;

	@Override
	protected void update(float percent) {
		boolean xIsOutSide = false;
		boolean yIsOutSide = false;
		// AABB ... AXIS-ALIGNED-BOUNDING-BOX
		Vector2 lower_AABB_edge = new Vector2();
		Vector2 upper_AABB_edge = new Vector2();
		physicsWorld.getPhysicsObject(sprite).getBoundaryBox(lower_AABB_edge, upper_AABB_edge);

		float AABB_width = upper_AABB_edge.x - lower_AABB_edge.x;
		float height = upper_AABB_edge.y - lower_AABB_edge.y;
		float xPosition = lower_AABB_edge.x + AABB_width / 2;
		float yPosition = lower_AABB_edge.y + height / 2;

		int virtualScreenWidth = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenWidth / 2;
		int virtualScreenHeight = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenHeight / 2;

		if (xPosition < -virtualScreenWidth + AABB_width / 2) {
			xIsOutSide = true;
			xPosition = -virtualScreenWidth + (AABB_width / 2);
		} else if (xPosition > virtualScreenWidth - AABB_width / 2) {
			xIsOutSide = true;
			xPosition = virtualScreenWidth - (AABB_width / 2);
		}

		if (yPosition < -virtualScreenHeight + height / 2) {
			yIsOutSide = true;
			yPosition = -virtualScreenHeight + (height / 2);
		} else if (yPosition > virtualScreenHeight - height / 2) {
			yIsOutSide = true;
			yPosition = virtualScreenHeight - (height / 2);
		}

		if (xIsOutSide) {
			physicsWorld.setBounceOnce(sprite);
			sprite.look.setXInUserInterfaceDimensionUnit(xPosition);
		}
		if (yIsOutSide) {
			physicsWorld.setBounceOnce(sprite);
			sprite.look.setYInUserInterfaceDimensionUnit(yPosition);
		}
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setPhysicsWorld(PhysicsWorld physicsWorld) {
		this.physicsWorld = physicsWorld;
	}
}
