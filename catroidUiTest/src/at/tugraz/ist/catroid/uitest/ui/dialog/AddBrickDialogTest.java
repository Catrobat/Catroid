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

package at.tugraz.ist.catroid.uitest.ui.dialog;

import java.io.IOException;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.Utils;

import com.jayway.android.robotium.solo.Solo;

public class AddBrickDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private String testProject = Utils.PROJECTNAME1;

	public AddBrickDialogTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		createTestProject(testProject);
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();

		Utils.clearAllUtilTestProjects();

		super.tearDown();
	}

	private void checkIfBrickIsPresent(int brickStringId) {

		String brickText = solo.getCurrentActivity().getString(R.string.add_new_brick);
		assertTrue("Inserted brick " + brickText + " was not found.", solo.searchText(brickText));

	}

	private void addAndCheckBrick(Solo solo, int brickStringId) {
		Utils.addNewBrickAndScrollDown(solo, brickStringId);
		checkIfBrickIsPresent(brickStringId);
	}

	public void testAddBrickDialog() {
		solo.clickOnButton(getActivity().getString(R.string.current_project_button));

		//solo.clickOnText(testProject);
		solo.clickInList(2);

		int[] brickIds = new int[] {
				R.string.brick_wait,
				R.string.brick_hide,
				R.string.brick_show,
				R.string.brick_place_at,
				R.string.brick_set_x,
				R.string.brick_set_y,
				R.string.brick_change_x_by,
				R.string.brick_change_y_by,
				R.string.brick_set_costume,
				R.string.brick_scale_costume,
				R.string.brick_go_back,
				R.string.brick_come_to_front,
				R.string.brick_play_sound,
				R.string.brick_glide
		};

		ProjectManager manager = ProjectManager.getInstance();
		for (int id : brickIds) {
			Script script = manager.getCurrentScript();
			int numberOfBricksBeforeAdding = id == R.string.brick_if_touched ? 0 : script.getBrickList().size();
			addAndCheckBrick(solo, id);
			assertEquals("Brick " + solo.getCurrentActivity().getString(id) + " was not added in the BrickList.",
					numberOfBricksBeforeAdding + 1,
					script.getBrickList().size());
		}

		int[] triggerBrickIds = new int[] {
				R.string.brick_if_started,
				R.string.brick_if_touched
		};

		for (int id : triggerBrickIds) {
			int oldNumberOfScripts = manager.getCurrentSprite().getScriptList().size();
			addAndCheckBrick(solo, id);
			Script script = manager.getCurrentScript();
			assertEquals("Adding new trigger brick did not create new empty script", 0, script.getBrickList().size());
			assertEquals("Adding new trigger brick did not create an additional script", oldNumberOfScripts + 1,
					manager.getCurrentSprite().getScriptList().size());
		}
	}

	private void createTestProject(String projectName) throws IOException {

		StorageHandler storageHandler = StorageHandler.getInstance();

		Project project = new Project(getActivity(), projectName);
		Sprite firstSprite = new Sprite("cat");

		Script testScript = new StartScript("ScriptTest", firstSprite);

		firstSprite.getScriptList().add(testScript);
		project.addSprite(firstSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);

		storageHandler.saveProject(project);
	}

}
