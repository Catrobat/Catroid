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
import java.util.List;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class BrickDragAndDropTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;
	private List<Brick> brickListToCheck;

	public BrickDragAndDropTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createEmptyProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testPutHoveringBrickDown() {
		String brickStopSoundsText = solo.getString(R.string.brick_stop_all_sounds);
		UiTestUtils.addNewBrick(solo, R.string.brick_set_x);
		UiTestUtils.addNewBrick(solo, R.string.brick_stop_all_sounds);

		assertTrue("Brick " + brickStopSoundsText + " should be hovering", solo.searchText(brickStopSoundsText));
		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo);
		solo.drag(10, 10, yPositionList.get(2), 400, 30);
		//		ArrayList<Integer> yPositionList = UiTestUtils.getListItemYPositions(solo);
		//		UiTestUtils.longClickAndDrag(solo, 10, yPositionList.get(7), 10, yPositionList.get(2), 20);

		solo.sleep(10000);
	}
}
