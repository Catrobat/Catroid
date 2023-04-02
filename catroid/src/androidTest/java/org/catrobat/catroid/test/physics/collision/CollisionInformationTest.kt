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

import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.badlogic.gdx.math.Polygon
import junit.framework.Assert
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.io.ResourceImporter
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.sensing.CollisionInformation
import org.catrobat.catroid.sensing.CollisionPolygonVertex
import org.catrobat.catroid.test.R
import org.catrobat.catroid.test.physics.PhysicsTestUtils
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.utils.Utils
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException
import java.util.Arrays

@RunWith(AndroidJUnit4::class)
class CollisionInformationTest {
    @Test
    fun testCheckMetaString() {
        val isNull: String? = null
        Assert.assertFalse(CollisionInformation.checkMetaDataString(isNull))
        val empty = ""
        Assert.assertFalse(CollisionInformation.checkMetaDataString(empty))
        val faulty1 = "1.0;1.0;1.0"
        Assert.assertFalse(CollisionInformation.checkMetaDataString(faulty1))
        val faulty2 = "1.0;1.0;1.0;1.0;1.0;1.0|"
        Assert.assertFalse(CollisionInformation.checkMetaDataString(faulty2))
        val faulty3 = "1.0;1.0;1.0;1.0;1.0;1.0|1.0;1.0;1.0"
        Assert.assertFalse(CollisionInformation.checkMetaDataString(faulty3))
        val faulty4 = "|1.0;1.0;1.0;1.0;1.0;1.0"
        Assert.assertFalse(CollisionInformation.checkMetaDataString(faulty4))
        val faulty5 = "1.0;1.0;1.0;1.0;1.0,1.0"
        Assert.assertFalse(CollisionInformation.checkMetaDataString(faulty5))
        val faulty6 = "1.0;1.0;1.0;1.0;1.0;1.0||1.0;1.0;1.0;1.0;1.0;1.0"
        Assert.assertFalse(CollisionInformation.checkMetaDataString(faulty6))
        val faulty7 = "1.0;1.0;1.0;1.0;1.0;1.0;1.0;1.0;1.0"
        Assert.assertFalse(CollisionInformation.checkMetaDataString(faulty7))
        val correct1 = "1.0;1.0;1.0;1.0;1.0;1.0"
        Assert.assertTrue(CollisionInformation.checkMetaDataString(correct1))
        val correct2 = "1.0;1.0;1.0;1.0;1.0;1.0|1.0;1.0;1.0;1.0;1.0;1.0"
        Assert.assertTrue(CollisionInformation.checkMetaDataString(correct2))
        val correct3 = "1.0;1.0;1.0;1.0;1.0;1.0;1.0;1.0;1.0;1.0;1.0;1.0"
        Assert.assertTrue(CollisionInformation.checkMetaDataString(correct3))
        val correct4 = "1.0;1.0;1.0;1.0;1.0;1.0|1.0;1.0;1.0;1.0;1.0;1.0|1.0;1.0;1.0;1.0;1.0;1.0"
        Assert.assertTrue(CollisionInformation.checkMetaDataString(correct4))
    }

    @Test
    fun testCreateCollisionPolygonByHitbox() {
        val bitmap = Bitmap.createBitmap(200, 100, Bitmap.Config.ALPHA_8)
        val polygons = CollisionInformation.createCollisionPolygonByHitbox(bitmap)
        org.junit.Assert.assertArrayEquals(
            floatArrayOf(0.0f, 0.0f, 200.0f, 0.0f, 200.0f, 100.0f, 0.0f, 100.0f),
            polygons[0].vertices, DELTA
        )
    }

