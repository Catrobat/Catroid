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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.common.Values;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class SpeakStageTestComplex extends ActivityInstrumentationTestCase2<PreStageActivity> {

	private Solo solo;
	private Script testScript;
	BroadcastScript receiveScript;

	public SpeakStageTestComplex() {
		super(PreStageActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public void testComplex() {
		solo.waitForActivity(PreStageActivity.class.getSimpleName());

		Intent intent = new Intent(getActivity(), StageActivity.class);
		getActivity().startActivity(intent);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(1000);
		assertEquals("wrong execution index. ", 0, testScript.getExecutingBrickIndex());
		assertEquals("wrong execution index. ", 0, receiveScript.getExecutingBrickIndex());
		assertEquals("isFinished is wrong. ", false, testScript.isFinished());

		solo.sleep(3100);
		assertEquals("wrong execution index. ", 2, testScript.getExecutingBrickIndex());
		assertEquals("wrong execution index. ", 1, receiveScript.getExecutingBrickIndex());
		assertEquals("isFinished is wrong. ", false, testScript.isFinished());
		assertEquals("isFinished is wrong. ", false, receiveScript.isFinished());

		solo.sleep(2300);
		assertEquals("wrong execution index. ", 3, testScript.getExecutingBrickIndex());
		assertEquals("wrong execution index. ", 2, receiveScript.getExecutingBrickIndex());
		assertEquals("isFinished is wrong. ", true, testScript.isFinished());
		assertEquals("isFinished is wrong. ", false, receiveScript.isFinished());

		solo.sleep(2000);
		assertEquals("wrong execution index. ", 3, testScript.getExecutingBrickIndex());
		assertEquals("wrong execution index. ", 3, receiveScript.getExecutingBrickIndex());
		assertEquals("isFinished is wrong. ", true, testScript.isFinished());
		assertEquals("isFinished is wrong. ", true, receiveScript.isFinished());

	}

	private void createProject() {
		Values.SCREEN_HEIGHT = 20;
		Values.SCREEN_WIDTH = 20;
		ProjectManager projectManager = ProjectManager.getInstance();
		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);

		Sprite firstSprite = new Sprite("cat");
		testScript = new StartScript(firstSprite);
		BroadcastBrick broadcastBrick = new BroadcastBrick(firstSprite);
		broadcastBrick.setSelectedMessage("speak");

		ArrayList<Brick> brickList = new ArrayList<Brick>();
		brickList.add(new SpeakBrick(firstSprite, "1 1 2 2 3 3 4 4 5 5"));
		brickList.add(broadcastBrick);
		brickList.add(new SpeakBrick(firstSprite, "6 6 7 7 8 8 9 9 10 10 11 11"));
		brickList.add(new WaitBrick(firstSprite, 1));

		for (Brick brick : brickList) {
			testScript.addBrick(brick);
		}

		Sprite secondSprite = new Sprite("dog");
		receiveScript = new BroadcastScript(secondSprite);
		receiveScript.setBroadcastMessage("speak");
		BroadcastReceiverBrick broadcastReceiver = new BroadcastReceiverBrick(secondSprite, receiveScript);

		brickList = new ArrayList<Brick>();
		brickList.add(broadcastReceiver);
		brickList.add(new WaitBrick(secondSprite, 2000));
		brickList.add(new SpeakBrick(secondSprite, "Stop Stop Stop Stop Stop"));
		brickList.add(new WaitBrick(firstSprite, 1));

		for (Brick brick : brickList) {
			receiveScript.addBrick(brick);
		}

		firstSprite.addScript(testScript);
		secondSprite.addScript(receiveScript);
		project.addSprite(firstSprite);
		project.addSprite(secondSprite);

		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);

		projectManager.setProject(project);
		StorageHandler.getInstance().saveProject(project);
	}
}
