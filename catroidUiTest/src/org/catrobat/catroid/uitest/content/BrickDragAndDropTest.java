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
package org.catrobat.catroid.uitest.content;

import java.util.ArrayList;
import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.StopAllSoundsBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.view.Display;
import android.widget.ListView;

public class BrickDragAndDropTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public BrickDragAndDropTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createEmptyProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	public void testClickOnEmptySpace() {
		solo.clickOnScreen(20, ScreenValues.SCREEN_HEIGHT - 150);
		solo.sleep(200);
		assertFalse("Brickcategories should not be shown", solo.searchText(solo.getString(R.string.categories)));
	}

	public void testPutHoveringBrickDown() {
		// clicks on spriteName needed to get focus on listview for solo without adding hovering brick
		String scriptsName = solo.getString(R.string.scripts);

		ListView view = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) view.getAdapter();

		UiTestUtils.addNewBrick(solo, R.string.brick_set_x);
		assertEquals("Wrong number of Bricks", 2, adapter.getCount());

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.sleep(200);
		assertFalse("Categories shouldn't be shown", solo.searchText(solo.getString(R.string.categories)));
		solo.clickOnScreen(200, 200);

		UiTestUtils.addNewBrick(solo, R.string.brick_stop_all_sounds);
		// just to get focus and get the correct list
		solo.clickOnText(scriptsName);
		solo.clickOnText(scriptsName);

		List<Brick> brickListToCheck = ProjectManager.INSTANCE.getCurrentScript().getBrickList();
		assertEquals("One Brick should be in bricklist, one hovering and therefore not in project yet", 1,
				brickListToCheck.size());
		assertEquals("Both bricks (plus ScriptBrick) should be present in the adapter", 3, adapter.getCount());
		assertTrue("Set brick should be instance of SetXBrick", brickListToCheck.get(0) instanceof SetXBrick);
		assertTrue("Set brick should be instance of SetXBrick", adapter.getItem(2) instanceof SetXBrick);
		assertTrue("Hovering brick should be instance of StopAllSoundsBrick",
				adapter.getItem(1) instanceof StopAllSoundsBrick);

		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo, 1);
		solo.drag(10, 10, yPositionList.get(1), yPositionList.get(2) + 100, 30);
		solo.sleep(200);
		assertEquals("Two Bricks should be in bricklist/project", 2, brickListToCheck.size());
		assertTrue("First brick should be instance of SetXBrick", brickListToCheck.get(0) instanceof SetXBrick);
		assertTrue("Second brick should be instance of StopAllSoundsBrick",
				brickListToCheck.get(1) instanceof StopAllSoundsBrick);

		UiTestUtils.addNewBrick(solo, R.string.brick_wait);
		solo.clickOnScreen(200, 200);

		if (solo.searchText(solo.getString(R.string.brick_context_dialog_move_brick), true)) {
			solo.goBack();
		}
		// just to get focus and get the correct list
		solo.clickOnText(scriptsName);
		solo.clickOnText(scriptsName);
		yPositionList = UiTestUtils.getListItemYPositions(solo, 1);

		solo.clickOnScreen(20, yPositionList.get(0));
		solo.clickOnScreen(20, yPositionList.get(1));
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_move_brick));

		Display display = solo.getCurrentActivity().getWindowManager().getDefaultDisplay();

		@SuppressWarnings("deprecation")
		int height = display.getHeight();

		solo.sleep(200);
		solo.drag(20, 20, 300, height - 20, 100);
		// just to get focus and get the correct list
		solo.clickOnText(scriptsName);
		solo.clickOnText(scriptsName);
		solo.sleep(400);

		assertTrue("Last Brick should now be WaitBrick", adapter.getItem(3) instanceof WaitBrick);
	}

	public void testAddNewBrickFromAnotherCategory() {
		int categoryStringId = UiTestUtils.getBrickCategory(solo, R.string.brick_set_x);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(solo.getString(categoryStringId));
		solo.goBack();
		categoryStringId = UiTestUtils.getBrickCategory(solo, R.string.brick_stop_all_sounds);
		solo.clickOnText(solo.getString(categoryStringId));
		solo.clickOnText(solo.getString(R.string.brick_stop_all_sounds));
		solo.clickOnScreen(200, 200);
		solo.sleep(200);

		BrickAdapter adapter = (BrickAdapter) UiTestUtils.getScriptListView(solo).getAdapter();
		assertEquals("Brick was not added.", 2, adapter.getCount());
	}
}
