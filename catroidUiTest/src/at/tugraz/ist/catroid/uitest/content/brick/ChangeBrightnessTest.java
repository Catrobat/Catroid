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

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.ChangeBrightnessBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class ChangeBrightnessTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;
	private Project project;
	private ChangeBrightnessBrick changeBrightnessBrick;
	private double brightnessToChange;

	public ChangeBrightnessTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
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

	@Smoke
	public void testChangeBrightnessBrick() {
		int childrenCount = ((ScriptActivity) getActivity().getCurrentActivity()).getAdapter()
				.getChildCountFromLastGroup();
		int groupCount = ((ScriptActivity) getActivity().getCurrentActivity()).getAdapter().getGroupCount();

		assertEquals("Incorrect number of bricks.", 2, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), ((ScriptActivity) getActivity()
				.getCurrentActivity()).getAdapter().getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist",
				solo.getText(getActivity().getString(R.string.brick_change_brightness)));

		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, brightnessToChange + "");
		solo.goBack();
		solo.clickOnButton(0);

		solo.sleep(1000);

		assertEquals("Wrong text in field", brightnessToChange, changeBrightnessBrick.getChangeBrightness());
		assertEquals("Text not updated", brightnessToChange,
				Double.parseDouble(solo.getEditText(0).getText().toString()));
	}

	public void testResizeInputField() {
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_home);
		solo.sleep(200);
		solo.clickOnText(getActivity().getString(R.string.current_project_button));
		createProject();
		solo.clickOnText(solo.getCurrentListViews().get(0).getItemAtPosition(0).toString());
		solo.sleep(100);

		UiTestUtils.testDoubleEditText(solo, 0, 1.0, 60, true);
		UiTestUtils.testDoubleEditText(solo, 0, 100.55, 60, true);
		UiTestUtils.testDoubleEditText(solo, 0, -0.1, 60, true);
		UiTestUtils.testDoubleEditText(solo, 0, 1000.55, 60, false);
	}

	private void createProject() {
		brightnessToChange = 56.6;
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		changeBrightnessBrick = new ChangeBrightnessBrick(sprite, 10.2);
		script.addBrick(changeBrightnessBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
