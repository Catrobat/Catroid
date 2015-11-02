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
package org.catrobat.catroid.stage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.SystemClock;
import android.util.Log;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.collect.Multimap;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenModes;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.BroadcastHandler;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.ui.dialogs.StageDialog;
import org.catrobat.catroid.utils.LedUtil;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.utils.VibratorUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StageListener implements ApplicationListener {

	private static final String TAG = StageListener.class.getSimpleName();
	private static final int AXIS_WIDTH = 4;
	private static final float DELTA_ACTIONS_DIVIDER_MAXIMUM = 50f;
	private static final int ACTIONS_COMPUTATION_TIME_MAXIMUM = 8;
	private static final boolean DEBUG = false;
	private static final java.lang.String SEQUENCE = "Sequence(";
	public static final String BROADCAST_NOTIFY = ", BroadcastNotify)";

	// needed for UiTests - is disabled to fix crashes with EMMA coverage
	// CHECKSTYLE DISABLE StaticVariableNameCheck FOR 1 LINES
	private static boolean DYNAMIC_SAMPLING_RATE_FOR_ACTIONS = true;

	private float deltaActionTimeDivisor = 10f;
	public static final String SCREENSHOT_AUTOMATIC_FILE_NAME = "automatic_screenshot"
			+ Constants.IMAGE_STANDARD_EXTENTION;
	public static final String SCREENSHOT_MANUAL_FILE_NAME = "manual_screenshot" + Constants.IMAGE_STANDARD_EXTENTION;
	private FPSLogger fpsLogger;

	private Stage stage;
	private boolean paused = false;
	private boolean finished = false;
	private boolean firstStart = true;
	private boolean reloadProject = false;

	private static boolean checkIfAutomaticScreenshotShouldBeTaken = true;
	private boolean makeAutomaticScreenshot = false;
	private boolean makeScreenshot = false;
	private String pathForScreenshot;
	private int screenshotWidth;
	private int screenshotHeight;
	private int screenshotX;
	private int screenshotY;
	private byte[] screenshot = null;
	// in first frame, framebuffer could be empty and screenshot
	// would be white
	private boolean skipFirstFrameForAutomaticScreenshot;

	private Project project;

	private OrthographicCamera camera;
	private Batch batch;
	private BitmapFont font;
	private Passepartout passepartout;
	private Viewport viewPort;

	private List<Sprite> sprites;

	private float virtualWidthHalf;
	private float virtualHeightHalf;
	private float virtualWidth;
	private float virtualHeight;

	private Texture axes;

	private boolean makeTestPixels = false;
	private byte[] testPixels;
	private int testX = 0;
	private int testY = 0;
	private int testWidth = 0;
	private int testHeight = 0;

	private StageDialog stageDialog;

	public int maximizeViewPortX = 0;
	public int maximizeViewPortY = 0;
	public int maximizeViewPortHeight = 0;
	public int maximizeViewPortWidth = 0;

	public boolean axesOn = false;

	private byte[] thumbnail;

	StageListener() {
	}

	@Override
	public void create() {
		font = new BitmapFont();
		font.setColor(1f, 0f, 0.05f, 1f);
		font.getData().setScale(1.2f);

		project = ProjectManager.getInstance().getCurrentProject();
		pathForScreenshot = Utils.buildProjectPath(project.getName()) + "/";

		virtualWidth = project.getXmlHeader().virtualScreenWidth;
		virtualHeight = project.getXmlHeader().virtualScreenHeight;

		virtualWidthHalf = virtualWidth / 2;
		virtualHeightHalf = virtualHeight / 2;

		camera = new OrthographicCamera();
		viewPort = new ExtendViewport(virtualWidth, virtualHeight, camera);
		batch = new SpriteBatch();
		stage = new Stage(viewPort, batch);
		initScreenMode();

		sprites = project.getSpriteList();
		for (Sprite sprite : sprites) {
			sprite.resetSprite();
			sprite.look.createBrightnessContrastShader();
			stage.addActor(sprite.look);
			sprite.resume();
		}

		passepartout = new Passepartout(ScreenValues.SCREEN_WIDTH, ScreenValues.SCREEN_HEIGHT, maximizeViewPortWidth,
				maximizeViewPortHeight, virtualWidth, virtualHeight);
		stage.addActor(passepartout);

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

		axes = new Texture(Gdx.files.internal("stage/red_pixel.bmp"));
		skipFirstFrameForAutomaticScreenshot = true;
		if (checkIfAutomaticScreenshotShouldBeTaken) {
			makeAutomaticScreenshot = project.manualScreenshotExists(SCREENSHOT_MANUAL_FILE_NAME);
		}
	}

	void activityResume() {
		if (!paused) {
			FaceDetectionHandler.resumeFaceDetection();
		}
	}

	void activityPause() {
		FaceDetectionHandler.pauseFaceDetection();
	}

	void menuResume() {
		if (reloadProject) {
			return;
		}
		paused = false;
		FaceDetectionHandler.resumeFaceDetection();
		SoundManager.getInstance().resume();
		for (Sprite sprite : sprites) {
			sprite.resume();
		}
	}

	void menuPause() {
		if (finished || reloadProject) {
			return;
		}

		try {
			paused = true;
			SoundManager.getInstance().pause();
			for (Sprite sprite : sprites) {
				sprite.pause();
			}
		} catch (Exception exception) {
			Log.e(TAG, "Pausing menu failed!", exception);
		}
	}

	public void reloadProject(Context context, StageDialog stageDialog) {
		if (reloadProject) {
			return;
		}
		this.stageDialog = stageDialog;

		project.getDataContainer().resetAllDataObjects();

		LedUtil.reset();
		VibratorUtil.reset();

		ProjectManager.getInstance().getCurrentProject().getDataContainer().resetAllDataObjects();

		reloadProject = true;
	}

	@Override
	public void resume() {
		if (!paused) {
			FaceDetectionHandler.resumeFaceDetection();
			SoundManager.getInstance().resume();
			for (Sprite sprite : sprites) {
				sprite.resume();
			}
		}

		for (Sprite sprite : sprites) {
			sprite.look.refreshTextures();
		}
	}

	@Override
	public void pause() {
		if (finished) {
			return;
		}
		if (!paused) {
			FaceDetectionHandler.pauseFaceDetection();
			SoundManager.getInstance().pause();
			for (Sprite sprite : sprites) {
				sprite.pause();
			}
		}
	}

	public void finish() {
		finished = true;
		SoundManager.getInstance().clear();
		if (thumbnail != null && !makeAutomaticScreenshot) {
			saveScreenshot(thumbnail, SCREENSHOT_AUTOMATIC_FILE_NAME);
		}
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (reloadProject) {
			int spriteSize = sprites.size();
			for (int i = 0; i < spriteSize; i++) {
				sprites.get(i).pause();
			}
			stage.clear();
			SoundManager.getInstance().clear();

			Sprite sprite;
			if (spriteSize > 0) {
				sprites.get(0).look.setLookData(createWhiteBackgroundLookData());
			}
			for (int i = 0; i < spriteSize; i++) {
				sprite = sprites.get(i);
				sprite.resetSprite();
				sprite.look.createBrightnessContrastShader();
				stage.addActor(sprite.look);
				sprite.pause();
			}
			stage.addActor(passepartout);

			paused = true;
			firstStart = true;
			reloadProject = false;
			synchronized (stageDialog) {
				stageDialog.notify();
			}
		}

		batch.setProjectionMatrix(camera.combined);

		if (firstStart) {
			int spriteSize = sprites.size();
			if (spriteSize > 0) {
				sprites.get(0).look.setLookData(createWhiteBackgroundLookData());
			}
			Map<String, List<String>> scriptActions = new HashMap<String, List<String>>();
			for (int currentSprite = 0; currentSprite < spriteSize; currentSprite++) {
				Sprite sprite = sprites.get(currentSprite);
				sprite.createStartScriptActionSequenceAndPutToMap(scriptActions);
				if (!sprite.getLookDataList().isEmpty()) {
					sprite.look.setLookData(sprite.getLookDataList().get(0));
				}
			}

			if (scriptActions.get(Constants.BROADCAST_SCRIPT) != null && !scriptActions.get(Constants.BROADCAST_SCRIPT).isEmpty()) {
				List<String> broadcastWaitNotifyActions = reconstructNotifyActions(scriptActions);
				Map<String, List<String>> notifyMap = new HashMap<String, List<String>>();
				notifyMap.put(Constants.BROADCAST_NOTIFY_ACTION, broadcastWaitNotifyActions);
				scriptActions.putAll(notifyMap);
			}
			precomputeActionsForBroadcastEvents(scriptActions);
			firstStart = false;
		}
		if (!paused) {
			float deltaTime = Gdx.graphics.getDeltaTime();

			/*
			 * Necessary for UiTests, when EMMA - code coverage is enabled.
			 * 
			 * Without setting DYNAMIC_SAMPLING_RATE_FOR_ACTIONS to false(via reflection), before
			 * the UiTest enters the stage, random segmentation faults(triggered by EMMA) will occur.
			 * 
			 * Can be removed, when EMMA is replaced by an other code coverage tool, or when a
			 * future EMMA - update will fix the bugs.
			 */
			if (!DYNAMIC_SAMPLING_RATE_FOR_ACTIONS) {
				stage.act(deltaTime);
			} else {
				float optimizedDeltaTime = deltaTime / deltaActionTimeDivisor;
				long timeBeforeActionsUpdate = SystemClock.uptimeMillis();
				while (deltaTime > 0f) {
					stage.act(optimizedDeltaTime);
					deltaTime -= optimizedDeltaTime;
				}
				long executionTimeOfActionsUpdate = SystemClock.uptimeMillis() - timeBeforeActionsUpdate;
				if (executionTimeOfActionsUpdate <= ACTIONS_COMPUTATION_TIME_MAXIMUM) {
					deltaActionTimeDivisor += 1f;
					deltaActionTimeDivisor = Math.min(DELTA_ACTIONS_DIVIDER_MAXIMUM, deltaActionTimeDivisor);
				} else {
					deltaActionTimeDivisor -= 1f;
					deltaActionTimeDivisor = Math.max(1f, deltaActionTimeDivisor);
				}
			}
		}

		if (!finished) {
			stage.draw();
		}

		if (makeAutomaticScreenshot) {
			if (skipFirstFrameForAutomaticScreenshot) {
				skipFirstFrameForAutomaticScreenshot = false;
			} else {
				thumbnail = ScreenUtils.getFrameBufferPixels(screenshotX, screenshotY, screenshotWidth,
						screenshotHeight, true);
				makeAutomaticScreenshot = false;
			}
		}

		if (makeScreenshot) {
			screenshot = ScreenUtils.getFrameBufferPixels(screenshotX, screenshotY, screenshotWidth, screenshotHeight,
					true);
			makeScreenshot = false;
		}

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

	private List<String> reconstructNotifyActions(Map<String, List<String>> actions) {
		List<String> broadcastWaitNotifyActions = new ArrayList<String>();
		for (String actionString : actions.get(Constants.BROADCAST_SCRIPT)) {
			String broadcastNotifyString = SEQUENCE + actionString.substring(0, actionString.indexOf(Constants.ACTION_SPRITE_SEPARATOR)) + BROADCAST_NOTIFY + actionString.substring(actionString.indexOf(Constants.ACTION_SPRITE_SEPARATOR));
			broadcastWaitNotifyActions.add(broadcastNotifyString);
		}
		return broadcastWaitNotifyActions;
	}

	public void precomputeActionsForBroadcastEvents(Map<String, List<String>> currentActions) {
		Multimap<String, String> actionsToRestartMap = BroadcastHandler.getActionsToRestartMap();
		if (!actionsToRestartMap.isEmpty()) {
			return;
		}
		List<String> actions = new ArrayList<String>();
		if (currentActions.get(Constants.START_SCRIPT) != null) {
			actions.addAll(currentActions.get(Constants.START_SCRIPT));
		}
		if (currentActions.get(Constants.BROADCAST_SCRIPT) != null) {
			actions.addAll(currentActions.get(Constants.BROADCAST_SCRIPT));
		}
		if (currentActions.get(Constants.BROADCAST_NOTIFY_ACTION) != null) {
			actions.addAll(currentActions.get(Constants.BROADCAST_NOTIFY_ACTION));
		}
		for (String action : actions) {
			for (String actionOfLook : actions) {
				if (action.equals(actionOfLook)
						|| isFirstSequenceActionAndEqualsSecond(action, actionOfLook)
						|| isFirstSequenceActionAndEqualsSecond(actionOfLook, action)) {
					if (!actionsToRestartMap.containsKey(action)) {
						actionsToRestartMap.put(action, actionOfLook);
					} else {
						actionsToRestartMap.get(action).add(actionOfLook);
					}
				}
			}
		}
	}

	private static boolean isFirstSequenceActionAndEqualsSecond(String action1, String action2) {
		String spriteOfAction1 = action1.substring(action1.indexOf(Constants.ACTION_SPRITE_SEPARATOR));
		String spriteOfAction2 = action2.substring(action2.indexOf(Constants.ACTION_SPRITE_SEPARATOR));

		if (!spriteOfAction1.equals(spriteOfAction2)) {
			return false;
		}

		if (!action1.startsWith(SEQUENCE) || !action1.contains(BROADCAST_NOTIFY)) {
			return false;
		}

		int startIndex1 = action1.indexOf(Constants.OPENING_BRACE) + 1;
		int endIndex1 = action1.indexOf(BROADCAST_NOTIFY);
		String innerAction1 = action1.substring(startIndex1, endIndex1);

		String action2Sub = action2.substring(0, action2.indexOf(Constants.ACTION_SPRITE_SEPARATOR));
		if (innerAction1.equals(action2Sub)) {
			return true;
		}
		return false;
	}

	private void drawAxes() {
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(axes, -virtualWidthHalf, -AXIS_WIDTH / 2, virtualWidth, AXIS_WIDTH);
		batch.draw(axes, -AXIS_WIDTH / 2, -virtualHeightHalf, AXIS_WIDTH, virtualHeight);

		GlyphLayout layout = new GlyphLayout();
		layout.setText(font, String.valueOf((int) virtualHeightHalf));
		font.draw(batch, "-" + (int) virtualWidthHalf, -virtualWidthHalf + 3, -layout.height / 2);
		font.draw(batch, String.valueOf((int) virtualWidthHalf), virtualWidthHalf - layout.width, -layout.height / 2);

		font.draw(batch, "-" + (int) virtualHeightHalf, layout.height / 2, -virtualHeightHalf + layout.height + 3);
		font.draw(batch, String.valueOf((int) virtualHeightHalf), layout.height / 2, virtualHeightHalf - 3);
		font.draw(batch, "0", layout.height / 2, -layout.height / 2);
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
		disposeStageButKeepActors();
		font.dispose();
		axes.dispose();
		disposeTextures();
	}

	public boolean makeManualScreenshot() {
		makeScreenshot = true;
		while (makeScreenshot) {
			Thread.yield();
		}
		return saveScreenshot(this.screenshot, SCREENSHOT_MANUAL_FILE_NAME);
	}

	private boolean saveScreenshot(byte[] screenshot, String fileName) {
		int length = screenshot.length;
		Bitmap fullScreenBitmap;
		Bitmap centerSquareBitmap;
		int[] colors = new int[length / 4];

		if (colors.length != screenshotWidth * screenshotHeight || colors.length == 0) {
			return false;
		}

		for (int i = 0; i < length; i += 4) {
			colors[i / 4] = android.graphics.Color.argb(255, screenshot[i + 0] & 0xFF, screenshot[i + 1] & 0xFF,
					screenshot[i + 2] & 0xFF);
		}
		fullScreenBitmap = Bitmap.createBitmap(colors, 0, screenshotWidth, screenshotWidth, screenshotHeight,
				Config.ARGB_8888);

		if (screenshotWidth < screenshotHeight) {
			int verticalMargin = (screenshotHeight - screenshotWidth) / 2;
			centerSquareBitmap = Bitmap.createBitmap(fullScreenBitmap, 0, verticalMargin, screenshotWidth,
					screenshotWidth);
		} else if (screenshotWidth > screenshotHeight) {
			int horizontalMargin = (screenshotWidth - screenshotHeight) / 2;
			centerSquareBitmap = Bitmap.createBitmap(fullScreenBitmap, horizontalMargin, 0, screenshotHeight,
					screenshotHeight);
		} else {
			centerSquareBitmap = Bitmap.createBitmap(fullScreenBitmap, 0, 0, screenshotWidth, screenshotHeight);
		}

		FileHandle image = Gdx.files.absolute(pathForScreenshot + fileName);
		OutputStream stream = image.write(false);
		try {
			new File(pathForScreenshot + Constants.NO_MEDIA_FILE).createNewFile();
			centerSquareBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
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
		byte[] copyOfTestPixels = new byte[testPixels.length];
		System.arraycopy(testPixels, 0, copyOfTestPixels, 0, testPixels.length);
		return copyOfTestPixels;
	}

	public void toggleScreenMode() {
		switch (project.getScreenMode()) {
			case MAXIMIZE:
				project.setScreenMode(ScreenModes.STRETCH);
				break;
			case STRETCH:
				project.setScreenMode(ScreenModes.MAXIMIZE);
				break;
		}

		initScreenMode();

		if (checkIfAutomaticScreenshotShouldBeTaken) {
			makeAutomaticScreenshot = project.manualScreenshotExists(SCREENSHOT_MANUAL_FILE_NAME);
		}
	}

	private void initScreenMode() {
		switch (project.getScreenMode()) {
			case STRETCH:
				screenshotWidth = ScreenValues.SCREEN_WIDTH;
				screenshotHeight = ScreenValues.SCREEN_HEIGHT;
				screenshotX = 0;
				screenshotY = 0;
				viewPort = new ScalingViewport(Scaling.stretch, virtualWidth, virtualHeight, camera);
				break;

			case MAXIMIZE:
				screenshotWidth = maximizeViewPortWidth;
				screenshotHeight = maximizeViewPortHeight;
				screenshotX = maximizeViewPortX;
				screenshotY = maximizeViewPortY;
				viewPort = new ExtendViewport(virtualWidth, virtualHeight, camera);
				break;

			default:
				break;
		}
		viewPort.update(ScreenValues.SCREEN_WIDTH, ScreenValues.SCREEN_HEIGHT, false);
		camera.position.set(0, 0, 0);
		camera.update();
	}

	private LookData createWhiteBackgroundLookData() {
		LookData whiteBackground = new LookData();
		Pixmap whiteBackgroundPixmap = new Pixmap((int) virtualWidth, (int) virtualHeight, Format.RGBA8888);
		whiteBackgroundPixmap.setColor(Color.WHITE);
		whiteBackgroundPixmap.fill();
		whiteBackground.setPixmap(whiteBackgroundPixmap);
		whiteBackground.setTextureRegion();
		return whiteBackground;
	}

	private void disposeTextures() {
		List<Sprite> sprites = project.getSpriteList();
		int spriteSize = sprites.size();
		for (int i = 0; i > spriteSize; i++) {
			List<LookData> data = sprites.get(i).getLookDataList();
			int dataSize = data.size();
			for (int j = 0; j < dataSize; j++) {
				LookData lookData = data.get(j);
				lookData.getPixmap().dispose();
				lookData.getTextureRegion().getTexture().dispose();
			}
		}
	}

	private void disposeStageButKeepActors() {
		stage.unfocusAll();
		batch.dispose();
	}

	public void addActor(ShowTextActor actor) {
		stage.addActor(actor);
	}

	public Stage getStage() {
		return stage;
	}
}
