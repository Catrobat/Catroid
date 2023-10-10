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
package org.catrobat.catroid.test.physics.look

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.Shape
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.ResourceImporter
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.physics.PhysicsLook
import org.catrobat.catroid.physics.PhysicsWorld
import org.catrobat.catroid.physics.shapebuilder.PhysicsShapeBuilder
import org.catrobat.catroid.test.R
import org.catrobat.catroid.test.physics.PhysicsTestUtils
import org.catrobat.catroid.test.utils.Reflection
import org.catrobat.catroid.test.utils.TestUtils
import org.hamcrest.Matchers
import org.hamcrest.number.OrderingComparison
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class PhysicsLookDataTest {
    private var physicsWorld: PhysicsWorld? = null
    private val projectName = "testProject"
    private var sprite: Sprite? = null
    private var lookData: LookData? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        physicsWorld = PhysicsWorld(1920, 1600)
        val projectDir = File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, projectName)
        if (projectDir.exists()) {
            StorageOperations.deleteDir(projectDir)
        }
        val testImageFilename =
            PhysicsTestUtils.getInternalImageFilenameFromFilename("testImage.png")
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        XstreamSerializer.getInstance().saveProject(project)
        ProjectManager.getInstance().currentProject = project
        val testImage = ResourceImporter.createImageFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            R.raw.multible_mixed_polygons,
            File(project.defaultScene.directory, Constants.IMAGE_DIRECTORY_NAME),
            testImageFilename, 1.0
        )
        sprite = Sprite("TestSprite")
        lookData = PhysicsTestUtils.generateLookData(testImage)
        sprite!!.lookList.add(lookData)
        val pixmap = PhysicsTestUtils.getPixmapFromFile(testImage)
        lookData!!.setPixmap(pixmap)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        TestUtils.deleteProjects(projectName)
    }

    @Test
    fun testShapeComputationOfLook() {
        val physicsShapeBuilder = PhysicsShapeBuilder.getInstance()
        val shapes = physicsShapeBuilder.getScaledShapes(
            lookData,
            sprite!!.look.sizeInUserInterfaceDimensionUnit / 100f
        )
        Assert.assertThat(shapes.size, Matchers.`is`(OrderingComparison.greaterThan(0)))
        physicsShapeBuilder.reset()
    }

    @Test
    @Throws(Exception::class)
    fun testSetScale() {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        val physicsLook = PhysicsLook(sprite, physicsWorld)
        physicsLook.lookData = lookData
        val testScaleFactor = 1.1f
        val expectedVertices = arrayOf(
            Vector2(10.84f, -7.31f),
            Vector2(10.84f, -0.6f),
            Vector2(9.63f, 10.62f),
            Vector2(-10.84f, 10.62f),
            Vector2(-10.84f, 6.44f),
            Vector2(-3.35f, -7.31f),
            Vector2(-0.06f, -10.61f),
            Vector2(7.54f, -10.61f)
        )
        physicsLook.setScale(testScaleFactor, testScaleFactor)
        val scaledShapes = Reflection.getPrivateField(physicsObject, "shapes") as Array<Shape>
        junit.framework.Assert.assertNotNull(scaledShapes)
        junit.framework.Assert.assertEquals(1, scaledShapes.size)
        val scaledShape = scaledShapes[0]
        junit.framework.Assert.assertEquals(Shape.Type.Polygon, scaledShape.type)
        val scaledVertexCount = (scaledShape as PolygonShape).vertexCount
        junit.framework.Assert.assertEquals(8, scaledVertexCount)
        val scaledVertices = arrayOfNulls<Vector2>(8)
        for (idx in 0 until scaledVertexCount) {
            scaledVertices[idx] = Vector2()
            scaledShape.getVertex(idx, scaledVertices[idx])
        }
        Assert.assertArrayEquals(expectedVertices, scaledVertices)
    }
}
