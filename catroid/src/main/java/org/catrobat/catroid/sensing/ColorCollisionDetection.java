/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

package org.catrobat.catroid.sensing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.catrobat.catroid.common.ScreenModes;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.formulaeditor.common.Conversion.convertToBitmap;
import static org.catrobat.catroid.formulaeditor.common.Conversion.matchesColor;
import static org.catrobat.catroid.formulaeditor.common.Conversion.tryParseColor;

public class ColorCollisionDetection {
	public static boolean interpretFunctionTouchesColor(Object parameter, Sprite sprite, Project currentProject, Scene currentlyPlayingScene) {
		if (!(parameter instanceof String)) {
			return false;
		} else if (sprite.look.getWidth() <= Float.MIN_VALUE || sprite.look.getHeight() <= Float.MIN_VALUE) {
			return false;
		}

		String stringParameter = (String) parameter;
		int color = tryParseColor(stringParameter) & 0xFFFFFF;

		List<Sprite> spriteList = new ArrayList<>(currentlyPlayingScene.getSpriteList());
		spriteList.remove(sprite);

		Camera camera = createCamera(currentProject, sprite.look, sprite.look.getWidth() * sprite.look.getScaleX(), sprite.look.getHeight() * sprite.look.getScaleY());

		Pixmap pixmap = drawSprites(spriteList, camera.combined, sprite.look);
		//Bitmap bitmap = flipBitmap(convertToBitmap(pixmap));

		ByteBuffer pixels = pixmap.getPixels();
		pixmap.dispose();

		return matchesColor(pixels, color);
	}

	private static SpriteBatch batch = new SpriteBatch();

	private static Pixmap drawSprites(List<Sprite> spriteList, Matrix4 projectionMatrix, Actor actor) {
		FrameBuffer buffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int) actor.getWidth(),
				(int) actor.getHeight(), false);

		batch.setProjectionMatrix(projectionMatrix);

		buffer.begin();
		batch.begin();

		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

		for (Sprite sprite : spriteList) {
			sprite.look.draw(batch, 1);
		}

		batch.end();

		Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, buffer.getWidth(), buffer.getHeight());
		buffer.end();

		//batch.dispose();
		buffer.dispose();

		return pixmap;
	}

	private static Camera createCamera(Project project, Actor actor, float width, float height) {
		Camera camera = new OrthographicCamera(width, height);
		Viewport viewPort = createViewPort(project, width, height, camera);
		viewPort.apply();
		camera.position.set(actor.getX() + width / 2, actor.getY() + height / 2, 0);
		camera.update();
		return camera;
	}

	private static Viewport createViewPort(Project project, float virtualWidth, float virtualHeight, Camera camera) {
		if (project.getScreenMode() == ScreenModes.STRETCH) {
			return new ScalingViewport(Scaling.stretch, virtualWidth, virtualHeight, camera);
		} else {
			return new ExtendViewport(virtualWidth, virtualHeight, camera);
		}
	}
}
