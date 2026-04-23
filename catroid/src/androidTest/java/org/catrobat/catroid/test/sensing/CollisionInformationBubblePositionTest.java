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

package org.catrobat.catroid.test.sensing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.sensing.CollisionInformation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.core.util.Pair;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;

import static org.mockito.Mockito.mock;

@RunWith(AndroidJUnit4.class)
public class CollisionInformationBubblePositionTest {
	public static final int BITMAP_WIDTH = 1080;
	public static final int BITMAP_HEIGHT = 2110;
	public static final float RECT_EDGE_LEFT = 255.0f;
	public static final float RECT_EDGE_TOP = 770.0f;
	public static final float RECT_EDGE_RIGHT = 825.0f;
	public static final float RECT_EDGE_BOTTOM = 1338.0f;
	private static final int EXPECTED_Y = 285;
	private CollisionInformation collisionInformation;
	private Bitmap bitmap;

	@Before
	public void setUp() throws Exception {

		bitmap = Bitmap.createBitmap(BITMAP_WIDTH, BITMAP_HEIGHT, Bitmap.Config.ARGB_8888);

		Paint paint = new Paint();
		paint.setColor(Color.BLACK);

		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.TRANSPARENT);
		canvas.drawRect(RECT_EDGE_LEFT, RECT_EDGE_TOP, RECT_EDGE_RIGHT, RECT_EDGE_BOTTOM, paint);

		LookData lookData = mock(LookData.class);

		collisionInformation = new CollisionInformation(lookData);
	}

	@Test
	public void testLeftBubblePositionCalculation() {
		Integer expectedXLeft = -285;
		Integer expectedYLeft = EXPECTED_Y;

		collisionInformation.calculateBubblePositions(bitmap);

		Pair<Integer, Integer> positionLeft = collisionInformation.getLeftBubblePos();

		assertEquals(expectedXLeft, positionLeft.first);
		assertEquals(expectedYLeft, positionLeft.second);
	}

	@Test
	public void testRightBubblePositionCalculation() {
		Integer expectedXRight = 284;
		Integer expectedYRight = EXPECTED_Y;

		collisionInformation.calculateBubblePositions(bitmap);

		Pair<Integer, Integer> positionRight = collisionInformation.getRightBubblePos();

		assertEquals(expectedXRight, positionRight.first);
		assertEquals(expectedYRight, positionRight.second);
	}
}

