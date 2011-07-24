/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.stage;

import java.util.List;

import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.SoundManager;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * @author Johannes Iber
 * 
 */
public class StageListener implements ApplicationListener {
	Stage stage;
	boolean paused = false;
	boolean finished = false;
	boolean firstStart = true;
	SpriteBatch batch;
	BitmapFont font;
	private OrthographicCamera camera;
	private OrthoCamController camController;
	private InputMultiplexer multiplexer;

	private final int VIRTUAL_WIDTH = 480;
	private final int VIRTUAL_HEIGHT = 800;
	private int DEVICE_WIDTH = 480;
	private int DEVICE_HEIGHT = 800;

	private ImmediateModeRenderer20 renderer;
	private Matrix4 projModelView = new Matrix4();

	private List<Sprite> sprites;

	public void create() {
		batch = new SpriteBatch();
		font = new BitmapFont();
		renderer = new ImmediateModeRenderer20(200, false, true, 0);

		stage = new Stage(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, true);
		camera = (OrthographicCamera) stage.getCamera();
		camera.position.set(0, 0, 0);
		camController = new OrthoCamController(camera);

		sprites = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		for (Sprite sprite : sprites) {
			stage.addActor(sprite.costume);
			//sprite.costume.action(Forever.$(Sequence.$(MoveBy.$(100, 100, 2), MoveBy.$(-100, -100, 2))));
		}

		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(stage);
		multiplexer.addProcessor(camController);
		Gdx.input.setInputProcessor(multiplexer);

	}

	public void resume() {
		paused = false;
		SoundManager.getInstance().resume();
		for (Sprite sprite : sprites) {
			sprite.resume();
		}

	}

	public void pause() {
		paused = true;
		SoundManager.getInstance().pause();
		for (Sprite sprite : sprites) {
			sprite.pause();
		}
	}

	public void finish() {
		finished = true;
		SoundManager.getInstance().clear();
		for (Sprite sprite : sprites) {
			if (sprite.costume.region != null && sprite.costume.region.getTexture() != null) {
				sprite.costume.region.getTexture().dispose();
			}
			sprite.costume.region = null;
			sprite.finish();
		}
	}

	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		float scale = VIRTUAL_WIDTH / (float) DEVICE_WIDTH;

		Gdx.gl.glViewport(0, (int) (DEVICE_HEIGHT - DEVICE_HEIGHT * scale) / 2, DEVICE_WIDTH,
				(int) (scale * DEVICE_HEIGHT));

		renderRectangle(projModelView, -1, -1, 2, 2, Color.WHITE);
		renderAxis(camera.combined);

		if (firstStart) {
			//TextureHandler.getInstance().loadTextures();
			for (Sprite sprite : sprites) {
				sprite.startStartScripts();
			}
			firstStart = false;
		}

		if (!paused) {
			stage.act(Gdx.graphics.getDeltaTime());
		}
		if (!finished) {
			stage.draw();
		}
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.begin();
		font.draw(batch, "res: " + Gdx.graphics.getWidth() + ", " + Gdx.graphics.getHeight(), 0, 30);
		batch.end();
	}

	private void renderRectangle(Matrix4 projModelView, float x, float y, float width, float height, Color color) {
		renderer.begin(projModelView, GL10.GL_TRIANGLE_FAN);
		renderer.color(color.r, color.g, color.b, color.a);
		renderer.vertex(x, y, 0);

		renderer.color(color.r, color.g, color.b, color.a);
		renderer.vertex(x + width, y, 0);

		renderer.color(color.r, color.g, color.b, color.a);
		renderer.vertex(x + width, y + height, 0);

		renderer.color(color.r, color.g, color.b, color.a);
		renderer.vertex(x, y + height, 0);
		renderer.end();
	}

	private void renderAxis(Matrix4 projModelView) {
		renderer.begin(projModelView, GL10.GL_LINES);
		renderer.color(0, 1, 0, 1);
		renderer.vertex(-VIRTUAL_WIDTH, 0, 0);
		renderer.color(0, 1, 0, 1);
		renderer.vertex(VIRTUAL_WIDTH, 0, 0);

		renderer.color(0, 1, 0, 1);
		renderer.vertex(0, -VIRTUAL_HEIGHT, 0);
		renderer.color(0, 1, 0, 1);
		renderer.vertex(0, VIRTUAL_HEIGHT, 0);
		renderer.end();
	}

	public void resize(int width, int height) {
		DEVICE_WIDTH = width;
		DEVICE_HEIGHT = height;
	}

	public void dispose() {
		if (!finished) {
			this.finish();
		}
	}

}
