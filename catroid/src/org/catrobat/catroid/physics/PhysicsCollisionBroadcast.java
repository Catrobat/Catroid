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

package org.catrobat.catroid.physics;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.BroadcastEvent;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.content.bricks.CollisionReceiverBrick;

import java.util.List;

public class PhysicsCollisionBroadcast {
	//private static final String TAG = PhysicsCollisionBroadcast.class.getSimpleName();
	private int contactCounter = 0;
	private String objectName1;
	private String objectName2;

	PhysicsCollisionBroadcast(String objectName1, String objectName2) {
		this.objectName1 = objectName1;
		this.objectName2 = objectName2;
	}

	public void increaseContactCounter() {
		contactCounter++;
	}

	public void decreaseContactCounter() {
		if (contactCounter > 0) {
			contactCounter--;
		}
	}

	public int getContactCounter() {
		return contactCounter;
	}

	public boolean sendBroadcast() {
		if (objectName1 != null && objectName2 != null && !objectName1.isEmpty() && !objectName2.isEmpty()) {
			//Log.d(TAG, "# COLLISION # :" + objectName1 + "<->" + objectName2);
			fireEvent(objectName1 + CollisionReceiverBrick.COLLISION_MESSAGE_CONNECTOR + objectName2);
			fireEvent(objectName2 + CollisionReceiverBrick.COLLISION_MESSAGE_CONNECTOR + objectName1);
			fireEvent(objectName1 + CollisionReceiverBrick.COLLISION_MESSAGE_CONNECTOR + "anybody");
			fireEvent(objectName2 + CollisionReceiverBrick.COLLISION_MESSAGE_CONNECTOR + "anybody");
			return true;
		}
		return false;
	}

	private void fireEvent(String message) {
		BroadcastEvent event = new BroadcastEvent();
		event.setBroadcastMessage(message);
		event.setType(BroadcastEvent.BroadcastType.broadcast);
		List<Sprite> sprites = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		for (Sprite spriteOfList : sprites) {
			spriteOfList.look.fire(event);
		}
	}

	public String toString() {
		String str = "PhysicsCollisionBroadcast:\n"
				+ "     objectName1: %s\n"
				+ "     objectName2: %s\n"
				+ "     contactCounter: %s\n";

		return String.format(str, objectName1, objectName2, String.valueOf(contactCounter));
	}
}
