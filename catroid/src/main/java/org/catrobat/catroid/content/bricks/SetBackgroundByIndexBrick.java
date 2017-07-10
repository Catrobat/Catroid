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

package org.catrobat.catroid.content.bricks;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;

import java.util.Collections;
import java.util.List;

public class SetBackgroundByIndexBrick extends SetLookByIndexBrick {

	public SetBackgroundByIndexBrick() {
	}

	public SetBackgroundByIndexBrick(int index) {
		super(index);
	}

	@Override
	protected Sprite getSprite() {
		return ProjectManager.getInstance().getCurrentScene().getSpriteList().get(0);
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		Sprite backgroundSprite = ProjectManager.getInstance().getSceneToPlay().getSpriteList().get(0);
		sequence.addAction(sprite.getActionFactory().createSetLookByIndexAction(backgroundSprite,
				getFormulaWithBrickField(BrickField.LOOK_INDEX), wait));

		return Collections.emptyList();
	}
}
