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
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
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
	private int image1Width;
	private int image1Height;

	public static final String LegoNXTBTStringStartsWith = "NXT";
	public static final String TestServerBTStringStartsWith = "PETER";

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

	public void testNXTStuff() {
		createTestproject(projectName);

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		assertTrue(bluetoothAdapter != null); //Bluetooth not supported on device
		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
		}

		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);

		solo.sleep(2000);

		int index = 0;
		ListView list = solo.getCurrentListViews().get(0);

		for (int i = 0; i < solo.getCurrentListViews().get(0).getCount(); i++) {

			String current = (String) list.getItemAtPosition(i);
			if (current.startsWith(TestServerBTStringStartsWith)) {
				break;
			}
			index++;

		}

		solo.clickInList(index);
		solo.sleep(5000);

		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
		solo.sleep(8000);

		Log.i("bt", "" + LegoNXTCommunicator.getReceivedMessageList().size());
		solo.sleep(2000);
	}

	public void createTestproject(String projectName) {

		//creating sprites for project:
		Sprite firstSprite = new Sprite("sprite1");
		Script startScript = new StartScript("startScript", firstSprite);
		Script whenScript = new WhenScript("whenScript", firstSprite);
		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);
		NXTMotorActionBrick nxt = new NXTMotorActionBrick(firstSprite, 3, 100);
		WaitBrick wait = new WaitBrick(firstSprite, 3000);
		NXTMotorStopBrick nxtStop = new NXTMotorStopBrick(firstSprite, 3);

		whenScript.addBrick(nxt);
		whenScript.addBrick(wait);
		whenScript.addBrick(nxtStop);

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
		image1Width = o.outWidth;
		image1Height = o.outHeight;

		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(image1.getName());
		costumeData.setCostumeName("image1");
		setCostumeBrick.setCostume(costumeData);
		firstSprite.getCostumeDataList().add(costumeData);

		storageHandler.saveProject(project);
	}

}
