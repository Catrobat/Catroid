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

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.drone.DroneInitializer;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.io.StageAudioFocus;
import org.catrobat.catroid.nfc.NfcHandler;
import org.catrobat.catroid.ui.dialogs.StageDialog;
import org.catrobat.catroid.utils.LedUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.VibratorUtil;

public class StageActivity extends AndroidApplication {
	public static final String TAG = StageActivity.class.getSimpleName();
	public static StageListener stageListener;
	private boolean resizePossible;
	private StageDialog stageDialog;

	private PendingIntent pendingIntent;
	private NfcAdapter nfcAdapter;
	private DroneConnection droneConnection = null;

	public static final int STAGE_ACTIVITY_FINISH = 7777;

	private StageAudioFocus stageAudioFocus;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (ProjectManager.getInstance().isCurrentProjectLandscape()) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		if (getIntent().getBooleanExtra(DroneInitializer.INIT_DRONE_STRING_EXTRA, false)) {
			droneConnection = new DroneConnection(this);
		}
		stageListener = new StageListener();
		stageDialog = new StageDialog(this, stageListener, R.style.stage_dialog);
		calculateScreenSizes();

		initialize(stageListener, new AndroidApplicationConfiguration());

		pendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		Log.d(TAG, "onCreate()");

		if (nfcAdapter == null) {
			Log.d(TAG, "could not get nfc adapter :(");
		}

		if (droneConnection != null) {
			try {
				droneConnection.initialise();
			} catch (RuntimeException runtimeException) {
				Log.e(TAG, "Failure during drone service startup", runtimeException);
				ToastUtil.showError(this, R.string.error_no_drone_connected);
				this.finish();
			}
		}

		ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).initialise();

		stageAudioFocus = new StageAudioFocus(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d(TAG, "processIntent");
		NfcHandler.processIntent(intent);
	}

	@Override
	public void onBackPressed() {
		pause();
		stageDialog.show();
	}

	public void manageLoadAndFinish() {
		stageListener.pause();
		stageListener.finish();

		PreStageActivity.shutdownResources();
	}

	@Override
	public void onPause() {
		SensorHandler.stopSensorListeners();
		stageListener.activityPause();
		stageAudioFocus.releaseAudioFocus();
		LedUtil.pauseLed();
		VibratorUtil.pauseVibrator();

		if (nfcAdapter != null) {
			Log.d(TAG, "onPause()disableForegroundDispatch()");
			nfcAdapter.disableForegroundDispatch(this);
		}

		super.onPause();

		if (droneConnection != null) {
			droneConnection.pause();
		}

		ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).pause();
	}

	@Override
	public void onResume() {
		super.onResume();
		SensorHandler.startSensorListener(this);
		stageListener.activityResume();
		stageAudioFocus.requestAudioFocus();
		LedUtil.resumeLed();
		VibratorUtil.resumeVibrator();

		if (nfcAdapter != null) {
			Log.d(TAG, "onResume()enableForegroundDispatch()");
			nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
		}

		if (droneConnection != null) {
			droneConnection.start();
		}

		ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).start();
	}

	public void pause() {
		SensorHandler.stopSensorListeners();
		stageListener.menuPause();

		if (nfcAdapter != null) {
			Log.d(TAG, "onPause()disableForegroundDispatch()");
			nfcAdapter.disableForegroundDispatch(this);
		}

		LedUtil.pauseLed();
		VibratorUtil.pauseVibrator();
		FaceDetectionHandler.pauseFaceDetection();

		ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).pause();
	}

	public void resume() {
		stageListener.menuResume();
		LedUtil.resumeLed();
		VibratorUtil.resumeVibrator();
		SensorHandler.startSensorListener(this);
		FaceDetectionHandler.startFaceDetection(this);

		ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).start();

		if (nfcAdapter != null) {
			nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
		}
	}

	public boolean getResizePossible() {
		return resizePossible;
	}

	private void calculateScreenSizes() {
		int virtualScreenWidth = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenWidth;
		int virtualScreenHeight = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenHeight;
		if (virtualScreenHeight > virtualScreenWidth) {
			ifLandscapeSwitchWidthAndHeight();
		} else {
			ifPortraitSwitchWidthAndHeight();
		}
		float aspectRatio = (float) virtualScreenWidth / (float) virtualScreenHeight;
		float screenAspectRatio = ScreenValues.getAspectRatio();

		if ((virtualScreenWidth == ScreenValues.SCREEN_WIDTH && virtualScreenHeight == ScreenValues.SCREEN_HEIGHT)
				|| Float.compare(screenAspectRatio, aspectRatio) == 0) {
			resizePossible = false;
			stageListener.maximizeViewPortWidth = ScreenValues.SCREEN_WIDTH;
			stageListener.maximizeViewPortHeight = ScreenValues.SCREEN_HEIGHT;
			return;
		}

		resizePossible = true;

		float scale = 1f;
		float ratioHeight = (float) ScreenValues.SCREEN_HEIGHT / (float) virtualScreenHeight;
		float ratioWidth = (float) ScreenValues.SCREEN_WIDTH / (float) virtualScreenWidth;

		if (aspectRatio < screenAspectRatio) {
			scale = ratioHeight / ratioWidth;
			stageListener.maximizeViewPortWidth = (int) (ScreenValues.SCREEN_WIDTH * scale);
			stageListener.maximizeViewPortX = (int) ((ScreenValues.SCREEN_WIDTH - stageListener.maximizeViewPortWidth) / 2f);
			stageListener.maximizeViewPortHeight = ScreenValues.SCREEN_HEIGHT;
		} else if (aspectRatio > screenAspectRatio) {
			scale = ratioWidth / ratioHeight;
			stageListener.maximizeViewPortHeight = (int) (ScreenValues.SCREEN_HEIGHT * scale);
			stageListener.maximizeViewPortY = (int) ((ScreenValues.SCREEN_HEIGHT - stageListener.maximizeViewPortHeight) / 2f);
			stageListener.maximizeViewPortWidth = ScreenValues.SCREEN_WIDTH;
		}
	}

	private void ifLandscapeSwitchWidthAndHeight() {
		if (ScreenValues.SCREEN_WIDTH > ScreenValues.SCREEN_HEIGHT) {
			int tmp = ScreenValues.SCREEN_HEIGHT;
			ScreenValues.SCREEN_HEIGHT = ScreenValues.SCREEN_WIDTH;
			ScreenValues.SCREEN_WIDTH = tmp;
		}
	}

	private void ifPortraitSwitchWidthAndHeight() {
		if (ScreenValues.SCREEN_WIDTH < ScreenValues.SCREEN_HEIGHT) {
			int tmp = ScreenValues.SCREEN_HEIGHT;
			ScreenValues.SCREEN_HEIGHT = ScreenValues.SCREEN_WIDTH;
			ScreenValues.SCREEN_WIDTH = tmp;
		}
	}

	@Override
	protected void onDestroy() {
		if (droneConnection != null) {
			droneConnection.destroy();
		}

		ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).destroy();

		Log.d(TAG, "Destroy");
		LedUtil.destroy();
		VibratorUtil.destroy();
		super.onDestroy();
	}

	@Override
	public ApplicationListener getApplicationListener() {
		return stageListener;
	}

	@Override
	public void log(String tag, String message, Throwable exception) {
		Log.d(tag, message, exception);
	}

	@Override
	public int getLogLevel() {
		return 0;
	}
}
