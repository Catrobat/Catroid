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
package at.tugraz.ist.catroid.uitest.stage;

import java.util.ArrayList;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.FileChecksumContainer;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.SpeakBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.stage.PreStageActivity;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class SpeakStageTestSimple extends ActivityInstrumentationTestCase2<PreStageActivity> {

	private Solo solo;
	private Script testScript;

	public SpeakStageTestSimple() {
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
	}

	public void testSimple() {
		solo.waitForActivity(PreStageActivity.class.getSimpleName());

		Intent intent = new Intent(getActivity(), StageActivity.class);
		getActivity().startActivity(intent);

		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(500);
		assertEquals("wrong execution index. ", 0, testScript.getExecutingBrickIndex());
		assertEquals("isFinished is wrong. ", false, testScript.isFinished());
		solo.sleep(2300);
		assertEquals("wrong execution index. ", 1, testScript.getExecutingBrickIndex());
		assertEquals("isFinished is wrong. ", false, testScript.isFinished());
		solo.sleep(2000);
		assertEquals("wrong execution index. ", 1, testScript.getExecutingBrickIndex());
		assertEquals("isFinished is wrong. ", true, testScript.isFinished());
	}

	private void createProject() {
		Values.SCREEN_HEIGHT = 20;
		Values.SCREEN_WIDTH = 20;
		ProjectManager projectManager = ProjectManager.getInstance();
		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);

		Sprite firstSprite = new Sprite("cat");

		testScript = new StartScript(firstSprite);

		ArrayList<Brick> brickList = new ArrayList<Brick>();
		brickList.add(new SpeakBrick(firstSprite, "ich bin der erste text"));
		brickList.add(new SpeakBrick(firstSprite, "und ich bin der zweite text"));

		for (Brick brick : brickList) {
			testScript.addBrick(brick);
		}

		firstSprite.addScript(testScript);
		project.addSprite(firstSprite);

		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);

		projectManager.setProject(project);
		StorageHandler.getInstance().saveProject(project);
	}
}
