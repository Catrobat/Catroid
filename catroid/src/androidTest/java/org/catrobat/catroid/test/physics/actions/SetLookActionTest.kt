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
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.Assert
import org.catrobat.catroid.common.Constants
import org.junit.runner.RunWith
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.test.physics.PhysicsTestRule
import org.junit.Before
import kotlin.Throws
import org.catrobat.catroid.test.physics.PhysicsTestUtils
import org.catrobat.catroid.io.ResourceImporter
import org.catrobat.catroid.test.R
import org.catrobat.catroid.test.utils.TestUtils
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class SetLookActionTest {
    private var multipleConvexPolygonsFileName: String? = null
    private var multipleConvexPolygonsFile: File? = null
    private var lookData: LookData? = null

    @get:Rule
    var rule = PhysicsTestRule()
    private var sprite: Sprite? = null
    private var project: Project? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        sprite = rule.sprite
        project = rule.project
        multipleConvexPolygonsFileName =
            PhysicsTestUtils.getInternalImageFilenameFromFilename("multible_convex_polygons.png")
        multipleConvexPolygonsFile = ResourceImporter.createImageFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            R.raw.multible_convex_polygons,
            File(project!!.getDefaultScene().directory, Constants.IMAGE_DIRECTORY_NAME),
            multipleConvexPolygonsFileName, 1.0
        )
        lookData = PhysicsTestUtils.generateLookData(multipleConvexPolygonsFile)
        Assert.assertNotNull(sprite!!.look.lookData)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        multipleConvexPolygonsFileName = null
        multipleConvexPolygonsFile = null
        TestUtils.deleteProjects()
    }

    @Test
    fun testLookChanged() {
        val expectedLookData = lookData
        val previousLookData = sprite!!.look.lookData
        changeLook()
        MatcherAssert.assertThat(sprite!!.look.lookData, Matchers.`is`(IsNot.not(previousLookData)))
        Assert.assertEquals(sprite!!.look.lookData, expectedLookData)
    }

    private fun changeLook() {
        sprite!!.lookList.add(lookData)
        val action = sprite!!.actionFactory.createSetLookAction(sprite, lookData)
        action.act(1.0f)
        Assert.assertNotNull(sprite!!.look)
    }
}