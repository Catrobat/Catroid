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

package at.tugraz.ist.catroid.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.dialogs.AboutDialog;
import at.tugraz.ist.catroid.ui.dialogs.LoadProjectDialog;
import at.tugraz.ist.catroid.ui.dialogs.NewProjectDialog;
import at.tugraz.ist.catroid.ui.dialogs.UploadProjectDialog;
import at.tugraz.ist.catroid.utils.ActivityHelper;
import at.tugraz.ist.catroid.utils.Utils;

public class MainMenuActivity extends Activity implements BTConnectable {
	private static final String PREFS_NAME = "at.tugraz.ist.catroid";
	private static final String PREF_PROJECTNAME_KEY = "projectName";
	private ProjectManager projectManager;
	private ActivityHelper activityHelper = new ActivityHelper(this);
	private static final int REQUEST_ENABLE_BT = 3;
	private Menu myMenu;
	public static final int MENU_TOGGLE_CONNECT = Menu.FIRST;
	public static final int MENU_TOOGLE_DISCONNECT = Menu.FIRST + 1;
	public static final int MENU_CONNECT_NXT = Menu.FIRST + 2;
	private boolean connected = false;

	private static final int REQUEST_CONNECT_DEVICE = 1000;

	public static final int UPDATE_TIME = 200;
	public static final int MENU_QUIT = Menu.FIRST + 1;

	private BTCommunicator myBTCommunicator = null;
	private ProgressDialog connectingProgressDialog;
	private boolean pairing;
	private Handler btcHandler;
	private Toast reusableToast;
	private List<String> programList;
	private boolean btErrorPending = false;
	private Activity thisActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.updateScreenWidthAndHeight(this);

		setContentView(R.layout.activity_main_menu);
		projectManager = ProjectManager.getInstance();

		// Try to load sharedPreferences
		SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		String projectName = prefs.getString(PREF_PROJECTNAME_KEY, null);

		if (projectName != null) {
			projectManager.loadProject(projectName, this, false);
		} else {
			projectManager.initializeDefaultProject(this);
		}

		if (projectManager.getCurrentProject() == null) {
			Button currentProjectButton = (Button) findViewById(R.id.current_project_button);
			currentProjectButton.setEnabled(false);

		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		activityHelper.setupActionBar(true, null);
		activityHelper.addActionButton(R.id.btn_action_play, R.drawable.ic_play_black, new View.OnClickListener() {
			public void onClick(View v) {
				if (projectManager.getCurrentProject() != null) {
					Intent intent = new Intent(MainMenuActivity.this, StageActivity.class);
					startActivity(intent);
				}
			}
		}, false);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		if (projectManager.getCurrentProject() != null
				&& StorageHandler.getInstance().projectExists(projectManager.getCurrentProject().getName())) {
			projectManager.saveProject(this);
		}

		switch (id) {
			case Consts.DIALOG_NEW_PROJECT:
				dialog = new NewProjectDialog(this);
				break;
			case Consts.DIALOG_LOAD_PROJECT:
				dialog = new LoadProjectDialog(this);
				break;
			case Consts.DIALOG_ABOUT:
				dialog = new AboutDialog(this);
				break;
			case Consts.DIALOG_UPLOAD_PROJECT:
				if (projectManager.getCurrentProject() == null) {
					dialog = null;
					break;
				}
				dialog = new UploadProjectDialog(this);
				break;
			default:
				dialog = null;
				break;
		}

		return dialog;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!Utils.checkForSdCard(this)) {
			return;
		}
		if (projectManager.getCurrentProject() == null) {
			return;
		}
		//		TextView currentProjectTextView = (TextView) findViewById(R.id.currentProjectNameTextView);
		//		currentProjectTextView.setText(getString(R.string.current_project) + " "
		//				+ projectManager.getCurrentProject().getName());

