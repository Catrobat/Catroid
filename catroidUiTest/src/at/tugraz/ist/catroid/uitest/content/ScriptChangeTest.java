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
package at.tugraz.ist.catroid.uitest.content;

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ListView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class ScriptChangeTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {

	private Solo solo;
	private ArrayList<Brick> brickListToCheck;
	private Script testScript;
	private Script testScript2;
	private Script testScript3;

	public ScriptChangeTest() {
		super(ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createTestProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
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

	public void testChangeScript() {
		ListView parent = solo.getCurrentListViews().get(0);
		View testScriptBrick = parent.getChildAt(0);

		solo.clickOnView(testScriptBrick);
		solo.sleep(100);

		assertEquals("Current Script in List is not testScript", testScript, ProjectManager.getInstance()
				.getCurrentScript());

		View startBrick = parent.getChildAt(4);
		solo.clickOnView(startBrick);
		solo.sleep(100);
		assertEquals("Current Script in List is not testScript", testScript3, ProjectManager.getInstance()
				.getCurrentScript());

		startBrick = parent.getChildAt(5);
		solo.clickOnView(startBrick);
		solo.sleep(100);
		assertEquals("Current Script in List is not testScript", testScript2, ProjectManager.getInstance()
				.getCurrentScript());

		startBrick = parent.getChildAt(2);
		String textViewText = solo.getCurrentTextViews(startBrick).get(0).getText().toString();
		String startBrickText = getActivity().getString(R.string.brick_show);
		assertEquals("Third script in listView is not startScript", startBrickText, textViewText);
	}

	private void createTestProject(String projectName) {
		double size = 0.8;

		Project project = new Project(null, projectName);
		Sprite firstSprite = new Sprite("cat");

		testScript = new StartScript(firstSprite);
		testScript2 = new StartScript(firstSprite);
		testScript3 = new StartScript(firstSprite);

		brickListToCheck = new ArrayList<Brick>();
		brickListToCheck.add(new HideBrick(firstSprite));
		brickListToCheck.add(new ShowBrick(firstSprite));
		brickListToCheck.add(new SetSizeToBrick(firstSprite, size));

		// adding Bricks: ----------------
		for (Brick brick : brickListToCheck) {
			testScript.addBrick(brick);
		}
		// -------------------------------

		firstSprite.addScript(testScript);
		firstSprite.addScript(testScript3);
		firstSprite.addScript(testScript2);

		project.addSprite(firstSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);
	}
}
