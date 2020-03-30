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

package org.catrobat.catroid.stage;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import org.catrobat.catroid.embroidery.EmbroideryPatternManager;
import org.catrobat.catroid.embroidery.StitchPoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.verification.VerificationMode;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static java.util.Arrays.asList;

@RunWith(Parameterized.class)
public class EmbroideryActorLineTest {
	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return asList(new Object[][] {
				{"Test Connecting Points", asList(getConnectingPointMock(), getConnectingPointMock()), true},
				{"Test Changing Points", asList(getChangePointMock(), getChangePointMock()), false},
				{"Test Jumping Points", asList(getJumpPointMock(), getJumpPointMock()), true},
				{"Test Jumping Point after Connecting Point", asList(getConnectingPointMock(), getJumpPointMock()), true},
				{"Test Changing Point after Connecting Point", asList(getConnectingPointMock(), getChangePointMock()), false}
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public List<StitchPoint> stitchPoints;

	@Parameterized.Parameter(2)
	public Boolean expectedLineDrawn;

	private EmbroideryActor embroideryActorSpy;

	@Before
	public void setUp() throws Exception {
		EmbroideryPatternManager embroideryPatternManager = mock(EmbroideryPatternManager.class);
		when(embroideryPatternManager.getEmbroideryPatternList()).thenReturn(stitchPoints);
		ShapeRenderer renderer = mock(ShapeRenderer.class);
		embroideryActorSpy = spy(new EmbroideryActor(1.0f, embroideryPatternManager, renderer));
	}

	@Test
	public void testDraw() {
		embroideryActorSpy.draw(mock(Batch.class), 1.0F);

		VerificationMode mode = expectedLineDrawn ? times(1) : never();
		verify(embroideryActorSpy, mode).drawLine(stitchPoints.get(0), stitchPoints.get(1));
	}

	private static StitchPoint getJumpPointMock() {
		StitchPoint mock = mock(StitchPoint.class);
		when(mock.isJumpPoint()).thenReturn(true);
		return mock;
	}

	private static StitchPoint getChangePointMock() {
		StitchPoint mock = mock(StitchPoint.class);
		when(mock.isColorChangePoint()).thenReturn(true);
		return mock;
	}

	private static StitchPoint getConnectingPointMock() {
		StitchPoint mock = mock(StitchPoint.class);
		when(mock.isConnectingPoint()).thenReturn(true);
		return mock;
	}
}
