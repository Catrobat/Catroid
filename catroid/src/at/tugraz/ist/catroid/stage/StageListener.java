/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.stage;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.ui.dialogs.StageDialog;
import at.tugraz.ist.catroid.utils.Utils;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;

public class StageListener implements ApplicationListener {
	private static final boolean DEBUG = false;
	public static final String SCREENSHOT_FILE_NAME = "screenshot.png";
	private FPSLogger fpsLogger;

	private Stage stage;
	private boolean paused = false;
	private boolean finished = false;
	private boolean firstStart = true;
	private boolean reloadProject = false;

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
	private SpriteBatch batch;
	private BitmapFont font;

	private List<Sprite> sprites;
	private Comparator<Actor> costumeComparator;

	private float virtualWidthHalf;
	private float virtualHeightHalf;
	private float virtualWidth;
	private float virtualHeight;

	enum ScreenModes {
		STRETCH, MAXIMIZE
	};

	public ScreenModes screenMode;
	public int maximizeViewPortX = 0;
	public int maximizeViewPortY = 0;
	public int maximizeViewPortHeight = 0;
	public int maximizeViewPortWidth = 0;

	public boolean axesOn = false;

	private Texture background;
	private Texture axes;

	private boolean makeTestPixels = false;
	private byte[] testPixels;
	private int testX = 0;
	private int testY = 0;
	private int testWidth = 0;
	private int testHeight = 0;

	private StageDialog stageDialog;

	private boolean texturesRendered = false;

	public StageListener() {
	}

	@Override
	public void create() {

		font = new BitmapFont();
		font.setColor(1f, 0f, 0.05f, 1f);
		font.setScale(1.2f);

		pathForScreenshot = Utils.buildProjectPath(ProjectManager.getInstance().getCurrentProject().getName()) + "/";

		costumeComparator = new CostumeComparator();

		project = ProjectManager.getInstance().getCurrentProject();

		virtualWidth = project.virtualScreenWidth;
		virtualHeight = project.virtualScreenHeight;

		virtualWidthHalf = virtualWidth / 2;
		virtualHeightHalf = virtualHeight / 2;

		screenMode = ScreenModes.STRETCH;

		stage = new Stage(virtualWidth, virtualHeight, true);
		batch = stage.getSpriteBatch();

		camera = (OrthographicCamera) stage.getCamera();
		camera.position.set(0, 0, 0);

		sprites = project.getSpriteList();
		for (Sprite sprite : sprites) {
			stage.addActor(sprite.costume);
		}
		if (DEBUG) {
			OrthoCamController camController = new OrthoCamController(camera);
			InputMultiplexer multiplexer = new InputMultiplexer();
			multiplexer.addProcessor(camController);
			multiplexer.addProcessor(stage);
			Gdx.input.setInputProcessor(multiplexer);
			fpsLogger = new FPSLogger();
		} else {
			Gdx.input.setInputProcessor(stage);
		}

		background = new Texture(Gdx.files.internal("stage/white_pixel.bmp"));
		axes = new Texture(Gdx.files.internal("stage/red_pixel.bmp"));
	}

	public void menuResume() {
		if (reloadProject) {
			return;
		}
		paused = false;
		SoundManager.getInstance().resume();
		for (Sprite sprite : sprites) {
			sprite.resume();
		}
	}

	public void menuPause() {
		if (finished || reloadProject || (sprites == null)) {
			return;
		}
		paused = true;
		SoundManager.getInstance().pause();
		for (Sprite sprite : sprites) {
			sprite.pause();
		}
	}

	public void reloadProject(Context context, StageDialog stageDialog) {
		if (reloadProject) {
			return;
		}
		this.stageDialog = stageDialog;
		ProjectManager projectManager = ProjectManager.getInstance();
		int currentSpritePos = projectManager.getCurrentSpritePosition();
		int currentScriptPos = projectManager.getCurrentScriptPosition();
		projectManager.loadProject(projectManager.getCurrentProject().getName(), context, false);
		projectManager.setCurrentSpriteWithPosition(currentSpritePos);
		projectManager.setCurrentScriptWithPosition(currentScriptPos);
		reloadProject = true;
	}

