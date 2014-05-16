/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.stage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.parrot.freeflight.receivers.DroneAvailabilityDelegate;
import com.parrot.freeflight.receivers.DroneAvailabilityReceiver;
import com.parrot.freeflight.receivers.DroneBatteryChangedReceiver;
import com.parrot.freeflight.receivers.DroneBatteryChangedReceiverDelegate;
import com.parrot.freeflight.receivers.DroneConnectionChangeReceiverDelegate;
import com.parrot.freeflight.receivers.DroneConnectionChangedReceiver;
import com.parrot.freeflight.receivers.DroneReadyReceiver;
import com.parrot.freeflight.receivers.DroneReadyReceiverDelegate;
import com.parrot.freeflight.service.DroneControlService;
import com.parrot.freeflight.service.intents.DroneStateManager;
import com.parrot.freeflight.tasks.CheckDroneNetworkAvailabilityTask;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.bluetooth.BluetoothManager;
import org.catrobat.catroid.bluetooth.DeviceListActivity;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.legonxt.LegoNXT;
import org.catrobat.catroid.legonxt.LegoNXTBtCommunicator;
import org.catrobat.catroid.ui.BaseActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.TermsOfUseDialogFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

@SuppressWarnings("deprecation")
public class PreStageActivity extends BaseActivity implements DroneReadyReceiverDelegate,
		DroneConnectionChangeReceiverDelegate, DroneAvailabilityDelegate, DroneBatteryChangedReceiverDelegate {

	private static final String TAG = PreStageActivity.class.getSimpleName();
	private static final int REQUEST_ENABLE_BLUETOOTH = 2000;
	private static final int REQUEST_CONNECT_DEVICE = 1000;
	public static final int REQUEST_RESOURCES_INIT = 101;
	public static final int REQUEST_TEXT_TO_SPEECH = 10;

	public static final String INIT_DRONE_STRING_EXTRA = "STRING_EXTRA_INIT_DRONE";
	private static final int DRONE_BATTERY_TRESHOLD = 5;

	private int resources = Brick.NO_RESOURCES;
	private int requiredResourceCounter;

	private static LegoNXT legoNXT;
	private boolean autoConnect = false;
	private ProgressDialog connectingProgressDialog;
	private static TextToSpeech textToSpeech;
	private static OnUtteranceCompletedListenerContainer onUtteranceCompletedListenerContainer;

	private DroneControlService droneControlService = null;
	private BroadcastReceiver droneReadyReceiver = null;
	private BroadcastReceiver droneStateReceiver = null;
	private int droneBatteryCharge = 0;
	private DroneBatteryChangedReceiver droneBatteryReceiver;
	private CheckDroneNetworkAvailabilityTask checkDroneConnectionTask;
	private DroneConnectionChangedReceiver droneConnectionChangeReceiver;

	private Intent returnToActivityIntent = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		returnToActivityIntent = new Intent();

		if (isFinishing()) {
			return;
		}

		setContentView(R.layout.activity_prestage);

		int requiredResources = getRequiredRessources();
		requiredResourceCounter = Integer.bitCount(requiredResources);

		if ((requiredResources & Brick.TEXT_TO_SPEECH) > 0) {
			Intent checkIntent = new Intent();
			checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
			startActivityForResult(checkIntent, REQUEST_TEXT_TO_SPEECH);
		}

		if ((requiredResources & Brick.BLUETOOTH_LEGO_NXT) > 0) {
			BluetoothManager bluetoothManager = new BluetoothManager(this);

			int bluetoothState = bluetoothManager.activateBluetooth();
			if (bluetoothState == BluetoothManager.BLUETOOTH_NOT_SUPPORTED) {

				Toast.makeText(PreStageActivity.this, R.string.notification_blueth_err, Toast.LENGTH_LONG).show();
				resourceFailed();
			} else if (bluetoothState == BluetoothManager.BLUETOOTH_ALREADY_ON) {
				if (legoNXT == null) {
					startBluetoothCommunication(true);
				} else {
					resourceInitialized();
				}
			}
		}

		if ((requiredResources & Brick.ARDRONE_SUPPORT) > 0) {
			if (SettingsActivity.areTermsOfSericeAgreedPermanently(getApplicationContext())) {
				initialiseDrone();
			} else {
				Bundle args = new Bundle();
				args.putBoolean(TermsOfUseDialogFragment.DIALOG_ARGUMENT_TERMS_OF_USE_ACCEPT, true);
				TermsOfUseDialogFragment termsOfUseDialog = new TermsOfUseDialogFragment();
				termsOfUseDialog.setArguments(args);
				termsOfUseDialog.show(getSupportFragmentManager(), TermsOfUseDialogFragment.DIALOG_FRAGMENT_TAG);
			}
		}

		if (requiredResourceCounter == Brick.NO_RESOURCES) {
			startStage();
		}
	}

	public void initialiseDrone() {
		if (!BuildConfig.DEBUG) {
			Log.d(TAG, "drone is not available in release build");
			showUncancelableErrorDialog(this, getString(R.string.error_drone_not_available_in_release_build_title),
					getString(R.string.error_drone_not_available_in_release_build));
			return;
		}

		if (!CatroidApplication.OS_ARCH.startsWith("arm")) {
			Log.d(TAG, "problem, we are on arm");
			showUncancelableErrorDialog(this, getString(R.string.error_drone_wrong_platform_title),
					getString(R.string.error_drone_wrong_platform));
			return;
		}

		if (!CatroidApplication.parrotNativeLibsAlreadyLoadedOrLoadingWasSucessful()) {
			showUncancelableErrorDialog(this, getString(R.string.error_drone_wrong_platform_title),
					getString(R.string.error_drone_wrong_platform));
			return;
		}

		Log.d(TAG, "Adding drone support!");
		returnToActivityIntent.putExtra(INIT_DRONE_STRING_EXTRA, true);

		checkDroneConnectivity();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (requiredResourceCounter == 0) {
			finish();
		}

		if (BuildConfig.DEBUG) {
			droneReadyReceiver = new DroneReadyReceiver(this);
			droneStateReceiver = new DroneAvailabilityReceiver(this);
			droneBatteryReceiver = new DroneBatteryChangedReceiver(this);
			droneConnectionChangeReceiver = new DroneConnectionChangedReceiver(this);

			LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getApplicationContext());
			manager.registerReceiver(droneBatteryReceiver, new IntentFilter(
					DroneControlService.DRONE_BATTERY_CHANGED_ACTION));
			manager.registerReceiver(droneReadyReceiver, new IntentFilter(DroneControlService.DRONE_STATE_READY_ACTION));
			manager.registerReceiver(droneConnectionChangeReceiver, new IntentFilter(
					DroneControlService.DRONE_CONNECTION_CHANGED_ACTION));
			manager.registerReceiver(droneStateReceiver, new IntentFilter(DroneStateManager.ACTION_DRONE_STATE_CHANGED));
		}
	}

	@SuppressLint("NewApi")
	private void checkDroneConnectivity() {
		if (checkDroneConnectionTask != null && checkDroneConnectionTask.getStatus() != Status.FINISHED) {
			checkDroneConnectionTask.cancel(true);
		}

		checkDroneConnectionTask = new CheckDroneNetworkAvailabilityTask() {
			@Override
			protected void onPostExecute(Boolean result) {
				onDroneAvailabilityChanged(result);
			}
		};

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			checkDroneConnectionTask.executeOnExecutor(CheckDroneNetworkAvailabilityTask.THREAD_POOL_EXECUTOR, this);
		} else {
			checkDroneConnectionTask.execute(this);
		}
	}

	@Override
	protected void onPause() {
		if (BuildConfig.DEBUG) {
			if (droneControlService != null) {
				droneControlService.pause();
			}

			LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getApplicationContext());
			manager.unregisterReceiver(droneReadyReceiver);
			manager.unregisterReceiver(droneConnectionChangeReceiver);
			manager.unregisterReceiver(droneStateReceiver);
			manager.unregisterReceiver(droneBatteryReceiver);

			if (taskRunning(checkDroneConnectionTask)) {
				checkDroneConnectionTask.cancelAnyFtpOperation();
			}
		}
		super.onPause();
	}

	private boolean taskRunning(AsyncTask<?, ?, ?> checkMediaTask2) {

		if (checkMediaTask2 == null || checkMediaTask2.getStatus() == Status.FINISHED) {
			return false;
		}

		return true;
	}

	@Override
	protected void onDestroy() {
		if (droneControlService != null) {
			unbindService(this.droneServiceConnection);
		}
		super.onDestroy();
	}

	//all resources that should be reinitialized with every stage start
	public static void shutdownResources() {
		if (textToSpeech != null) {
			textToSpeech.stop();
			textToSpeech.shutdown();
		}
		if (legoNXT != null) {
			legoNXT.pauseCommunicator();
		}
	}

	//all resources that should not have to be reinitialized every stage start
	public static void shutdownPersistentResources() {
		if (legoNXT != null) {
			legoNXT.destroyCommunicator();
			legoNXT = null;
		}
		deleteSpeechFiles();
	}

	private static void deleteSpeechFiles() {
		File pathToSpeechFiles = new File(Constants.TEXT_TO_SPEECH_TMP_PATH);
		if (pathToSpeechFiles.isDirectory()) {
			for (File file : pathToSpeechFiles.listFiles()) {
				file.delete();
			}
		}
	}

	private void resourceFailed() {
		setResult(RESULT_CANCELED, returnToActivityIntent);
		finish();
	}

	private synchronized void resourceInitialized() {
		requiredResourceCounter--;
		if (requiredResourceCounter == 0) {
			Log.d(TAG, "Start Stage");

			startStage();
		}
	}

	public void startStage() {
		setResult(RESULT_OK, returnToActivityIntent);
		finish();
	}

	private void startBluetoothCommunication(boolean autoConnect) {
		connectingProgressDialog = ProgressDialog.show(this, "",
				getResources().getString(R.string.connecting_please_wait), true);
		Intent serverIntent = new Intent(this, DeviceListActivity.class);
		serverIntent.putExtra(DeviceListActivity.AUTO_CONNECT, autoConnect);
		this.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	}

	private int getRequiredRessources() {
		ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject()
				.getSpriteList();

		resources = Brick.NO_RESOURCES;
		for (Sprite sprite : spriteList) {
			resources |= sprite.getRequiredResources();
		}
		return resources;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("bt", "requestcode " + requestCode + " result code" + resultCode);

		switch (requestCode) {
			case REQUEST_ENABLE_BLUETOOTH:
				switch (resultCode) {
					case Activity.RESULT_OK:
						startBluetoothCommunication(true);
						break;
					case Activity.RESULT_CANCELED:
						Toast.makeText(PreStageActivity.this, R.string.notification_blueth_err, Toast.LENGTH_LONG)
								.show();
						resourceFailed();
						break;
				}
				break;

			case REQUEST_CONNECT_DEVICE:
				switch (resultCode) {
					case Activity.RESULT_OK:
						legoNXT = new LegoNXT(this, recieveHandler);
						String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
						autoConnect = data.getExtras().getBoolean(DeviceListActivity.AUTO_CONNECT);
						legoNXT.startBTCommunicator(address);
						break;

					case Activity.RESULT_CANCELED:
						connectingProgressDialog.dismiss();
						Toast.makeText(PreStageActivity.this, R.string.bt_connection_failed, Toast.LENGTH_LONG).show();
						resourceFailed();
						break;
				}
				break;

			case REQUEST_TEXT_TO_SPEECH:
				if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
					textToSpeech = new TextToSpeech(getApplicationContext(), new OnInitListener() {
						@Override
						public void onInit(int status) {
							onUtteranceCompletedListenerContainer = new OnUtteranceCompletedListenerContainer();
							textToSpeech.setOnUtteranceCompletedListener(onUtteranceCompletedListenerContainer);
							resourceInitialized();
							if (status == TextToSpeech.ERROR) {
								Toast.makeText(PreStageActivity.this,
										"Error occurred while initializing Text-To-Speech engine", Toast.LENGTH_LONG)
										.show();
								resourceFailed();
							}
						}
					});
					if (textToSpeech.isLanguageAvailable(Locale.getDefault()) == TextToSpeech.LANG_MISSING_DATA) {
						Intent installIntent = new Intent();
						installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
						startActivity(installIntent);
						resourceFailed();
					}
				} else {
					AlertDialog.Builder builder = new CustomAlertDialogBuilder(this);
					builder.setMessage(R.string.text_to_speech_engine_not_installed).setCancelable(false)
							.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int id) {
									Intent installIntent = new Intent();
									installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
									startActivity(installIntent);
									resourceFailed();
								}
							}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
									resourceFailed();
								}
							});
					AlertDialog alert = builder.create();
					alert.show();
				}
				break;
			default:
				resourceFailed();
				break;
		}
	}

	public static void textToSpeech(String text, File speechFile, OnUtteranceCompletedListener listener,
			HashMap<String, String> speakParameter) {
		if (text == null) {
			text = "";
		}

		if (onUtteranceCompletedListenerContainer.addOnUtteranceCompletedListener(speechFile, listener,
				speakParameter.get(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID))) {
			int status = textToSpeech.synthesizeToFile(text, speakParameter, speechFile.getAbsolutePath());
			if (status == TextToSpeech.ERROR) {
				Log.e(TAG, "File synthesizing failed");
			}
		}
	}

	//messages from Lego NXT device can be handled here
	// TODO should be fixed - could lead to problems
	@SuppressLint("HandlerLeak")
	final Handler recieveHandler = new Handler() {
		@Override
		public void handleMessage(Message myMessage) {

			Log.i("bt", "message" + myMessage.getData().getInt("message"));
			switch (myMessage.getData().getInt("message")) {
				case LegoNXTBtCommunicator.STATE_CONNECTED:
					//autoConnect = false;
					connectingProgressDialog.dismiss();
					resourceInitialized();
					break;
				case LegoNXTBtCommunicator.STATE_CONNECTERROR:
					Toast.makeText(PreStageActivity.this, R.string.bt_connection_failed, Toast.LENGTH_SHORT).show();
					connectingProgressDialog.dismiss();
					legoNXT.destroyCommunicator();
					legoNXT = null;
					if (autoConnect) {
						startBluetoothCommunication(false);
					} else {
						resourceFailed();
					}
					break;
				default:
					return;
			}
		}
	};

	private void onDroneServiceConnected(IBinder service) {
		Log.d(TAG, "onDroneServiceConnected");
		droneControlService = ((DroneControlService.LocalBinder) service).getService();

		droneControlService.resume();
		droneControlService.requestDroneStatus();
	}

	private ServiceConnection droneServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			onDroneServiceConnected(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			droneControlService = null; //nothing else to do here

		}
	};

	public static void addDroneSupportExtraToNewIntentIfPresentInOldIntent(Intent oldIntent, Intent newIntent) {
		if (newIntent == null || oldIntent == null) {
			return;
		}

		Boolean isDroneRequired = oldIntent.getBooleanExtra(INIT_DRONE_STRING_EXTRA, false);
		Log.d(TAG, "Extra STRING_EXTRA_INIT_DRONE=" + isDroneRequired.toString());
		newIntent.putExtra(INIT_DRONE_STRING_EXTRA, isDroneRequired);
	}

	@Override
	public void onDroneReady() {
		Log.d(TAG, "onDroneReady -> check battery -> go to stage");
		if (droneBatteryCharge < DRONE_BATTERY_TRESHOLD) {
			String dialogTitle = String.format(getString(R.string.error_drone_low_battery_title), droneBatteryCharge);
			showUncancelableErrorDialog(this, dialogTitle, getString(R.string.error_drone_low_battery));
			return;
		}
		resourceInitialized();
	}

	@Override
	public void onDroneConnected() {
		// We still waiting for onDroneReady event
		Log.d(TAG, "onDroneConnected, requesting Config update and wait for drone ready.");
		droneControlService.requestConfigUpdate();
	}

	@Override
	public void onDroneDisconnected() {
		//nothing to do
	}

	@Override
	public void onDroneAvailabilityChanged(boolean isDroneOnNetwork) {
		// Here we know that the drone is on the network
		Log.d(TAG, "Drone availability  = " + isDroneOnNetwork);
		if (isDroneOnNetwork) {
			Intent startService = new Intent(this, DroneControlService.class);

			Object obj = startService(startService);

			boolean isSuccessful = bindService(new Intent(this, DroneControlService.class),
					this.droneServiceConnection, Context.BIND_AUTO_CREATE);
			// TODO Drone: Condition has no effect, even drone is not connected a connection will be "established"
			if (obj == null || !isSuccessful) {
				resourceFailed();
			}
		} else {
			showUncancelableErrorDialog(this, getString(R.string.error_no_drone_connected_title),
					getString(R.string.error_no_drone_connected));
		}
	}

	public static void showUncancelableErrorDialog(final PreStageActivity context, String title, String message) {
		Builder builder = new CustomAlertDialogBuilder(context);

		builder.setTitle(title);
		builder.setCancelable(false);
		builder.setMessage(message);
		builder.setNeutralButton(R.string.close, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//TODO Drone: shut down nicely all resources and go back to the activity we came from
				context.resourceFailed();
			}
		});
		builder.show();
	}

	@Override
	public void onDroneBatteryChanged(int value) {
		Log.d(TAG, "Drone Battery Status =" + Integer.toString(value));
		droneBatteryCharge = value;
	}
}
