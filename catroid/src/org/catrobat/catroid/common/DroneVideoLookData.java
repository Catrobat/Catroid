/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

import android.graphics.Color;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.parrot.freeflight.ui.gl.GLBGVideoSprite;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.bricks.Brick;

import java.io.Serializable;

public class DroneVideoLookData extends LookData implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private static final String TAG = DroneVideoLookData.class.getSimpleName();
	private boolean firstStart;
	private GLBGVideoSprite videoTexture;

	private int[] videoSize = {0, 0};
	private final int[] defaultVideoTextureSize;

	public DroneVideoLookData () {

		firstStart = true;
		defaultVideoTextureSize = fitToScreenWidth();
	}

	private int[] fitToScreenWidth()
	{
		float virtualScreenWidth = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenWidth;
		float videoRatio = 64f/36f; //fixed ratio of the droneVideo -> see developer guide
		float videoHeight = virtualScreenWidth / videoRatio;

		return new int[] {(int)virtualScreenWidth, (int)videoHeight};
	}

	@Override
	public int[] getMeasure() {
		return defaultVideoTextureSize;
	}

	@Override
	public Pixmap getPixmap() {
		if (pixmap == null) {
			pixmap = new Pixmap(defaultVideoTextureSize[0], defaultVideoTextureSize[1], Pixmap.Format.RGB888);
			pixmap.setColor(Color.BLUE);
			pixmap.fill();
		}
		return pixmap;
	}

	@Override
	public void setTextureRegion() {
		this.region = new TextureRegion(new Texture(getPixmap()));

	}

	@Override
	public void onDraw()
	{
		if (firstStart)
		{
			videoTexture = new GLBGVideoSprite();
			onSurfaceChanged();
			firstStart = false;
		}

		if (videoSize[0] != videoTexture.imageWidth || videoSize[1] != videoTexture.imageHeight)
		{
			onSurfaceChanged();
		}

		Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_2D, region.getTexture().getTextureObjectHandle());
		videoTexture.onUpdateVideoTexture();
	}

	private void onSurfaceChanged()
	{

		videoSize[0] = videoTexture.imageWidth;
		videoSize[1] = videoTexture.imageHeight;
		videoTexture.onSurfaceChanged(videoSize[0], videoSize[1]);

	}

	@Override
	public int getRequiredResources()
	{
		return Brick.ARDRONE_SUPPORT;
	}
}
