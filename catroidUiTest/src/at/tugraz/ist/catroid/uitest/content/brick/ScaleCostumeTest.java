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
package at.tugraz.ist.catroid.uitest.content.brick;

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.ScaleCostumeBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;

import com.jayway.android.robotium.solo.Solo;

/**
 * 
 * @author Daniel Burtscher
 *
 */
public class ScaleCostumeTest extends ActivityInstrumentationTestCase2<ScriptActivity>{
	private Solo solo;
	private Project project;
	private ScaleCostumeBrick scaleCostumeBrick;

	public ScaleCostumeTest() {
		super("at.tugraz.ist.catroid",
				ScriptActivity.class);
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
	public void testScaleCostumeBrick() throws Throwable {
		int childrenCount = getActivity().getAdapter().getChildCountFromLastGroup();
		int groupCount = getActivity().getAdapter().getGroupCount();
		assertEquals("Incorrect number of bricks.", 2, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScriptList().get(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), getActivity().getAdapter().getChild(groupCount-1, 0));
		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.scale_costume)));

		double newScale = 25;

		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, newScale + "");
		solo.clickOnButton(0);

		Thread.sleep(1000);
		
		assertEquals("Wrong text in field", newScale, scaleCostumeBrick.getScale());
		assertEquals("Text not updated", newScale, Double.parseDouble(solo.getEditText(0).getText().toString()));
	}

	private void createProject() {
		project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new Script("script", sprite);
		scaleCostumeBrick = new ScaleCostumeBrick(sprite, 20);
		script.addBrick(scaleCostumeBrick);

		sprite.getScriptList().add(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

}