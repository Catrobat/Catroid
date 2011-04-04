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

import java.io.File;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.jayway.android.robotium.solo.Solo;

public class ScriptActivityBaseTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;

    private final String projectNameOne = "Ulumulu";
    private final String projectNameTwo = "Ulumulu2";

	public ScriptActivityBaseTest() {
		super("at.tugraz.ist.catroid.ui", MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		File directory = new File("/sdcard/catroid/" + projectNameOne);
        UtilFile.deleteDirectory(directory);
        directory = new File("/sdcard/catroid/" + projectNameTwo);
        UtilFile.deleteDirectory(directory);
		solo = new Solo(getInstrumentation(), getActivity());
        super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
        File directory = new File("/sdcard/catroid/" + projectNameOne);
        UtilFile.deleteDirectory(directory);
        assertFalse(projectNameOne + " was not deleted!", directory.exists());
        directory = new File("/sdcard/catroid/" + projectNameTwo);
        UtilFile.deleteDirectory(directory);
        assertFalse(projectNameTwo + " was not deleted!", directory.exists());
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();

		super.tearDown();
	}

	public void testMainMenuButton() throws InterruptedException {
		solo.clickOnButton(getActivity().getString(R.string.new_project));
		solo.clickOnEditText(0);
		solo.enterText(0, projectNameOne);
		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.new_project_dialog_button));

        solo.clickOnText(getActivity().getString(R.string.stage));

		solo.clickOnButton(getActivity().getString(R.string.main_menu));
		solo.clickOnButton(getActivity().getString(R.string.resume)); //if this is possible it worked! (will throw AssertionFailedError if not working
	}

    public void testCreateNewBrickButton() throws InterruptedException {
		solo.clickOnButton(getActivity().getString(R.string.new_project));
		solo.clickOnEditText(0);
		solo.enterText(0, projectNameTwo);
		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.new_project_dialog_button));

        solo.clickOnText(getActivity().getString(R.string.stage));

        System.out.println("new brick: "+getActivity().getString(R.string.add_new_brick));
        Thread.sleep(1000);
		solo.clickOnText(getActivity().getString(R.string.add_new_brick).substring(2));
		solo.clickInList(2);
		//solo.clickOnText(getActivity().getString(R.string.hide_main_adapter));

        Thread.sleep(100);
		assertTrue("in hidebrick is not in List", solo.searchText(getActivity().getString(R.string.hide_main_adapter)));
        assertEquals("not one brick in listview", 1, solo.getCurrentListViews().get(0).getCount());
    }
}