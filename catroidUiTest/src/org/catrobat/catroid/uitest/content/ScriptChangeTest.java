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
package org.catrobat.catroid.uitest.content;

import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;

public class ScriptChangeTest extends ActivityInstrumentationTestCase2<ScriptActivity> {

	private Solo solo;
	private ArrayList<Brick> brickListToCheck;
	private Script testScript;
	private Script testScript2;
	private Script testScript3;

	public ScriptChangeTest() {
		super(ScriptActivity.class);
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
		solo = null;
	}

	public void testChangeScript() {
		ListView parent = UiTestUtils.getScriptListView(solo);
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
		String textViewText = solo.getCurrentViews(TextView.class, startBrick).get(1).getText().toString();
		String startBrickText = solo.getString(R.string.brick_show);
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
