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

package at.tugraz.ist.catroid.uitest.ui.dialog;

import android.bluetooth.BluetoothAdapter;
import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.uitest.util.Utils;

import com.jayway.android.robotium.solo.Solo;

public class SensorDialogTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	BluetoothAdapter bluetoothAdapter;

	public SensorDialogTest() {
		super("at.tugraz.ist.catroid", ScriptActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Utils.createTestProject();
		solo = new Solo(getInstrumentation(), getActivity());
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
		}

	}

	@Override
	protected void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		Utils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testBluetoothConnection() {

		//TODO Check if BTImagebutton changes color
		if (bluetoothAdapter != null) {
			if (bluetoothAdapter.isEnabled()) {
				bluetoothAdapter.disable();
			}

			assertEquals(false, bluetoothAdapter.isEnabled());

			bluetoothAdapter.enable();

			assertEquals(true, bluetoothAdapter.isEnabled());
		}

	}

	public void testBluetoothScanDialog() {
		Utils.addNewBrickAndScrollDown(solo, R.string.sensor_main_adapter);

		solo.clickOnButton("B");

		solo.getViews().size();

		int bluetoothScanButton = solo.getCurrentButtons().size() - 2;
		int bluetoothExitButton = solo.getCurrentButtons().size() - 1;

		solo.clickOnButton(bluetoothScanButton);
		//solo.sleep(2000);
		solo.clickOnButton(bluetoothExitButton);

	}

	public void testDigitalSensorValues() {
		Utils.addNewBrickAndScrollDown(solo, R.string.sensor_main_adapter);

		int pinValue = 5;
		int valueDigital = 1;
		double time = 4.0;

		int pinTextEdits = solo.getCurrentEditTexts().size() - 3;
		int valueTextEdits = solo.getCurrentEditTexts().size() - 2;
		int timeTextEdits = solo.getCurrentEditTexts().size() - 1;

		solo.clickOnButton("D");

		Utils.insertIntegerIntoEditText(solo, pinTextEdits, pinValue);
		solo.clickOnButton("OK");

		Utils.insertIntegerIntoEditText(solo, valueTextEdits, valueDigital);
		solo.clickOnButton("OK");

		Utils.insertDoubleIntoEditText(solo, timeTextEdits, time);
		solo.clickOnButton("OK");

	}

	public void testAnalogSensorValues() {
		Utils.addNewBrickAndScrollDown(solo, R.string.sensor_main_adapter);

		int pinValue = 5;
		double valueAnalog = 3.2;
		double time = 4.5;

		int pinTextEdits = solo.getCurrentEditTexts().size() - 3;
		int valueTextEdits = solo.getCurrentEditTexts().size() - 2;
		int timeTextEdits = solo.getCurrentEditTexts().size() - 1;

		solo.clickOnButton("A");

		solo.clickOnEditText(pinTextEdits);
		solo.clearEditText(0);
		solo.enterText(0, Integer.toString(pinValue));
		solo.clickOnButton(0);
		solo.sleep(3000);

		Utils.insertDoubleIntoEditText(solo, valueTextEdits, valueAnalog);
		solo.clickOnButton("OK");
		solo.sleep(3000);

	}

	public void testFalseDigitalSensorValues() {
		Utils.addNewBrickAndScrollDown(solo, R.string.sensor_main_adapter);

		int pinValue = 20;
		int valueDigital = 3;
		double time = 4.5;

		int pinTextEdits = solo.getCurrentEditTexts().size() - 1;
		int valueTextEdits = solo.getCurrentEditTexts().size() - 2;
		int timeTextEdits = solo.getCurrentEditTexts().size() - 3;

		solo.clickOnButton("D");

		Utils.insertIntegerIntoEditText(solo, pinTextEdits, pinValue);
		solo.clickOnButton("OK");

		Utils.insertIntegerIntoEditText(solo, valueTextEdits, valueDigital);
		solo.clickOnButton("OK");

		Utils.insertDoubleIntoEditText(solo, timeTextEdits, time);
		solo.clickOnButton("OK");

		assertEquals("You just have 0 to 13 PINs here so please a number from 0-13", pinValue + "",
				solo.getEditText(pinTextEdits).getText().toString());
		assertEquals("For this PIN you can just enter 1 for HIGH or 0 for LOW", valueDigital + "",
				solo.getEditText(valueTextEdits).getText().toString());
	}

	public void testFalseAnalogSensorValues() {
		Utils.addNewBrickAndScrollDown(solo, R.string.sensor_main_adapter);

		int pinValue = 20;
		double valueAnalog = 8.0;
		double time = 4.5;

		int pinTextEdits = solo.getCurrentEditTexts().size() - 3;
		int valueTextEdits = solo.getCurrentEditTexts().size() - 2;
		int timeTextEdits = solo.getCurrentEditTexts().size() - 1;

		solo.clickOnButton("A");

		Utils.insertIntegerIntoEditText(solo, pinTextEdits, pinValue);
		solo.clickOnButton("OK");

		assertTrue(solo.searchText("You just have 0 to 5 PINs here so please a number from 0-5"));

		Utils.insertDoubleIntoEditText(solo, valueTextEdits, valueAnalog);
		solo.clickOnButton("OK");

		assertEquals("For this PIN you can just enter a value from 0.0 to 5.0", valueAnalog + "",
				solo.getEditText(valueTextEdits).getText().toString());

		Utils.insertDoubleIntoEditText(solo, timeTextEdits, time);
		solo.clickOnButton("OK");

	}
}
