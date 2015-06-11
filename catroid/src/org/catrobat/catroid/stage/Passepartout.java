/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Passepartout extends Actor {

	private float virtualScreenWidth;
	private float virtualScreenHeight;

	private float passepartoutHeight;
	private float passepartoutWidth;

	private Texture texture;

	Passepartout(int screenWidth, int screenHeight, int screenViewPortWidth, int screenViewPortHeight,
			float virtualScreenWidth, float virtualScreenHeight) {

		this.virtualScreenWidth = virtualScreenWidth;
		this.virtualScreenHeight = virtualScreenHeight;

		passepartoutHeight = ((screenHeight / (screenViewPortHeight / virtualScreenHeight)) - virtualScreenHeight) / 2f;
		passepartoutWidth = ((screenWidth / (screenViewPortWidth / virtualScreenWidth)) - virtualScreenWidth) / 2f;

		Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pixmap.setColor(Color.BLACK);
		pixmap.fill();
		texture = new Texture(pixmap);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (Float.compare(passepartoutWidth, 0f) != 0) {
			batch.draw(texture, -virtualScreenWidth / 2f, -virtualScreenHeight / 2f, -passepartoutWidth,
					virtualScreenHeight);
			batch.draw(texture, virtualScreenWidth / 2f, virtualScreenHeight / 2f, passepartoutWidth,
					-virtualScreenHeight);
		}
		if (Float.compare(passepartoutHeight, 0f) != 0) {
			batch.draw(texture, -virtualScreenWidth / 2f, -virtualScreenHeight / 2f, virtualScreenWidth,
					-passepartoutHeight);
			batch.draw(texture, virtualScreenWidth / 2f, virtualScreenHeight / 2f, -virtualScreenWidth,
					passepartoutHeight);
		}
		batch.flush();
	}

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		if (x < -virtualScreenWidth / 2 || x > virtualScreenWidth / 2 || y < -virtualScreenHeight / 2
				|| y > virtualScreenHeight / 2) {
			return this;
		}
		return null;
	}
}
