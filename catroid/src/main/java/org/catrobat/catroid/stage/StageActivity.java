/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.util.SparseArray;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidGraphics;
import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.devices.raspberrypi.RaspberryPiService;
import org.catrobat.catroid.io.StageAudioFocus;
import org.catrobat.catroid.nfc.NfcHandler;
import org.catrobat.catroid.ui.MarketingActivity;
import org.catrobat.catroid.ui.dialogs.StageDialog;
import org.catrobat.catroid.ui.recyclerview.dialog.PlaySceneDialog;
import org.catrobat.catroid.ui.runtimepermissions.BrickResourcesToRuntimePermissions;
import org.catrobat.catroid.ui.runtimepermissions.PermissionAdaptingActivity;
import org.catrobat.catroid.ui.runtimepermissions.PermissionHandlingActivity;
import org.catrobat.catroid.ui.runtimepermissions.PermissionRequestActivityExtension;
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask;
import org.catrobat.catroid.utils.ScreenValueHandler;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.VibrationManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.test.espresso.idling.CountingIdlingResource;

import static org.catrobat.catroid.common.Constants.SCREENSHOT_AUTOMATIC_FILE_NAME;
import static org.catrobat.catroid.stage.TestResult.TEST_RESULT_MESSAGE;
import static org.catrobat.catroid.ui.MainMenuActivity.surveyCampaign;
import static org.koin.java.KoinJavaComponent.get;

public class StageActivity extends AndroidApplication implements PermissionHandlingActivity, PermissionAdaptingActivity {

	public static final String TAG = StageActivity.class.getSimpleName();
	public static StageListener stageListener;

	public static final int REQUEST_START_STAGE = 101;

	public static final int REGISTER_INTENT = 0;
	private static final int PERFORM_INTENT = 1;
	public static final int SHOW_DIALOG = 2;
	public static final int SHOW_TOAST = 3;

	StageAudioFocus stageAudioFocus;
	PendingIntent pendingIntent;
	NfcAdapter nfcAdapter;
	private static NdefMessage nfcTagMessage;
	StageDialog stageDialog;
	BrickDialogManager brickDialogManager;
	private boolean resizePossible;

	static int numberOfSpritesCloned;

	public static Handler messageHandler;
	CameraManager cameraManager;
	public VibrationManager vibrationManager;

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

