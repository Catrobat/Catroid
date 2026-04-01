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
package org.catrobat.catroid.cast;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;
import com.google.android.gms.common.api.Status;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.bricks.CameraBrick;
import org.catrobat.catroid.content.bricks.ChooseCameraBrick;
import org.catrobat.catroid.content.bricks.FlashBrick;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.ui.adapter.CastDevicesAdapter;
import org.catrobat.catroid.ui.dialogs.SelectCastDialog;
import org.catrobat.catroid.utils.ToastUtil;

import java.util.ArrayList;
import java.util.EnumMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.mediarouter.media.MediaRouteSelector;
import androidx.mediarouter.media.MediaRouter;

import static org.catrobat.catroid.common.Constants.CAST_IDLE_BACKGROUND_COLOR;

public final class CastManager {
	private static final CastManager INSTANCE = new CastManager();
	private final ArrayList<MediaRouter.RouteInfo> routeInfos = new ArrayList<MediaRouter.RouteInfo>();
	private StageActivity gamepadActivity;
	private EnumMap<Sensors, Boolean> isGamepadButtonPressed = new EnumMap<>(Sensors.class);
	private MediaRouter mediaRouter;
	private MediaRouteSelector mediaRouteSelector;
	private MyMediaRouterCallback callback;
	private ArrayAdapter<MediaRouter.RouteInfo> deviceAdapter;
	private CastDevice selectedDevice;
	private boolean isConnected = false;
	private GLSurfaceView20 stageViewDisplayedOnCast;
	private AppCompatActivity initializingActivity;
	private RelativeLayout remoteLayout;
	private RelativeLayout pausedView = null;
	private MenuItem castButton;
	private boolean pausedScreenShowing = false;
	private boolean isCastDeviceAvailable;

	public static ArrayList<Class<?>> unsupportedBricks = new ArrayList<Class<?>>() {
		{
			add(CameraBrick.class);
			add(ChooseCameraBrick.class);
			add(FlashBrick.class);
		}
	};

	private CastManager() {
		isGamepadButtonPressed.put(Sensors.GAMEPAD_A_PRESSED, false);
		isGamepadButtonPressed.put(Sensors.GAMEPAD_B_PRESSED, false);
		isGamepadButtonPressed.put(Sensors.GAMEPAD_LEFT_PRESSED, false);
		isGamepadButtonPressed.put(Sensors.GAMEPAD_RIGHT_PRESSED, false);
		isGamepadButtonPressed.put(Sensors.GAMEPAD_UP_PRESSED, false);
		isGamepadButtonPressed.put(Sensors.GAMEPAD_DOWN_PRESSED, false);
	}

	public static CastManager getInstance() {
		return INSTANCE;
	}

	public synchronized void initializeGamepadActivity(StageActivity gamepadActivity) { //TODO needs to be synced?
		this.gamepadActivity = gamepadActivity;
		initGamepadListeners();
	}

	public synchronized void setIsConnected(boolean isConnected) {

		int drawableId = isConnected ? R.drawable.ic_cast_connected_white : R.drawable.ic_cast_white;
		castButton.setIcon(drawableId);
		this.isConnected = isConnected;
		initializingActivity.invalidateOptionsMenu();
	}

	public void startCastButtonAnimation() {
		int drawableId = R.drawable.animation_cast_button_connecting;
		castButton.setIcon(drawableId);
		((AnimationDrawable) castButton.getIcon()).start();
	}

	public synchronized boolean isConnected() {
		return isConnected;
	}

	public MediaRouter getMediaRouter() {
		return mediaRouter;
	}

	public ArrayAdapter<MediaRouter.RouteInfo> getDeviceAdapter() {
		return deviceAdapter;
	}

	public ArrayList<MediaRouter.RouteInfo> getRouteInfos() {
		return routeInfos;
	}

	public boolean isButtonPressed(Sensors btnSensor) {
		return isGamepadButtonPressed.get(btnSensor);
	}

