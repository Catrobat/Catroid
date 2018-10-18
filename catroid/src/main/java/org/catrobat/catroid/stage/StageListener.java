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

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.SystemClock;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenModes;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.EventWrapper;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.content.eventids.GamepadEventId;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.physics.PhysicsDebugSettings;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.shapebuilder.PhysicsShapeBuilder;
import org.catrobat.catroid.ui.dialogs.StageDialog;
import org.catrobat.catroid.utils.FlashUtil;
import org.catrobat.catroid.utils.PathBuilder;
import org.catrobat.catroid.utils.TouchUtil;
import org.catrobat.catroid.utils.VibratorUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.catrobat.catroid.common.Constants.DEFAULT_IMAGE_EXTENSION;
import static org.catrobat.catroid.common.ScreenValues.SCREEN_HEIGHT;
import static org.catrobat.catroid.common.ScreenValues.SCREEN_WIDTH;

public class StageListener implements ApplicationListener {

	private static final int AXIS_WIDTH = 4;
	private static final float DELTA_ACTIONS_DIVIDER_MAXIMUM = 50f;
	private static final int ACTIONS_COMPUTATION_TIME_MAXIMUM = 8;

	private float deltaActionTimeDivisor = 10f;
	public static final String SCREENSHOT_AUTOMATIC_FILE_NAME = "automatic_screenshot" + DEFAULT_IMAGE_EXTENSION;
	public static final String SCREENSHOT_MANUAL_FILE_NAME = "manual_screenshot" + DEFAULT_IMAGE_EXTENSION;

	private Stage stage = null;
	private boolean paused = false;
	private boolean finished = false;
	private boolean reloadProject = false;
	public boolean firstFrameDrawn = false;

	private static boolean checkIfAutomaticScreenshotShouldBeTaken = true;
	private boolean makeAutomaticScreenshot = false;
	private boolean makeScreenshot = false;
	private String pathForSceneScreenshot;
	private int screenshotWidth;
	private int screenshotHeight;
	private int screenshotX;
	private int screenshotY;
	private byte[] screenshot = null;
	// in first frame, framebuffer could be empty and screenshot
	// would be white
	private boolean skipFirstFrameForAutomaticScreenshot;

	private Project project;
	private Scene scene;

	private PhysicsWorld physicsWorld;

	private OrthographicCamera camera;
	private Batch batch = null;
	private BitmapFont font;
	private Passepartout passepartout;
	private Viewport viewPort;
	public ShapeRenderer shapeRenderer;
	private PenActor penActor;

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
	private Map<String, StageBackup> stageBackupMap = new HashMap<>();

	private InputListener inputListener = null;

	private Map<Sprite, ShowBubbleActor> bubbleActorMap = new HashMap<>();

	StageListener() {
	}

	@Override
	public void create() {
		font = new BitmapFont();
		font.setColor(1f, 0f, 0.05f, 1f);
		font.getData().setScale(1.2f);
		deltaActionTimeDivisor = 10f;

		shapeRenderer = new ShapeRenderer();

		project = ProjectManager.getInstance().getCurrentProject();
		scene = ProjectManager.getInstance().getCurrentlyPlayingScene();
		pathForSceneScreenshot = PathBuilder.buildScenePath(project.getName(), scene.getName()) + "/";

		if (stage == null) {
			createNewStage();
			Gdx.input.setInputProcessor(stage);
		} else {
			stage.getRoot().clear();
		}
		initScreenMode();
		initStageInputListener();

		physicsWorld = scene.resetPhysicsWorld();
		sprites = new ArrayList<>(scene.getSpriteList());
		initActors(sprites);

		passepartout = new Passepartout(SCREEN_WIDTH, SCREEN_HEIGHT, maximizeViewPortWidth, maximizeViewPortHeight, virtualWidth, virtualHeight);
		stage.addActor(passepartout);

		axes = new Texture(Gdx.files.internal("stage/red_pixel.bmp"));
		skipFirstFrameForAutomaticScreenshot = true;
		if (checkIfAutomaticScreenshotShouldBeTaken) {
			makeAutomaticScreenshot = project.manualScreenshotExists(SCREENSHOT_MANUAL_FILE_NAME)
					|| scene.hasScreenshot();
		}
		FaceDetectionHandler.resumeFaceDetection();
	}