		projectManager.loadProject(projectManager.getCurrentProject().getName(), this, false);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (projectManager.getCurrentProject() == null) {
			return;
		}
		//		TextView currentProjectTextView = (TextView) findViewById(R.id.currentProjectNameTextView);
		//		currentProjectTextView.setText(getString(R.string.current_project) + " "
		//				+ projectManager.getCurrentProject().getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		// onPause is sufficient --> gets called before "process_killed",
		// onStop(), onDestroy(), onRestart()
		// also when you switch activities
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject(this);
			SharedPreferences.Editor prefs = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
			prefs.putString(PREF_PROJECTNAME_KEY, projectManager.getCurrentProject().getName());
			prefs.commit();
		}
	}

	public void handleCurrentProjectButton(View v) {
		if (projectManager.getCurrentProject() != null) {
			Intent intent = new Intent(MainMenuActivity.this, ProjectActivity.class);
			startActivity(intent);
		}
	}

	public void handleNewProjectButton(View v) {
		showDialog(Consts.DIALOG_NEW_PROJECT);
	}

	public void handleLoadProjectButton(View v) {
		showDialog(Consts.DIALOG_LOAD_PROJECT);
	}

	public void handleUploadProjectButton(View v) {
		showDialog(Consts.DIALOG_UPLOAD_PROJECT);
	}

	public void handleAboutCatroidButton(View v) {
		showDialog(Consts.DIALOG_ABOUT);
	}

	public boolean BtisOn() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		connected = mBluetoothAdapter.isEnabled();
		Log.i("bt", "BtisOn() " + connected);
		return connected;
	}

	public void connectBT() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
		}
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}

	public void disconnectBT() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			mBluetoothAdapter.disable();
		}
		Toast.makeText(this, "bluetooth disabled", Toast.LENGTH_LONG).show();
		connected = false;
		updateMenu();
		// Device does not support Bluetooth
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		myMenu = menu;

		if (!BtisOn()) {
			//Log.i("bt", "menu_connect connected is " + BtisOn());
			myMenu.add(Menu.NONE, MENU_TOGGLE_CONNECT, Menu.NONE, R.string.enable_bluetooth);
			myMenu.add(Menu.NONE, MENU_CONNECT_NXT, Menu.NONE, R.string.connect_nxt).setEnabled(false);

		} else {
			//Log.i("bt", "menu_disconnect connected is " + BtisOn());
			myMenu.add(Menu.NONE, MENU_TOOGLE_DISCONNECT, Menu.NONE, R.string.disable_bluetooth);
			myMenu.add(Menu.NONE, MENU_CONNECT_NXT, Menu.NONE, R.string.connect_nxt).setEnabled(true);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_TOGGLE_CONNECT:
				Log.i("bt", "menu toogle connect selected");
				connectBT();
				break;
			case MENU_TOOGLE_DISCONNECT:
				Log.i("bt", "menu toogle disconnected selected");
				disconnectBT();
				break;
			case MENU_CONNECT_NXT:
				selectNXT();
				break;
		}
		return true;
	}

	public void updateMenu() {
		//		if (myMenu == null) {
		//			return;
		//		}

		myMenu.clear();

		if (!connected) {
			myMenu.add(Menu.NONE, MENU_TOGGLE_CONNECT, Menu.NONE, R.string.enable_bluetooth);
			myMenu.add(Menu.NONE, MENU_CONNECT_NXT, Menu.NONE, R.string.connect_nxt).setEnabled(false);
		} else {
			myMenu.add(Menu.NONE, MENU_TOOGLE_DISCONNECT, Menu.NONE, R.string.disable_bluetooth);
			myMenu.add(Menu.NONE, MENU_CONNECT_NXT, Menu.NONE, R.string.connect_nxt).setEnabled(true);
		}

	}

	private void startBTCommunicator(String mac_address) {
		Log.i("sbt", "start startBTCommunicator");
		connected = false;
		Log.i("sbt", "start startBTCommunicator progressdialog");
		connectingProgressDialog = ProgressDialog.show(this, "", getResources().getString(
				R.string.connecting_please_wait), true);
		Log.i("sbt", "start startBTCommunicator after progressdialog");
		if (myBTCommunicator != null) {
			try {
				Log.i("sbt", "myBTCommunicator.destroyNXTconnection()");
				myBTCommunicator.destroyNXTconnection();
			} catch (IOException e) {
			}
		}
		Log.i("sbt", "start createBTCommunicator()");
		createBTCommunicator();
		Log.i("sbt", "finish createBTCommunicator()");
		myBTCommunicator.setMACAddress(mac_address);
		Log.i("sbt", "setMACAddress(mac_address)");
		myBTCommunicator.start();
		//updateButtonsAndMenu();
	}

	/**
	 * Creates a new object for communication to the NXT robot via bluetooth and fetches the corresponding handler.
	 */
	private void createBTCommunicator() {
		// interestingly BT adapter needs to be obtained by the UI thread - so we pass it in in the constructor
		myBTCommunicator = new BTCommunicator(this, myHandler, BluetoothAdapter.getDefaultAdapter(), getResources());
		btcHandler = myBTCommunicator.getHandler();
	}

	/**
	 * Sends the message via the BTCommuncator to the robot.
	 * 
	 * @param delay
	 *            time to wait before sending the message.
	 * @param message
	 *            the message type (as defined in BTCommucator)
	 * @param String
	 *            a String parameter
	 */
	void sendBTCmessage(int delay, int message, String name) {
		Bundle myBundle = new Bundle();
		myBundle.putInt("message", message);
		myBundle.putString("name", name);
		Message myMessage = myHandler.obtainMessage();
		myMessage.setData(myBundle);

		if (delay == 0) {
			btcHandler.sendMessage(myMessage);
		} else {
			btcHandler.sendMessageDelayed(myMessage, delay);
		}
	}

	/**
	 * Sends the message via the BTCommuncator to the robot.
	 * 
	 * @param delay
	 *            time to wait before sending the message.
	 * @param message
	 *            the message type (as defined in BTCommucator)
	 * @param value1
	 *            first parameter
	 * @param value2
	 *            second parameter
	 */
	void sendBTCmessage(int delay, int message, int value1, int value2) {
		Bundle myBundle = new Bundle();
		myBundle.putInt("message", message);
		myBundle.putInt("value1", value1);
		myBundle.putInt("value2", value2);
		Message myMessage = myHandler.obtainMessage();
		myMessage.setData(myBundle);

		if (delay == 0) {
			btcHandler.sendMessage(myMessage);
		} else {
			btcHandler.sendMessageDelayed(myMessage, delay);
		}
	}

	/**
	 * Sends a message for disconnecting to the communication thread.
	 */
	public void destroyBTCommunicator() {

		if (myBTCommunicator != null) {
			sendBTCmessage(BTCommunicator.NO_DELAY, BTCommunicator.DISCONNECT, 0, 0);
			myBTCommunicator = null;
		}

		connected = false;
		//updateButtonsAndMenu();
	}

	/**
	 * Receive messages from the BTCommunicator
	 */
	final Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message myMessage) {
			switch (myMessage.getData().getInt("message")) {

				case BTCommunicator.STATE_CONNECTED:
					connected = true;
					programList = new ArrayList<String>();
					connectingProgressDialog.dismiss();
					sendBTCmessage(BTCommunicator.NO_DELAY, BTCommunicator.GET_FIRMWARE_VERSION, 0, 0);
					break;
				//				case BTCommunicator.MOTOR_STATE:
				//
				//					if (myBTCommunicator != null) {
				//						byte[] motorMessage = myBTCommunicator.getReturnMessage();
				//						int position = byteToInt(motorMessage[21]) + (byteToInt(motorMessage[22]) << 8)
				//								+ (byteToInt(motorMessage[23]) << 16) + (byteToInt(motorMessage[24]) << 24);
				//						showToast(getResources().getString(R.string.current_position) + position, Toast.LENGTH_SHORT);
				//					}
				//
				//					break;

				case BTCommunicator.STATE_CONNECTERROR_PAIRING:
					connectingProgressDialog.dismiss();
					destroyBTCommunicator();
					break;

				case BTCommunicator.STATE_CONNECTERROR:
					connectingProgressDialog.dismiss();
				case BTCommunicator.STATE_RECEIVEERROR:
				case BTCommunicator.STATE_SENDERROR:

					destroyBTCommunicator();
					if (btErrorPending == false) {
						btErrorPending = true;
						// inform the user of the error with an AlertDialog
						AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
						builder.setTitle(getResources().getString(R.string.bt_error_dialog_title)).setMessage(
								getResources().getString(R.string.bt_error_dialog_message)).setCancelable(false)
								.setPositiveButton("OK", new DialogInterface.OnClickListener() {
									//why not ovverride
									//@Override
									public void onClick(DialogInterface dialog, int id) {
										btErrorPending = false;
										dialog.cancel();
										selectNXT();
									}
								});
						builder.create().show();
					}

					break;

			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("mainmenu", "start onActivityResult");
		switch (requestCode) {
			case REQUEST_CONNECT_DEVICE:

				// When DeviceListActivity returns with a device to connect
				if (resultCode == Activity.RESULT_OK) {
					// Get the device MAC address and start a new bt communicator thread
					Log.i("mainmenu", "resultCode == Activity.RESULT_OK");
					String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
					pairing = data.getExtras().getBoolean(DeviceListActivity.PAIRING);
					startBTCommunicator(address);
				}

				break;

			case REQUEST_ENABLE_BT:
				if (resultCode == Activity.RESULT_OK) {
					Log.i("mainmenu", "resultCode is RESULT_OK " + resultCode);
					Toast.makeText(this, "Bluetooth enablad", Toast.LENGTH_LONG).show();
					connected = true;
					updateMenu();
				} else {
					//Log.i("bt", "resultCode is not RESULT_OK" + resultCode);
					Toast.makeText(this, "Bluetooth not activ", Toast.LENGTH_LONG).show();
					connected = false;
					updateMenu();
				}

				break;
		}

	}

	void selectNXT() {
		Intent serverIntent = new Intent(this, DeviceListActivity.class);
		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.tugraz.ist.catroid.lego.BTConnectable#isPairing()
	 */
	public boolean isPairing() {
		// TODO Auto-generated method stub
		return pairing;
	}

	public void actionButtonPressed() {
		//		Log.i("btc", " begin actionButtonPressed main menu");
		//		new Thread(new Runnable() {
		//
		//			public void run() {
		//
		//				if (myBTCommunicator != null) {
		//
		//					// MOTOR ACTION: forth an back
		//					// other robots: 180 degrees forth and back
		//					sendBTCmessage(BTCommunicator.NO_DELAY, BTCommunicator.MOTOR_A, 75 * 1, 0);
		//					sendBTCmessage(500, BTCommunicator.MOTOR_A, -75 * 1, 0);
		//					sendBTCmessage(1000, BTCommunicator.MOTOR_A, 0, 0);
		//
		//				}
		//
		//			}
		//		}).start();
		//		if (myBTCommunicator != null) {
		//
		//			// MOTOR ACTION: forth an back
		//			// other robots: 180 degrees forth and back
		sendBTCmessage(1000, BTCommunicator.MOTOR_A, 0, 0);
		//			sendBTCmessage(500, BTCommunicator.MOTOR_A, -75 * 1, 0);
		//			sendBTCmessage(1000, BTCommunicator.MOTOR_A, 0, 0);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			mBluetoothAdapter.disable();
		}
	}
}
