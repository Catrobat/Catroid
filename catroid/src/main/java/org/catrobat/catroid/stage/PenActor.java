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

package org.catrobat.catroid.stage;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.PenConfiguration;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.XmlHeader;

public class PenActor extends Actor {
	private FrameBuffer buffer;
	private Batch bufferBatch;
	private OrthographicCamera camera;
	private Float screenRatio;

	public PenActor() {
		XmlHeader header = ProjectManager.getInstance().getCurrentProject().getXmlHeader();
		buffer = new FrameBuffer(Pixmap.Format.RGBA8888, header.virtualScreenWidth, header.virtualScreenHeight, false);
		bufferBatch = new SpriteBatch();
		camera = new OrthographicCamera(header.virtualScreenWidth, header.virtualScreenHeight);
		bufferBatch.setProjectionMatrix(camera.combined);
		screenRatio = calculateScreenRatio();
		reset();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		buffer.begin();
		for (Sprite sprite : StageActivity.stageListener.getSpritesFromStage()) {
			PenConfiguration pen = sprite.penConfiguration;
			pen.drawLinesForSprite(screenRatio, getStage().getViewport().getCamera());
		}
		buffer.end();

		batch.end();
		TextureRegion region = new TextureRegion(buffer.getColorBufferTexture());
		region.flip(false, true);
		Image image = new Image(region);
		image.setPosition(-buffer.getWidth() / 2, -buffer.getHeight() / 2);
		batch.begin();
		image.draw(batch, parentAlpha);
	}

	public void reset() {
		buffer.begin();
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		buffer.end();
	}

	public void stampToFrameBuffer() {
		bufferBatch.begin();
		buffer.begin();
		for (Sprite sprite : StageActivity.stageListener.getSpritesFromStage()) {
			PenConfiguration pen = sprite.penConfiguration;
			if (pen.hasStamp()) {
				sprite.look.draw(bufferBatch, 1.0f);
				pen.setStamp(false);
			}
		}
		buffer.end();
		bufferBatch.end();
	}

	public void dispose() {
		if (buffer != null) {
			buffer.dispose();
			buffer = null;
		}
		if (bufferBatch != null) {
			bufferBatch.dispose();
			bufferBatch = null;
		}
	}

	private float calculateScreenRatio() {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		float deviceDiagonalPixel = (float) Math.sqrt(Math.pow(metrics.widthPixels, 2) + Math.pow(metrics.heightPixels, 2));

		XmlHeader header = ProjectManager.getInstance().getCurrentProject().getXmlHeader();
		float creatorDiagonalPixel = (float) Math.sqrt(Math.pow(header.getVirtualScreenWidth(), 2)
				+ Math.pow(header.getVirtualScreenHeight(), 2));
		return creatorDiagonalPixel / deviceDiagonalPixel;
	}
}
