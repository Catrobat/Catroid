/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import org.catrobat.catroid.content.CollisionEventIdentifier;
import org.catrobat.catroid.content.Sprite;

import java.util.List;

public class PhysicsCollisionBroadcast {

	private int contactCounter = 0;
	private Sprite sprite1;
	private Sprite sprite2;

	PhysicsCollisionBroadcast(Sprite sprite1, Sprite sprite2) {
		this.sprite1 = sprite1;
		this.sprite2 = sprite2;
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
		if (sprite1 != null || sprite2 != null) {
			fireEvent(sprite1, sprite2);
			fireEvent(sprite1, null);
			fireEvent(sprite2, null);
			return true;
		}
		return false;
	}

	public static void fireEvent(Sprite sprite1, Sprite sprite2) {
		if (sprite1 == null && sprite2 == null) {
			return;
		}
		BroadcastEvent event = new BroadcastEvent(false);
		CollisionEventIdentifier identifier = new CollisionEventIdentifier(sprite1, sprite2);
		event.setIdentifier(identifier);
		List<Sprite> sprites = ProjectManager.getInstance().getCurrentProject().getSpriteListWithClones();
		for (Sprite spriteOfList : sprites) {
			spriteOfList.look.fire(event);
		}
	}

	public String toString() {
		String str = "PhysicsCollisionBroadcast:\n"
				+ "     sprite1: %s\n"
				+ "     sprite2: %s\n"
				+ "     contactCounter: %s\n";

		return String.format(str, sprite1, sprite2, String.valueOf(contactCounter));
	}
}
