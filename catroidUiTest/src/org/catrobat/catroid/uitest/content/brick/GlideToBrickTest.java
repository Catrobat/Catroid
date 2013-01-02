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

import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptTabActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class GlideToBrickTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {

	private Solo solo;

	public GlideToBrickTest() {
		super(ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		UiTestUtils.createTestProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public void testNumberInput() {
		String whenStartedText = solo.getString(R.string.brick_when_started);
		solo.clickLongOnText(whenStartedText);
		solo.clickOnText(solo.getString(R.string.delete));

		UiTestUtils.addNewBrick(solo, R.string.brick_glide);
		solo.clickOnText(whenStartedText);

		double duration = 1.5;
		int xPosition = 123;
		int yPosition = 567;

		UiTestUtils.clickEnterClose(solo, 0, String.valueOf(duration));
		UiTestUtils.clickEnterClose(solo, 1, String.valueOf(xPosition));
		UiTestUtils.clickEnterClose(solo, 2, String.valueOf(yPosition));

		ProjectManager manager = ProjectManager.getInstance();
		List<Brick> brickList = manager.getCurrentSprite().getScript(0).getBrickList();
		GlideToBrick glideToBrick = (GlideToBrick) brickList.get(0);
		assertEquals("Wrong duration input in Glide to brick", Math.round(duration * 1000),
				glideToBrick.getDurationInMilliSeconds());

		assertEquals("Wrong x input in Glide to brick", xPosition,
				UiTestUtils.getPrivateField("xDestination", glideToBrick));
		assertEquals("Wrong y input in Glide to brick", yPosition,
				UiTestUtils.getPrivateField("yDestination", glideToBrick));
	}

	public void testResizeInputFields() {
		UiTestUtils.goToHomeActivity(getActivity());
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		createProject();
		solo.sleep(200);
		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName(), 1);
		solo.sleep(200);
		solo.clickOnText(solo.getCurrentListViews().get(0).getItemAtPosition(0).toString());
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());

		UiTestUtils.testDoubleEditText(solo, 0, 1.1, 60, true);
		UiTestUtils.testDoubleEditText(solo, 0, 12345.67, 60, true);
		UiTestUtils.testDoubleEditText(solo, 0, -1, 60, true);
		UiTestUtils.testDoubleEditText(solo, 0, 12345.678, 60, false);

		for (int i = 1; i < 3; i++) {
			UiTestUtils.testIntegerEditText(solo, i, 1, 60, true);
			UiTestUtils.testIntegerEditText(solo, i, 123456, 60, true);
			UiTestUtils.testIntegerEditText(solo, i, -1, 60, true);
			UiTestUtils.testIntegerEditText(solo, i, 1234567, 60, false);
		}
	}

	private void createProject() {
		int xValue = 800;
		int yValue = 0;
		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		Brick glideToBrick = new GlideToBrick(sprite, xValue, yValue, 1000);
		script.addBrick(glideToBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
