/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;

/**
 * Built exactly like FaceNameTrain, because that brick already runs in the right
 * order: Ask holds the sequence until it is answered, then the next action runs.
 *
 * The only addition is addRequiredResources. Declaring FACE_NAME_DETECTION is
 * what makes Catroid ask for the camera permission before the stage starts,
 * through the mapping already present in BrickResourcesToRuntimePermissions.
 */
public class FaceNameDetect extends BrickBaseType {

	private static final long serialVersionUID = 1L;

	public FaceNameDetect() {
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_face_name;
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(FACE_NAME_DETECTION);
		super.addRequiredResources(requiredResourcesSet);
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().faceNameDetectAction(sprite, sequence));
	}
}
