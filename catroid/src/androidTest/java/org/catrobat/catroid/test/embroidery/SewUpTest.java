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

package org.catrobat.catroid.test.embroidery;

import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.SewUpAction;
import org.catrobat.catroid.embroidery.DSTPatternManager;
import org.catrobat.catroid.embroidery.EmbroideryPatternManager;
import org.catrobat.catroid.embroidery.StitchPoint;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
@RunWith(AndroidJUnit4.class)
public class SewUpTest {
	private Sprite sprite;
	private Look spriteLook;
	private EmbroideryPatternManager embroideryPatternManager;

	@Before
	public void setUp() {
		sprite = new Sprite("testSprite");
		spriteLook = Mockito.mock(Look.class);
		sprite.look = spriteLook;
		embroideryPatternManager = new DSTPatternManager();
		StageActivity.stageListener = new StageListener();
		StageActivity.stageListener.embroideryPatternManager = embroideryPatternManager;
	}

	@After
	public void tearDown() {
		StageActivity.stageListener = null;
	}

	@Test
	public void testVerticalSewUp() {
		sprite.getActionFactory().createSewUpAction(sprite).act(1f);

		List<StitchPoint> stitches =
				StageActivity.stageListener.embroideryPatternManager.getEmbroideryPatternList();

		ArrayList<Float> expectedStitchesX = new ArrayList<>();
		ArrayList<Float> expectedStitchesY = new ArrayList<>();
		float x = sprite.look.getXInUserInterfaceDimensionUnit();
		float y = sprite.look.getYInUserInterfaceDimensionUnit();

		expectedStitchesX.add(x);
		expectedStitchesY.add(y);
		expectedStitchesX.add(x);
		expectedStitchesY.add(y + SewUpAction.STEPS);
		expectedStitchesX.add(x);
		expectedStitchesY.add(y);
		expectedStitchesX.add(x);
		expectedStitchesY.add(y - SewUpAction.STEPS);
		expectedStitchesX.add(x);
		expectedStitchesY.add(y);

		for (int i = 0; i < expectedStitchesX.size(); i++) {
			assertEquals(stitches.get(i).getX(), expectedStitchesX.get(i), 0.01);
			assertEquals(stitches.get(i).getY(), expectedStitchesY.get(i), 0.01);
		}
	}

	@Test
	public void testAngledSewUp() {
		sprite.look.setMotionDirectionInUserInterfaceDimensionUnit(137);
		sprite.getActionFactory().createSewUpAction(sprite).act(1f);

		List<StitchPoint> stitches =
				StageActivity.stageListener.embroideryPatternManager.getEmbroideryPatternList();

		ArrayList<Float> expectedStitchesX = new ArrayList<>();
		ArrayList<Float> expectedStitchesY = new ArrayList<>();
		float x = sprite.look.getXInUserInterfaceDimensionUnit();
		float y = sprite.look.getYInUserInterfaceDimensionUnit();
		double radians = Math.toRadians(sprite.look.getMotionDirectionInUserInterfaceDimensionUnit());

		expectedStitchesX.add(x);
		expectedStitchesY.add(y);
		expectedStitchesX.add(x + SewUpAction.STEPS * (float) Math.sin(radians));
		expectedStitchesY.add(y + SewUpAction.STEPS * (float) Math.cos(radians));
		expectedStitchesX.add(x);
		expectedStitchesY.add(y);
		expectedStitchesX.add(x - SewUpAction.STEPS * (float) Math.sin(radians));
		expectedStitchesY.add(y - SewUpAction.STEPS * (float) Math.cos(radians));
		expectedStitchesX.add(x);
		expectedStitchesY.add(y);

		for (int i = 0; i < expectedStitchesX.size(); i++) {
			assertEquals(stitches.get(i).getX(), expectedStitchesX.get(i), 0.01);
			assertEquals(stitches.get(i).getY(), expectedStitchesY.get(i), 0.01);
		}
	}
}
