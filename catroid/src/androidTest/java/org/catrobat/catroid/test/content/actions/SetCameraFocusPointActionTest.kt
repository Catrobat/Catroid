/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageListener
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SetCameraFocusPointActionTest {
    companion object {
        private const val SCREEN_WIDTH = 1080.0f
        private const val SCREEN_HEIGHT = 1920.0f
    }

    private lateinit var spriteToFocus: Sprite
    private lateinit var stageListener: StageListener

    @Before
    fun setUp() {
        StageActivity.stageListener = StageListener()
        stageListener = StageActivity.stageListener
        stageListener.camera = OrthographicCamera()
        stageListener.camera.position.set(0.0f, 0.0f, 0.0f)
        stageListener.setVirtualWidthHalf(SCREEN_WIDTH / 2)
        stageListener.setVirtualHeightHalf(SCREEN_HEIGHT / 2)
        spriteToFocus = Sprite("spriteToFocus")
        spriteToFocus.look.setPositionInUserInterfaceDimensionUnit(0.0f, 0.0f)
    }

    @Test
    fun testCameraMovingWithSprite() {
        createAction(0.0f, 0.0f).act(1.0f)
        setPositionAndFocusCamera(10.0f, 10.0f)
        assertEquals(
            spriteToFocus.look.xInUserInterfaceDimensionUnit,
            stageListener.camera.position.x)
        assertEquals(
            spriteToFocus.look.yInUserInterfaceDimensionUnit,
            stageListener.camera.position.y)
    }

    @Test
    fun testNoChangeInPositionOfOtherSprite() {
        val sprite = Sprite("testSprite")
        sprite.look.setPositionInUserInterfaceDimensionUnit(200.0f, 100.0f)
        createAction(0.0f, 0.0f).act(1.0f)
        setPositionAndFocusCamera(10.0f, 10.0f)
        assertEquals(200.0f, sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(100.0f, sprite.look.yInUserInterfaceDimensionUnit)
    }

    @Test
    fun testNoChangeWithinHorizontalFlex() {
        createAction(50.0f, 0.0f).act(1.0f)
        setPositionAndFocusCamera(SCREEN_WIDTH / 2 * 0.5f, 0.0f)
        assertEquals(Vector3(0.0f, 0.0f, 0.0f), StageActivity.stageListener.camera.position)
    }

    @Test
    fun testChangeOutsideHorizontalFlex() {
        createAction(50.0f, 0.0f).act(1.0f)
        setPositionAndFocusCamera(SCREEN_WIDTH / 2 * 0.6f, 0.0f)
        assertEquals(
            Vector3(SCREEN_WIDTH / 2 * 0.1f, 0.0f, 0.0f),
            stageListener.camera.position)
    }

    @Test
    fun testNoChangeWithinVerticalFlex() {
        createAction(0.0f, 50.0f).act(1.0f)
        setPositionAndFocusCamera(0.0f, SCREEN_HEIGHT / 2 * 0.5f)
        assertEquals(
            Vector3(0.0f, 0.0f, 0.0f),
            stageListener.camera.position)
    }

    @Test
    fun testChangeOutsideVerticalFlex() {
        createAction(0.0f, 50.0f).act(1.0f)
        setPositionAndFocusCamera(0.0f, SCREEN_HEIGHT / 2 * 0.6f)
        assertEquals(
            Vector3(0.0f, SCREEN_HEIGHT / 2 * 0.1f, 0.0f),
            stageListener.camera.position)
    }

    @Test
    fun testChangeOutSideHorizontalAndVerticalFlex() {
        createAction(50.0f, 50.0f).act(1.0f)
        setPositionAndFocusCamera(SCREEN_WIDTH / 2 * 0.6f, SCREEN_HEIGHT / 2 * 0.6f)
        assertEquals(
            Vector3(SCREEN_WIDTH / 2 * 0.1f, SCREEN_HEIGHT / 2 * 0.1f, 0.0f),
            stageListener.camera.position)
    }

    @Test
    fun testNoCameraChangeMultiplePositionChanges() {
        createAction(50.0f, 50.0f).act(1.0f)
        setPositionAndFocusCamera(SCREEN_WIDTH / 2 * 0.4f, SCREEN_HEIGHT / 2 * 0.5f)
        setPositionAndFocusCamera(SCREEN_WIDTH / 2 * 0.2f, SCREEN_HEIGHT / 2 * 0.1f)
        setPositionAndFocusCamera(SCREEN_WIDTH / 2 * 0.3f, SCREEN_HEIGHT / 2 * 0.3f)
        setPositionAndFocusCamera(SCREEN_WIDTH / 2 * 0.5f, SCREEN_HEIGHT / 2 * 0.5f)
        assertEquals(
            Vector3(0.0f, 0.0f, 0.0f),
            stageListener.camera.position)
    }

    private fun createAction(horizontalFlex: Float, verticalFlex: Float): Action {
        return spriteToFocus.actionFactory.createSetCameraFocusPointAction(
            spriteToFocus,
            Formula(FormulaElement(FormulaElement.ElementType.NUMBER, horizontalFlex.toString(), null)),
            Formula(FormulaElement(FormulaElement.ElementType.NUMBER, verticalFlex.toString(), null))
        )
    }

    private fun setPositionAndFocusCamera(x: Float, y: Float) {
        spriteToFocus.look.setPositionInUserInterfaceDimensionUnit(x, y)
        stageListener.calculateCameraPositionForFocusedSprite()
    }
}
