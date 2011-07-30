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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import android.widget.Toast;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.SoundManager;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * @author Johannes Iber
 * 
 */
public class StageListener implements ApplicationListener {
	private static final boolean DEBUG = false;
	private Stage stage;
	private boolean paused = false;
	private boolean finished = false;
	private boolean firstStart = true;
	private Project project;

	private OrthographicCamera camera;
	private InputMultiplexer multiplexer;
	private SpriteBatch batch;
	private BitmapFont font;

	private ImmediateModeRenderer20 renderer;

	private List<Sprite> sprites;

	public int screenMode = Consts.STRETCH;
	public int maximizeViewPortX = 0;
	public int maximizeViewPortY = 0;
	public int maximizeViewPortHeight = 0;
	public int maximizeViewPortWidth = 0;

	public boolean axesOn = false;
	public boolean makeScreenshot = false;

	Texture pauseScreen;

	StageActivity stageActivity;

	public StageListener(StageActivity stageActivity) {
		this.stageActivity = stageActivity;
	}

	public void create() {
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(1f, 0f, 0.05f, 1f);
		font.setScale(1.2f);

		renderer = new ImmediateModeRenderer20(200, false, true, 0);
		project = ProjectManager.getInstance().getCurrentProject();
		stage = new Stage(project.VIRTUAL_SCREEN_WIDTH, project.VIRTUAL_SCREEN_HEIGHT, true);
		camera = (OrthographicCamera) stage.getCamera();
		camera.position.set(0, 0, 0);

		sprites = project.getSpriteList();
		for (Sprite sprite : sprites) {
			stage.addActor(sprite.costume);
		}
		if (DEBUG) {
			OrthoCamController camController = new OrthoCamController(camera);
			multiplexer = new InputMultiplexer();
			multiplexer.addProcessor(stage);
			multiplexer.addProcessor(camController);
			Gdx.input.setInputProcessor(multiplexer);
		} else {
			Gdx.input.setInputProcessor(stage);
		}

		pauseScreen = new Texture(Gdx.files.internal("data/paused_cat.png"));

	}

	public void resume() {
		paused = false;
		SoundManager.getInstance().resume();
		for (Sprite sprite : sprites) {
			sprite.resume();
		}

	}

	public void pause() {
		if (finished) {
			return;
		}
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
			sprite.finish();
		}
		ProjectManager.getInstance().textureRegionContainer.clear();
		pauseScreen.dispose();
	}

	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		switch (screenMode) {
			case Consts.MAXIMIZE:
				Gdx.gl.glViewport(maximizeViewPortX, maximizeViewPortY, maximizeViewPortWidth, maximizeViewPortHeight);
				break;
			case Consts.STRETCH:
			default:
				Gdx.gl.glViewport(0, 0, Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
				break;
		}

		renderRectangle(-1, -1, 2, 2, Color.WHITE);

		if (firstStart) {
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

		if (makeScreenshot) {
			String text;
			if (saveThumbnail()) {
				text = stageActivity.getString(R.string.screenshot_ok);
			} else {
				text = stageActivity.getString(R.string.error_screenshot_failed);
			}
			Toast toast = Toast.makeText(stageActivity, text, Toast.LENGTH_SHORT);
			toast.show();
		}

		if (paused && !finished) {
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			batch.draw(pauseScreen, -pauseScreen.getWidth() / 2, -pauseScreen.getHeight() / 2);
			batch.end();
		}

		if (axesOn && !finished) {
			renderAxes();
		}
	}

	private void renderRectangle(float x, float y, float width, float height, Color color) {
		renderer.begin(new Matrix4(), GL10.GL_TRIANGLE_FAN);
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

	private void renderAxes() {
		float virtualWidthHalf = project.VIRTUAL_SCREEN_WIDTH / 2;
		float virtualHeightHalf = project.VIRTUAL_SCREEN_HEIGHT / 2;
		renderer.begin(camera.combined, GL20.GL_LINES);
		renderer.color(1f, 0f, 0.05f, 1f);
		renderer.vertex(-virtualWidthHalf, 0, 0);
		renderer.color(1f, 0f, 0.05f, 1f);
		renderer.vertex(virtualWidthHalf, 0, 0);

		renderer.color(1f, 0f, 0.05f, 1f);
		renderer.vertex(0, -virtualHeightHalf, 0);
		renderer.color(1f, 0f, 0.05f, 1f);
		renderer.vertex(0, virtualHeightHalf, 0);
		renderer.end();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		font.draw(batch, "-" + (int) virtualWidthHalf, -virtualWidthHalf, 0);
		TextBounds bounds = font.getBounds(String.valueOf((int) virtualWidthHalf));
		font.draw(batch, String.valueOf((int) virtualWidthHalf), virtualWidthHalf - bounds.width, 0);

		font.draw(batch, "-" + (int) virtualHeightHalf, 0, -virtualHeightHalf + bounds.height);
		font.draw(batch, String.valueOf((int) virtualHeightHalf), 0, virtualHeightHalf);
		font.draw(batch, "0", 0, 0);
		batch.end();

	}

	public void resize(int width, int height) {
	}

	public void dispose() {
		if (!finished) {
			this.finish();
		}
	}

	public boolean saveThumbnail() {
		try {
			String path = Consts.DEFAULT_ROOT + "/" + ProjectManager.getInstance().getCurrentProject().getName() + "/";
			File file = new File(path + Consts.SCREENSHOT_FILE_NAME);
			File noMediaFile = new File(path + ".nomedia");
			if (!noMediaFile.exists()) {
				noMediaFile.createNewFile();
			}

			FileOutputStream fileOutputStream = new FileOutputStream(file.getAbsolutePath());
			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
			//canvasBitmap.compress(CompressFormat.PNG, 0, bos);
			bos.flush();
			bos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;

		}
	}

}
