/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.content.brick;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListView;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class WhenStartedBrickTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private Project project;

	public WhenStartedBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	public void testWhenStartedBrick() {
		if (!solo.waitForView(DragAndDropListView.class, 0, 5000, false)) {
			fail("DragAndDropListView not shown in 5 secs!");
		}

		ArrayList<Integer> yPosition;
		int addedYPosition;
		float whenBrickPosition = 300;

		assertEquals("Incorrect number of bricks.", 4, UiTestUtils.getScriptListView(solo).getCount());

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());
		assertNotNull("TextView does not exist", solo.getText(solo.getString(R.string.brick_when)));

		solo.sleep(100);

		UiTestUtils.addNewBrick(solo, R.string.brick_when_started);

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPosition, yPosition.get(yPosition.size() - 1) + 20, 100);
		solo.sleep(200);
		projectBrickList = ProjectManager.getInstance().getCurrentSprite().getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());
		projectBrickList = ProjectManager.getInstance().getCurrentSprite().getScript(1).getBrickList();
		assertEquals("Incorrect number of bricks.", 0, projectBrickList.size());
		assertTrue("Wrong Script instance.",
				(ProjectManager.getInstance().getCurrentSprite().getScript(1) instanceof StartScript));

		solo.sleep(200);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(solo.getString(R.string.category_control));
		solo.searchText(solo.getString(R.string.category_control));
		solo.clickOnScreen(200, whenBrickPosition);

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);

		solo.drag(20, 20, addedYPosition, yPosition.get(3) + 20, 100);
		solo.sleep(200);
		projectBrickList = ProjectManager.getInstance().getCurrentSprite().getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 2, projectBrickList.size());
		assertTrue("Wrong Script instance.",
				(ProjectManager.getInstance().getCurrentSprite().getScript(1) instanceof WhenScript));

		projectBrickList = ProjectManager.getInstance().getCurrentSprite().getScript(1).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());
		assertTrue("Wrong Script instance.",
				(ProjectManager.getInstance().getCurrentSprite().getScript(2) instanceof StartScript));

		projectBrickList = ProjectManager.getInstance().getCurrentSprite().getScript(2).getBrickList();
		assertEquals("Incorrect number of bricks.", 0, projectBrickList.size());
		assertTrue("Wrong Script instance.",
				(ProjectManager.getInstance().getCurrentSprite().getScript(2) instanceof StartScript));

		solo.sleep(200);

		UiTestUtils.addNewBrick(solo, R.string.brick_when_started);

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);

		solo.goBack();

		solo.sleep(200);
		projectBrickList = ProjectManager.getInstance().getCurrentSprite().getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 2, projectBrickList.size());
		assertTrue("Wrong Script instance.",
				(ProjectManager.getInstance().getCurrentSprite().getScript(0) instanceof WhenScript));

		projectBrickList = ProjectManager.getInstance().getCurrentSprite().getScript(1).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());
		assertTrue("Wrong Script instance.",
				(ProjectManager.getInstance().getCurrentSprite().getScript(1) instanceof WhenScript));

		projectBrickList = ProjectManager.getInstance().getCurrentSprite().getScript(2).getBrickList();
		assertEquals("Incorrect number of bricks.", 0, projectBrickList.size());
		assertTrue("Wrong Script instance.",
				(ProjectManager.getInstance().getCurrentSprite().getScript(2) instanceof StartScript));
	}

	private void createProject() {

		project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new WhenScript();
		script.addBrick(new PlaceAtBrick(100, 100));
		script.addBrick(new PlaceAtBrick(100, 100));
		script.addBrick(new PlaceAtBrick(100, 100));
		sprite.addScript(script);

		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
