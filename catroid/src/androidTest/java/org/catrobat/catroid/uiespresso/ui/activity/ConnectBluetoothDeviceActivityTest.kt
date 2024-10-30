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
package org.catrobat.catroid.uiespresso.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.R
import org.catrobat.catroid.bluetooth.ConnectBluetoothDeviceActivity
import org.catrobat.catroid.bluetooth.ConnectBluetoothDeviceActivity.DEVICE_TO_CONNECT
import org.catrobat.catroid.bluetooth.base.BluetoothDevice.MULTIPLAYER
import org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_MULTIPLAYER_BLUETOOTH_DIALOG_KEY
import org.catrobat.catroid.uiespresso.util.matchers.DrawableMatchers.withDrawable
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConnectBluetoothDeviceActivityTest {
    private var bufferedShowMultiplayerBluetoothDialog = true
    private lateinit var sharedPreferences: SharedPreferences

    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        ConnectBluetoothDeviceTestActivity::class.java, false, false
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        sharedPreferences = getDefaultSharedPreferences(getApplicationContext())
        bufferedShowMultiplayerBluetoothDialog = sharedPreferences.getBoolean(SHOW_MULTIPLAYER_BLUETOOTH_DIALOG_KEY, true)
        sharedPreferences
            .edit()
            .remove(SHOW_MULTIPLAYER_BLUETOOTH_DIALOG_KEY)
            .apply()
        val intent = Intent(getApplicationContext(), ConnectBluetoothDeviceTestActivity::class.java)
        intent.putExtra(DEVICE_TO_CONNECT, MULTIPLAYER)
        baseActivityTestRule.launchActivity(intent)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        sharedPreferences
            .edit()
            .putBoolean(SHOW_MULTIPLAYER_BLUETOOTH_DIALOG_KEY, bufferedShowMultiplayerBluetoothDialog)
            .commit()
    }

    @Test
    fun testMultiplayerDialogShowed() {
        onView(withText(R.string.multiplayer_bluetooth_connection_information_title))
            .check(matches(isDisplayed()))

        onView(withText(R.string.multiplayer_bluetooth_connection_information))
            .check(matches(isDisplayed()))

        onView(withText(R.string.got_it))
            .check(matches(isDisplayed()))
            .perform(click())

        assertFalse(sharedPreferences.getBoolean(SHOW_MULTIPLAYER_BLUETOOTH_DIALOG_KEY, true))
    }

    @Test
    fun testBluetoothConnectionScreen() {
        onView(withText(R.string.got_it))
            .perform(click())

        onView(withText(R.string.bluetooth_connection_title))
            .check(matches(isDisplayed()))

        onView(withId(R.id.skip_bluetooth))
            .check(matches(isDisplayed()))

        onView(withText(R.string.title_paired_devices))
            .check(matches(not(isDisplayed())))

        onView(withText(R.string.title_other_devices))
            .check(matches(isDisplayed()))

        onView(withId(R.id.bluetooth_scan))
            .check(matches(isDisplayed()))
            .check(matches(withDrawable(R.drawable.ic_search)))
            .perform(click())
            .check(matches(withDrawable(R.drawable.ic_close)))
    }

    class ConnectBluetoothDeviceTestActivity : ConnectBluetoothDeviceActivity() {
        override fun initBluetooth() = Unit
        override fun doDiscovery() = Unit
    }
}