		if (surveyCampaign != null) {
			surveyCampaign.endStageTime();

			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

			if (isApplicationSentToBackground(this) || !pm.isInteractive()) {
				surveyCampaign.endAppTime(this);
			}
		}
	}

	private boolean isApplicationSentToBackground(final Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> runningProcesses = activityManager.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
			if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				for (String activeProcess : processInfo.pkgList) {
					if (activeProcess.equals(context.getPackageName())) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public void onResume() {
		StageLifeCycleController.stageResume(this);
		super.onResume();
		activeStageActivity = new WeakReference<>(this);

		if (surveyCampaign != null) {
			surveyCampaign.startAppTime(this);
			surveyCampaign.startStageTime();
		}
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
					case REGISTER_INTENT:
						currentStage.queueIntent((IntentListener) params.get(0));
						break;
					case PERFORM_INTENT:
						currentStage.startQueuedIntent((Integer) params.get(0));
						break;
					case SHOW_DIALOG:
						brickDialogManager.showDialog((BrickDialogManager.DialogType) params.get(0),
								(Action) params.get(1), (String) params.get(2));
						break;
					case SHOW_TOAST:
						showToastMessage((String) params.get(0));
						break;
					default:
						Log.e(TAG, "Unhandled message in messagehandler, case " + message.what);
				}
			}
		};
	}

	public boolean dialogIsShowing() {
		return (stageDialog.isShowing() || brickDialogManager.dialogIsShowing());
	}

	private void showToastMessage(String message) {
		ToastUtil.showError(this, message);
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
			BluetoothDeviceService service = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);
			if (service != null) {
				service.disconnectDevices();
			}

			TextToSpeechHolder.getInstance().deleteSpeechFiles();
			Intent marketingIntent = new Intent(this, MarketingActivity.class);
			startActivity(marketingIntent);
			finish();
		} else {
			StageLifeCycleController.stagePause(this);
			idlingResource.increment();
			stageListener.requestTakingScreenshot(SCREENSHOT_AUTOMATIC_FILE_NAME,
					success -> runOnUiThread(() -> idlingResource.decrement()));
			stageDialog.show();
		}
	}

	public void manageLoadAndFinish() {
		stageListener.pause();
		stageListener.finish();

		TextToSpeechHolder.getInstance().shutDownTextToSpeech();
		get(SpeechRecognitionHolderFactory.class).getInstance().destroy();

		BluetoothDeviceService service = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);
		if (service != null) {
			service.pause();
		}

		RaspberryPiService.getInstance().disconnect();
	}

	public static CameraManager getActiveCameraManager() {
		if (activeStageActivity != null) {
			return activeStageActivity.get().cameraManager;
		}
		return null;
	}

	public static VibrationManager getActiveVibrationManager() {
		if (activeStageActivity != null) {
			return activeStageActivity.get().vibrationManager;
		}
		return null;
	}

	public boolean getResizePossible() {
		return resizePossible;
	}

	void calculateScreenSizes() {
		ScreenValueHandler.updateScreenWidthAndHeight(getContext());
		int virtualScreenWidth = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenWidth;
		int virtualScreenHeight = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenHeight;

		if (virtualScreenHeight > virtualScreenWidth && isInLandscapeMode()
				|| virtualScreenHeight < virtualScreenWidth && isInPortraitMode()) {
			swapWidthAndHeight();
		}

		float aspectRatio = (float) virtualScreenWidth / (float) virtualScreenHeight;
		float screenAspectRatio = ScreenValues.getAspectRatio();

		if ((virtualScreenWidth == ScreenValues.SCREEN_WIDTH && virtualScreenHeight == ScreenValues.SCREEN_HEIGHT)
				|| Float.compare(screenAspectRatio, aspectRatio) == 0
				|| ProjectManager.getInstance().getCurrentProject().isCastProject()) {
			resizePossible = false;
			stageListener.maxViewPortWidth = ScreenValues.SCREEN_WIDTH;
			stageListener.maxViewPortHeight = ScreenValues.SCREEN_HEIGHT;
			return;
		}

		resizePossible = true;

		float ratioHeight = (float) ScreenValues.SCREEN_HEIGHT / (float) virtualScreenHeight;
		float ratioWidth = (float) ScreenValues.SCREEN_WIDTH / (float) virtualScreenWidth;

		if (aspectRatio < screenAspectRatio) {
			float scale = ratioHeight / ratioWidth;
			stageListener.maxViewPortWidth = (int) (ScreenValues.SCREEN_WIDTH * scale);
			stageListener.maxViewPortX = (int) ((ScreenValues.SCREEN_WIDTH - stageListener.maxViewPortWidth) / 2f);
			stageListener.maxViewPortHeight = ScreenValues.SCREEN_HEIGHT;
		} else if (aspectRatio > screenAspectRatio) {
			float scale = ratioWidth / ratioHeight;
			stageListener.maxViewPortHeight = (int) (ScreenValues.SCREEN_HEIGHT * scale);
			stageListener.maxViewPortY = (int) ((ScreenValues.SCREEN_HEIGHT - stageListener.maxViewPortHeight) / 2f);
			stageListener.maxViewPortWidth = ScreenValues.SCREEN_WIDTH;
		}
	}

	private boolean isInPortraitMode() {
		return ScreenValues.SCREEN_WIDTH < ScreenValues.SCREEN_HEIGHT;
	}

	private boolean isInLandscapeMode() {
		return !isInPortraitMode();
	}

	private void swapWidthAndHeight() {
		int tmp = ScreenValues.SCREEN_HEIGHT;
		ScreenValues.SCREEN_HEIGHT = ScreenValues.SCREEN_WIDTH;
		ScreenValues.SCREEN_WIDTH = tmp;
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
		Intent queuedIntent = intentListeners.get(intentKey).getTargetIntent();
		if (queuedIntent == null) {
			return;
		}
		Package pack = this.getClass().getPackage();
		if (pack != null) {
			queuedIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, pack.getName());
		}
		this.startActivityForResult(queuedIntent, intentKey);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == TestResult.STAGE_ACTIVITY_TEST_SUCCESS
				|| resultCode == TestResult.STAGE_ACTIVITY_TEST_FAIL) {
			String message = data.getStringExtra(TEST_RESULT_MESSAGE);
			ToastUtil.showError(this, message);
			ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			ClipData testResult = ClipData.newPlainText("TestResult",
					ProjectManager.getInstance().getCurrentProject().getName() + "\n" + message);
			clipboard.setPrimaryClip(testResult);
		}

		if (intentListeners.indexOfKey(requestCode) >= 0) {
			IntentListener asker = intentListeners.get(requestCode);
			if (data != null) {
				asker.onIntentResult(resultCode, data);
			}
			intentListeners.remove(requestCode);
		} else {
			stageResourceHolder.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void adaptToDeniedPermissions(List<String> deniedPermissions) {
		Brick.ResourcesSet requiredResources = new Brick.ResourcesSet();
		Project project = ProjectManager.getInstance().getCurrentProject();

		for (Scene scene: project.getSceneList()) {
			for (Sprite sprite : scene.getSpriteList()) {
				for (Brick brick : sprite.getAllBricks()) {
					brick.addRequiredResources(requiredResources);
					List<String> requiredPermissions = BrickResourcesToRuntimePermissions.translate(requiredResources);
					requiredPermissions.retainAll(deniedPermissions);

					if (!requiredPermissions.isEmpty()) {
						brick.setCommentedOut(true);
					}
					requiredResources.clear();
				}
			}
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
					.setPositiveButton(R.string.play, (dialog, which) -> startStageActivity(activity))
					.create()
					.show();
		}
	}

	private static void startStageActivity(Activity activity) {
		Intent intent = new Intent(activity, StageActivity.class);
		activity.startActivityForResult(intent, StageActivity.REQUEST_START_STAGE);
	}

	public static void finishStage() {
		StageActivity stageActivity = StageActivity.activeStageActivity.get();
		if (stageActivity != null && !stageActivity.isFinishing()) {
			stageActivity.finish();
		}
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
