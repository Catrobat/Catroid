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

package org.catrobat.catroid.test.stage

import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.stage.StageListener
import org.catrobat.catroid.web.WebConnectionHolder
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.internal.util.reflection.FieldSetter
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(StageListener::class)
class StageListenerTest {

    @Test
    fun testGetAllClonesOfSprite() {
        PowerMockito.whenNew(WebConnectionHolder::class.java).withNoArguments().thenReturn(null)
        val stageListener = PowerMockito.spy(StageListener())

        val sprites = ArrayList<Sprite>()
        val spriteOther = Sprite("spriteOther")
        sprites.add(spriteOther)
        val spriteClone = Sprite("spriteOther")
        spriteClone.isClone = true
        spriteClone.cloneNameExtension = "001"
        sprites.add(spriteClone)
        FieldSetter.setField(stageListener, stageListener.javaClass.getDeclaredField("sprites"), sprites)

        val clonedSprites = stageListener?.getAllClonesOfSprite(spriteOther)
        assertEquals(1, clonedSprites?.size)

        val spritesOfClone = stageListener?.getAllClonesOfSprite(spriteClone)
        assertEquals(0, spritesOfClone?.size)
    }
}
