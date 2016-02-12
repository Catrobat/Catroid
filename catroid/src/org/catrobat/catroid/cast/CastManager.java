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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.view.HapticFeedbackConstants;
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

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.ui.dialogs.SelectCastDialog;

import java.util.ArrayList;
import java.util.EnumMap;

public final class CastManager {


    private static final String TAG = CastManager.class.getSimpleName();
    private static final CastManager INSTANCE = new CastManager();
    private EnumMap<Sensors, Boolean> isGamepadButtonPressed = new EnumMap<>(Sensors.class);

    private MediaRouter mediaRouter;
    private MediaRouteSelector mediaRouteSelector;
    private MyMediaRouterCallback callback;
    private ArrayList<String> routeNames = new ArrayList<>();
    private ArrayAdapter<String> deviceAdapter;
    private CastDevice selectedDevice;
    private boolean isConnected = false;
    private GLSurfaceView20 stageViewDisplayedOnCast;

    public void initializeGamepadActivity(StageActivity gamepadActivity) {
        this.gamepadActivity = gamepadActivity;
        initGamepadListeners();
    }

    StageActivity gamepadActivity;

    private Activity initializingActivity;
    private RelativeLayout remoteLayout;

    public void setIsConnected(boolean isConnected) {
        // Has to be called inside a sync block!

        int drawableId = isConnected ? R.drawable.ic_cast_connected_white_24dp : R.drawable.ic_cast_white_24dp;
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

    public static CastManager getInstance() {
        return INSTANCE;
    }

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
            for (String routeName : routeNames) {
                Log.d(TAG, routeName);
            }
            Log.d(TAG, "Logging finished...");
        }
    }

    public boolean isButtonPressed(Sensors btnSensor) {
        final boolean pressed;
        synchronized (this) {
            pressed = isGamepadButtonPressed.get(btnSensor);
        }
        return pressed;
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
        deviceAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, routeNames);
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

    private void handleGamepadTouch(ImageButton button, MotionEvent event) {

        if (event.getAction() != MotionEvent.ACTION_DOWN && event.getAction() != MotionEvent.ACTION_UP) {
            // We only care about the event when a gamepad button is pressed and when a gamepad button is unpressed
            return;
        }

        boolean isActionDown = (event.getAction() == MotionEvent.ACTION_DOWN);
        String buttonPressedName;

        switch (button.getId()) {
            case R.id.gamepadButtonA:
                buttonPressedName = gamepadActivity.getString(R.string.cast_gamepad_A);
                button.setImageResource(isActionDown ? R.drawable.gamepad_button_a_pressed : R.drawable.gamepad_button_a);
                setButtonPress(Sensors.GAMEPAD_A_PRESSED, isActionDown);
                break;
            case R.id.gamepadButtonB:
                buttonPressedName = gamepadActivity.getString(R.string.cast_gamepad_B);
                button.setImageResource(isActionDown ? R.drawable.gamepad_button_b_pressed : R.drawable.gamepad_button_b);
                setButtonPress(Sensors.GAMEPAD_B_PRESSED, isActionDown);
                break;
            case R.id.gamepadButtonUp:
                buttonPressedName = gamepadActivity.getString(R.string.cast_gamepad_up);
                setButtonPress(Sensors.GAMEPAD_UP_PRESSED, isActionDown);
                break;
            case R.id.gamepadButtonDown:
                buttonPressedName = gamepadActivity.getString(R.string.cast_gamepad_down);
                setButtonPress(Sensors.GAMEPAD_DOWN_PRESSED, isActionDown);
                break;
            case R.id.gamepadButtonLeft:
                buttonPressedName = gamepadActivity.getString(R.string.cast_gamepad_left);
                setButtonPress(Sensors.GAMEPAD_LEFT_PRESSED, isActionDown);
                break;
            case R.id.gamepadButtonRight:
                buttonPressedName = gamepadActivity.getString(R.string.cast_gamepad_right);
                setButtonPress(Sensors.GAMEPAD_RIGHT_PRESSED, isActionDown);
                break;
            default:
                throw new IllegalArgumentException("Unknown button pressed");
        }

        if (isActionDown) {
            ((StageListener) gamepadActivity.getApplicationListener()).gamepadPressed(buttonPressedName);
            button.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }
    }

    public void addStageViewToLayout(GLSurfaceView20 stageView) {
        stageViewDisplayedOnCast = stageView;
        remoteLayout.removeAllViews();
        remoteLayout.addView(stageViewDisplayedOnCast);
        stageView.surfaceChanged(stageView.getHolder(), 0, ScreenValues.CAST_SCREEN_WIDTH, ScreenValues.CAST_SCREEN_HEIGHT);

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

    public void setRemoteLayout(RelativeLayout remoteLayout) {
        this.remoteLayout = remoteLayout;
    }

    public void setRemoteLayoutToIdleScreen(Context context) {
        remoteLayout.removeAllViews();
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.idle_screen_1);
        remoteLayout.setBackground(drawable);
    }

    public void onStageDestroyed() {
        //Must be synced
        stageViewDisplayedOnCast = null;
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
                onCastStop();
            }
        }

        public void onCastStop() {

            // Needs to be called synchronized
            if (stageViewDisplayedOnCast != null) {
                // Meaning that there is currently a stage being displayed on the remote screen
                // TODO needs sync?
                gamepadActivity.onBackPressed();
            }
            onStageDestroyed();
            setIsConnected(false);
            selectedDevice = null;
            gamepadActivity = null;
            remoteLayout = null;
            CastRemoteDisplayLocalService.stopService();
        }

        public void startCastService(final Activity activity) {

            Intent intent = new Intent(activity, activity.getClass());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent notificationPendingIntent = PendingIntent.getActivity(activity, 0, intent, 0);


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
                    synchronized (this) {
                        onCastStop();
                        activity.finish();
                    }
                }
            };

            CastRemoteDisplayLocalService.startService(activity, CastService.class,
                    Constants.REMOTE_DISPLAY_APP_ID, selectedDevice, settings, callbacks);
        }

    }

}