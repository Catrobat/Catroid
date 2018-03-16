/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.content;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.eventids.CollisionEventId;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.physics.content.bricks.CollisionReceiverBrick;

public class CollisionScript extends Script implements EventScript {

	private static final long serialVersionUID = 1L;
	private String spriteToCollideWithName;

	private transient Sprite spriteToCollideWith;

	public CollisionScript(String spriteToCollideWithName) {
		this.spriteToCollideWithName = spriteToCollideWithName;
	}

	@Override
	public ScriptBrick getScriptBrick() {
		if (brick == null) {
			brick = new CollisionReceiverBrick(this);
		}
		return brick;
	}

	@Override
	public Script clone() throws CloneNotSupportedException {
		CollisionScript clone = new CollisionScript(spriteToCollideWithName);
		clone.getBrickList().addAll(cloneBrickList());
		return clone;
	}

	public String getSpriteToCollideWithName() {
		return spriteToCollideWithName;
	}

	public void setSpriteToCollideWithName(String spriteToCollideWithName) {
		this.spriteToCollideWithName = spriteToCollideWithName;
		updateSpriteToCollideWith();
	}

	public Sprite getSpriteToCollideWith() {
		updateSpriteToCollideWith();
		return spriteToCollideWith;
	}

	private void updateSpriteToCollideWith() {
		if (spriteToCollideWithName != null
				&& (spriteToCollideWith == null || !spriteToCollideWithName.equals(spriteToCollideWith.getName()))) {
			Scene currentScene = ProjectManager.getInstance().getCurrentScene();
			spriteToCollideWith = currentScene.getSpriteBySpriteName(spriteToCollideWithName);
		}
	}

	@Override
	public EventId createEventId(Sprite sprite) {
		return new CollisionEventId(sprite, spriteToCollideWith);
	}
}
