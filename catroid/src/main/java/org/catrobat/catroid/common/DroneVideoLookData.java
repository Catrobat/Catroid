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
package org.catrobat.catroid.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.parrot.freeflight.ui.gl.GLBGVideoSprite;

import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.io.StorageOperations;

import java.io.File;
import java.io.IOException;

public class DroneVideoLookData extends LookData {

	private static final String TAG = DroneVideoLookData.class.getSimpleName();

	private transient boolean firstStart = true;
	private transient GLBGVideoSprite videoTexture;
	private transient int[] videoSize = {0, 0};
	private transient int[] defaultVideoTextureSize;

	public DroneVideoLookData() {
		super();
	}

	public DroneVideoLookData(String name, File file) {
		super(name, file);
	}

	@Override
	public DroneVideoLookData clone() {
		try {
			return new DroneVideoLookData(name, StorageOperations.duplicateFile(file));
		} catch (IOException e) {
			throw new RuntimeException(TAG + ": Could not copy file: " + file.getAbsolutePath());
		}
	}

	@Override
	public int[] getMeasure() {
		return defaultVideoTextureSize.clone();
	}

	@Override
	public Pixmap getPixmap() {
		// BUG: Height() should be 1280, but it is 1184, so we need an scaling factor of 1.081081
		int virtualScreenHeight = (int) Math.round(1.081081 * ScreenValues.SCREEN_HEIGHT);

		defaultVideoTextureSize = new int[] {virtualScreenHeight, ScreenValues.SCREEN_WIDTH};

		if (pixmap == null) {
			pixmap = new Pixmap(virtualScreenHeight, ScreenValues.SCREEN_WIDTH, Pixmap.Format.RGB888);
			pixmap.setColor(Color.BLUE);
			pixmap.fill();
			pixmap.setBlending(Pixmap.Blending.None);
		}
		return pixmap;
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
	}

	private void onSurfaceChanged() {
		videoSize[0] = videoTexture.imageWidth;
		videoSize[1] = videoTexture.imageHeight;
		videoTexture.onSurfaceChanged(videoSize[0], videoSize[1]);
	}

	@Override
	public void addRequiredResources(final Brick.ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(Brick.ARDRONE_SUPPORT);
	}
}
