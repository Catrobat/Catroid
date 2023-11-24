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

package org.catrobat.catroid.test.visualplacement

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import org.catrobat.catroid.common.defaultprojectcreators.BitmapWithRotationInfo
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.test.UnitTestWithMockedContext
import org.catrobat.catroid.utils.AndroidCoordinates
import org.catrobat.catroid.utils.GameCoordinates
import org.catrobat.catroid.utils.ShowTextUtils
import org.catrobat.catroid.utils.UnscaledGameCoordinatesForBrick
import org.catrobat.catroid.visualplacement.LayoutComputer
import org.catrobat.catroid.visualplacement.VisualPlacementDrawingUtils
import org.catrobat.catroid.visualplacement.VisualPlacementViewModel
import org.catrobat.catroid.visualplacement.VisualPlacementViewModel.Companion.EXTRA_X_COORDINATE
import org.catrobat.catroid.visualplacement.VisualPlacementViewModel.Companion.EXTRA_Y_COORDINATE
import org.catrobat.catroid.visualplacement.model.DrawableSprite
import org.catrobat.catroid.visualplacement.model.Size
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.mock.declareMock
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(ShowTextUtils::class, Bitmap::class, Bundle::class)
class VisualPlacementViewModelTest : UnitTestWithMockedContext() {

    // Needed because of the LiveData
    @Rule
    val taskExecutor = InstantTaskExecutorRule()

    private lateinit var layoutRatio: Size
    private lateinit var layoutSize: Size

    @Before
    fun setUp() {
        layoutRatio = Size(1f, 1f)
        layoutSize = Size(100f, 200f)
        declareMock<LayoutComputer> {
            whenever(getLayoutRatio()).doReturn(layoutRatio)
            whenever(getLayoutSize()).doReturn(layoutSize)
        }
    }

    @Test
    fun shouldTakeInitialPositionFromBundle() {
        val currentSprite = spy(Sprite("name")) {
            doReturn(mock<BitmapWithRotationInfo>()).whenever(it).spriteBitmap
        }

        val expectedScalingFactor = Size(1f, 1f)
        currentSprite.look = mock<Look> {
            on { scaleX } doReturn 1f
            on { scaleY } doReturn 1f
        }

        val currentlyEditedScene = mock<Scene> {
            on { spriteList } doReturn listOf(currentSprite)
        }
        whenever(projectManager.currentlyEditedScene).thenReturn(currentlyEditedScene)
        whenever(projectManager.currentSprite).thenReturn(currentSprite)

        val initialPosition = UnscaledGameCoordinatesForBrick(2f, 3f)
        val saveStateHandle = mock<SavedStateHandle> {
            on { it.get<Int>(EXTRA_X_COORDINATE) } doReturn initialPosition.x.toInt()
            on { it.get<Int>(EXTRA_Y_COORDINATE) } doReturn initialPosition.y.toInt()
        }
        declareMock<VisualPlacementDrawingUtils> {
            whenever(rotateAndScaleBitmap(any<BitmapWithRotationInfo>(), any<Size>())).thenReturn(
                mock<Bitmap>()
            )
        }
        val viewModel = VisualPlacementViewModel(saveStateHandle)

        viewModel.drawAllSprites()

        val sprite2Place = viewModel.spriteToPlace.value!!
        assertEquals(
            initialPosition, sprite2Place.coordinates.toUnscaledGameCoordinates(layoutRatio)
        )
        assertEquals(expectedScalingFactor, sprite2Place.scalingFactor)
    }

    @Test
    fun shouldFlagImageAsMovedAndSetCoordinates() {
        val spriteToMove = DrawableSprite(mock<Drawable>(), GameCoordinates(1f, 2f), mock<Size>())
        val savedStateHandle = SavedStateHandle()
        val viewModel = VisualPlacementViewModel(savedStateHandle)
        viewModel.spriteToPlace.value = spriteToMove
        val newCoordinates = AndroidCoordinates(10f, 20f)
        val expectedCoordinates = GameCoordinates(-40f, -80f)

        viewModel.setCoordinates(newCoordinates)

        assertTrue(viewModel.imageWasMoved)
        assertEquals(expectedCoordinates, viewModel.spriteToPlace.value!!.coordinates)
    }
}
