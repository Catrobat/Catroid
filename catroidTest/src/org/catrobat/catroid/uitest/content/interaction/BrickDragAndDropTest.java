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
package org.catrobat.catroid.uitest.content.interaction;

import android.os.Build;
import android.widget.ListView;

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
import org.catrobat.catroid.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class BrickDragAndDropTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public BrickDragAndDropTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createEmptyProject();
		solo.waitForActivity(MainMenuActivity.class);
		solo.sleep(300);
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	public void testClickOnEmptySpace() {
		solo.clickOnScreen(20, ScreenValues.SCREEN_HEIGHT - 150);
		solo.sleep(2000);
		assertFalse("Brickcategories should not be shown", solo.searchText(solo.getString(R.string.categories)));
	}

	public void testPutHoveringBrickDown() {
		// clicks on spriteName needed to get focus on listview for solo without adding hovering brick

		ListView view = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) view.getAdapter();

		UiTestUtils.addNewBrick(solo, R.string.brick_set_x);
		assertEquals("Wrong number of Bricks", 2, adapter.getCount());

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.sleep(500);
		assertFalse("Categories shouldn't be shown", solo.searchText(solo.getString(R.string.categories)));
		UiTestUtils.dragFloatingBrickDownwards(solo);
		solo.sleep(500);

		UiTestUtils.addNewBrick(solo, R.string.brick_stop_all_sounds);
		String currentSprite = ProjectManager.getInstance().getCurrentSprite().getName();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			// just to get focus and get the correct list
			solo.clickOnText(currentSprite);
		}

		List<Brick> brickListToCheck = ProjectManager.getInstance().getCurrentScript().getBrickList();
		assertEquals("One Brick should be in bricklist, one hovering and therefore not in project yet", 1,
				brickListToCheck.size());
		assertEquals("Both bricks (plus ScriptBrick) should be present in the adapter", 3, adapter.getCount());
		assertTrue("Set brick should be instance of SetXBrick", brickListToCheck.get(0) instanceof SetXBrick);
		assertTrue("Set brick should be instance of SetXBrick", adapter.getItem(2) instanceof SetXBrick);
		assertTrue("Hovering brick should be instance of StopAllSoundsBrick",
				adapter.getItem(1) instanceof StopAllSoundsBrick);

		solo.sleep(500);
		UiTestUtils.dragFloatingBrickDownwards(solo);
		solo.sleep(500);
		assertEquals("Two Bricks should be in bricklist/project", 2, brickListToCheck.size());
		assertTrue("First brick should be instance of SetXBrick", brickListToCheck.get(0) instanceof SetXBrick);
		assertTrue("Second brick should be instance of StopAllSoundsBrick",
				brickListToCheck.get(1) instanceof StopAllSoundsBrick);

		UiTestUtils.addNewBrick(solo, R.string.brick_wait);
		solo.sleep(500);
		UiTestUtils.dragFloatingBrickUpwards(solo, 2);
		solo.sleep(500);

		if (solo.searchText(solo.getString(R.string.brick_context_dialog_move_brick), true)) {
			solo.goBack();
		}

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			// just to get focus and get the correct list
			currentSprite = ProjectManager.getInstance().getCurrentSprite().getName();
			solo.clickOnText(currentSprite);
		}

		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo, 0);

		//just to gain focus
		solo.clickOnScreen(20, yPositionList.get(0));
		solo.goBack();

		solo.clickOnScreen(20, yPositionList.get(1));
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_move_brick));

		Utils.updateScreenWidthAndHeight(solo.getCurrentActivity());
		int height = ScreenValues.SCREEN_HEIGHT;

		solo.sleep(2000);
		solo.drag(20, 20, 300, height - 20, 100);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			// just to get focus and get the correct list
			currentSprite = ProjectManager.getInstance().getCurrentSprite().getName();
			solo.clickOnText(currentSprite);
		}
		solo.sleep(2000);
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
		solo.sleep(500);
		UiTestUtils.dragFloatingBrickDownwards(solo);
		solo.sleep(500);

		BrickAdapter adapter = (BrickAdapter) UiTestUtils.getScriptListView(solo).getAdapter();
		assertEquals("Brick was not added.", 2, adapter.getCount());
	}
}
