/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.nativetest.io;

import java.io.IOException;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.bricks.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.bricks.GlideToBrick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.stage.NativeAppActivity;
import at.tugraz.ist.catroid.test.utils.TestUtils;

public class StorageHandlerTest extends InstrumentationTestCase {
	private StorageHandler storageHandler;

	public StorageHandlerTest() throws IOException {
		storageHandler = StorageHandler.getInstance();
	}

	@Override
	protected void tearDown() throws Exception {
		NativeAppActivity.setContext(null);
	}

	public void testLoadProject() throws Exception {
		double scaleValue = 0.8;
		int timeToWaitInMilliSeconds = 1000;
		int xPosition = 100;
		int yPosition = 100;
		int xMovement = -100;
		int yMovement = -100;
		int durationInMilliSeconds = 3000;
		int xDestination = 500;
		int yDestination = 500;
		int steps = 1;

		String projectName = "testProject";
		String firstSpriteName = "Stage";
		String secondSpriteName = "first";
		String thirdSpriteName = "second";

		NativeAppActivity.setContext(getInstrumentation().getContext());
		Project loadedProject = storageHandler.loadProject("test_project.xml");

		assertEquals("Project title missmatch.", projectName, loadedProject.getName());

		assertEquals("Name of first sprite does not match.", firstSpriteName, loadedProject.getSpriteList().get(0)
				.getName());
		assertEquals("Name of second sprite does not match.", secondSpriteName, loadedProject.getSpriteList().get(1)
				.getName());
		assertEquals("Name of third sprite does not match.", thirdSpriteName, loadedProject.getSpriteList().get(2)
				.getName());

		assertEquals("HideBrick was not loaded right", HideBrick.class,
				loadedProject.getSpriteList().get(1).getScript(0).getBrickList().get(0).getClass());
		assertEquals("ShowBrick was not loaded right", ShowBrick.class,
				loadedProject.getSpriteList().get(1).getScript(0).getBrickList().get(1).getClass());
		assertEquals(
				"ScaleBrick was not loaded right",
				scaleValue,
				TestUtils.getPrivateField("size",
						loadedProject.getSpriteList().get(1).getScript(0).getBrickList().get(2), false));
		assertEquals("ComeToFrontBrick was not loaded right", ComeToFrontBrick.class, loadedProject.getSpriteList()
				.get(1).getScript(0).getBrickList().get(3).getClass());
		assertEquals("SetCostumeBrick was not loaded right", SetCostumeBrick.class, loadedProject.getSpriteList()
				.get(1).getScript(0).getBrickList().get(4).getClass());

		assertEquals(
				"WaitBrick was not loaded right",
				timeToWaitInMilliSeconds,
				TestUtils.getPrivateField("timeToWaitInMilliSeconds", loadedProject.getSpriteList().get(1).getScript(1)
						.getBrickList().get(0), false));
		assertEquals("PlaySoundBrick was not loaded right", PlaySoundBrick.class, loadedProject.getSpriteList().get(1)
				.getScript(1).getBrickList().get(1).getClass());

		assertEquals(
				"PlaceAtBrick was not loaded right",
				xPosition,
				TestUtils.getPrivateField("xPosition", loadedProject.getSpriteList().get(2).getScript(0).getBrickList()
						.get(0), false));
		assertEquals(
				"PlaceAtBrick was not loaded right",
				yPosition,
				TestUtils.getPrivateField("xPosition", loadedProject.getSpriteList().get(2).getScript(0).getBrickList()
						.get(0), false));

		assertEquals(
				"SetXBrick was not loaded right",
				xPosition,
				TestUtils.getPrivateField("xPosition", loadedProject.getSpriteList().get(2).getScript(0).getBrickList()
						.get(1), false));
		assertEquals(
				"SetYBrick was not loaded right",
				yPosition,
				TestUtils.getPrivateField("yPosition", loadedProject.getSpriteList().get(2).getScript(0).getBrickList()
						.get(2), false));
		assertEquals(
				"ChangeXByBrick was not loaded right",
				xMovement,
				TestUtils.getPrivateField("xMovement", loadedProject.getSpriteList().get(2).getScript(0).getBrickList()
						.get(3), false));
		assertEquals(
				"ChangeYByBrick was not loaded right",
				yMovement,
				TestUtils.getPrivateField("yMovement", loadedProject.getSpriteList().get(2).getScript(0).getBrickList()
						.get(4), false));
		assertEquals(
				"GlideToBrick was not loaded right",
				xDestination,
				TestUtils.getPrivateField("xDestination", loadedProject.getSpriteList().get(2).getScript(0)
						.getBrickList().get(5), false));
		assertEquals(
				"GlideToBrick was not loaded right",
				yDestination,
				TestUtils.getPrivateField("yDestination", loadedProject.getSpriteList().get(2).getScript(0)
						.getBrickList().get(5), false));
		assertEquals("GlideToBrick was not loaded right", durationInMilliSeconds, ((GlideToBrick) (loadedProject
				.getSpriteList().get(2).getScript(0).getBrickList().get(5))).getDurationInMilliSeconds());
		assertEquals(
				"GoNStepsBackBrick was not loaded right",
				steps,
				TestUtils.getPrivateField("steps", loadedProject.getSpriteList().get(2).getScript(0).getBrickList()
						.get(6), false));

	}

}
