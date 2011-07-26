/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.uitest.content;

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
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
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class ScriptDeleteTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private ArrayList<Brick> brickListToCheck;

	public ScriptDeleteTest() {
		super("at.tugraz.ist.catroid", ScriptActivity.class);

	}

	@Override
	public void setUp() throws Exception {
		createTestProject("testProject");
		//UiTestUtils.createTestProject();
		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();

	}

	@Override
	public void tearDown() throws Exception {
		//UiTestUtils.clearAllUtilTestProjects();

		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();

		super.tearDown();
	}

	public void testAddLooksCategoryBrick() {
		UiTestUtils.addNewBrickAndScrollDown(solo, R.string.brick_set_costume);
		assertTrue(solo.searchText(getActivity().getString(R.string.brick_set_costume)));

		UiTestUtils.addNewBrickAndScrollDown(solo, R.string.brick_set_size_to);
		assertTrue(solo.searchText(getActivity().getString(R.string.brick_set_size_to)));

		//		solo.clickOnButton(getActivity().getString(R.string.add_new_brick));
		//		assertTrue("Category was not found!",
		//				solo.searchText(solo.getCurrentActivity().getString(R.string.category_motion)));
		//		assertTrue("Category was not found!",
		//				solo.searchText(solo.getCurrentActivity().getString(R.string.category_looks)));
		//		assertTrue("Category was not found!",
		//				solo.searchText(solo.getCurrentActivity().getString(R.string.category_sound)));
		//		assertTrue("Category was not found!",
		//				solo.searchText(solo.getCurrentActivity().getString(R.string.category_control)));
		//		solo.clickOnText(getActivity().getString(R.string.category_looks));
		//		solo.clickOnText(getActivity().getString(R.string.brick_set_size_to));
		//
		//		solo.clickOnButton(getActivity().getString(R.string.add_new_brick));
		//		assertTrue("Category was not found!",
		//				solo.searchText(solo.getCurrentActivity().getString(R.string.category_motion)));
		//		assertTrue("Category was not found!",
		//				solo.searchText(solo.getCurrentActivity().getString(R.string.category_looks)));
		//		assertTrue("Category was not found!",
		//				solo.searchText(solo.getCurrentActivity().getString(R.string.category_sound)));
		//		assertTrue("Category was not found!",
		//				solo.searchText(solo.getCurrentActivity().getString(R.string.category_control)));
		//		solo.clickOnText(getActivity().getString(R.string.category_looks));
		//		solo.clickOnText(getActivity().getString(R.string.brick_set_costume));
	}

	public void testDeleteScript() {
		solo.clickOnButton(getActivity().getString(R.string.add_new_brick));
		solo.clickOnText(getActivity().getString(R.string.category_control));
		solo.clickOnText(getActivity().getString(R.string.brick_if_touched));

		solo.clickLongOnText(getActivity().getString(R.string.brick_if_touched));
		solo.clickOnText(getActivity().getString(R.string.delete));
		solo.sleep(1000);

		int numberOfScripts = ProjectManager.getInstance().getCurrentSprite().getNumberOfScripts();
		assertEquals("Incorrect number of scripts in scriptList", 1, numberOfScripts);
		assertEquals("Incorrect number of elements in listView", 4, solo.getCurrentListViews().get(0).getChildCount());

		solo.clickLongOnText(getActivity().getString(R.string.brick_if_started));
		solo.clickOnText(getActivity().getString(R.string.delete));
		solo.sleep(1000);

		numberOfScripts = ProjectManager.getInstance().getCurrentSprite().getNumberOfScripts();
		assertEquals("Incorrect number of scripts in list", 0, numberOfScripts);
		assertEquals("Incorrect number of elements in listView", 0, solo.getCurrentListViews().get(0).getChildCount());

		UiTestUtils.addNewBrickAndScrollDown(solo, R.string.brick_hide);
		solo.sleep(2000);

		numberOfScripts = ProjectManager.getInstance().getCurrentSprite().getNumberOfScripts();
		assertEquals("Incorrect number of scripts in scriptList", 1, numberOfScripts);
		assertEquals("Incorrect number of elements in listView", 2, solo.getCurrentListViews().get(0).getChildCount());
	}

	private void createTestProject(String projectName) {
		double size = 0.8;

		Project project = new Project(null, projectName);
		Sprite firstSprite = new Sprite("cat");

		Script testScript = new StartScript("testscript", firstSprite);

		brickListToCheck = new ArrayList<Brick>();
		brickListToCheck.add(new HideBrick(firstSprite));
		brickListToCheck.add(new ShowBrick(firstSprite));
		brickListToCheck.add(new SetSizeToBrick(firstSprite, size));

		for (Brick brick : brickListToCheck) {
			testScript.addBrick(brick);
		}

		firstSprite.addScript(testScript);

		project.addSprite(firstSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);
	}

}
