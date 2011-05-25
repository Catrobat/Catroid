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
import android.widget.ListView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.Utils;

import com.jayway.android.robotium.solo.Solo;

public class RenameSpriteDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private String testProject = Utils.PROJECTNAME1;
	private String cat = "cat";
	private String kat = "kat";

	public RenameSpriteDialogTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {

		Utils.clearAllUtilTestProjects();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {

		Utils.clearAllUtilTestProjects();
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();

		super.tearDown();
	}

	public void testRenameSpriteDialog() throws NameNotFoundException, IOException {

		createTestProject(testProject);
		solo.clickOnButton(getActivity().getString(R.string.projects_on_phone));
		solo.clickOnText(testProject);
		solo.clickLongOnText(cat);

		solo.sleep(1000);
		solo.clickOnText("Rename");
		solo.sleep(1000);
		solo.clearEditText(0);
		Utils.enterText(solo, 0, kat);
		solo.sendKey(Solo.ENTER);

		ListView spritesList = (ListView) solo.getCurrentActivity().findViewById(android.R.id.list);
		String first = spritesList.getItemAtPosition(1).toString();

		assertEquals("The first sprite is NOT rename!", first, kat);

	}

	public void createTestProject(String projectName) throws IOException, NameNotFoundException {
		StorageHandler storageHandler = StorageHandler.getInstance();

		Project project = new Project(getActivity(), projectName);
		Sprite firstSprite = new Sprite("cat");
		Sprite secondSprite = new Sprite("dog");
		Sprite thirdSprite = new Sprite("horse");
		Sprite fourthSprite = new Sprite("pig");

		project.addSprite(firstSprite);
		project.addSprite(secondSprite);
		project.addSprite(thirdSprite);
		project.addSprite(fourthSprite);

		storageHandler.saveProject(project);
	}
}