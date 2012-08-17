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
package at.tugraz.ist.catroid.uitest.content;

import java.util.ArrayList;
import java.util.List;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ImageView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.SetXBrick;
import at.tugraz.ist.catroid.content.bricks.StopAllSoundsBrick;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class BrickDragAndDropTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;

	public BrickDragAndDropTest() {
		super(ScriptTabActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createEmptyProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testPutHoveringBrickDown() {
		// clicks on spriteName needed to get focus on listview for solo without adding hovering brick
		String spriteName = solo.getString(R.string.sprite_name);

		UiTestUtils.addNewBrick(solo, R.string.brick_set_x);
		solo.clickOnText(spriteName);
		UiTestUtils.addNewBrick(solo, R.string.brick_stop_all_sounds);
		solo.clickOnText(spriteName);

		List<Brick> brickListToCheck = ProjectManager.getInstance().getCurrentScript().getBrickList();
		assertEquals("One Brick should be in bricklist, one hovering", 2, brickListToCheck.size());
		assertTrue("Set brick should be instance of SetXBrick", brickListToCheck.get(1) instanceof SetXBrick);
		assertTrue("Hovering brick should be instance of StopAllSoundsBrick",
				brickListToCheck.get(0) instanceof StopAllSoundsBrick);

		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo);
		solo.drag(10, 10, yPositionList.get(1), yPositionList.get(2) + 100, 30);
		solo.sleep(200);
		assertEquals("Two Bricks should be in bricklist", 2, brickListToCheck.size());
		assertTrue("First brick should be instance of SetXBrick", brickListToCheck.get(0) instanceof SetXBrick);
		assertTrue("Second brick should be instance of StopAllSoundsBrick",
				brickListToCheck.get(1) instanceof StopAllSoundsBrick);

		UiTestUtils.addNewBrick(solo, R.string.brick_broadcast);
		solo.clickOnText(spriteName);
		yPositionList = UiTestUtils.getListItemYPositions(solo);
		solo.clickOnScreen(10, yPositionList.get(3));

		solo.clickOnText(spriteName);
		ImageView trash = (ImageView) solo.getView(R.id.trash);
		assertEquals("Trash should be GONE", View.GONE, trash.getVisibility());
	}
}
