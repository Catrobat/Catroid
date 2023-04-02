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
package org.catrobat.catroid.test.physics.collision

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.Assert
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.ResourceImporter
import org.catrobat.catroid.test.physics.PhysicsTestUtils
import org.catrobat.catroid.utils.Utils
import java.io.File
import java.io.IOException

class CollisionTestUtils private constructor() {
    init {
        throw AssertionError()
    }

    companion object {
        @JvmStatic
		@Throws(IOException::class)
        fun initializeSprite(
            sprite: Sprite, resourceId: Int, filename: String, context: Context?,
            project: Project
        ) {
            sprite.look = Look(sprite)
            sprite.actionFactory = ActionFactory()
            val hashedFileName = Utils.md5Checksum(filename) + "_" + filename
            val file = ResourceImporter.createImageFileFromResourcesInDirectory(
                InstrumentationRegistry.getInstrumentation().context.resources,
                resourceId,
                File(project.defaultScene.directory, Constants.IMAGE_DIRECTORY_NAME),
                hashedFileName, 1.0
            )
            val lookData = PhysicsTestUtils.generateLookData(file)
            Assert.assertNotNull(lookData)
            val collisionInformation = lookData.collisionInformation
            collisionInformation.loadCollisionPolygon()
            sprite.look.lookData = lookData
            sprite.lookList.add(lookData)
            sprite.look.height = sprite.look.lookData.pixmap.height.toFloat()
            sprite.look.width = sprite.look.lookData.pixmap.width.toFloat()
            sprite.look.setPositionInUserInterfaceDimensionUnit(0f, 0f)
        }
    }
}
