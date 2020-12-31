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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ads.AdsBanner;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.cast.CastManager;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.AdsBannerSizeEnum;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.devices.mindstorms.MindstormsException;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.formulaeditor.UserDataWrapper;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.io.StageAudioFocus;
import org.catrobat.catroid.pocketmusic.mididriver.MidiSoundManager;
import org.catrobat.catroid.ui.dialogs.StageDialog;
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask;
import org.catrobat.catroid.utils.VibrationUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.catrobat.catroid.ads.AdsBanner.ADS_BANNER_BOTTOM;
import static org.catrobat.catroid.ads.AdsBanner.ADS_BANNER_TOP;
import static org.catrobat.catroid.ads.AdsBanner.ADS_HIDE_BANNER;
import static org.catrobat.catroid.ads.AdsBanner.ADS_LARGE_BANNER_BOTTOM;
import static org.catrobat.catroid.ads.AdsBanner.ADS_LARGE_BANNER_TOP;
import static org.catrobat.catroid.ads.AdsBanner.ADS_SMART_BANNER_BOTTOM;
import static org.catrobat.catroid.ads.AdsBanner.ADS_SMART_BANNER_TOP;
import static org.catrobat.catroid.stage.StageResourceHolder.getProjectsRuntimePermissionList;
import static org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask.checkPermission;

public final class StageLifeCycleController {
	public static final String TAG = StageLifeCycleController.class.getSimpleName();

	private static final int REQUEST_PERMISSIONS_STAGE_RESOURCE_CREATE = 601;
	private static final int ADS_VIEW_POSITION = 1;

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
			View stageView = stageActivity.initializeForView(StageActivity.stageListener, stageActivity.configuration);
			RelativeLayout stageLayout = new RelativeLayout(stageActivity);
			stageLayout.addView(stageView, 0);
			setupAdMobBannerHandler(stageActivity, stageLayout);
			stageActivity.setContentView(stageLayout);
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

	private static void setupAdMobBannerHandler(StageActivity stageActivity, RelativeLayout stageLayout) {
		stageActivity.adsBanner = new AdsBanner(stageActivity);
		StageActivity.adsHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(@NotNull Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
					case ADS_BANNER_TOP:
						showAdsBannerOnStage(stageActivity,
								stageLayout,
								AdsBannerSizeEnum.BANNER,
								RelativeLayout.ALIGN_PARENT_TOP);
						break;
					case ADS_SMART_BANNER_TOP:
						showAdsBannerOnStage(stageActivity,
								stageLayout,
								AdsBannerSizeEnum.SMART_BANNER,
								RelativeLayout.ALIGN_PARENT_TOP);
						break;
					case ADS_LARGE_BANNER_TOP:
						showAdsBannerOnStage(stageActivity,
								stageLayout,
								AdsBannerSizeEnum.LARGE_BANNER,
								RelativeLayout.ALIGN_PARENT_TOP);
						break;
					case ADS_BANNER_BOTTOM:
						showAdsBannerOnStage(stageActivity,
								stageLayout,
								AdsBannerSizeEnum.BANNER,
								RelativeLayout.ALIGN_PARENT_BOTTOM);
						break;
					case ADS_SMART_BANNER_BOTTOM:
						showAdsBannerOnStage(stageActivity,
								stageLayout,
								AdsBannerSizeEnum.SMART_BANNER,
								RelativeLayout.ALIGN_PARENT_BOTTOM);
						break;
					case ADS_LARGE_BANNER_BOTTOM:
						showAdsBannerOnStage(stageActivity,
								stageLayout,
								AdsBannerSizeEnum.LARGE_BANNER,
								RelativeLayout.ALIGN_PARENT_BOTTOM);
						break;
					case ADS_HIDE_BANNER:
						hideAdsBannerIfExists(stageActivity, stageLayout);
						break;
					default:
						Log.e(TAG, "Unhandled message in adMobBannerHandler, case " + msg.what);
						break;
				}
			}
		};
	}

	private static void hideAdsBannerIfExists(StageActivity stageActivity,
			RelativeLayout stageLayout) {
		if (stageLayout.getChildAt(ADS_VIEW_POSITION) != null) {
			stageLayout.removeViewAt(ADS_VIEW_POSITION);
			stageActivity.adsBanner.hide();
		}
	}

	private static void showAdsBannerOnStage(StageActivity stageActivity, RelativeLayout stageLayout, AdsBannerSizeEnum adSize, int position) {
		hideAdsBannerIfExists(stageActivity, stageLayout);
		stageActivity.adsBanner = new AdsBanner(stageActivity);
		stageActivity.adsBanner.createNew(adSize, position);
		stageActivity.adsBanner.show();
		stageLayout.addView(stageActivity.adsBanner.getAdView(), ADS_VIEW_POSITION);
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
			SpeechRecognitionHolder.Companion.getInstance().destroy();

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
		stageActivity.adsBanner.pause();
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
		stageActivity.adsBanner.resume();
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