	private void createNewStage() {
		virtualWidth = project.getXmlHeader().virtualScreenWidth;
		virtualHeight = project.getXmlHeader().virtualScreenHeight;

		virtualWidthHalf = virtualWidth / 2;
		virtualHeightHalf = virtualHeight / 2;

		camera = new OrthographicCamera();
		viewPort = new ExtendViewport(virtualWidth, virtualHeight, camera);
		if (batch == null) {
			batch = new SpriteBatch();
		} else {
			batch = new SpriteBatch(1000, batch.getShader());
		}

		stage = new Stage(viewPort, batch);
	}

	private void initActors(List<Sprite> sprites) {
		if (sprites.isEmpty()) {
			return;
		}

		for (Sprite sprite : sprites) {
			sprite.resetSprite();
			sprite.look.createBrightnessContrastHueShader();
			stage.addActor(sprite.look);

			if (sprites.indexOf(sprite) == 0) {
				penActor = new PenActor();
				stage.addActor(penActor);
			}
		}
	}

	public void cloneSpriteAndAddToStage(Sprite cloneMe) {
		Sprite copy = cloneMe.cloneForCloneBrick();
		copy.look.createBrightnessContrastHueShader();
		stage.getRoot().addActorBefore(cloneMe.look, copy.look);
		sprites.add(copy);
		if (!copy.getLookList().isEmpty()) {
			copy.look.setLookData(copy.getLookList().get(0));
		}
		copy.initializeEventThreads(EventId.START_AS_CLONE);
		copy.initConditionScriptTriggers();
	}

	public boolean removeClonedSpriteFromStage(Sprite sprite) {
		if (!sprite.isClone) {
			return false;
		}
		boolean removedSprite = sprites.remove(sprite);
		if (removedSprite) {
			ProjectManager.getInstance().getCurrentlyPlayingScene().getDataContainer()
					.removeSpriteUserData(sprite);

			sprite.look.remove();
			sprite.invalidate();
		}
		return removedSprite;
	}

	private void removeAllClonedSpritesFromStage() {
		List<Sprite> spritesCopy = new ArrayList<>(sprites);
		for (Sprite sprite : spritesCopy) {
			if (sprite.isClone) {
				removeClonedSpriteFromStage(sprite);
			}
		}
		StageActivity.resetNumberOfClonedSprites();
	}

	private void disposeClonedSprites() {
		for (Scene scene : ProjectManager.getInstance().getCurrentProject().getSceneList()) {
			scene.removeClonedSprites();
		}
	}

	private void initStageInputListener() {
		if (inputListener == null) {
			inputListener = new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					TouchUtil.touchDown(event.getStageX(), event.getStageY(), pointer);
					return true;
				}

				@Override
				public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
					TouchUtil.touchUp(pointer);
				}

