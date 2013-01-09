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
import org.catrobat.catroid.common.Values;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.StopAllSoundsBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;
import android.view.Display;
import android.widget.ListView;

import com.jayway.android.robotium.solo.Solo;

public class BrickDragAndDropTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;

	public BrickDragAndDropTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createEmptyProject();
		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	@Override
	protected void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public void testClickOnEmptySpace() {
		solo.clickOnScreen(20, Values.SCREEN_HEIGHT - 50);
		solo.sleep(200);
		assertTrue("Brickcategories did not show up", solo.searchText(solo.getString(R.string.categories)));
	}

	public void testPutHoveringBrickDown() {
		// clicks on spriteName needed to get focus on listview for solo without adding hovering brick
		String scriptsName = solo.getString(R.string.scripts);

		ListView view = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) view.getAdapter();

		UiTestUtils.addNewBrick(solo, R.string.brick_set_x);
		assertEquals("Wrong number of Bricks", 3, adapter.getCount());

		UiTestUtils.clickOnBottomBar(solo, R.id.btn_add);
		solo.sleep(200);
		assertFalse("Categories shouldn't be shown", solo.searchText(solo.getString(R.string.categories)));
		solo.clickOnScreen(200, 200);

		UiTestUtils.addNewBrick(solo, R.string.brick_stop_all_sounds);
		solo.clickOnText(scriptsName);

		List<Brick> brickListToCheck = ProjectManager.getInstance().getCurrentScript().getBrickList();
		assertEquals("One Brick should be in bricklist, one hovering and therefore not in project yet", 1,
				brickListToCheck.size());
		assertEquals("Both bricks (plus ScriptBrick) should be present in the adapter", 3 + 1, adapter.getCount()); // don't forget the footer
		assertTrue("Set brick should be instance of SetXBrick", brickListToCheck.get(0) instanceof SetXBrick);
		assertTrue("Set brick should be instance of SetXBrick", adapter.getItem(2) instanceof SetXBrick);
		assertTrue("Hovering brick should be instance of StopAllSoundsBrick",
				adapter.getItem(1) instanceof StopAllSoundsBrick);

		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo);
		solo.drag(10, 10, yPositionList.get(1), yPositionList.get(2) + 100, 30);
		solo.sleep(200);
		assertEquals("Two Bricks should be in bricklist/project", 2, brickListToCheck.size());
		assertTrue("First brick should be instance of SetXBrick", brickListToCheck.get(0) instanceof SetXBrick);
		assertTrue("Second brick should be instance of StopAllSoundsBrick",
				brickListToCheck.get(1) instanceof StopAllSoundsBrick);

		UiTestUtils.addNewBrick(solo, R.string.brick_broadcast);
		solo.clickOnScreen(200, 200);

		yPositionList = UiTestUtils.getListItemYPositions(solo);

		solo.clickOnScreen(20, yPositionList.get(0));
		solo.clickOnScreen(20, yPositionList.get(2));
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_move_brick));

		Display display = solo.getCurrentActivity().getWindowManager().getDefaultDisplay();

		@SuppressWarnings("deprecation")
		int height = display.getHeight();

		solo.sleep(200);
		solo.drag(20, 20, 300, height - 20, 100);
		solo.sleep(200);

		assertTrue("Last Brick should now be BroadcastBrick", adapter.getItem(3) instanceof BroadcastBrick);
	}

	public void testAddNewBrickFromAnotherCategory() {
		int categoryStringId = UiTestUtils.getBrickCategory(solo, R.string.brick_set_x);

		UiTestUtils.clickOnBottomBar(solo, R.id.btn_add);
		solo.clickOnText(solo.getCurrentActivity().getString(categoryStringId));
		solo.clickOnImageButton(0);
		categoryStringId = UiTestUtils.getBrickCategory(solo, R.string.brick_stop_all_sounds);
		solo.clickOnText(solo.getCurrentActivity().getString(categoryStringId));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.brick_stop_all_sounds));
		solo.clickOnScreen(200, 200);
		solo.sleep(200);

		BrickAdapter adapter = (BrickAdapter) UiTestUtils.getScriptListView(solo).getAdapter();
		assertEquals("Brick was not added.", 2 + 1, adapter.getCount()); // don't forget the footer
	}
}