	@Override
	public void resume() {
		if (!paused) {
			SoundManager.getInstance().resume();
			for (Sprite sprite : sprites) {
				sprite.resume();
			}
		}
		renderTextures();
		for (Sprite sprite : sprites) {
			sprite.costume.refreshTextures();
		}

	}

	@Override
	public void pause() {
		if (finished || (sprites == null)) {
			return;
		}
		if (!paused) {
			SoundManager.getInstance().pause();
			for (Sprite sprite : sprites) {
				sprite.pause();
			}
		}
	}

	public void finish() {
		finished = true;
		SoundManager.getInstance().clear();
		if (sprites != null) {
			for (Sprite sprite : sprites) {
				sprite.finish();
			}
		}
	}

	@Override
	public void render() {

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (reloadProject) {
			int spriteSize = sprites.size();
			for (int i = 0; i < spriteSize; i++) {
				Sprite sprite = sprites.get(i);
				sprite.pause();
				sprite.finish();
			}
			stage.clear();
			SoundManager.getInstance().clear();

			project = ProjectManager.getInstance().getCurrentProject();
			sprites = project.getSpriteList();
			for (int i = 0; i < spriteSize; i++) {
				Sprite sprite = sprites.get(i);
				stage.addActor(sprite.costume);
				sprite.pause();
			}

			paused = true;
			firstStart = true;
			reloadProject = false;
			synchronized (stageDialog) {
				stageDialog.notify();
			}
		}

		if (!texturesRendered) {
			renderTextures();
			texturesRendered = true;
		}

		stage.getRoot().sortChildren(costumeComparator);

		switch (screenMode) {
			case MAXIMIZE:
				Gdx.gl.glViewport(maximizeViewPortX, maximizeViewPortY, maximizeViewPortWidth, maximizeViewPortHeight);
				screenshotWidth = maximizeViewPortWidth;
				screenshotHeight = maximizeViewPortHeight;
				screenshotX = maximizeViewPortX;
				screenshotY = maximizeViewPortY;
				break;
			case STRETCH:
			default:
				Gdx.gl.glViewport(0, 0, Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
				screenshotWidth = Values.SCREEN_WIDTH;
				screenshotHeight = Values.SCREEN_HEIGHT;
				screenshotX = 0;
				screenshotY = 0;
				break;
		}

		this.drawRectangle();

		if (firstStart) {
			int spriteSize = sprites.size();
			for (int i = 0; i < spriteSize; i++) {
				sprites.get(i).startStartScripts();
			}
			firstStart = false;
		}
		if (!paused) {
			stage.act(Gdx.graphics.getDeltaTime());
		}

		if (!finished) {
			stage.draw();
		}

		if (makeFirstScreenshot && !NativeAppActivity.isRunning()) {
			File file = new File(pathForScreenshot + SCREENSHOT_FILE_NAME);
			if (!file.exists()) {
				File noMediaFile = new File(pathForScreenshot + ".nomedia");
				try {
					file.createNewFile();
					noMediaFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				this.makeThumbnail();
			}
			makeFirstScreenshot = false;
		}

		if (makeScreenshot) {
			screenshot = ScreenUtils.getFrameBufferPixels(screenshotX, screenshotY, screenshotWidth, screenshotHeight,
					true);
			makeScreenshot = false;
		}

		//		if (paused && !finished) {
		//			batch.setProjectionMatrix(camera.combined);
		//			batch.begin();
		//			batch.draw(pauseScreen, -pauseScreen.getWidth() / 2, -pauseScreen.getHeight() / 2);
		//			batch.end();
		//		}

		if (axesOn && !finished) {
			drawAxes();
		}

		if (DEBUG) {
			fpsLogger.log();
		}

		if (makeTestPixels) {
			testPixels = ScreenUtils.getFrameBufferPixels(testX, testY, testWidth, testHeight, false);
			makeTestPixels = false;
		}
	}

	private void drawRectangle() {
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(background, -virtualWidthHalf, -virtualHeightHalf, virtualWidth, virtualHeight);
		batch.end();
	}

	private void drawAxes() {
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(axes, -virtualWidthHalf, 0, virtualWidth, 1);
		batch.draw(axes, 0, -virtualHeightHalf, 1, virtualHeight);

		font.draw(batch, "-" + (int) virtualWidthHalf, -virtualWidthHalf, 0);
		TextBounds bounds = font.getBounds(String.valueOf((int) virtualWidthHalf));
		font.draw(batch, String.valueOf((int) virtualWidthHalf), virtualWidthHalf - bounds.width, 0);

		font.draw(batch, "-" + (int) virtualHeightHalf, 0, -virtualHeightHalf + bounds.height);
		font.draw(batch, String.valueOf((int) virtualHeightHalf), 0, virtualHeightHalf);
		font.draw(batch, "0", 0, 0);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void dispose() {
		if (!finished) {
			this.finish();
		}
		stage.dispose();
		font.dispose();
		background.dispose();
		axes.dispose();
		disposeTextures();
	}

	private void makeThumbnail() {
		byte[] screenshot = ScreenUtils.getFrameBufferPixels(screenshotX, screenshotY, screenshotWidth,
				screenshotHeight, true);
		this.saveScreenshot(screenshot);
	}

	public boolean makeScreenshot() {
		makeScreenshot = true;
		while (makeScreenshot) {
			Thread.yield();
		}
		return this.saveScreenshot(this.screenshot);
	}

	private boolean saveScreenshot(byte[] screenshot) {
		int length = screenshot.length;
		int[] colors = new int[length / 4];

		for (int i = 0; i < length; i += 4) {
			colors[i / 4] = Color.argb(255, screenshot[i + 0] & 0xFF, screenshot[i + 1] & 0xFF,
					screenshot[i + 2] & 0xFF);
		}

		Bitmap bitmap = Bitmap.createBitmap(colors, 0, screenshotWidth, screenshotWidth, screenshotHeight,
				Config.ARGB_8888);

		FileHandle image = Gdx.files.absolute(pathForScreenshot + SCREENSHOT_FILE_NAME);
		OutputStream stream = image.write(false);
		try {
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			stream.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public byte[] getPixels(int x, int y, int width, int height) {
		testX = x;
		testY = y;
		testWidth = width;
		testHeight = height;
		makeTestPixels = true;
		while (makeTestPixels) {
			Thread.yield();
		}
		return testPixels;
	}

	public void changeScreenSize() {
		switch (screenMode) {
			case MAXIMIZE:
				screenMode = ScreenModes.STRETCH;
				break;
			case STRETCH:
				screenMode = ScreenModes.MAXIMIZE;
				break;
		}
	}

	private void renderTextures() {
		List<Sprite> sprites = project.getSpriteList();
		int spriteSize = sprites.size();
		for (int i = 0; i > spriteSize; i++) {
			List<CostumeData> data = sprites.get(i).getCostumeDataList();
			int dataSize = data.size();
			for (int j = 0; j < dataSize; j++) {
				CostumeData costumeData = data.get(j);
				costumeData.setTextureRegion();
			}
		}
	}

	private void disposeTextures() {
		List<Sprite> sprites = project.getSpriteList();
		int spriteSize = sprites.size();
		for (int i = 0; i > spriteSize; i++) {
			List<CostumeData> data = sprites.get(i).getCostumeDataList();
			int dataSize = data.size();
			for (int j = 0; j < dataSize; j++) {
				CostumeData costumeData = data.get(j);
				costumeData.getPixmap().dispose();
				costumeData.getTextureRegion().getTexture().dispose();
			}
		}
	}

}
