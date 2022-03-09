/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import com.google.common.base.Objects;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.EventWrapper;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.eventids.BounceOffEventId;

import static org.koin.java.KoinJavaComponent.inject;

public class PhysicalCollision {

	private int contactCounter = 0;
	private CollidingSprites objects;

	PhysicalCollision(CollidingSprites objects) {
		this.objects = objects;
	}

	void increaseContactCounter() {
		contactCounter++;
	}

	void decreaseContactCounter() {
		if (contactCounter > 0) {
			contactCounter--;
		}
	}

	public int getContactCounter() {
		return contactCounter;
	}

	void sendBounceOffEvents() {
		sendBounceOffEvent(objects.sprite1, objects.sprite2);
		sendBounceOffEvent(objects.sprite2, objects.sprite1);
	}

	private void sendBounceOffEvent(Sprite spriteBouncingOff, Sprite otherSprite) {
		if (spriteBouncingOff.isClone) {
			fireBounceOffEvent(spriteBouncingOff.myOriginal, otherSprite);
		}
		if (otherSprite.isClone) {
			fireBounceOffEvent(spriteBouncingOff, otherSprite.myOriginal);
		}
		fireBounceOffEvent(spriteBouncingOff, otherSprite);
		fireBounceOffEvent(spriteBouncingOff, null);
	}

	public static void fireBounceOffEvent(Sprite bouncingSprite, Sprite staticSprite) {
		BounceOffEventId identifier = new BounceOffEventId(bouncingSprite, staticSprite);
		EventWrapper event = new EventWrapper(identifier, false);
		final ProjectManager projectManager = inject(ProjectManager.class).getValue();
		projectManager.getCurrentProject().fireToAllSprites(event);
	}

	public String toString() {
		String str = "PhysicalCollision:\n"
				+ "     sprite1: %s\n"
				+ "     sprite2: %s\n"
				+ "     contactCounter: %s\n";

		return String.format(str, objects.sprite1, objects.sprite2, String.valueOf(contactCounter));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof PhysicalCollision)) {
			return false;
		}
		PhysicalCollision collision = (PhysicalCollision) o;
		return Objects.equal(objects, collision.objects);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(objects);
	}
}
