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
import junit.framework.Assert
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.SetNextLookAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.koin.projectManagerModule
import org.catrobat.catroid.koin.stop
import org.catrobat.catroid.test.MockUtil
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.module.Module
import org.koin.java.KoinJavaComponent.inject
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.io.File
import java.util.Collections

@RunWith(PowerMockRunner::class)
@PrepareForTest(StorageOperations::class, XstreamSerializer::class, GdxNativesLoader::class)
class CopyLookActionTest {
    private lateinit var projectMock: Project
    private lateinit var testSequence: SequenceAction
    private val xstreamSerializerMock = PowerMockito.mock(XstreamSerializer::class.java)
    private val lookDataFile = mock(File::class.java)
    private val lookDataFileCopy = mock(File::class.java)
    private val lookData = LookData("firstLook", lookDataFile)
    private val formulaName = Formula("CopiedLook")

    private val projectManager: ProjectManager by inject(ProjectManager::class.java)
    private val dependencyModules: List<Module> = Collections.singletonList(projectManagerModule)

    @Before
    fun setUp() {
        val context = MockUtil.mockContextForProject(dependencyModules)
        projectMock = Project(context, "testProject").also { project ->
            projectManager.currentProject = project
        }
        testSequence = SequenceAction()
        PowerMockito.mockStatic(StorageOperations::class.java)
        PowerMockito.mockStatic(XstreamSerializer::class.java)
        PowerMockito.mockStatic(GdxNativesLoader::class.java)
        Mockito.`when`(StorageOperations.duplicateFile(lookDataFile)).thenReturn(lookDataFileCopy)
        Mockito.`when`(XstreamSerializer.getInstance()).thenReturn(xstreamSerializerMock)
        Mockito.`when`(xstreamSerializerMock.saveProject(projectMock)).thenReturn(true)
        Mockito.`when`(lookDataFile.exists()).thenReturn(true)
    }

    @After
    fun tearDown() {
        stop(dependencyModules)
    }

    @Test
    fun testWithEmptyLookList() {
        with(Sprite()) {
            val setNewLookAction = actionFactory.createSetNextLookAction(this, testSequence)
            actionFactory.createCopyLookAction(this, testSequence, formulaName, setNewLookAction as
                SetNextLookAction).act(1f)
            Assert.assertNull(look.lookData)
        }
    }

    @Test
    fun testWithSingleLookList() {
        with(Sprite()) {
            lookList.add(lookData)
            look.lookData = lookData
            val setNewLookAction = actionFactory.createSetNextLookAction(this, testSequence)
            actionFactory.createCopyLookAction(this, testSequence, formulaName, setNewLookAction as
                SetNextLookAction).act(1f)
            org.junit.Assert.assertEquals(lookDataFileCopy, this.lookList[1].file)
        }
    }
}
