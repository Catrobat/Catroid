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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.utils.GdxNativesLoader
import junit.framework.Assert
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.EditLookAction
import org.catrobat.catroid.content.actions.SetNextLookAction
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.test.MockUtil
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.io.File

@RunWith(PowerMockRunner::class)
@PrepareForTest(StorageOperations::class, XstreamSerializer::class, GdxNativesLoader::class)
class EditLookActionTest {
    private lateinit var projectMock: Project
    private lateinit var testSequence: SequenceAction
    private val xstreamSerializerMock = PowerMockito.mock(XstreamSerializer::class.java)
    private val lookDataFile = mock(File::class.java)
    private val lookDataFileEdited = mock(File::class.java)
    private val lookData = LookData("firstLook", lookDataFile)

    @Before
    fun setUp() {
        projectMock = Project(MockUtil.mockContextForProject(), "testProject").also { project ->
            ProjectManager.getInstance().currentProject = project
        }
        testSequence = SequenceAction()
        PowerMockito.mockStatic(XstreamSerializer::class.java)
        PowerMockito.mockStatic(GdxNativesLoader::class.java)
        PowerMockito.mockStatic(StorageOperations::class.java)
        Mockito.`when`(StorageOperations.duplicateFile(lookDataFile)).thenReturn(lookDataFileEdited)
        Mockito.`when`(XstreamSerializer.getInstance()).thenReturn(xstreamSerializerMock)
        Mockito.`when`(xstreamSerializerMock.saveProject(projectMock)).thenReturn(true)
    }

    @Test
    fun testWithEmptyLookList() {
        with(Sprite()) {
            val setNewLookAction = actionFactory.createSetNextLookAction(this, testSequence)
            val editLookAction = actionFactory.createEditLookAction(
                this, testSequence, setNewLookAction as SetNextLookAction) as EditLookAction
            editLookAction.setLookData()
            setNewLookAction.act(1f)
            Assert.assertEquals(0, this.lookList.size)
        }
    }

    @Test
    fun testWithSingleLookList() {
        with(Sprite()) {
            this.lookList.add(lookData)
            this.look.lookData = lookData
            val setNewLookAction = actionFactory.createSetNextLookAction(this, testSequence)
            val editLookAction = actionFactory.createEditLookAction(
                this, testSequence, setNewLookAction as SetNextLookAction) as EditLookAction
            editLookAction.setLookData()
            Assert.assertEquals(lookDataFileEdited, this.lookList[0].file)
        }
    }
}
