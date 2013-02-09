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
package org.catrobat.catroid.uitest.content.brick;

import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick.Direction;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.ListView;

import com.jayway.android.robotium.solo.Solo;

public class PointInDirectionBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity> {

	private Solo solo;
	private Project project;
	private PointInDirectionBrick pointInDirectionBrick;

	public PointInDirectionBrickTest() {
		super(ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	@Smoke
	public void testPointInDirectionBrickTest() throws InterruptedException {
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 2, dragDropListView.getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist", solo.getText(solo.getString(R.string.brick_point_in_direction)));

		solo.clickOnEditText(0);

		solo.clickInList(1);
		assertEquals("Wrong value in field!", "90", solo.getEditText(0).getText().toString());
		solo.clickInList(2);
		assertEquals("Wrong value in field!", "-90", solo.getEditText(0).getText().toString());
		solo.clickInList(3);
		assertEquals("Wrong value in field!", "0", solo.getEditText(0).getText().toString());
		solo.clickInList(4);
		assertEquals("Wrong value in field!", "180", solo.getEditText(0).getText().toString());

		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, "100");
		solo.clickOnButton(solo.getString(R.string.ok));

		solo.sleep(200);

		assertTrue("Wrong selection", solo.searchEditText("100"));

		double degrees = (Double) Reflection.getPrivateField(pointInDirectionBrick, "degrees");
		assertEquals("Text not updated", "" + degrees, "100.0");

		solo.clickOnEditText(0);

		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, "-12.34");
		solo.clickOnButton(solo.getString(R.string.ok));

		solo.sleep(200);

		assertTrue("Wrong selection", solo.searchEditText("-12.34"));

		degrees = 0.0;
		degrees = (Double) Reflection.getPrivateField(pointInDirectionBrick, "degrees");
		assertEquals("Text not updated", "" + degrees, "-12.34");

		solo.clickOnEditText(0);
		solo.clickOnButton(solo.getString(R.string.cancel_button));

		solo.sleep(200);

		assertTrue("Wrong selection", solo.searchEditText("-12.34"));

		degrees = 0.0;
		degrees = (Double) Reflection.getPrivateField(pointInDirectionBrick, "degrees");
		assertEquals("Text not updated", "" + degrees, "-12.34");

		solo.clickOnEditText(0);

		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.clickOnButton(solo.getString(R.string.cancel_button));

		solo.sleep(200);

		assertTrue("Wrong selection", solo.searchEditText("-12.34"));

		degrees = 0.0;
		degrees = (Double) Reflection.getPrivateField(pointInDirectionBrick, "degrees");
		assertEquals("Text not updated", "" + degrees, "-12.34");
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		pointInDirectionBrick = new PointInDirectionBrick(sprite, Direction.DIRECTION_RIGHT);
		script.addBrick(pointInDirectionBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.INSTANCE.setProject(project);
		ProjectManager.INSTANCE.setCurrentSprite(sprite);
		ProjectManager.INSTANCE.setCurrentScript(script);
	}
}
