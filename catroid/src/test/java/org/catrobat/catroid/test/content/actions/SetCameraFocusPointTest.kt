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

package org.catrobat.catroid.test.content.actions

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.stage.CameraPositioner
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

private const val SCREEN_WIDTH_HALF = 540.0f
private const val SCREEN_HEIGHT_HALF = 960.0f

@RunWith(JUnit4::class)
class SetCameraFocusPointTest {

    private val camera = Mockito.spy(OrthographicCamera())
    private val cameraPositioner = CameraPositioner(camera, SCREEN_HEIGHT_HALF, SCREEN_WIDTH_HALF)
    private lateinit var spriteToFocus: Sprite

    @Before
    fun setUp() {
        Mockito.doNothing().`when`(camera).update()
        camera.position.set(0.0f, 0.0f, 0.0f)
        spriteToFocus = Sprite("spriteToFocus")
        spriteToFocus.look.setPositionInUserInterfaceDimensionUnit(0.0f, 0.0f)
    }

    @Test
    fun testCameraMovingWithSprite() {
        prepareCameraPositioner(0.0f, 0.0f)
        setPositionAndFocusCamera(10.0f, 10.0f)
        assertEquals(spriteToFocus.look.xInUserInterfaceDimensionUnit, camera.position.x)
        assertEquals(spriteToFocus.look.yInUserInterfaceDimensionUnit, camera.position.y)
    }

    @Test
    fun testNoChangeInPositionOfOtherSprite() {
        camera.position.set(0.0f, 0.0f, 0.0f)
        val sprite = Sprite("testSprite")
        sprite.look.setPositionInUserInterfaceDimensionUnit(200.0f, 100.0f)
        prepareCameraPositioner(0.0f, 0.0f)
        setPositionAndFocusCamera(10.0f, 10.0f)
        assertEquals(200.0f, sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(100.0f, sprite.look.yInUserInterfaceDimensionUnit)
    }

    @Test
    fun testNoChangeWithinHorizontalFlex() {
        camera.position.set(0.0f, 0.0f, 0.0f)
        prepareCameraPositioner(50.0f, 0.0f)
        setPositionAndFocusCamera(SCREEN_WIDTH_HALF * 0.5f, 0.0f)
        assertEquals(Vector3(0.0f, 0.0f, 0.0f), camera.position)
    }

    @Test
    fun testChangeOutsideHorizontalFlex() {
        camera.position.set(0.0f, 0.0f, 0.0f)
        prepareCameraPositioner(50.0f, 0.0f)
        setPositionAndFocusCamera(SCREEN_WIDTH_HALF * 0.6f, 0.0f)
        assertEquals(Vector3(SCREEN_WIDTH_HALF * 0.1f, 0.0f, 0.0f), camera.position)
    }

    @Test
    fun testNoChangeWithinVerticalFlex() {
        camera.position.set(0.0f, 0.0f, 0.0f)
        prepareCameraPositioner(0.0f, 50.0f)
        setPositionAndFocusCamera(0.0f, SCREEN_HEIGHT_HALF * 0.5f)
        assertEquals(Vector3(0.0f, 0.0f, 0.0f), camera.position)
    }

    @Test
    fun testChangeOutsideVerticalFlex() {
        camera.position.set(0.0f, 0.0f, 0.0f)
        prepareCameraPositioner(0.0f, 50.0f)
        setPositionAndFocusCamera(0.0f, SCREEN_HEIGHT_HALF * 0.6f)
        assertEquals(Vector3(0.0f, SCREEN_HEIGHT_HALF * 0.1f, 0.0f), camera.position)
    }

    @Test
    fun testChangeOutSideHorizontalAndVerticalFlex() {
        camera.position.set(0.0f, 0.0f, 0.0f)
        prepareCameraPositioner(50.0f, 50.0f)
        setPositionAndFocusCamera(SCREEN_WIDTH_HALF * 0.6f, SCREEN_HEIGHT_HALF * 0.6f)
        assertEquals(
            Vector3(SCREEN_WIDTH_HALF * 0.1f, SCREEN_HEIGHT_HALF * 0.1f, 0.0f),
            camera.position
        )
    }

    @Test
    fun testNoCameraChangeMultiplePositionChanges() {
        camera.position.set(0.0f, 0.0f, 0.0f)
        prepareCameraPositioner(50.0f, 50.0f)
        setPositionAndFocusCamera(SCREEN_WIDTH_HALF * 0.4f, SCREEN_HEIGHT_HALF * 0.5f)
        setPositionAndFocusCamera(SCREEN_WIDTH_HALF * 0.2f, SCREEN_HEIGHT_HALF * 0.1f)
        setPositionAndFocusCamera(SCREEN_WIDTH_HALF * 0.3f, SCREEN_HEIGHT_HALF * 0.3f)
        setPositionAndFocusCamera(SCREEN_WIDTH_HALF * 0.5f, SCREEN_HEIGHT_HALF * 0.5f)
        assertEquals(Vector3(0.0f, 0.0f, 0.0f), camera.position)
    }

    @Test
    fun testCameraChangeMultiplePositionChanges() {
        camera.position.set(0.0f, 0.0f, 0.0f)
        prepareCameraPositioner(50.0f, 50.0f)
        setPositionAndFocusCamera(SCREEN_WIDTH_HALF * 0.6f, SCREEN_HEIGHT_HALF * 0.8f)
        setPositionAndFocusCamera(SCREEN_WIDTH_HALF * 0.8f, SCREEN_HEIGHT_HALF * 0.9f)
        setPositionAndFocusCamera(SCREEN_WIDTH_HALF * 0.7f, SCREEN_HEIGHT_HALF * 0.6f)
        setPositionAndFocusCamera(SCREEN_WIDTH_HALF * 0.9f, SCREEN_HEIGHT_HALF * 0.9f)
        assertEquals(
            Vector3(SCREEN_WIDTH_HALF * 0.4f, SCREEN_HEIGHT_HALF * 0.4f, 0.0f),
            camera.position
        )
    }

    private fun setPositionAndFocusCamera(x: Float, y: Float) {
        spriteToFocus.look.setPositionInUserInterfaceDimensionUnit(x, y)
        cameraPositioner.updateCameraPositionForFocusedSprite()
    }

    private fun prepareCameraPositioner(horizontalFlex: Float, verticalFlex: Float) {
        cameraPositioner.horizontalFlex = horizontalFlex
        cameraPositioner.verticalFlex = verticalFlex
        cameraPositioner.spriteToFocusOn = spriteToFocus
    }
}
