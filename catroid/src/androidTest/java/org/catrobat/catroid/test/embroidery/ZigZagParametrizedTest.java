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
import org.catrobat.catroid.embroidery.EmbroideryPatternManager;
import org.catrobat.catroid.embroidery.ZigZagRunningStitch;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageListener;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static java.util.Arrays.asList;

@RunWith(Parameterized.class)
public class ZigZagParametrizedTest {
	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return asList(new Object[][] {
				{"Test Points", 10.F, 5.F, 90.F, asList(10.0F, 20.F, 30.F), asList(2.5F, -2.5F,
						2.5F)},
				{"Test more Points", 5.F, 2.F, 90.F, asList(10.F, 15.F, 20.F, 25.F, 30.F),
						asList(1.F, -1.F, 1.F, -1.F, 1.F)},
				{"Test different length", 20.F, 5.F, 90.F, asList(10.0F, 30.F), asList(2.5F,
						-2.5F)},
				{"Test different width", 10.F, 10.F, 90.F, asList(10.0F, 20.F, 30.F), asList(5.0F,
						-5.0F, 5.0F)},
				{"Test degrees", 10.F, 10.F, 270.F, asList(10.0F, 20.F, 30.F), asList(-5.0F,
						5.0F, -5.0F)},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public Float length;

	@Parameterized.Parameter(2)
	public Float width;

	@Parameterized.Parameter(3)
	public Float degrees;

	@Parameterized.Parameter(4)
	public List<Float> expectedstitchPointsX;

	@Parameterized.Parameter(5)
	public List<Float> expectedStitchPointsY;

	private ZigZagRunningStitch zigZagRunningStitch;
	private EmbroideryPatternManager embroideryPatternManager;
	private Sprite sprite;
	private Look spriteLook;
	private ArrayList<Float> actualStitchPointsX = new ArrayList<>();
	private ArrayList<Float> actualStitchPointsY = new ArrayList<>();

	@Before
	public void setUp() throws Exception {
		sprite = Mockito.mock(Sprite.class);
		spriteLook = Mockito.mock(Look.class);
		when(spriteLook.getMotionDirectionInUserInterfaceDimensionUnit()).thenReturn(degrees);
		sprite.look = spriteLook;
		embroideryPatternManager = mock(EmbroideryPatternManager.class);
		StageActivity.stageListener = Mockito.mock(StageListener.class);
		StageActivity.stageListener.embroideryPatternManager = embroideryPatternManager;
		zigZagRunningStitch = new ZigZagRunningStitch(sprite, length, width);
		zigZagRunningStitch.setListener((x, y) -> {
			actualStitchPointsX.add(x);
			actualStitchPointsY.add(y);
		});
		zigZagRunningStitch.setStartCoordinates(10, 0);
		zigZagRunningStitch.update(30, 0);
	}

	@After
	public void tearDown() {
		StageActivity.stageListener = null;
	}

	@Test
	public void testXCoordinates() {
		Assert.assertEquals(expectedstitchPointsX, actualStitchPointsX);
	}

	@Test
	public void testYCoordinates() {
		Assert.assertEquals(expectedStitchPointsY, actualStitchPointsY);
	}
}
