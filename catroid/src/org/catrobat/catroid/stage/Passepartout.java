/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.stage;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Passepartout extends Actor {

	private float virtualScreenWidth;
	private float virtualScreenHeight;

	private float passepartoutHeight;
	private float passepartoutWidth;

	private ShapeRenderer screenModeMaximizedPassepartout;

	Passepartout(int screenWidth, int screenHeight, int screenViewPortWidth, int screenViewPortHeight,
			float virtualScreenWidth, float virtualScreenHeight, Matrix4 cameraCombined) {

		this.virtualScreenWidth = virtualScreenWidth;
		this.virtualScreenHeight = virtualScreenHeight;

		float scale = virtualScreenHeight / screenHeight;
		passepartoutHeight = ((screenHeight - screenViewPortHeight) * scale);

		scale = virtualScreenWidth / screenWidth;
		passepartoutWidth = ((screenWidth - screenViewPortWidth) * scale);

		screenModeMaximizedPassepartout = new ShapeRenderer();
		screenModeMaximizedPassepartout.setColor(0f, 0f, 0f, 1f);
		screenModeMaximizedPassepartout.setProjectionMatrix(cameraCombined);
	}

	public void setCameraCombined(Matrix4 cameraCombined) {
		screenModeMaximizedPassepartout.setProjectionMatrix(cameraCombined);
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {

		screenModeMaximizedPassepartout.begin(ShapeRenderer.ShapeType.FilledRectangle);

		if (Float.compare(passepartoutWidth, 0f) != 0) {
			screenModeMaximizedPassepartout.filledRect(-virtualScreenWidth / 2f, -virtualScreenHeight / 2f,
					-passepartoutWidth, virtualScreenHeight);
			screenModeMaximizedPassepartout.filledRect(virtualScreenWidth / 2f, virtualScreenHeight / 2f,
					passepartoutWidth, -virtualScreenHeight);

		} else if (Float.compare(passepartoutHeight, 0f) != 0) {
			screenModeMaximizedPassepartout.filledRect(-virtualScreenWidth / 2f, -virtualScreenHeight / 2f,
					virtualScreenWidth, -passepartoutHeight);
			screenModeMaximizedPassepartout.filledRect(virtualScreenWidth / 2f, virtualScreenHeight / 2f,
					-virtualScreenWidth, passepartoutHeight);
		}
		screenModeMaximizedPassepartout.end();
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
