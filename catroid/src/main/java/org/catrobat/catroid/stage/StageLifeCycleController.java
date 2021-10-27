/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.cast.CastManager;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.devices.mindstorms.MindstormsException;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.formulaeditor.UserDataWrapper;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.io.StageAudioFocus;
import org.catrobat.catroid.pocketmusic.mididriver.MidiSoundManager;
import org.catrobat.catroid.ui.dialogs.StageDialog;
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask;
import org.catrobat.catroid.utils.TouchUtil;
import org.catrobat.catroid.utils.VibrationUtil;

import java.util.List;

import androidx.core.view.MotionEventCompat;

import static org.catrobat.catroid.stage.StageResourceHolder.getProjectsRuntimePermissionList;
import static org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask.checkPermission;
import static org.koin.java.KoinJavaComponent.get;

public final class StageLifeCycleController {
	public static final String TAG = StageLifeCycleController.class.getSimpleName();

	private static final int REQUEST_PERMISSIONS_STAGE_RESOURCE_CREATE = 601;

	private StageLifeCycleController() {
		throw new AssertionError("no.");
	}

	static void stageCreate(final StageActivity stageActivity) {
		if (ProjectManager.getInstance().getCurrentProject() == null) {
			stageActivity.finish();
			Log.d(TAG, "no current project set, cowardly refusing to run");
			return;
		}

		StageActivity.numberOfSpritesCloned = 0;

		if (ProjectManager.getInstance().isCurrentProjectLandscapeMode()) {
			stageActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			stageActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		UserDataWrapper.resetAllUserData(ProjectManager.getInstance().getCurrentProject());

		for (Scene scene : ProjectManager.getInstance().getCurrentProject().getSceneList()) {
			scene.firstStart = true;
		}

		stageActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		StageActivity.stageListener = new StageListener();
		stageActivity.stageDialog = new StageDialog(stageActivity, StageActivity.stageListener, R.style.StageDialog);
		stageActivity.brickDialogManager = new BrickDialogManager(stageActivity);
		stageActivity.calculateScreenSizes();

		stageActivity.configuration = new AndroidApplicationConfiguration();
		stageActivity.configuration.r = stageActivity.configuration.g = stageActivity.configuration.b = stageActivity.configuration.a = 8;
		if (ProjectManager.getInstance().getCurrentProject().isCastProject()) {
			stageActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			stageActivity.setContentView(R.layout.activity_stage_gamepad);
			CastManager.getInstance().initializeGamepadActivity(stageActivity);
			CastManager.getInstance()
					.addStageViewToLayout((GLSurfaceView20) stageActivity.initializeForView(StageActivity.stageListener, stageActivity.configuration));
		} else {
			stageActivity.initialize(StageActivity.stageListener, stageActivity.configuration);
			View view = stageActivity.getGdxGraphics().getView();
			view.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int action = event.getAction();
					float displayRatio = 2.f;
					float x = (event.getX() - v.getWidth() / displayRatio) / 2.f;
					float y = (-event.getY() + v.getHeight() / displayRatio) / 2.f;
					XmlHeader header = ProjectManager.getInstance().getCurrentProject().getXmlHeader();
					float virtual_x = header.getVirtualScreenWidth();
					float virtual_y = header.getVirtualScreenHeight();

					float offset_x = v.getWidth()/displayRatio - virtual_x;
					float offset_y = v.getHeight()/displayRatio - virtual_y;

					if(x > 0)
						x = x - offset_x / 2.f;
					else
						x = x + offset_x / 2.f;
					if(y > 0)
						y = y - offset_y / 2.f;
					else
						y = y + offset_y / 2.f;

					switch(action) {
						case (MotionEvent.ACTION_DOWN) :
							Log.d("&&&",
									"Stage Height: " + v.getHeight()/2.f + "Width: " + v.getWidth()/2.f);
							Log.d("&&&", "Virtual x: " + virtual_x + "virtual y: " + virtual_y);
							Log.d("&&&", "Offset x: " + offset_x + "Offset y: " + offset_y);
							Log.d("&&&",
									"DOWN - Motionevent - x: " + x + " y: " + y + " "
											+ "pointer: " +
											event.getPointerId(event.getActionIndex()));
							TouchUtil.touchDown(x, y,
									event.getPointerId(event.getActionIndex()));
							return true;
						case (MotionEvent.ACTION_MOVE) :
							Log.d("&&&",
									"MOVE - Motionevent - x: " + x + " y: " + y + " "
											+ "pointer: " +
											event.getPointerId(event.getActionIndex()));
							TouchUtil.updatePosition(x, y,
									event.getPointerId(event.getActionIndex()));
							return true;
						case (MotionEvent.ACTION_UP) :
							Log.d("&&&",
									"UP - Motionevent - pointer: " +
											event.getPointerId(event.getActionIndex()));
							TouchUtil.touchUp(event.getPointerId(event.getActionIndex()));
							return true;
					}
					Log.d("&&&", String.valueOf(event.getSize()));
					return false;
				}
			});
		}

		//CATROID-105 - TODO: does this make any difference? probably necessary for cast:
		if (stageActivity.getGdxGraphics().getView() instanceof SurfaceView) {
			SurfaceView glView = (SurfaceView) stageActivity.getGdxGraphics().getView();
			glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
			glView.setZOrderMediaOverlay(true);
		}
		stageActivity.stageAudioFocus = new StageAudioFocus(stageActivity);
		stageActivity.stageResourceHolder = new StageResourceHolder(stageActivity);
		MidiSoundManager.getInstance().reset();

		List<String> requiredPermissions = getProjectsRuntimePermissionList();
		if (requiredPermissions.isEmpty()) {
			stageActivity.stageResourceHolder.initResources();
		} else {
			new RequiresPermissionTask(REQUEST_PERMISSIONS_STAGE_RESOURCE_CREATE, requiredPermissions, R.string.runtime_permission_general) {
				public void task() {
					stageActivity.stageResourceHolder.initResources();
				}
			}.execute(stageActivity);
		}
	}

	static int pxToDp(int px, View view) {
		DisplayMetrics displayMetrics = view.getContext().getResources().getDisplayMetrics();
		int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return dp;
	}

	static void stagePause(final StageActivity stageActivity) {
		if (checkPermission(stageActivity, getProjectsRuntimePermissionList())) {
			if (stageActivity.nfcAdapter != null) {
				try {
					stageActivity.nfcAdapter.disableForegroundDispatch(stageActivity);
				} catch (IllegalStateException illegalStateException) {
					Log.e(TAG, "Disabling NFC foreground dispatching went wrong!", illegalStateException);
				}
			}

			List<Sprite> sprites = ((StageListener) stageActivity.getApplicationListener()).getSpritesFromStage();

			if (sprites != null) {
				for (Sprite sprite : sprites) {
					sprite.look.pauseParticleEffect();
				}
			}

			get(SpeechRecognitionHolderFactory.class).getInstance().destroy();

			SensorHandler.timerPauseValue = SystemClock.uptimeMillis();

			SensorHandler.stopSensorListeners();
			SoundManager.getInstance().pause();
			MidiSoundManager.getInstance().pause();
			StageActivity.stageListener.menuPause();
			stageActivity.stageAudioFocus.releaseAudioFocus();
			if (stageActivity.cameraManager != null) {
				stageActivity.cameraManager.pause();
			}

			BluetoothDeviceService bluetoothDeviceService =
					ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);
			if (bluetoothDeviceService != null) {
				bluetoothDeviceService.pause();
			}
			VibrationUtil.pauseVibration();
			if (ProjectManager.getInstance().getCurrentProject().isCastProject()) {
				CastManager.getInstance().setRemoteLayoutToPauseScreen(stageActivity);
			}
		}
	}

	public static void stageResume(final StageActivity stageActivity) {
		if (stageActivity.dialogIsShowing()) {
			return;
		}

		if (checkPermission(stageActivity, getProjectsRuntimePermissionList())) {
			Brick.ResourcesSet resourcesSet = ProjectManager.getInstance().getCurrentProject().getRequiredResources();
			List<Sprite> spriteList = ProjectManager.getInstance().getCurrentlyPlayingScene().getSpriteList();

			SensorHandler.startSensorListener(stageActivity);

			for (Sprite sprite : spriteList) {
				if (sprite.getPlaySoundBricks().size() > 0) {
					stageActivity.stageAudioFocus.requestAudioFocus();
					break;
				}
			}

			List<Sprite> sprites =
					((StageListener) stageActivity.getApplicationListener()).getSpritesFromStage();
			if (sprites != null) {
				for (Sprite sprite : sprites) {
					sprite.look.resumeParticleEffect();
				}
			}

			if (resourcesSet.contains(Brick.VIBRATION)) {
				VibrationUtil.resumeVibration();
			}

			if (resourcesSet.contains(Brick.BLUETOOTH_LEGO_NXT)
					|| resourcesSet.contains(Brick.BLUETOOTH_PHIRO)
					|| resourcesSet.contains(Brick.BLUETOOTH_SENSORS_ARDUINO)) {
				try {
					ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).start();
				} catch (MindstormsException e) {
					Log.e(TAG, e.getMessage());
				}
			}

			if (stageActivity.cameraManager != null) {
				stageActivity.cameraManager.resume();
			}

			if (resourcesSet.contains(Brick.TEXT_TO_SPEECH)) {
				stageActivity.stageAudioFocus.requestAudioFocus();
			}

			if (resourcesSet.contains(Brick.NFC_ADAPTER)
					&& stageActivity.nfcAdapter != null) {
				stageActivity.nfcAdapter.enableForegroundDispatch(stageActivity, stageActivity.pendingIntent, null, null);
			}

			if (ProjectManager.getInstance().getCurrentProject().isCastProject()) {
				CastManager.getInstance().resumeRemoteLayoutFromPauseScreen();
			}

			SoundManager.getInstance().resume();
			MidiSoundManager.getInstance().resume();
			if (stageActivity.stageResourceHolder.initFinished()) {
				StageActivity.stageListener.menuResume();
			}
		}
	}

	static void stageDestroy(StageActivity stageActivity) {
		if (checkPermission(stageActivity, getProjectsRuntimePermissionList())) {
			if (stageActivity.brickDialogManager != null) {
				stageActivity.brickDialogManager.dismissAllDialogs();
			}
			BluetoothDeviceService service = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);
			if (service != null) {
				service.destroy();
			}
			VibrationUtil.destroy();
			if (stageActivity.cameraManager != null) {
				stageActivity.cameraManager.destroy();
				stageActivity.cameraManager = null;
			}
			SensorHandler.destroy();
			if (ProjectManager.getInstance().getCurrentProject().isCastProject()) {
				CastManager.getInstance().onStageDestroyed();
			}
			StageActivity.stageListener.finish();
			stageActivity.manageLoadAndFinish();
			StageActivity.stageListener = null;
		}
		ProjectManager.getInstance().setCurrentlyPlayingScene(ProjectManager.getInstance().getCurrentlyEditedScene());
	}
}
