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

import android.graphics.PointF;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.XmlHeader;

public class PenActor extends Actor {
	private FrameBuffer buffer;
	private Batch bufferBatch;
	private OrthographicCamera camera;

	public PenActor() {
		XmlHeader header = ProjectManager.getInstance().getCurrentProject().getXmlHeader();
		buffer = new FrameBuffer(Pixmap.Format.RGBA8888, header.virtualScreenWidth, header.virtualScreenHeight, false);
		bufferBatch = new SpriteBatch();
		camera = new OrthographicCamera(header.virtualScreenWidth, header.virtualScreenHeight);
		bufferBatch.setProjectionMatrix(camera.combined);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		buffer.begin();
		for (Sprite sprite : ProjectManager.getInstance().getCurrentlyPlayingScene().getSpriteList()) {
			drawLinesForSprite(sprite);
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
		XmlHeader header = ProjectManager.getInstance().getCurrentProject().getXmlHeader();
		buffer.dispose();
		buffer = new FrameBuffer(Pixmap.Format.RGBA8888, header.virtualScreenWidth, header.virtualScreenHeight, false);
	}

	public void stampToFrameBuffer() {
		bufferBatch.begin();
		buffer.begin();
		for (Sprite sprite : ProjectManager.getInstance().getCurrentlyPlayingScene().getSpriteList()) {
			Sprite.PenConfiguration pen = sprite.penConfiguration;
			if (pen.stamp) {
				sprite.look.draw(bufferBatch, 1.0f);
				pen.stamp = false;
			}
		}
		buffer.end();
		bufferBatch.end();
	}

	private void drawLinesForSprite(Sprite sprite) {
		float x = sprite.look.getXInUserInterfaceDimensionUnit();
		float y = sprite.look.getYInUserInterfaceDimensionUnit();
		Sprite.PenConfiguration pen = sprite.penConfiguration;

		if (pen.previousPoint == null) {
			pen.previousPoint = new PointF(x, y);
			return;
		}

		ShapeRenderer renderer = StageActivity.stageListener.shapeRenderer;
		renderer.setColor(pen.penColor);
		renderer.begin(ShapeRenderer.ShapeType.Filled);

		if (pen.penDown && (pen.previousPoint.x != sprite.look.getX() || pen.previousPoint.y != sprite.look.getY())) {
			renderer.circle(pen.previousPoint.x, pen.previousPoint.y, pen.penSize / 2);
			renderer.rectLine(pen.previousPoint.x, pen.previousPoint.y, x, y, pen.penSize);
			renderer.circle(x, y, pen.penSize / 2);
		}

		renderer.end();
		pen.previousPoint.x = x;
		pen.previousPoint.y = y;
	}

	@Override
	public boolean remove() {
		boolean hadParent = super.remove();
		if (hadParent) {
			if (buffer != null) {
				buffer.dispose();
			}
			if (bufferBatch != null) {
				bufferBatch.dispose();
			}
		}
		return hadParent;
	}
}
