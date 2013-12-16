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
package org.catrobat.catroid.physic;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;

public class PhysicsLook extends Look {

	private transient final PhysicsObject physicsObject;

	public PhysicsLook(Sprite sprite) {
		super(sprite);
		PhysicsWorld physicWorld = ProjectManager.getInstance().getCurrentProject().getPhysicWorld();
		physicsObject = physicWorld.getPhysicObject(sprite);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		physicsObject.setVisible(visible);
	}

	@Override
	public void setLookData(LookData lookData) {
		super.setLookData(lookData);
		PhysicsWorld physicWorld = ProjectManager.getInstance().getCurrentProject().getPhysicWorld();
		physicWorld.changeLook(physicsObject, this);
	}

	@Override
	public void setX(float x) {
		if (null != physicsObject) {
			physicsObject.setX(x + getWidth() / 2.0f);
		}
	}

	@Override
	public void setY(float y) {
		if (null != physicsObject) {
			physicsObject.setY(y + getHeight() / 2.0f);
		}
	}

	@Override
	public float getX() {
		return physicsObject.getX() - getWidth() / 2.0f;
	}

	@Override
	public float getY() {
		return physicsObject.getY() - getHeight() / 2.0f;
	}

	@Override
	public float getRotation() {
		return -physicsObject.getDirection() + DEGREE_UI_OFFSET;
	}

	@Override
	public void setRotation(float degrees) {
		if (null != physicsObject) {
			physicsObject.setDirection(degrees);
		}
	}

	@Override
	public void setScale(float scaleX, float scaleY) {
		super.setScale(scaleX, scaleY);
		PhysicsWorld physicWorld = ProjectManager.getInstance().getCurrentProject().getPhysicWorld();
		if (null != physicsObject) {
			physicWorld.changeLook(physicsObject, this);
		}
	}
}
