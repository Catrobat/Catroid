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
import junit.framework.Assert
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.ResourceImporter
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.sensing.CollisionInformation
import org.catrobat.catroid.test.R
import org.catrobat.catroid.test.physics.PhysicsTestUtils
import org.catrobat.catroid.test.utils.TestUtils
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class CollisionDetectionPolygonCreationTest {
    protected var project: Project? = null
    protected var sprite: Sprite? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        TestUtils.deleteProjects()
        project = Project(
            ApplicationProvider.getApplicationContext(),
            TestUtils.DEFAULT_TEST_PROJECT_NAME
        )
        sprite = Sprite("testSprite")
        project!!.defaultScene.addSprite(sprite)
        XstreamSerializer.getInstance().saveProject(project)
        ProjectManager.getInstance().currentProject = project
    }

    @Throws(IOException::class)
    protected fun generateCollisionInformation(
        resourceId: Int,
        filename: String?
    ): CollisionInformation {
        val hashedFileName = PhysicsTestUtils.getInternalImageFilenameFromFilename(filename)
        val file = ResourceImporter.createImageFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            resourceId,
            File(project!!.defaultScene.directory, Constants.IMAGE_DIRECTORY_NAME),
            hashedFileName, 1.0
        )
        val lookData = PhysicsTestUtils.generateLookData(file)
        sprite!!.lookList.add(lookData)
        val collisionInformation = lookData.collisionInformation
        collisionInformation.loadCollisionPolygon()
        return collisionInformation
    }

    @Test
    @Throws(IOException::class)
    fun testRectangle() {
        val collisionInformation =
            generateCollisionInformation(R.raw.rectangle_125x125, "rectangle_125x125.png")
        collisionInformation.printDebugCollisionPolygons()
        Assert.assertNotNull(collisionInformation.collisionPolygons)
        Assert.assertEquals(1, collisionInformation.collisionPolygons.size)
        org.junit.Assert.assertArrayEquals(
            floatArrayOf(0.0f, 0.0f, 0.0f, 125.0f, 125.0f, 125.0f, 125.0f, 0.0f),
            collisionInformation.collisionPolygons[0].vertices, DELTA
        )
    }

    @Test
    @Throws(IOException::class)
    fun testSimpleConvexPolygon() {
        val collisionInformation = generateCollisionInformation(
            R.raw.complex_single_convex_polygon,
            "complex_single_convex_polygon.png"
        )
        collisionInformation.printDebugCollisionPolygons()
        Assert.assertNotNull(collisionInformation.collisionPolygons)
        Assert.assertEquals(1, collisionInformation.collisionPolygons.size)
        org.junit.Assert.assertArrayEquals(
            floatArrayOf(
                0.0f,
                47.0f,
                17.0f,
                98.0f,
                52.0f,
                98.0f,
                68.0f,
                44.0f,
                52.0f,
                0.0f,
                17.0f,
                0.0f
            ),
            collisionInformation.collisionPolygons[0].vertices, DELTA
        )
    }

    @Test
    @Throws(IOException::class)
    fun testMultipleConcavePolygons() {
        val collisionInformation = generateCollisionInformation(
            R.raw.multible_concave_polygons,
            "multible_concave_polygons.png"
        )
        collisionInformation.printDebugCollisionPolygons()
        Assert.assertNotNull(collisionInformation.collisionPolygons)
        Assert.assertEquals(2, collisionInformation.collisionPolygons.size)
        org.junit.Assert.assertArrayEquals(
            floatArrayOf(
                0.0f, 110.0f, 0.0f, 185.0f, 91.0f, 185.0f, 91.0f, 136.0f, 34.0f, 136.0f,
                34.0f, 110.0f
            ),
            collisionInformation.collisionPolygons[0].vertices, DELTA
        )
        org.junit.Assert.assertArrayEquals(
            floatArrayOf(
                128.0f,
                30.0f,
                128.0f,
                91.0f,
                159.0f,
                91.0f,
                159.0f,
                121.0f,
                227.0f,
                121.0f,
                227.0f,
                91.0f,
                257.0f,
                91.0f,
                257.0f,
                30.0f,
                227.0f,
                30.0f,
                227.0f,
                0.0f,
                159.0f,
                0.0f,
                159.0f,
                30.0f
            ),
            collisionInformation.collisionPolygons[1].vertices, DELTA
        )
    }

    @Test
    @Throws(IOException::class)
    fun testDonutPolygons() {
        val collisionInformation = generateCollisionInformation(
            R.raw.collision_donut,
            "collision_donut.png"
        )
        collisionInformation.printDebugCollisionPolygons()
        Assert.assertNotNull(collisionInformation.collisionPolygons)
        Assert.assertEquals(2, collisionInformation.collisionPolygons.size)
        org.junit.Assert.assertArrayEquals(
            floatArrayOf(
                0.0f,
                228.0f,
                9.0f,
                321.0f,
                57.0f,
                411.0f,
                136.0f,
                474.0f,
                228.0f,
                500.0f,
                305.0f,
                495.0f,
                375.0f,
                468.0f,
                436.0f,
                419.0f,
                474.0f,
                364.0f,
                497.0f,
                295.0f,
                499.0f,
                218.0f,
                481.0f,
                151.0f,
                443.0f,
                89.0f,
                385.0f,
                38.0f,
                321.0f,
                9.0f,
                179.0f,
                9.0f,
                115.0f,
                38.0f,
                57.0f,
                89.0f,
                19.0f,
                151.0f
            ),
            collisionInformation.collisionPolygons[0].vertices, DELTA
        )
        org.junit.Assert.assertArrayEquals(
            floatArrayOf(
                125.0f,
                248.0f,
                154.0f,
                330.0f,
                201.0f,
                365.0f,
                248.0f,
                375.0f,
                313.0f,
                358.0f,
                365.0f,
                299.0f,
                374.0f,
                234.0f,
                346.0f,
                170.0f,
                285.0f,
                130.0f,
                206.0f,
                133.0f,
                150.0f,
                175.0f
            ),
            collisionInformation.collisionPolygons[1].vertices, DELTA
        )
    }

    companion object {
        private const val DELTA = Float.MIN_VALUE
    }
}
