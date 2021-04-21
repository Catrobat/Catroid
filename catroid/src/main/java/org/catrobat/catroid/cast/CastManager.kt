/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.cast

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Handler
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.mediarouter.media.MediaRouteSelector
import androidx.mediarouter.media.MediaRouter
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20
import com.google.android.gms.cast.CastDevice
import com.google.android.gms.cast.CastMediaControlIntent
import com.google.android.gms.cast.CastRemoteDisplayLocalService
import com.google.android.gms.cast.CastRemoteDisplayLocalService.NotificationSettings
import com.google.android.gms.common.api.Status
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.bricks.CameraBrick
import org.catrobat.catroid.content.bricks.ChooseCameraBrick
import org.catrobat.catroid.content.bricks.FlashBrick
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageListener
import org.catrobat.catroid.ui.adapter.CastDevicesAdapter
import org.catrobat.catroid.ui.dialogs.SelectCastDialog
import org.catrobat.catroid.utils.ToastUtil
import java.util.ArrayList
import java.util.EnumMap

class CastManager private constructor() {
    val routeInfos = ArrayList<MediaRouter.RouteInfo>()
    private var gamepadActivity: StageActivity? = null
    private val isGamepadButtonPressed = EnumMap<Sensors, Boolean>(
        Sensors::class.java
    )
    var mediaRouter: MediaRouter? = null
        private set
    private var mediaRouteSelector: MediaRouteSelector? = null
    private var callback: MyMediaRouterCallback? = null
    var deviceAdapter: ArrayAdapter<MediaRouter.RouteInfo>? = null
        private set
    var selectedDevice: CastDevice? = null
        private set
    private var isConnected = false
    private var stageViewDisplayedOnCast: GLSurfaceView20? = null
    private var initializingActivity: AppCompatActivity? = null
    private var remoteLayout: RelativeLayout? = null
    private var pausedView: RelativeLayout? = null
    private var castButton: MenuItem? = null
    private var pausedScreenShowing = false
    private var isCastDeviceAvailable = false
    @Synchronized
    fun initializeGamepadActivity(gamepadActivity: StageActivity?) { //TODO needs to be synced?
        this.gamepadActivity = gamepadActivity
        initGamepadListeners()
    }

    @Synchronized
    fun setIsConnected(isConnected: Boolean) {
        val drawableId =
            if (isConnected) R.drawable.ic_cast_connected_white else R.drawable.ic_cast_white
        castButton!!.setIcon(drawableId)
        this.isConnected = isConnected
        initializingActivity!!.invalidateOptionsMenu()
    }

    fun startCastButtonAnimation() {
        val drawableId = R.drawable.animation_cast_button_connecting
        castButton!!.setIcon(drawableId)
        (castButton!!.icon as AnimationDrawable).start()
    }

    @Synchronized
    fun isConnected(): Boolean {
        return isConnected
    }

    fun isButtonPressed(btnSensor: Sensors?): Boolean {
        return isGamepadButtonPressed[btnSensor]!!
    }

    fun setButtonPress(btn: Sensors, b: Boolean) {
        isGamepadButtonPressed[btn] = b
    }

    @Synchronized
    fun initializeCast(activity: AppCompatActivity) {
        initializingActivity = activity
        if (mediaRouter != null) {
            return
        }
        deviceAdapter =
            CastDevicesAdapter(activity, R.layout.fragment_cast_device_list_item, routeInfos)
        mediaRouter = MediaRouter.getInstance(activity.applicationContext)
        mediaRouteSelector = MediaRouteSelector.Builder()
            .addControlCategory(CastMediaControlIntent.categoryForCast(Constants.REMOTE_DISPLAY_APP_ID))
            .build()
        setCallback()
    }

    fun addCallback() {
        callback = MyMediaRouterCallback()
        mediaRouter!!.addCallback(
            mediaRouteSelector!!,
            callback!!,
            MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY
        )
    }

    @Synchronized
    fun setCallback() {
        setCallback(MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY)
    }

    @Synchronized
    fun setCallback(callbackFlag: Int) {
        if (callback == null) {
            callback = MyMediaRouterCallback()
        }
        mediaRouter!!.addCallback(mediaRouteSelector!!, callback!!, callbackFlag)
    }

