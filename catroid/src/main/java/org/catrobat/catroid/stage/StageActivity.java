/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.EditText;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.BackgroundWaitHandler;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.AskAction;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.io.StageAudioFocus;
import org.catrobat.catroid.nfc.NfcHandler;
import org.catrobat.catroid.ui.MarketingActivity;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.StageDialog;
import org.catrobat.catroid.utils.FlashUtil;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.UtilUi;
import org.catrobat.catroid.utils.VibratorUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class StageActivity extends AndroidApplication {
	public static final String TAG = StageActivity.class.getSimpleName();
	public static StageListener stageListener;
	public static final int STAGE_ACTIVITY_FINISH = 7777;

	public static final int ASK_MESSAGE = 0;
	public static final int REGISTER_INTENT = 1;
	private static final int PERFORM_INTENT = 2;

	private StageAudioFocus stageAudioFocus;
	private PendingIntent pendingIntent;
	private NfcAdapter nfcAdapter;
	private static BlockingDeque<NdefMessage> ndefMessageBlockingDeque = new LinkedBlockingDeque<NdefMessage>();
	private StageDialog stageDialog;
	private boolean resizePossible;
	private boolean askDialogUnanswered = false;

	private static int numberOfSpritesCloned;

	public static Handler messageHandler;
	public static Map<Integer, IntentListener> intentListeners = new HashMap<>();
	public static Random randomGenerator = new Random();

	AndroidApplicationConfiguration configuration = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate()");

		numberOfSpritesCloned = 0;
		setupAskHandler();

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
		}

		pendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		Log.d(TAG, "onCreate()");

		if (nfcAdapter == null) {
			Log.d(TAG, "could not get nfc adapter :(");
		}

		ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).initialise();

		stageAudioFocus = new StageAudioFocus(this);

		CameraManager.getInstance().setStageActivity(this);

		BackgroundWaitHandler.reset();
		SnackbarUtil.showHintSnackbar(this, R.string.hint_stage);
	}

	private void setupAskHandler() {
		final StageActivity currentStage = this;
		messageHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message message) {
				List<Object> params = (ArrayList<Object>) message.obj;

				switch (message.what) {
					case ASK_MESSAGE:
						showDialog((String) params.get(1), (AskAction) params.get(0));
						break;
					case REGISTER_INTENT:
						currentStage.queueIntent((IntentListener) params.get(0));
						break;
					case PERFORM_INTENT:
						currentStage.startQueuedIntent((Integer) params.get(0));
						break;
					default:
						Log.e(TAG, "Unhandled message in messagehandler, case " + message.what);
						break;
				}
			}
		};
	}

	private void showDialog(String question, final AskAction askAction) {
		pause();

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Dialog));
		final EditText edittext = new EditText(getContext());
		alertBuilder.setView(edittext);
		alertBuilder.setMessage(getContext().getString(R.string.brick_ask_dialog_hint));
		alertBuilder.setTitle(question);
		alertBuilder.setCancelable(false);

		alertBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					onBackPressed();
					return true;
				}
				return false;
			}
		});

		alertBuilder.setPositiveButton(getContext().getString(R.string.brick_ask_dialog_submit), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String questionAnswer = edittext.getText().toString();
				askAction.setAnswerText(questionAnswer);
				askDialogUnanswered = false;
				resume();
			}
		});

		AlertDialog dialog = alertBuilder.create();
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		askDialogUnanswered = true;
		dialog.show();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d(TAG, "processIntent");
		NfcHandler.processIntent(intent);

		if (!ndefMessageBlockingDeque.isEmpty()) {
			Tag currentTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			NfcHandler.writeTag(currentTag, ndefMessageBlockingDeque.poll());
		}
	}

	@Override
	public void onBackPressed() {
		if (BuildConfig.FEATURE_APK_GENERATOR_ENABLED) {
			PreStageActivity.shutdownPersistentResources();
			Intent marketingIntent = new Intent(StageActivity.this, MarketingActivity.class);
			startActivity(marketingIntent);
			finish();
		} else {
			pause();
			stageDialog.show();
		}
	}

	public void manageLoadAndFinish() {
		stageListener.pause();
		stageListener.finish();

		PreStageActivity.shutdownResources();
	}

	@Override
	public void onPause() {
		if (nfcAdapter != null) {
			try {
				nfcAdapter.disableForegroundDispatch(this);
			} catch (IllegalStateException illegalStateException) {
				Log.e(TAG, "Disabling NFC foreground dispatching went wrong!", illegalStateException);
			}
		}
		SensorHandler.stopSensorListeners();
		stageAudioFocus.releaseAudioFocus();
		FlashUtil.pauseFlash();
		FaceDetectionHandler.pauseFaceDetection();
		CameraManager.getInstance().pausePreview();
		CameraManager.getInstance().releaseCamera();
		VibratorUtil.pauseVibrator();
		super.onPause();

		ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).pause();
	}

	@Override
	public void onResume() {
		resumeResources();
		super.onResume();
	}

	public void pause() {
		if (nfcAdapter != null) {
			nfcAdapter.disableForegroundDispatch(this);
		}

		SensorHandler.stopSensorListeners();
		stageListener.menuPause();
		FlashUtil.pauseFlash();
		VibratorUtil.pauseVibrator();
		FaceDetectionHandler.pauseFaceDetection();

		CameraManager.getInstance().pausePreviewAsync();

		ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).pause();
	}

	public void resume() {
		if (askDialogUnanswered) {
			return;
		}
		stageListener.menuResume();
		resumeResources();
	}

	public void resumeResources() {
		int requiredResources = ProjectManager.getInstance().getCurrentProject().getRequiredResources();
		List<Sprite> spriteList = ProjectManager.getInstance().getSceneToPlay().getSpriteList();

		SensorHandler.startSensorListener(this);

		for (Sprite sprite : spriteList) {
			if (sprite.getPlaySoundBricks().size() > 0) {
				stageAudioFocus.requestAudioFocus();
				break;
			}
		}

		if ((requiredResources & Brick.CAMERA_FLASH) != 0) {
			FlashUtil.resumeFlash();
		}

		if ((requiredResources & Brick.VIBRATOR) != 0) {
			VibratorUtil.resumeVibrator();
		}

		if ((requiredResources & Brick.FACE_DETECTION) != 0) {
			FaceDetectionHandler.resumeFaceDetection();
		}

		if ((requiredResources & Brick.BLUETOOTH_LEGO_NXT) != 0
				|| (requiredResources & Brick.BLUETOOTH_PHIRO) != 0
				|| (requiredResources & Brick.BLUETOOTH_SENSORS_ARDUINO) != 0) {
			ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).start();
		}

		if ((requiredResources & Brick.CAMERA_BACK) != 0
				|| (requiredResources & Brick.CAMERA_FRONT) != 0
				|| (requiredResources & Brick.VIDEO) != 0) {
			CameraManager.getInstance().resumePreviewAsync();
		}

		if ((requiredResources & Brick.TEXT_TO_SPEECH) != 0) {
			stageAudioFocus.requestAudioFocus();
		}

		if ((requiredResources & Brick.NFC_ADAPTER) != 0
				&& nfcAdapter != null) {
			nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
		}
	}

	public boolean getResizePossible() {
		return resizePossible;
	}

	private void calculateScreenSizes() {
		UtilUi.updateScreenWidthAndHeight(getContext());
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
		ProjectManager.getInstance().setSceneToPlay(ProjectManager.getInstance().getCurrentScene());
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

	public static int getAndIncrementNumberOfClonedSprites() {
		return ++numberOfSpritesCloned;
	}

	public static void addNfcTagMessageToDeque(NdefMessage message) {
		ndefMessageBlockingDeque.addLast(message);
	}

	public synchronized void queueIntent(IntentListener asker) {
		if (StageActivity.messageHandler == null) {
			return;
		}
		int newIdentId;
		do {
			newIdentId = StageActivity.randomGenerator.nextInt(Integer.MAX_VALUE);
		} while (intentListeners.containsKey(newIdentId));

		intentListeners.put(newIdentId, asker);
		ArrayList<Object> params = new ArrayList<>();
		params.add(newIdentId);
		Message message = StageActivity.messageHandler.obtainMessage(StageActivity.PERFORM_INTENT, params);
		message.sendToTarget();
	}

	private void startQueuedIntent(int intentKey) {
		if (!intentListeners.containsKey(intentKey)) {
			return;
		}
		Intent i = intentListeners.get(intentKey).getTargetIntent();
		i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getClass().getPackage().getName());
		this.startActivityForResult(i, intentKey);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Register your intent with "queueIntent"
		if (!intentListeners.containsKey(requestCode)) {
			Log.e(TAG, "Unknown intent result recieved!");
		} else {
			IntentListener asker = intentListeners.get(requestCode);
			asker.onIntentResult(resultCode, data);
			intentListeners.remove(requestCode);
		}
	}

	public interface IntentListener {
		Intent getTargetIntent();
		void onIntentResult(int resultCode, Intent data); //don't do heavy processing here
	}
}
