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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import android.content.IntentFilter;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.drone.DroneConnectToWifi;
import org.catrobat.catroid.drone.DroneInitializer;
import org.catrobat.catroid.drone.DroneServiceWrapper;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.ui.BaseActivity;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.utils.LedUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.VibratorUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@SuppressWarnings("deprecation")
public class PreStageActivity extends BaseActivity {

    private static final String TAG = PreStageActivity.class.getSimpleName();
    private static final int REQUEST_CONNECT_DEVICE = 1000;
    public static final int REQUEST_RESOURCES_INIT = 101;
    public static final int REQUEST_TEXT_TO_SPEECH = 10;

    private int requiredResourceCounter;

    private static TextToSpeech textToSpeech;
    private static OnUtteranceCompletedListenerContainer onUtteranceCompletedListenerContainer;

    private DroneInitializer droneInitializer = null;

    private Intent returnToActivityIntent = null;
    private WifiScanReceiver wifiReciever;
    private WifiManager wifi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        returnToActivityIntent = new Intent();

        if (isFinishing()) {
            return;
        }

        setContentView(R.layout.activity_prestage);

        int requiredResources = ProjectManager.getInstance().getCurrentProject().getRequiredResources();
        requiredResourceCounter = Integer.bitCount(requiredResources);


        if ((requiredResources & Brick.TEXT_TO_SPEECH) > 0) {
            Intent checkIntent = new Intent();
            checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(checkIntent, REQUEST_TEXT_TO_SPEECH);
        }

        if ((requiredResources & Brick.BLUETOOTH_LEGO_NXT) > 0) {
            connectBTDevice(BluetoothDevice.LEGO_NXT);
        }

        if ((requiredResources & Brick.BLUETOOTH_PHIRO) > 0) {
            connectBTDevice(BluetoothDevice.PHIRO);
        }

        if (DroneServiceWrapper.checkARDroneAvailability()) {

            wifi = (WifiManager) getSystemService(getBaseContext().WIFI_SERVICE);
            wifiReciever = new WifiScanReceiver();
            registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

            wifi.setWifiEnabled(true);
            wifi.startScan();

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
            registerReceiver(wifiReciever, intentFilter);

            /*
            CatroidApplication.loadNativeLibs();
            if (CatroidApplication.parrotLibrariesLoaded) {
                droneInitializer = getDroneInitialiser();
                droneInitializer.initialise();
            }
            */
        }

        FaceDetectionHandler.resetFaceDedection();
        if ((requiredResources & Brick.FACE_DETECTION) > 0) {
            boolean success = FaceDetectionHandler.startFaceDetection(this);
            if (success) {
                resourceInitialized();
            } else {
                resourceFailed();
            }
        }