    fun openDeviceSelectorOrDisconnectDialog() {
        openDeviceSelectorOrDisconnectDialog(initializingActivity)
    }

    private fun initGamepadListeners() {
        val pauseButtonListener = View.OnClickListener { v ->
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            gamepadActivity!!.onBackPressed()
        }
        gamepadActivity!!.findViewById<View>(R.id.gamepadPauseButton)
            .setOnClickListener(pauseButtonListener)
        val otl = OnTouchListener { v, event ->
            handleGamepadTouch(v as ImageButton, event)
            true
        }
        val gamepadButtons = arrayOf(
            gamepadActivity!!.findViewById<View>(R.id.gamepadButtonA) as ImageButton,
            gamepadActivity!!.findViewById<View>(R.id.gamepadButtonB) as ImageButton,
            gamepadActivity!!.findViewById<View>(R.id.gamepadButtonUp) as ImageButton,
            gamepadActivity!!.findViewById<View>(R.id.gamepadButtonDown) as ImageButton,
            gamepadActivity!!.findViewById<View>(R.id.gamepadButtonLeft) as ImageButton,
            gamepadActivity!!.findViewById<View>(R.id.gamepadButtonRight) as ImageButton
        )
        for (btn in gamepadButtons) {
            btn.setOnTouchListener(otl)
        }
    }

