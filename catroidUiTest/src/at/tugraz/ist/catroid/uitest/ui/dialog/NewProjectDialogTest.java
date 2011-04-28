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
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.jayway.android.robotium.solo.Solo;

public class NewProjectDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private String testingproject = "testingproject";

	public NewProjectDialogTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		File directory = new File("/sdcard/catroid/" + testingproject);
		UtilFile.deleteDirectory(directory);
		assertFalse("testProject was not deleted!", directory.exists());

		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception {

		File directory = new File("/sdcard/catroid/" + testingproject);
		UtilFile.deleteDirectory(directory);
		assertFalse("testProject was not deleted!", directory.exists());
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}

	public void testNewProjectDialog() throws NameNotFoundException, IOException {

		solo.clickOnButton(getActivity().getString(R.string.new_project));

		int nameEditTextId = solo.getCurrentEditTexts().size() - 1;

		UiTestUtils.enterText(solo, nameEditTextId, "testingproject");

		solo.sendKey(Solo.ENTER);

		solo.sleep(1000);

		TextView newProject = (TextView) solo.getCurrentActivity().findViewById(R.id.projectTitleTextView);

		assertEquals("New Project is not testingproject!", "Project: " + testingproject, newProject.getText());

	}

}
