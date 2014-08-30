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
package org.catrobat.catroid.uitest.content.brick;

import android.bluetooth.BluetoothAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ArduinoSendBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class ArduinoSendBrickTest extends BaseActivityInstrumentationTestCase<ScriptActivity> {

	private Project project;
	private ArduinoSendBrick arduinoSendBrick;

	public ArduinoSendBrickTest() {
		super(ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		// normally super.setUp should be called first
		// but kept the test failing due to view is null
		// when starting in ScriptActivity
		createProject();
		super.setUp();
	}

	public void testArduinoSendBrick() {
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 2, dragDropListView.getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.brick_arduino_select_value)));

	}
	/*
		public void testArduinoSendBrickClickOnPinSpinner() {
			String[] arduinoPins = getActivity().getResources().getStringArray(R.array.arduino_pin_chooser);
			assertTrue("Spinner items list too short!", arduinoPins.length == 11);

			int newPinSpinnerPosition = 0;
			Spinner currentPinSpinner = solo.getCurrentViews(Spinner.class).get(newPinSpinnerPosition);
			//Pin Spinner
			solo.pressSpinnerItem(newPinSpinnerPosition, 0);
			assertEquals("Wrong item in spinner!", arduinoPins[0], currentPinSpinner.getSelectedItem());
			solo.pressSpinnerItem(newPinSpinnerPosition, +1);
			assertEquals("Wrong item in spinner!", arduinoPins[1], currentPinSpinner.getSelectedItem());
			solo.pressSpinnerItem(newPinSpinnerPosition, +1);
			assertEquals("Wrong item in spinner!", arduinoPins[2], currentPinSpinner.getSelectedItem());
			solo.pressSpinnerItem(newPinSpinnerPosition, +1);
			assertEquals("Wrong item in spinner!", arduinoPins[3], currentPinSpinner.getSelectedItem());
			solo.pressSpinnerItem(newPinSpinnerPosition, +1);
			assertEquals("Wrong item in spinner!", arduinoPins[4], currentPinSpinner.getSelectedItem());
			solo.pressSpinnerItem(newPinSpinnerPosition, +1);
			assertEquals("Wrong item in spinner!", arduinoPins[5], currentPinSpinner.getSelectedItem());
			solo.pressSpinnerItem(newPinSpinnerPosition, +1);
			assertEquals("Wrong item in spinner!", arduinoPins[6], currentPinSpinner.getSelectedItem());
			solo.pressSpinnerItem(newPinSpinnerPosition, +1);
			assertEquals("Wrong item in spinner!", arduinoPins[7], currentPinSpinner.getSelectedItem());
			solo.pressSpinnerItem(newPinSpinnerPosition, +1);
			assertEquals("Wrong item in spinner!", arduinoPins[8], currentPinSpinner.getSelectedItem());
			solo.pressSpinnerItem(newPinSpinnerPosition, +1);
			assertEquals("Wrong item in spinner!", arduinoPins[9], currentPinSpinner.getSelectedItem());
			solo.pressSpinnerItem(newPinSpinnerPosition, +1);
			assertEquals("Wrong item in spinner!", arduinoPins[10], currentPinSpinner.getSelectedItem());
		}
	*/
	public void testSetPinToHighWithPinAndValueSpinner() {
		//check if the Spinner list element length is correct
		String[] arduinoPins = getActivity().getResources().getStringArray(R.array.arduino_pin_chooser);
		assertTrue("Spinner items list too short!", arduinoPins.length == 11);
		String[] arduinoValues = getActivity().getResources().getStringArray(R.array.arduino_value_chooser);
		assertTrue("Spinner items list too short!", arduinoValues.length == 2);

		//select Value from Spinner (H)
		solo.pressSpinnerItem(1, 1);
		//Pin Spinner (Pin 13)
		solo.pressSpinnerItem(0, 10);
		solo.sleep(1000);

		//press play
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.sleep(300);

		//check if Bluetooth is enabled
		assertTrue("Bluetooth not enabled!", BluetoothAdapter.getDefaultAdapter().isEnabled());

		//select the Arduino Bluetooth board
		solo.clickOnText("ARDUINOBT");
		solo.sleep(5000);

		//press back
		solo.goBack();
		solo.sleep(1000);

		//press return
		solo.clickOnText("Back");
		solo.sleep(300);
	}

	public void testSetPinToLowWithPinAndValueSpinner() {
		//check if the Spinner list element length is correct
		String[] arduinoPins = getActivity().getResources().getStringArray(R.array.arduino_pin_chooser);
		assertTrue("Spinner items list too short!", arduinoPins.length == 11);
		String[] arduinoValues = getActivity().getResources().getStringArray(R.array.arduino_value_chooser);
		assertTrue("Spinner items list too short!", arduinoValues.length == 2);

		//select Value from Spinner (L)
		solo.pressSpinnerItem(1, 0);
		//Pin Spinner (Pin 13)
		solo.pressSpinnerItem(0, 10);
		solo.sleep(1000);

		//press play
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.sleep(300);

		//check if Bluetooth is enabled
		assertTrue("Bluetooth not enabled!", BluetoothAdapter.getDefaultAdapter().isEnabled());

		//select the Arduino Bluetooth board
		solo.clickOnText("ARDUINOBT");
		solo.sleep(5000);

		//press back
		solo.goBack();
		solo.sleep(1000);

		//press return
		solo.clickOnText("Back");
		solo.sleep(300);
	}

	private void createProject() {

		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("Arduino Brick");
		Script script = new StartScript();
		arduinoSendBrick = new ArduinoSendBrick();

		script.addBrick(arduinoSendBrick);
		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}