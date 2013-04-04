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
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PointToBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.jayway.android.robotium.solo.Solo;

public class PointToBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity> {

	private Solo solo;
	private Project project;

	public PointToBrickTest() {
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
	public void testPointToBrickTest() throws InterruptedException {
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();

		assertEquals("Incorrect number of bricks.", 3, dragDropListView.getChildCount());
		assertEquals("Incorrect number of bricks.", 2, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		int oldSpriteListSize = project.getSpriteList().size();

		assertNotNull("TextView does not exist", solo.getText(solo.getString(R.string.brick_point_to)));

		String spinnerNewText = solo.getString(R.string.new_broadcast_message);
		String newSpriteName = "cat3";

		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.brick_point_to_spinner));
		solo.waitForText(spinnerNewText);
		solo.clickInList(0);
		solo.waitForView(EditText.class);
		solo.enterText(0, newSpriteName);
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.sleep(300);

		assertEquals("Wrong number of sprites", oldSpriteListSize + 1, project.getSpriteList().size());
		assertEquals("Wrong selection", newSpriteName, ((Spinner) solo.getView(R.id.brick_point_to_spinner))
				.getSelectedItem().toString());

		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.brick_point_to_spinner));
		solo.waitForText(spinnerNewText);
		solo.clickInList(0);
		solo.goBack();
		solo.goBack();

		assertEquals("Wrong selection", newSpriteName, ((Spinner) solo.getView(R.id.brick_point_to_spinner))
				.getSelectedItem().toString());
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);

		Sprite sprite2 = new Sprite("cat2");
		Script startScript2 = new StartScript(sprite2);
		PlaceAtBrick placeAt2 = new PlaceAtBrick(sprite2, -400, -300);
		startScript2.addBrick(placeAt2);
		sprite2.addScript(startScript2);
		project.addSprite(sprite2);

		Sprite sprite1 = new Sprite("cat1");
		Script startScript1 = new StartScript(sprite1);
		PlaceAtBrick placeAt1 = new PlaceAtBrick(sprite1, 300, 400);
		startScript1.addBrick(placeAt1);
		PointToBrick pointToBrick = new PointToBrick(sprite1, sprite2);
		startScript1.addBrick(pointToBrick);
		sprite1.addScript(startScript1);
		project.addSprite(sprite1);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite1);
		ProjectManager.getInstance().setCurrentScript(startScript1);
	}
}
