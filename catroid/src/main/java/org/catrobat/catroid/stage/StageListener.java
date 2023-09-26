/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
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
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenModes;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.common.ThreadScheduler;
import org.catrobat.catroid.content.EventWrapper;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.SoundBackup;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.content.eventids.GamepadEventId;
import org.catrobat.catroid.embroidery.DSTPatternManager;
import org.catrobat.catroid.embroidery.EmbroideryPatternManager;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.formulaeditor.UserDataWrapper;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.physics.PhysicsDebugSettings;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.shapebuilder.PhysicsShapeBuilder;
import org.catrobat.catroid.pocketmusic.mididriver.MidiSoundManager;
import org.catrobat.catroid.ui.dialogs.StageDialog;
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController;
import org.catrobat.catroid.utils.TouchUtil;
import org.catrobat.catroid.utils.VibrationManager;
import org.catrobat.catroid.web.WebConnectionHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import kotlinx.coroutines.GlobalScope;

import static org.catrobat.catroid.common.ScreenValues.SCREEN_HEIGHT;
import static org.catrobat.catroid.common.ScreenValues.SCREEN_WIDTH;
import static org.koin.java.KoinJavaComponent.get;

public class StageListener implements ApplicationListener {

	private static final int AXIS_WIDTH = 4;
	private static final float DELTA_ACTIONS_DIVIDER_MAXIMUM = 50f;
	private static final int ACTIONS_COMPUTATION_TIME_MAXIMUM = 8;
	private static final float AXIS_FONT_SIZE_SCALE_FACTOR = 0.025f;

	private float deltaActionTimeDivisor = 10f;

	private Stage stage = null;
	private boolean paused = true;
	private boolean finished = false;
	private boolean reloadProject = false;
	public boolean firstFrameDrawn = false;

	private boolean makeScreenshot = false;
	private int screenshotWidth;
	private int screenshotHeight;
	private int screenshotX;
	private int screenshotY;

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
	public EmbroideryPatternManager embroideryPatternManager;
	public WebConnectionHolder webConnectionHolder;

	private List<Sprite> sprites;
	public CameraPositioner cameraPositioner;

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

	int maxViewPortX = 0;
	int maxViewPortY = 0;
	int maxViewPortHeight = 0;
	int maxViewPortWidth = 0;

	public boolean axesOn = false;
	private static final Color AXIS_COLOR = new Color(0xff000cff);

	private static final int Z_LAYER_PEN_ACTOR = 1;
	private static final int Z_LAYER_EMBROIDERY_ACTOR = 2;

	private Map<String, StageBackup> stageBackupMap = new HashMap<>();

	private InputListener inputListener = null;

	private Map<Sprite, ShowBubbleActor> bubbleActorMap = new HashMap<>();
	private String screenshotName;
	private ScreenshotSaverCallback screenshotSaverCallback = null;
	private ScreenshotSaver screenshotSaver;

	public StageListener() {
		webConnectionHolder = new WebConnectionHolder();
	}

	@Override
	public void create() {
		deltaActionTimeDivisor = 10f;

		shapeRenderer = new ShapeRenderer();

		project = ProjectManager.getInstance().getCurrentProject();
		scene = ProjectManager.getInstance().getCurrentlyPlayingScene();

		if (stage == null) {
			createNewStage();
			Gdx.input.setInputProcessor(stage);
		} else {
			stage.getRoot().clear();
		}
		initScreenMode();
		initStageInputListener();
		screenshotSaver = new ScreenshotSaver(Gdx.files, getScreenshotPath(), screenshotWidth,
				screenshotHeight);

		font = getLabelFont(project);

		physicsWorld = scene.resetPhysicsWorld();
		sprites = new ArrayList<>(scene.getSpriteList());

		resetConditionScriptTriggers();

		embroideryPatternManager = new DSTPatternManager();
		initActors(sprites);

		passepartout = new Passepartout(SCREEN_WIDTH, SCREEN_HEIGHT, maxViewPortWidth, maxViewPortHeight, virtualWidth, virtualHeight);
		stage.addActor(passepartout);

		axes = new Texture(Gdx.files.internal("stage/red_pixel.bmp"));
	}

