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

package at.tugraz.ist.catroid.uitest.construction_site;

import java.util.ArrayList;
import java.util.List;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ListView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.brick.Brick;
import at.tugraz.ist.catroid.content.brick.HideBrick;
import at.tugraz.ist.catroid.content.brick.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.brick.ShowBrick;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.ui.ScriptActivity;

import com.jayway.android.robotium.solo.Solo;

public class ScriptChangeTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private ArrayList<Brick> brickListToCheck;

	public ScriptChangeTest() {
		super(ScriptActivity.class);

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

	public void testChangeScript() throws InterruptedException {
		ListView parent = solo.getCurrentListViews().get(0);
		View testScriptBrick = parent.getChildAt(0);
		
		solo.clickOnView(testScriptBrick);
		Thread.sleep(3000);
		
		List<Script> scriptList = ProjectManager.getInstance().getCurrentSprite().getScriptList();
		assertEquals("First script in list is not testScript2", "testScript2", scriptList.get(0).getName());
		assertEquals("Second script in list is not touchScript", "touchScript", scriptList.get(1).getName());
		assertEquals("Third script in list is not testScript", "testScript", scriptList.get(2).getName());

		View touchBrick = parent.getChildAt(1);
		solo.clickOnView(touchBrick);
		Thread.sleep(1500);
		scriptList = ProjectManager.getInstance().getCurrentSprite().getScriptList();
		assertEquals("First script in list is not testScript2", "testScript2", scriptList.get(0).getName());
		assertEquals("Second script in list is not testScript", "testScript", scriptList.get(1).getName());
		assertEquals("Third script in list is not touchScript", "touchScript", scriptList.get(2).getName());

		touchBrick = parent.getChildAt(2);
		String textViewText = solo.getCurrentTextViews(touchBrick).get(0).getText().toString();
		String touchBrickText = getActivity().getString(R.string.touched_main_adapter);
		assertEquals("Third script in listView is not touchScript", touchBrickText, textViewText);
	}

	private void createTestProject(String projectName) {
		double scaleValue = 0.8;

		Project project = new Project(null, projectName);
		Sprite firstSprite = new Sprite("cat");

		Script testScript = new Script("testScript", firstSprite);
		Script touchScript = new Script("touchScript", firstSprite);
		touchScript.setTouchScript(true);
		Script testScript2 = new Script("testScript2", firstSprite);

		brickListToCheck = new ArrayList<Brick>();
		brickListToCheck.add(new HideBrick(firstSprite));
		brickListToCheck.add(new ShowBrick(firstSprite));
		brickListToCheck.add(new ScaleCostumeBrick(firstSprite, scaleValue));

		// adding Bricks: ----------------
		for (Brick brick : brickListToCheck) {
			testScript.addBrick(brick);
		}
		// -------------------------------

		firstSprite.getScriptList().add(testScript);
		firstSprite.getScriptList().add(touchScript);
		firstSprite.getScriptList().add(testScript2);

		project.addSprite(firstSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);
	}

}
