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

package org.catrobat.catroid.test.content.actions

import com.badlogic.gdx.utils.GdxNativesLoader
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.CameraBrickAction
import org.catrobat.catroid.stage.StageActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.MockedStatic
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CameraBrickActionTest {
    private lateinit var testSprite: Sprite
    private lateinit var cameraManager: org.catrobat.catroid.camera.CameraManager

    private lateinit var gdxNativesLoaderMock: MockedStatic<GdxNativesLoader>
    private lateinit var stageActivityMock: MockedStatic<StageActivity>

    @Before
    @Throws(Exception::class)
    fun setUp() {
        testSprite = Sprite("testSprite")
        cameraManager = mock(org.catrobat.catroid.camera.CameraManager::class.java)
        gdxNativesLoaderMock = mockStatic(GdxNativesLoader::class.java)
        stageActivityMock = mockStatic(StageActivity::class.java)
        given(StageActivity.getActiveCameraManager()).willReturn(cameraManager)
    }

    @After
    fun tearDown() {
        gdxNativesLoaderMock.close()
        stageActivityMock.close()
    }

    @Test
    fun testStartPreview() {
        val action = testSprite.actionFactory.createUpdateCameraPreviewAction(true)
            as CameraBrickAction
        action.act(1f)
        verify(cameraManager, times(1)).startPreview()
    }

    @Test
    fun testStopPreview() {
        val action = testSprite.actionFactory.createUpdateCameraPreviewAction(false)
            as CameraBrickAction
        action.act(1f)
        verify(cameraManager, times(1)).stopPreview()
    }
}
