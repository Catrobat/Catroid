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

package org.catrobat.catroid.content.bricks

import org.catrobat.catroid.R
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.Brick.FACE_NAME_DETECTION
import org.catrobat.catroid.content.bricks.Brick.ResourcesSet

/**
 * Face name detection. FaceNameDetectAction holds the script until the capture
 * has reported a name, or Unknown, or timed out, and writes the result into the
 * face name detection sensor.
 *
 * Declaring FACE_NAME_DETECTION is what makes Catroid ask for the camera
 * permission before the stage starts, through the mapping already present in
 * BrickResourcesToRuntimePermissions.
 */
class FaceNameDetect : BrickBaseType() {

    override fun getViewResource(): Int = R.layout.brick_face_name_detect

    override fun addRequiredResources(requiredResourcesSet: ResourcesSet) {
        requiredResourcesSet.add(FACE_NAME_DETECTION)
        super.addRequiredResources(requiredResourcesSet)
    }

    override fun addActionToSequence(sprite: Sprite, sequence: ScriptSequenceAction) {
        sequence.addAction(sprite.actionFactory.faceNameDetectAction(sprite, sequence))
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