	public void setButtonPress(Sensors btn, boolean b) {
		isGamepadButtonPressed.put(btn, b);
	}

	public CastDevice getSelectedDevice() {
		return selectedDevice;
	}

	public synchronized void initializeCast(AppCompatActivity activity) {

		initializingActivity = activity;

		if (mediaRouter != null) {
			return;
		}
		deviceAdapter = new CastDevicesAdapter(activity, R.layout.fragment_cast_device_list_item, routeInfos);
		mediaRouter = MediaRouter.getInstance(activity.getApplicationContext());
		mediaRouteSelector = new MediaRouteSelector.Builder()
				.addControlCategory(CastMediaControlIntent.categoryForCast(Constants.REMOTE_DISPLAY_APP_ID))
				.build();
		setCallback();
	}

	public void addCallback() {
		callback = new MyMediaRouterCallback();
		mediaRouter.addCallback(mediaRouteSelector, callback, MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
	}

	public synchronized void setCallback() {
		setCallback(MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
	}

	public synchronized void setCallback(int callbackFlag) {
		if (callback == null) {
			callback = new MyMediaRouterCallback();
		}
		mediaRouter.addCallback(mediaRouteSelector, callback, callbackFlag);
	}

	public void openDeviceSelectorOrDisconnectDialog() {
		openDeviceSelectorOrDisconnectDialog(initializingActivity);
	}

	private void initGamepadListeners() {

		View.OnClickListener pauseButtonListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				gamepadActivity.onBackPressed();
			}
		};

		gamepadActivity.findViewById(R.id.gamepadPauseButton).setOnClickListener(pauseButtonListener);

		View.OnTouchListener otl = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				handleGamepadTouch((ImageButton) v, event);
				return true;
			}
		};

		ImageButton[] gamepadButtons = {

				(ImageButton) gamepadActivity.findViewById(R.id.gamepadButtonA),
				(ImageButton) gamepadActivity.findViewById(R.id.gamepadButtonB),
				(ImageButton) gamepadActivity.findViewById(R.id.gamepadButtonUp),
				(ImageButton) gamepadActivity.findViewById(R.id.gamepadButtonDown),
				(ImageButton) gamepadActivity.findViewById(R.id.gamepadButtonLeft),
				(ImageButton) gamepadActivity.findViewById(R.id.gamepadButtonRight)
		};

		for (ImageButton btn : gamepadButtons) {
			btn.setOnTouchListener(otl);
		}
	}

	private synchronized void handleGamepadTouch(ImageButton button, MotionEvent event) {

		if (event.getAction() != MotionEvent.ACTION_DOWN && event.getAction() != MotionEvent.ACTION_UP) {
			// We only care about the event when a gamepad button is pressed and when a gamepad button is unpressed
			return;
		}

		if (gamepadActivity == null) {
			return;
		}

		boolean isActionDown = (event.getAction() == MotionEvent.ACTION_DOWN);

		Sensors buttonPressed;
		String buttonPressedName;
		switch (button.getId()) {
			case R.id.gamepadButtonA:
				button.setImageResource(isActionDown ? R.drawable.gamepad_button_a_pressed : R.drawable.gamepad_button_a);
				buttonPressedName = gamepadActivity.getString(R.string.cast_gamepad_A);
				buttonPressed = Sensors.GAMEPAD_A_PRESSED;
				break;
			case R.id.gamepadButtonB:
				button.setImageResource(isActionDown ? R.drawable.gamepad_button_b_pressed : R.drawable.gamepad_button_b);
				buttonPressedName = gamepadActivity.getString(R.string.cast_gamepad_B);
				buttonPressed = Sensors.GAMEPAD_B_PRESSED;
				break;
			case R.id.gamepadButtonUp:
				buttonPressedName = gamepadActivity.getString(R.string.cast_gamepad_up);
				buttonPressed = Sensors.GAMEPAD_UP_PRESSED;
				break;
			case R.id.gamepadButtonDown:
				buttonPressedName = gamepadActivity.getString(R.string.cast_gamepad_down);
				buttonPressed = Sensors.GAMEPAD_DOWN_PRESSED;
				break;
			case R.id.gamepadButtonLeft:
				buttonPressedName = gamepadActivity.getString(R.string.cast_gamepad_left);
				buttonPressed = Sensors.GAMEPAD_LEFT_PRESSED;
				break;
			case R.id.gamepadButtonRight:
				buttonPressedName = gamepadActivity.getString(R.string.cast_gamepad_right);
				buttonPressed = Sensors.GAMEPAD_RIGHT_PRESSED;
				break;
			default:
				throw new IllegalArgumentException("Unknown button pressed");
		}
		setButtonPress(buttonPressed, isActionDown);

		if (isActionDown) {
			((StageListener) gamepadActivity.getApplicationListener()).gamepadPressed(buttonPressedName);
			button.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		}
	}

	public synchronized void addStageViewToLayout(GLSurfaceView20 stageView) {
		stageViewDisplayedOnCast = stageView;
		remoteLayout.setBackgroundColor(ContextCompat.getColor(initializingActivity, android.R.color.white));
		remoteLayout.removeAllViews();
		remoteLayout.addView(stageViewDisplayedOnCast);
		Project project = ProjectManager.getInstance().getCurrentProject();
		stageView.surfaceChanged(stageView.getHolder(), 0, project.getXmlHeader().getVirtualScreenWidth(),
				project.getXmlHeader().getVirtualScreenHeight());
	}

	public synchronized boolean currentlyConnecting() {
		return (!isConnected && selectedDevice != null);
	}

	public synchronized void openDeviceSelectorOrDisconnectDialog(AppCompatActivity activity) {
		SelectCastDialog dialog = new SelectCastDialog();
		dialog.show(activity.getSupportFragmentManager(), SelectCastDialog.TAG);
	}

	public synchronized void setCastButton(MenuItem castButton) {
		this.castButton = castButton;
		castButton.setVisible(mediaRouter.isRouteAvailable(mediaRouteSelector, MediaRouter.AVAILABILITY_FLAG_REQUIRE_MATCH));
		setIsConnected(isConnected);
	}

	public void selectRoute(MediaRouter.RouteInfo routeInfo) {
		mediaRouter.selectRoute(routeInfo);
	}

	public synchronized void setRemoteLayout(RelativeLayout remoteLayout) {
		this.remoteLayout = remoteLayout;
	}

	public synchronized void setRemoteLayoutToIdleScreen(Context context) {
		remoteLayout.removeAllViews();
		Drawable drawable = ContextCompat.getDrawable(context, R.drawable.idle_screen_1);
		remoteLayout.setBackground(drawable);
	}

	@SuppressLint("InflateParams")
	public synchronized void setRemoteLayoutToPauseScreen(Context context) {
		if (remoteLayout != null) {
			if ((pausedView == null) && !pausedScreenShowing) {
				pausedView = (RelativeLayout) LayoutInflater.from(context)
						.inflate(R.layout.cast_pause_screen, null);
				remoteLayout.addView(pausedView);
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) pausedView.getLayoutParams();
				Project p = ProjectManager.getInstance().getCurrentProject();
				layoutParams.height = p.getXmlHeader().getVirtualScreenHeight();
				layoutParams.width = p.getXmlHeader().getVirtualScreenWidth();
				pausedView.setLayoutParams(layoutParams);
				pausedView.setBackgroundColor(CAST_IDLE_BACKGROUND_COLOR);
				pausedScreenShowing = true;
			}
			pausedView.setVisibility(View.VISIBLE);
			pausedScreenShowing = true;
		}
	}

	public synchronized void resumeRemoteLayoutFromPauseScreen() {
		if (remoteLayout != null && pausedView != null) {
			pausedView.setVisibility(View.GONE);
			pausedScreenShowing = false;
		}
	}

	public synchronized void onStageDestroyed() {
		if (isConnected) {
			setRemoteLayoutToIdleScreen(initializingActivity);
		}
		stageViewDisplayedOnCast = null;
		pausedView = null;
		pausedScreenShowing = false;
	}

	private class MyMediaRouterCallback extends MediaRouter.Callback {

		private long lastConnectionTry;

		@Override
		public void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo info) {
			// Add route to list of discovered routes
			synchronized (this) {
				for (int i = 0; i < routeInfos.size(); i++) {
					MediaRouter.RouteInfo routeInfo = routeInfos.get(i);
					if (routeInfo.equals(info)) {
						routeInfos.remove(i);
					}
				}
				routeInfos.add(info);
				castButton.setVisible(mediaRouter.isRouteAvailable(mediaRouteSelector, MediaRouter
						.AVAILABILITY_FLAG_REQUIRE_MATCH));
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
						if (routeInfos.size() == 0) {
							castButton.setVisible(mediaRouter.isRouteAvailable(mediaRouteSelector, MediaRouter
									.AVAILABILITY_FLAG_REQUIRE_MATCH));
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
				lastConnectionTry = System.currentTimeMillis();
				// Show a msg if still connecting after CAST_CONNECTION_TIMEOUT milliseconds
				// and abort connection.
				isCastDeviceAvailable = (CastRemoteDisplayLocalService.getInstance() != null)
						&& (System.currentTimeMillis() - lastConnectionTry >= Constants.CAST_CONNECTION_TIMEOUT);
				(new Handler()).postDelayed(new Runnable() {
					@Override
					public void run() {
						synchronized (this) {
							if (currentlyConnecting() && isCastDeviceAvailable) {
								CastRemoteDisplayLocalService.stopService();
								ToastUtil.showError(initializingActivity,
										initializingActivity.getString(R.string.cast_connection_timout_msg));
							}
						}
					}
				}, Constants.CAST_CONNECTION_TIMEOUT);
			}
		}

		@Override
		public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
			onCastStop();
		}

		public synchronized void onCastStop() {

			if (stageViewDisplayedOnCast != null) {
				// Meaning that there is currently a stage being displayed on the remote screen
				gamepadActivity.onBackPressed();
			}
			stageViewDisplayedOnCast = null;
			setIsConnected(false);
			selectedDevice = null;
			gamepadActivity = null;
			remoteLayout = null;
			pausedView = null;
			pausedScreenShowing = false;
			CastRemoteDisplayLocalService.stopService();
		}

		public void startCastService(final AppCompatActivity activity) {

			Intent intent = new Intent(activity, activity.getClass());
			PendingIntent notificationPendingIntent;
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
				notificationPendingIntent = PendingIntent.getActivity(activity, 0,
						intent,PendingIntent.FLAG_IMMUTABLE);
			} else {
				notificationPendingIntent = PendingIntent.getActivity(activity, 0,
						intent, 0);
			}


			CastRemoteDisplayLocalService.NotificationSettings settings = new CastRemoteDisplayLocalService
					.NotificationSettings.Builder()
					.setNotificationPendingIntent(notificationPendingIntent).build();

			CastRemoteDisplayLocalService.Callbacks callbacks = new CastRemoteDisplayLocalService.Callbacks() {

				@Override
				public void onServiceCreated(CastRemoteDisplayLocalService castRemoteDisplayLocalService) {
				}

				@Override
				public void onRemoteDisplaySessionStarted(
						CastRemoteDisplayLocalService service) {
				}

				@Override
				public void onRemoteDisplaySessionError(Status errorReason) {
					onCastStop();
					activity.finish();
				}

				@Override
				public void onRemoteDisplaySessionEnded(CastRemoteDisplayLocalService castRemoteDisplayLocalService) {
				}
			};

			CastRemoteDisplayLocalService.startService(activity, CastService.class,
					Constants.REMOTE_DISPLAY_APP_ID, selectedDevice, settings, callbacks);
		}
	}
}
