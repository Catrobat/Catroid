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
package at.tugraz.ist.catroid.uitest.content.brick;

import java.util.List;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.GlideToBrick;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class GlideToBrickTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;

	public GlideToBrickTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		UiTestUtils.createTestProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		getActivity().finish();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testNumberInput() {
		solo.clickLongOnText(getActivity().getString(R.string.brick_when_started));
		solo.clickOnText(getActivity().getString(R.string.delete));
		solo.sleep(1000);

		UiTestUtils.addNewBrick(solo, R.string.brick_glide);
		solo.clickOnText(getActivity().getString(R.string.brick_when_started));

		double duration = 1.5;
		int xPosition = 123;
		int yPosition = 567;

		UiTestUtils.clickEnterClose(solo, 0, String.valueOf(duration));
		UiTestUtils.clickEnterClose(solo, 1, String.valueOf(xPosition));
		UiTestUtils.clickEnterClose(solo, 2, String.valueOf(yPosition));

		solo.sleep(1000);
		ProjectManager manager = ProjectManager.getInstance();
		List<Brick> brickList = manager.getCurrentScript().getBrickList();
		GlideToBrick glideToBrick = (GlideToBrick) brickList.get(0);
		assertEquals("Wrong duration input in Glide to brick", Math.round(duration * 1000),
				glideToBrick.getDurationInMilliSeconds());

		assertEquals("Wrong x input in Glide to brick", xPosition,
				UiTestUtils.getPrivateField("xDestination", glideToBrick));
		assertEquals("Wrong y input in Glide to brick", yPosition,
				UiTestUtils.getPrivateField("yDestination", glideToBrick));
	}

	public void testResizeInputFields() {
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_home);
		solo.sleep(200);
		solo.clickOnText(getActivity().getString(R.string.current_project_button));
		createProject();
		solo.clickOnText(solo.getCurrentListViews().get(0).getItemAtPosition(0).toString());
		solo.sleep(100);

		UiTestUtils.testDoubleEditText(solo, 0, 1.1, 60, true);
		UiTestUtils.testDoubleEditText(solo, 0, 1234.567, 60, true);
		UiTestUtils.testDoubleEditText(solo, 0, -1, 60, true);
		UiTestUtils.testDoubleEditText(solo, 0, 1234.5678, 60, false);

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
