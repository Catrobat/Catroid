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
package org.catrobat.catroid.common;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.parrot.freeflight.ui.gl.GLBGVideoSprite;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.bricks.Brick;

import java.io.FileNotFoundException;

public class DroneVideoLookData extends LookData {

	private static final String TAG = LookData.class.getSimpleName();

	private transient boolean firstStart = true;
	private transient GLBGVideoSprite videoTexture;
	private transient int[] videoSize = {0, 0};
	private transient int[] defaultVideoTextureSize;
	private transient boolean isLandscapeMode = true;

	@Override
	public DroneVideoLookData clone() {
		DroneVideoLookData cloneVideoLookData = new DroneVideoLookData();

		cloneVideoLookData.name = this.name;
		cloneVideoLookData.fileName = this.fileName;
		String filePath = getPathToImageDirectory() + "/" + fileName;
		try {
			ProjectManager.getInstance().getFileChecksumContainer().incrementUsage(filePath);
		} catch (FileNotFoundException fileNotFoundexception) {
			Log.e(TAG, Log.getStackTraceString(fileNotFoundexception));
		}

		return cloneVideoLookData;
	}

	@Override
	public int[] getMeasure() {
		return defaultVideoTextureSize.clone();
	}

	@Override
	public Pixmap getPixmap() {

		double virtualScreenWidth = Gdx.graphics.getWidth();
		// BUG: getHeight() should be 1200, but it is 1100, so we need an scaling factor of 1.1
		double virtualScreenHeight = Gdx.graphics.getHeight() * 1.1;
		double videoRatio = 64f / 36f;
		double videoHeight = virtualScreenWidth * videoRatio;
		double videoWidth = virtualScreenHeight / videoRatio;
		isLandscapeMode = ProjectManager.getInstance().getCurrentProject().isLandscapeMode();
		// Da im landscape modus schon gedreht wurde, entfehlt somit eine weitere Drehung

		if (isLandscapeMode) {
			//defaultVideoTextureSize = new int[]{(int) 10, (int) 10}; // it is a hack, but you don't need it anymore
			defaultVideoTextureSize = new int[]{(int) virtualScreenWidth, (int) virtualScreenHeight};
			Log.d(getClass().getSimpleName(), "virtualScreenWidth: " + virtualScreenWidth);
			Log.d(getClass().getSimpleName(), "virtualScreenHeight: " + virtualScreenHeight);

			OrthographicCamera camera = new OrthographicCamera();
			camera.setToOrtho(false, (int) virtualScreenWidth, (int) virtualScreenHeight);
			camera.viewportWidth = (float) virtualScreenHeight;
			camera.viewportHeight = (float) virtualScreenWidth;
			camera.update();

		} else {
			defaultVideoTextureSize = new int[]{(int) virtualScreenWidth, (int) videoHeight};
		}

		if (pixmap == null) {
			pixmap = new Pixmap(defaultVideoTextureSize[0], defaultVideoTextureSize[1], Pixmap.Format.RGB888);
			pixmap.setColor(Color.BLUE);
			pixmap.fill();
			pixmap.setBlending(Pixmap.Blending.None);

			//pixmap.setColor(0, 1, 0, 0.75f);
			// make a nice picture here if you like
		}
		return pixmap;
	}

	@Override
	public void setTextureRegion() {
		this.textureRegion = new TextureRegion(new Texture(getPixmap()));
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (firstStart) {
			videoTexture = new GLBGVideoSprite();
			onSurfaceChanged();
			firstStart = false;
		}

		if (videoSize[0] != videoTexture.imageWidth || videoSize[1] != videoTexture.imageHeight) {
			onSurfaceChanged();
		}

		Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_2D, textureRegion.getTexture().getTextureObjectHandle());
		videoTexture.onUpdateVideoTexture();

		/*if (isLandscapeMode) {
			batch.draw(textureRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
		}*/
	}

	private void onSurfaceChanged() {

		videoSize[0] = videoTexture.imageWidth;
		videoSize[1] = videoTexture.imageHeight;
		videoTexture.onSurfaceChanged(videoSize[0], videoSize[1]);

		setSize(1f, 1f * Gdx.graphics.getHeight() / Gdx.graphics.getWidth());
		//setVideoTextureToFullScreen();
	}

	private void setVideoTextureToFullScreen() {
		//inverted because of rotation
		float newX = getY() - (Gdx.graphics.getHeight() - getHeight()) / 2f;
		float newY = getX() - (Gdx.graphics.getWidth() - getWidth()) / 2f;
		setPosition(newX, newY);
		setSize(Gdx.graphics.getHeight(), Gdx.graphics.getWidth());
		setOrigin(getWidth() / 2f, getHeight() / 2f);
		setScale(1f, 1f);
		setRotation(90f);

	}


	@Override
	public int getRequiredResources() {
		return Brick.ARDRONE_SUPPORT;
	}
}
