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

import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.cast.CastManager;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.devices.mindstorms.MindstormsException;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.formulaeditor.UserDataWrapper;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.io.StageAudioFocus;
import org.catrobat.catroid.ui.dialogs.StageDialog;
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask;
import org.catrobat.catroid.utils.FlashUtil;
import org.catrobat.catroid.utils.VibrationUtil;

import java.util.List;

import static org.catrobat.catroid.stage.StageResourceHolder.getProjectsRuntimePermissionList;
import static org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask.checkPermission;

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

		stageActivity.numberOfSpritesCloned = 0;

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

		stageActivity.stageListener = new StageListener();
		stageActivity.stageDialog = new StageDialog(stageActivity, stageActivity.stageListener, R.style.StageDialog);
		stageActivity.calculateScreenSizes();

		stageActivity.configuration = new AndroidApplicationConfiguration();
		stageActivity.configuration.r = stageActivity.configuration.g = stageActivity.configuration.b = stageActivity.configuration.a = 8;
		if (ProjectManager.getInstance().getCurrentProject().isCastProject()) {
			stageActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			stageActivity.setContentView(R.layout.activity_stage_gamepad);
			CastManager.getInstance().initializeGamepadActivity(stageActivity);
			CastManager.getInstance()
					.addStageViewToLayout((GLSurfaceView20) stageActivity.initializeForView(stageActivity.stageListener, stageActivity.configuration));
		} else {
			stageActivity.initialize(stageActivity.stageListener, stageActivity.configuration);
		}

		//CATROID-105 - TODO: does this make any difference? probably necessary for cast:
		if (stageActivity.getGdxGraphics().getView() instanceof SurfaceView) {
			SurfaceView glView = (SurfaceView) stageActivity.getGdxGraphics().getView();
			glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		}
		stageActivity.stageAudioFocus = new StageAudioFocus(stageActivity);
		stageActivity.stageResourceHolder = new StageResourceHolder(stageActivity);

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

	static void stagePause(final StageActivity stageActivity) {
		if (checkPermission(stageActivity, getProjectsRuntimePermissionList())) {
			if (stageActivity.nfcAdapter != null) {
				try {
					stageActivity.nfcAdapter.disableForegroundDispatch(stageActivity);
				} catch (IllegalStateException illegalStateException) {
					Log.e(TAG, "Disabling NFC foreground dispatching went wrong!", illegalStateException);
				}
			}
			SensorHandler.stopSensorListeners();
			SoundManager.getInstance().pause();
			stageActivity.stageListener.menuPause();
			stageActivity.stageAudioFocus.releaseAudioFocus();
			if (CameraManager.getInstance() != null) {
				FlashUtil.pauseFlash();
				FaceDetectionHandler.pauseFaceDetection();
				CameraManager.getInstance().pausePreview();
				CameraManager.getInstance().releaseCamera();
			}
			ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).pause();
			VibrationUtil.pauseVibration();
			if (ProjectManager.getInstance().getCurrentProject().isCastProject()) {
				CastManager.getInstance().setRemoteLayoutToPauseScreen(stageActivity);
			}
			if (stageActivity.stageResourceHolder.droneInitializer != null) {
				stageActivity.stageResourceHolder.droneInitializer.onPause();
			}
			if (stageActivity.stageResourceHolder.droneController != null) {
				stageActivity.stageResourceHolder.droneController.onPause();
			}
		}
	}

	public static void stageResume(final StageActivity stageActivity) {
		if (stageActivity.stageDialog.isShowing() || stageActivity.askDialog != null) {
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

			if (resourcesSet.contains(Brick.CAMERA_FLASH)) {
				FlashUtil.resumeFlash();
			}

			if (resourcesSet.contains(Brick.VIBRATION)) {
				VibrationUtil.resumeVibration();
			}

			if (resourcesSet.contains(Brick.FACE_DETECTION)) {
				FaceDetectionHandler.resumeFaceDetection();
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

			if (resourcesSet.contains(Brick.CAMERA_BACK)
					|| resourcesSet.contains(Brick.CAMERA_FRONT)
					|| resourcesSet.contains(Brick.VIDEO)) {
				CameraManager.getInstance().resumePreviewAsync();
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

			if (CameraManager.getInstance() != null) {
				FaceDetectionHandler.resumeFaceDetection();
			}

			SoundManager.getInstance().resume();
			if (stageActivity.stageResourceHolder.initFinished()) {
				stageActivity.stageListener.menuResume();
			}
			if (stageActivity.stageResourceHolder.droneInitializer != null) {
				stageActivity.stageResourceHolder.droneInitializer.onResume();
			}
			if (stageActivity.stageResourceHolder.droneController != null) {
				stageActivity.stageResourceHolder.droneController.onResume();
			}
		}
	}

	static void stageDestroy(StageActivity stageActivity) {
		if (checkPermission(stageActivity, getProjectsRuntimePermissionList())) {
			stageActivity.jumpingSumoDisconnect();
			ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).destroy();
			VibrationUtil.destroy();
			SensorHandler.stopSensorListeners();
			if (CameraManager.getInstance() != null) {
				FlashUtil.destroy();
				FaceDetectionHandler.stopFaceDetection();
				CameraManager.getInstance().stopPreviewAsync();
				CameraManager.getInstance().releaseCamera();
				CameraManager.getInstance().setToDefaultCamera();
			}
			if (ProjectManager.getInstance().getCurrentProject().isCastProject()) {
				CastManager.getInstance().onStageDestroyed();
			}
			stageActivity.stageListener.finish();
			stageActivity.manageLoadAndFinish();
			if (stageActivity.stageResourceHolder.droneInitializer != null) {
				stageActivity.stageResourceHolder.droneInitializer.onDestroy();
			}
			if (stageActivity.stageResourceHolder.droneController != null) {
				stageActivity.stageResourceHolder.droneController.onDestroy();
			}
		}
		ProjectManager.getInstance().setCurrentlyPlayingScene(ProjectManager.getInstance().getCurrentlyEditedScene());
	}
}
