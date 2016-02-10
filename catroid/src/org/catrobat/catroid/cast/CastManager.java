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

package org.catrobat.catroid.cast;

import android.app.Activity;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.formulaeditor.Sensors;

import java.util.ArrayList;
import java.util.EnumMap;

public final class CastManager {


	private static final String TAG = CastManager.class.getSimpleName();
	private static final CastManager INSTANCE = new CastManager();
	private EnumMap<Sensors, Boolean> isGamepadButtonPressed = new EnumMap<>(Sensors.class);

	// CAST REMOTE API STUFF
	private MediaRouter mediaRouter;
	private MediaRouteSelector mediaRouteSelector;
	private MyMediaRouterCallback callback;
	private ArrayList<String> routeNames = new ArrayList<String>();
	private CastDevice selectedDevice;
	private final ArrayList<MediaRouter.RouteInfo> routeInfos = new ArrayList<MediaRouter.RouteInfo>();
	private MenuItem castButton;
	// END

	public static CastManager getInstance() { return INSTANCE; }

	private CastManager() {
		isGamepadButtonPressed.put(Sensors.GAMEPAD_A_PRESSED, false);
		isGamepadButtonPressed.put(Sensors.GAMEPAD_B_PRESSED, false);
		isGamepadButtonPressed.put(Sensors.GAMEPAD_LEFT_PRESSED, false);
		isGamepadButtonPressed.put(Sensors.GAMEPAD_RIGHT_PRESSED, false);
		isGamepadButtonPressed.put(Sensors.GAMEPAD_UP_PRESSED, false);
		isGamepadButtonPressed.put(Sensors.GAMEPAD_DOWN_PRESSED, false);
	}

	public void forDebuggingLogAllRouteNames() {
		synchronized (this) {
			Log.d(TAG, "Starting...");
			for (String routeName: routeNames) {
				Log.d(TAG, routeName);
			}
			Log.d(TAG, "Logging finished...");
		}
	}

	public boolean isButtonPressed(Sensors btnSensor) {
		return isGamepadButtonPressed.get(btnSensor);
	}

	public void setButtonPress(Sensors btn, boolean b) {
		isGamepadButtonPressed.put(btn, b);
	}

	public void initializeCast(Activity activity) {

		if (mediaRouter == null) {
			mediaRouter = MediaRouter.getInstance(activity.getApplicationContext());
			mediaRouteSelector = new MediaRouteSelector.Builder()
					.addControlCategory(CastMediaControlIntent.categoryForCast(Constants.REMOTE_DISPLAY_APP_ID))
					.build(); //TODO: Does this need to be done once for the application lifetime or seperately for each activity?
			callback = new MyMediaRouterCallback();
		}
	}

	public void setCastButton(MenuItem castButton) {
		synchronized (this) { //TODO: necessary?
			this.castButton = castButton;
			if (routeNames.size() > 0) {
				castButton.setVisible(true);
			}
		}
	}

	public void addCallback() {
		// This should only be called from onResume!
		if (callback == null) {
			throw new AssertionError("The callback has not been created yet. " +
					                 "Make sure you've called the initializeCast method in onStart!");
		}
		mediaRouter.addCallback(mediaRouteSelector, callback, MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
	}

	public void removeCallback() {
		// This should only be called from onPause
		mediaRouter.removeCallback(callback);
	}

	public boolean isConnected() {
		return (selectedDevice != null);
	}

	private class MyMediaRouterCallback extends MediaRouter.Callback {

		@Override
		public void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo info) {
			// Add route to list of discovered routes
			synchronized (this) {
				if (routeInfos.contains(info)) {
					return;
				}

				for (int i = 0; i < routeInfos.size(); i++) {
					MediaRouter.RouteInfo routeInfo = routeInfos.get(i);
					if ((routeInfo.getName() + routeInfo.getDescription()).equals(info.getName() + info.getDescription())) {
						//TODO: The comparison here would fail if the user has two cast devices with same name and description
						//      in the same network. There must be a better way to do this.
						routeInfos.remove(i);
						routeNames.remove(i);
					}
				}

				routeInfos.add(info);
				routeNames.add(info.getName() + " (" + info.getDescription() + ")");
				if (castButton != null) {
					castButton.setVisible(true);
				}
				//mAdapter.notifyDataSetChanged();
			}
		}

		@Override
		public void onRouteRemoved(MediaRouter router, MediaRouter.RouteInfo info) {
			// Remove route from list of routes
			synchronized (this) {
				for (int i = 0; i < routeInfos.size(); i++) {
					MediaRouter.RouteInfo routeInfo = routeInfos.get(i);
					if ((routeInfo.getName() + routeInfo.getDescription()).equals(info.getName() + info.getDescription())) {
						//TODO: The comparison here would fail if the user has two cast devices with same name and description
						//      in the same network. There must be a better way to do this.
						routeInfos.remove(i);
						routeNames.remove(i);
						if (castButton != null && routeInfos.size() == 0) {
							castButton.setVisible(false);
						}
						//mAdapter.notifyDataSetChanged();
					}
				}
			}

		}

		@Override
		public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
			selectedDevice = CastDevice.getFromBundle(info.getExtras());
		}

		@Override
		public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
			selectedDevice = null;
		}

	}

}