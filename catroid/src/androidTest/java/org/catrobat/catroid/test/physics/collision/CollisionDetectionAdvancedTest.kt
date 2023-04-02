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
package org.catrobat.catroid.test.physics.collision

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.io.ResourceImporter
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.sensing.CollisionDetection
import org.catrobat.catroid.test.R
import org.catrobat.catroid.test.physics.PhysicsTestUtils
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.utils.Utils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class CollisionDetectionAdvancedTest {
    protected var project: Project? = null
    protected var sprite1: Sprite? = null
    protected var sprite2: Sprite? = null
    @Throws(IOException::class)
    private fun initializeSprite(sprite: Sprite, resourceId: Int, filename: String) {
        sprite.look = Look(sprite)
        sprite.actionFactory = ActionFactory()
        val hashedFileName = Utils.md5Checksum(filename) + "_" + filename
        val file = ResourceImporter.createImageFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            resourceId,
            File(project!!.defaultScene.directory, Constants.IMAGE_DIRECTORY_NAME),
            hashedFileName, 1.0
        )
        val lookData = PhysicsTestUtils.generateLookData(file)
        val collisionInformation = lookData.collisionInformation
        collisionInformation.loadCollisionPolygon()
        sprite.look.lookData = lookData
        sprite.lookList.add(lookData)
        sprite.look.height = sprite.look.lookData.pixmap.height.toFloat()
        sprite.look.width = sprite.look.lookData.pixmap.width.toFloat()
        sprite.look.setPositionInUserInterfaceDimensionUnit(0f, 0f)
    }

    @Before
    @Throws(Exception::class)
    fun setUp() {
        TestUtils.deleteProjects()
        project = Project(
            ApplicationProvider.getApplicationContext(),
            TestUtils.DEFAULT_TEST_PROJECT_NAME
        )
        sprite1 = Sprite("TestSprite1")
        sprite2 = Sprite("TestSprite2")
        project!!.defaultScene.addSprite(sprite1)
        project!!.defaultScene.addSprite(sprite2)
        XstreamSerializer.getInstance().saveProject(project)
        ProjectManager.getInstance().currentProject = project
        initializeSprite(sprite1!!, R.raw.collision_donut, "collision_donut.png")
        initializeSprite(sprite2!!, R.raw.icon, "icon.png")
        val collisionPolygons1 = sprite1!!.look.lookData.collisionInformation.collisionPolygons
        val collisionPolygons2 = sprite2!!.look.lookData.collisionInformation.collisionPolygons
        Assert.assertNotNull(collisionPolygons1)
        Assert.assertEquals(2, collisionPolygons1.size.toLong())
        Assert.assertNotNull(collisionPolygons2)
        Assert.assertEquals(3, collisionPolygons2.size.toLong())
        XstreamSerializer.getInstance().saveProject(project)
    }

    @Test
    fun testCollisionBetweenMovingLooks() {
        junit.framework.Assert.assertFalse(
            CollisionDetection.checkCollisionBetweenLooks(
                sprite1!!.look,
                sprite2!!.look
            )
        )
        val steps = 200.0f
        val factory = ActionFactory()
        sprite2!!.actionFactory = factory
        val moveNSteptsaction =
            factory.createMoveNStepsAction(sprite2, SequenceAction(), Formula(steps))
        moveNSteptsaction.act(1.0f)
        junit.framework.Assert.assertTrue(
            CollisionDetection.checkCollisionBetweenLooks(
                sprite1!!.look,
                sprite2!!.look
            )
        )
    }

    @Test
    fun testCollisionBetweenExpandingLooks() {
        junit.framework.Assert.assertFalse(
            CollisionDetection.checkCollisionBetweenLooks(
                sprite1!!.look,
                sprite2!!.look
            )
        )
        val size = 300.0f
        val factory = ActionFactory()
        sprite2!!.actionFactory = factory
        val createChangeSizeByNAction =
            factory.createChangeSizeByNAction(sprite2, SequenceAction(), Formula(size))
        createChangeSizeByNAction.act(1.0f)
        junit.framework.Assert.assertTrue(
            CollisionDetection.checkCollisionBetweenLooks(
                sprite1!!.look,
                sprite2!!.look
            )
        )
    }
}
