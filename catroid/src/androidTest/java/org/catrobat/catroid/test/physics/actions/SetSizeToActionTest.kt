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
package org.catrobat.catroid.test.physics.actions

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import org.catrobat.catroid.physics.PhysicsLook
import org.catrobat.catroid.physics.PhysicsObject
import org.catrobat.catroid.test.physics.PhysicsTestRule
import org.junit.Before
import kotlin.Throws
import com.badlogic.gdx.math.Vector2
import org.catrobat.catroid.test.utils.TestUtils
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import junit.framework.Assert
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.test.utils.Reflection
import org.junit.Rule
import org.junit.Test
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class SetSizeToActionTest {
    private var physicsLook: PhysicsLook? = null
    private var physicsObject: PhysicsObject? = null

    @get:Rule
    var rule = PhysicsTestRule()
    private var sprite: Sprite? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        sprite = rule.sprite
        physicsLook = sprite!!.look as PhysicsLook
        physicsObject = Reflection.getPrivateField(physicsLook, "physicsObject") as PhysicsObject
    }

    @Test
    fun testSizeLarger() {
        val oldAabbDimensions = physicsObject!!.boundaryBoxDimensions
        val oldCircumference = physicsObject!!.circumference
        val scaleFactor = 500.0f
        performSetSizeToAction(scaleFactor)
        val newAabbDimensions = physicsObject!!.boundaryBoxDimensions
        val newCircumference = physicsObject!!.circumference
        Assert.assertEquals(
            oldAabbDimensions.x * (scaleFactor / 100.0f),
            newAabbDimensions.x,
            SIZE_COMPARISON_DELTA * scaleFactor / 100f
        )
        Assert.assertEquals(
            oldAabbDimensions.y * (scaleFactor / 100.0f),
            newAabbDimensions.y,
            SIZE_COMPARISON_DELTA * scaleFactor / 100f
        )
        Assert.assertEquals(
            oldCircumference * (scaleFactor / 100.0f),
            newCircumference,
            SIZE_COMPARISON_DELTA * scaleFactor / 100f
        )
    }

    @Test
    fun testSizeSmaller() {
        val smallerSizeComparisonDelta = 1.5f
        val oldAabbDimensions = physicsObject!!.boundaryBoxDimensions
        val oldCircumference = physicsObject!!.circumference
        val scaleFactor = 10.0f
        performSetSizeToAction(scaleFactor)
        val newAabbDimensions = physicsObject!!.boundaryBoxDimensions
        val newCircumference = physicsObject!!.circumference
        Assert.assertEquals(
            oldAabbDimensions.x * (scaleFactor / 100.0f),
            newAabbDimensions.x,
            smallerSizeComparisonDelta
        )
        Assert.assertEquals(
            oldAabbDimensions.y * (scaleFactor / 100.0f),
            newAabbDimensions.y,
            smallerSizeComparisonDelta
        )
        Assert.assertEquals(
            oldCircumference * (scaleFactor / 100.0f),
            newCircumference,
            smallerSizeComparisonDelta
        )
    }

    @Test
    fun testSizeSame() {
        val oldAabbDimensions = physicsObject!!.boundaryBoxDimensions
        val oldCircumference = physicsObject!!.circumference
        val scaleFactor = 100.0f
        performSetSizeToAction(scaleFactor)
        val newAabbDimensions = physicsObject!!.boundaryBoxDimensions
        val newCircumference = physicsObject!!.circumference
        Assert.assertEquals(
            oldAabbDimensions.x.toDouble(),
            newAabbDimensions.x.toDouble(),
            TestUtils.DELTA
        )
        Assert.assertEquals(
            oldAabbDimensions.y.toDouble(),
            newAabbDimensions.y.toDouble(),
            TestUtils.DELTA
        )
        Assert.assertEquals(
            oldCircumference.toDouble(),
            newCircumference.toDouble(),
            TestUtils.DELTA
        )
    }

    @Test
    fun testSizeSmallerAndOriginal() {
        val oldAabbDimensions = physicsObject!!.boundaryBoxDimensions
        val oldCircumference = physicsObject!!.circumference
        var scaleFactor = 25.0f
        performSetSizeToAction(scaleFactor)
        scaleFactor = 100.0f
        performSetSizeToAction(scaleFactor)
        val newAabbDimensions = physicsObject!!.boundaryBoxDimensions
        val newCircumference = physicsObject!!.circumference
        Assert.assertEquals(
            oldAabbDimensions.x.toDouble(),
            newAabbDimensions.x.toDouble(),
            TestUtils.DELTA
        )
        Assert.assertEquals(
            oldAabbDimensions.y.toDouble(),
            newAabbDimensions.y.toDouble(),
            TestUtils.DELTA
        )
        Assert.assertEquals(
            oldCircumference.toDouble(),
            newCircumference.toDouble(),
            TestUtils.DELTA
        )
    }

    @Test
    fun testSizeZero() {
        val scaleFactor = 0.0f
        performSetSizeToAction(scaleFactor)
        val newAabbDimensions = physicsObject!!.boundaryBoxDimensions
        val newCircumference = physicsObject!!.circumference
        Assert.assertEquals(1.0, newAabbDimensions.x.toDouble(), TestUtils.DELTA)
        Assert.assertEquals(1.0, newAabbDimensions.y.toDouble(), TestUtils.DELTA)
        Assert.assertEquals(0.0, newCircumference.toDouble(), TestUtils.DELTA)
    }

    private fun performSetSizeToAction(scaleFactor: Float) {
        sprite!!.actionFactory.createSetSizeToAction(
            sprite, SequenceAction(),
            Formula(scaleFactor)
        ).act(1.0f)
    }

    companion object {
        const val SIZE_COMPARISON_DELTA = 1.0f
    }
}
