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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.utils.GdxNativesLoader
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.koin.projectManagerModule
import org.catrobat.catroid.koin.stop
import org.catrobat.catroid.test.MockUtil
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.module.Module
import org.koin.java.KoinJavaComponent.inject
import org.powermock.api.mockito.PowerMockito.mock
import org.powermock.api.mockito.PowerMockito.mockStatic
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.io.File
import java.util.Collections

@RunWith(PowerMockRunner::class)
@PrepareForTest(GdxNativesLoader::class)
class SetPreviousLookActionTest {
    private lateinit var sprite: Sprite
    private lateinit var sequence: SequenceAction
    private lateinit var lookData1: LookData
    private lateinit var lookData2: LookData
    private lateinit var lookData3: LookData

    private val projectManager: ProjectManager by inject(ProjectManager::class.java)
    private val dependencyModules: List<Module> = Collections.singletonList(projectManagerModule)

    @Before
    fun setUp() {
        mockStatic(GdxNativesLoader::class.java)

        lookData1 = LookData("firstLook", mock(File::class.java))
        lookData2 = LookData("secondLook", mock(File::class.java))
        lookData3 = LookData("thirdLook", mock(File::class.java))
        sprite = Sprite("testSprite").apply {
            lookList.add(lookData1)
            lookList.add(lookData2)
            lookList.add(lookData3)
        }
        sequence = SequenceAction()
        val context = MockUtil.mockContextForProject(dependencyModules)
        Project(context, "testProject").also { project ->
            projectManager.currentProject = project
        }
    }

    @After
    fun tearDown() {
        stop(dependencyModules)
    }

    @Test
    fun testPreviousLook() {
        sprite.look.lookData = lookData2
        sprite.actionFactory.createSetPreviousLookAction(sprite, sequence).act(1f)
        assertEquals(lookData1, sprite.look.lookData)
    }

    @Test
    fun testFirstLook() {
        sprite.look.lookData = lookData1
        sprite.actionFactory.createSetPreviousLookAction(sprite, sequence).act(1f)
        assertEquals(lookData3, sprite.look.lookData)
    }

    @Test
    fun testEmptyLookList() {
        with(Sprite("noLookSprite")) {
            actionFactory.createSetPreviousLookAction(sprite, sequence).act(1f)
            org.junit.Assert.assertNull(look.lookData)
        }
    }

    @Test
    fun testSingleLook() {
        with(Sprite("singleLookSprite")) {
            lookList.add(lookData1)
            look.lookData = lookData1
            actionFactory.createSetPreviousLookAction(sprite, sequence).act(1f)
            assertEquals(lookData1, look.lookData)
        }
    }
}
