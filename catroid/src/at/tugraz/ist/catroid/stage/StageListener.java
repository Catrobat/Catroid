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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.List;

import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.utils.RGBA8888ToPngEncoder;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;

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

	private boolean makeFirstScreenshot = true;
	private String pathForScreenshot;
	private int screenshotWidth;
	private int screenshotHeight;
	private int screenshotX;
	private int screenshotY;
	private byte[] screenshot;
	private boolean makeScreenshot = false;

	private Project project;

	private OrthographicCamera camera;
	private InputMultiplexer multiplexer;
	private SpriteBatch batch;
	private BitmapFont font;

	private ImmediateModeRenderer20 renderer;

	private List<Sprite> sprites;
	private Comparator<Actor> costumeComparator;

	private float virtualWidthHalf;
	private float virtualHeightHalf;

	public int screenMode = Consts.STRETCH;
	public int maximizeViewPortX = 0;
	public int maximizeViewPortY = 0;
	public int maximizeViewPortHeight = 0;
	public int maximizeViewPortWidth = 0;

	public boolean axesOn = false;

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

		pathForScreenshot = Consts.DEFAULT_ROOT + "/" + ProjectManager.getInstance().getCurrentProject().getName()
				+ "/";

		costumeComparator = new CostumeComparator();

		renderer = new ImmediateModeRenderer20(200, false, true, 0);
		project = ProjectManager.getInstance().getCurrentProject();

		virtualWidthHalf = project.VIRTUAL_SCREEN_WIDTH / 2;
		virtualHeightHalf = project.VIRTUAL_SCREEN_HEIGHT / 2;

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

		pauseScreen = new Texture(Gdx.files.internal("images/paused_cat.png"));
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

		stage.getRoot().sortChildren(costumeComparator);

		switch (screenMode) {
			case Consts.MAXIMIZE:
				Gdx.gl.glViewport(maximizeViewPortX, maximizeViewPortY, maximizeViewPortWidth, maximizeViewPortHeight);
				screenshotWidth = maximizeViewPortWidth;
				screenshotHeight = maximizeViewPortHeight;
				screenshotX = maximizeViewPortX;
				screenshotY = maximizeViewPortY;
				break;
			case Consts.STRETCH:
			default:
				Gdx.gl.glViewport(0, 0, Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
				screenshotWidth = Values.SCREEN_WIDTH;
				screenshotHeight = Values.SCREEN_HEIGHT;
				screenshotX = 0;
				screenshotY = 0;
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

		if (makeFirstScreenshot) {
			File file = new File(pathForScreenshot + Consts.SCREENSHOT_FILE_NAME);
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}

				this.saveThumbnail();
			}
			makeFirstScreenshot = false;
		}

		if (makeScreenshot) {
			screenshot = ScreenUtils.getFrameBufferPixels(screenshotX, screenshotY, screenshotWidth, screenshotHeight,
					true);
			makeScreenshot = false;
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

	private void saveThumbnail() {
		File noMediaFile = new File(pathForScreenshot + ".nomedia");
		try {
			noMediaFile.createNewFile();
		} catch (IOException e) {
			return;
		}
		byte[] screenshot = ScreenUtils.getFrameBufferPixels(screenshotX, screenshotY, screenshotWidth,
				screenshotHeight, true);
		FileHandle image = Gdx.files.absolute(pathForScreenshot + Consts.SCREENSHOT_FILE_NAME);
		OutputStream stream = image.write(false);
		try {
			byte[] bytes = RGBA8888ToPngEncoder.toPNG(screenshot, screenshotWidth, screenshotHeight);
			stream.write(bytes);
			stream.close();
		} catch (IOException e) {
		}

	}

	public boolean makeScreenshot() {
		File noMediaFile = new File(pathForScreenshot + ".nomedia");
		try {
			noMediaFile.createNewFile();
		} catch (IOException e) {
			return false;
		}
		makeScreenshot = true;
		while (makeScreenshot) {
			Thread.yield();
		}
		FileHandle image = Gdx.files.absolute(pathForScreenshot + Consts.SCREENSHOT_FILE_NAME);
		OutputStream stream = image.write(false);
		try {
			byte[] bytes = RGBA8888ToPngEncoder.toPNG(screenshot, screenshotWidth, screenshotHeight);
			stream.write(bytes);
			stream.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

}
