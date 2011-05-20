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

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;

import com.jayway.android.robotium.solo.Solo;

public class ScriptDeleteTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private ArrayList<Brick> brickListToCheck;

	public ScriptDeleteTest() {
		super("at.tugraz.ist.catroid", ScriptActivity.class);

	}

	@Override
	public void setUp() throws Exception {
		createTestProject("testProject");
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

	public void testDeleteScript() {
		solo.clickOnButton(getActivity().getString(R.string.add_new_brick));
		solo.clickOnText(getActivity().getString(R.string.brick_if_touched));

		solo.clickLongOnText(getActivity().getString(R.string.brick_if_touched));
		solo.clickOnText(getActivity().getString(R.string.delete_script_button));
		solo.sleep(1000);

		int numberOfScripts = ProjectManager.getInstance().getCurrentSprite().getScriptList().size();
		assertEquals("Incorrect number of scripts in scriptList", 1, numberOfScripts);
		assertEquals("Incorrect number of elements in listView", 4, solo.getCurrentListViews().get(0).getChildCount());

		solo.clickLongOnText(getActivity().getString(R.string.brick_if_started));
		solo.clickOnText(getActivity().getString(R.string.delete_script_button));
		solo.sleep(1000);

		numberOfScripts = ProjectManager.getInstance().getCurrentSprite().getScriptList().size();
		assertEquals("Incorrect number of scripts in list", 0, numberOfScripts);
		assertEquals("Incorrect number of elements in listView", 0, solo.getCurrentListViews().get(0).getChildCount());

		solo.clickOnButton(getActivity().getString(R.string.add_new_brick));
		solo.clickOnText(getActivity().getString(R.string.brick_hide));
		solo.sleep(1000);

		numberOfScripts = ProjectManager.getInstance().getCurrentSprite().getScriptList().size();
		assertEquals("Incorrect number of scripts in scriptList", 1, numberOfScripts);
		assertEquals("Incorrect number of elements in listView", 2, solo.getCurrentListViews().get(0).getChildCount());
	}

	private void createTestProject(String projectName) {
		double scaleValue = 0.8;

		Project project = new Project(null, projectName);
		Sprite firstSprite = new Sprite("cat");

		Script testScript = new StartScript("testscript", firstSprite);

		brickListToCheck = new ArrayList<Brick>();
		brickListToCheck.add(new HideBrick(firstSprite));
		brickListToCheck.add(new ShowBrick(firstSprite));
		brickListToCheck.add(new ScaleCostumeBrick(firstSprite, scaleValue));

		for (Brick brick : brickListToCheck) {
			testScript.addBrick(brick);
		}

		firstSprite.getScriptList().add(testScript);

		project.addSprite(firstSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);
	}

}
