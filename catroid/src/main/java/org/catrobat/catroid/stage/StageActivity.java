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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.EditText;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidGraphics;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.actions.AskAction;
import org.catrobat.catroid.devices.raspberrypi.RaspberryPiService;
import org.catrobat.catroid.drone.jumpingsumo.JumpingSumoDeviceController;
import org.catrobat.catroid.drone.jumpingsumo.JumpingSumoInitializer;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.io.StageAudioFocus;
import org.catrobat.catroid.nfc.NfcHandler;
import org.catrobat.catroid.ui.MarketingActivity;
import org.catrobat.catroid.ui.dialogs.StageDialog;
import org.catrobat.catroid.ui.recyclerview.dialog.PlaySceneDialog;
import org.catrobat.catroid.ui.runtimepermissions.PermissionHandlingActivity;
import org.catrobat.catroid.ui.runtimepermissions.PermissionRequestActivityExtension;
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask;
import org.catrobat.catroid.utils.FlashUtil;
import org.catrobat.catroid.utils.ScreenValueHandler;
import org.catrobat.catroid.utils.VibrationUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.test.espresso.idling.CountingIdlingResource;

import static org.catrobat.catroid.stage.StageListener.SCREENSHOT_AUTOMATIC_FILE_NAME;
import static org.catrobat.catroid.stage.TestResult.TEST_RESULT_MESSAGE;

public class StageActivity extends AndroidApplication implements PermissionHandlingActivity {

	public static final String TAG = StageActivity.class.getSimpleName();
	public static StageListener stageListener;

	public static final int REQUEST_START_STAGE = 101;

	public static final int ASK_MESSAGE = 0;
	public static final int REGISTER_INTENT = 1;
	private static final int PERFORM_INTENT = 2;

	StageAudioFocus stageAudioFocus;
	PendingIntent pendingIntent;
	NfcAdapter nfcAdapter;
	private static NdefMessage nfcTagMessage;
	StageDialog stageDialog;
	AlertDialog askDialog;
	private boolean resizePossible;

	static int numberOfSpritesCloned;

	public static Handler messageHandler;
	JumpingSumoDeviceController jumpingSumoDeviceController;

	public static SparseArray<IntentListener> intentListeners = new SparseArray<>();
	public static Random randomGenerator = new Random();

	AndroidApplicationConfiguration configuration = null;

