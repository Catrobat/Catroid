/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

package org.catrobat.catroid.merge;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;

public class ReferenceHelper {

	Project into;
	Project from;

	public ReferenceHelper(Project into, Project from) {
		this.into = into;
		this.from = from;
	}

	public Project updateAllReferences() {
		for (int i = 0; i < into.getSpriteList().size(); i++) {
			Sprite sprite = into.getSpriteList().get(i);

			if (from.containsSpriteBySpriteName(sprite)) {
				into.getSpriteList().set(i, updateReference(sprite));
			}
		}
		return into;
	}

	public Sprite updateReference(Sprite sprite) {
		for (int i = 0; i < sprite.getScriptList().size(); i++) {
			Script script = sprite.getScript(i);
			sprite.getScriptList().set(i, updateReference(script));
		}
		return sprite;
	}

	public Script updateReference(Script script) {
		for (int i = 0; i < script.getBrickList().size(); i++) {
			Brick brick = script.getBrickList().get(i);
			script.getBrickList().set(i, updateReference(brick));
		}
		return script;
	}

	public Brick updateReference(Brick brick) {
		if (brick instanceof FormulaBrick) {
			((FormulaBrick) brick).updateReferenceAfterMerge(into, from);
		}
		return brick;
	}
}
