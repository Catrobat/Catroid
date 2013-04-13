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
package org.catrobat.catroid.uitest.stage;

import java.util.ArrayList;

import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class MultipleBroadcastsTest extends ActivityInstrumentationTestCase2<StageActivity> {

	private Solo solo;
	private Sprite sprite1;
	private int sprite1PosX = 30;
	private Sprite sprite2;
	private int sprite2PosX = 60;
	private Sprite sprite3;
	private int sprite3PosX = -30;
	private Sprite sprite4;
	private int sprite4PosX = -60;

	public MultipleBroadcastsTest() {
		super(StageActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public void testSendMultipleBroadcastsWhenProjectStart() {
		solo.waitForActivity(StageActivity.class.getSimpleName());
		Reflection.setPrivateField(StageActivity.stageListener, "makeAutomaticScreenshot", false);
		solo.sleep(2000);
		assertEquals("Sprite1 is at the false x position", sprite1PosX, (int) sprite1.look.getXPosition());
		assertEquals("Sprite2 is at the false x position", sprite2PosX, (int) sprite2.look.getXPosition());
		assertEquals("Sprite3 is at the false x position", sprite3PosX, (int) sprite3.look.getXPosition());
		assertEquals("Sprite4 is at the false x position", sprite4PosX, (int) sprite4.look.getXPosition());

	}

	private void createProject() {
		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();

		sprite1 = new Sprite("sprite1");
		StartScript startScript1 = new StartScript(sprite1);
		BroadcastBrick broadcastBrick1 = new BroadcastBrick(sprite1);
		broadcastBrick1.setSelectedMessage("run");
		startScript1.addBrick(broadcastBrick1);
		BroadcastScript broadcastScript1 = new BroadcastScript(sprite1);
		broadcastScript1.setBroadcastMessage("run");
		SetXBrick setXBrick1 = new SetXBrick(sprite1, sprite1PosX);
		broadcastScript1.addBrick(setXBrick1);
		sprite1.addScript(startScript1);
		sprite1.addScript(broadcastScript1);
		spriteList.add(sprite1);

		sprite2 = new Sprite("sprite2");
		StartScript startScript2 = new StartScript(sprite2);
		BroadcastBrick broadcastBrick2 = new BroadcastBrick(sprite2);
		broadcastBrick2.setSelectedMessage("run");
		startScript2.addBrick(broadcastBrick2);
		BroadcastScript broadcastScript2 = new BroadcastScript(sprite2);
		broadcastScript2.setBroadcastMessage("run");
		SetXBrick setXBrick2 = new SetXBrick(sprite2, sprite2PosX);
		broadcastScript2.addBrick(setXBrick2);
		sprite2.addScript(startScript2);
		sprite2.addScript(broadcastScript2);
		spriteList.add(sprite2);

		sprite3 = new Sprite("sprite3");
		StartScript startScript3 = new StartScript(sprite3);
		BroadcastBrick broadcastBrick3 = new BroadcastBrick(sprite3);
		broadcastBrick3.setSelectedMessage("run");
		startScript3.addBrick(broadcastBrick3);
		BroadcastScript broadcastScript3 = new BroadcastScript(sprite3);
		broadcastScript3.setBroadcastMessage("run");
		SetXBrick setXBrick3 = new SetXBrick(sprite3, sprite3PosX);
		broadcastScript3.addBrick(setXBrick3);
		sprite3.addScript(startScript3);
		sprite3.addScript(broadcastScript3);
		spriteList.add(sprite3);

		sprite4 = new Sprite("sprite4");
		StartScript startScript4 = new StartScript(sprite4);
		BroadcastBrick broadcastBrick4 = new BroadcastBrick(sprite4);
		broadcastBrick4.setSelectedMessage("run");
		startScript4.addBrick(broadcastBrick4);
		BroadcastScript broadcastScript4 = new BroadcastScript(sprite4);
		broadcastScript4.setBroadcastMessage("run");
		SetXBrick setXBrick4 = new SetXBrick(sprite4, sprite4PosX);
		broadcastScript4.addBrick(setXBrick4);
		sprite4.addScript(startScript4);
		sprite4.addScript(broadcastScript4);
		spriteList.add(sprite4);

		UiTestUtils.createProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, spriteList, null);
	}

}
