/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.test.common;

import android.support.test.runner.AndroidJUnit4;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import org.catrobat.catroid.common.LookData;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.fail;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class LookDataTest {

	@Test
	public void testPixmapAndTextureRegionDisposal() {
		LookData lookData = new LookData();
		Pixmap pixmap = mock(Pixmap.class);
		Texture texture = mock(Texture.class);
		TextureRegion textureRegion = new TextureRegion(texture);

		lookData.setPixmap(pixmap);
		lookData.setTextureRegion(textureRegion);

		lookData.resetLookData();

		verify(pixmap, times(1)).dispose();
		verify(textureRegion.getTexture(), times(1)).dispose();
	}

	@Test
	public void testNoExceptionThrownOnCallingResetLookData() {
		LookData lookData = new LookData();
		try {
			lookData.resetLookData();
		} catch (Exception error) {
			fail();
		}
	}
}
