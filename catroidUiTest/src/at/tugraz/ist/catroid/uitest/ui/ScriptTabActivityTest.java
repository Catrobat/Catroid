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

package at.tugraz.ist.catroid.uitest.ui;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ui.CostumeActivity;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.SoundActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class ScriptTabActivityTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;

	public ScriptTabActivityTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createTestProject();
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
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testScriptTab() {
		solo.clickOnText("Costumes");
		solo.clickOnText("Script");
		solo.sleep(100);
		assertTrue("Clicking on Script Tab did not cause ScriptActivity to be displayed",
				solo.getCurrentActivity() instanceof ScriptActivity);
	}

	public void testCostumesTab() {
		solo.clickOnText("Costumes");
		solo.sleep(100);
		assertTrue("Clicking on Costumes Tab did not cause CostumeActivity to be displayed",
				solo.getCurrentActivity() instanceof CostumeActivity);
	}

	public void testSoundsTab() {
		solo.clickOnText("Sounds");
		solo.sleep(100);
		assertTrue("Clicking on Sounds Tab did not cause SoundActivity to be displayed",
				solo.getCurrentActivity() instanceof SoundActivity);
	}

}
