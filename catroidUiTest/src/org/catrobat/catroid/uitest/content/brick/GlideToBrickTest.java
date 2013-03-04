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
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class GlideToBrickTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private Solo solo;
	private Project project;

	public GlideToBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
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
		double duration = 1.5;
		int xPosition = 123;
		int yPosition = 567;

		solo.clickOnEditText(0);
		UiTestUtils.insertDoubleIntoEditText(solo, duration);

		solo.goBack();

		solo.clickOnEditText(1);
		UiTestUtils.insertDoubleIntoEditText(solo, xPosition);
		solo.goBack();

		solo.clickOnEditText(2);
		UiTestUtils.insertDoubleIntoEditText(solo, yPosition);
		solo.goBack();

		assertEquals("Text not updated within FormulaEditor", duration,
				Double.parseDouble(solo.getEditText(0).getText().toString()));
		assertEquals("Text not updated within FormulaEditor", xPosition,
				Integer.parseInt(solo.getEditText(1).getText().toString().substring(0, 3)));
		assertEquals("Text not updated within FormulaEditor", yPosition,
				Integer.parseInt(solo.getEditText(2).getText().toString().substring(0, 3)));

		solo.goBack();

		ProjectManager manager = ProjectManager.getInstance();
		List<Brick> brickList = manager.getCurrentSprite().getScript(0).getBrickList();
		GlideToBrick glideToBrick = (GlideToBrick) brickList.get(0);

		Formula formula = (Formula) Reflection.getPrivateField(glideToBrick, "durationInSeconds");
		float temp = formula.interpretFloat();

		assertEquals("Wrong duration input in Glide to brick", Math.round(duration * 1000), Math.round(temp * 1000));
		formula = (Formula) Reflection.getPrivateField(glideToBrick, "xDestination");
		int temp2 = formula.interpretInteger();
		assertEquals("Wrong x input in Glide to brick", xPosition, temp2);

		formula = (Formula) Reflection.getPrivateField(glideToBrick, "yDestination");
		temp2 = formula.interpretInteger();
		assertEquals("Wrong y input in Glide to brick", yPosition, temp2);
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		script.addBrick(new GlideToBrick(sprite, 0, 0, 0));

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
