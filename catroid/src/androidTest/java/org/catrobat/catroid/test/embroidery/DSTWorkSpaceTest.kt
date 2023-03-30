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
package org.catrobat.catroid.test.embroidery

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.content.Sprite
import org.junit.runner.RunWith
import org.catrobat.catroid.embroidery.EmbroideryWorkSpace
import org.catrobat.catroid.embroidery.DSTWorkSpace
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class DSTWorkSpaceTest {
    @Test
    fun simpleWorkSpaceTest() {
        val sprite = Mockito.mock(
            Sprite::class.java
        )
        val x = 1.5f
        val y = 1.5f
        val workSpace: EmbroideryWorkSpace = DSTWorkSpace()
        workSpace[x, y] = sprite
        Assert.assertEquals(x, workSpace.currentX, Float.MIN_VALUE)
        Assert.assertEquals(y, workSpace.currentY, Float.MIN_VALUE)
        Assert.assertEquals(sprite, workSpace.lastSprite)
    }
}
