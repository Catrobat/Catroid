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
package org.catrobat.catroid.test.stage

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.stage.StageListener
import org.catrobat.catroid.web.WebConnectionHolder
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(WebConnectionHolder::class, StageListener::class)
class AddCloneActorStageTest {
    var stageListenerSpy: StageListener? = null
    var stageMock: Stage? = null
    var arrayMock: Array<Actor?>? = null
    var cloneMeLook: Look? = Look(Sprite())
    var copiedSpriteLook: Look? = Look(Sprite())
    var actorGroup: Group? = Group()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        PowerMockito.whenNew(WebConnectionHolder::class.java).withNoArguments().thenReturn(null)
        stageListenerSpy = PowerMockito.spy(StageListener())
        stageMock = PowerMockito.mock(Stage::class.java)
        arrayMock = PowerMockito.mock(Array::class.java) as Array<Actor?>
    }

    @Test
    fun testNoIndexOutOfBoundsExceptionOnAddingCloneActor() {
        PowerMockito.`when`(stageMock?.actors).thenReturn(arrayMock)
        PowerMockito.`when`(arrayMock?.contains(cloneMeLook, true)).thenReturn(false)
        stageListenerSpy?.addCloneActorToStage(stageMock, actorGroup, cloneMeLook, copiedSpriteLook)
    }
}