	private void resetConditionScriptTriggers() {
		for (Sprite sprite : sprites) {
			sprite.resetConditionScriptTriggers();
		}
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	private BitmapFont getLabelFont(Project project) {
		BitmapFont font = new BitmapFont();
		font.setColor(AXIS_COLOR);
		font.getData().setScale(
				getFontScaleFactor(project, font, new GlyphLayout()));
		return font;
	}

	@VisibleForTesting
	public float getFontScaleFactor(Project project, BitmapFont font, GlyphLayout tempAxisLabelLayout) {
		tempAxisLabelLayout.setText(font, String.valueOf(project.getXmlHeader().virtualScreenWidth / 2));

		float shortDisplaySide;
		if (project.getXmlHeader().islandscapeMode()) {
			shortDisplaySide = project.getXmlHeader().virtualScreenHeight;
		} else {
			shortDisplaySide = project.getXmlHeader().virtualScreenWidth;
		}

		return AXIS_FONT_SIZE_SCALE_FACTOR * shortDisplaySide / tempAxisLabelLayout.height;
	}

	private void createNewStage() {
		virtualWidth = project.getXmlHeader().virtualScreenWidth;
		virtualHeight = project.getXmlHeader().virtualScreenHeight;

		virtualWidthHalf = virtualWidth / 2;
		virtualHeightHalf = virtualHeight / 2;

		camera = new OrthographicCamera();
		cameraPositioner = new CameraPositioner(camera, virtualHeightHalf, virtualWidthHalf);
		viewPort = new ExtendViewport(virtualWidth, virtualHeight, camera);
		if (batch == null) {
			batch = new SpriteBatch();
		} else {
			batch = new SpriteBatch(1000, batch.getShader());
		}

		stage = new Stage(viewPort, batch);
		SensorHandler.timerReferenceValue = SystemClock.uptimeMillis();
	}

	private void initActors(List<Sprite> sprites) {
		if (sprites.isEmpty()) {
			return;
		}

		for (Sprite sprite : sprites) {
			sprite.resetSprite();
			sprite.look.createBrightnessContrastHueShader();
			stage.addActor(sprite.look);
		}

		penActor = new PenActor();
		stage.addActor(penActor);
		penActor.setZIndex(Z_LAYER_PEN_ACTOR);

		float screenRatio = calculateScreenRatio();
		EmbroideryActor embroideryActor = new EmbroideryActor(screenRatio, embroideryPatternManager, shapeRenderer);
		stage.addActor(embroideryActor);
		embroideryActor.setZIndex(Z_LAYER_EMBROIDERY_ACTOR);
	}

	public void cloneSpriteAndAddToStage(Sprite cloneMe) {
		Sprite copy = new SpriteController().copyForCloneBrick(cloneMe);
		if (cloneMe.isClone) {
			copy.myOriginal = cloneMe.myOriginal;
		} else {
			copy.myOriginal = cloneMe;
		}
		copy.look.createBrightnessContrastHueShader();
		addCloneActorToStage(stage, stage.getRoot(), cloneMe.look, copy.look);
		sprites.add(copy);
		if (!copy.getLookList().isEmpty()) {
			int currentLookDataIndex = cloneMe.getLookList().indexOf(cloneMe.look.getLookData());
			copy.look.setLookData(copy.getLookList().get(currentLookDataIndex));
		}
		copy.initializeEventThreads(EventId.START_AS_CLONE);
		copy.initConditionScriptTriggers();
	}

	public void addCloneActorToStage(Stage stage, Group rootGroup, Look cloneMeLook, Look copyLook) {
		if (!stage.getActors().contains(cloneMeLook, true)) {
			rootGroup.addActor(cloneMeLook);
		}
		rootGroup.addActorBefore(cloneMeLook, copyLook);
	}

	public boolean removeClonedSpriteFromStage(Sprite sprite) {
		if (!sprite.isClone) {
			return false;
		}
		boolean removedSprite = sprites.remove(sprite);
		if (removedSprite) {
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

	public List<Sprite> getAllClonesOfSprite(Sprite sprite) {
		List<Sprite> clonesOfSprite = new ArrayList<>();
		for (Sprite spriteOfStage : sprites) {
			if (spriteIsCloneOfSprite(sprite, spriteOfStage)) {
				clonesOfSprite.add(spriteOfStage);
			}
		}
		return clonesOfSprite;
	}

	private Boolean spriteIsCloneOfSprite(Sprite sprite, Sprite cloneSprite) {
		if (!cloneSprite.isClone) {
			return false;
		}
		String cloneNameExtensionRegexPattern = "\\-c\\d+$";
		String[] splitCloneNameStrings = cloneSprite.getName().split(cloneNameExtensionRegexPattern);
		return splitCloneNameStrings[0].contentEquals(sprite.getName());
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
	}

	void menuPause() {
		if (finished || reloadProject) {
			return;
		}

		paused = true;
		webConnectionHolder.onPause();
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

		CameraManager cameraManager = StageActivity.getActiveCameraManager();
		if (cameraManager != null) {
			cameraManager.resume();
		}

		SoundManager.getInstance().clear();
		get(SpeechRecognitionHolderFactory.class).getInstance().destroy();

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
		embroideryPatternManager.clear();

		CameraManager cameraManager = StageActivity.getActiveCameraManager();
		if (cameraManager != null) {
			cameraManager.reset();
		}
		VibrationManager vibrationManager = StageActivity.getActiveVibrationManager();
		if (vibrationManager != null) {
			vibrationManager.reset();
		}
		TouchUtil.reset();
		MidiSoundManager.getInstance().reset();
		removeAllClonedSpritesFromStage();

		UserDataWrapper.resetAllUserData(ProjectManager.getInstance().getCurrentProject());

		for (Scene scene : ProjectManager.getInstance().getCurrentProject().getSceneList()) {
			scene.firstStart = true;
		}
		reloadProject = true;
	}

	@Override
	public void resume() {
		if (!paused) {
			setSchedulerStateForAllLooks(ThreadScheduler.RUNNING);
			SoundManager.getInstance().resume();
		}

		for (Sprite sprite : sprites) {
			sprite.look.refreshTextures(true);
		}
	}

	@Override
	public void pause() {
		if (finished) {
			return;
		}
		if (!paused) {
			setSchedulerStateForAllLooks(ThreadScheduler.SUSPENDED);
			SoundManager.getInstance().pause();
		}
	}

	@Override
	public void render() {
		CameraManager cameraManager = StageActivity.getActiveCameraManager();
		float color = cameraManager != null && cameraManager.getPreviewVisible() ? 0f : 1f;
		Gdx.gl20.glClearColor(color, color, color, 0f);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (reloadProject) {
			stage.clear();
			if (penActor != null) {
				penActor.dispose();
			}

			embroideryPatternManager.clear();

			SoundManager.getInstance().clear();

			physicsWorld = scene.resetPhysicsWorld();

			initActors(sprites);
			stage.addActor(passepartout);

			initStageInputListener();

			paused = true;
			scene.firstStart = true;
			reloadProject = false;

			cameraPositioner.reset();

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
				sprite.initIfConditionBrickTriggers();
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

		if (makeScreenshot) {
			byte[] screenshot = ScreenUtils
					.getFrameBufferPixels(screenshotX, screenshotY, screenshotWidth, screenshotHeight, true);
			makeScreenshot = false;
			screenshotSaver.saveScreenshotAndNotify(
					screenshot,
					screenshotName,
					this::notifyScreenshotCallbackAndCleanup,
					GlobalScope.INSTANCE
			);
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

		cameraPositioner.updateCameraPositionForFocusedSprite();
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
		GlyphLayout layout = new GlyphLayout();
		layout.setText(font, String.valueOf((int) virtualWidthHalf));

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(axes, -virtualWidthHalf, -AXIS_WIDTH / 2, virtualWidth, AXIS_WIDTH);
		batch.draw(axes, -AXIS_WIDTH / 2, -virtualHeightHalf, AXIS_WIDTH, virtualHeight);

		final float fontOffset = layout.height / 2;

		font.draw(batch, "-" + (int) virtualWidthHalf, -virtualWidthHalf + fontOffset, -fontOffset);
		font.draw(batch, String.valueOf((int) virtualWidthHalf), virtualWidthHalf - layout.width - fontOffset,
				-fontOffset);

		font.draw(batch, "-" + (int) virtualHeightHalf, fontOffset, -virtualHeightHalf + layout.height + fontOffset);
		font.draw(batch, String.valueOf((int) virtualHeightHalf), fontOffset, virtualHeightHalf - fontOffset);

		font.draw(batch, "0", fontOffset, -fontOffset);
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

		SoundManager.getInstance().clear();
		PhysicsShapeBuilder.getInstance().reset();
		embroideryPatternManager = null;
		if (penActor != null) {
			penActor.dispose();
		}
	}

	public void finish() {
		finished = true;
	}

	public void requestTakingScreenshot(@NonNull String screenshotName,
			@NonNull ScreenshotSaverCallback screenshotCallback) {
		this.screenshotName = screenshotName;
		this.screenshotSaverCallback = screenshotCallback;
		makeScreenshot = true;
	}

	private void notifyScreenshotCallbackAndCleanup(Boolean success) {
		if (screenshotSaverCallback != null) {
			screenshotSaverCallback.screenshotSaved(success);
			this.screenshotSaverCallback = null;
		} else {
			Log.e("StageListener", "Lost reference to screenshot callback");
		}
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
	}

	public void clearBackground() {
		penActor.reset();
	}

	private void initScreenMode() {
		screenshotWidth = ScreenValues.getScreenWidthForProject(project);
		screenshotHeight = ScreenValues.getScreenHeightForProject(project);

		switch (project.getScreenMode()) {
			case STRETCH:
				screenshotX = 0;
				screenshotY = 0;
				viewPort = new ScalingViewport(Scaling.stretch, virtualWidth, virtualHeight, camera);
				shapeRenderer.identity();
				break;
			case MAXIMIZE:
				float yScale = 1.0f;
				float xScale = 1.0f;
				if (screenshotWidth != maxViewPortWidth && maxViewPortWidth > 0) {
					xScale = screenshotWidth / (float) maxViewPortWidth;
				}
				if (screenshotHeight != maxViewPortHeight && maxViewPortHeight > 0) {
					yScale = screenshotHeight / (float) maxViewPortHeight;
				}

				screenshotWidth = maxViewPortWidth;
				screenshotHeight = maxViewPortHeight;
				screenshotX = maxViewPortX;
				screenshotY = maxViewPortY;

				viewPort = new ExtendViewport(virtualWidth, virtualHeight, camera);
				shapeRenderer.scale(xScale, yScale, 1.0f);
				break;
			default:
				break;
		}
		viewPort.update(SCREEN_WIDTH, SCREEN_HEIGHT, false);
		camera.position.set(0, 0, 0);
		camera.update();
		shapeRenderer.updateMatrices();
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
		EventWrapper gamepadEvent = new EventWrapper(eventId, false);
		project.fireToAllSprites(gamepadEvent);
	}

	public void addActor(Actor actor) {
		stage.addActor(actor);
	}

	public Stage getStage() {
		return stage;
	}

	private void setSchedulerStateForAllLooks(@ThreadScheduler.SchedulerState int state) {
		for (Actor actor : stage.getActors()) {
			if (actor instanceof Look) {
				Look look = (Look) actor;
				look.setSchedulerState(state);
			}
		}
	}

	public void setBubbleActorForSprite(Sprite sprite, ShowBubbleActor showBubbleActor) {
		addActor(showBubbleActor);
		bubbleActorMap.put(sprite, showBubbleActor);
	}

	public void removeBubbleActorForSprite(Sprite sprite) {
		getBubbleActorForSprite(sprite).close();
		getStage().getActors().removeValue(getBubbleActorForSprite(sprite), true);
		bubbleActorMap.remove(sprite);
	}

	public ShowBubbleActor getBubbleActorForSprite(Sprite sprite) {
		return bubbleActorMap.get(sprite);
	}

	public List<Sprite> getSpritesFromStage() {
		return sprites;
	}

	@VisibleForTesting
	public static class StageBackup {

		List<Sprite> sprites;
		Array<Actor> actors;
		PenActor penActor;
		EmbroideryPatternManager embroideryPatternManager;
		Map<Sprite, ShowBubbleActor> bubbleActorMap;
		List<SoundBackup> soundBackupList;

		boolean paused;
		boolean finished;
		boolean reloadProject;
		boolean flashState;
		long timeToVibrate;

		PhysicsWorld physicsWorld;
		OrthographicCamera camera;
		Sprite spriteToFocusOn;
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
		CameraManager cameraManager = StageActivity.getActiveCameraManager();
		VibrationManager vibrationManager = StageActivity.getActiveVibrationManager();

		backup.sprites = new ArrayList<>(sprites);
		backup.actors = new Array<>(stage.getActors());
		backup.penActor = penActor;
		backup.bubbleActorMap = new HashMap<>(bubbleActorMap);
		backup.embroideryPatternManager = embroideryPatternManager;

		backup.paused = paused;
		backup.finished = finished;
		backup.reloadProject = reloadProject;
		backup.flashState = cameraManager != null && cameraManager.getFlashOn();
		if (backup.flashState) {
			cameraManager.disableFlash();
		}
		if (vibrationManager != null && vibrationManager.hasActiveVibration()) {
			vibrationManager.pause();
			backup.timeToVibrate = vibrationManager.getTimeToVibrate();
			vibrationManager.reset();
		}
		backup.physicsWorld = physicsWorld;
		backup.camera = camera;
		backup.spriteToFocusOn = cameraPositioner.getSpriteToFocusOn();
		cameraPositioner.reset();
		backup.batch = batch;
		backup.font = font;
		backup.passepartout = passepartout;
		backup.viewPort = viewPort;

		backup.axesOn = axesOn;
		backup.deltaActionTimeDivisor = deltaActionTimeDivisor;
		backup.cameraRunning = cameraManager != null && cameraManager.isCameraActive();
		if (backup.cameraRunning) {
			cameraManager.pause();
		}
		backup.soundBackupList = new ArrayList<>();
		backup.soundBackupList.addAll(SoundManager.getInstance().getPlayingSoundBackups());
		return backup;
	}

	private void restoreFromBackup(StageBackup backup) {
		sprites.clear();
		sprites.addAll(backup.sprites);
		CameraManager cameraManager = StageActivity.getActiveCameraManager();
		VibrationManager vibrationManager = StageActivity.getActiveVibrationManager();

		stage.clear();
		for (Actor actor : backup.actors) {
			stage.addActor(actor);
		}

		penActor = backup.penActor;

		bubbleActorMap.clear();
		bubbleActorMap.putAll(backup.bubbleActorMap);

		embroideryPatternManager = backup.embroideryPatternManager;

		paused = backup.paused;
		finished = backup.finished;
		reloadProject = backup.reloadProject;
		if (backup.flashState && cameraManager != null) {
			cameraManager.enableFlash();
		}
		if (backup.timeToVibrate > 0 && vibrationManager != null) {
			vibrationManager.setTimeToVibrate(backup.timeToVibrate);
			vibrationManager.resume();
		} else if (vibrationManager != null) {
			vibrationManager.pause();
		}
		physicsWorld = backup.physicsWorld;
		camera = backup.camera;
		cameraPositioner.setSpriteToFocusOn(backup.spriteToFocusOn);
		cameraPositioner.updateCameraPositionForFocusedSprite();
		batch = backup.batch;
		font = backup.font;
		passepartout = backup.passepartout;
		viewPort = backup.viewPort;
		axesOn = backup.axesOn;
		deltaActionTimeDivisor = backup.deltaActionTimeDivisor;
		if (backup.cameraRunning && cameraManager != null) {
			cameraManager.resume();
		}
		for (SoundBackup soundBackup : backup.soundBackupList) {
			SoundManager.getInstance().playSoundFileWithStartTime(soundBackup.getPathToSoundFile(),
					soundBackup.getStartedBySprite(), soundBackup.getCurrentPosition());
		}
		initStageInputListener();
	}

	private float calculateScreenRatio() {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		XmlHeader header = ProjectManager.getInstance().getCurrentProject().getXmlHeader();
		float deviceDiagonalPixel = (float) Math.sqrt(Math.pow(metrics.widthPixels, 2) + Math.pow(metrics.heightPixels, 2));
		float creatorDiagonalPixel = (float) Math.sqrt(Math.pow(header.getVirtualScreenWidth(), 2)
				+ Math.pow(header.getVirtualScreenHeight(), 2));
		return creatorDiagonalPixel / deviceDiagonalPixel;
	}

	@VisibleForTesting
	public String getScreenshotPath() {
		return scene.getDirectory().getAbsolutePath() + "/";
	}
}
