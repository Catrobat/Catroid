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

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.parrot.freeflight.receivers.DroneConnectionChangeReceiverDelegate;
import com.parrot.freeflight.receivers.DroneConnectionChangedReceiver;
import com.parrot.freeflight.receivers.DroneReadyReceiver;
import com.parrot.freeflight.receivers.DroneReadyReceiverDelegate;
import com.parrot.freeflight.service.DroneControlService;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.drone.DroneServiceWrapper;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.ui.dialogs.StageDialog;

public class StageActivity extends AndroidApplication implements DroneReadyReceiverDelegate,
		DroneConnectionChangeReceiverDelegate {
	public static final String TAG = StageActivity.class.getSimpleName();
	public static StageListener stageListener;
	private boolean resizePossible;
	private StageDialog stageDialog;

	protected DroneControlService droneControlService = null;
	private BroadcastReceiver droneReadyReceiver = null;
	DroneConnectionChangedReceiver droneConnectionChangeReceiver = null;

	public static final int STAGE_ACTIVITY_FINISH = 7777;

	private Boolean droneIsRequired;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		stageListener = new StageListener();
		stageDialog = new StageDialog(this, stageListener, R.style.stage_dialog);
		calculateScreenSizes();

		initialize(stageListener, true);

		//TODO Drone: process reuturn value
		if (prepareRessources()) {
			Log.d(TAG, "Failure during drone service startup");
		}
	}

	private boolean prepareRessources() {
		if (BuildConfig.DEBUG) {
			droneIsRequired = getIntent().getBooleanExtra(PreStageActivity.STRING_EXTRA_INIT_DRONE, false);
			Log.d(TAG, "prepareRessources() initDrone=" + droneIsRequired.toString());
			if (droneIsRequired) {
				droneReadyReceiver = new DroneReadyReceiver(this);
				droneConnectionChangeReceiver = new DroneConnectionChangedReceiver(this);

				helpBindService();
			}
			return true;
		}
		return false;
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
		super.onPause();

		if (BuildConfig.DEBUG) {
			if (droneControlService != null) {
				droneControlService.pause();
				DroneServiceWrapper.getInstance().setDroneService(null);
			}
			LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getApplicationContext());
			manager.unregisterReceiver(droneReadyReceiver);
			manager.unregisterReceiver(droneConnectionChangeReceiver);
		}
	}

	@Override
	public void onResume() {
		SensorHandler.startSensorListener(this);
		super.onResume();

		if (BuildConfig.DEBUG) {
			if (droneControlService != null) {
				Log.d(TAG, "droneControlService .. onResume");
				droneControlService.resume();
				DroneServiceWrapper.getInstance().setDroneService(droneControlService);
			}
			LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getApplicationContext());
			manager.registerReceiver(droneReadyReceiver, new IntentFilter(DroneControlService.DRONE_STATE_READY_ACTION));
			manager.registerReceiver(droneConnectionChangeReceiver, new IntentFilter(
					DroneControlService.DRONE_CONNECTION_CHANGED_ACTION));

		}
	}

	public void pause() {
		SensorHandler.stopSensorListeners();
		stageListener.menuPause();
	}

	public void resume() {
		stageListener.menuResume();
		SensorHandler.startSensorListener(this);
	}

	public boolean getResizePossible() {
		return resizePossible;
	}

	private void calculateScreenSizes() {
		ifLandscapeSwitchWidthAndHeight();
		int virtualScreenWidth = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenWidth;
		int virtualScreenHeight = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenHeight;
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

	private void ifLandscapeSwitchWidthAndHeight() {
		if (ScreenValues.SCREEN_WIDTH > ScreenValues.SCREEN_HEIGHT) {
			int tmp = ScreenValues.SCREEN_HEIGHT;
			ScreenValues.SCREEN_HEIGHT = ScreenValues.SCREEN_WIDTH;
			ScreenValues.SCREEN_WIDTH = tmp;
		}
	}

	private void onDroneServiceConnected(IBinder service) {
		Log.d(TAG, "onDroneServiceConnected");
		droneControlService = ((DroneControlService.LocalBinder) service).getService();
		DroneServiceWrapper.getInstance().setDroneService(droneControlService);
		droneControlService.resume();
		droneControlService.requestDroneStatus();
		droneControlService.requestConfigUpdate();

		Log.d(TAG, "DroneServiceConnection");
	}

	private ServiceConnection droneServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "Drone Connected");
			onDroneServiceConnected(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG, "Drone Disconnected");
			droneControlService = null;
		}

	};

	@Override
	protected void onDestroy() {
		helpUnbindService();
		Log.d(TAG, "Destroy");
		super.onDestroy();
	}

	private void helpUnbindService() {
		if (droneControlService != null) {
			unbindService(droneServiceConnection);
			droneServiceConnection = null;
			droneControlService = null;
		}
	}

	private boolean helpBindService() {
		boolean droneServiceWasCreated = false;
		if (droneControlService == null) {
			droneServiceWasCreated = bindService(new Intent(this, DroneControlService.class),
					this.droneServiceConnection, Context.BIND_AUTO_CREATE);
			if (!droneServiceWasCreated) {
				Toast.makeText(this, "Connection to the drone not successful", Toast.LENGTH_LONG).show();
			}
		}
		return droneServiceWasCreated;
	}

	@Override
	public void onDroneReady() {
		Log.d(TAG, "onDroneReady");
	}

	@Override
	public void onDroneConnected() {
		Log.d(TAG, "onDroneConnected");
		droneControlService.requestConfigUpdate();
	}

	@Override
	public void onDroneDisconnected() {
		Log.d(TAG, "onDroneDisconnected");
		//Nothing to do here
	}
}
