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

package org.catrobat.catroid.stage;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import org.catrobat.catroid.embroidery.EmbroideryPatternManager;
import org.catrobat.catroid.embroidery.StitchPoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
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
public class EmbroideryActorCircleTest {
	@Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return asList(new Object[][] {
				{"Test ConnectingPoints", asList(getConnectingPointMock(),
						getConnectingPointMock(), getConnectingPointMock()), true, true},
				{"Test JumpPoints", asList(getConnectingPointMock(),
						getJumpPointMock(), getJumpPointMock()), false, false},
				{"Test ChangePoints", asList(getConnectingPointMock(),
						getColorChangePointMock(), getColorChangePointMock()), false, false},
				{"Test JumpPoint after ChangePoint", asList(getConnectingPointMock(),
						getColorChangePointMock(), getJumpPointMock()), false, false},
				{"Test ConnectingPoint after JumpPoint", asList(getConnectingPointMock(),
						getJumpPointMock(), getConnectingPointMock()), false, true},
				{"Test ChangePoint after JumpPoint", asList(getConnectingPointMock(),
						getJumpPointMock(), getColorChangePointMock()), false, false},
				{"Test JumpPoint after ConnectingPoint", asList(getConnectingPointMock(),
						getConnectingPointMock(), getJumpPointMock()), true, false}
		});
	}

	@Parameter
	public String name;

	@Parameter(1)
	public List<StitchPoint> stitchPoints;

	@Parameter(2)
	public boolean firstPointDrawn;

	@Parameter(3)
	public boolean secondPointDrawn;

	private EmbroideryActor embroideryActorSpy;

	@Before
	public void setUp() throws Exception {
		EmbroideryPatternManager embroideryPatternManager = mock(EmbroideryPatternManager.class);
		when(embroideryPatternManager.getEmbroideryPatternList()).thenReturn(stitchPoints);
		ShapeRenderer renderer = mock(ShapeRenderer.class);
		embroideryActorSpy = spy(new EmbroideryActor(1.0f, embroideryPatternManager, renderer));
	}

	@Test
	public void testFirstCircleDrawn() {
		embroideryActorSpy.draw(mock(Batch.class), 1.0F);
		verify(embroideryActorSpy, times(1)).drawCircle(stitchPoints.get(0));
	}

	@Test
	public void testSecondCircleDrawn() {
		embroideryActorSpy.draw(mock(Batch.class), 1.0F);
		VerificationMode mode = firstPointDrawn ? times(1) : never();
		verify(embroideryActorSpy, mode).drawCircle(stitchPoints.get(1));
	}

	@Test
	public void testThirdCircleDrawn() {
		embroideryActorSpy.draw(mock(Batch.class), 1.0F);
		VerificationMode mode = secondPointDrawn ? times(1) : never();
		verify(embroideryActorSpy, mode).drawCircle(stitchPoints.get(2));
	}

	private static StitchPoint getConnectingPointMock() {
		StitchPoint mock = mock(StitchPoint.class);
		when(mock.isConnectingPoint()).thenReturn(true);
		return mock;
	}

	private static StitchPoint getJumpPointMock() {
		StitchPoint mock = mock(StitchPoint.class);
		when(mock.isJumpPoint()).thenReturn(true);
		return mock;
	}

	private static StitchPoint getColorChangePointMock() {
		StitchPoint mock = mock(StitchPoint.class);
		when(mock.isColorChangePoint()).thenReturn(true);
		return mock;
	}
}
