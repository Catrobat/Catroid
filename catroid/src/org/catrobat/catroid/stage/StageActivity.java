/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.io.StageAudioFocus;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.StageDialog;
import org.catrobat.catroid.utils.FlashUtil;
import org.catrobat.catroid.utils.VibratorUtil;

public class StageActivity extends AndroidApplication {
	public static final String TAG = StageActivity.class.getSimpleName();
	public static StageListener stageListener;
	public static final int STAGE_ACTIVITY_FINISH = 7777;

	private StageAudioFocus stageAudioFocus;
	private StageDialog stageDialog;
	private boolean resizePossible;

	AndroidApplicationConfiguration configuration = null;

	private void initGamepadListeners() {

		View.OnTouchListener otl = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				handleGamepadTouch((ImageButton) v, event);
				return true;
			}
		};

		ImageButton[] gamepadButtons = {

				(ImageButton) findViewById(R.id.gamepadButtonA),
				(ImageButton) findViewById(R.id.gamepadButtonB),
				(ImageButton) findViewById(R.id.gamepadButtonUp),
				(ImageButton) findViewById(R.id.gamepadButtonDown),
				(ImageButton) findViewById(R.id.gamepadButtonLeft),
				(ImageButton) findViewById(R.id.gamepadButtonRight)
		};

		for (ImageButton btn : gamepadButtons) {
			btn.setOnTouchListener(otl);
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate()");

		if (ProjectManager.getInstance().isCurrentProjectLandscapeMode()) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		stageListener = new StageListener();
		stageDialog = new StageDialog(this, stageListener, R.style.stage_dialog);
		calculateScreenSizes();

		// need we this here?
		configuration = new AndroidApplicationConfiguration();
		configuration.r = configuration.g = configuration.b = configuration.a = 8;

		initialize(stageListener, configuration);

		if (graphics.getView() instanceof SurfaceView) {
			SurfaceView glView = (SurfaceView) graphics.getView();
			glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
			glView.setZOrderOnTop(true);
		}

		ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).initialise();

		stageAudioFocus = new StageAudioFocus(this);

		if (ProjectManager.getInstance().getCurrentProject().isCastProject()) {
			initGamepadListeners();
		}

		CameraManager.getInstance().setStageActivity(this);
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
		stageAudioFocus.releaseAudioFocus();
		FlashUtil.pauseFlash();
		FaceDetectionHandler.pauseFaceDetection();

		CameraManager.getInstance().releaseCamera();
		VibratorUtil.pauseVibrator();
		super.onPause();

		ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).pause();
	}

	@Override
	public void onResume() {
		stageAudioFocus.requestAudioFocus();

		FaceDetectionHandler.resumeFaceDetection();
		FlashUtil.resumeFlash();
		CameraManager.getInstance().resumePreviewAsync();

		VibratorUtil.resumeVibrator();

		SensorHandler.startSensorListener(this);

		super.onResume();

		ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).start();
	}

	public void pause() {
		SensorHandler.stopSensorListeners();
		stageListener.menuPause();
		FlashUtil.pauseFlash();
		VibratorUtil.pauseVibrator();
		FaceDetectionHandler.pauseFaceDetection();

		CameraManager.getInstance().pausePreviewAsync();

		ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).pause();
	}

	public void resume() {
		stageListener.menuResume();
		FlashUtil.resumeFlash();
		VibratorUtil.resumeVibrator();
		SensorHandler.startSensorListener(this);
		FaceDetectionHandler.resumeFaceDetection();

		CameraManager.getInstance().resumePreviewAsync();

		ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).start();
	}

	public boolean getResizePossible() {
		return resizePossible;
	}

	private void calculateScreenSizes() {
		int virtualScreenWidth = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenWidth;
		int virtualScreenHeight = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenHeight;
		if (virtualScreenHeight > virtualScreenWidth) {
			iflandscapeModeSwitchWidthAndHeight();
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

	private void iflandscapeModeSwitchWidthAndHeight() {
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
		Log.d(TAG, "onDestroy()");
		ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).destroy();
		FlashUtil.destroy();
		VibratorUtil.destroy();
		FaceDetectionHandler.stopFaceDetection();
		CameraManager.getInstance().stopPreviewAsync();
		CameraManager.getInstance().releaseCamera();

		CameraManager.getInstance().setToDefaultCamera();

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

	public void onPauseButtonPressed(View view) {
		view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		onBackPressed();
	}

	private void handleGamepadTouch(ImageButton button, MotionEvent event) {

		if (event.getAction() != MotionEvent.ACTION_DOWN && event.getAction() != MotionEvent.ACTION_UP) {
			// We only care about the event when a gamepad button is pressed and when a gamepad button is unpressed
			return;
		}

		//CastManager castManager = CastManager.getInstance();

		boolean isActionDown = (event.getAction() == MotionEvent.ACTION_DOWN);
		String buttonPressedName;

		switch (button.getId())
		{
			case R.id.gamepadButtonA:
				buttonPressedName = getString(R.string.cast_gamepad_A);
				button.setImageResource(isActionDown ? R.drawable.gamepad_button_a_pressed : R.drawable.gamepad_button_a);
				//castManager.setButtonPress(Sensors.GAMEPAD_A_PRESSED, isActionDown);
				break;
			case R.id.gamepadButtonB:
				buttonPressedName = getString(R.string.cast_gamepad_B);
				button.setImageResource(isActionDown ? R.drawable.gamepad_button_b_pressed : R.drawable.gamepad_button_b);
				//castManager.setButtonPress(Sensors.GAMEPAD_B_PRESSED, isActionDown);
				break;
			case R.id.gamepadButtonUp:
				buttonPressedName = getString(R.string.cast_gamepad_up);
				//castManager.setButtonPress(Sensors.GAMEPAD_UP_PRESSED, isActionDown);
				break;
			case R.id.gamepadButtonDown:
				buttonPressedName = getString(R.string.cast_gamepad_down);
				//castManager.setButtonPress(Sensors.GAMEPAD_DOWN_PRESSED, isActionDown);
				break;
			case R.id.gamepadButtonLeft:
				buttonPressedName = getString(R.string.cast_gamepad_left);
				//castManager.setButtonPress(Sensors.GAMEPAD_LEFT_PRESSED, isActionDown);
				break;
			case R.id.gamepadButtonRight:
				buttonPressedName = getString(R.string.cast_gamepad_right);
				//castManager.setButtonPress(Sensors.GAMEPAD_RIGHT_PRESSED, isActionDown);
				break;
			default:
				throw new IllegalArgumentException("Unknown button pressed");
		}

		if (isActionDown) {
			stageListener.gamepadPressed(buttonPressedName);
			button.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		}
	}

	//for running Asynchronous Tasks from the stage
	public void post(Runnable r) {
		handler.post(r);
	}

	public void destroy() {
		stageListener.finish();
		manageLoadAndFinish();

		final AlertDialog.Builder builder = new CustomAlertDialogBuilder(this);
		builder.setMessage(R.string.error_flash_front_camera).setCancelable(false)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						onDestroy();
						exit();
					}
				});

		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					AlertDialog dialog = builder.create();
					dialog.show();
				} catch (Exception e) {
					Log.e(TAG, "Error while showing dialog. " + e.getMessage());
				}
			}
		});
	}
}
