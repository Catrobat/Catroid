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

import java.io.File;
import java.io.IOException;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.Utils;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.jayway.android.robotium.solo.Solo;

public class AddBrickDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private String testProject = "testProject";

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

		File directory = new File(Consts.DEFAULT_ROOT + "/" + testProject);
		UtilFile.deleteDirectory(directory);

		super.tearDown();
	}

	public void testAddBrickDialog() {

		solo.clickOnButton(getActivity().getString(R.string.load_project));

		solo.clickOnText(testProject);
		solo.clickInList(2);

		Utils.addNewBrickAndScrollDown(solo, R.string.brick_wait);

		Utils.addNewBrickAndScrollDown(solo, R.string.brick_hide);

		Utils.addNewBrickAndScrollDown(solo, R.string.brick_show);

		Utils.addNewBrickAndScrollDown(solo, R.string.brick_place_at);

		Utils.addNewBrickAndScrollDown(solo, R.string.brick_set_x);

		Utils.addNewBrickAndScrollDown(solo, R.string.brick_set_y);

		Utils.addNewBrickAndScrollDown(solo, R.string.brick_change_x_by);

		Utils.addNewBrickAndScrollDown(solo, R.string.brick_change_y_by);

		Utils.addNewBrickAndScrollDown(solo, R.string.brick_set_costume);

		Utils.addNewBrickAndScrollDown(solo, R.string.brick_scale_costume);

		Utils.addNewBrickAndScrollDown(solo, R.string.brick_go_back);

		Utils.addNewBrickAndScrollDown(solo, R.string.brick_come_to_front);

		Utils.addNewBrickAndScrollDown(solo, R.string.brick_play_sound);

		Utils.addNewBrickAndScrollDown(solo, R.string.brick_if_touched);

		//TODO: This test takes ages but there is not a single assert. Guess what's to do. --> U mad Bro? when addnewbrick doesn't work this test fails hard
	}

	private void createTestProject(String projectName) throws IOException {

		StorageHandler storageHandler = StorageHandler.getInstance();

		Project project = new Project(getActivity(), projectName);
		Sprite firstSprite = new Sprite("cat");

		Script testScript = new Script("ScriptTest", firstSprite);

		firstSprite.getScriptList().add(testScript);
		project.addSprite(firstSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);

		storageHandler.saveProject(project);
	}

}