	public StageResourceHolder stageResourceHolder;
	public CountingIdlingResource idlingResource = new CountingIdlingResource("StageActivity");
	private PermissionRequestActivityExtension permissionRequestActivityExtension = new PermissionRequestActivityExtension();
	public static WeakReference<StageActivity> activeStageActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StageLifeCycleController.stageCreate(this);
		activeStageActivity = new WeakReference<>(this);
	}

	@Override
	public void onPause() {
		StageLifeCycleController.stagePause(this);
		super.onPause();
	}

	@Override
	public void onResume() {
		StageLifeCycleController.stageResume(this);
		super.onResume();
		activeStageActivity = new WeakReference<>(this);
	}

	@Override
	protected void onDestroy() {
		if (ProjectManager.getInstance().getCurrentProject() != null) {
			StageLifeCycleController.stageDestroy(this);
		}
		super.onDestroy();
	}

	AndroidGraphics getGdxGraphics() {
		return graphics;
	}

	void setupAskHandler() {
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
		StageLifeCycleController.stagePause(this);

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog));
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
			}
		});

		alertBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				askDialog = null;
				StageLifeCycleController.stageResume(StageActivity.this);
			}
		});

		askDialog = alertBuilder.create();
		askDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		askDialog.show();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		NfcHandler.processIntent(intent);

		if (nfcTagMessage != null) {
			Tag currentTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			synchronized (StageActivity.class) {
				NfcHandler.writeTag(currentTag, nfcTagMessage);
				setNfcTagMessage(null);
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (BuildConfig.FEATURE_APK_GENERATOR_ENABLED) {
			ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).disconnectDevices();

			TextToSpeechHolder.getInstance().deleteSpeechFiles();
			if (FlashUtil.isAvailable()) {
				FlashUtil.destroy();
			}
			if (VibrationUtil.isActive()) {
				VibrationUtil.destroy();
			}
			Intent marketingIntent = new Intent(this, MarketingActivity.class);
			startActivity(marketingIntent);
			finish();
		} else {
			StageLifeCycleController.stagePause(this);
			idlingResource.increment();
			stageListener.requestTakingScreenshot(SCREENSHOT_AUTOMATIC_FILE_NAME,
					success -> runOnUiThread(() -> {
						stageDialog.show();
						idlingResource.decrement();
					}));
		}
	}

	public void manageLoadAndFinish() {
		stageListener.pause();
		stageListener.finish();

		TextToSpeechHolder.getInstance().shutDownTextToSpeech();

		ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).pause();

		if (FaceDetectionHandler.isFaceDetectionRunning()) {
			FaceDetectionHandler.stopFaceDetection();
		}

		if (VibrationUtil.isActive()) {
			VibrationUtil.pauseVibration();
		}

		RaspberryPiService.getInstance().disconnect();
	}

	public boolean jumpingSumoDisconnect() {
		boolean success;
		if (jumpingSumoDeviceController != null && !jumpingSumoDeviceController.isConnected()) {
			return true;
		}
		success = JumpingSumoInitializer.getInstance().disconnect();
		return success;
	}

	public boolean getResizePossible() {
		return resizePossible;
	}

	void calculateScreenSizes() {
		ScreenValueHandler.updateScreenWidthAndHeight(getContext());
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
				|| Float.compare(screenAspectRatio, aspectRatio) == 0
				|| ProjectManager.getInstance().getCurrentProject().isCastProject()) {
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

	public void jsDestroy() {
		stageListener.finish();
		manageLoadAndFinish();
		exit();
	}

	public static int getAndIncrementNumberOfClonedSprites() {
		return ++numberOfSpritesCloned;
	}

	public static void resetNumberOfClonedSprites() {
		numberOfSpritesCloned = 0;
	}

	public static void setNfcTagMessage(NdefMessage message) {
		nfcTagMessage = message;
	}

	public static NdefMessage getNfcTagMessage() {
		return nfcTagMessage;
	}

	public synchronized void queueIntent(IntentListener asker) {
		if (StageActivity.messageHandler == null) {
			return;
		}
		int newIdentId;
		do {
			newIdentId = StageActivity.randomGenerator.nextInt(Integer.MAX_VALUE);
		} while (intentListeners.indexOfKey(newIdentId) >= 0);

		intentListeners.put(newIdentId, asker);
		ArrayList<Object> params = new ArrayList<>();
		params.add(newIdentId);
		Message message = StageActivity.messageHandler.obtainMessage(StageActivity.PERFORM_INTENT, params);
		message.sendToTarget();
	}

	private void startQueuedIntent(int intentKey) {
		if (intentListeners.indexOfKey(intentKey) < 0) {
			return;
		}
		Intent i = intentListeners.get(intentKey).getTargetIntent();
		i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getClass().getPackage().getName());
		this.startActivityForResult(i, intentKey);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Register your intent with "queueIntent"
		if (intentListeners.indexOfKey(requestCode) >= 0) {
			IntentListener asker = intentListeners.get(requestCode);
			asker.onIntentResult(resultCode, data);
			intentListeners.remove(requestCode);
		} else {
			stageResourceHolder.onActivityResult(requestCode, resultCode, data);
		}
	}

	public interface IntentListener {
		Intent getTargetIntent();
		void onIntentResult(int resultCode, Intent data); //don't do heavy processing here
	}

	@Override
	public void addToRequiresPermissionTaskList(RequiresPermissionTask task) {
		permissionRequestActivityExtension.addToRequiresPermissionTaskList(task);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		permissionRequestActivityExtension.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
	}

	public static void handlePlayButton(ProjectManager projectManager, final Activity activity) {
		Scene currentScene = projectManager.getCurrentlyEditedScene();
		Scene defaultScene = projectManager.getCurrentProject().getDefaultScene();

		if (currentScene.getName().equals(defaultScene.getName())) {
			projectManager.setCurrentlyPlayingScene(defaultScene);
			projectManager.setStartScene(defaultScene);
			startStageActivity(activity);
		} else {
			new PlaySceneDialog.Builder(activity)
					.setPositiveButton(R.string.play, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startStageActivity(activity);
						}
					})
					.create()
					.show();
		}
	}

	private static void startStageActivity(Activity activity) {
		Intent intent = new Intent(activity, StageActivity.class);
		activity.startActivityForResult(intent, StageActivity.REQUEST_START_STAGE);
	}

	public static void finishTestWithResult(TestResult testResult) {
		StageActivity stageActivity = StageActivity.activeStageActivity.get();
		if (stageActivity != null && !stageActivity.isFinishing()) {
			Intent resultIntent = new Intent();
			resultIntent.putExtra(TEST_RESULT_MESSAGE, testResult.getMessage());
			stageActivity.setResult(testResult.getResultCode(), resultIntent);
			stageActivity.finish();
		}
	}
}
