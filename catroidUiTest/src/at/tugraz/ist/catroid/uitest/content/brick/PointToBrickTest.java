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
package at.tugraz.ist.catroid.uitest.content.brick;

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.PointToBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;

import com.jayway.android.robotium.solo.Solo;

public class PointToBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private Project project;

	public PointToBrickTest() {
		super("at.tugraz.ist.catroid", ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
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

	@Smoke
	public void testPointToBrickTest() throws InterruptedException {
		int childrenCount = getActivity().getAdapter().getChildCountFromLastGroup();

		assertEquals("Incorrect number of bricks.", 3, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 2, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.brick_point_to)));
		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.point_to_spinner));
		solo.clickInList(0);
		solo.sleep(300);
		assertEquals("Wrong selection", "Nothing...", solo.getCurrentSpinners().get(0).getSelectedItem());
	}

	private void createProject() {
		project = new Project(null, "testProject");

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
