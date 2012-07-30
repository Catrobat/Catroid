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
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class WhenBrickTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;
	private Project project;

	public WhenBrickTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testWhenBrick() {
		int groupCount = ((ScriptActivity) getActivity().getCurrentActivity()).getAdapter().getScriptCount();
		ArrayList<Integer> yPos;
		int addedYPos;

		assertEquals("Incorrect number of bricks.", 4, solo.getCurrentListViews().get(0).getCount());

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), ((ScriptActivity) getActivity()
				.getCurrentActivity()).getAdapter().getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.brick_when_started)));

		// Inactive until spinner is used again
		//		solo.pressSpinnerItem(0, 0);
		//		Log.v(TAG, solo.getCurrentSpinners().get(0).getSelectedItemPosition() + "");
		//		Log.v(TAG, solo.getCurrentSpinners().get(0).getSelectedItem().toString());
		//		solo.sleep(1500);
		//		assertEquals("Wrong event selected!", 0, solo.getCurrentSpinners().get(0).getSelectedItemPosition());

		solo.sleep(100);

		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_add_button);
		solo.sleep(100);
		solo.clickInList(4);
		solo.sleep(100);
		solo.clickInList(2);

		solo.sleep(1000);
		yPos = UiTestUtils.getListItemYPositions(solo);
		addedYPos = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPos, yPos.get(yPos.size() - 1) + 20, 100);
		solo.sleep(1000);
		projectBrickList = ProjectManager.getInstance().getCurrentSprite().getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());
		assertTrue("Wrong Script instance.",
				(ProjectManager.getInstance().getCurrentSprite().getScript(1) instanceof WhenScript));

		solo.sleep(1000);

		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_add_button);
		solo.sleep(100);
		solo.clickInList(4);
		solo.sleep(100);
		solo.clickInList(2);

		solo.sleep(1000);
		yPos = UiTestUtils.getListItemYPositions(solo);
		addedYPos = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPos, yPos.get(3) + 20, 100);
		solo.sleep(1000);
		projectBrickList = ProjectManager.getInstance().getCurrentSprite().getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 2, projectBrickList.size());
		assertTrue("Wrong Script instance.",
				(ProjectManager.getInstance().getCurrentSprite().getScript(0) instanceof StartScript));

		projectBrickList = ProjectManager.getInstance().getCurrentSprite().getScript(1).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());
		assertTrue("Wrong Script instance.",
				(ProjectManager.getInstance().getCurrentSprite().getScript(1) instanceof WhenScript));

		projectBrickList = ProjectManager.getInstance().getCurrentSprite().getScript(2).getBrickList();
		assertEquals("Incorrect number of bricks.", 0, projectBrickList.size());
		assertTrue("Wrong Script instance.",
				(ProjectManager.getInstance().getCurrentSprite().getScript(2) instanceof WhenScript));

		solo.sleep(1000);

		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_add_button);
		solo.sleep(100);
		solo.clickInList(4);
		solo.sleep(100);
		solo.clickInList(2);

		solo.sleep(1000);
		yPos = UiTestUtils.getListItemYPositions(solo);
		addedYPos = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, getActivity().getWindowManager().getDefaultDisplay().getWidth() - 20, addedYPos,
				yPos.get(3) + 20, 100);
		solo.sleep(1000);
		projectBrickList = ProjectManager.getInstance().getCurrentSprite().getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 2, projectBrickList.size());
		assertTrue("Wrong Script instance.",
				(ProjectManager.getInstance().getCurrentSprite().getScript(0) instanceof StartScript));

		projectBrickList = ProjectManager.getInstance().getCurrentSprite().getScript(1).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());
		assertTrue("Wrong Script instance.",
				(ProjectManager.getInstance().getCurrentSprite().getScript(1) instanceof WhenScript));

		projectBrickList = ProjectManager.getInstance().getCurrentSprite().getScript(2).getBrickList();
		assertEquals("Incorrect number of bricks.", 0, projectBrickList.size());
		assertTrue("Wrong Script instance.",
				(ProjectManager.getInstance().getCurrentSprite().getScript(2) instanceof WhenScript));
	}

	private void createProject() {

		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		script.addBrick(new PlaceAtBrick(sprite, 100, 100));
		script.addBrick(new PlaceAtBrick(sprite, 100, 100));
		script.addBrick(new PlaceAtBrick(sprite, 100, 100));
		sprite.addScript(script);

		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