        if ((requiredResources & Brick.CAMERA_LED) > 0) {
            if (!CameraManager.getInstance().isFacingBack()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.led_and_front_camera_warning)).setCancelable(false)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                ledInitialize();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                ledInitialize();
            }
        }

        if ((requiredResources & Brick.VIBRATOR) > 0) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (vibrator != null) {
                requiredResourceCounter--;
                VibratorUtil.setContext(this.getBaseContext());
                VibratorUtil.activateVibratorThread();
            } else {
                ToastUtil.showError(PreStageActivity.this, R.string.no_vibrator_available);
                resourceFailed();
            }
        }

        if (requiredResourceCounter == Brick.NO_RESOURCES) {
            startStage();
        }
    }

    private void connectBTDevice(Class<? extends BluetoothDevice> service) {
        BluetoothDeviceService btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);

        if (btService.connectDevice(service, this, REQUEST_CONNECT_DEVICE)
                == BluetoothDeviceService.ConnectDeviceResult.ALREADY_CONNECTED) {
            resourceInitialized();
        }
    }

    public DroneInitializer getDroneInitialiser() {
        if (droneInitializer == null) {
            droneInitializer = new DroneInitializer(this);
        }
        return droneInitializer;
    }

    protected boolean hasFlash() {
        boolean hasCamera = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
        boolean hasLed = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasCamera || !hasLed) {
            return false;
        }

        Camera camera = CameraManager.getInstance().getCamera();

        try {
            if (camera == null) {
                camera = CameraManager.getInstance().getCamera();
            }
        } catch (Exception exception) {
            Log.e(TAG, "failed to open Camera", exception);
        }

        if (camera == null) {
            return false;
        }

        Camera.Parameters parameters = camera.getParameters();

        if (parameters.getFlashMode() == null) {
            return false;
        }

        List<String> supportedFlashModes = parameters.getSupportedFlashModes();
        if (supportedFlashModes == null || supportedFlashModes.isEmpty() ||
                supportedFlashModes.size() == 1 && supportedFlashModes.get(0).equals(Camera.Parameters.FLASH_MODE_OFF)) {
            return false;
        }

        return true;
    }

    @Override
    public void onResume() {
        if (droneInitializer != null) {
            droneInitializer.onPrestageActivityResume();
        }
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        super.onResume();
        if (requiredResourceCounter == 0) {
            finish();
        }
    }

    @Override
    protected void onPause() {
        if (droneInitializer != null) {
            droneInitializer.onPrestageActivityPause();
        }
        unregisterReceiver(wifiReciever);

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (droneInitializer != null) {
            droneInitializer.onPrestageActivityDestroy();
        }

        super.onDestroy();
    }

    //all resources that should be reinitialized with every stage start
    public static void shutdownResources() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).pause();

        if (FaceDetectionHandler.isFaceDetectionRunning()) {
            FaceDetectionHandler.stopFaceDetection();
        }
    }

    //all resources that should not have to be reinitialized every stage start
    public static void shutdownPersistentResources() {

        ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).disconnectDevices();

        deleteSpeechFiles();
        if (LedUtil.isActive()) {
            LedUtil.destroy();
        }
        if (VibratorUtil.isActive()) {
            VibratorUtil.destroy();
        }
    }

    private static void deleteSpeechFiles() {
        File pathToSpeechFiles = new File(Constants.TEXT_TO_SPEECH_TMP_PATH);
        if (pathToSpeechFiles.isDirectory()) {
            for (File file : pathToSpeechFiles.listFiles()) {
                file.delete();
            }
        }
    }

    public void resourceFailed() {
        setResult(RESULT_CANCELED, returnToActivityIntent);
        finish();
    }

    public synchronized void resourceInitialized() {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("bt", "requestcode " + requestCode + " result code" + resultCode);

        switch (requestCode) {

            case REQUEST_CONNECT_DEVICE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        resourceInitialized();
                        break;

                    case Activity.RESULT_CANCELED:
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
                                ToastUtil.showError(PreStageActivity.this, "Error occurred while initializing Text-To-Speech engine");
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

    private void ledInitialize() {
        if (hasFlash()) {
            resourceInitialized();
            LedUtil.activateLedThread();
        } else {
            ToastUtil.showError(PreStageActivity.this, R.string.no_flash_led_available);
            resourceFailed();
        }
    }

    private class WifiScanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("PreStageActivity", "onReceive entered");
            WifiManager wifiManager = wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);


            if(wifiManager.isWifiEnabled()) {
                List<WifiConfiguration> list = wifi.getConfiguredNetworks();
                List<ScanResult> list2 = wifi.getScanResults();

                List<String> ssidSet = new ArrayList<String>();
                final List<Integer> networkIdSet = new ArrayList<Integer>();
                for (ScanResult network : list2) {
                    Log.d("PreStageActivity", "SSIDs" + network.SSID);
                    WifiConfiguration wifiConfig = new WifiConfiguration();
                    wifiConfig.SSID = String.format("\"%s\"", network.SSID);


                    if(network.SSID.startsWith("ardrone2")) {
                        Log.d("PreStageActivity", "ardrone2 found!!!!");
                        int netId = wifiManager.addNetwork(wifiConfig);
                        wifiManager.enableNetwork(netId, true);

                    }
                    //ssidSet.add(network.SSID);
                    //networkIdSet.add(network.networkId);
                }
            }

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);



            if(mWifi.isConnected()){
                Log.d("PreStageActivity", "is connected to");

                CatroidApplication.loadNativeLibs();
                if (CatroidApplication.parrotLibrariesLoaded) {
                    if (droneInitializer == null) {
                        droneInitializer = new DroneInitializer(PreStageActivity.this);
                    }

                    droneInitializer.initialise();
                }
            } else {
                Log.d("PreStageActivity", "is NOT connected to");
            }

            final String action = intent.getAction();
            if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
                Log.d("PreStageActivity", "before intent getBooleanExtra");
                if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)){
                    Log.d("PreStageActivity", "is connected to wifi!");
                } else {
                    Log.d("PreStageActivity", "is NOT connected to wifi!");
                }
            }

            /*
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if(info != null) {
                Log.d("PreStageActivity", "info is not null");
                if(info.isConnected()) {
                    // Do your work.

                    // e.g. To check the Network Name or other info:

                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    String ssid = wifiInfo.getSSID();

                    Log.d("PreStageActivity", "ssid is = "+ssid);
                } else {
                    Log.d("PreStageActivity", "not connected!");
                }
            }

            */
            /*
            for (String ssid : ssidSet) {
                Log.d(getClass().getSimpleName(), "Liste von SSIDs: " + ssid);
            }
            */
			//showAvailableSSIDSetDialog(ssidSet, networkIdSet);

        }

		private void showAvailableSSIDSetDialog(final List<String> ssidSet, final List<Integer> networkIdSet) {
			final CharSequence[] items = ssidSet.toArray(new CharSequence[ssidSet.size()]);

            runOnUiThread(new Runnable() {
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PreStageActivity.this);
                    builder.setTitle("Select your Drone");
                    builder.setItems(items, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int item) {
                            new DroneConnectToWifi(PreStageActivity.this).execute(networkIdSet.get(item).toString(), ssidSet.get(item));

                            Log.d(getClass().getSimpleName(), ssidSet.get(item).toString());

                            //Log.d("PreStageActivity2", "ssid is = " + ssidSet.get(item));
                            //Log.d("PreStageActivity2", "network id is = "+networkIdSet.get(item));

                            /*
                            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                            Log.d("PreStageActivity2", "before isconnected");

                            while (!mWifi.isConnected()) ;
                            Log.d("PreStageActivity2", "is connected");
                            CatroidApplication.loadNativeLibs();
                            if (CatroidApplication.parrotLibrariesLoaded) {
                                if (droneInitializer == null) {
                                    droneInitializer = new DroneInitializer(PreStageActivity.this);
                                }

                                droneInitializer.initialise();
                            }

                            */
                            unregisterReceiver(wifiReciever);
                        }

                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }

            });


		}
    }


}