				@Override
				public void touchDragged(InputEvent event, float x, float y, int pointer) {
					TouchUtil.updatePosition(event.getStageX(), event.getStageY(), pointer);
				}
			};
		}
		stage.addListener(inputListener);
	}

	void menuResume() {
		if (reloadProject) {
			return;
		}
		paused = false;

		FaceDetectionHandler.resumeFaceDetection();
		SoundManager.getInstance().resume();
	}

	void menuPause() {
		if (finished || reloadProject) {
			return;
		}

		paused = true;
		SoundManager.getInstance().pause();
	}

	public void transitionToScene(String sceneName) {
		Scene newScene = ProjectManager.getInstance().getCurrentProject().getSceneByName(sceneName);

		if (newScene == null) {
			return;
		}

		stageBackupMap.put(scene.getName(), saveToBackup());
		pause();

		scene = newScene;
		ProjectManager.getInstance().setCurrentlyPlayingScene(scene);

		if (stageBackupMap.containsKey(scene.getName())) {
			restoreFromBackup(stageBackupMap.get(scene.getName()));
		}

		if (scene.firstStart) {
			create();
		} else {
			resume();
		}
		Gdx.input.setInputProcessor(stage);
	}

	public void startScene(String sceneName) {
		Scene newScene = ProjectManager.getInstance().getCurrentProject().getSceneByName(sceneName);

		if (newScene == null) {
			return;
		}

		stageBackupMap.put(scene.getName(), saveToBackup());
		pause();

		scene = newScene;
		ProjectManager.getInstance().setCurrentlyPlayingScene(scene);

		SoundManager.getInstance().clear();
		stageBackupMap.remove(sceneName);

		Gdx.input.setInputProcessor(stage);

		scene.firstStart = true;
		create();
	}

	public void reloadProject(StageDialog stageDialog) {
		if (reloadProject) {
			return;
		}
		this.stageDialog = stageDialog;
		if (!ProjectManager.getInstance().getStartScene().getName().equals(scene.getName())) {
			transitionToScene(ProjectManager.getInstance().getStartScene().getName());
		}
		stageBackupMap.clear();

		FlashUtil.reset();
		VibratorUtil.reset();
		TouchUtil.reset();
		removeAllClonedSpritesFromStage();

		for (Scene scene : ProjectManager.getInstance().getCurrentProject().getSceneList()) {
			scene.firstStart = true;
			scene.getDataContainer().resetUserData();
		}
		reloadProject = true;
	}

	@Override
	public void resume() {
		if (!paused) {
			FaceDetectionHandler.resumeFaceDetection();
			SoundManager.getInstance().resume();
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
		}
	}

	public void finish() {
		SoundManager.getInstance().clear();
		if (thumbnail != null && !makeAutomaticScreenshot) {
			saveScreenshot(thumbnail, SCREENSHOT_AUTOMATIC_FILE_NAME);
		}
		PhysicsShapeBuilder.getInstance().reset();
		CameraManager.getInstance().setToDefaultCamera();
		if (penActor != null) {
			penActor.dispose();
		}
		finished = true;
	}

	@Override
	public void render() {
		if (CameraManager.getInstance().getState() == CameraManager.CameraState.previewRunning) {
			Gdx.gl20.glClearColor(0f, 0f, 0f, 0f);
		} else {
			Gdx.gl20.glClearColor(1f, 1f, 1f, 0f);
		}
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (reloadProject) {
			stage.clear();
			if (penActor != null) {
				penActor.dispose();
			}
			SoundManager.getInstance().clear();

			physicsWorld = scene.resetPhysicsWorld();

			initActors(sprites);
			stage.addActor(passepartout);

			initStageInputListener();

			paused = true;
			scene.firstStart = true;
			reloadProject = false;

			if (stageDialog != null) {
				synchronized (stageDialog) {
					stageDialog.notify();
				}
			}
		}

		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);

		if (scene.firstStart) {
			for (Sprite sprite : sprites) {
				sprite.initializeEventThreads(EventId.START);
				sprite.initConditionScriptTriggers();
				if (!sprite.getLookList().isEmpty()) {
					sprite.look.setLookData(sprite.getLookList().get(0));
				}
			}
			scene.firstStart = false;
		}

		if (!paused) {
			float deltaTime = Gdx.graphics.getDeltaTime();

			float optimizedDeltaTime = deltaTime / deltaActionTimeDivisor;
			long timeBeforeActionsUpdate = SystemClock.uptimeMillis();

			while (deltaTime > 0f) {
				physicsWorld.step(optimizedDeltaTime);
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

		if (!finished) {
			stage.draw();
			firstFrameDrawn = true;
		}

		if (makeAutomaticScreenshot) {
			if (skipFirstFrameForAutomaticScreenshot) {
				skipFirstFrameForAutomaticScreenshot = false;
			} else {
				thumbnail = ScreenUtils
						.getFrameBufferPixels(screenshotX, screenshotY, screenshotWidth, screenshotHeight, true);
				makeAutomaticScreenshot = false;
			}
		}

		if (makeScreenshot) {
			screenshot = ScreenUtils
					.getFrameBufferPixels(screenshotX, screenshotY, screenshotWidth, screenshotHeight, true);
			makeScreenshot = false;
		}

		if (axesOn && !finished) {
			drawAxes();
		}

		if (PhysicsDebugSettings.Render.RENDER_PHYSIC_OBJECT_LABELING) {
			printPhysicsLabelOnScreen();
		}

		if (PhysicsDebugSettings.Render.RENDER_COLLISION_FRAMES && !finished) {
			physicsWorld.render(camera.combined);
		}

		if (makeTestPixels) {
			testPixels = ScreenUtils.getFrameBufferPixels(testX, testY, testWidth, testHeight, false);
			makeTestPixels = false;
		}
	}

	private void printPhysicsLabelOnScreen() {
		PhysicsObject tempPhysicsObject;
		final int fontOffset = 5;
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		for (Sprite sprite : sprites) {
			if (sprite.look instanceof PhysicsLook) {
				tempPhysicsObject = physicsWorld.getPhysicsObject(sprite);
				font.draw(batch, "velocity_x: " + tempPhysicsObject.getVelocity().x, tempPhysicsObject.getX(),
						tempPhysicsObject.getY());
				font.draw(batch, "velocity_y: " + tempPhysicsObject.getVelocity().y, tempPhysicsObject.getX(),
						tempPhysicsObject.getY() + font.getXHeight() + fontOffset);
				font.draw(batch, "angular velocity: " + tempPhysicsObject.getRotationSpeed(), tempPhysicsObject.getX(),
						tempPhysicsObject.getY() + font.getXHeight() * 2 + fontOffset * 2);
				font.draw(batch, "direction: " + tempPhysicsObject.getDirection(), tempPhysicsObject.getX(),
						tempPhysicsObject.getY() + font.getXHeight() * 3 + fontOffset * 3);
			}
		}
		batch.end();
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

	public PenActor getPenActor() {
		return penActor;
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
		disposeClonedSprites();
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
			colors[i / 4] = android.graphics.Color.argb(255, screenshot[i] & 0xFF, screenshot[i + 1] & 0xFF,
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

		FileHandle imageScene = Gdx.files.absolute(pathForSceneScreenshot + fileName);
		OutputStream streamScene = imageScene.write(false);
		try {
			new File(pathForSceneScreenshot + Constants.NO_MEDIA_FILE).createNewFile();
			centerSquareBitmap.compress(Bitmap.CompressFormat.PNG, 100, streamScene);
			streamScene.close();
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

	public void clearBackground() {
		penActor.reset();
	}

	private void initScreenMode() {
		switch (project.getScreenMode()) {
			case STRETCH:
				screenshotWidth = ScreenValues.getScreenWidthForProject(project);
				screenshotHeight = ScreenValues.getScreenHeightForProject(project);
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
		viewPort.update(SCREEN_WIDTH, SCREEN_HEIGHT, false);
		camera.position.set(0, 0, 0);
		camera.update();
	}

	private void disposeTextures() {
		for (Scene scene : project.getSceneList()) {
			for (Sprite sprite : scene.getSpriteList()) {
				for (LookData lookData : sprite.getLookList()) {
					lookData.dispose();
				}
			}
		}
	}

	private void disposeStageButKeepActors() {
		stage.unfocusAll();
		batch.dispose();
	}

	public void gamepadPressed(String buttonType) {
		EventId eventId = new GamepadEventId(buttonType);
		EventWrapper gamepadEvent = new EventWrapper(eventId, EventWrapper.NO_WAIT);
		project.fireToAllSprites(gamepadEvent);
	}

	public void addActor(Actor actor) {
		stage.addActor(actor);
	}

	public Stage getStage() {
		return stage;
	}

	public void removeActor(Look look) {
		look.remove();
	}

	public void setBubbleActorForSprite(Sprite sprite, ShowBubbleActor showBubbleActor) {
		addActor(showBubbleActor);
		bubbleActorMap.put(sprite, showBubbleActor);
	}

	public void removeBubbleActorForSprite(Sprite sprite) {
		getStage().getActors().removeValue(getBubbleActorForSprite(sprite), true);
		bubbleActorMap.remove(sprite);
	}

	public ShowBubbleActor getBubbleActorForSprite(Sprite sprite) {
		return bubbleActorMap.get(sprite);
	}

	public List<Sprite> getSpritesFromStage() {
		return sprites;
	}

	private class StageBackup {

		List<Sprite> sprites;
		Array<Actor> actors;
		PenActor penActor;
		Map<Sprite, ShowBubbleActor> bubbleActorMap;

		boolean paused;
		boolean finished;
		boolean reloadProject;
		boolean flashState;
		long timeToVibrate;

		PhysicsWorld physicsWorld;
		OrthographicCamera camera;
		Batch batch;
		BitmapFont font;
		Passepartout passepartout;
		Viewport viewPort;

		boolean axesOn;
		float deltaActionTimeDivisor;
		boolean cameraRunning;
	}

	private StageBackup saveToBackup() {
		StageBackup backup = new StageBackup();

		backup.sprites = new ArrayList<>(sprites);
		backup.actors = new Array<>(stage.getActors());
		backup.penActor = penActor;
		backup.bubbleActorMap = new HashMap<>(bubbleActorMap);

		backup.paused = paused;
		backup.finished = finished;
		backup.reloadProject = reloadProject;
		backup.flashState = FlashUtil.isOn();
		if (backup.flashState) {
			FlashUtil.flashOff();
		}
		backup.timeToVibrate = VibratorUtil.getTimeToVibrate();
		backup.physicsWorld = physicsWorld;
		backup.camera = camera;
		backup.batch = batch;
		backup.font = font;
		backup.passepartout = passepartout;
		backup.viewPort = viewPort;

		backup.axesOn = axesOn;
		backup.deltaActionTimeDivisor = deltaActionTimeDivisor;
		backup.cameraRunning = CameraManager.getInstance().isCameraActive();
		if (backup.cameraRunning) {
			CameraManager.getInstance().pauseForScene();
		}

		return backup;
	}

	private void restoreFromBackup(StageBackup backup) {
		sprites.clear();
		sprites.addAll(backup.sprites);

		stage.clear();
		for (Actor actor : backup.actors) {
			stage.addActor(actor);
		}

		penActor = backup.penActor;

		bubbleActorMap.clear();
		bubbleActorMap.putAll(backup.bubbleActorMap);

		paused = backup.paused;
		finished = backup.finished;
		reloadProject = backup.reloadProject;
		if (backup.flashState) {
			FlashUtil.flashOn();
		}
		if (backup.timeToVibrate > 0) {
			VibratorUtil.resumeVibrator();
			VibratorUtil.setTimeToVibrate(backup.timeToVibrate);
		} else {
			VibratorUtil.pauseVibrator();
		}
		physicsWorld = backup.physicsWorld;
		camera = backup.camera;
		batch = backup.batch;
		font = backup.font;
		passepartout = backup.passepartout;
		viewPort = backup.viewPort;
		axesOn = backup.axesOn;
		deltaActionTimeDivisor = backup.deltaActionTimeDivisor;
		if (backup.cameraRunning) {
			CameraManager.getInstance().resumeForScene();
		}
	}
}
