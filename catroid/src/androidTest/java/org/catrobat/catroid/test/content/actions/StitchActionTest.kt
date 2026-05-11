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

import android.graphics.PointF
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.badlogic.gdx.graphics.Color
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.embroidery.DSTPatternManager
import org.catrobat.catroid.embroidery.DSTStitchCommand
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageListener
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class StitchActionTest {
	private lateinit var testSprite1: Sprite
	private lateinit var testSprite2: Sprite
	private lateinit var spriteCoords1: PointF
	private lateinit var spriteCoords2: PointF
	private lateinit var embroideryPatternManagerMock: DSTPatternManager

	@Before
	fun setUp() {
		val xCoord = 50.0f
		val yCoord = 160.0f

		testSprite1 = Sprite("testSprite1").apply {
			look.setX(xCoord)
			look.setY(yCoord)
		}
		spriteCoords1 = PointF(xCoord, yCoord)

		testSprite2 = Sprite("testSprite2")
		spriteCoords2 = PointF(0f, 0f)

		val project = Project(ApplicationProvider.getApplicationContext(), "testProject")
		ProjectManager.getInstance().currentProject = project
		
		embroideryPatternManagerMock = mock(DSTPatternManager::class.java)
		val stageListenerMock = mock(StageListener::class.java)
		stageListenerMock.embroideryPatternManager = embroideryPatternManagerMock
		StageActivity.stageListener = stageListenerMock
	}

	@After
	fun tearDown() {
		StageActivity.stageListener = null
	}

	@Test
	fun testAddSingleStitchPoint() {
		ActionFactory.createStitchAction(testSprite1).act(1f)
		verify(embroideryPatternManagerMock, times(1)).addStitchCommand(
			eq(
				DSTStitchCommand(
					spriteCoords1.x,
					spriteCoords1.y,
					testSprite1.look.zIndex,
					testSprite1,
					Color.BLACK
				)
			)
		)
	}

	@Test
	fun testAddPointsTwoSprites() {
		ActionFactory.createStitchAction(testSprite1).act(1f)
		ActionFactory.createStitchAction(testSprite2).act(1f)

		verify(embroideryPatternManagerMock, times(1)).addStitchCommand(
			DSTStitchCommand(
				spriteCoords1.x,
				spriteCoords1.y,
				testSprite1.look.zIndex,
				testSprite1,
				Color.BLACK
			)
		)
		verify(embroideryPatternManagerMock, times(1)).addStitchCommand(
			DSTStitchCommand(
				spriteCoords2.x,
				spriteCoords2.y,
				testSprite2.look.zIndex,
				testSprite2,
				Color.BLACK
			)
		)
	}
}
