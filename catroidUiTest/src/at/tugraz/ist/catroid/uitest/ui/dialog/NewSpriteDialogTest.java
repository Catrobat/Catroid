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

import android.content.pm.PackageManager.NameNotFoundException;
import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class NewSpriteDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private String testingproject = UiTestUtils.PROJECTNAME1;
	private String testingsprite = "testingsprite";

	public NewSpriteDialogTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.clearAllUtilTestProjects();

		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception {

		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		getActivity().finish();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testNewSpriteDialog() throws NameNotFoundException, IOException {

		createTestProject(testingproject);
		solo.clickOnButton(getActivity().getString(R.string.projects_on_phone));
		solo.clickOnText(testingproject);

		//solo.clickOnButton(solo.getCurrentActivity().getString(R.string.add_sprite));
		solo.clickOnImageButton(1);
		int spriteEditTextId = solo.getCurrentEditTexts().size() - 1;
		UiTestUtils.enterText(solo, spriteEditTextId, "testingsprite");
		solo.sendKey(Solo.ENTER);
		solo.sleep(1000);
		solo.clickOnText(testingsprite);
		solo.sleep(1000);

		solo.assertCurrentActivity("Current Activity is not ScriptActivity", ScriptActivity.class);

	}

	public void createTestProject(String projectName) {
		StorageHandler storageHandler = StorageHandler.getInstance();

		Project project = new Project(getActivity(), projectName);
		Sprite firstSprite = new Sprite("cat");

		project.addSprite(firstSprite);

		storageHandler.saveProject(project);
	}

}
