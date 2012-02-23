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
import at.tugraz.ist.catroid.content.bricks.TurnRightBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class TurnRightBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity> {

	private Solo solo;
	private Project project;
	private TurnRightBrick turnRightBrick;
	private double turnDegrees;

	public TurnRightBrickTest() {
		super("at.tugraz.ist.catroid", ScriptActivity.class);
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
		super.tearDown();
	}

	@Smoke
	public void testTurnRightBrickTest() {
		int childrenCount = getActivity().getAdapter().getChildCountFromLastGroup();
		int groupCount = getActivity().getAdapter().getGroupCount();

		assertEquals("Incorrect number of bricks.", 2, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0),
				getActivity().getAdapter().getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.brick_turn_right)));

		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, turnDegrees + "");
		solo.goBack();
		solo.clickOnButton(0);

		solo.sleep(1000);

		double actualDegrees = (Double) UiTestUtils.getPrivateField("degrees", turnRightBrick);

		assertEquals("Wrong text in field", turnDegrees, actualDegrees);
		assertEquals("Text not updated", turnDegrees, Double.parseDouble(solo.getEditText(0).getText().toString()));
	}

	public void testResizeInputField() {
		UiTestUtils.testDoubleEditText(solo, 0, 1.0, 75, true);
		UiTestUtils.testDoubleEditText(solo, 0, 1080.55, 75, true);
		UiTestUtils.testDoubleEditText(solo, 0, 0.75, 75, true);
		UiTestUtils.testDoubleEditText(solo, 0, 1080.555, 75, false);
	}

	private void createProject() {
		turnDegrees = 25;
		project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		turnRightBrick = new TurnRightBrick(sprite, 0);
		script.addBrick(turnRightBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
