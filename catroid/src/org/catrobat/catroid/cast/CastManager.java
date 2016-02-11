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
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;
import com.google.android.gms.common.api.Status;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.ui.dialogs.SelectCastDialog;

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
	private ArrayAdapter<String> deviceAdapter;
	private CastDevice selectedDevice;
	private boolean isConnected = false;

	private Activity initializingActivity;

	public void setIsConnected(boolean isConnected) {
		// Has to be called inside a sync block!

		int drawableId = isConnected ? R.drawable.ic_cast_connected_white_24dp : R.drawable.ic_cast_white_24dp;
		Drawable drawable = ContextCompat.getDrawable(initializingActivity, R.drawable.idle_screen_1);
		castButton.setIcon(drawableId);
		this.isConnected = isConnected;
		initializingActivity.invalidateOptionsMenu();
	}

	public boolean isConnected() {
		synchronized (this) { //better to sync this where called
			return isConnected;
		}
	}

	public ArrayList<MediaRouter.RouteInfo> getRouteInfos() {
		return routeInfos;
	}

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

		synchronized (this) {
			initializingActivity = activity;
		}

		if (mediaRouter != null) {
			return;
		}
		deviceAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, routeNames);
		mediaRouter = MediaRouter.getInstance(activity.getApplicationContext());
		mediaRouteSelector = new MediaRouteSelector.Builder()
				.addControlCategory(CastMediaControlIntent.categoryForCast(Constants.REMOTE_DISPLAY_APP_ID))
				.build(); //TODO: Does this need to be done once for the application lifetime or seperately for each activity?
		addCallback();

	}

	public void addCallback() {
		callback = new MyMediaRouterCallback();
		mediaRouter.addCallback(mediaRouteSelector, callback, MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
	}

	public void openDeviceSelectorOrDisconnectDialog() {
		openDeviceSelectorOrDisconnectDialog(initializingActivity);
	}

	public void openDeviceSelectorOrDisconnectDialog(Activity activity) {
		synchronized (this) {
			if (isConnected) {
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setMessage("Stop casting?");
				builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						mediaRouter.unselect(MediaRouter.UNSELECT_REASON_STOPPED);
					}
				}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {

					}
				});
				builder.create().show();
			} else {
				mediaRouter.addCallback(mediaRouteSelector, callback, MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);
				SelectCastDialog dialog = new SelectCastDialog();
				dialog.openDialog(activity, deviceAdapter);
			}
		}
	}

	public void setCastButton(MenuItem castButton) {
		synchronized (this) { //TODO: necessary?
			this.castButton = castButton;
			if (routeNames.size() > 0) {
				synchronized (this) {
					castButton.setVisible(true);
				}
			}
			setIsConnected(isConnected);
		}
	}

	public void selectRoute(MediaRouter.RouteInfo routeInfo) {
		mediaRouter.selectRoute(routeInfo);
	}

	private class MyMediaRouterCallback extends MediaRouter.Callback {


		@Override
		public void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo info) {
			// Add route to list of discovered routes
			synchronized (this) {
				for (int i = 0; i < routeInfos.size(); i++) {
					MediaRouter.RouteInfo routeInfo = routeInfos.get(i);
					if (routeInfo.equals(info)) {
						routeInfos.remove(i);
						routeNames.remove(i);
					}
				}
				routeInfos.add(info);
				routeNames.add(info.getName() + " (" + info.getDescription() + ")");
				if (castButton != null) {
					castButton.setVisible(true);
				}
				deviceAdapter.notifyDataSetChanged();
			}
		}

		@Override
		public void onRouteRemoved(MediaRouter router, MediaRouter.RouteInfo info) {
			// Remove route from list of routes
			synchronized (this) {
				for (int i = 0; i < routeInfos.size(); i++) {
					MediaRouter.RouteInfo routeInfo = routeInfos.get(i);
					if (routeInfo.equals(info)) {
						routeInfos.remove(i);
						routeNames.remove(i);
						if (castButton != null && routeInfos.size() == 0) {
							castButton.setVisible(false);
						}
						deviceAdapter.notifyDataSetChanged();
					}
				}
			}

		}

		@Override
		public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
			synchronized (this) {
				selectedDevice = CastDevice.getFromBundle(info.getExtras());
				startCastService(initializingActivity);
			}
		}

		@Override
		public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
			synchronized (this) {
				setIsConnected(false);
				selectedDevice = null;
			}
		}

		public void startCastService(final Activity activity) {

			Intent intent = new Intent(activity, activity.getClass());
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent notificationPendingIntent = PendingIntent.getActivity(activity, 0, intent, 0);

			CastRemoteDisplayLocalService.NotificationSettings settings = new CastRemoteDisplayLocalService
					.NotificationSettings.Builder()
					.setNotificationPendingIntent(notificationPendingIntent).build();

			CastRemoteDisplayLocalService.startService(activity, CastService.class,
					Constants.REMOTE_DISPLAY_APP_ID, selectedDevice, settings,
					new CastRemoteDisplayLocalService.Callbacks() {

						@Override
						public void onServiceCreated(CastRemoteDisplayLocalService castRemoteDisplayLocalService) {

						}

						@Override
						public void onRemoteDisplaySessionStarted(
								CastRemoteDisplayLocalService service) {
						}

						@Override
						public void onRemoteDisplaySessionError(Status errorReason) {
							synchronized (this) {
								setIsConnected(false);
								selectedDevice = null;
								activity.finish();
							}
						}
					});
		}

	}

}