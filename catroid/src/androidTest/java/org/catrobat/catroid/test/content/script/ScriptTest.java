/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
package org.catrobat.catroid.test.content.script;

import android.test.AndroidTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.BackPackScriptController;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.formulaeditor.DataContainer.USER_LIST_PROJECT;
import static org.catrobat.catroid.formulaeditor.DataContainer.USER_LIST_SPRITE;
import static org.catrobat.catroid.formulaeditor.DataContainer.USER_VARIABLE_PROJECT;
import static org.catrobat.catroid.formulaeditor.DataContainer.USER_VARIABLE_SPRITE;
import static org.catrobat.catroid.uitest.util.UiTestUtils.DEFAULT_TEST_PROJECT_NAME;

public class ScriptTest extends AndroidTestCase {

	private HideBrick hideBrick;
	private ShowBrick showBrick;
	private PlaceAtBrick placeAtBrick;
	private ArrayList<Brick> brickList;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		hideBrick = new HideBrick();
		showBrick = new ShowBrick();
		placeAtBrick = new PlaceAtBrick(0, 0);
		BackPackListManager.getInstance().clearBackPackScripts();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		ProjectManager.getInstance().deleteProject(DEFAULT_TEST_PROJECT_NAME, getContext());
	}

	public void testAddBricks() {
		Script script = new StartScript();
		script.addBrick(hideBrick);
		script.addBrick(showBrick);
		script.addBrick(placeAtBrick);

		brickList = script.getBrickList();

		assertEquals("Wrong size of brick list", 3, brickList.size());
		assertEquals("hideBrick is not at index 0", 0, brickList.indexOf(hideBrick));
		assertEquals("showBrick is not at index 1", 1, brickList.indexOf(showBrick));
		assertEquals("placeAtBrick is not at index 2", 2, brickList.indexOf(placeAtBrick));
	}

	public void testBackPackScriptWithVariablesAndListsOnlyInFormulaBricks() {
		String group = "testGroup";
		List<Brick> brickList = UiTestUtils.createTestProjectWithUserVariables();
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();

		assertTrue("ScriptBackPack is not empty", BackPackListManager.getInstance().getAllBackPackedScripts().isEmpty());
		BackPackScriptController.getInstance().backpack(group, brickList, false, sprite);
		List<Script> backPackedScripts = BackPackListManager.getInstance().getBackPackedScripts().get(group);
		assertFalse("Group was not backpacked", backPackedScripts.isEmpty());

		SetXBrick setXVariableBrick = (SetXBrick) backPackedScripts.get(0).getBrick(0);
		SetYBrick setYVariableBrick = (SetYBrick) backPackedScripts.get(0).getBrick(1);
		SetXBrick setXListBrick = (SetXBrick) backPackedScripts.get(0).getBrick(2);
		SetYBrick setYListBrick = (SetYBrick) backPackedScripts.get(0).getBrick(3);

		assertNotNull("No UserVariable was backpacked", setXVariableBrick.getBackPackedVariableData().get(0).userVariable);
		assertEquals("Wrong UserVariable type backpacked", (Integer) USER_VARIABLE_PROJECT,
				setXVariableBrick.getBackPackedVariableData().get(0).userVariableType);

		assertNotNull("No UserVariable was backpacked", setYVariableBrick.getBackPackedVariableData().get(0).userVariable);
		assertEquals("Wrong UserVariable type backpacked", (Integer) USER_VARIABLE_SPRITE,
				setYVariableBrick.getBackPackedVariableData().get(0).userVariableType);

		assertNotNull("No UserList was backpacked", setXListBrick.getBackPackedListData().get(0).userList);
		assertEquals("Wrong UserList type backpacked", (Integer) USER_LIST_PROJECT,
				setXListBrick.getBackPackedListData().get(0).userListType);

		assertNotNull("No UserList was backpacked", setYListBrick.getBackPackedListData().get(0).userList);
		assertEquals("Wrong UserList type backpacked", (Integer) USER_LIST_SPRITE,
				setYListBrick.getBackPackedListData().get(0).userListType);
	}
}
