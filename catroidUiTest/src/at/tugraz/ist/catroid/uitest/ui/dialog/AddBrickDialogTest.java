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

import android.content.pm.PackageManager.NameNotFoundException;
import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.jayway.android.robotium.solo.Solo;

public class AddBrickDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private String testProject = "testProject";

	public AddBrickDialogTest() {
		super("at.tugraz.ist.catroid.ui", MainMenuActivity.class);
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

		File directory = new File("/sdcard/catroid/" + testProject);
		UtilFile.deleteDirectory(directory);

		super.tearDown();
	}

	public void testAddBrickDialog() throws NameNotFoundException, IOException {

		solo.clickOnButton(getActivity().getString(R.string.load_project));

		solo.clickOnText(testProject);
		solo.clickInList(2);

		solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick).substring(2));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.wait_main_adapter));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.wait_main_adapter));

		solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick).substring(2));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.hide_main_adapter));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.hide_main_adapter));

		solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick).substring(2));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.show_main_adapter));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.show_main_adapter));

		solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick).substring(2));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.goto_main_adapter));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.goto_main_adapter));

		solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick).substring(2));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.set_x_main_adapter));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.set_x_main_adapter));

		solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick).substring(2));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.set_y_main_adapter));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.set_y_main_adapter));

		solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick).substring(2));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.change_x_main_adapter));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.change_x_main_adapter));

		solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick).substring(2));
		// solo.scrollDownList(0);
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.change_y_main_adapter));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.change_y_main_adapter));

		solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick).substring(2));
		//solo.scrollDownList(1);
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.costume_main_adapter));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.costume_main_adapter));

		solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick).substring(2));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.scale_costume));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.scale_costume));

		solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick).substring(2));
		solo.scrollDownList(1);
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.go_back_main_adapter));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.go_back_main_adapter));

		solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick).substring(2));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.come_to_front_main_adapter));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.come_to_front_main_adapter));

		solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick).substring(2));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.play_sound_main_adapter));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.play_sound_main_adapter));

		solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick).substring(2));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.touched_main_adapter));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.touched_main_adapter));
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
