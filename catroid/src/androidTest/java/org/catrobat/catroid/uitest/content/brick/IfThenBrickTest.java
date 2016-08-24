/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicEndBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class IfThenBrickTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private Project project;
	private IfThenLogicBeginBrick ifBrick;

	public IfThenBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new SingleSprite("cat");
		Script script = new StartScript();
		ifBrick = new IfThenLogicBeginBrick(0);
		IfThenLogicEndBrick ifEndBrick = new IfThenLogicEndBrick(ifBrick);
		ifBrick.setIfThenEndBrick(ifEndBrick);

		script.addBrick(ifBrick);
		script.addBrick(new ChangeYByNBrick(-10));
		script.addBrick(ifEndBrick);

		sprite.addScript(script);
		sprite.addScript(new StartScript());
		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

	public void testCopyIfLogicBeginBrick() {
		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy);
		solo.clickOnCheckBox(1);
		UiTestUtils.acceptAndCloseActionMode(solo);

		ArrayList<Brick> projectBrickList = project.getDefaultScene().getSpriteList().get(0).getScript(0).getBrickList();

		assertEquals("Incorrect number of bricks.", 5, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof IfThenLogicBeginBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(1) instanceof ChangeYByNBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(2) instanceof IfThenLogicEndBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(3) instanceof IfThenLogicBeginBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(4) instanceof IfThenLogicEndBrick);
	}

	public void testCopyIfLogicEndBrick() {
		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy);
		solo.clickOnCheckBox(3);
		UiTestUtils.acceptAndCloseActionMode(solo);

		ArrayList<Brick> projectBrickList = project.getDefaultScene().getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 5, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof IfThenLogicBeginBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(1) instanceof ChangeYByNBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(2) instanceof IfThenLogicEndBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(3) instanceof IfThenLogicBeginBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(4) instanceof IfThenLogicEndBrick);
	}
}