    @Test
    @Throws(IOException::class)
    fun testGetCollisionPolygonFromPNGMeta() {
        TestUtils.deleteProjects()
        val project = Project(
            ApplicationProvider.getApplicationContext(),
            TestUtils.DEFAULT_TEST_PROJECT_NAME
        )
        XstreamSerializer.getInstance().saveProject(project)
        ProjectManager.getInstance().currentProject = project
        val filename = PhysicsTestUtils.getInternalImageFilenameFromFilename("polygon_in_file.png")
        val file = ResourceImporter.createImageFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            R.raw.polygon_in_file,
            File(project.defaultScene.directory, Constants.IMAGE_DIRECTORY_NAME),
            filename, 1.0
        )
        val collisionPolygons =
            CollisionInformation.getCollisionPolygonFromPNGMeta(file.absolutePath)
        Assert.assertNotNull(collisionPolygons)
        MatcherAssert.assertThat(collisionPolygons.size, Matchers.`is`(Matchers.greaterThan(0)))
        Assert.assertEquals(1, collisionPolygons.size)
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
            collisionPolygons[0].vertices, DELTA
        )
    }

    @Test
    @Throws(IOException::class)
    fun testWriteReadCollisionVerticesToPNGMeta() {
        TestUtils.deleteProjects()
        val project = Project(
            ApplicationProvider.getApplicationContext(),
            TestUtils.DEFAULT_TEST_PROJECT_NAME
        )
        XstreamSerializer.getInstance().saveProject(project)
        ProjectManager.getInstance().currentProject = project
        val filename = "collision_donut.png"
        val hashedFileName = Utils.md5Checksum(filename) + "_" + filename
        val file = ResourceImporter.createImageFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            R.raw.collision_donut,
            File(project.defaultScene.directory, Constants.IMAGE_DIRECTORY_NAME),
            hashedFileName, 1.0
        )
        val firstVertices = floatArrayOf(0.0f, 0.0f, 111.0f, 0.0f, 111.0f, 222.0f)
        val secondVertices = floatArrayOf(10.0f, 10.0f, 20.0f, 10.0f, 20.0f, 20.0f, 10.0f, 20.0f)
        val polygons = arrayOf(Polygon(firstVertices), Polygon(secondVertices))
        CollisionInformation.writeCollisionVerticesToPNGMeta(polygons, file.absolutePath)
        val testPolygons = CollisionInformation.getCollisionPolygonFromPNGMeta(file.absolutePath)
        val sameVertices = (Arrays.equals(testPolygons[0].vertices, firstVertices)
            && Arrays.equals(testPolygons[1].vertices, secondVertices))
        Assert.assertTrue(sameVertices)
    }

    @Test
    @Throws(IOException::class)
    fun testWriteReadEmptyCollisionVerticesToPNGMeta() {
        TestUtils.deleteProjects()
        val project = Project(
            ApplicationProvider.getApplicationContext(),
            TestUtils.DEFAULT_TEST_PROJECT_NAME
        )
        XstreamSerializer.getInstance().saveProject(project)
        ProjectManager.getInstance().currentProject = project
        val filename = "collision_donut.png"
        val hashedFileName = Utils.md5Checksum(filename) + "_" + filename
        val file = ResourceImporter.createImageFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            R.raw.collision_donut,
            File(project.defaultScene.directory, Constants.IMAGE_DIRECTORY_NAME),
            hashedFileName, 1.0
        )
        val polygons = arrayOfNulls<Polygon>(0)
        CollisionInformation.writeCollisionVerticesToPNGMeta(polygons, file.absolutePath)
        val testPolygons = CollisionInformation.getCollisionPolygonFromPNGMeta(file.absolutePath)
        Assert.assertEquals(0, testPolygons.size)
    }

    private fun getFloatArrayFromCollisionPolygonVertexArrayList(arrayList: ArrayList<CollisionPolygonVertex>): FloatArray {
        val array = FloatArray(arrayList.size * 4)
        for (i in arrayList.indices) {
            array[i * 4] = arrayList[i].startX
            array[i * 4 + 1] = arrayList[i].startY
            array[i * 4 + 2] = arrayList[i].endX
            array[i * 4 + 3] = arrayList[i].endY
        }
        return array
    }

    @Test
    fun testCreateHorizontalAndVerticalVertices() {
        val grid = arrayOf(
            booleanArrayOf(false, false, true, true, true, false, false),
            booleanArrayOf(false, false, true, false, true, false, false),
            booleanArrayOf(true, true, true, true, true, true, true),
            booleanArrayOf(true, false, true, false, true, false, true),
            booleanArrayOf(true, true, true, true, true, true, true),
            booleanArrayOf(false, false, true, false, true, false, false),
            booleanArrayOf(false, false, true, true, true, false, false)
        )
        val width = grid.size
        val height = grid[0].size
        val horizontalCorrect = floatArrayOf(
            2.0f,
            0.0f,
            5.0f,
            0.0f,
            3.0f,
            1.0f,
            4.0f,
            1.0f,
            0.0f,
            2.0f,
            2.0f,
            2.0f,
            1.0f,
            3.0f,
            2.0f,
            3.0f,
            3.0f,
            2.0f,
            4.0f,
            2.0f,
            3.0f,
            3.0f,
            4.0f,
            3.0f,
            5.0f,
            2.0f,
            7.0f,
            2.0f,
            5.0f,
            3.0f,
            6.0f,
            3.0f,
            0.0f,
            5.0f,
            2.0f,
            5.0f,
            1.0f,
            4.0f,
            2.0f,
            4.0f,
            3.0f,
            4.0f,
            4.0f,
            4.0f,
            3.0f,
            5.0f,
            4.0f,
            5.0f,
            5.0f,
            4.0f,
            6.0f,
            4.0f,
            5.0f,
            5.0f,
            7.0f,
            5.0f,
            2.0f,
            7.0f,
            5.0f,
            7.0f,
            3.0f,
            6.0f,
            4.0f,
            6.0f
        )
        val verticalCorrect = floatArrayOf(
            0.0f,
            2.0f,
            0.0f,
            5.0f,
            1.0f,
            3.0f,
            1.0f,
            4.0f,
            2.0f,
            0.0f,
            2.0f,
            2.0f,
            3.0f,
            1.0f,
            3.0f,
            2.0f,
            2.0f,
            3.0f,
            2.0f,
            4.0f,
            3.0f,
            3.0f,
            3.0f,
            4.0f,
            2.0f,
            5.0f,
            2.0f,
            7.0f,
            3.0f,
            5.0f,
            3.0f,
            6.0f,
            5.0f,
            0.0f,
            5.0f,
            2.0f,
            4.0f,
            1.0f,
            4.0f,
            2.0f,
            4.0f,
            3.0f,
            4.0f,
            4.0f,
            5.0f,
            3.0f,
            5.0f,
            4.0f,
            4.0f,
            5.0f,
            4.0f,
            6.0f,
            5.0f,
            5.0f,
            5.0f,
            7.0f,
            7.0f,
            2.0f,
            7.0f,
            5.0f,
            6.0f,
            3.0f,
            6.0f,
            4.0f
        )
        val horizontal = CollisionInformation.createHorizontalVertices(
            grid,
            width,
            height
        )
        val vertical = CollisionInformation.createVerticalVertices(grid, width, height)
        val horizontalTest = getFloatArrayFromCollisionPolygonVertexArrayList(horizontal)
        val verticalTest = getFloatArrayFromCollisionPolygonVertexArrayList(vertical)
        Assert.assertEquals(horizontalCorrect.size, horizontalTest.size)
        Assert.assertEquals(verticalCorrect.size, verticalTest.size)
        org.junit.Assert.assertArrayEquals(horizontalCorrect, horizontalTest, DELTA)
        org.junit.Assert.assertArrayEquals(verticalCorrect, verticalTest, DELTA)
    }

    companion object {
        private const val DELTA = Float.MIN_VALUE
    }
}
