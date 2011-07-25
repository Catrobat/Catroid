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

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.io.BTCommunicator;
import at.tugraz.ist.catroid.io.BTConnectable;
import at.tugraz.ist.catroid.io.DeviceListActivity;
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.utils.Utils;

public class StageActivity extends Activity implements BTConnectable {

	private static final int REQUEST_ENABLE_BT = 2000;
	private static final int REQUEST_CONNECT_DEVICE = 1000;
	public static SurfaceView stage;
	private SoundManager soundManager;
	private StageManager stageManager;
	private boolean stagePlaying = false;
	private BluetoothAdapter bluetoothAdapter;
	private BTCommunicator myBTCommunicator;
	private ProgressDialog connectingProgressDialog;
	private boolean pairing;
	private static Handler btcHandler;
	private boolean connected;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Utils.checkForSdCard(this)) {
			Window window = getWindow();
			window.requestFeature(Window.FEATURE_NO_TITLE);
			window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

			setContentView(R.layout.activity_stage);
			stage = (SurfaceView) findViewById(R.id.stageView);

			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			Utils.updateScreenWidthAndHeight(this);

			soundManager = SoundManager.getInstance();
			stageManager = new StageManager(this);

			if (!stageManager.getBluetoothNeeded()) {
				stageManager.start();
				stageManager.startScripts();
				stagePlaying = true;
			} else {

				// Start Bluetooth 
				activateBluetooth();

				//stageManager.startScripts();
				//stageManager.start();
				//stagePlaying = true;

			}
		}
	} // dummy

	private void activateBluetooth() {

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			return;// Device does not support Bluetooth
		}
		if (!bluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			connectLegoNXT();
		}
	}

	private void startBTCommunicator(String mac_address) {
		connected = false;
		connectingProgressDialog = ProgressDialog.show(this, "",
				getResources().getString(R.string.connecting_please_wait), true);

		if (myBTCommunicator != null) {
			try {
				myBTCommunicator.destroyNXTconnection();
			} catch (IOException e) {
			}
		}
		// interestingly BT adapter needs to be obtained by the UI thread - so we pass it in in the constructor
		myBTCommunicator = new BTCommunicator(this, myHandler, BluetoothAdapter.getDefaultAdapter(), getResources());
		btcHandler = myBTCommunicator.getHandler();

		myBTCommunicator.setMACAddress(mac_address);
		myBTCommunicator.start();

		// Continue Stage execution
		stageManager.startScripts();
		stageManager.start();
		stagePlaying = true;

	}

	public void destroyBTCommunicator() {

		if (myBTCommunicator != null) {
			sendBTCmessage(BTCommunicator.NO_DELAY, BTCommunicator.DISCONNECT, 0, 0);
			try {
				myBTCommunicator.destroyNXTconnection();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			myBTCommunicator = null;
		}

		connected = false;
		//updateButtonsAndMenu();
	}

	//Sollte in eine für alle verfügbare Klasse kommen
	private void sendBTCmessage(int delay, int motor, int speed, int angle) {
		Bundle myBundle = new Bundle();
		myBundle.putInt("motor", motor);
		myBundle.putInt("speed", speed);
		myBundle.putInt("angle", angle);
		Message myMessage = btcHandler.obtainMessage();
		myMessage.setData(myBundle);

		if (delay == 0) {
			btcHandler.sendMessage(myMessage);
		} else {
			btcHandler.sendMessageDelayed(myMessage, delay);
		}
	}

	public static Handler getBTCHandler() {
		return btcHandler;
	}

	public boolean isPairing() {
		// TODO Auto-generated method stub
		return pairing;
	}

	/**
	 * Receive messages from the BTCommunicator
	 */
	final Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message myMessage) {
			switch (myMessage.getData().getInt("message")) {
				case BTCommunicator.DISPLAY_TOAST:

					//showToast(myMessage.getData().getString("toastText"), Toast.LENGTH_SHORT);
					break;
				case BTCommunicator.STATE_CONNECTED:
					connected = true;
					connectingProgressDialog.dismiss();

					break;

			}
		}
	};

	private void connectLegoNXT() {
		Intent serverIntent = new Intent(this, DeviceListActivity.class);
		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("bt", "requestcode " + requestCode + " result code" + resultCode);
		switch (requestCode) {

			case REQUEST_ENABLE_BT:
				switch (resultCode) {
					case Activity.RESULT_OK:
						connectLegoNXT();
						break;

					case Activity.RESULT_CANCELED:
						Toast.makeText(StageActivity.this, R.string.notification_blueth_err, Toast.LENGTH_LONG).show();
						finish();
						break;
				}

				break;
			case REQUEST_CONNECT_DEVICE: {
				switch (resultCode) {
					case Activity.RESULT_OK:
						String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
						//pairing = data.getExtras().getBoolean(DeviceListActivity.PAIRING);
						startBTCommunicator(address);
						break;

					case Activity.RESULT_CANCELED:

						break;

				}
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// first pointer: MotionEvent.ACTION_DOWN
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			processOnTouch((int) event.getX(), (int) event.getY());
		}

		// second pointer: MotionEvent.ACTION_POINTER_2_DOWN
		if (event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN) {
			processOnTouch((int) event.getX(1), (int) event.getY(1));
		}

		return false;
	}

	public void processOnTouch(int xCoordinate, int yCoordinate) {
		xCoordinate = xCoordinate + stage.getTop();
		yCoordinate = yCoordinate + stage.getLeft();

		stageManager.processOnTouch(xCoordinate, yCoordinate);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.stage_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.stagemenuStart:
				pauseOrContinue();
				break;
			case R.id.stagemenuConstructionSite:
				manageLoadAndFinish();
				break;
		}
		return true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		soundManager.pause();
		stageManager.pause(false);
		stagePlaying = false;
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		stageManager.resume();
		soundManager.resume();
		stagePlaying = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stageManager.finish();
		soundManager.clear();
		destroyBTCommunicator();
	}

	@Override
	public void onBackPressed() {
		manageLoadAndFinish();
		destroyBTCommunicator();

	}

	private void manageLoadAndFinish() {
		ProjectManager projectManager = ProjectManager.getInstance();
		int currentSpritePos = projectManager.getCurrentSpritePosition();
		int currentScriptPos = projectManager.getCurrentScriptPosition();
		projectManager.loadProject(projectManager.getCurrentProject().getName(), this, false);
		projectManager.setCurrentSpriteWithPosition(currentSpritePos);
		projectManager.setCurrentScriptWithPosition(currentScriptPos);
		finish();
	}

	private void pauseOrContinue() {
		if (stagePlaying) {
			stageManager.pause(true);
			soundManager.pause();
			stagePlaying = false;
		} else {
			stageManager.resume();
			soundManager.resume();
			stagePlaying = true;
		}
	}

	@Override
	protected void onResume() {
		if (!Utils.checkForSdCard(this)) {
			return;
		}
		super.onResume();
	}
}
