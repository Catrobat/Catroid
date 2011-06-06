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
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.Utils;

import com.jayway.android.robotium.solo.Solo;

public class NewProjectDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private String testingproject = Utils.PROJECTNAME1;

	public NewProjectDialogTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Utils.clearAllUtilTestProjects();
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
		Utils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testNewProjectDialog() throws NameNotFoundException, IOException {

		solo.clickOnButton(getActivity().getString(R.string.new_project));

		int nameEditTextId = solo.getCurrentEditTexts().size() - 1;

		Utils.enterText(solo, nameEditTextId, testingproject);

		solo.sendKey(Solo.ENTER);

		solo.sleep(1000);
		assertTrue("New Project is not testingproject!",
				ProjectManager.getInstance().getCurrentProject().getName().equals(Utils.PROJECTNAME1));
	}

}
