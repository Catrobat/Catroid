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
package at.tugraz.ist.catroid.uitest.stage;

import java.io.File;
import java.util.ArrayList;

import android.bluetooth.BluetoothAdapter;
import android.graphics.BitmapFactory;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.ListView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.LegoNXT.LegoNXTBtCommunicator;
import at.tugraz.ist.catroid.LegoNXT.LegoNXTCommunicator;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.NXTMotorActionBrick;
import at.tugraz.ist.catroid.content.bricks.NXTMotorStopBrick;
import at.tugraz.ist.catroid.content.bricks.NXTMotorTurnAngleBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class LegoNXTTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private StorageHandler storageHandler;
	private final String projectName = UiTestUtils.PROJECTNAME1;

	private File image1;
	private String imageName1 = "image1";
	private static final int IMAGE_FILE_ID = at.tugraz.ist.catroid.uitest.R.raw.icon;
	private static final int MOTORACTION = 0;
	private static final int MOTORSTOP = 1;
	private static final int MOTORTURN = 2;

	public static final String LegoNXTBTStringStartsWith = "NXT";
	public static final String TestServerBTStringStartsWith = "PETER";

	ArrayList<int[]> commands = new ArrayList<int[]>();

	public LegoNXTTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
		storageHandler = StorageHandler.getInstance();
	}

	@Override
	public void setUp() throws Exception {
		UiTestUtils.clearAllUtilTestProjects();

		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		UiTestUtils.clearAllUtilTestProjects();

		getActivity().finish();
		super.tearDown();
	}

	// This test requires the NXTBTTestServer to be running or a LegoNXT Robot to run! Check connect string to see if you connect to the right device!
	public void testNXTFunctionality() {
		createTestproject(projectName);

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		assertTrue("Bluetooth not supported on device", bluetoothAdapter != null);
		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
			solo.sleep(5000);
		}

		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);

		solo.sleep(2000);

		ListView list = solo.getCurrentListViews().get(0);
		String fullConnectionString = null;
		for (int i = 0; i < solo.getCurrentListViews().get(0).getCount(); i++) {

			String current = (String) list.getItemAtPosition(i);
			if (current.startsWith(TestServerBTStringStartsWith)) {
				fullConnectionString = current;
				break;
			}
		}

		solo.clickOnText(fullConnectionString);
		solo.sleep(5000);

		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
		solo.sleep(8000);

		Log.i("bt", "" + LegoNXTCommunicator.getReceivedMessageList().size());
		solo.sleep(2000);

		ArrayList<byte[]> executed_commands = LegoNXTCommunicator.getReceivedMessageList();
		assertEquals("Commands seem to have not been executed! Connected to correct device??", commands.size(),
				executed_commands.size());

		int i = 0;
		for (int[] item : commands) {

			switch (item[0]) {
				case MOTORACTION:
					assertEquals("Wrong motor was used!", item[1], executed_commands.get(i)[3]);
					assertEquals("Wrong speed was used!", item[2], executed_commands.get(i)[4]);
					break;
				case MOTORSTOP:
					assertEquals("Wrong motor was used!", item[1], executed_commands.get(i)[3]);
					assertEquals("Motor didnt actually stop!", 0, executed_commands.get(i)[4]);
					break;
				case MOTORTURN:
					for (int j = 0; j < executed_commands.get(i).length; j++) {
						Log.i("bt", "i" + j + ": " + (int) executed_commands.get(i)[j]);
					}
					assertEquals("Wrong motor was used!", item[1], executed_commands.get(i)[3]);
					int turnValue = 0;
					turnValue = (0x000000FF & executed_commands.get(i)[9]); //unsigned types would be too smart for java, sorry no chance mate!
					turnValue += ((0x000000FF & executed_commands.get(i)[10]) << 8);
					turnValue += ((0x000000FF & executed_commands.get(i)[11]) << 16);
					turnValue += ((0x000000FF & executed_commands.get(i)[12]) << 24);

					int turnSpeed = 30; //fixed value in Brick, however LegoBot needs negative speed instead of negative angles 
					if (item[2] < 0) {
						item[2] += -2 * item[2];
						turnSpeed -= 2 * turnSpeed;
					}

					assertEquals("Motor turned wrong angle", item[2], turnValue);
					assertEquals("Motor didnt turn with fixed value 30!", turnSpeed, executed_commands.get(i)[4]);
					break;

			}
			i++;
		}
		solo.goBack();
		solo.goBack();
		//solo.goBack();
		solo.sleep(2000);
	}

	// This test requires the NXTBTTestServer to be running or a LegoNXT Robot to run! Check connect string to see if you connect to the right device!
	public void testNXTPersistentConnection() {
		createTestproject(projectName);

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		assertTrue("Bluetooth not supported on device", bluetoothAdapter != null);
		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
			solo.sleep(5000);
		}

		solo.clickOnButton(0);
		solo.sleep(1000);
		solo.clickOnText("sprite1");
		solo.sleep(1000);

		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);
		solo.sleep(1500);

		ListView list = solo.getCurrentListViews().get(0);
		String fullConnectionString = null;
		for (int i = 0; i < solo.getCurrentListViews().get(0).getCount(); i++) {

			String current = (String) list.getItemAtPosition(i);
			if (current.startsWith(TestServerBTStringStartsWith)) {
				fullConnectionString = current;
				break;
			}
		}

		solo.clickOnText(fullConnectionString);
		solo.sleep(5000); // if null pointer exception somewhere, increase this sleep!

		solo.goBack();
		solo.sleep(500);
		solo.goBack();
		solo.sleep(1000);
		solo.goBack();
		solo.sleep(500);
		//Device is still connected (until visiting main menu or exiting program)!
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);
		solo.sleep(3000);
		solo.assertCurrentActivity("lol", StageActivity.class);

		solo.goBack();
		solo.sleep(500);
		solo.goBack();
		solo.sleep(1000);
		solo.goBack();
		solo.sleep(1000);
		//main menu => device disconnected!
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);
		solo.sleep(1500);
		assertTrue("I should be on the bluetooth device choosing screen, but am not! Device still connected??",
				solo.searchText(fullConnectionString));

		solo.goBack();
		solo.sleep(2000);

	}

	public void createTestproject(String projectName) {

		Sprite firstSprite = new Sprite("sprite1");
		Script startScript = new StartScript("startScript", firstSprite);
		Script whenScript = new WhenScript("whenScript", firstSprite);
		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);

		LegoNXTBtCommunicator.enableRequestConfirmFromDevice();

		NXTMotorActionBrick nxt = new NXTMotorActionBrick(firstSprite, 3, 100);
		commands.add(new int[] { MOTORACTION, 0, 100 }); //motor = 3 means brick will move motors A and C.
		commands.add(new int[] { MOTORACTION, 2, 100 });
		WaitBrick wait = new WaitBrick(firstSprite, 1000);

		NXTMotorStopBrick nxtStop = new NXTMotorStopBrick(firstSprite, 3);
		commands.add(new int[] { MOTORSTOP, 0 });
		commands.add(new int[] { MOTORSTOP, 2 });
		WaitBrick wait2 = new WaitBrick(firstSprite, 1000);

		NXTMotorTurnAngleBrick nxtTurn = new NXTMotorTurnAngleBrick(firstSprite, 2, 515);
		commands.add(new int[] { MOTORTURN, 2, 515 });

		//		WaitBrick wait3 = new WaitBrick(firstSprite, 1000);
		//		NXTPlayToneBrick nxtTone = new NXTPlayToneBrick(firstSprite, 50, 1);
		//Tone does not return a command

		whenScript.addBrick(nxt);
		whenScript.addBrick(wait);
		whenScript.addBrick(nxtStop);
		whenScript.addBrick(wait2);
		whenScript.addBrick(nxtTurn);
		//		whenScript.addBrick(wait3);
		//		whenScript.addBrick(nxtTone);

		startScript.addBrick(setCostumeBrick);
		firstSprite.addScript(startScript);
		firstSprite.addScript(whenScript);

		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(firstSprite);
		Project project = UiTestUtils.createProject(projectName, spriteList, getActivity());

		image1 = UiTestUtils.saveFileToProject(projectName, imageName1, IMAGE_FILE_ID, getInstrumentation()
				.getContext(), UiTestUtils.TYPE_IMAGE_FILE);
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(image1.getAbsolutePath(), o);

		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(image1.getName());
		costumeData.setCostumeName("image1");
		setCostumeBrick.setCostume(costumeData);
		firstSprite.getCostumeDataList().add(costumeData);

		storageHandler.saveProject(project);
	}

}
