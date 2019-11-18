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
	private Image image;

	@Before
	public void setUp() throws Exception {
		image = PowerMockito.mock(Image.class);
		Pixmap pixmap = PowerMockito.mock(Pixmap.class);
		Bitmap bitmap = PowerMockito.mock(Bitmap.class);
		Texture texture = PowerMockito.mock(Texture.class);

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
	public void testBubblePosition() {
		Integer calcXLeft = -347;
		Integer calcYLeft = 347;
		Integer calcXRight = 347;
		Integer calcYRight = 347;
		Float xLeft = -495.0f;
		Float yLeft = 347.0f;
		Float xRight = 347.0f;
		Float yRight = 347.0f;
		String name = "Test";

		Sprite sprite = new Sprite(name);
		sprite.look.setX(-540.0f);
		sprite.look.setY(-897.0f);
		sprite.look.setHeight(1794.0f);
		sprite.look.setWidth(1080.0f);

		Mockito.when(image.getWidth()).thenReturn(148.0f);

		ShowBubbleActor bubbleActor = new ShowBubbleActor(name, sprite, Constants.SAY_BRICK);

		assertEquals(xLeft, bubbleActor.calculateLeftImageX(calcXLeft));
		assertEquals(yLeft, bubbleActor.calculateImageY(calcYLeft));
		assertEquals(xRight, bubbleActor.calculateRightImageX(calcXRight));
		assertEquals(yRight, bubbleActor.calculateImageY(calcYRight));
	}

	@Test
	public void testBubblePositionAfterMoveX() {
		Integer calcXLeft = -347;
		Integer calcYLeft = 347;
		Integer calcXRight = 347;
		Integer calcYRight = 347;
		Float xLeft = 5.0f;
		Float yLeft = 347.0f;
		Float xRight = 847.0f;
		Float yRight = 347.0f;
		String name = "Test";

		Sprite sprite = new Sprite(name);
		sprite.look.setX(-40.0f);
		sprite.look.setY(-897.0f);
		sprite.look.setHeight(1794.0f);
		sprite.look.setWidth(1080.0f);

		Mockito.when(image.getWidth()).thenReturn(148.0f);

		ShowBubbleActor bubbleActor = new ShowBubbleActor(name, sprite, Constants.SAY_BRICK);

		assertEquals(xLeft, bubbleActor.calculateLeftImageX(calcXLeft));
		assertEquals(yLeft, bubbleActor.calculateImageY(calcYLeft));
		assertEquals(xRight, bubbleActor.calculateRightImageX(calcXRight));
		assertEquals(yRight, bubbleActor.calculateImageY(calcYRight));
	}

	@Test
	public void testBubblePositionAfterMoveY() {
		Integer calcXLeft = -347;
		Integer calcYLeft = 347;
		Integer calcXRight = 347;
		Integer calcYRight = 347;
		Float xLeft = -495.0f;
		Float yLeft = -153.0f;
		Float xRight = 347.0f;
		Float yRight = -153.0f;
		String name = "Test";

		Sprite sprite = new Sprite(name);
		sprite.look.setX(-540.0f);
		sprite.look.setY(-1397.0f);
		sprite.look.setHeight(1794.0f);
		sprite.look.setWidth(1080.0f);

		Mockito.when(image.getWidth()).thenReturn(148.0f);

		ShowBubbleActor bubbleActor = new ShowBubbleActor(name, sprite, Constants.SAY_BRICK);

		assertEquals(xLeft, bubbleActor.calculateLeftImageX(calcXLeft));
		assertEquals(yLeft, bubbleActor.calculateImageY(calcYLeft));
		assertEquals(xRight, bubbleActor.calculateRightImageX(calcXRight));
		assertEquals(yRight, bubbleActor.calculateImageY(calcYRight));
	}
}
