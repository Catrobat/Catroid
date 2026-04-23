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

package org.catrobat.catroid.test.stage;

import android.graphics.Bitmap;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.stage.ShowBubbleActor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ShowBubbleActor.class, Image.class, Pixmap.class, Bitmap.class, Texture.class})
public class ShowBubbleActorTest {
	public static final float EXPECTED_X_LEFT_FOR_MOVE_X = 5.0f;
	public static final float EXPECTED_X_RIGHT_FOR_MOVE_X = 847.0f;
	private Image image;
	private Sprite sprite;
	private static final String OBJECT_NAME = "Test";
	private static final int CALC_X_LEFT = -347;
	private static final int CALC_Y_LEFT = 347;
	private static final int CALC_X_RIGHT = 347;
	private static final int CALC_Y_RIGHT = 347;
	private static final Float IMAGE_WIDTH = 148.0f;
	private static final Float EXPECTED_Y_STD = 347.0f;
	private static final Float EXPECTED_Y_MOVE_Y = -153.0f;
	private static final Float EXPECTED_X_STD_RIGHT = 347.0f;
	private static final Float EXPECTED_X_STD_LEFT = -495.0f;
	private static final Float SPRITE_WIDTH_DEFAULT = 1080.0f;
	private static final Float SPRITE_HEIGHT_DEFAULT = 1794.0f;
	private static final Float SPRITE_Y_DEFAULT = -897.0f;
	private static final Float SPRITE_X_DEFAULT = -540.0f;
	private static final Float SPRITE_X_FOR_MOVE_X = -40.0f;
	private static final Float SPRITE_Y_FOR_MOVE_Y = -1397.0f;

	@Before
	public void setUp() throws Exception {
		image = PowerMockito.mock(Image.class);
		Pixmap pixmap = PowerMockito.mock(Pixmap.class);
		Bitmap bitmap = PowerMockito.mock(Bitmap.class);
		Texture texture = PowerMockito.mock(Texture.class);

		sprite = new Sprite(OBJECT_NAME);
		sprite.look.setX(SPRITE_X_DEFAULT);
		sprite.look.setY(SPRITE_Y_DEFAULT);
		sprite.look.setHeight(SPRITE_HEIGHT_DEFAULT);
		sprite.look.setWidth(SPRITE_WIDTH_DEFAULT);

		Mockito.when(image.getWidth()).thenReturn(IMAGE_WIDTH);

		PowerMockito.mockStatic(Bitmap.class);

		Mockito.when(Bitmap.createBitmap(Mockito.any(Integer.class), Mockito.any(Integer.class),
				Mockito.any(Bitmap.Config.class))).thenReturn(bitmap);

		PowerMockito.whenNew(Image.class).withParameterTypes(Texture.class)
				.withArguments(Mockito.any()).thenReturn(image);

		PowerMockito.whenNew(Pixmap.class).withArguments(Mockito.anyByte(), Mockito.anyInt(),
				Mockito.anyInt()).thenReturn(pixmap);

		PowerMockito.whenNew(Texture.class).withParameterTypes(Pixmap.class)
				.withArguments(Mockito.any()).thenReturn(texture);
	}

	@Test
	public void testLeftBubblePosition() {
		ShowBubbleActor bubbleActor = new ShowBubbleActor(OBJECT_NAME, sprite, Constants.SAY_BRICK);

		assertEquals(EXPECTED_X_STD_LEFT, bubbleActor.calculateLeftImageX(CALC_X_LEFT));
		assertEquals(EXPECTED_Y_STD, bubbleActor.calculateImageY(CALC_Y_LEFT));
	}

	@Test
	public void testRightBubblePosition() {
		ShowBubbleActor bubbleActor = new ShowBubbleActor(OBJECT_NAME, sprite, Constants.SAY_BRICK);

		assertEquals(EXPECTED_X_STD_RIGHT, bubbleActor.calculateRightImageX(CALC_X_RIGHT));
		assertEquals(EXPECTED_Y_STD, bubbleActor.calculateImageY(CALC_Y_RIGHT));
	}

	@Test
	public void testLeftBubblePositionAfterMoveX() {
		sprite.look.setX(SPRITE_X_FOR_MOVE_X);

		ShowBubbleActor bubbleActor = new ShowBubbleActor(OBJECT_NAME, sprite, Constants.SAY_BRICK);

		assertEquals(EXPECTED_X_LEFT_FOR_MOVE_X, bubbleActor.calculateLeftImageX(CALC_X_LEFT));
		assertEquals(EXPECTED_Y_STD, bubbleActor.calculateImageY(CALC_Y_LEFT));
	}

	@Test
	public void testRightBubblePositionAfterMoveX() {
		sprite.look.setX(SPRITE_X_FOR_MOVE_X);

		ShowBubbleActor bubbleActor = new ShowBubbleActor(OBJECT_NAME, sprite, Constants.SAY_BRICK);

		assertEquals(EXPECTED_X_RIGHT_FOR_MOVE_X, bubbleActor.calculateRightImageX(CALC_X_RIGHT));
		assertEquals(EXPECTED_Y_STD, bubbleActor.calculateImageY(CALC_Y_RIGHT));
	}

	@Test
	public void testLeftBubblePositionAfterMoveY() {
		sprite.look.setY(SPRITE_Y_FOR_MOVE_Y);

		ShowBubbleActor bubbleActor = new ShowBubbleActor(OBJECT_NAME, sprite, Constants.SAY_BRICK);

		assertEquals(EXPECTED_X_STD_LEFT, bubbleActor.calculateLeftImageX(CALC_X_LEFT));
		assertEquals(EXPECTED_Y_MOVE_Y, bubbleActor.calculateImageY(CALC_Y_LEFT));
	}

	@Test
	public void testRightBubblePositionAfterMoveY() {
		sprite.look.setY(SPRITE_Y_FOR_MOVE_Y);

		ShowBubbleActor bubbleActor = new ShowBubbleActor(OBJECT_NAME, sprite, Constants.SAY_BRICK);

		assertEquals(EXPECTED_X_STD_RIGHT, bubbleActor.calculateRightImageX(CALC_X_RIGHT));
		assertEquals(EXPECTED_Y_MOVE_Y, bubbleActor.calculateImageY(CALC_Y_RIGHT));
	}
}
