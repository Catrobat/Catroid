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
package at.tugraz.ist.catroid.uitest.ui.dialog;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class AddBrickDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;

	public AddBrickDialogTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		createTestProject(UiTestUtils.PROJECTNAME1);
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();

		UiTestUtils.clearAllUtilTestProjects();

		super.tearDown();
	}

	public void testAddBrickDialog() {
		solo.clickOnButton(getActivity().getString(R.string.current_project_button));

		solo.clickInList(2);

		// At least one from each category
		int[] brickIds = new int[] { R.string.brick_glide, R.string.brick_change_brightness, R.string.brick_play_sound,
				R.string.brick_broadcast_wait, R.string.brick_forever, R.string.brick_motor_action };

		ProjectManager manager = ProjectManager.getInstance();
		for (int id : brickIds) {
			Script script = manager.getCurrentScript();
			int numberOfBricksBeforeAdding = id == R.string.brick_when ? 0 : script.getBrickList().size();
			UiTestUtils.addNewBrickAndScrollDown(solo, id);
			if (id == R.string.brick_forever) {
				assertEquals("Brick " + solo.getCurrentActivity().getString(id) + " didn't created a LoopEndBrick.",
						numberOfBricksBeforeAdding + 2, script.getBrickList().size());
			} else {
				assertEquals("Brick " + solo.getCurrentActivity().getString(id) + " was not added in the BrickList.",
						numberOfBricksBeforeAdding + 1, script.getBrickList().size());
			}
		}

		int[] triggerBrickIds = new int[] { R.string.brick_when_started, R.string.brick_when,
				R.string.brick_broadcast_receive };

		for (int id : triggerBrickIds) {
			int oldNumberOfScripts = manager.getCurrentSprite().getNumberOfScripts();
			UiTestUtils.addNewBrickAndScrollDown(solo, id);
			Script script = manager.getCurrentScript();
			assertEquals("Adding new trigger brick changed currentScript to change", brickIds.length + 1, script
					.getBrickList().size());
			assertEquals("Adding new trigger brick did not create an additional script", oldNumberOfScripts + 1,
					manager.getCurrentSprite().getNumberOfScripts());
		}
	}

	private void createTestProject(String projectName) {

		StorageHandler storageHandler = StorageHandler.getInstance();

		Project project = new Project(getActivity(), projectName);
		Sprite firstSprite = new Sprite("cat");

		Script testScript = new StartScript(firstSprite);

		firstSprite.addScript(testScript);
		project.addSprite(firstSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);

		storageHandler.saveProject(project);
	}
}
