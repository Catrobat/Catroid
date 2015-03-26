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

package org.catrobat.catroid.content;

import android.graphics.Bitmap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.parrot.freeflight.ui.gl.GLBGVideoSprite;

/**
 * Created by Lukas on 25.03.2015.
 */
public class DroneVideoLook extends Look {

	private boolean firstStart = true;
	private GLBGVideoSprite videoTexture;
	private TextureRegion textureRegion;
	private Texture texture;
	private int[] videoSize = {640, 480};

	public DroneVideoLook (Sprite sprite)
	{
		super(sprite);
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha)
	{
		if (firstStart) {
			initializeTexture();
		}

		if (videoSize[0] != videoTexture.imageWidth || videoSize[1] != videoTexture.imageHeight)
		{

			onSurfaceChanged();
		}

		Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_2D, texture.getTextureObjectHandle());
		videoTexture.onUpdateVideoTexture();

		batch.draw(textureRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
	}

	private void onSurfaceChanged()
	{
		videoSize[0] = videoTexture.imageWidth;
		videoSize[1] = videoTexture.imageHeight;
		videoTexture.onSurfaceChanged(videoSize[0], videoSize[1]);

		//float newX = getX() - (videoSize[0] - getWidth()) /2f;
		//float newY = getY() - (videoSize[1] - getHeight()) /2f;

		float newX = getX() - (Gdx.graphics.getHeight() - getWidth()) /2f;
		float newY = getY() - (Gdx.graphics.getWidth() - getHeight()) /2f;

		setPosition(newX, newY); //center
		//setSize(videoSize[0], videoSize[1]);
		//setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		setSize(Gdx.graphics.getHeight(), Gdx.graphics.getWidth());
		setOrigin(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2); //0,0 is in the center
		setScale(1f,1f);
		setRotation(270f);

	}

	private void initializeTexture()
	{
		videoTexture = new GLBGVideoSprite();
		Bitmap video = Bitmap.createBitmap(videoSize[0], videoSize[1], Bitmap.Config.RGB_565);
		onSurfaceChanged();
		texture = new Texture(video.getWidth(), video.getHeight(), Pixmap.Format.RGB888);
		textureRegion = new TextureRegion(texture);
		firstStart = false;
	}
}
