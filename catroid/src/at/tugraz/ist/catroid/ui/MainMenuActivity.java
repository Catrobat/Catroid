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

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import at.tugraz.ist.catroid.lego.DeviceListActivity;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.dialogs.AboutDialog;
import at.tugraz.ist.catroid.ui.dialogs.LoadProjectDialog;
import at.tugraz.ist.catroid.ui.dialogs.NewProjectDialog;
import at.tugraz.ist.catroid.ui.dialogs.UploadProjectDialog;
import at.tugraz.ist.catroid.utils.ActivityHelper;
import at.tugraz.ist.catroid.utils.Utils;

public class MainMenuActivity extends Activity {
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

	//private static final int REQUEST_CONNECT_DEVICE = 1000;

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
		mBluetoothAdapter.disable();
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == Activity.RESULT_OK) {
				//Log.i("bt", "resultCode is RESULT_OK " + resultCode);
				Toast.makeText(this, "Bluetooth enablad", Toast.LENGTH_LONG).show();
				connected = true;
				updateMenu();
			} else {
				//Log.i("bt", "resultCode is not RESULT_OK" + resultCode);
				Toast.makeText(this, "Bluetooth not activ", Toast.LENGTH_LONG).show();
				connected = false;
				updateMenu();
			}

		}
	}

	void selectNXT() {
		Intent serverIntent = new Intent(this, DeviceListActivity.class);
		startActivity(serverIntent);
	}
}