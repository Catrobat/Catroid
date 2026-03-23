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
package org.catrobat.catroid.test.stage

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit4.MockKRule
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.stage.StageListener
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AddCloneActorStageTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @SpyK
    var stageListenerSpy = StageListener()

    @RelaxedMockK
    lateinit var stageMock: Stage

    @RelaxedMockK
    lateinit var arrayMock: Array<Actor>

    @RelaxedMockK
    lateinit var rootGroupMock: Group

    @RelaxedMockK
    lateinit var cloneMeLookMock: Look

    @RelaxedMockK
    lateinit var copyLookMock: Look

    @Test
    fun testNoIndexOutOfBoundsExceptionOnAddingCloneActor() {
        every { stageMock.actors } returns arrayMock
        every { arrayMock.contains(cloneMeLookMock, true) } returns false

        stageListenerSpy.addCloneActorToStage(
            stageMock,
            rootGroupMock,
            cloneMeLookMock,
            copyLookMock
        )
    }
}
