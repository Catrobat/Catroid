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

package at.tugraz.ist.catroid.uitest.ui;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ListView;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.constructionSite.content.ProjectValuesManager;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class ScriptActivityTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private List<Brick> brickListToCheck;

	private ProjectValuesManager projectValuesManager = ProjectManager.getInstance().getProjectValuesManager();

	public ScriptActivityTest() {
		super("at.tugraz.ist.catroid", ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		brickListToCheck = UiTestUtils.createTestProject();
		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();
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

	public void testSimpleDragNDrop() throws InterruptedException {
		ArrayList<Integer> yposlist = getListItemYPositions();
		Thread.sleep(2000);
		solo.drag(30, 30, yposlist.get(2), (yposlist.get(4) + yposlist.get(5)) / 2, 20);
		ArrayList<Brick> brickList = projectValuesManager.getCurrentScript().getBrickList();

		assertEquals(brickListToCheck.size(), brickList.size());
		assertEquals(brickListToCheck.get(0), brickList.get(0));
		assertEquals(brickListToCheck.get(1), brickList.get(3));
		assertEquals(brickListToCheck.get(2), brickList.get(1));
		assertEquals(brickListToCheck.get(3), brickList.get(2));
		assertEquals(brickListToCheck.get(4), brickList.get(4));

		Thread.sleep(2000);
		brickListToCheck = brickList;
	}

	public void testDeleteItem() throws InterruptedException {
		ArrayList<Integer> yposlist = getListItemYPositions();
		Thread.sleep(2000);
		solo.drag(30, 400, yposlist.get(2), (yposlist.get(4) + yposlist.get(5)) / 2, 20);
		Thread.sleep(2000);
		ArrayList<Brick> brickList = projectValuesManager.getCurrentScript().getBrickList();

		assertEquals(brickListToCheck.size() - 1, brickList.size());
		assertEquals(brickListToCheck.get(0), brickList.get(0));
		assertEquals(brickListToCheck.get(2), brickList.get(1));
		assertEquals(brickListToCheck.get(3), brickList.get(2));
		assertEquals(brickListToCheck.get(4), brickList.get(3));

		Thread.sleep(2000);
		brickListToCheck = brickList;
	}

	private ArrayList<Integer> getListItemYPositions() {
		ArrayList<Integer> yposlist = new ArrayList<Integer>();

		ListView listView = solo.getCurrentListViews().get(0);

		for (int i = 0; i < listView.getChildCount(); ++i) {
			View currentViewInList = listView.getChildAt(i);

			Rect globalVisilbleRect = new Rect();
			currentViewInList.getGlobalVisibleRect(globalVisilbleRect);
			int middleYPos = globalVisilbleRect.top + globalVisilbleRect.height() / 2;
			yposlist.add(middleYPos);
		}

		return yposlist;
	}
}
