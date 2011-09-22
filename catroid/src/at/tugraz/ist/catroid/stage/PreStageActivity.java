/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.stage;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.widget.Toast;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.LegoNXT.LegoNXT;
import at.tugraz.ist.catroid.LegoNXT.LegoNXTBtCommunicator;
import at.tugraz.ist.catroid.bluetooth.BluetoothManager;
import at.tugraz.ist.catroid.bluetooth.DeviceListActivity;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;

/**
 * @author Johannes Iber
 * 
 */
public class PreStageActivity extends Activity {

	private static final int REQUEST_ENABLE_BT = 2000;
	private static final int REQUEST_CONNECT_DEVICE = 1000;
	public static final int REQUEST_RESOURCES_INIT = 0101;

	public static StageListener stageListener;
	private static LegoNXT legoNXT;
	private ProgressDialog connectingProgressDialog;
	public static TextToSpeech textToSpeech;
	private int requiredResourceCounter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int required_resources = getRequiredRessources();
		int mask = 0x1;
		int value = required_resources;
		boolean noResources = true;

		while (value > 0) {
			if ((mask & required_resources) > 0) {
				requiredResourceCounter++;
				noResources = false;
			}
			value = value >> 1;
			mask = mask << 1;
		}
		if ((required_resources & Brick.TEXT_TO_SPEECH) > 0) {
			textToSpeech = new TextToSpeech(this.getApplicationContext(), new OnInitListener() {
				public void onInit(int status) {
					resourceInitialized();
					if (status == TextToSpeech.ERROR) {
						Toast.makeText(PreStageActivity.this,
								"Error occurred while initializing Text-To-Speech engine", Toast.LENGTH_LONG).show();
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
			;
		}
		if ((required_resources & Brick.BLUETOOTH_LEGO_NXT) > 0) {
			BluetoothManager bluetoothManager = new BluetoothManager(this);
			legoNXT = new LegoNXT(this, recieveHandler);
			int bluetoothState = bluetoothManager.activateBluetooth();
			if (bluetoothState == -1) {
				Toast.makeText(PreStageActivity.this, R.string.notification_blueth_err, Toast.LENGTH_LONG).show();
				finish();
			} else if (bluetoothState == 1) {
				startBTComm();
			}
		}

		if (noResources == true) {
			startStage();
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		if (requiredResourceCounter == 0) {
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	public static void shutdownResources() {
		if (textToSpeech != null) {
			textToSpeech.stop();
			textToSpeech.shutdown();
		}
		if (legoNXT != null) {
			legoNXT.destroyCommunicator();
		}
	}

	private void resourceFailed() {
		Intent intent = this.getIntent();
		this.setResult(RESULT_CANCELED, intent);
		finish();
	}

	private synchronized void resourceInitialized() {
		Log.i("bt", "Res init: " + requiredResourceCounter);

		requiredResourceCounter--;
		if (requiredResourceCounter <= 0) {
			startStage();

		}

	}

	public void startStage() {
		Intent intent = this.getIntent();
		this.setResult(RESULT_OK, intent);
		finish();
	}

	private void startBTComm() {
		connectingProgressDialog = ProgressDialog.show(this, "",
				getResources().getString(R.string.connecting_please_wait), true);
		Intent serverIntent = new Intent(this, DeviceListActivity.class);
		this.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	}

	private int getRequiredRessources() {

		ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject()
				.getSpriteList();
		int ressources = Brick.NO_RESOURCES;

		for (Sprite sprite : spriteList) {
			ressources |= sprite.getRequiredResources();
		}
		return ressources;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("bt", "requestcode " + requestCode + " result code" + resultCode);
		switch (requestCode) {

			case REQUEST_ENABLE_BT:
				switch (resultCode) {
					case Activity.RESULT_OK:
						startBTComm();
						break;

					case Activity.RESULT_CANCELED:
						Toast.makeText(PreStageActivity.this, R.string.notification_blueth_err, Toast.LENGTH_LONG)
								.show();
						//finish();
						resourceFailed();
						break;
				}
				break;

			case REQUEST_CONNECT_DEVICE:
				switch (resultCode) {
					case Activity.RESULT_OK:
						String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
						//pairing = data.getExtras().getBoolean(DeviceListActivity.PAIRING);
						legoNXT.startBTCommunicator(address);
						break;

					case Activity.RESULT_CANCELED:
						connectingProgressDialog.dismiss();
						Toast.makeText(PreStageActivity.this, R.string.bt_connection_failed, Toast.LENGTH_LONG).show();
						//finish();
						resourceFailed();
						break;
				}
				break;

			default:
				finish();
		}
	}

	public void makeToast(String text) {
		Toast.makeText(this.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	}

	public static void textToSpeech(String text) {
		textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}

	//messages from Lego NXT device can be handled here
	final Handler recieveHandler = new Handler() {
		@Override
		public void handleMessage(Message myMessage) {

			Log.i("bt", "message" + myMessage.getData().getInt("message"));
			switch (myMessage.getData().getInt("message")) {
				case LegoNXTBtCommunicator.STATE_CONNECTED:
					connectingProgressDialog.dismiss();
					resourceInitialized();
					break;
				case LegoNXTBtCommunicator.STATE_CONNECTERROR:
					Toast.makeText(PreStageActivity.this, R.string.bt_connection_failed, Toast.LENGTH_SHORT);
					connectingProgressDialog.dismiss();
					//finish();
					resourceFailed();
					break;
				default:

					//Toast.makeText(StageActivity.this, myMessage.getData().getString("toastText"), Toast.LENGTH_SHORT);
					break;

			}
		}
	};

}
