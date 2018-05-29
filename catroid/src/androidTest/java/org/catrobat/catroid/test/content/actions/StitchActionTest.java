/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.test.content.actions;

import android.graphics.PointF;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.embroidery.EmbroideryList;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class StitchActionTest {
	private Sprite testSprite1;
	private Sprite testSprite2;
	private PointF spriteCoords1;
	private PointF spriteCoords2;

	@Before
	public void setUp() throws Exception {
		Project project;
		float xCoord = 50.0f;
		float yCoord = 160.0f;

		testSprite1 = new Sprite("testSprite1");
		testSprite1.look.setX(xCoord);
		testSprite1.look.setY(yCoord);
		spriteCoords1 = new PointF(xCoord, yCoord);

		testSprite2 = new Sprite("testSprite2");
		spriteCoords2 = new PointF(0, 0);

		project = new Project(InstrumentationRegistry.getTargetContext(), "testProject");
		ProjectManager.getInstance().setProject(project);
		StageActivity.stageListener = Mockito.mock(StageListener.class);
		StageActivity.stageListener.embroideryList = Mockito.mock(EmbroideryList.class);
	}

	@Test
	public void testAddSingleStitchPoint() {
		final int expectedInvocations = 1;
		testSprite1.getActionFactory().createStitchAction(testSprite1).act(1f);
		Mockito.verify(StageActivity.stageListener.embroideryList, Mockito.times(expectedInvocations)).add(spriteCoords1);
	}

	@Test
	public void testAddPointsTwoSprites() {
		final int expectedInvocations = 1;
		testSprite1.getActionFactory().createStitchAction(testSprite1).act(1f);
		testSprite2.getActionFactory().createStitchAction(testSprite2).act(1f);
		Mockito.verify(StageActivity.stageListener.embroideryList, Mockito.times(expectedInvocations)).add(spriteCoords1);
		Mockito.verify(StageActivity.stageListener.embroideryList, Mockito.times(expectedInvocations)).add(spriteCoords2);
	}

	@Test
	public void testSamePointConsecutively() {
		final int expectedListSize = 1;
		EmbroideryList stitchPoints = new EmbroideryList();
		stitchPoints.add(spriteCoords1);
		stitchPoints.add(spriteCoords1);
		stitchPoints.add(spriteCoords1);

		assertEquals(stitchPoints.size(), expectedListSize);
	}
}
