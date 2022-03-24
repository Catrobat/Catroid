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
import org.catrobat.catroid.content.actions.EditLookAction
import org.catrobat.catroid.content.actions.SetNextLookAction
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.stage.StageActivity
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
import java.io.IOException
import java.lang.ref.WeakReference

@RunWith(PowerMockRunner::class)
@PrepareForTest(StorageOperations::class, XstreamSerializer::class, GdxNativesLoader::class)
class EditLookActionTest {
    private lateinit var projectMock: Project
    private lateinit var testSequence: SequenceAction
    private lateinit var stageActivity: StageActivity
    private val xstreamSerializerMock = PowerMockito.mock(XstreamSerializer::class.java)
    private val lookDataFile = mock(File::class.java)
    private val lookDataFileTemp = mock(File::class.java)
    private val lookDataFileEdited = mock(File::class.java)
    private val lookData = LookData("firstLook", lookDataFile)

    private fun doEditLookAction(sprite: Sprite) {
        if (sprite.lookList.isEmpty()) {
            sprite.lookList.add(lookData)
            sprite.look.lookData = lookData
        }
        val setNewLookAction = sprite.actionFactory.createSetNextLookAction(sprite, testSequence)
        val editLookAction = sprite.actionFactory.createEditLookAction(
            sprite, testSequence, setNewLookAction as SetNextLookAction) as EditLookAction
        editLookAction.restart()
        editLookAction.onIntentResult(-1, null)
        setNewLookAction.act(1f)
    }

    @Before
    fun setUp() {
        projectMock = Project(MockUtil.mockContextForProject(), "testProject").also { project ->
            ProjectManager.getInstance().currentProject = project
        }
        testSequence = SequenceAction()
        PowerMockito.mockStatic(XstreamSerializer::class.java)
        PowerMockito.mockStatic(GdxNativesLoader::class.java)
        PowerMockito.mockStatic(StorageOperations::class.java)
        Mockito.`when`(StorageOperations.duplicateFile(lookDataFile)).thenReturn(lookDataFileTemp)
        Mockito.`when`(StorageOperations.duplicateFile(lookDataFileTemp))
            .thenReturn(lookDataFileEdited)
        Mockito.`when`(XstreamSerializer.getInstance()).thenReturn(xstreamSerializerMock)
        Mockito.`when`(xstreamSerializerMock.saveProject(projectMock)).thenReturn(true)

        stageActivity = StageActivity()
        StageActivity.activeStageActivity = WeakReference<StageActivity>(stageActivity)
    }

    @Test
    fun testWithEmptyLookList() {
        with(Sprite()) {
            val setNewLookAction = actionFactory.createSetNextLookAction(this, testSequence)
            val editLookAction = actionFactory.createEditLookAction(
                this, testSequence, setNewLookAction as SetNextLookAction) as EditLookAction
            editLookAction.protectOriginalLookData()
            editLookAction.setLookData()
            setNewLookAction.act(1f)
            Assert.assertEquals(0, this.lookList.size)
        }
    }

    @Test
    fun testSizeOfLookListWhenSpriteIsNoClone() {
        with(Sprite()) {
            doEditLookAction(this)
            Assert.assertEquals(1, this.lookList.size)
        }
    }

    @Test
    fun testSizeOfLookListWhenSpriteIsClone() {
        with(Sprite()) {
            this.isClone = true
            doEditLookAction(this)
            Assert.assertEquals(1, this.lookList.size)
        }
    }

    @Test
    fun testFileFlowWhenSpriteIsNoClone() {
        with(Sprite()) {
            doEditLookAction(this)
            Assert.assertEquals(lookDataFileEdited, this.lookList[0].file)
        }
    }

    @Test
    fun testFileFlowWhenSpriteIsClone() {
        with(Sprite()) {
            this.isClone = true
            doEditLookAction(this)
            Assert.assertEquals(lookDataFileEdited, this.lookList[0].file)
        }
    }

    @Test
    fun testFileFlowWhenSpriteIsNoCloneInALoop() {
        with(Sprite()) {
            repeat(10) {
                if (this.lookList.isNotEmpty()) {
                    this.lookList[0].file = lookDataFile
                }
                doEditLookAction(this)
            }
            Assert.assertEquals(lookDataFileEdited, this.lookList[0].file)
            Assert.assertEquals(1, this.lookList.size)
        }
    }

    @Test
    fun testFileFlowWhenSpriteIsCloneInALoop() {
        with(Sprite()) {
            this.isClone = true
            repeat(10) {
                if (this.lookList.isNotEmpty()) {
                    this.lookList[0].file = lookDataFile
                }
                doEditLookAction(this)
            }
            Assert.assertEquals(lookDataFileEdited, this.lookList[0].file)
            Assert.assertEquals(1, this.lookList.size)
        }
    }

    @Test
    fun testIfLookDataIsNoCopyWhenSpriteIsNoClone() {
        with(Sprite()) {
            doEditLookAction(this)
            Assert.assertFalse(this.look.lookData.isCopy)
        }
    }

    @Test
    fun testIfLookDataIsCopyWhenSpriteIsClone() {
        with(Sprite()) {
            this.isClone = true
            doEditLookAction(this)
            Assert.assertTrue(this.look.lookData.isCopy)
        }
    }

    @Test
    fun testIfFileStaysUnchangedWhenExceptionIsThrown() {
        val lookDataFileMock = mock(File::class.java)
        Mockito.`when`(StorageOperations.duplicateFile(lookDataFileMock))
            .thenThrow(IOException::class.java)
        lookData.file = lookDataFileMock

        with(Sprite()) {
            this.lookList.add(lookData)
            this.look.lookData = lookData
            this.isClone = true
            val setNewLookAction = this.actionFactory.createSetNextLookAction(this, testSequence)
            val editLookAction = this.actionFactory.createEditLookAction(
                this, testSequence, setNewLookAction as SetNextLookAction) as EditLookAction
            editLookAction.protectOriginalLookData()
            editLookAction.setLookData()
            Assert.assertEquals(lookDataFileMock, this.look.lookData.file)
        }
    }
}
