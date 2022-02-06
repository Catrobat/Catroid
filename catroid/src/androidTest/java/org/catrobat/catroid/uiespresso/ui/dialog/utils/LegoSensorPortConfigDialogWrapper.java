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

package org.catrobat.catroid.uiespresso.ui.dialog.utils;

import org.catrobat.catroid.R;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.wrappers.ViewInteractionWrapper;

import java.lang.annotation.Retention;

import androidx.annotation.IntDef;
import androidx.test.espresso.ViewInteraction;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public final class LegoSensorPortConfigDialogWrapper extends ViewInteractionWrapper {

	@Retention(SOURCE)
	@IntDef({PORT_1, PORT_2, PORT_3, PORT_4})
	public @interface FormulaEditorLegoPorts {}
	public static final int PORT_1 = R.string.lego_port_1;
	public static final int PORT_2 = R.string.lego_port_2;
	public static final int PORT_3 = R.string.lego_port_3;
	public static final int PORT_4 = R.string.lego_port_4;

	@Retention(SOURCE)
	@IntDef({NXT_NO_SENSOR, NXT_SENSOR_LIGHT_ACTIVE, NXT_SENSOR_SOUND, NXT_SENSOR_TOUCH, NXT_SENSOR_ULTRASONIC})
	public @interface FormulaEditorNXTLegoSensors {}
	public static final int NXT_NO_SENSOR = R.string.nxt_no_sensor;
	public static final int NXT_SENSOR_LIGHT_ACTIVE = R.string.nxt_sensor_light_active;
	public static final int NXT_SENSOR_SOUND = R.string.nxt_sensor_sound;
	public static final int NXT_SENSOR_TOUCH = R.string.nxt_sensor_touch;
	public static final int NXT_SENSOR_ULTRASONIC = R.string.nxt_sensor_ultrasonic;

	private LegoSensorPortConfigDialogWrapper(ViewInteraction viewInteraction) {
		super(viewInteraction);
	}

	public static LegoSensorPortConfigDialogWrapper onLegoSensorPortConfigDialog(@FormulaEditorNXTLegoSensors int sensorStringResource) {
		String sensorString = UiTestUtils.getResourcesString(sensorStringResource);
		String legoSensorConfigDialogTitle = UiTestUtils
				.getResourcesStringWithArgs(R.string.lego_sensor_port_config_dialog_title, sensorString);
		return new LegoSensorPortConfigDialogWrapper(onView(withText(legoSensorConfigDialogTitle)));
	}

	public LegoSensorPortConfigDialogWrapper performClickOnOK() {
		onView(withText(R.string.ok)).perform(click());
		return this;
	}

	public LegoSensorPortConfigDialogWrapper checkDialogVisible() {
		viewInteraction.inRoot(isDialog()).check(matches(isDisplayed()));
		return this;
	}

	public LegoSensorPortConfigDialogWrapper performClickOnPort(@FormulaEditorLegoPorts int portStringResource, @FormulaEditorNXTLegoSensors int previousSensor) {
		String sensor = UiTestUtils.getResourcesString(previousSensor);
		String formulaEditorPortString = UiTestUtils.getResourcesString(portStringResource) + ": " + sensor;
		onView(withText(formulaEditorPortString)).perform(click());
		return this;
	}

	public LegoSensorPortConfigDialogWrapper checkPortDisplayed(@FormulaEditorLegoPorts int portStringRes, @FormulaEditorNXTLegoSensors int previousSensor) {
		String sensor = UiTestUtils.getResourcesString(previousSensor);
		String formulaEditorPortString = UiTestUtils.getResourcesString(portStringRes) + ": " + sensor;
		onView(withText(formulaEditorPortString)).check(matches(isDisplayed()));
		return this;
	}
}
