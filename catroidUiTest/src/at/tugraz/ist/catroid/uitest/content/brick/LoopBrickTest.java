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
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.ChangeYByBrick;
import at.tugraz.ist.catroid.content.bricks.ForeverBrick;
import at.tugraz.ist.catroid.content.bricks.LoopBeginBrick;
import at.tugraz.ist.catroid.content.bricks.LoopEndBrick;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class LoopBrickTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;
	private Project project;

	public LoopBrickTest() {
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
		super.tearDown();
	}

	public void testLoopBrick() {
		ArrayList<Integer> yPos;
		int addedYPos;
		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();

		yPos = UiTestUtils.getListItemYPositions(solo);
		UiTestUtils.longClickAndDrag(solo, getActivity(), 10, yPos.get(1), 10, yPos.get(4) + 20, 20);
		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());
		assertTrue("Wrong Brick instance.", (projectBrickList.get(1) instanceof LoopBeginBrick));

		solo.sleep(100);

		yPos = UiTestUtils.getListItemYPositions(solo);
		UiTestUtils.longClickAndDrag(solo, getActivity(), 10, yPos.get(3), 10, yPos.get(0), 20);
		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());
		assertTrue("Wrong Brick instance.", (projectBrickList.get(2) instanceof LoopEndBrick));

		solo.sleep(100);

		yPos = UiTestUtils.getListItemYPositions(solo);
		UiTestUtils.longClickAndDrag(solo, getActivity(), 10, yPos.get(2), 10, yPos.get(0), 20);
		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());
		assertTrue("Wrong Brick instance.", (projectBrickList.get(0) instanceof LoopBeginBrick));

		solo.sleep(100);

		yPos = UiTestUtils.getListItemYPositions(solo);
		UiTestUtils.longClickAndDrag(solo, getActivity(), 10, yPos.get(3), 10, yPos.get(4) + 20, 20);
		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());
		assertTrue("Wrong Brick instance.", (projectBrickList.get(2) instanceof LoopEndBrick));

		solo.sleep(100);

		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_add_button);
		solo.sleep(100);
		solo.clickInList(4);
		solo.sleep(100);
		solo.clickInList(2);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(1000);
		yPos = UiTestUtils.getListItemYPositions(solo);
		addedYPos = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPos, yPos.get(2) + 20, 100);
		solo.setActivityOrientation(Solo.PORTRAIT);
		projectBrickList = ProjectManager.getInstance().getCurrentScript().getBrickList();
		assertEquals("Incorrect number of bricks.", 4, projectBrickList.size());
		assertTrue("Wrong Brick instance: " + projectBrickList.get(1).getClass().getSimpleName(),
				(projectBrickList.get(1) instanceof ChangeYByBrick));

		solo.sleep(100);

		yPos = UiTestUtils.getListItemYPositions(solo);
		UiTestUtils.longClickAndDrag(solo, getActivity(), 10, yPos.get(3), getActivity().getWindowManager()
				.getDefaultDisplay().getWidth() - 10, yPos.get(3), 20);
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());
		assertTrue("Wrong Brick instance.", (projectBrickList.get(0) instanceof ChangeYByBrick));

		solo.sleep(100);
		yPos = UiTestUtils.getListItemYPositions(solo);
		UiTestUtils.longClickAndDrag(solo, getActivity(), 10, yPos.get(1), 10, yPos.get(2) + 20, 20);
		assertEquals("Incorrect number of bricks.", 0, projectBrickList.size());
		projectBrickList = project.getSpriteList().get(0).getScript(1).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());
		assertTrue("Wrong Brick instance.", (projectBrickList.get(0) instanceof ChangeYByBrick));
	}

	private void createProject() {
		LoopBeginBrick beginBrick;
		LoopEndBrick endBrick;

		project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);

		beginBrick = new ForeverBrick(sprite);
		endBrick = new LoopEndBrick(sprite, beginBrick);

		script.addBrick(beginBrick);
		script.addBrick(new ChangeYByBrick(sprite, -10));
		script.addBrick(endBrick);

		sprite.addScript(script);
		sprite.addScript(new StartScript(sprite));
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
