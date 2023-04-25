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

package org.catrobat.catroid.test.content.actions

import com.badlogic.gdx.utils.GdxNativesLoader
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageListener
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(GdxNativesLoader::class, StageActivity::class)
class CloneActionTest {
    lateinit var actionFactory: ActionFactory
    lateinit var sprite: Sprite

    @Before
    fun setUp() {
        actionFactory = ActionFactory()
        sprite = Sprite("testSprite")
        PowerMockito.mockStatic(GdxNativesLoader::class.java)
        PowerMockito.mockStatic(StageActivity::class.java)
        StageActivity.stageListener = Mockito.mock(StageListener::class.java)
    }

    @Test
    fun testSpriteCloned() {
        val clone = actionFactory.createCloneAction(sprite)
        clone.act(1.0f)
        verify(StageActivity.stageListener, times(1)).cloneSpriteAndAddToStage(sprite)
    }

    @Test
    fun testSpriteNotCloned() {
        val clone = actionFactory.createCloneAction(null)
        clone.act(1.0f)
        verify(StageActivity.stageListener, never()).cloneSpriteAndAddToStage(sprite)
    }
}
