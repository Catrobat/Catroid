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
				{"Test ConnectingPoints", asList(getConnectingPointMock(),
						getConnectingPointMock(), getConnectingPointMock()), true, true},
				{"Test JumpPoints", asList(getConnectingPointMock(),
						getJumpPointMock(), getJumpPointMock()), true, true},
				{"Test ChangePoints", asList(getConnectingPointMock(),
						getColorChangePointMock(), getColorChangePointMock()), false, false},
				{"Test ConnectingPoint after JumpPoint", asList(getConnectingPointMock(),
						getJumpPointMock(), getConnectingPointMock()), true, true},
				{"Test ConnectingPoint after ChangePoint", asList(getConnectingPointMock(),
						getColorChangePointMock(), getConnectingPointMock()), false, false},
				{"Test JumpPoint after ChangePoint", asList(getConnectingPointMock(),
						getColorChangePointMock(), getJumpPointMock()), false, false},
				{"Test ChangePoint after JumpPoint", asList(getConnectingPointMock(),
						getJumpPointMock(), getColorChangePointMock()), true, false}
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public List<StitchPoint> stitchPoints;

	@Parameterized.Parameter(2)
	public Boolean firstLineDrawn;

	@Parameterized.Parameter(3)
	public Boolean secondLineDrawn;

	private EmbroideryActor embroideryActorSpy;

	@Before
	public void setUp() throws Exception {
		EmbroideryPatternManager embroideryPatternManager = mock(EmbroideryPatternManager.class);
		when(embroideryPatternManager.getEmbroideryPatternList()).thenReturn(stitchPoints);
		ShapeRenderer renderer = mock(ShapeRenderer.class);
		embroideryActorSpy = spy(new EmbroideryActor(1.0f, embroideryPatternManager, renderer));
	}

	@Test
	public void testFirstLineDrawn() {
		embroideryActorSpy.draw(mock(Batch.class), 1f);
		VerificationMode mode = firstLineDrawn ? times(1) : never();
		verify(embroideryActorSpy, mode).drawLine(stitchPoints.get(0), stitchPoints.get(1));
	}

	@Test
	public void testSecondLineDrawn() {
		embroideryActorSpy.draw(mock(Batch.class), 1f);
		VerificationMode mode = secondLineDrawn ? times(1) : never();
		verify(embroideryActorSpy, mode).drawLine(stitchPoints.get(1), stitchPoints.get(2));
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
