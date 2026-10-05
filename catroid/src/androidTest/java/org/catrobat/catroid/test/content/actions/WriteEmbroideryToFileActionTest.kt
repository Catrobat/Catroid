/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.WriteEmbroideryToFileAction
import org.catrobat.catroid.embroidery.EmbroideryPatternManager
import org.catrobat.catroid.embroidery.EmbroideryStream
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageListener
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.io.File

@RunWith(AndroidJUnit4::class)
class WriteEmbroideryToFileActionTest {

    private lateinit var action: WriteEmbroideryToFileAction
    private lateinit var testFile: File
    private lateinit var mockEmbroideryManager: EmbroideryPatternManager

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val mockProject = Mockito.mock(Project::class.java)
        val mockSprite = Mockito.mock(Sprite::class.java)
        val mockStageListener = Mockito.mock(StageListener::class.java)
        mockEmbroideryManager = Mockito.mock(EmbroideryPatternManager::class.java)
        val mockStream = Mockito.mock(EmbroideryStream::class.java)
        val mockHeader = Mockito.mock(org.catrobat.catroid.embroidery.EmbroideryHeader::class.java)
        Mockito.doAnswer { invocation ->
            val stream = invocation.arguments[0] as java.io.FileOutputStream
            stream.write(ByteArray(512))
            null
        }.`when`(mockHeader).appendToStream(Mockito.any())
        val emptyPointList = ArrayList<org.catrobat.catroid.embroidery.StitchPoint>()

        StageActivity.stageListener = mockStageListener
        mockStageListener.embroideryPatternManager = mockEmbroideryManager

        `when`(mockEmbroideryManager.embroideryStream).thenReturn(mockStream)
        `when`(mockEmbroideryManager.validPatternExists()).thenReturn(true)
        `when`(mockStream.getHeader()).thenReturn(mockHeader)
        `when`(mockStream.getPointList()).thenReturn(emptyPointList)

        action = WriteEmbroideryToFileAction()
        action.scope = Scope(mockProject, mockSprite, null)
        testFile = File(context.cacheDir, "test_embroidery.dst")
    }

    @Test
    fun testHandleFileWorkGeneratesDstContent() {
        val result = action.handleFileWork(testFile)

        assertTrue("handleFileWork should return true on success", result)
        assertTrue("DST file should have been created", testFile.exists())
        assertTrue("DST file is too small (header is missing)", testFile.length() >= 512)

        testFile.delete()
    }

    @Test
    fun testCheckIfDataIsReadyReturnsTrueWhenPatternExists() {
        `when`(mockEmbroideryManager.validPatternExists()).thenReturn(true)

        assertTrue("Action should be ready when a valid embroidery pattern exists",
                   action.checkIfDataIsReady())
    }

    @Test
    fun testCheckIfDataIsReadyReturnsFalseWhenNoPatternExists() {
        `when`(mockEmbroideryManager.validPatternExists()).thenReturn(false)

        assertFalse("Action should NOT be ready when no embroidery pattern exists",
                    action.checkIfDataIsReady())
    }
}