    @Synchronized
    private fun handleGamepadTouch(button: ImageButton, event: MotionEvent) {
        if (event.action != MotionEvent.ACTION_DOWN && event.action != MotionEvent.ACTION_UP) {
            // We only care about the event when a gamepad button is pressed and when a gamepad button is unpressed
            return
        }
        if (gamepadActivity == null) {
            return
        }
        val isActionDown = event.action == MotionEvent.ACTION_DOWN
        val buttonPressed: Sensors
        val buttonPressedName: String
        when (button.id) {
            R.id.gamepadButtonA -> {
                button.setImageResource(if (isActionDown) R.drawable.gamepad_button_a_pressed else R.drawable.gamepad_button_a)
                buttonPressedName = gamepadActivity!!.getString(R.string.cast_gamepad_A)
                buttonPressed = Sensors.GAMEPAD_A_PRESSED
            }
            R.id.gamepadButtonB -> {
                button.setImageResource(if (isActionDown) R.drawable.gamepad_button_b_pressed else R.drawable.gamepad_button_b)
                buttonPressedName = gamepadActivity!!.getString(R.string.cast_gamepad_B)
                buttonPressed = Sensors.GAMEPAD_B_PRESSED
            }
            R.id.gamepadButtonUp -> {
                buttonPressedName = gamepadActivity!!.getString(R.string.cast_gamepad_up)
                buttonPressed = Sensors.GAMEPAD_UP_PRESSED
            }
            R.id.gamepadButtonDown -> {
                buttonPressedName = gamepadActivity!!.getString(R.string.cast_gamepad_down)
                buttonPressed = Sensors.GAMEPAD_DOWN_PRESSED
            }
            R.id.gamepadButtonLeft -> {
                buttonPressedName = gamepadActivity!!.getString(R.string.cast_gamepad_left)
                buttonPressed = Sensors.GAMEPAD_LEFT_PRESSED
            }
            R.id.gamepadButtonRight -> {
                buttonPressedName = gamepadActivity!!.getString(R.string.cast_gamepad_right)
                buttonPressed = Sensors.GAMEPAD_RIGHT_PRESSED
            }
            else -> throw IllegalArgumentException("Unknown button pressed")
        }
        setButtonPress(buttonPressed, isActionDown)
        if (isActionDown) {
            (gamepadActivity!!.applicationListener as StageListener).gamepadPressed(
                buttonPressedName
            )
            button.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }

    @Synchronized
    fun addStageViewToLayout(stageView: GLSurfaceView20) {
        stageViewDisplayedOnCast = stageView
        remoteLayout!!.setBackgroundColor(
            ContextCompat.getColor(
                initializingActivity!!,
                android.R.color.white
            )
        )
        remoteLayout!!.removeAllViews()
        remoteLayout!!.addView(stageViewDisplayedOnCast)
        val project = ProjectManager.getInstance().currentProject
        stageView.surfaceChanged(
            stageView.holder, 0, project.xmlHeader.getVirtualScreenWidth(),
            project.xmlHeader.getVirtualScreenHeight()
        )
    }

    @Synchronized
    fun currentlyConnecting(): Boolean {
        return !isConnected && selectedDevice != null
    }

    @Synchronized
    fun openDeviceSelectorOrDisconnectDialog(activity: AppCompatActivity?) {
        val dialog = SelectCastDialog()
        dialog.show(activity!!.supportFragmentManager, SelectCastDialog.TAG)
    }

    @Synchronized
    fun setCastButton(castButton: MenuItem) {
        this.castButton = castButton
        castButton.isVisible = mediaRouter!!.isRouteAvailable(
            mediaRouteSelector!!,
            MediaRouter.AVAILABILITY_FLAG_REQUIRE_MATCH
        )
        setIsConnected(isConnected)
    }

    fun selectRoute(routeInfo: MediaRouter.RouteInfo?) {
        mediaRouter!!.selectRoute(routeInfo!!)
    }

    @Synchronized
    fun setRemoteLayout(remoteLayout: RelativeLayout?) {
        this.remoteLayout = remoteLayout
    }

    @Synchronized
    fun setRemoteLayoutToIdleScreen(context: Context?) {
        remoteLayout!!.removeAllViews()
        val drawable = ContextCompat.getDrawable(context!!, R.drawable.idle_screen_1)
        remoteLayout!!.background = drawable
    }

    @SuppressLint("InflateParams")
    @Synchronized
    fun setRemoteLayoutToPauseScreen(context: Context?) {
        if (remoteLayout != null) {
            if (pausedView == null && !pausedScreenShowing) {
                pausedView = LayoutInflater.from(context)
                    .inflate(R.layout.cast_pause_screen, null) as RelativeLayout
                remoteLayout!!.addView(pausedView)
                val layoutParams = pausedView!!.layoutParams as RelativeLayout.LayoutParams
                val p = ProjectManager.getInstance().currentProject
                layoutParams.height = p.xmlHeader.getVirtualScreenHeight()
                layoutParams.width = p.xmlHeader.getVirtualScreenWidth()
                pausedView!!.layoutParams = layoutParams
                pausedView!!.setBackgroundColor(Constants.CAST_IDLE_BACKGROUND_COLOR)
                pausedScreenShowing = true
            }
            pausedView!!.visibility = View.VISIBLE
            pausedScreenShowing = true
        }
    }

    @Synchronized
    fun resumeRemoteLayoutFromPauseScreen() {
        if (remoteLayout != null && pausedView != null) {
            pausedView!!.visibility = View.GONE
            pausedScreenShowing = false
        }
    }

    @Synchronized
    fun onStageDestroyed() {
        if (isConnected) {
            setRemoteLayoutToIdleScreen(initializingActivity)
        }
        stageViewDisplayedOnCast = null
        pausedView = null
        pausedScreenShowing = false
    }

    private inner class MyMediaRouterCallback : MediaRouter.Callback() {
        private var lastConnectionTry: Long = 0
        override fun onRouteAdded(router: MediaRouter, info: MediaRouter.RouteInfo) {
            // Add route to list of discovered routes
            synchronized(this) {
                for (i in routeInfos.indices) {
                    val routeInfo = routeInfos[i]
                    if (routeInfo == info) {
                        routeInfos.removeAt(i)
                    }
                }
                routeInfos.add(info)
                castButton!!.isVisible = mediaRouter!!.isRouteAvailable(
                    mediaRouteSelector!!,
                    MediaRouter.AVAILABILITY_FLAG_REQUIRE_MATCH
                )
                deviceAdapter!!.notifyDataSetChanged()
            }
        }

        override fun onRouteRemoved(router: MediaRouter, info: MediaRouter.RouteInfo) {
            // Remove route from list of routes
            synchronized(this) {
                for (i in routeInfos.indices) {
                    val routeInfo = routeInfos[i]
                    if (routeInfo == info) {
                        routeInfos.removeAt(i)
                        if (routeInfos.size == 0) {
                            castButton!!.isVisible = mediaRouter!!.isRouteAvailable(
                                mediaRouteSelector!!,
                                MediaRouter.AVAILABILITY_FLAG_REQUIRE_MATCH
                            )
                        }
                        deviceAdapter!!.notifyDataSetChanged()
                    }
                }
            }
        }

        override fun onRouteSelected(router: MediaRouter, info: MediaRouter.RouteInfo) {
            synchronized(this) {
                selectedDevice = CastDevice.getFromBundle(info.extras)
                startCastService(initializingActivity)
                lastConnectionTry = System.currentTimeMillis()
                // Show a msg if still connecting after CAST_CONNECTION_TIMEOUT milliseconds
                // and abort connection.
                isCastDeviceAvailable = (CastRemoteDisplayLocalService.getInstance() != null
                    && System.currentTimeMillis() - lastConnectionTry >= Constants.CAST_CONNECTION_TIMEOUT)
                Handler().postDelayed(object : Runnable {
                    override fun run() {
                        synchronized(this) {
                            if (currentlyConnecting() && isCastDeviceAvailable) {
                                CastRemoteDisplayLocalService.stopService()
                                ToastUtil.showError(
                                    initializingActivity,
                                    initializingActivity!!.getString(R.string.cast_connection_timout_msg)
                                )
                            }
                        }
                    }
                }, Constants.CAST_CONNECTION_TIMEOUT.toLong())
            }
        }

        override fun onRouteUnselected(router: MediaRouter, info: MediaRouter.RouteInfo) {
            onCastStop()
        }

        @Synchronized
        fun onCastStop() {
            if (stageViewDisplayedOnCast != null) {
                // Meaning that there is currently a stage being displayed on the remote screen
                gamepadActivity!!.onBackPressed()
            }
            stageViewDisplayedOnCast = null
            setIsConnected(false)
            selectedDevice = null
            gamepadActivity = null
            remoteLayout = null
            pausedView = null
            pausedScreenShowing = false
            CastRemoteDisplayLocalService.stopService()
        }

        fun startCastService(activity: AppCompatActivity?) {
            val intent = Intent(activity, activity!!.javaClass)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val notificationPendingIntent = PendingIntent.getActivity(activity, 0, intent, 0)
            val settings = NotificationSettings.Builder()
                .setNotificationPendingIntent(notificationPendingIntent).build()
            val callbacks: CastRemoteDisplayLocalService.Callbacks =
                object : CastRemoteDisplayLocalService.Callbacks {
                    override fun onServiceCreated(castRemoteDisplayLocalService: CastRemoteDisplayLocalService) {}
                    override fun onRemoteDisplaySessionStarted(
                        service: CastRemoteDisplayLocalService
                    ) {
                    }

                    override fun onRemoteDisplaySessionError(errorReason: Status) {
                        onCastStop()
                        activity!!.finish()
                    }

                    override fun onRemoteDisplaySessionEnded(castRemoteDisplayLocalService: CastRemoteDisplayLocalService) {}
                }
            CastRemoteDisplayLocalService.startService(
                activity, CastService::class.java,
                Constants.REMOTE_DISPLAY_APP_ID, selectedDevice, settings, callbacks
            )
        }
    }

    companion object {
        @JvmStatic
        val instance = CastManager()
        @JvmField
		var unsupportedBricks: ArrayList<Class<*>?> = object : ArrayList<Class<*>?>() {
            init {
                add(CameraBrick::class.java)
                add(ChooseCameraBrick::class.java)
                add(FlashBrick::class.java)
            }
        }
    }

    init {
        isGamepadButtonPressed[Sensors.GAMEPAD_A_PRESSED] = false
        isGamepadButtonPressed[Sensors.GAMEPAD_B_PRESSED] = false
        isGamepadButtonPressed[Sensors.GAMEPAD_LEFT_PRESSED] = false
        isGamepadButtonPressed[Sensors.GAMEPAD_RIGHT_PRESSED] = false
        isGamepadButtonPressed[Sensors.GAMEPAD_UP_PRESSED] = false
        isGamepadButtonPressed[Sensors.GAMEPAD_DOWN_PRESSED] = false
    }
}