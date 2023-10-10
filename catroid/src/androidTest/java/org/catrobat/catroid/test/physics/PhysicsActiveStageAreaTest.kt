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
package org.catrobat.catroid.test.physics

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.Assert
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.ResourceImporter
import org.catrobat.catroid.physics.PhysicsLook
import org.catrobat.catroid.physics.PhysicsObject
import org.catrobat.catroid.physics.PhysicsWorld
import org.catrobat.catroid.physics.content.ActionPhysicsFactory
import org.catrobat.catroid.test.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class PhysicsActiveStageAreaTest {
    private var physicsObject: PhysicsObject? = null
    private var physicsLook: PhysicsLook? = null

    @get:Rule
    var rule = PhysicsTestRule()
    private var sprite: Sprite? = null
    private var physicsWorld: PhysicsWorld? = null
    private var project: Project? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        sprite = rule.sprite
        physicsWorld = rule.physicsWorld
        project = rule.project
        physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        physicsLook = sprite!!.look as PhysicsLook
    }

    @Test
    fun testCircumferenceCalculation() {
        Assert.assertEquals(
            EXPECTED_CIRCUMFERENCE_125X125,
            physicsObject!!.circumference,
            CIRCUMFERENCE_COMPARISON_DELTA
        )
    }

    @Test
    fun testCenteredObjectIsActive() {
        physicsObject!!.setPosition(0f, 0f)
        physicsLook!!.updatePhysicsObjectState(true)
        Assert.assertFalse(physicsLook!!.isHangedUp)
    }

    @Test
    fun testXOutOfBounds() {
        physicsObject!!.x = (PhysicsWorld.activeArea.x / 2.0f
            + physicsObject!!.circumference - 1)
        physicsWorld!!.step(0.05f)
        physicsLook!!.updatePhysicsObjectState(true)
        Assert.assertFalse(physicsLook!!.isHangedUp)
        physicsObject!!.x = PhysicsWorld.activeArea.x / 2.0f + physicsObject!!.circumference + 1
        physicsWorld!!.step(0.05f)
        physicsLook!!.updatePhysicsObjectState(true)
        Assert.assertTrue(physicsLook!!.isHangedUp)
    }

    @Test
    fun testYOutOfBounds() {
        physicsObject!!.y = (PhysicsWorld.activeArea.y / 2.0f
            + physicsObject!!.circumference - 1)
        physicsWorld!!.step(0.05f)
        physicsLook!!.updatePhysicsObjectState(true)
        Assert.assertFalse(physicsLook!!.isHangedUp)
        physicsObject!!.y = PhysicsWorld.activeArea.y / 2.0f + physicsObject!!.circumference + 1
        physicsWorld!!.step(0.05f)
        physicsLook!!.updatePhysicsObjectState(true)
        Assert.assertTrue(physicsLook!!.isHangedUp)
    }

    @Test
    fun testNegativeXYOutOfBounds() {
        physicsObject!!.x = -PhysicsWorld.activeArea.x / 2.0f - physicsObject!!.circumference - 1
        physicsObject!!.y = -PhysicsWorld.activeArea.y / 2.0f - physicsObject!!.circumference - 1
        physicsWorld!!.step(0.05f)
        physicsLook!!.updatePhysicsObjectState(true)
        Assert.assertTrue(physicsLook!!.isHangedUp)
    }

    @Test
    fun testResumeAfterXYHangup() {
        physicsObject!!.x = PhysicsWorld.activeArea.x / 2.0f + physicsObject!!.circumference + 1
        physicsObject!!.y = PhysicsWorld.activeArea.y / 2.0f + physicsObject!!.circumference + 1
        physicsWorld!!.step(0.05f)
        physicsLook!!.updatePhysicsObjectState(true)
        Assert.assertTrue(physicsLook!!.isHangedUp)
        physicsObject!!.setPosition(0.0f, 0.0f)
        physicsWorld!!.step(0.05f)
        physicsLook!!.updatePhysicsObjectState(true)
        Assert.assertFalse(physicsLook!!.isHangedUp)
    }

    @Test
    @Throws(Exception::class)
    fun testSpriteLargerThanActiveAreaHangupAndResume() {
        val rectangle8192x8192FileName =
            PhysicsTestUtils.getInternalImageFilenameFromFilename("rectangle_8192x8192.png")
        val rectangle8192x8192File = ResourceImporter.createImageFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            R.raw.rectangle_8192x8192,
            File(project!!.defaultScene.directory, Constants.IMAGE_DIRECTORY_NAME),
            rectangle8192x8192FileName, 1.0
        )
        sprite = Sprite("TestSprite")
        sprite!!.look = PhysicsLook(sprite, physicsWorld)
        sprite!!.actionFactory = ActionPhysicsFactory()
        val lookdata = PhysicsTestUtils.generateLookData(rectangle8192x8192File)
        sprite!!.look.lookData = lookdata
        physicsWorld!!.step(0.05f)
        physicsLook!!.updatePhysicsObjectState(true)
        Assert.assertNotNull(sprite!!.look.lookData)
        physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        physicsLook = sprite!!.look as PhysicsLook
        Assert.assertFalse(physicsLook!!.isHangedUp)
        physicsObject!!.setX(PhysicsWorld.activeArea.x / 2.0f + physicsObject!!.getCircumference()
                               + 1)
        physicsObject!!.setY(PhysicsWorld.activeArea.y / 2.0f + physicsObject!!.getCircumference()
                               + 1)
        physicsWorld!!.step(0.05f)
        physicsLook!!.updatePhysicsObjectState(true)
        Assert.assertTrue(physicsLook!!.isHangedUp)
        physicsObject!!.setPosition(0.0f, 0.0f)
        physicsWorld!!.step(0.05f)
        physicsLook!!.updatePhysicsObjectState(true)
        Assert.assertFalse(physicsLook!!.isHangedUp)
    }

    companion object {
        private val EXPECTED_CIRCUMFERENCE_125X125 =
            Math.sqrt(2 * Math.pow((125 / 2f).toDouble(), 2.0)).toFloat()
        private const val CIRCUMFERENCE_COMPARISON_DELTA = 1.0f
    }
}
