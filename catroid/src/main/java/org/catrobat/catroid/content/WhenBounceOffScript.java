/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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
import org.catrobat.catroid.content.bricks.WhenBounceOffBrick;
import org.catrobat.catroid.content.eventids.BounceOffEventId;
import org.catrobat.catroid.content.eventids.EventId;

public class WhenBounceOffScript extends Script {

	private static final long serialVersionUID = 1L;
	private String spriteToBounceOffName = "";

	private transient Sprite spriteToBounceOff;

	public WhenBounceOffScript() {
	}

	public WhenBounceOffScript(String spriteToBounceOffName) {
		if (spriteToBounceOffName == null) {
			this.spriteToBounceOffName = "";
		} else {
			this.spriteToBounceOffName = spriteToBounceOffName;
		}
	}

	@Override
	public ScriptBrick getScriptBrick() {
		if (scriptBrick == null) {
			scriptBrick = new WhenBounceOffBrick(this);
		}
		return scriptBrick;
	}

	public String getSpriteToBounceOffName() {
		if (spriteToBounceOffName.isEmpty()) {
			return null;
		}
		return spriteToBounceOffName;
	}

	public void setSpriteToBounceOffName(String spriteToCollideWithName) {
		if (spriteToCollideWithName == null) {
			this.spriteToBounceOffName = "";
		} else {
			this.spriteToBounceOffName = spriteToCollideWithName;
		}
		updateSpriteToCollideWith(ProjectManager.getInstance().getCurrentlyEditedScene());
	}

	public void updateSpriteToCollideWith(Scene scene) {
		if (!spriteToBounceOffName.isEmpty()) {
			spriteToBounceOff = scene.getSprite(spriteToBounceOffName);
			if (spriteToBounceOff == null) {
				spriteToBounceOffName = "";
			}
		}
	}

	@Override
	public EventId createEventId(Sprite sprite) {
		return new BounceOffEventId(sprite, spriteToBounceOff);
	}
}
