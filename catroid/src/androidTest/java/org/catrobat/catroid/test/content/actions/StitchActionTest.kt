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

import org.junit.runner.RunWith
import android.graphics.PointF
import org.junit.Before
import kotlin.Throws
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.badlogic.gdx.graphics.Color
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageListener
import org.catrobat.catroid.embroidery.DSTPatternManager
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.embroidery.EmbroideryPatternManager
import org.catrobat.catroid.embroidery.DSTStitchCommand
import org.junit.After
import org.junit.Test
import org.mockito.Mockito
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class StitchActionTest {
    private var testSprite1: Sprite? = null
    private var testSprite2: Sprite? = null
    private var spriteCoords1: PointF? = null
    private var spriteCoords2: PointF? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        val project: Project
        val xCoord = 50.0f
        val yCoord = 160.0f
        testSprite1 = Sprite("testSprite1")
        testSprite1!!.look.x = xCoord
        testSprite1!!.look.y = yCoord
        spriteCoords1 = PointF(xCoord, yCoord)
        testSprite2 = Sprite("testSprite2")
        spriteCoords2 = PointF(0F, 0F)
        project = Project(ApplicationProvider.getApplicationContext(), "testProject")
        ProjectManager.getInstance().currentProject = project
        StageActivity.stageListener = Mockito.mock(StageListener::class.java)
        StageActivity.stageListener.embroideryPatternManager = Mockito.mock(
            DSTPatternManager::class.java
        )
    }

    @After
    fun tearDown() {
        StageActivity.stageListener = null
    }

    @Test
    fun testAddSingleStitchPoint() {
        ActionFactory.createStitchAction(testSprite1).act(1f)
        Mockito.verify(StageActivity.stageListener.embroideryPatternManager, Mockito.times(1))
            .addStitchCommand(
                Mockito.eq(
                    DSTStitchCommand(
                        spriteCoords1!!.x, spriteCoords1!!.y,
                        testSprite1!!.look.zIndex, testSprite1, Color.BLACK
                    )
                )
            )
    }

    @Test
    fun testAddPointsTwoSprites() {
        ActionFactory.createStitchAction(testSprite1).act(1f)
        ActionFactory.createStitchAction(testSprite2).act(1f)
        Mockito.verify(StageActivity.stageListener.embroideryPatternManager, Mockito.times(1))
            .addStitchCommand(
                DSTStitchCommand(
                    spriteCoords1!!.x,
                    spriteCoords1!!.y,
                    testSprite1!!.look.zIndex,
                    testSprite1,
                    Color.BLACK
                )
            )
        Mockito.verify(StageActivity.stageListener.embroideryPatternManager, Mockito.times(1))
            .addStitchCommand(
                DSTStitchCommand(
                    spriteCoords2!!.x,
                    spriteCoords2!!.y,
                    testSprite2!!.look.zIndex,
                    testSprite2,
                    Color.BLACK
                )
            )
    }
}